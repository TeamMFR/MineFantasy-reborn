package minefantasy.mfr.recipe.types;

import net.minecraft.util.IStringSerializable;

public enum AlloyRecipeType implements IStringSerializable, IRecipeMFRType {
	ALLOY_RATIO_RECIPE,
	ALLOY_SHAPED_RECIPE,
	NONE;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static AlloyRecipeType deserialize(String name) {
		for (AlloyRecipeType type : values()) {

			if (type.getName().equals(name)) {
				return type;
			}
		}
		return NONE;
	}

	public AlloyRecipeType getByNameWithModId(String name, String modId) {
		for (AlloyRecipeType type : values()) {

			if ((modId + ":" + type.getName()).equals(name)) {
				return type;
			}
		}
		return NONE;
	}
}
