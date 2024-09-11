package minefantasy.mfr.recipe.types;

import net.minecraft.util.IStringSerializable;

public enum QuernRecipeType implements IStringSerializable, IRecipeMFRType {
	QUERN_RECIPE,
	NONE;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static QuernRecipeType deserialize(String name) {
		for (QuernRecipeType type : values()) {

			if (type.getName().equals(name)) {
				return type;
			}
		}
		return NONE;
	}

	public QuernRecipeType getByNameWithModId(String name, String modId) {
		for (QuernRecipeType type : values()) {

			if ((modId + ":" + type.getName()).equals(name)) {
				return type;
			}
		}
		return NONE;
	}
}
