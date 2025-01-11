package minefantasy.mfr.item;

import minefantasy.mfr.block.BlockCakeMFR;
import minefantasy.mfr.constants.Rarity;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;

public class ItemBlockCake extends ItemBlockBase {
	private BlockCakeMFR cake;

	public ItemBlockCake(Block block) {
		super(block);
		cake = (BlockCakeMFR) block;
		setMaxStackSize(1);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		int lvl = cake.getRarity().getRarityValue();
		if (lvl >= Rarity.values().length) {
			lvl = Rarity.values().length - 1;
		}
		return Rarity.getRarityByValue(lvl);
	}
}
