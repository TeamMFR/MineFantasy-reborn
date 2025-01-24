package minefantasy.mfr.recipe.types;

import net.minecraft.util.IStringSerializable;

public enum RecipeType implements IStringSerializable{
	ALLOY_RECIPES,
	ANVIL_RECIPES,

	BIG_FURNACE_RECIPES,
	BLAST_FURNACE_RECIPES,
	BLOOMERY_RECIPES,
	CARPENTER_RECIPES,
	KITCHEN_BENCH_RECIPES,
	QUERN_RECIPES,
	ROAST_RECIPES,
	SALVAGE_RECIPES,
	SPECIAL_RECIPES,
	TANNER_RECIPES,
	TRANSFORMATION_RECIPES,
	NONE;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static RecipeType deserialize(String name) {
		for (RecipeType type : values()) {

			if (type.getName().equals(name)) {
				return type;
			}
		}
		return NONE;
	}

	public RecipeType getByNameWithModId(String name, String modId) {
		for (RecipeType type : values()) {

			if ((modId + ":" + type.getName()).equals(name)) {
				return type;
			}
		}
		return NONE;
	}
}
