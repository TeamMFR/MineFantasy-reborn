package minefantasy.mfr.recipe.factories;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.recipe.TransformationRecipeBase;
import minefantasy.mfr.recipe.TransformationRecipeBlockState;
import minefantasy.mfr.recipe.TransformationRecipeProgressive;
import minefantasy.mfr.recipe.TransformationRecipeStandard;
import minefantasy.mfr.recipe.types.TransformationRecipeType;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class TransformationRecipeFactory implements IRecipeMFRFactory<TransformationRecipeBase> {
	public TransformationRecipeBase parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "type");
		TransformationRecipeType recipeType = TransformationRecipeType.deserialize(type);
		switch (recipeType) {
			case TRANSFORMATION_RECIPE:
				return parseStandard(context, json);
			case TRANSFORMATION_RECIPE_BLOCKSTATE:
				return parseWithBlockStates(context, json);
			case TRANSFORMATION_RECIPE_PROGRESSIVE:
				return parseProgressive(context, json);
			default:
				return null;
		}
	}

	private TransformationRecipeBase parseProgressive(JsonContext context, JsonObject json) {
		Tool tool = Tool.fromName(JsonUtils.getString(json, "tool_type", "other"));

		NonNullList<Ingredient> consumableStacks = NonNullList.create();
		if (JsonUtils.hasField(json, "consumableStacks")) {
			for (JsonElement ele : JsonUtils.getJsonArray(json, "consumableStacks")) {
				consumableStacks.add(CraftingHelper.getIngredient(ele, context));
			}
		}

		Ingredient dropStack = Ingredient.EMPTY;
		if (JsonUtils.hasField(json, "dropStack")) {
			dropStack = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "dropStack"), context);
		}

		boolean shouldDropOnProgress = JsonUtils.getBoolean(json, "shouldDropOnProgress", false);

		Ingredient offhandStack = Ingredient.EMPTY;
		if (JsonUtils.hasField(json, "offhandStack")) {
			offhandStack = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "offhandStack"), context);
		}

		int maxProgress = JsonUtils.getInt(json, "maxProgress", 1);
		String soundName = JsonUtils.getString(json, "soundName", "");

		IBlockState inputState = parseBlockState(json.get("inputBlockState"));
		if (inputState.getBlock() == Blocks.AIR) {
			throw new JsonParseException("The input BlockState for this transformation recipe is invalid!");
		}

		String propertyName = JsonUtils.getString(json, "propertyName", "");
		if (propertyName.isEmpty()) {
			throw new JsonParseException("The property name for this transformation is invalid!");
		}

		IProperty<?> retrievedProperty = inputState.getBlock().getBlockState().getProperty(propertyName);
		if (!(retrievedProperty instanceof PropertyInteger)) {
			throw new JsonParseException("The Property defined by the propertyName is not an integer!");
		}
		PropertyInteger property = (PropertyInteger) retrievedProperty;

		boolean shouldIncrement = JsonUtils.getBoolean(json, "shouldIncrement", true);

		JsonObject outputBlockStateJson = json.getAsJsonObject("outputBlockState");
		IBlockState outputState = parseBlockState(outputBlockStateJson);
		if (outputState.getBlock() == Blocks.AIR
				&& !JsonUtils.getString(outputBlockStateJson.get("Name"), "Name").equals("minecraft:air")) {
			throw new JsonParseException("The output BlockState for this transformation recipe is invalid!");
		}

		String requiredResearch = JsonUtils.getString(json, "research", "none");
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);

		return new TransformationRecipeProgressive(
				property,
				inputState, outputState,
				tool, consumableStacks, dropStack, shouldDropOnProgress, offhandStack,
				skill, requiredResearch, skillXp, vanillaXp, maxProgress, soundName,
				shouldIncrement);
	}

	private TransformationRecipeBase parseWithBlockStates(JsonContext context, JsonObject json) {
		Tool tool = Tool.fromName(JsonUtils.getString(json, "tool_type", "other"));

		NonNullList<Ingredient> consumableStacks = NonNullList.create();
		if (JsonUtils.hasField(json, "consumableStacks")) {
			for (JsonElement ele : JsonUtils.getJsonArray(json, "consumableStacks")) {
				consumableStacks.add(CraftingHelper.getIngredient(ele, context));
			}
		}

		Ingredient dropStack = Ingredient.EMPTY;
		if (JsonUtils.hasField(json, "dropStack")) {
			dropStack = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "dropStack"), context);
		}

		boolean shouldDropOnProgress = JsonUtils.getBoolean(json, "shouldDropOnProgress", false);

		Ingredient offhandStack = Ingredient.EMPTY;
		if (JsonUtils.hasField(json, "offhandStack")) {
			offhandStack = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "offhandStack"), context);
		}

		int maxProgress = JsonUtils.getInt(json, "maxProgress", 1);
		String soundName = JsonUtils.getString(json, "soundName", "");

		IBlockState inputState = parseBlockState(json.get("inputBlockState"));
		if (inputState.getBlock() == Blocks.AIR) {
			throw new JsonParseException("The input BlockState for this transformation recipe is invalid!");
		}

		JsonObject outputBlockStateJson = json.getAsJsonObject("outputBlockState");
		IBlockState outputState = parseBlockState(outputBlockStateJson);
		if (outputState.getBlock() == Blocks.AIR
				&& !JsonUtils.getString(outputBlockStateJson.get("Name"), "Name").equals("minecraft:air")) {
			throw new JsonParseException("The output BlockState for this transformation recipe is invalid!");
		}

		String requiredResearch = JsonUtils.getString(json, "research", "none");
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);

		return new TransformationRecipeBlockState(
				inputState, outputState,
				tool, consumableStacks, dropStack, shouldDropOnProgress, offhandStack,
				skill, requiredResearch, skillXp, vanillaXp, maxProgress, soundName);
	}

	private IBlockState parseBlockState(JsonElement json) {
		try {
			NBTTagCompound nbt = JsonToNBT.getTagFromJson(json.toString());
			return NBTUtil.readBlockState(nbt);
		}
		catch (NBTException e) {
			throw new JsonParseException(e);
		}
	}

	private TransformationRecipeBase parseStandard(JsonContext context, JsonObject json) {
		Tool tool = Tool.fromName(JsonUtils.getString(json, "tool_type", "other"));

		NonNullList<Ingredient> inputs = NonNullList.create();

		for (JsonElement ele : JsonUtils.getJsonArray(json, "inputs")) {
			inputs.add(CraftingHelper.getIngredient(ele, context));
		}

		if (inputs.isEmpty()) {
			throw new JsonParseException("No inputs for transformation recipe");
		}

		inputs.forEach(ingredient -> Arrays.stream(ingredient.getMatchingStacks())
				.forEach(stack -> {
					if (!(stack.getItem() instanceof ItemBlock)) {
						throw new JsonParseException(
								String.format("This stack '%s' is not an ItemBlock "
												+ "when it is a input for a transformation recipe!",
										stack));
					}
				}));


		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
		if (!(result.getItem() instanceof ItemBlock)) {
			throw new JsonParseException("The output of this transformation recipe is not an ItemBlock!");
		}

		NonNullList<Ingredient> consumableStacks = NonNullList.create();
		if (JsonUtils.hasField(json, "consumableStacks")) {
			for (JsonElement ele : JsonUtils.getJsonArray(json, "consumableStacks")) {
				consumableStacks.add(CraftingHelper.getIngredient(ele, context));
			}
		}

		Ingredient dropStack = Ingredient.EMPTY;
		if (JsonUtils.hasField(json, "dropStack")) {
			dropStack = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "dropStack"), context);
		}

		boolean shouldDropOnProgress = JsonUtils.getBoolean(json, "shouldDropOnProgress", false);

		Ingredient offhandStack = Ingredient.EMPTY;
		if (JsonUtils.hasField(json, "offhandStack")) {
			offhandStack = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "offhandStack"), context);
		}

		List<String> blockStateProperties = new ArrayList<>();
		if (JsonUtils.hasField(json, "blockStateProperties")) {
			for (JsonElement ele : JsonUtils.getJsonArray(json, "blockStateProperties")) {
				blockStateProperties.add(ele.getAsString());
			}
		}

		List<String> outputBlockStateProperties = Block.getBlockFromItem(result.getItem())
				.getDefaultState()
				.getPropertyKeys()
				.stream()
				.map(IProperty::getName)
				.collect(Collectors.toList());

		if (!(new HashSet<>(outputBlockStateProperties).containsAll(blockStateProperties))) {
			throw new JsonParseException(String.format("Inputted blockStateProperties, %s, for this transformation recipe "
							+ "do not match the output blockState properties, %s",
					blockStateProperties,
					outputBlockStateProperties));
		}

		int maxProgress = JsonUtils.getInt(json, "maxProgress", 1);
		String soundName = JsonUtils.getString(json, "soundName", "");

		String requiredResearch = JsonUtils.getString(json, "research", "none");
		Skill skill = Skill.fromName(JsonUtils.getString(json, "skill", "none"));
		int skillXp = JsonUtils.getInt(json, "skill_xp", 0);
		float vanillaXp = JsonUtils.getFloat(json, "vanilla_xp", 0);

		return new TransformationRecipeStandard(tool, inputs, result, consumableStacks,
				dropStack, shouldDropOnProgress, offhandStack, skill, requiredResearch, skillXp, vanillaXp, maxProgress, soundName,
				blockStateProperties.isEmpty() ? outputBlockStateProperties : blockStateProperties);
	}
}
