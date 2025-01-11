package minefantasy.mfr.item;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.armour.ArmourDesign;
import minefantasy.mfr.config.ConfigClient;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.material.BaseMaterial;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemClothing extends ItemArmourMFR {
	@SideOnly(Side.CLIENT)
	private Object model;

	/**
	 * @param piece 0head, 1body, 2legs, 3boots
	 */
	public ItemClothing(String name, BaseMaterial material, EntityEquipmentSlot piece, String tex, Rarity rarity) {
		super(name, material, ArmourDesign.CLOTH, piece, tex, rarity);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		String tex = MineFantasyReforged.MOD_ID + ":textures/models/armour/" + design.getName().toLowerCase() + "/" + texture;
		if (type == null && canColour())// bottom layer
		{
			return tex + "_cloth.png";
		}
		return tex + ".png";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public net.minecraft.client.model.ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped model) {

		if (!ConfigClient.customModel) {
			return super.getArmorModel(entityLiving, itemStack, armorSlot, model);
		}
		if (model == null) {
			model = new net.minecraft.client.model.ModelBiped(0.25F);
		}
		return (net.minecraft.client.model.ModelBiped) model;
	}

}
