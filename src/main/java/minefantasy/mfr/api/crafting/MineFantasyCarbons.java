package minefantasy.mfr.api.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class MineFantasyCarbons {

	/**
	 * Adds a carbon item for smelting
	 *
	 * @param input Item Block or ItemStack
	 * @param uses
	 */
	public static void addCarbon(Object input, int uses) {
		ItemStack itemstack = convert(input);

		if (!itemstack.isEmpty()) {
			OreDictionary.registerOre("Carbon-" + uses, itemstack);
		}
	}

	/**
	 * How many smelts (blast furn or bloomery) this can give as carbon
	 */
	public static int getCarbon(ItemStack item) {
		if (item.isEmpty())
			return 0;

		for (int i : OreDictionary.getOreIDs(item)) {
			String name = OreDictionary.getOreName(i);
			if (name != null && name.startsWith("Carbon-")) {
				String s = name.substring(7);
				int uses = Integer.parseInt(s);
				return uses;
			}
		}

		return 0;
	}

	/**
	 * Determines if the item can be used for carbon in smelting.
	 */
	public static boolean isCarbon(ItemStack item) {
		return getCarbon(item) > 0;
	}

	public static ItemStack convert(Object input) {
		if (input == null)
			return ItemStack.EMPTY;

		if (input instanceof ItemStack) {
			return (ItemStack) input;
		} else if (input instanceof Block) {
			return new ItemStack((Block) input);
		} else if (input instanceof Item) {
			return new ItemStack((Item) input);
		}

		return ItemStack.EMPTY;
	}
}
