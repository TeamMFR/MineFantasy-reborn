package minefantasy.mfr.recipe.factories;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.recipe.SalvageRecipeBase;
import minefantasy.mfr.recipe.SalvageRecipeShared;
import minefantasy.mfr.recipe.SalvageRecipeStandard;
import minefantasy.mfr.recipe.types.SalvageRecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

public class SalvageRecipeFactory implements IRecipeMFRFactory<SalvageRecipeBase> {
	public SalvageRecipeBase parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "type");
		SalvageRecipeType recipeType = SalvageRecipeType.deserialize(type);
		switch (recipeType) {
			case SALVAGE_RECIPE:
				return parseStandard(context, json);
			case SALVAGE_RECIPE_SHARED:
				return parseStandardWithShared(context, json);
			default:
				return null;
		}
	}

	private SalvageRecipeBase parseStandardWithShared(JsonContext context, JsonObject json) {

		ItemStack input = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "input"), context);

		NonNullList<Ingredient> outputs = NonNullList.create();
		for (JsonElement ele : JsonUtils.getJsonArray(json, "outputs")) {
			outputs.add(CraftingHelper.getIngredient(ele, context));
		}

		if (outputs.isEmpty()) {
			throw new JsonParseException("No outputs for salvage recipe");
		}

		NonNullList<ItemStack> shared = NonNullList.create();
		for (JsonElement ele : JsonUtils.getJsonArray(json, "shared")) {
			shared.add(CraftingHelper.getItemStack(ele.getAsJsonObject(), context));
		}

		if (shared.isEmpty()) {
			throw new JsonParseException("No shared items for shared salvage recipe");
		}

		String requiredResearch = JsonUtils.getString(json, "research", "none");
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);

		return new SalvageRecipeShared(input, outputs, shared, requiredResearch, skill, skillXp, vanillaXp);
	}

	private SalvageRecipeBase parseStandard(JsonContext context, JsonObject json) {

		ItemStack input = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "input"), context);

		NonNullList<Ingredient> outputs = NonNullList.create();
		for (JsonElement ele : JsonUtils.getJsonArray(json, "outputs")) {
			outputs.add(CraftingHelper.getIngredient(ele, context));
		}

		if (outputs.isEmpty()) {
			throw new JsonParseException("No outputs for salvage recipe");
		}

		String requiredResearch = JsonUtils.getString(json, "research", "none");
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);

		return new SalvageRecipeStandard(input, outputs, requiredResearch, skill, skillXp, vanillaXp);
	}
}
