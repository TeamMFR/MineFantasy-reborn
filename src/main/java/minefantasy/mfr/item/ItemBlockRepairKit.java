package minefantasy.mfr.item;

import minefantasy.mfr.block.BlockRepairKit;
import minefantasy.mfr.constants.Rarity;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;

import java.util.List;

public class ItemBlockRepairKit extends ItemBlockBase {
	private BlockRepairKit kit;

	public ItemBlockRepairKit(Block base) {
		super(base);
		this.setMaxStackSize(8);
		this.kit = (BlockRepairKit) base;
	}

	@Override
	public void addInformation(ItemStack item, World world, List list, ITooltipFlag flag) {
		if (kit.isOrnate) {
			list.add(I18n.format("attribute.kit.repairRate_normal.name") + ": "
					+ kit.repairLevel * 100F + "%");
			list.add(I18n.format("attribute.kit.repairRate_enchant.name") + ": "
					+ kit.repairLevelEnchant * 100F + "%");
		} else {
			list.add(I18n.format("attribute.kit.repairRate.name") + ": " + kit.repairLevel * 100F
					+ "%");
		}
		list.add(I18n.format("attribute.kit.repairChance.name") + ": " + kit.successRate * 100F
				+ "%");
		list.add(
				I18n.format("attribute.kit.breakChance.name") + ": " + kit.breakChance * 100F + "%");
		super.addInformation(item, world, list, flag);
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		if (kit.isOrnate) {
			return Rarity.RARE;
		}
		if (kit.repairLevel >= 1.0F) {
			return Rarity.UNCOMMON;
		}
		return super.getRarity(item);
	}
}
