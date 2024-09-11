package minefantasy.mfr.recipe.factories;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.recipe.RoastRecipeBase;
import minefantasy.mfr.recipe.types.RoastRecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

public class RoastRecipeFactory implements IRecipeMFRFactory<RoastRecipeBase> {
	public RoastRecipeBase parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "type");
		RoastRecipeType recipeType = RoastRecipeType.deserialize(type);
		if (recipeType == RoastRecipeType.COOKING_RECIPE) {
			return parseStandard(context, json);
		}
		return null;
	}

	private RoastRecipeBase parseStandard(JsonContext context, JsonObject json) {
		NonNullList<Ingredient> ingredients = NonNullList.create();

		for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients")) {
			ingredients.add(CraftingHelper.getIngredient(ele, context));
		}

		if (ingredients.isEmpty()) {
			throw new JsonParseException("No ingredients for cooking recipe");
		}

		ItemStack burnt_output;
		if (JsonUtils.hasField(json, "burnt_output")) {
			burnt_output = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "burnt_output"), context);
		}
		else {
			burnt_output = ItemStack.EMPTY;
		}

		int min_temperature = JsonUtils.getInt(json, "min_temperature", 0);
		int max_temperature = JsonUtils.getInt(json, "max_temperature", 0);
		int cook_time = JsonUtils.getInt(json, "cook_time", 0);
		int burn_time = JsonUtils.getInt(json, "burn_time", 0);
		boolean can_burn = JsonUtils.getBoolean(json, "can_burn", false);
		boolean is_oven_recipe = JsonUtils.getBoolean(json, "is_oven_recipe", false);
		String requiredResearch = JsonUtils.getString(json, "research", "none");
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);

		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

		return new RoastRecipeBase(result, ingredients, burnt_output, min_temperature, max_temperature,
				cook_time, burn_time, can_burn, is_oven_recipe,
				requiredResearch, skill, skillXp, vanillaXp);
	}
}
