package minefantasy.mfr.recipe.types;

import net.minecraft.util.IStringSerializable;

public enum RoastRecipeType implements IStringSerializable, IRecipeMFRType {
	COOKING_RECIPE,
	NONE;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static RoastRecipeType deserialize(String name) {
		for (RoastRecipeType type : values()) {

			if (type.getName().equals(name)) {
				return type;
			}
		}
		return NONE;
	}

	public RoastRecipeType getByNameWithModId(String name, String modId) {
		for (RoastRecipeType type : values()) {

			if ((modId + ":" + type.getName()).equals(name)) {
				return type;
			}
		}
		return NONE;
	}
}
