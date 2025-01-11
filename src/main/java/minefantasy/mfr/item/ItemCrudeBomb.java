package minefantasy.mfr.item;

import com.google.common.collect.Lists;
import minefantasy.mfr.api.crafting.ISpecialSalvage;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.IRarity;

import java.util.List;

public class ItemCrudeBomb extends ItemBomb implements ISpecialSalvage {

	public ItemCrudeBomb(String name) {
		super(name);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack item) {
		return 20;
	}

	@Override
	public String getTranslationKey(ItemStack item) {
		return "item.bomb_crude";
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab)) {
			return;
		}
		items.add(new ItemStack(this));
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		return Rarity.POOR;
	}

	@Override
	public List<ItemStack> getSalvage(ItemStack item) {
		return Lists.newArrayList(
				new ItemStack(Items.PAPER),
				new ItemStack(MineFantasyItems.THREAD),
				new ItemStack(MineFantasyItems.BLACKPOWDER));
	}

	@Override
	public String getItemFuse(String value) {
		return "basic";
	}

	@Override
	public String getItemFilling(String value) {
		return "basic";
	}

	@Override
	public String getItemCasing(String value) {
		return "crude";
	}

	@Override
	public String getItemPowder(String value) {
		return "black_powder";
	}
}