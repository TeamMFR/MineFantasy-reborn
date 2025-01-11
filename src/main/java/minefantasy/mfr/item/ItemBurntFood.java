package minefantasy.mfr.item;

import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.Random;

public class ItemBurntFood extends ItemComponentMFR {

	private Random rand = new Random();

	public ItemBurntFood(String name) {
		super(name, Rarity.COMMON);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		if (this == MineFantasyItems.BURNT_FOOD) {
			if (!world.isRemote && rand.nextInt(5) == 0) {
				player.entityDropItem(new ItemStack(Items.COAL, 1, 1), 0.0F);
			}
			item.shrink(1);
			return ActionResult.newResult(EnumActionResult.PASS, item);
		}
		if (!world.isRemote) {
			player.entityDropItem(new ItemStack(MineFantasyItems.BURNT_FOOD, item.getCount()), 0.0F);
		}
		return ActionResult.newResult(EnumActionResult.PASS, item.getItem().getContainerItem(item));
	}

	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		return new ItemStack(getContainerItem(), itemStack.getCount());
	}
}
