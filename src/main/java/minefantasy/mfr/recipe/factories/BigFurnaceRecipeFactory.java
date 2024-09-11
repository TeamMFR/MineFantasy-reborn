package minefantasy.mfr.recipe.factories;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.recipe.BigFurnaceRecipeBase;
import minefantasy.mfr.recipe.types.BigFurnaceRecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

public class BigFurnaceRecipeFactory implements IRecipeMFRFactory<BigFurnaceRecipeBase> {

	public BigFurnaceRecipeBase parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "type");
		BigFurnaceRecipeType recipeType = BigFurnaceRecipeType.deserialize(type);
		if (recipeType == BigFurnaceRecipeType.BIG_FURNACE_RECIPE) {
			return parseStandard(context, json);
		}
		return null;
	}

	private BigFurnaceRecipeBase parseStandard(JsonContext context, JsonObject json) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients")) {
			ingredients.add(CraftingHelper.getIngredient(ele, context));
		}

		if (ingredients.isEmpty()) {
			throw new JsonParseException("No ingredients for big furnace recipe");
		}

		int tier = JsonUtils.getInt(json, "furnace_tier", 0);
		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

		String requiredResearch = JsonUtils.getString(json, "research", "none");
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);

		return new BigFurnaceRecipeBase(result, ingredients, tier, requiredResearch, skill, skillXp, vanillaXp);
	}
}
