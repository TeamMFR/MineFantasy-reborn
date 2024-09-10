package minefantasy.mfr.item;

import minefantasy.mfr.api.crafting.MineFantasyCarbons;
import minefantasy.mfr.init.MineFantasyItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.oredict.OreDictionary;

public class AdvancedFuelHandler implements IFuelHandler {

	public static void registerItems() {
		MineFantasyCarbons.addCarbon(new ItemStack(Items.COAL, 1, OreDictionary.WILDCARD_VALUE), 1);
		MineFantasyCarbons.addCarbon(MineFantasyItems.COKE, 1);
		MineFantasyCarbons.addCarbon(MineFantasyItems.COAL_FLUX, 1);
		MineFantasyCarbons.addCarbon(Blocks.COAL_BLOCK, 9);
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		if (fuel.isEmpty()) {
			return 0;
		}
		if (fuel.getItem() == MineFantasyItems.COAL_DUST) {
			return 300;// 15s
		}
		if (fuel.getItem() == MineFantasyItems.COKE) {
			return 2400;
		}
		if (fuel.getItem() == Items.COAL) {
			return 1200;
		}
		if (fuel.getItem() == Item.getItemFromBlock(Blocks.COAL_BLOCK)) {
			return 10800;
		}
		return 0;
	}

}
