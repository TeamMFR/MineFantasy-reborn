package minefantasy.mfr.recipe.types;

import net.minecraft.util.IStringSerializable;

public enum TransformationRecipeType implements IStringSerializable, IRecipeMFRType {
	TRANSFORMATION_RECIPE,
	TRANSFORMATION_RECIPE_BLOCKSTATE,
	TRANSFORMATION_RECIPE_PROGRESSIVE,
	NONE;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static TransformationRecipeType deserialize(String name) {
		for (TransformationRecipeType type : values()) {

			if (type.getName().equals(name)) {
				return type;
			}
		}
		return NONE;
	}

	public TransformationRecipeType getByNameWithModId(String name, String modId) {
		for (TransformationRecipeType type : values()) {

			if ((modId + ":" + type.getName()).equals(name)) {
				return type;
			}
		}
		return NONE;
	}
}
