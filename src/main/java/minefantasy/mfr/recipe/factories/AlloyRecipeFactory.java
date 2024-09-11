package minefantasy.mfr.recipe.factories;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.recipe.AlloyRatioRecipe;
import minefantasy.mfr.recipe.AlloyRecipeBase;
import minefantasy.mfr.recipe.AlloyShapedRecipe;
import minefantasy.mfr.recipe.types.AlloyRecipeType;
import minefantasy.mfr.util.RecipeHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class AlloyRecipeFactory implements IRecipeMFRFactory<AlloyRecipeBase> {
	public AlloyRecipeBase parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "type");
		AlloyRecipeType recipeType = AlloyRecipeType.deserialize(type);
		switch (recipeType) {
			case ALLOY_RATIO_RECIPE:
				return parseRatio(context, json);
			case ALLOY_SHAPED_RECIPE:
				return parseShaped(context, json);
			default:
				return null;
		}
	}

	private AlloyRecipeBase parseShaped(JsonContext context, JsonObject json) {
		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
		int crucible_tier = JsonUtils.getInt(json, "crucible_tier", 0);
		String requiredResearch = JsonUtils.getString(json, "research", "none");
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);

		return new AlloyShapedRecipe(recipe.getRecipeOutput(), recipe.getIngredients(), crucible_tier,
				requiredResearch, skill, skillXp, vanillaXp,
				recipe.getRecipeHeight(), recipe.getRecipeWidth());
	}

	private AlloyRecipeBase parseRatio(JsonContext context, JsonObject json) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients")) {
			ingredients.add(CraftingHelper.getIngredient(ele, context));
		}

		int crucible_tier = JsonUtils.getInt(json, "crucible_tier", 0);
		int repeat_amount = JsonUtils.getInt(json, "repeat_amount", 0);
		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

		if (ingredients.isEmpty()) {
			throw new JsonParseException("No ingredients for alloy ratio recipe");
		}
		if (RecipeHelper.duplicateList(ingredients, repeat_amount).size() > AlloyRecipeBase.getMaxCrucibleSize()) {
			throw new JsonParseException("Repeat amount is too high! That many of this ingredients will not fit into the Crucible");
		}
		if (hasDuplicateSimpleIngredients(ingredients)) {
			throw new JsonParseException(
					"Multiple of the same simple ingredient are not allowed in ratio alloy recipes." +
					"\n Simple Ingredients are simple item definitions, like minecraft:stick. Ingredients with nbt are considered complex." +
					"\n If that functionality is important to you, use alloy shaped recipe instead.");
		}

		String requiredResearch = JsonUtils.getString(json, "research", "none");
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);

		return new AlloyRatioRecipe(result, ingredients, crucible_tier,
				requiredResearch, skill, skillXp, vanillaXp,
				repeat_amount);
	}

	private boolean hasDuplicateSimpleIngredients(NonNullList<Ingredient> ingredients) {
		List<Ingredient> simples = ingredients.stream().filter(Ingredient::isSimple).collect(Collectors.toList());
		if (simples.isEmpty()) {
			return false;
		}
		return !simples.stream()
				.flatMap(ingredient -> Arrays.stream(ingredient.getMatchingStacks()))
				.map(ItemStack::getItem)
				.allMatch(new HashSet<>()::add);
	}
}
