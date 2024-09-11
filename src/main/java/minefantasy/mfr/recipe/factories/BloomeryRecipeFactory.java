package minefantasy.mfr.recipe.factories;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.recipe.BloomeryRecipeBase;
import minefantasy.mfr.recipe.types.BloomeryRecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

public class BloomeryRecipeFactory implements IRecipeMFRFactory<BloomeryRecipeBase> {
	public BloomeryRecipeBase parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "type");
		BloomeryRecipeType recipeType = BloomeryRecipeType.deserialize(type);
		if (recipeType == BloomeryRecipeType.BLOOMERY_RECIPE) {
			return parseStandard(context, json);
		}
		return null;
	}

	private BloomeryRecipeBase parseStandard(JsonContext context, JsonObject json) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients")) {
			ingredients.add(CraftingHelper.getIngredient(ele, context));
		}

		if (ingredients.isEmpty()) {
			throw new JsonParseException("No ingredients for bloomery recipe");
		}

		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

		String requiredResearch = JsonUtils.getString(json, "research", "none");
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);

		return new BloomeryRecipeBase(result, ingredients, requiredResearch, skill, skillXp, vanillaXp);
	}
}
