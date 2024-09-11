package minefantasy.mfr.recipe.factories;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.recipe.AnvilDynamicRecipe;
import minefantasy.mfr.recipe.AnvilRecipeBase;
import minefantasy.mfr.recipe.AnvilShapedCustomMaterialRecipe;
import minefantasy.mfr.recipe.AnvilShapedRecipe;
import minefantasy.mfr.recipe.AnvilShapelessCustomMaterialRecipe;
import minefantasy.mfr.recipe.AnvilShapelessRecipe;
import minefantasy.mfr.recipe.types.AnvilRecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class AnvilRecipeFactory implements IRecipeMFRFactory<AnvilRecipeBase> {

	public AnvilRecipeBase parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "type");
		AnvilRecipeType recipeType = AnvilRecipeType.deserialize(type);
		switch (recipeType) {
			case ANVIL_SHAPED_RECIPE:
				return parseShaped(context, json);
			case ANVIL_SHAPELESS_RECIPE:
				return parseShapeless(context, json);
			case ANVIL_SHAPED_CUSTOM_MATERIAL_RECIPE:
				return parseShapedCustomMaterial(context, json);
			case ANVIL_SHAPELESS_CUSTOM_MATERIAL_RECIPE:
				return parseShapelessCustomMaterial(context, json);
			case ANVIL_DYNAMIC_RECIPE:
				return parseDynamic(context, json);
			default:
				return null;
		}
	}

	private AnvilRecipeBase parseDynamic(JsonContext context, JsonObject json) {
		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		String research = JsonUtils.getString(json, "research", "none");
		String tool_type = JsonUtils.getString(json, "tool_type", "none");
		boolean output_hot = JsonUtils.getBoolean(json, "output_hot", false);
		int recipe_hammer = JsonUtils.getInt(json, "recipe_hammer", 0);
		int anvil_tier = JsonUtils.getInt(json, "anvil_tier", 0);
		int recipe_time = JsonUtils.getInt(json, "recipe_time", 0);
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);
		boolean modifyOutput = JsonUtils.getBoolean(json, "modify_output", false);
		boolean shouldModifyTiers = JsonUtils.getBoolean(json, "should_modify_tiers", true);

		return new AnvilDynamicRecipe(recipe.getIngredients(), recipe.getRecipeOutput(),
				tool_type, recipe_time, recipe_hammer, anvil_tier, output_hot, research, skill,
				skillXp, vanillaXp, modifyOutput, shouldModifyTiers,
				recipe.getRecipeWidth(), recipe.getRecipeHeight());
	}

	private AnvilRecipeBase parseShapelessCustomMaterial(JsonContext context, JsonObject json) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients")) {
			ingredients.add(CraftingHelper.getIngredient(ele, context));
		}

		if (ingredients.isEmpty()) {
			throw new JsonParseException("No ingredients for shapeless recipe");
		}

		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		String research = JsonUtils.getString(json, "research", "none");
		String tool_type = JsonUtils.getString(json, "tool_type", "none");
		boolean output_hot = JsonUtils.getBoolean(json, "output_hot", false);
		int recipe_hammer = JsonUtils.getInt(json, "recipe_hammer", 0);
		int anvil_tier = JsonUtils.getInt(json, "anvil_tier", 0);
		int recipe_time = JsonUtils.getInt(json, "recipe_time", 0);
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);
		boolean tierModifyOutputCount = JsonUtils.getBoolean(json, "tierModifyOutputCount", false);

		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

		return new AnvilShapelessCustomMaterialRecipe(ingredients, result,
				tool_type, recipe_time, recipe_hammer, anvil_tier, output_hot, research, skill, skillXp, vanillaXp,
				tierModifyOutputCount);
	}

	private AnvilRecipeBase parseShapedCustomMaterial(JsonContext context, JsonObject json) {
		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		String research = JsonUtils.getString(json, "research", "none");
		String tool_type = JsonUtils.getString(json, "tool_type", "none");
		boolean output_hot = JsonUtils.getBoolean(json, "output_hot", false);
		int recipe_hammer = JsonUtils.getInt(json, "recipe_hammer", 0);
		int anvil_tier = JsonUtils.getInt(json, "anvil_tier", 0);
		int recipe_time = JsonUtils.getInt(json, "recipe_time", 0);
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);
		boolean tierModifyOutputCount = JsonUtils.getBoolean(json, "tierModifyOutputCount", false);

		return new AnvilShapedCustomMaterialRecipe(recipe.getIngredients(), recipe.getRecipeOutput(),
				tool_type, recipe_time, recipe_hammer, anvil_tier, output_hot, research, skill,
				skillXp, vanillaXp,
				recipe.getRecipeWidth(), recipe.getRecipeHeight(), tierModifyOutputCount);
	}

	private AnvilRecipeBase parseShapeless(JsonContext context, JsonObject json) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients")) {
			ingredients.add(CraftingHelper.getIngredient(ele, context));
		}

		if (ingredients.isEmpty()) {
			throw new JsonParseException("No ingredients for shapeless recipe");
		}

		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		String research = JsonUtils.getString(json, "research", "none");
		String tool_type = JsonUtils.getString(json, "tool_type", "none");
		boolean output_hot = JsonUtils.getBoolean(json, "output_hot", false);
		int recipe_hammer = JsonUtils.getInt(json, "recipe_hammer", 0);
		int anvil_tier = JsonUtils.getInt(json, "anvil_tier", 0);
		int recipe_time = JsonUtils.getInt(json, "recipe_time", 0);
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);

		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

		return new AnvilShapelessRecipe(ingredients, result, tool_type,
				recipe_time, recipe_hammer, anvil_tier, output_hot, research, skill, skillXp, vanillaXp);
	}

	private AnvilRecipeBase parseShaped(JsonContext context, JsonObject json) {
		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		String research = JsonUtils.getString(json, "research", "none");
		String tool_type = JsonUtils.getString(json, "tool_type", "none");
		boolean output_hot = JsonUtils.getBoolean(json, "output_hot", false);
		int recipe_hammer = JsonUtils.getInt(json, "recipe_hammer", 0);
		int anvil_tier = JsonUtils.getInt(json, "anvil_tier", 0);
		int recipe_time = JsonUtils.getInt(json, "recipe_time", 0);
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);

		return new AnvilShapedRecipe(recipe.getIngredients(), recipe.getRecipeOutput(),
				tool_type, recipe_time, recipe_hammer, anvil_tier, output_hot, research, skill,
				skillXp, vanillaXp,
				recipe.getRecipeWidth(), recipe.getRecipeHeight());
	}
}
