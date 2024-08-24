package minefantasy.mfr.api.crafting;

import minefantasy.mfr.api.heating.ForgeFuel;
import minefantasy.mfr.api.heating.ForgeItemHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class MineFantasyFuels {
	public static void addForgeFuel(Object input, float time, int temperature) {
		addForgeFuel(input, time, temperature, false, false);
	}

	public static void addForgeFuel(Object input, float time, int temperature, boolean willLight) {
		addForgeFuel(input, time, temperature, willLight, false);
	}

	public static void addForgeFuel(Object input, float time, int temperature, boolean willLight, boolean refined) {
		ItemStack item = convert(input);
		if (!item.isEmpty()) {
			//ForgeItemHandler.forgeFuel.add(new ForgeFuel(item, time, temperature, willLight, refined));
			if ((int) (temperature * 1.25) > ForgeItemHandler.forgeMaxTemp) {
				ForgeItemHandler.forgeMaxTemp = (int) (temperature * 1.25);
			}
		}
	}

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
