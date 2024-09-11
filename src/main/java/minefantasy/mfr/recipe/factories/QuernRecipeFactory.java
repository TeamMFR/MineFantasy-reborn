package minefantasy.mfr.recipe.factories;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.recipe.QuernRecipeBase;
import minefantasy.mfr.recipe.types.QuernRecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

public class QuernRecipeFactory implements IRecipeMFRFactory<QuernRecipeBase> {
	public QuernRecipeBase parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "type");
		QuernRecipeType recipeType = QuernRecipeType.deserialize(type);
		if (recipeType == QuernRecipeType.QUERN_RECIPE) {
			return parseStandard(context, json);
		}
		return null;
	}

	private QuernRecipeBase parseStandard(JsonContext context, JsonObject json) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		NonNullList<Ingredient> pot_ingredients = NonNullList.create();

		for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients")) {
			ingredients.add(CraftingHelper.getIngredient(ele, context));
		}

		for (JsonElement ele : JsonUtils.getJsonArray(json, "pot_ingredients")) {
			pot_ingredients.add(CraftingHelper.getIngredient(ele, context));
		}

		if (ingredients.isEmpty()) {
			throw new JsonParseException("No ingredients for quern recipe");
		}

		if (pot_ingredients.isEmpty()) {
			throw new JsonParseException("No pot ingredients for quern recipe");
		}

		String requiredResearch = JsonUtils.getString(json, "research", "none");
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);

		boolean consume_pot = JsonUtils.getBoolean(json, "consume_pot", false);

		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

		return new QuernRecipeBase(result, ingredients, pot_ingredients, consume_pot,
				requiredResearch, skill, skillXp, vanillaXp);
	}
}
