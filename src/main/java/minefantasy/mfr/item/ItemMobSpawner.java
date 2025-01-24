package minefantasy.mfr.item;

import minefantasy.mfr.MineFantasyReforged;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMobSpawner extends ItemBaseMFR {
	private String entity;

	public ItemMobSpawner(String entity) {
		super("spawn_" + entity);
		this.entity = entity;
		this.setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack item, @Nullable World world, List list, ITooltipFlag flagIn) {
		list.add(I18n.format("item.spawn_" + entity + ".desc"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack item) {
		return I18n.format("item.spawn_" + entity + ".name");
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer user, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack item = user.getHeldItem(hand);
		if (world.isRemote) {
			return EnumActionResult.FAIL;
		} else {

			BlockPos blockpos = pos.offset(facing);
			double d0 = this.getYOffset(world, blockpos);
			Entity entity = spawnCreature(world, this.entity, (double) blockpos.getX() + 0.5D, (double) blockpos.getY() + d0, (double) blockpos.getZ() + 0.5D);

			if (entity != null) {
				if (entity instanceof EntityLivingBase && item.hasDisplayName()) {
					entity.setCustomNameTag(item.getDisplayName());
				}

				if (!user.capabilities.isCreativeMode) {
					item.shrink(1);
				}
			}

			return EnumActionResult.PASS;
		}
	}

	@Nullable
	public static Entity spawnCreature(World worldIn, String entityID, double x, double y, double z) {
		if (entityID != null) {
			EntityLivingBase entity = null;

			ResourceLocation entityResourceLocation = new ResourceLocation(MineFantasyReforged.MOD_ID, entityID);

			for (int i = 0; i < 1; ++i) {
				entity = (EntityLivingBase) EntityList.createEntityByIDFromName(entityResourceLocation, worldIn);

				entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
				entity.rotationYawHead = entity.rotationYaw;
				entity.renderYawOffset = entity.rotationYaw;

				if (entity instanceof EntityLiving) {
					EntityLiving entityliving = (EntityLiving) entity;

					if (net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn(entityliving, worldIn, (float) x, (float) y, (float) z, null))
						return null;
					entityliving.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityliving)), null);

					entityliving.playLivingSound();
				}

				worldIn.spawnEntity(entity);

				if (entity instanceof EntityLiving) {
					EntityLiving entityliving = (EntityLiving) entity;
					entityliving.playLivingSound();
				}
			}

			return entity;
		} else {
			return null;
		}
	}

	protected double getYOffset(World world, BlockPos pos) {
		AxisAlignedBB axisalignedbb = (new AxisAlignedBB(pos)).grow(0.0D, -1.0D, 0.0D);
		List<AxisAlignedBB> list = world.getCollisionBoxes(null, axisalignedbb);

		if (list.isEmpty()) {
			return 0.0D;
		} else {
			double d0 = axisalignedbb.minY;

			for (AxisAlignedBB axisalignedbb1 : list) {
				d0 = Math.max(axisalignedbb1.maxY, d0);
			}

			return d0 - (double) pos.getY();
		}
	}
}
