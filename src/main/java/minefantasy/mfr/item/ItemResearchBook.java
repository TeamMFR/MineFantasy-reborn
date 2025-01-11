package minefantasy.mfr.item;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.mechanics.knowledge.ResearchLogic;
import minefantasy.mfr.network.NetworkHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemResearchBook extends ItemBaseMFR {
	public ItemResearchBook() {
		super("research_book");
		setMaxStackSize(1);
		setCreativeTab(MineFantasyTabs.tabGadget);
		setTranslationKey("infobook");

		setContainerItem(this);
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		return Rarity.UNCOMMON;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed.
	 * Args: itemStack, world, entityPlayer
	 *
	 * @return
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			ResearchLogic.syncData(player);
		}
		player.openGui(MineFantasyReforged.MOD_ID, NetworkHandler.GUI_RESEARCH_BOOK, world, 0, -1, 0);
		return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack item, World world, List list, ITooltipFlag fullInfo) {
		super.addInformation(item, world, list, fullInfo);
	}
}
