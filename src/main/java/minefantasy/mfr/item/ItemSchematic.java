package minefantasy.mfr.item;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.mechanics.knowledge.IArtefact;
import minefantasy.mfr.proxy.IClientRegister;
import minefantasy.mfr.util.ModelLoaderHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSchematic extends ItemBlock implements IArtefact, IClientRegister {

	public final String[] researches;
	Block schematic;

	public ItemSchematic(Block schematic, String... researches) {
		super(schematic);
		this.researches = researches;
		this.schematic = schematic;

		setRegistryName(block.getRegistryName());
		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	@Override
	public IRarity getForgeRarity(ItemStack stack) {
		return Rarity.RARE;
	}

	@Override
	public String[] getResearches() {
		return researches;
	}

	@Override
	public int getStudyTime(ItemStack item) {
		return 50;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this);
//		ModelLoaderHelper.registerItem(Item.getItemFromBlock(schematic));
	}
}
