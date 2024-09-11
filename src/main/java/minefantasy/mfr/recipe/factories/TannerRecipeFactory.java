package minefantasy.mfr.recipe.factories;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.recipe.TannerRecipeBase;
import minefantasy.mfr.recipe.types.TannerRecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

public class TannerRecipeFactory implements IRecipeMFRFactory<TannerRecipeBase> {
	public TannerRecipeBase parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "type");
		TannerRecipeType recipeType = TannerRecipeType.deserialize(type);
		if (recipeType == TannerRecipeType.TANNER_RECIPE) {
			return parseStandard(context, json);
		}
		return null;
	}

	private TannerRecipeBase parseStandard(JsonContext context, JsonObject json) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients")) {
			ingredients.add(CraftingHelper.getIngredient(ele, context));
		}

		if (ingredients.isEmpty()) {
			throw new JsonParseException("No ingredients for tanner recipe");
		}

		int craft_time = JsonUtils.getInt(json, "craft_time", 0);
		int tanner_tier = JsonUtils.getInt(json, "tanner_tier", 0);
		String tool_type = JsonUtils.getString(json, "tool_type", "none");
		String requiredResearch = JsonUtils.getString(json, "research", "none");
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);

		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

		return new TannerRecipeBase(result, ingredients, tool_type, tanner_tier, craft_time,
				requiredResearch, skill, skillXp, vanillaXp);
	}
}
