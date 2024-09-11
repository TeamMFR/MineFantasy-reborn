package minefantasy.mfr.recipe.types;

import net.minecraft.util.IStringSerializable;

public enum BigFurnaceRecipeType implements IStringSerializable, IRecipeMFRType {
	BIG_FURNACE_RECIPE,
	NONE;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static BigFurnaceRecipeType deserialize(String name) {
		for (BigFurnaceRecipeType type : values()) {

			if (type.getName().equals(name)) {
				return type;
			}
		}
		return NONE;
	}

	public BigFurnaceRecipeType getByNameWithModId(String name, String modId) {
		for (BigFurnaceRecipeType type : values()) {

			if ((modId + ":" + type.getName()).equals(name)) {
				return type;
			}
		}
		return NONE;
	}
}
