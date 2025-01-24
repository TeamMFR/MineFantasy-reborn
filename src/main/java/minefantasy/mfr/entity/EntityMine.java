package minefantasy.mfr.entity;

import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.item.EnumCasingType;
import minefantasy.mfr.item.EnumFillingType;
import minefantasy.mfr.item.EnumFuseType;
import minefantasy.mfr.item.EnumPowderType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class EntityMine extends Entity {
	private static final DataParameter<String> FILLING = EntityDataManager.createKey(EntityMine.class, DataSerializers.STRING);
	private static final DataParameter<String> CASING = EntityDataManager.createKey(EntityMine.class, DataSerializers.STRING);
	private static final DataParameter<String> FUSE = EntityDataManager.createKey(EntityMine.class, DataSerializers.STRING);
	private static final DataParameter<String> POWDER = EntityDataManager.createKey(EntityMine.class, DataSerializers.STRING);
	private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityMine.class, DataSerializers.ITEM_STACK);

	private static DamageSource mineDmg = new DamageSource("mine").setExplosion();
	private static DamageSource mineFireDmg = new DamageSource("mine").setExplosion().setFireDamage();
	/**
	 * How long the fuse is
	 */
	public int fuse;

	public EntityMine(World world) {
		super(world);
		this.preventEntitySpawning = true;
		this.setSize(0.5F, 0.25F);
	}

	public EntityMine(World world, EntityLivingBase thrower) {
		this(world);
		this.fuse = 40;

		this.setLocationAndAngles(thrower.posX, thrower.posY + thrower.getEyeHeight(), thrower.posZ,
				thrower.rotationYaw, thrower.rotationPitch);
		this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		this.posY -= 0.10000000149011612D;
		this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		this.setPosition(this.posX, this.posY, this.posZ);

		float f = 0.4F;
		this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI)
				* MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
		this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI)
				* MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
		this.motionY = -MathHelper.sin((this.rotationPitch + this.getThrownOffset()) / 180.0F * (float) Math.PI) * f;
		this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, this.getThrownForce(), 1.0F);

		this.prevPosX = posX;
		this.prevPosY = posY;
		this.prevPosZ = posZ;
	}

	@Override
	public boolean processInitialInteract(EntityPlayer user, EnumHand hand) {
		if (ticksExisted > 20 && user.getActiveHand() == hand) {
			if (user.isSneaking()) {
				// DISARM
				setDead();
				user.swingArm(hand);
				if (!world.isRemote) {
					user.setActiveHand(hand);
					ItemStack item = MineFantasyItems.MINE_CUSTOM.createMine(getCasing(), getFilling(), getFuse(), getPowder(), 1);
					if (!user.inventory.addItemStackToInventory(item)) {
						this.entityDropItem(item, 0.0F);
					}
				}
			}
		}
		return super.processInitialInteract(user, hand);
	}

	public void setThrowableHeading(double x, double y, double z, float offset, float force) {
		float f2 = MathHelper.sqrt(x * x + y * y + z * z);
		x /= f2;
		y /= f2;
		z /= f2;
		x += this.rand.nextGaussian() * 0.007499999832361937D * force;
		y += this.rand.nextGaussian() * 0.007499999832361937D * force;
		z += this.rand.nextGaussian() * 0.007499999832361937D * force;
		x *= offset;
		y *= offset;
		z *= offset;
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
		float f3 = MathHelper.sqrt(x * x + z * z);
		this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(x, z) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(y, f3) * 180.0D / Math.PI);
	}

	protected float getThrownOffset() {
		return 0.0F;
	}

	protected float getThrownForce() {
		return 0.5F;
	}

	@Override
	protected void entityInit() {
		dataManager.register(ITEM, ItemStack.EMPTY);
		dataManager.register(FILLING, "");
		dataManager.register(CASING, "");
		dataManager.register(FUSE, "");
		dataManager.register(POWDER, "");
	}

	public ItemStack getItem() {
		return dataManager.get(ITEM);
	}

	/**
	 * Sets the item that this entity represents. Used for rendering
	 */
	public void setItem(ItemStack stack) {
		this.getDataManager().set(ITEM, stack);
		this.getDataManager().setDirty(ITEM);
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk
	 * on. used for spiders and wolves to prevent them from trampling crops
	 */
	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this
	 * Entity.
	 */
	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= (0.03999999910593033D * getCase().weightModifier);
		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if (this.onGround) {
			this.motionX = 0;
			this.motionZ = 0;
			this.motionY *= -0.99D;
		}

		if (fuse > 0) {
			--fuse;
		}
		double radius = Math.max(1.0D, getRangeOfBlast() - 1.5D);

		if (ticksExisted % 5 == 0) {
			List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(radius * 2, radius, radius * 2));
			if (fuse == 0 && !list.isEmpty()) {
				boolean detonate = false;
				for (EntityLivingBase e : list) {
					if (e.getDistance(this) < radius) {
						detonate = true;
						break;
					}
				}

				if (detonate) {
					world.playSound(posX, posY, posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 0.75F, true);
					--fuse;
				}
			}
		}
		if (fuse < 0) {
			fuse--;

			if (fuse < -10) {
				this.setDead();

				if (!this.world.isRemote) {
					this.explode();
				}
			}
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setByte("Fuse", (byte) this.fuse);
		nbt.setString("casing", getCasing());
		nbt.setString("filling", getFilling());
		nbt.setString("fuse", getFuse());
		nbt.setString("powder", getPowder());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.fuse = nbt.getByte("Fuse");
		this.dataManager.set(FILLING, nbt.getString("filling"));
		this.dataManager.set(CASING, nbt.getString("casing"));
		this.dataManager.set(FUSE, nbt.getString("fuse"));
		this.dataManager.set(POWDER, nbt.getString("powder"));
	}

	public void explode() {
		world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 0.3F, 10F - 5F, true);
		world.createExplosion(this, posX, posY, posZ, 0, false);
		if (!this.world.isRemote) {
			double area = getRangeOfBlast() * 2D * getPowderType().rangeModifier;
			AxisAlignedBB AABB = this.getEntityBoundingBox().grow(area, area / 2, area);
			List<EntityLivingBase> entitiesWithinAABB = this.world.getEntitiesWithinAABB(EntityLivingBase.class, AABB);

			if (entitiesWithinAABB != null && !entitiesWithinAABB.isEmpty()) {

				for (Entity entity : entitiesWithinAABB) {

					double distanceToEntity = this.getDistance(entity);

					double radius = getRangeOfBlast() * getPowderType().rangeModifier;
					if (distanceToEntity < radius) {
						float dam = getDamage();

						if (!getCasing().equals("obsidian") && distanceToEntity > radius / 2) {
							double sc = distanceToEntity - (radius / 2);
							if (sc < 0)
								sc = 0;
							if (sc > (radius / 2))
								sc = (radius / 2);
							dam *= (sc / (radius / 2));
						}
						if (!(entity instanceof EntityItem)) {
							DamageSource source = mineDmg;
							if (getFilling().equals("fire")) {
								source = mineFireDmg;
							}
							if (entity.attackEntityFrom(source, dam)) {
								applyEffects(entity);
							}
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
				EntityShrapnel shrapnel = new EntityShrapnel(world, posX, posY + 0.5D, posZ, (rand.nextDouble() - 0.5) * (range / 2), (rand.nextDouble()) * range, (rand.nextDouble() - 0.5) * (range / 2));

				if (filling.equals("fire")) {
					shrapnel.setFire(10);
				}
				world.spawnEntity(shrapnel);
			}
		}
	}

	private void applyEffects(Entity hit) {
		if (getFilling().equals("fire")) {
			hit.setFire(8);
		}
	}

	private double getRangeOfBlast() {
		return getBlast().range * getCase().rangeModifier;
	}

	private float getDamage() {
		return getBlast().damage * getCase().damageModifier * getPowderType().damageModifier;
	}

	public String getFilling() {
		return dataManager.get(FILLING);
	}

	public String getCasing() {
		return dataManager.get(CASING);
	}

	public String getFuse() {
		return dataManager.get(FUSE);
	}

	public String getPowder() {
		return dataManager.get(POWDER);
	}

	public EntityMine setType(String filling, String casing, String fuse, String powder) {
		dataManager.set(FILLING, filling);
		dataManager.set(CASING, casing);
		dataManager.set(FUSE, fuse);
		dataManager.set(POWDER, powder);

		this.fuse = getFuseType().time;

		return this;
	}

	private EnumFillingType getBlast() {
		return EnumFillingType.getType(getFilling());
	}

	private EnumCasingType getCase() {
		return EnumCasingType.getType(getCasing());
	}

	private EnumFuseType getFuseType() {
		return EnumFuseType.getType(getFuse());
	}

	private EnumPowderType getPowderType() {
		return EnumPowderType.getType(getPowder());
	}
}