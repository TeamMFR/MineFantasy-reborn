package minefantasy.mfr.recipe.types;

import net.minecraft.util.IStringSerializable;

public enum BloomeryRecipeType implements IStringSerializable, IRecipeMFRType {
	BLOOMERY_RECIPE,
	NONE;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static BloomeryRecipeType deserialize(String name) {
		for (BloomeryRecipeType type : values()) {

			if (type.getName().equals(name)) {
				return type;
			}
		}
		return NONE;
	}

	public BloomeryRecipeType getByNameWithModId(String name, String modId) {
		for (BloomeryRecipeType type : values()) {

			if ((modId + ":" + type.getName()).equals(name)) {
				return type;
			}
		}
		return NONE;
	}
}
