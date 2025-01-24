package minefantasy.mfr.entity;

import minefantasy.mfr.config.ConfigMobs;
import minefantasy.mfr.entity.mob.EntityDragon;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityFireBlast extends EntityFireball {
	private static final float size = 0.75F;
	public static DamageSource blastDamage = (new DamageSource("blastfurn")).setFireDamage();
	public static DamageSource basicDamage = (new DamageSource("fireblastBase")).setFireDamage();

	public EntityFireBlast(World world) {
		super(world);
		this.setInvisible(true);
		this.setSize(size, size);
	}

	public EntityFireBlast(World world, EntityLivingBase shooter, double xv, double yv, double zv) {
		super(world, shooter, xv, yv, zv);
		this.setInvisible(true);
		this.setSize(size, size);
	}

	public EntityFireBlast(World world, double x, double y, double z, double xv, double yv, double zv) {
		super(world, x, y, z, xv, yv, zv);
		this.setInvisible(true);
		this.setSize(size, size);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.ticksExisted >= getLifeSpan()) {
			setDead();
		}
		if (ticksExisted % 5 == 0) {
			int lifeScale = (int) Math.floor(ticksExisted / 5F);
			float newSize = size + (size / 4 * lifeScale);
			this.setSize(newSize, newSize);
		}
		if (ticksExisted % 10 == 0) {
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						int i = (int) posX + x;
						int j = (int) posY + y;
						int k = (int) posZ + z;
						BlockPos pos = new BlockPos(i, j, k);
						if (!world.isRemote && canBlockCatchFire(pos.add(0, -1, 0)) && world.isAirBlock(pos) && rand.nextFloat() < getPyro()) {
							this.world.setBlockState(pos, Blocks.FIRE.getDefaultState());
						}
					}
				}
			}
		}
		AxisAlignedBB bb = this.getEntityBoundingBox().grow(1D);
		if (!world.isRemote)
			this.destroyBlocksInAABB(bb);
	}

	private int getLifeSpan() {
		if (isPreset("BlastFurnace")) {
			return 15;
		}
		return 30;
	}

	/**
	 * Called when this EntityFireball hits a block or entity.
	 */
	@Override
	protected void onImpact(RayTraceResult result) {
		if (!this.world.isRemote) {
			if (result.entityHit != null) {
				if (!result.entityHit.isImmuneToFire()) {
					result.entityHit.setFire(isPreset("Dragon") ? 2 : 4);
					result.entityHit.attackEntityFrom(causeFireblastDamage(), getDamage());
				}
			} else {
				int i = result.getBlockPos().getX();
				int j = result.getBlockPos().getY();
				int k = result.getBlockPos().getZ();

				switch (result.sideHit) {
					case DOWN:
						--j;
						break;
					case UP:
						++j;
						break;
					case NORTH:
						--k;
						break;
					case SOUTH:
						++k;
						break;
					case EAST:
						--i;
						break;
					case WEST:
						++i;
				}

				BlockPos pos = new BlockPos(i, j, k);

				if (!world.isRemote && this.world.isAirBlock(pos) && rand.nextFloat() < getPyro()) {
					this.world.setBlockState(pos, Blocks.FIRE.getDefaultState());
				}
				boolean tnt = world.getBlockState(result.getBlockPos()).getMaterial() == Material.TNT;
				if (isPreset("BlastFurnace") && (rand.nextInt(50) == 0) || tnt) {
					boolean solid = world.isBlockNormalCube(result.getBlockPos(), false);
					world.newExplosion(this, posX, posY, posZ, solid ? 1.5F : 0.5F, true, true);
				}
			}

			this.setDead();
		}
	}

	private float getPyro() {

		if (!ConfigMobs.dragonGriefFire && shootingEntity != null && shootingEntity instanceof EntityDragon) {
			return 0F;
		}

		if (getEntityData().hasKey("Pyro")) {
			return getEntityData().getFloat("Pyro");
		}

		return 0.75F;
	}

	public EntityFireBlast setPyro(float dam) {
		getEntityData().setFloat("Pyro", dam);
		return this;
	}

	private float getDamage() {
		if (getEntityData().hasKey("Damage")) {
			return getEntityData().getFloat("Damage");
		}

		if (isPreset("BlastFurnace")) {
			return 8F;
		}
		return 2.0F;
	}

	public EntityFireBlast setDamage(float dam) {
		getEntityData().setFloat("Damage", dam);
		return this;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this
	 * Entity.
	 */
	public boolean canBeCollidedWith() {
		return false;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource src, float power) {
		return false;
	}

	@Override
	public boolean isInvisible() {
		return true;
	}

	public void modifySpeed(float mod) {
		this.accelerationX *= mod;
		this.accelerationY *= mod;
		this.accelerationZ *= mod;
	}

	public boolean isPreset(String s) {
		if (getEntityData().hasKey("Preset")) {
			return getEntityData().getString("Preset").equalsIgnoreCase(s);
		}
		return false;
	}

	public boolean canBlockCatchFire(BlockPos pos) {
		return canCatchFire(pos, EnumFacing.UP);
	}

	public boolean canCatchFire(BlockPos pos, EnumFacing facing) {
		return world.getBlockState(pos).getBlock().isFlammable(world, pos, facing);
	}

	public EntityFireBlast preset(String string) {
		getEntityData().setBoolean(string, true);
		return this;
	}

	private boolean destroyBlocksInAABB(AxisAlignedBB box) {
		int xStart = MathHelper.floor(box.minX);
		int yStart = MathHelper.floor(box.minY);
		int zStart = MathHelper.floor(box.minZ);
		int xBound = MathHelper.floor(box.maxX);
		int yBound = MathHelper.floor(box.maxY);
		int zBound = MathHelper.floor(box.maxZ);
		boolean var8 = false;
		boolean glassVariable = false;

		for (int x = xStart; x <= xBound; ++x) {
			for (int y = yStart; y <= yBound; ++y) {
				for (int z = zStart; z <= zBound; ++z) {
					BlockPos pos = new BlockPos(x, y, z);
					Material material = this.world.getBlockState(pos).getMaterial();

					if (material != null) {
						if (material == Material.GLASS) {
							glassVariable = true;
							this.world.setBlockToAir(pos);
						} else if (material == Material.TNT) {
							glassVariable = true;
							this.world.setBlockToAir(pos);
							this.world.createExplosion(this, x, y, z, 4.0F, true);
						} else {
							var8 = true;
						}
					}
				}
			}
		}

		if (glassVariable) {
			double x = box.minX + (box.maxX - box.minX) * this.rand.nextFloat();
			double y = box.minY + (box.maxY - box.minY) * this.rand.nextFloat();
			double z = box.minZ + (box.maxZ - box.minZ) * this.rand.nextFloat();
			this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y, z, 0.0D, 0.0D, 0.0D);
			for (int a = 0; a < 1 + rand.nextInt(4); a++)
				this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1F, 1F);
		}

		return var8;
	}

	public DamageSource causeFireblastDamage() {
		if (isPreset("BlastFurnace")) {
			return blastDamage;
		}
		return shootingEntity == null ? basicDamage
				: (new EntityDamageSourceIndirect("fireblast", this, shootingEntity)).setFireDamage().setProjectile();
	}
}