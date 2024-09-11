package minefantasy.mfr.recipe.types;

import net.minecraft.util.IStringSerializable;

public enum SpecialRecipeType implements IStringSerializable, IRecipeMFRType {
	SPECIAL_RECIPE_DRAGONFORGED,
	SPECIAL_RECIPE_ORNATE,
	NONE;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static SpecialRecipeType deserialize(String name) {
		for (SpecialRecipeType type : values()) {

			if (type.getName().equals(name)) {
				return type;
			}
		}
		return NONE;
	}

	public SpecialRecipeType getByNameWithModId(String name, String modId) {
		for (SpecialRecipeType type : values()) {

			if ((modId + ":" + type.getName()).equals(name)) {
				return type;
			}
		}
		return NONE;
	}
}
