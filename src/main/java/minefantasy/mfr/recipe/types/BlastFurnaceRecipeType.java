package minefantasy.mfr.recipe.types;

import net.minecraft.util.IStringSerializable;

public enum BlastFurnaceRecipeType implements IStringSerializable, IRecipeMFRType {
	BLAST_FURNACE_RECIPE,
	NONE;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static BlastFurnaceRecipeType deserialize(String name) {
		for (BlastFurnaceRecipeType type : values()) {

			if (type.getName().equals(name)) {
				return type;
			}
		}
		return NONE;
	}

	public BlastFurnaceRecipeType getByNameWithModId(String name, String modId) {
		for (BlastFurnaceRecipeType type : values()) {

			if ((modId + ":" + type.getName()).equals(name)) {
				return type;
			}
		}
		return NONE;
	}
}
