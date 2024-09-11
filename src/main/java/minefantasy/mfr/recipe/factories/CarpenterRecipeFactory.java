package minefantasy.mfr.recipe.factories;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.recipe.CarpenterDynamicRecipe;
import minefantasy.mfr.recipe.CarpenterRecipeBase;
import minefantasy.mfr.recipe.CarpenterShapedCustomMaterialRecipe;
import minefantasy.mfr.recipe.CarpenterShapedRecipe;
import minefantasy.mfr.recipe.CarpenterShapelessCustomMaterialRecipe;
import minefantasy.mfr.recipe.CarpenterShapelessRecipe;
import minefantasy.mfr.recipe.types.CarpenterRecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CarpenterRecipeFactory implements IRecipeMFRFactory<CarpenterRecipeBase> {
	public CarpenterRecipeBase parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "type");
		CarpenterRecipeType recipeType = CarpenterRecipeType.deserialize(type);
		switch (recipeType) {
			case CARPENTER_SHAPED_RECIPE:
				return parseShaped(context, json);
			case CARPENTER_SHAPELESS_RECIPE:
				return parseShapeless(context, json);
			case CARPENTER_SHAPED_CUSTOM_MATERIAL_RECIPE:
				return parseShapedCustomMaterial(context, json);
			case CARPENTER_SHAPELESS_CUSTOM_MATERIAL_RECIPE:
				return parseShapelessCustomMaterial(context, json);
			case CARPENTER_DYNAMIC_RECIPE:
				return parseDynamic(context, json);
			default:
				return null;
		}
	}

	private CarpenterRecipeBase parseDynamic(JsonContext context, JsonObject json) {
		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		String research = JsonUtils.getString(json, "research", "none");
		String sound = JsonUtils.getString(json, "sound", "minecraft:block.wood.hit");
		String tool_type = JsonUtils.getString(json, "tool_type", "none");
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);
		int craft_time = JsonUtils.getInt(json, "craft_time", 0);
		int tool_tier = JsonUtils.getInt(json, "tool_tier", 0);
		int block_tier = JsonUtils.getInt(json, "block_tier", -1);

		SoundEvent soundEvent = SoundEvent.REGISTRY.getObject(new ResourceLocation(sound));
		handleSoundError(sound, soundEvent, recipe.getRecipeOutput());

		return new CarpenterDynamicRecipe(
				recipe.getRecipeOutput(), recipe.getIngredients(),
				tool_tier, block_tier, craft_time, skillXp, vanillaXp, tool_type,
				SoundEvent.REGISTRY.getObject(new ResourceLocation(sound)), research, skill,
				recipe.getRecipeWidth(), recipe.getRecipeHeight());
	}

	private CarpenterRecipeBase parseShapelessCustomMaterial(JsonContext context, JsonObject json) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients")) {
			ingredients.add(CraftingHelper.getIngredient(ele, context));
		}

		if (ingredients.isEmpty()) {
			throw new JsonParseException("No ingredients for shapeless recipe");
		}

		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		String research = JsonUtils.getString(json, "research", "none");
		String sound = JsonUtils.getString(json, "sound", "minecraft:block.wood.hit");
		String tool_type = JsonUtils.getString(json, "tool_type", "none");
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);
		int craft_time = JsonUtils.getInt(json, "craft_time", 0);
		int tool_tier = JsonUtils.getInt(json, "tool_tier", 0);
		int block_tier = JsonUtils.getInt(json, "block_tier", -1);

		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

		SoundEvent soundEvent = SoundEvent.REGISTRY.getObject(new ResourceLocation(sound));
		handleSoundError(sound, soundEvent, result);

		return new CarpenterShapelessCustomMaterialRecipe(
				result, ingredients, tool_tier, block_tier, craft_time,
				skillXp, vanillaXp, tool_type,
				SoundEvent.REGISTRY.getObject(new ResourceLocation(sound)), research, skill);
	}

	private CarpenterRecipeBase parseShapedCustomMaterial(JsonContext context, JsonObject json) {
		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		String research = JsonUtils.getString(json, "research", "none");
		String sound = JsonUtils.getString(json, "sound", "minecraft:block.wood.hit");
		String tool_type = JsonUtils.getString(json, "tool_type", "none");
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);
		int craft_time = JsonUtils.getInt(json, "craft_time", 0);
		int tool_tier = JsonUtils.getInt(json, "tool_tier", 0);
		int block_tier = JsonUtils.getInt(json, "block_tier", -1);

		SoundEvent soundEvent = SoundEvent.REGISTRY.getObject(new ResourceLocation(sound));
		handleSoundError(sound, soundEvent, recipe.getRecipeOutput());

		return new CarpenterShapedCustomMaterialRecipe(
				recipe.getRecipeOutput(), recipe.getIngredients(),
				tool_tier, block_tier, craft_time, skillXp, vanillaXp, tool_type,
				SoundEvent.REGISTRY.getObject(new ResourceLocation(sound)), research, skill,
				recipe.getRecipeWidth(), recipe.getRecipeHeight());
	}

	private CarpenterRecipeBase parseShapeless(JsonContext context, JsonObject json) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients")) {
			ingredients.add(CraftingHelper.getIngredient(ele, context));
		}

		if (ingredients.isEmpty()) {
			throw new JsonParseException("No ingredients for shapeless recipe");
		}

		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		String research = JsonUtils.getString(json, "research", "none");
		String sound = JsonUtils.getString(json, "sound", "minecraft:block.wood.hit");
		String tool_type = JsonUtils.getString(json, "tool_type", "none");
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);
		int craft_time = JsonUtils.getInt(json, "craft_time", 0);
		int tool_tier = JsonUtils.getInt(json, "tool_tier", 0);
		int block_tier = JsonUtils.getInt(json, "block_tier", -1);

		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

		SoundEvent soundEvent = SoundEvent.REGISTRY.getObject(new ResourceLocation(sound));
		handleSoundError(sound, soundEvent, result);

		return new CarpenterShapelessRecipe(
				result, ingredients,
				tool_tier, block_tier, craft_time, skillXp, vanillaXp, tool_type,
				SoundEvent.REGISTRY.getObject(new ResourceLocation(sound)), research, skill);
	}

	private CarpenterRecipeBase parseShaped(JsonContext context, JsonObject json) {
		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		String research = JsonUtils.getString(json, "research", "none");
		String sound = JsonUtils.getString(json, "sound", "minecraft:block.wood.hit");
		String tool_type = JsonUtils.getString(json, "tool_type", "none");
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);
		int craft_time = JsonUtils.getInt(json, "craft_time", 0);
		int tool_tier = JsonUtils.getInt(json, "tool_tier", 0);
		int block_tier = JsonUtils.getInt(json, "block_tier", -1);

		boolean shouldMirror = JsonUtils.getBoolean(json, "shouldMirror", true);

		SoundEvent soundEvent = SoundEvent.REGISTRY.getObject(new ResourceLocation(sound));
		handleSoundError(sound, soundEvent, recipe.getRecipeOutput());

		return new CarpenterShapedRecipe(
				recipe.getRecipeOutput(), recipe.getIngredients(),
				tool_tier, block_tier, craft_time, skillXp, vanillaXp, tool_type,
				SoundEvent.REGISTRY.getObject(new ResourceLocation(sound)), research, skill, shouldMirror,
				recipe.getRecipeWidth(), recipe.getRecipeHeight());
	}

	private static void handleSoundError(String sound, SoundEvent soundEvent, ItemStack recipeOutput) {
		if (soundEvent == null) {
			MineFantasyReforged.LOG.error(
					"[MFR RECIPE PARSE ERROR] Sound for recipe output, {}, could not be found. Invalid sound resource: {}",
					recipeOutput.getDisplayName(), sound);
		}
	}
}
