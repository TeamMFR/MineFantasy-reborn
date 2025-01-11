package minefantasy.mfr.constants;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.common.util.EnumHelper;

public enum Rarity implements net.minecraftforge.common.IRarity {

	POOR(TextFormatting.DARK_GRAY, "Poor", -1), // used to be "-1"
	COMMON(TextFormatting.WHITE, EnumRarity.COMMON.rarityName, 0), // used to be "0"
	UNCOMMON(TextFormatting.YELLOW, EnumRarity.UNCOMMON.rarityName, 1), // used to be "1"
	RARE(TextFormatting.AQUA, EnumRarity.RARE.rarityName, 2), // used to be "2"
	EPIC(TextFormatting.LIGHT_PURPLE, EnumRarity.EPIC.rarityName, 3), // used to be "3"
	UNIQUE(TextFormatting.DARK_GREEN, "Unique", 4);

	static {
		EnumHelper.addRarity("poor", TextFormatting.DARK_GRAY, "poor");
		EnumHelper.addRarity("Rare", TextFormatting.DARK_BLUE, "rare");
		EnumHelper.addRarity("unique", TextFormatting.DARK_GREEN, "unique");
	}

	/**
	 * A decimal representation of the hex color codes of a the color assigned to this rarity type. (13 becomes d as in
	 * \247d which is light purple)
	 */
	private final TextFormatting rarityColor;
	/**
	 * Rarity name.
	 */
	private final String rarityName;

	private final int rarityValue;

	Rarity(TextFormatting color, String name, int rarityValue) {
		this.rarityColor = color;
		this.rarityName = name;
		this.rarityValue = rarityValue;
	}

	@Override
	public TextFormatting getColor() {
		return this.rarityColor;
	}

	@Override
	public String getName() {
		return this.rarityName;
	}

	public int getRarityValue() {
		return this.rarityValue;
	}

	public static Rarity getRarityByValue(int rarityValue) {
		for (Rarity rarity : values()) {
			if (rarity.getRarityValue() == rarityValue) {
				return rarity;
			}
		}
		return POOR;
	}

	public static IRarity getForgeRarity(ItemStack item, Rarity itemRarity) {
		int lvl = itemRarity.getRarityValue();
		if (item.isItemEnchanted()) {
			if (lvl == 0) {
				lvl++;
			}
			lvl++;
		}
		if (lvl >= Rarity.values().length) {
			lvl = Rarity.values().length - 1;
		}
		return Rarity.getRarityByValue(lvl);
	}
}