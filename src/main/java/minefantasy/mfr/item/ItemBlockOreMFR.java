package minefantasy.mfr.item;

import minefantasy.mfr.block.BlockOreMF;
import minefantasy.mfr.constants.Rarity;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;

public class ItemBlockOreMFR extends ItemBlockBase {
	private BlockOreMF ore;

	public ItemBlockOreMFR(Block block) {
		super(block);
		ore = (BlockOreMF) block;

	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		return Rarity.getForgeRarity(item, ore.rarity);
	}
}
