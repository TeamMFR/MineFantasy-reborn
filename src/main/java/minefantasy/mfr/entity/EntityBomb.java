package minefantasy.mfr.entity;

import minefantasy.mfr.item.EnumCasingType;
import minefantasy.mfr.item.EnumFillingType;
import minefantasy.mfr.item.EnumFuseType;
import minefantasy.mfr.item.EnumPowderType;
import minefantasy.mfr.mechanics.CombatMechanics;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class EntityBomb extends Entity {
	private static final DataParameter<String> FILLING = EntityDataManager.createKey(EntityBomb.class, DataSerializers.STRING);
	private static final DataParameter<String> CASING = EntityDataManager.createKey(EntityBomb.class, DataSerializers.STRING);
	private static final DataParameter<String> FUSE = EntityDataManager.createKey(EntityBomb.class, DataSerializers.STRING);
	private static final DataParameter<String> POWDER = EntityDataManager.createKey(EntityBomb.class, DataSerializers.STRING);
	private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityBomb.class, DataSerializers.ITEM_STACK);
	/**
	 * How long the fuse is
	 */
	public int fuse;
	private EntityLivingBase thrower;

	public EntityBomb(World world) {
		super(world);
		this.fuse = getFuseTime();
		this.preventEntitySpawning = true;
		this.setSize(0.5F, 0.5F);
		this.setItem(ItemStack.EMPTY);
	}

	public EntityBomb(World world, EntityLivingBase thrower, ItemStack item) {
		this(world);
		this.thrower = thrower;
		this.setItem(item);

		this.setLocationAndAngles(thrower.posX, thrower.posY + thrower.getEyeHeight(), thrower.posZ,
				thrower.rotationYaw, thrower.rotationPitch);
		this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		this.posY -= 0.10000000149011612D;
		this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		this.setPosition(this.posX, this.posY, this.posZ);

		float force = this.getThrownForce() + (CombatMechanics.getStrengthEnhancement(thrower) / 4);

		float f = 0.4F;
		this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI)
				* MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
		this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI)
				* MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
		this.motionY = -MathHelper.sin((this.rotationPitch + this.getThrownOffset()) / 180.0F * (float) Math.PI) * f;
		this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, force, 1.0F);

		this.prevPosX = posX;
		this.prevPosY = posY;
		this.prevPosZ = posZ;
	}

	public static DamageSource causeBombDamage(Entity bomb, Entity user) {
		return (new EntityDamageSourceBomb(bomb, user)).setProjectile();
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

	private int getFuseTime() {
		return (this.fuse = getFuseType().time) + (isSticky() ? 20 : 0);
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
		return 1.5F;
	}

	@Override
	protected void entityInit() {
		dataManager.register(ITEM, ItemStack.EMPTY);
		dataManager.register(FILLING, "");
		dataManager.register(CASING, "");
		dataManager.register(FUSE, "");
		dataManager.register(POWDER, "");
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
		return true;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= (0.03999999910593033D * getGravityModifier());
		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if (this.onGround) {
			double d = 0.75D;
			this.motionX *= d;
			this.motionZ *= d;
			this.motionY *= -0.99D;
		}
		List<Entity> collide = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox());
		if (!collide.isEmpty() && !(thrower != null && collide.contains(thrower))) {
			if (isSticky()) {
				Entity object = collide.get(0);
				if (object != null && object != this) {
					if (this.getRidingEntity() == null && !object.isBeingRidden() && canStick(object)) {
						this.startRiding(object);
						this.fuse = getFuseTime();
					}
				}
			}
			this.motionX = motionZ = 0;
		}

		if (this.fuse-- <= 0) {
			this.setDead();

			if (!this.world.isRemote) {
				this.explode();
			}
		} else {
			this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.125D, this.posZ, 0.0D, 0.0D, 0.0D);
			this.world.spawnParticle(EnumParticleTypes.FLAME, this.posX, this.posY + 0.125D, this.posZ, 0.0D, 0.0D, 0.0D);
		}

		if (!world.isRemote && this.getRidingEntity() != null && getRidingEntity() instanceof EntityLivingBase) {
			if (!(getRidingEntity() instanceof EntityChicken)) {
				CombatMechanics.panic((EntityLivingBase) getRidingEntity(), 1.0F, 30);
			}
		}
		if (isStuckInBlock()) {
			motionX = motionY = motionZ = 0;
		}
	}

	private boolean canStick(Entity entity) {
		return !(entity instanceof EntityCogwork);
	}

	@Override
	public boolean startRiding(Entity object) {
		super.startRiding(object);

		if (object instanceof EntityChicken) {
			EntityChicken chook = (EntityChicken) object;
			Entity target = world.findNearestEntityWithinAABB(Entity.class, chook.getEntityBoundingBox().grow(64, 8, 64), chook);
			if (target != null) {
				if (target instanceof EntityLivingBase) {
					chook.setCustomNameTag("Suicide Chook!");
					fuse = getFuseTime() * 2;
					chook.getNavigator().clearPath();
					chook.setAttackTarget((EntityLivingBase) target);
					chook.targetTasks.addTask(1, new EntityAINearestAttackableTarget(chook, IMob.class, true, true));
					chook.setAIMoveSpeed(0.5F);
				}
			}
		}
		return true;
	}

	private boolean isStuckInBlock() {
		return false;// isSticky() && isAABBInSolid(boundingBox.expand(0.5D, 0.5D, 0.5D));
	}

	private boolean isSticky() {
		return getEntityData().hasKey("stickyBomb");
	}

	private double getGravityModifier() {
		return isStuckInBlock() ? 0F : getCase().weightModifier;
	}

	private void explode2() {
		float power = 3.5F;
		this.world.createExplosion(this, this.posX, this.posY, this.posZ, power, false);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setByte("Fuse", (byte) this.fuse);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.fuse = nbt.getByte("Fuse");
	}

	@Override
	public void applyEntityCollision(Entity entity) {
		if (!(thrower != null && thrower == entity)) {
			this.motionX = 0;
			this.motionZ = 0;
		}
	}

	public void explode() {
		world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.AMBIENT, 0.3F, 10F - 5F, true);
		world.createExplosion(this, posX, posY, posZ, 0, false);
		if (!this.world.isRemote) {
			double area = getRangeOfBlast() * 2D;
			AxisAlignedBB axisAlignedBB = this.getEntityBoundingBox().grow(area, area / 2, area);
			List<EntityLivingBase> entitiesWithinAABB = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisAlignedBB);

			if (entitiesWithinAABB != null && !entitiesWithinAABB.isEmpty()) {

				for (Entity entity : entitiesWithinAABB) {

					double distanceToEntity = this.getDistance(entity);
					double radius = getRangeOfBlast();
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

							DamageSource source = causeBombDamage(this, thrower != null ? thrower : this);
							source.setExplosion();
							if (getFilling().equals("fire")) {
								source.setFireDamage();
							}
							if (getRidingEntity() != null && entity == getRidingEntity()) {
								dam *= 1.5F;
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
				EntityShrapnel shrapnel = new EntityShrapnel(world, posX, posY + 0.5D, posZ,
						(rand.nextDouble() - 0.5) * range, (rand.nextDouble() - 0.5F) * range,
						(rand.nextDouble() - 0.5) * range);

				if (filling.equals("fire")) {
					shrapnel.setFire(10);
				}
				world.spawnEntity(shrapnel);
			}
		}
	}

	private void applyEffects(Entity hit) {
		if (getFilling().equals("fire")) {
			hit.setFire(5);
		}
	}

	private double getRangeOfBlast() {
		return getBlast().range * getCase().rangeModifier * getPowderType().rangeModifier;
	}

	private int getDamage() {
		return (int) (getBlast().damage * getCase().damageModifier * getPowderType().damageModifier);
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

	public EntityBomb setType(String filling, String casing, String fuse, String powder) {
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