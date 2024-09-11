package minefantasy.mfr.recipe.types;

import net.minecraft.util.IStringSerializable;

public enum SalvageRecipeType implements IStringSerializable, IRecipeMFRType {
	SALVAGE_RECIPE,
	SALVAGE_RECIPE_SHARED,
	NONE;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static SalvageRecipeType deserialize(String name) {
		for (SalvageRecipeType type : values()) {

			if (type.getName().equals(name)) {
				return type;
			}
		}
		return NONE;
	}

	public SalvageRecipeType getByNameWithModId(String name, String modId) {
		for (SalvageRecipeType type : values()) {

			if ((modId + ":" + type.getName()).equals(name)) {
				return type;
			}
		}
		return NONE;
	}
}
