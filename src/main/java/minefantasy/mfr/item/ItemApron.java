package minefantasy.mfr.item;

import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.material.BaseMaterial;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class ItemApron extends ItemClothing {
	public ItemApron(String name, BaseMaterial material, String tex, Rarity rarity) {
		super(name, material, EntityEquipmentSlot.CHEST, tex, rarity);
	}

	public static boolean isUserProtected(EntityPlayer user) {
		ItemStack worn = user.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (worn.isEmpty()) {
			return false;
		}
		return worn.getItem() instanceof ItemApron;
	}
}
