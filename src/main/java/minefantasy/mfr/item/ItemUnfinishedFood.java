package minefantasy.mfr.item;

import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;

/**
 * @author Anonymous Productions
 */
public class ItemUnfinishedFood extends ItemBaseMFR {
	private final Rarity itemRarity;

	public ItemUnfinishedFood(String name) {
		this(name, Rarity.COMMON);
	}

	public ItemUnfinishedFood(String name, Rarity rarity) {
		super(name);
		setMaxStackSize(1);
		itemRarity = rarity;

		this.setCreativeTab(MineFantasyTabs.tabFood);
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		return Rarity.getForgeRarity(item, itemRarity);
	}

}
