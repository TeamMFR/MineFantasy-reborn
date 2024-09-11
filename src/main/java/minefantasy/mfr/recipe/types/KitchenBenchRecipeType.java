package minefantasy.mfr.recipe.types;

import net.minecraft.util.IStringSerializable;

public enum KitchenBenchRecipeType implements IStringSerializable, IRecipeMFRType {
	KITCHEN_BENCH_SHAPED_RECIPE,
	KITCHEN_BENCH_SHAPELESS_RECIPE,
	NONE;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static KitchenBenchRecipeType deserialize(String name) {
		for (KitchenBenchRecipeType type : values()) {

			if (type.getName().equals(name)) {
				return type;
			}
		}
		return NONE;
	}

	public KitchenBenchRecipeType getByNameWithModId(String name, String modId) {
		for (KitchenBenchRecipeType type : values()) {

			if ((modId + ":" + type.getName()).equals(name)) {
				return type;
			}
		}
		return NONE;
	}
}
