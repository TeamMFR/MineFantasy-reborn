package minefantasy.mfr.util;

import minefantasy.mfr.api.armour.IPowerArmour;
import minefantasy.mfr.api.heating.ForgeFuel;
import minefantasy.mfr.block.BlockFrame;
import minefantasy.mfr.block.BlockFrameHolder;
import minefantasy.mfr.registry.ForgeFuelRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PowerArmour {
	public static boolean isWearingCogwork(EntityLivingBase user) {
		return user.getRidingEntity() != null && user.getRidingEntity() instanceof IPowerArmour;
	}

	public static boolean isPowered(EntityLivingBase user) {
		if (user.getRidingEntity() != null && user.getRidingEntity() instanceof IPowerArmour) {
			return ((IPowerArmour) user.getRidingEntity()).isPowered();
		}
		return false;
	}

	public static boolean isFullyArmoured(EntityLivingBase user) {
		if (user.getRidingEntity() != null && user.getRidingEntity() instanceof IPowerArmour) {
			return ((IPowerArmour) user.getRidingEntity()).isFullyArmoured();
		}
		return false;
	}

	public static float modifyDamage(EntityLivingBase user, float dam, DamageSource src) {
		if (user.getRidingEntity() != null) {
			if (user.getRidingEntity() instanceof IPowerArmour) {
				return ((IPowerArmour) user.getRidingEntity()).modifyDamage(user, dam, src);
			}
		}
		return dam;
	}

	public static boolean allowDamageToBlock(DamageSource src) {
		if (src == DamageSource.STARVE || src == DamageSource.OUT_OF_WORLD || src == DamageSource.DROWN) {
			return false;
		}
		return true;
	}

	public static float getFuelValue(ItemStack item) {
		ForgeFuel stats = ForgeFuelRegistry.getFuelStats(item);
		if (stats != null && stats.isRefined) {
			return stats.burnTime;
		}
		return 0;
	}

	public static boolean isStationBlock(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return isBasicStationFrame(world, pos) && block instanceof BlockFrameHolder;
	}

	public static boolean isBasicStationFrame(World world, BlockPos pos) {
		if (!(world.getBlockState(pos).getBlock() instanceof BlockFrame || world.getBlockState(pos).getBlock() instanceof BlockFrameHolder)) {
			return false;
		}
		int shafts = 0;

		if (isShaft(world, pos.add(1, 0, 0))) {
			++shafts;
		}
		if (isShaft(world, pos.add(-1, 0, 0))) {
			++shafts;
		}
		if (isShaft(world, pos.add(0, 0, 1))) {
			++shafts;
		}
		if (isShaft(world, pos.add(0, 0, -1))) {
			++shafts;
		}

		return shafts == 3;
	}

	public static boolean isShaft(World world, BlockPos pos) {
		for (int y2 = 0; y2 < 4; y2++) {
			if (!(world.getBlockState(pos).getBlock() instanceof BlockFrame)) {
				return false;
			}
		}
		return true;
	}
}
