package minefantasy.mfr.recipe.types;

import net.minecraft.util.IStringSerializable;

public enum AnvilRecipeType implements IStringSerializable, IRecipeMFRType {
	ANVIL_SHAPED_RECIPE,
	ANVIL_SHAPELESS_RECIPE,
	ANVIL_SHAPED_CUSTOM_MATERIAL_RECIPE,
	ANVIL_SHAPELESS_CUSTOM_MATERIAL_RECIPE,
	ANVIL_DYNAMIC_RECIPE,
	NONE;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static AnvilRecipeType deserialize(String name) {
		for (AnvilRecipeType type : values()) {

			if (type.getName().equals(name)) {
				return type;
			}
		}
		return NONE;
	}

	public AnvilRecipeType getByNameWithModId(String name, String modId) {
		for (AnvilRecipeType type : values()) {

			if ((modId + ":" + type.getName()).equals(name)) {
				return type;
			}
		}
		return NONE;
	}
}
