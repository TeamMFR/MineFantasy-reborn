package minefantasy.mfr.item;

import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyLoot;
import minefantasy.mfr.init.MineFantasyTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.IRarity;

import java.util.List;
import java.util.Random;

public class ItemLootSack extends ItemBaseMFR {

	protected int amount, tier;
	protected ResourceLocation pool;

	public ItemLootSack(String name, int tier) {
		super(name);
		this.setCreativeTab(MineFantasyTabs.tabGadget);
		this.tier = tier;
		pool = tier == 0 ? MineFantasyLoot.LOOT_SACK_COMMON : tier == 1 ? MineFantasyLoot.LOOT_SACK_VALUABLE : MineFantasyLoot.LOOT_SACK_EXQUISITE;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer user, EnumHand hand) {
		ItemStack item = user.getHeldItem(hand);
		if (user.isSwingInProgress)
			return ActionResult.newResult(EnumActionResult.PASS, item);

		user.swingArm(EnumHand.MAIN_HAND);
		if (!user.capabilities.isCreativeMode) {
			item.shrink(1);
		}
		user.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 1.5F);

		if (!world.isRemote) {
			Random rand = new Random();
			LootContext.Builder lootContextBuilder = new LootContext.Builder((WorldServer) world);
			List<ItemStack> result = world.getLootTableManager().getLootTableFromLocation(this.pool).generateLootForPools(rand, lootContextBuilder.build());
			for (ItemStack stack : result) {
				if (!stack.isEmpty()) {
					if (!user.inventory.addItemStackToInventory(stack)) {
						user.entityDropItem(stack, 0F);
					}
				}
			}
		}
		return ActionResult.newResult(EnumActionResult.PASS, item);
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		return tier == 0 ? Rarity.COMMON : tier == 1 ? Rarity.UNCOMMON : Rarity.EPIC;
	}
}
