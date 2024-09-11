package minefantasy.mfr.recipe.types;

import net.minecraft.util.IStringSerializable;

public enum TannerRecipeType implements IStringSerializable, IRecipeMFRType {
	TANNER_RECIPE,
	NONE;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static TannerRecipeType deserialize(String name) {
		for (TannerRecipeType type : values()) {

			if (type.getName().equals(name)) {
				return type;
			}
		}
		return NONE;
	}

	public TannerRecipeType getByNameWithModId(String name, String modId) {
		for (TannerRecipeType type : values()) {

			if ((modId + ":" + type.getName()).equals(name)) {
				return type;
			}
		}
		return NONE;
	}
}
