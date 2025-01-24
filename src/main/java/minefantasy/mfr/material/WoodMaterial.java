package minefantasy.mfr.material;

import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.registry.types.CustomMaterialType;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.crafting.Ingredient;

public class WoodMaterial extends CustomMaterial {

	public WoodMaterial (String name,Ingredient materialIngredient,  int[] colourRGB, float hardness,
			float durability, float flexibility, float sharpness, float resistance, float density, int tier, Rarity rarity,
			int enchantability, int crafterTier, Float craftTimeModifier, boolean unbreakable) {

		super(name, CustomMaterialType.WOOD_MATERIAL, materialIngredient, colourRGB, hardness, durability, flexibility, sharpness, resistance, density,
				tier, rarity, enchantability, crafterTier, null, craftTimeModifier,
				null, null, unbreakable);
	}

	@Override
	public String getMaterialString() {
		return I18n.format("materialtype." + this.getType().getName() + ".name", this.getTier());
	}
}
