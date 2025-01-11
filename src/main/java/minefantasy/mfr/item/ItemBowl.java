package minefantasy.mfr.item;

import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.integration.CustomSand;
import minefantasy.mfr.util.MFRLogUtil;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public class ItemBowl extends ItemComponentMFR {
	private Random rand = new Random();

	public ItemBowl(String name) {
		super(name, Rarity.COMMON);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand){
		ItemStack item = player.getHeldItem(hand);
		RayTraceResult rayTraceResult = this.rayTrace(world, player, true);

		if (rayTraceResult == null) {
			return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
		} else {
			if (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {

				if (!world.canMineBlockBody(player, rayTraceResult.getBlockPos())) {
					return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
				}

				if (!player.canPlayerEdit(rayTraceResult.getBlockPos(), rayTraceResult.sideHit, item)) {
					return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
				}

				if (isSaltWaterSource(world, rayTraceResult.getBlockPos())) {
					gather(item, hand, world, player);
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}

	private void gather(ItemStack item, EnumHand hand, World world, EntityPlayer player) {
		player.swingArm(hand);
		if (!world.isRemote) {
			world.playSound(player, player.getPosition(), SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.AMBIENT, 0.125F + rand.nextFloat() / 4F, 0.5F + rand.nextFloat());
			item.shrink(1);
			EntityItem resultItem = new EntityItem(world, player.posX, player.posY, player.posZ,
					new ItemStack(MineFantasyItems.BOWL_WATER_SALT));
			world.spawnEntity(resultItem);
		}
	}

	private boolean isSaltWaterSource(World world, BlockPos pos) {
		if (world.getBlockState(pos).getMaterial() != Material.WATER) {
			return false;
		}

		Biome biome = world.getBiome(pos);
		if (biome == Biome.getBiome(0) || biome == Biome.getBiome(24) || biome == Biome.getBiome(16)) {
			return true;
		}
		MFRLogUtil.logDebug("Biome = " + biome);
		return CustomSand.isSand(world.getBlockState(pos.add(0, -1, 0)).getBlock());
	}
}
