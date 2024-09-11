package minefantasy.mfr.recipe.types;

import net.minecraft.util.IStringSerializable;

public enum CarpenterRecipeType implements IStringSerializable, IRecipeMFRType {
	CARPENTER_SHAPED_RECIPE,
	CARPENTER_SHAPELESS_RECIPE,
	CARPENTER_SHAPED_CUSTOM_MATERIAL_RECIPE,
	CARPENTER_SHAPELESS_CUSTOM_MATERIAL_RECIPE,
	CARPENTER_DYNAMIC_RECIPE,
	NONE;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static CarpenterRecipeType deserialize(String name) {
		for (CarpenterRecipeType type : values()) {

			if (type.getName().equals(name)) {
				return type;
			}
		}
		return NONE;
	}

	public CarpenterRecipeType getByNameWithModId(String name, String modId) {
		for (CarpenterRecipeType type : values()) {

			if ((modId + ":" + type.getName()).equals(name)) {
				return type;
			}
		}
		return NONE;
	}
}
