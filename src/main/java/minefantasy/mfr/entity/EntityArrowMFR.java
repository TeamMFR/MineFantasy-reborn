package minefantasy.mfr.entity;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.archery.IArrowMFR;
import minefantasy.mfr.api.archery.IArrowRetrieve;
import minefantasy.mfr.api.weapon.IDamageType;
import minefantasy.mfr.config.ConfigWeapon;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.init.MineFantasySounds;
import minefantasy.mfr.item.ArrowType;
import minefantasy.mfr.item.EnumFillingType;
import minefantasy.mfr.item.EnumPowderType;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.mechanics.CombatMechanics;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.util.CustomToolHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IPosition;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

public class EntityArrowMFR extends EntityArrow implements IProjectile, IDamageType, IArrowRetrieve {
	private static final DataParameter<String> TEXTURE_DW = EntityDataManager.createKey(EntityArrowMFR.class, DataSerializers.STRING);
	/**
	 * Seems to be some sort of timer for animating an arrow.
	 */
	public int arrowShake;
	public float velocityModifier = 1.0F;
	public ArrowType arrowtype = ArrowType.NORMAL;
	public float firepower = 1F;
	private boolean playedSound = false;
	private float power = 0F;

	public EntityArrowMFR(World worldIn) {
		super(worldIn);
	}

	public EntityArrowMFR(World worldIn, EntityLivingBase shooter) {
		super(worldIn, shooter.posX, shooter.posY + (double) shooter.getEyeHeight() - 0.10000000149011612D, shooter.posZ);
		this.shootingEntity = shooter;

		if (shooter instanceof EntityPlayer) {
			this.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
		}
	}

	public EntityArrowMFR(World world, IPosition position) {
		super(world, position.getX(), position.getY(), position.getZ());
	}

	public static DamageSource causeBombDamage(Entity bomb, Entity user) {
		return (new EntityDamageSourceBomb(bomb, user)).setProjectile();
	}

	/**
	 * Implement this with the constructor
	 */
	public EntityArrowMFR setArrow(ItemStack arrow) {
		ItemStack arrowStack = arrow.copy();
		arrowStack.setCount(1);
		setArrowStack(arrowStack);
		return this;
	}

	public EntityArrowMFR setArrowTex(String tex) {
		updateTex(tex);
		return this;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(TEXTURE_DW, "steel_arrow");
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (isMagicArrow()) {
			for (int i = 0; i < 4; ++i) {
				this.world.spawnParticle(EnumParticleTypes.REDSTONE, this.posX + this.motionX * i / 4.0D,
						this.posY + this.motionY * i / 4.0D, this.posZ + this.motionZ * i / 4.0D, -this.motionX,
						-this.motionY + 0.2D, -this.motionZ);
			}
		}

		if (!this.inGround && !this.hasNoGravity()) {
			this.motionY += 0.05000000074505806D;//Revert gravity adjustment from super
			this.motionY -= 0.05000000074505806D * getGravityModifier();//Apply MFR gravity mod
		}
	}

	@Override
	protected void onHit(RayTraceResult rayTraceResult) {
		if (isExplosive()) {
			explode();
		}
		Entity entity = rayTraceResult.entityHit;

		if (entity != null) {
			float damage = Math.max(0.1F, this.getHitDamage() * firepower);

			//Apply MFR Damage modifications
			damage *= getDamageModifier(rayTraceResult.entityHit);

			if (this.getIsCritical()) {
				damage *= (rand.nextFloat() * 0.5F) + 1.0F;
			}

			DamageSource damagesource;

			if (this.shootingEntity == null) {
				damagesource = DamageSource.causeArrowDamage(this, this);
			} else {
				damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
			}

			if (this.isBurning() && !(entity instanceof EntityEnderman)) {
				entity.setFire(5);
			}

			if (isExplosive()) {
				damage = this.getExplosionDamage();
				damagesource.setExplosion();
				if (this.getFilling().equals("fire")) {
					damagesource.setFireDamage();
				}
			}

			if (entity.attackEntityFrom(damagesource, damage)) {
				if (entity instanceof EntityLivingBase) {
					EntityLivingBase hitEntity = (EntityLivingBase) entity;

					if (!this.world.isRemote) {
						hitEntity.setArrowCountInEntity(hitEntity.getArrowCountInEntity() + 1);
					}

					if (this.knockbackStrength > 0) {
						float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

						if (f1 > 0.0F) {
							hitEntity.addVelocity(this.motionX * (double) this.knockbackStrength * 0.6000000238418579D / (double) f1, 0.1D, this.motionZ * (double) this.knockbackStrength * 0.6000000238418579D / (double) f1);
						}
					}

					if (this.shootingEntity instanceof EntityLivingBase) {
						EnchantmentHelper.applyThornEnchantments(hitEntity, this.shootingEntity);
						EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase) this.shootingEntity, hitEntity);
					}

					//Apply special effects based on the bow type, aka Dragonforged making things catch on fire
					onHitEntity(hitEntity, damage);

					if (this.shootingEntity != null && hitEntity != this.shootingEntity && hitEntity instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
						((EntityPlayerMP) this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
					}
				}

				playHitSound();

				if (!(entity instanceof EntityEnderman)) {
					this.setDead();
				}
			} else {
				this.motionX *= -0.10000000149011612D;
				this.motionY *= -0.10000000149011612D;
				this.motionZ *= -0.10000000149011612D;
				this.rotationYaw += 180.0F;
				this.prevRotationYaw += 180.0F;
				this.ticksInAir = 0;

				if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513D) {
					if (this.pickupStatus == EntityArrow.PickupStatus.ALLOWED) {
						this.entityDropItem(this.getArrowStack(), 0.1F);
					}

					this.setDead();
				}

				if (isMagicArrow()) {
					setDead();
				}
			}
		} else {
			BlockPos blockpos = rayTraceResult.getBlockPos();
			this.xTile = blockpos.getX();
			this.yTile = blockpos.getY();
			this.zTile = blockpos.getZ();
			IBlockState state = this.world.getBlockState(blockpos);
			this.inTile = state.getBlock();
			this.inData = this.inTile.getMetaFromState(state);
			this.motionX = (float) (rayTraceResult.hitVec.x - this.posX);
			this.motionY = (float) (rayTraceResult.hitVec.y - this.posY);
			this.motionZ = (float) (rayTraceResult.hitVec.z - this.posZ);
			float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
			this.posX -= this.motionX / (double) f2 * 0.05000000074505806D;
			this.posY -= this.motionY / (double) f2 * 0.05000000074505806D;
			this.posZ -= this.motionZ / (double) f2 * 0.05000000074505806D;
			playHitSound();
			this.inGround = true;
			this.arrowShake = 7;
			this.setIsCritical(false);

			if (state.getMaterial() != Material.AIR) {
				this.inTile.onEntityCollision(this.world, blockpos, state, this);
			}

			if (isMagicArrow()) {
				setDead();
			}
			//Handle MFR arrow breaking
			if (!world.isRemote && ConfigWeapon.breakArrowsGround && didArrowBreak()) {
				breakArrow();
			}
		}
		if (getEntityData().hasKey("hasBroken_MFArrow")) {
			this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
			setDead();
		}
	}

	@Nullable
	protected Entity findEntityOnPath(Vec3d start, Vec3d end) {
		//Minecraft doesn't let us add to this list normally, so I need to override the whole method just to make it not collide
		// with shrapnel entities from bomb arrows
		Predicate<Entity> arrowTargets = Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, entity -> !(entity instanceof EntityShrapnel), Entity::canBeCollidedWith);
		Entity entity = null;
		List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D), arrowTargets);
		double d0 = 0.0D;

		for (Entity entity1 : list) {
			if (entity1 != this.shootingEntity || this.ticksInAir >= 5) {
				AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
				RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);

				if (raytraceresult != null) {
					double d1 = start.squareDistanceTo(raytraceresult.hitVec);

					if (d1 < d0 || d0 == 0.0D) {
						entity = entity1;
						d0 = d1;
					}
				}
			}
		}

		return entity;
	}

	private float getDamageModifier(Entity target) {
		CustomMaterial material = CustomToolHelper.getCustomPrimaryMaterial(getArrowStack());
		String design = StringUtils.defaultString(getEntityData().getString("Design"), "standard");
		return CombatMechanics.getSpecialModifier(material, design, target, true);
	}

	private boolean isExplosive() {
		return getEntityData().hasKey("explosive");
	}

	private void playHitSound() {
		if (world.isRemote || playedSound)
			return;
		float pitch = 1.0F / (this.rand.nextFloat() * 0.2F + 0.9F);
		if (this.arrowtype != null && this.arrowtype == ArrowType.BROADHEAD) {
			pitch *= 0.75F;
		}
		this.playSound(MineFantasySounds.ARROW_HIT, 0.5F, pitch);
		playedSound = true;
	}

	private void onHitEntity(Entity entityHit, float dam) {
		ItemStack arrow = getArrowStack();
		if (!arrow.isEmpty() && arrow.getItem() instanceof IArrowMFR) {
			((IArrowMFR) arrow.getItem()).onHitEntity(this, this.shootingEntity, entityHit, dam);
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		nbt.setFloat("firepower", firepower);
		nbt.setBoolean("playedSound", playedSound);
		nbt.setFloat("pierceChance", velocityModifier);
		nbt.setString("arrowTexture", this.getCustomTex());
		nbt.setString("arrowType", arrowtype.name);
		nbt.setFloat("power", power);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		firepower = nbt.getFloat("firepower");
		playedSound = nbt.getBoolean("playedSound");
		velocityModifier = nbt.getFloat("pierceChance");

		if (nbt.hasKey("arrowTexture")) {
			updateTex(nbt.getString("arrowTexture"));
		}
		if (nbt.hasKey("arrowType")) {
			ArrowType arrow = ArrowType.arrowMap.get(nbt.getString("arrowType"));
			if (arrow != null) {
				arrowtype = arrow;
			}
		}
		power = nbt.getFloat("power");
	}

	public float getHitDamage() {
		float dam = 2.0F;
		ItemStack arrowStack = getArrowStack();
		if (!arrowStack.isEmpty()) {
			if (arrowStack.getItem() instanceof IArrowMFR) {
				dam = ((IArrowMFR) arrowStack.getItem()).getDamageModifier(arrowStack);
			}
		}
		if (getEntityData().hasKey(Constants.MFR_BOW_DAMAGE_TAG)) {
			dam *= getEntityData().getFloat(Constants.MFR_BOW_DAMAGE_TAG);
		}
		return dam;
	}

	public float getGravityModifier() {
		ItemStack arrowStack = getArrowStack();
		if (!arrowStack.isEmpty()) {
			if (arrowStack.getItem() instanceof IArrowMFR) {
				return ((IArrowMFR) arrowStack.getItem()).getGravityModifier(arrowStack);
			}
		}
		return 1.0F;
	}

	public float getBreakChance() {
		ItemStack arrowStack = getArrowStack();
		if (!arrowStack.isEmpty()) {
			if (arrowStack.getItem() instanceof IArrowMFR) {
				return ((IArrowMFR) arrowStack.getItem()).getBreakChance(this, arrowStack);
			}
		}
		return 1.0F;
	}

	@SideOnly(Side.CLIENT)
	public String getTexture() {
		return getCustomTex();
	}

	@Override
	protected ItemStack getArrowStack() {
		if (getEntityData().hasKey("MF_ArrowItem")) {
			NBTTagCompound heldArrow = getEntityData().getCompoundTag("MF_ArrowItem");
			return new ItemStack(heldArrow);
		}
		return new ItemStack(Items.ARROW);
	}

	/**
	 * Saves the arrow to the NBT
	 *
	 * @param arrow the arrow ItemStack
	 */
	private void setArrowStack(ItemStack arrow) {
		NBTTagCompound arrowNBT = new NBTTagCompound();
		arrow.writeToNBT(arrowNBT);
		getEntityData().setTag("MF_ArrowItem", arrowNBT);
	}

	private void updateTex(String tex) {
		dataManager.set(TEXTURE_DW, tex);
	}

	private String getCustomTex() {
		try {
			return dataManager.get(TEXTURE_DW);
		}
		catch (Exception e) {
			MineFantasyReforged.LOG.warn("Arrow Failed To Load Texture");
			return "steel_arrow";
		}
	}

	@Override
	public PickupStatus canBePickedUp() {
		if (isMagicArrow()) {
			return PickupStatus.DISALLOWED;
		}
		if (didArrowBreak()) {
			breakArrow();
			return PickupStatus.DISALLOWED;
		}
		return pickupStatus;
	}

	private void breakArrow() {
		getEntityData().setBoolean("hasBroken_MFArrow", true);
	}

	private boolean didArrowBreak() {
		if (isExplosive()) {
			return false;
		}
		float c = rand.nextFloat();
		float br = getBreakChance() * ConfigWeapon.arrowBreakMod;
		return c < br;
	}

	@Override
	public ItemStack getDroppedItem() {
		return getArrowStack();
	}

	@Override
	public float[] getDamageRatio(Object... implement) {
		if (isExplosive()) {
			return new float[] {0, 1, 0};
		}
		if (arrowtype != null) {
			return arrowtype.ratio;
		}
		return new float[] {0, 0, 1};
	}

	public void modifyVelocity(float velocity) {
		this.velocityModifier = velocity;
		motionX *= velocity;
		motionY *= velocity;
		motionZ *= velocity;
	}

	@Override
	public float getPenetrationLevel(Object implement) {
		return arrowtype == ArrowType.BROADHEAD ? -0.5F : 0F;
	}

	public void setPower(float f) {
		power = f;
	}

	public EntityArrowMFR setBombStats(String powder, String filling) {
		getEntityData().setString("powder", powder);
		getEntityData().setString("filling", filling);
		getEntityData().setBoolean("explosive", true);
		return this;
	}

	public void explode() {
		world.playSound(null, new BlockPos(posX, posY, posZ), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 0.3F, 10F - 5F);
		world.createExplosion(this, posX, posY, posZ, 0, false);
		if (!this.world.isRemote) {
			double area = getRangeOfBlast() * 2D;
			AxisAlignedBB entityBoundBox = this.getEntityBoundingBox().grow(area, area / 2, area);
			List<Entity> entitiesWithinAABB = this.world.getEntitiesWithinAABB(EntityLivingBase.class, entityBoundBox);

			if (entitiesWithinAABB != null && !entitiesWithinAABB.isEmpty()) {

				for (Entity entityHit : entitiesWithinAABB) {
					double distanceToEntity = this.getDistance(entityHit);

					double radius = getRangeOfBlast();
					if (distanceToEntity < radius) {
						float dam = getExplosionDamage();

						if (distanceToEntity > radius / 2) {
							double sc = distanceToEntity - (radius / 2);
							if (sc < 0)
								sc = 0;
							if (sc > (radius / 2))
								sc = (radius / 2);
							dam *= (sc / (radius / 2));
						}
						if (!(entityHit instanceof EntityItem)) {
							DamageSource source = causeBombDamage(this, this.shootingEntity != null ? this.shootingEntity : this);
							source.setExplosion();
							if (getFilling().equals("fire")) {
								source.setFireDamage();
							}
							entityHit.attackEntityFrom(source, dam);
						}
					}
				}
			}
			this.setDead();
		}

		String filling = getFilling();
		if (!filling.equals("basic")) {
			for (int a = 0; a < 16; a++) {
				float range = 0.6F;
				EntityShrapnel shrapnel = new EntityShrapnel(world, posX, posY + 0.5D, posZ,
						(rand.nextDouble() - 0.5) * range, (rand.nextDouble() - 0.5) * range,
						(rand.nextDouble() - 0.5) * range);

				if (filling.equals("fire")) {
					shrapnel.setFire(10);
				}
				world.spawnEntity(shrapnel);
			}
		}
	}

	private String getFilling() {
		return getEntityData().getString("filling");
	}

	private String getPowder() {
		return getEntityData().getString("powder");
	}

	private EnumFillingType getBlast() {
		return EnumFillingType.getType(getFilling());
	}

	private EnumPowderType getPowderType() {
		return EnumPowderType.getType(getPowder());
	}

	private double getRangeOfBlast() {
		return getBlast().range * getPowderType().rangeModifier * 0.5F;
	}

	private int getExplosionDamage() {
		return (int) (getBlast().damage * getPowderType().damageModifier * 0.5F);
	}

	public boolean isMagicArrow() {
		CustomMaterial material = CustomToolHelper.getCustomPrimaryMaterial(getArrowStack());
		return material != CustomMaterialRegistry.NONE && material.getType().getName().equalsIgnoreCase("magic");
	}
}