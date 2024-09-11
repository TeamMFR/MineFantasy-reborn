package minefantasy.mfr.recipe.factories;

import com.google.gson.JsonObject;
import minefantasy.mfr.recipe.SpecialRecipeBase;
import minefantasy.mfr.recipe.types.SpecialRecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

public class SpecialRecipeFactory implements IRecipeMFRFactory<SpecialRecipeBase> {
	public SpecialRecipeBase parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "type");
		SpecialRecipeType recipeType = SpecialRecipeType.deserialize(type);
		switch (recipeType) {
			case SPECIAL_RECIPE_DRAGONFORGED:
				return parseStandard(context, json, "dragonforged");
			case SPECIAL_RECIPE_ORNATE:
				return parseStandard(context, json, "ornate");
			default:
				return null;
		}
	}

	private SpecialRecipeBase parseStandard(JsonContext context, JsonObject json, String design) {

		Ingredient input = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "input"), context);
		Ingredient specialInput = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "specialInput"), context);

		ItemStack output = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

		String research = JsonUtils.getString(json, "research", "none");

		return new SpecialRecipeBase(input, specialInput, output, research, design);
	}
}
