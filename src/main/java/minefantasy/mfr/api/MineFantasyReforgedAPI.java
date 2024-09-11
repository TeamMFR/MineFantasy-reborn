package minefantasy.mfr.api;

import com.google.common.collect.Lists;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.crafting.engineer.ICrossbowPart;
import minefantasy.mfr.api.heating.FirepitFuel;
import minefantasy.mfr.api.heating.ForgeFuel;
import minefantasy.mfr.api.heating.Heatable;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.recipe.AlloyRatioRecipe;
import minefantasy.mfr.recipe.AlloyShapedRecipe;
import minefantasy.mfr.recipe.AnvilDynamicRecipe;
import minefantasy.mfr.recipe.AnvilShapedCustomMaterialRecipe;
import minefantasy.mfr.recipe.AnvilShapedRecipe;
import minefantasy.mfr.recipe.AnvilShapelessCustomMaterialRecipe;
import minefantasy.mfr.recipe.AnvilShapelessRecipe;
import minefantasy.mfr.recipe.BigFurnaceRecipeBase;
import minefantasy.mfr.recipe.BlastFurnaceRecipeBase;
import minefantasy.mfr.recipe.BloomeryRecipeBase;
import minefantasy.mfr.recipe.CarpenterDynamicRecipe;
import minefantasy.mfr.recipe.CarpenterShapedCustomMaterialRecipe;
import minefantasy.mfr.recipe.CarpenterShapedRecipe;
import minefantasy.mfr.recipe.CarpenterShapelessCustomMaterialRecipe;
import minefantasy.mfr.recipe.CarpenterShapelessRecipe;
import minefantasy.mfr.recipe.KitchenBenchShapedRecipe;
import minefantasy.mfr.recipe.KitchenBenchShapelessRecipe;
import minefantasy.mfr.recipe.QuernRecipeBase;
import minefantasy.mfr.recipe.RoastRecipeBase;
import minefantasy.mfr.registry.FirepitFuelRegistry;
import minefantasy.mfr.registry.ForgeFuelRegistry;
import minefantasy.mfr.recipe.SalvageRecipeShared;
import minefantasy.mfr.recipe.SalvageRecipeStandard;
import minefantasy.mfr.recipe.SpecialRecipeBase;
import minefantasy.mfr.recipe.TannerRecipeBase;
import minefantasy.mfr.recipe.TransformationRecipeBlockState;
import minefantasy.mfr.recipe.TransformationRecipeStandard;
import minefantasy.mfr.util.MFRLogUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class MineFantasyReforgedAPI {
	/**
	 * This variable saves if MineFantasy is in debug mode
	 */
	public static boolean isInDebugMode;
	/**
	 * This fuel handler is for MineFantasy equipment, it uses real fuel(like coal)
	 * not wood
	 */
	private static List<IFuelHandler> fuelHandlers = Lists.newArrayList();

	@SideOnly(Side.CLIENT)
	public static void init() {
	}

	public static void debugMsg(String msg) {
		MFRLogUtil.logDebug(msg);
	}

	/**
	 * Adds an alloy ratio recipe with a minimal crucible level
	 *
	 * @param out               The result
	 * @param tier              The minimal crucible tier
	 * @param inputs            The list of required items
	 * @param requiredResearch  The Research the player performing the recipe is required to have unlocked
	 * @param skill 			The Skill of the player to be given xp
	 * @param skillXp			The amount of Skill xp to grant to the player for above skill for this recipe
	 * @param vanillaXp         The amount of vanilla xp to grant the player for this recipe
	 * @param repeatAmount 		How many times the ratio can repeat
	 * @param modId             The modId for this recipe to registered under
	 * @param name				The name of this recipe
	 */
	public static void addAlloyRatioRecipe(ItemStack out, NonNullList<Ingredient> inputs, int tier,
			String requiredResearch, Skill skill, int skillXp, float vanillaXp,
			int repeatAmount, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_ALLOY.addRecipe(new AlloyRatioRecipe(out, inputs, tier,
				requiredResearch, skill, skillXp, vanillaXp,
				repeatAmount), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds an alloy ratio recipe with a minimal crucible level
	 *
	 * @param out       		The result
	 * @param tier      		The minimal crucible tier
	 * @param inputs    		The list of required items
	 * @param requiredResearch  The Research the player performing the recipe is required to have unlocked
	 * @param skill 			The Skill of the player to be given xp
	 * @param skillXp			The amount of Skill xp to grant to the player for above skill for this recipe
	 * @param vanillaXp         The amount of vanilla xp to grant the player for this recipe
	 * @param height    		The height of the recipe
	 * @param width     		The width of the recipe
	 * @param modId             The modId for this recipe to registered under
	 * @param name				The name of this recipe
	 */
	public static void addAlloyShapedRecipe(ItemStack out, NonNullList<Ingredient> inputs, int tier,
			String requiredResearch, Skill skill, int skillXp, float vanillaXp,
			int height, int width, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_ALLOY.addRecipe(new AlloyShapedRecipe(out, inputs, tier,
				requiredResearch, skill, skillXp, vanillaXp,
				height, width), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a shaped recipe for anvils with all variables
	 *
	 * @param inputs              The ingredients of this recipe
	 * @param output     		  What the recipe results in
	 * @param tool                What is the tool type of this recipe
	 * @param craftTime           How long should this recipe take to make
	 * @param hammerTier          The required tier of the hammer for making this recipe
	 * @param anvilTier 		  The required tier of the anvil this recipe is used on
	 * @param outputHot           Does this recipe result in a hot output
	 * @param requiredResearch    The required Research for this recipe
	 * @param skill		  		  The Skill for this recipe
	 * @param skillXp 			  The amount of base skill xp for this recipe (for anvils this is modified by the craft itself
	 * @param vanillaXp           The amount of vanilla xp for this recipe
	 * @param width               The width of the recipe (Max = 6)
	 * @param height              The height of the recipe (Max = 4)
	 * @param modId               The modId for this recipe to registered under
	 * @param name				  The name of this recipe
	 */
	public static void addShapedAnvilRecipe(NonNullList<Ingredient> inputs, ItemStack output, Tool tool,
			int craftTime, int hammerTier, int anvilTier, boolean outputHot, String requiredResearch,
			Skill skill, int skillXp, float vanillaXp,
			int width, int height, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_ANVIL.addRecipe(new AnvilShapedRecipe(inputs, output, tool.getName(),
				craftTime, hammerTier, anvilTier, outputHot, requiredResearch, skill,
				skillXp, vanillaXp,
				width, height), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a shapeless recipe for anvils with all variables
	 *
	 * @param inputs              The ingredients of this recipe
	 * @param output     		  What the recipe results in
	 * @param tool                What is the tool type of this recipe
	 * @param craftTime           How long should this recipe take to make
	 * @param hammerTier          The required tier of the hammer for making this recipe
	 * @param anvilTier 		  The required tier of the anvil this recipe is used on
	 * @param outputHot           Does this recipe result in a hot output
	 * @param requiredResearch    The required Research for this recipe
	 * @param requiredSkill		  The required Skill for this recipe
	 * @param skillXp 			  The amount of base skill xp for this recipe (for anvils this is modified by the craft itself
	 * @param vanillaXp           The amount of vanilla xp for this recipe
	 * @param modId               The modId for this recipe to registered under
	 * @param name				  The name of this recipe
	 */
	public static void addShapelessAnvilRecipe(NonNullList<Ingredient> inputs, ItemStack output, Tool tool,
			int craftTime, int hammerTier, int anvilTier, boolean outputHot, String requiredResearch,
			Skill requiredSkill, int skillXp, float vanillaXp, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_ANVIL.addRecipe(new AnvilShapelessRecipe(inputs, output, tool.getName(),
				craftTime, hammerTier, anvilTier, outputHot, requiredResearch, requiredSkill, skillXp, vanillaXp),
				true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a shaped Custom Material recipe for anvils with all variables
	 *
	 * @param inputs              		The ingredients of this recipe
	 * @param output     		  		What the recipe results in
	 * @param tool                		What is the tool type of this recipe
	 * @param craftTime           		How long should this recipe take to make
	 * @param hammerTier          		The required tier of the hammer for making this recipe
	 * @param anvilTier 		  		The required tier of the anvil this recipe is used on
	 * @param outputHot           		Does this recipe result in a hot output
	 * @param requiredResearch    		The required Research for this recipe
	 * @param requiredSkill		  		The required Skill for this recipe
	 * @param skillXp 			        The amount of base skill xp for this recipe (for anvils this is modified by the craft itself
	 * @param vanillaXp                 The amount of vanilla xp for this recipe
	 * @param width               		The width of the recipe (Max = 6)
	 * @param height              		The height of the recipe (Max = 4)
	 * @param tierModifyOutputCount		Does this recipe modify the output count
	 * @param modId               		The modId for this recipe to registered under
	 * @param name				  		The name of this recipe
	 */
	public static void addShapedCustomMaterialAnvilRecipe(NonNullList<Ingredient> inputs, ItemStack output, Tool tool,
			int craftTime, int hammerTier, int anvilTier, boolean outputHot, String requiredResearch, Skill requiredSkill,
			int skillXp, float vanillaXp,
			int width, int height, boolean tierModifyOutputCount, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_ANVIL.addRecipe(new AnvilShapedCustomMaterialRecipe(inputs, output, tool.getName(),
				craftTime, hammerTier, anvilTier, outputHot, requiredResearch, requiredSkill,
				skillXp, vanillaXp,
				width, height, tierModifyOutputCount), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a shapeless Custom Material recipe for anvils with all variables
	 *
	 * @param inputs              		The ingredients of this recipe
	 * @param output     		  		What the recipe results in
	 * @param tool                		What is the tool type of this recipe
	 * @param craftTime           		How long should this recipe take to make
	 * @param hammerTier          		The required tier of the hammer for making this recipe
	 * @param anvilTier 		  		The required tier of the anvil this recipe is used on
	 * @param outputHot           		Does this recipe result in a hot output
	 * @param requiredResearch    		The required Research for this recipe
	 * @param requiredSkill		  		The required Skill for this recipe
	 * @param skillXp 			  		The amount of base skill xp for this recipe (for anvils this is modified by the craft itself
	 * @param vanillaXp           		The amount of vanilla xp for this recipe
	 * @param tierModifyOutputCount		Does this recipe modify the output count
	 * @param modId               		The modId for this recipe to registered under
	 * @param name				  		The name of this recipe
	 */
	public static void addShapelessCustomMaterialAnvilRecipe(NonNullList<Ingredient> inputs, ItemStack output, Tool tool,
			int craftTime, int hammerTier, int anvilTier, boolean outputHot, String requiredResearch,
			Skill requiredSkill, int skillXp, float vanillaXp,
			boolean tierModifyOutputCount, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_ANVIL.addRecipe(new AnvilShapelessCustomMaterialRecipe(inputs, output, tool.getName(),
				craftTime, hammerTier, anvilTier, outputHot, requiredResearch, requiredSkill, skillXp, vanillaXp,
				tierModifyOutputCount), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds an Anvil Dynamic Recipe with all variables
	 * Dynamic meaning it will seek to convert a Material's oreDict entry matching inputs to that Material MFR item (bars, ingots, etc.)
	 *
	 * @param inputs              		The ingredients of this recipe
	 * @param output     		  		What the recipe results in
	 * @param toolType                	What is the tool type of this recipe
	 * @param craftTime           		How long should this recipe take to make
	 * @param hammerTier          		The required tier of the hammer for making this recipe
	 * @param anvilTier 		  		The required tier of the anvil this recipe is used on
	 * @param hotOutput           		Does this recipe result in a hot output
	 * @param requiredResearch    		The required Research for this recipe
	 * @param requiredSkill		  		The required Skill for this recipe
	 * @param skillXp 			  		The amount of base skill xp for this recipe (for anvils this is modified by the craft itself
	 * @param vanillaXp           		The amount of vanilla xp for this recipe
	 * @param modifyOutput				Does this recipe convert from the Material to the OreDict entry, or the other way
	 * @param shouldModifyTiers			Does this recipe modify the required tier of the anvil/hammer, etc.
	 * @param width               		The width of the recipe (Max = 6)
	 * @param height              		The height of the recipe (Max = 4)
	 * @param modId               		The modId for this recipe to registered under
	 * @param name				  		The name of this recipe
	 */
	public static void addDynamicAnvilRecipe(NonNullList<Ingredient> inputs, ItemStack output,
			String toolType, int craftTime, int hammerTier, int anvilTier, boolean hotOutput,
			String requiredResearch, Skill requiredSkill,
			int skillXp, float vanillaXp, boolean modifyOutput, boolean shouldModifyTiers,
			int width, int height, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_ANVIL.addRecipe(new AnvilDynamicRecipe(inputs, output, toolType, craftTime,
				hammerTier, anvilTier, hotOutput, requiredResearch, requiredSkill, skillXp, vanillaXp, modifyOutput,
				shouldModifyTiers, width, height), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a Big Furnace Recipe with all variables
	 *
	 * @param input				The input Ingredients
	 * @param output			The output ItemStack
	 * @param tier				The required Tier of the Big Furnace
	 * @param requiredResearch  The required research for this recipe
	 * @param skill				The Skill to grant xp to
	 * @param skillXp			The amount of xp to give to Skill
	 * @param vanillaXp			The amount of vanilla xp to give
	 * @param modId				The modId for this recipe to registered under
	 * @param name				The name of this recipe
	 */
	public static void addBigFurnaceRecipe(NonNullList<Ingredient> input, ItemStack output, int tier,
			String requiredResearch, Skill skill, int skillXp, float vanillaXp, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_BIG_FURNACE.addRecipe(new BigFurnaceRecipeBase(output, input, tier,
				requiredResearch, skill, skillXp, vanillaXp), true, new ResourceLocation(modId, name));
	}

	/**
	 * Add a Blast Furnace Recipe with all variables
	 *
	 * @param inputs			Input Ingredients
	 * @param output			Output ItemStack
	 * @param requiredResearch  The Required Research for this Recipe
	 * @param skill				The Skill of this recipe
	 * @param skillXp			The amount of Skill Xp to be granted to the Recipe Skill
	 * @param vanillaXp			The amount of vanilla Xp to be granted
	 * @param modId             The modId for this recipe to registered under
	 * @param name				The name of this recipe
	 */
	public static void addBlastFurnaceRecipe(NonNullList<Ingredient> inputs, ItemStack output,
			String requiredResearch, Skill skill, int skillXp, float vanillaXp, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_BLAST_FURNACE.addRecipe(new BlastFurnaceRecipeBase(output, inputs, requiredResearch,
				skill, skillXp, vanillaXp), true, new ResourceLocation(modId, name));
	}

	/**
	 *	Add a Bloomery recipe with all variables
	 *
	 * @param output			The output ItemStack
	 * @param inputs			The input Ingredients
	 * @param requiredResearch	The required research for this recipe
	 * @param skill				The Skill to grant xp to
	 * @param skillXp			The amount of xp to give to Skill
	 * @param vanillaXp			The amount of vanilla xp to give
	 * @param modId				The modId for this recipe to registered under
	 * @param name				The name of this recipe
	 */
	public static void addBloomeryRecipe(ItemStack output, NonNullList<Ingredient> inputs,
			String requiredResearch, Skill skill, int skillXp, float vanillaXp, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_BLOOMERY.addRecipe(new BloomeryRecipeBase(output, inputs, requiredResearch,
				skill, skillXp, vanillaXp), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a Shaped Carpenter Recipe with all variables
	 *
	 * @param output			The ingredients of this recipe
	 * @param inputs			What the recipe results in
	 * @param toolTier			The required tier of the tool for making this recipe
	 * @param carpenterTier		The required tier of the carpenter this recipe is used on
	 * @param craftTime			How long should this recipe take to make
	 * @param skillXp			The amount of Skill Xp to be granted to the Recipe Skill
	 * @param vanillaXp			The amount of vanilla Xp to be granted
	 * @param toolType			What is the tool type of this recipe
	 * @param soundOfCraft		The Sound crafting the recipe makes
	 * @param research			The Required Research for this Recipe
	 * @param skillUsed			The Skill of this recipe
	 * @param shouldMirror		Should the Recipe be mirror-able in the crafting gui
	 * @param width             The width of the recipe (Max = 4)
	 * @param height            The height of the recipe (Max = 4)
	 * @param modId             The modId for this recipe to registered under
	 * @param name				The name of this recipe
	 */

	public static void addShapedCarpenterRecipe(ItemStack output, NonNullList<Ingredient> inputs,
			int toolTier, int carpenterTier, int craftTime,
			int skillXp, float vanillaXp, String toolType, SoundEvent soundOfCraft,
			String research, Skill skillUsed, boolean shouldMirror,
			int width, int height,
			String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_CARPENTER.addRecipe(new CarpenterShapedRecipe(output, inputs,
				toolTier, carpenterTier, craftTime, skillXp, vanillaXp, toolType, soundOfCraft, research, skillUsed, shouldMirror,
				width, height), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a Shapeless Carpenter Recipe with all variables
	 *
	 * @param output			The ingredients of this recipe
	 * @param inputs			What the recipe results in
	 * @param toolTier			The required tier of the tool for making this recipe
	 * @param carpenterTier		The required tier of the carpenter this recipe is used on
	 * @param craftTime			How long should this recipe take to make
	 * @param skillXp			The amount of Skill Xp to be granted to the Recipe Skill
	 * @param vanillaXp			The amount of vanilla Xp to be granted
	 * @param toolType			What is the tool type of this recipe
	 * @param soundOfCraft		The Sound crafting the recipe makes
	 * @param research			The Required Research for this Recipe
	 * @param skillUsed			The Skill of this recipe
	 * @param modId             The modId for this recipe to registered under
	 * @param name				The name of this recipe
	 */
	public static void addShapelessCarpenterRecipe(ItemStack output, NonNullList<Ingredient> inputs,
			int toolTier, int carpenterTier, int craftTime,
			int skillXp, float vanillaXp, String toolType,
			SoundEvent soundOfCraft, String research, Skill skillUsed,
			String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_CARPENTER.addRecipe(new CarpenterShapelessRecipe(output, inputs,
				toolTier, carpenterTier, craftTime,
				skillXp, vanillaXp, toolType,
				soundOfCraft, research, skillUsed), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a Shaped Custom Material Carpenter Recipe with all variables
	 *
	 * @param output			The ingredients of this recipe
	 * @param inputs			What the recipe results in
	 * @param toolTier			The required tier of the tool for making this recipe
	 * @param carpenterTier		The required tier of the carpenter this recipe is used on
	 * @param craftTime			How long should this recipe take to make
	 * @param skillXp			The amount of Skill Xp to be granted to the Recipe Skill
	 * @param vanillaXp			The amount of vanilla Xp to be granted
	 * @param toolType			What is the tool type of this recipe
	 * @param soundOfCraft		The Sound crafting the recipe makes
	 * @param research			The Required Research for this Recipe
	 * @param skillUsed			The Skill of this recipe
	 * @param width             The width of the recipe (Max = 4)
	 * @param height            The height of the recipe (Max = 4)
	 * @param modId             The modId for this recipe to registered under
	 * @param name				The name of this recipe
	 */
	public static void addShapedCustomMaterialCarpenterRecipe(ItemStack output, NonNullList<Ingredient> inputs,
			int toolTier, int carpenterTier, int craftTime,
			int skillXp, float vanillaXp, String toolType, SoundEvent soundOfCraft,
			String research, Skill skillUsed,
			int width, int height,
			String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_CARPENTER.addRecipe(new CarpenterShapedCustomMaterialRecipe(output, inputs,
				toolTier, carpenterTier, craftTime, skillXp, vanillaXp, toolType, soundOfCraft,
				research, skillUsed,
				width, height), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a Shapeless Custom Material Carpenter Recipe with all variables
	 *
	 * @param output			The ingredients of this recipe
	 * @param inputs			What the recipe results in
	 * @param toolTier			The required tier of the tool for making this recipe
	 * @param carpenterTier		The required tier of the carpenter this recipe is used on
	 * @param craftTime			How long should this recipe take to make
	 * @param skillXp			The amount of Skill Xp to be granted to the Recipe Skill
	 * @param vanillaXp			The amount of vanilla Xp to be granted
	 * @param toolType			What is the tool type of this recipe
	 * @param soundOfCraft		The Sound crafting the recipe makes
	 * @param research			The Required Research for this Recipe
	 * @param skillUsed			The Skill of this recipe
	 * @param modId             The modId for this recipe to registered under
	 * @param name				The name of this recipe
	 */
	public static void addShapelessCustomMaterialCarpenterRecipe(
			ItemStack output, NonNullList<Ingredient> inputs,
			int toolTier, int carpenterTier, int craftTime,
			int skillXp, float vanillaXp,
			String toolType, SoundEvent soundOfCraft, String research, Skill skillUsed,
			String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_CARPENTER.addRecipe(new CarpenterShapelessCustomMaterialRecipe(
				output, inputs, toolTier, carpenterTier, craftTime, skillXp, vanillaXp, toolType, soundOfCraft,
				research, skillUsed), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a Dynamic Custom Material Carpenter Recipe with all variables
	 * Dynamic meaning it will seek to convert a Material's oreDict entry matching inputs to that Material MFR item (timbers, etc.)
	 *
	 * @param output			The ingredients of this recipe
	 * @param inputs			What the recipe results in
	 * @param toolTier			The required tier of the tool for making this recipe
	 * @param carpenterTier		The required tier of the carpenter this recipe is used on
	 * @param craftTime			How long should this recipe take to make
	 * @param skillXp			The amount of Skill Xp to be granted to the Recipe Skill
	 * @param vanillaXp			The amount of vanilla Xp to be granted
	 * @param toolType			What is the tool type of this recipe
	 * @param soundOfCraft		The Sound crafting the recipe makes
	 * @param research			The Required Research for this Recipe
	 * @param skillUsed			The Skill of this recipe
	 * @param width             The width of the recipe (Max = 4)
	 * @param height            The height of the recipe (Max = 4)
	 * @param modId             The modId for this recipe to registered under
	 * @param name				The name of this recipe
	 */
	public static void addDynamicCarpenterRecipe(
			ItemStack output, NonNullList<Ingredient> inputs,
			int toolTier, int carpenterTier, int craftTime,
			int skillXp, float vanillaXp, String toolType, SoundEvent soundOfCraft,
			String research, Skill skillUsed,
			int width, int height,
			String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_CARPENTER.addRecipe(new CarpenterDynamicRecipe(output, inputs,
				toolTier, carpenterTier, craftTime, skillXp, vanillaXp, toolType, soundOfCraft,
				research, skillUsed, width, height), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a Shaped Kitchen Bench Recipe with all variables
	 *
	 * @param output				The ingredients of this recipe
	 * @param inputs				What the recipe results in
	 * @param toolTier				The required tier of the tool for making this recipe
	 * @param kitchenBenchTier		The required tier of the kitchen bench this recipe is used on
	 * @param craftTime				How long should this recipe take to make
	 * @param toolType				What is the tool type of this recipe
	 * @param soundOfCraft			The Sound crafting the recipe makes
	 * @param research				The Required Research for this Recipe
	 * @param skillUsed				The Skill of this recipe
	 * @param skillXp 				The amount of skill Xp to be granted
	 * @param vanillaXp				The amount of vanilla Xp to be granted
	 * @param dirtyProgressAmount	How much dirtiness the recipe generates
	 * @param shouldMirror			Should the Recipe be mirror-able in the crafting gui
	 * @param width             	The width of the recipe (Max = 4)
	 * @param height            	The height of the recipe (Max = 4)
	 * @param modId             	The modId for this recipe to registered under
	 * @param name					The name of this recipe
	 */
	public static void addKitchenBenchShapedRecipe(ItemStack output, NonNullList<Ingredient> inputs,
			int toolTier, int kitchenBenchTier, int craftTime,
			String toolType, SoundEvent soundOfCraft,
			String research, Skill skillUsed, int skillXp, float vanillaXp, int dirtyProgressAmount,
			boolean shouldMirror, int width, int height,
			String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_KITCHEN_BENCH.addRecipe(new KitchenBenchShapedRecipe(
				output, inputs, toolTier, kitchenBenchTier, craftTime, toolType, soundOfCraft, research, skillUsed,
				skillXp, vanillaXp, dirtyProgressAmount, shouldMirror, width, height
		), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a Shapeless Kitchen Bench Recipe with all variables
	 *
	 * @param output				The ingredients of this recipe
	 * @param inputs				What the recipe results in
	 * @param toolTier				The required tier of the tool for making this recipe
	 * @param kitchenBenchTier		The required tier of the kitchen bench this recipe is used on
	 * @param craftTime				How long should this recipe take to make
	 * @param toolType				What is the tool type of this recipe
	 * @param soundOfCraft			The Sound crafting the recipe makes
	 * @param research				The Required Research for this Recipe
	 * @param skillUsed				The Skill of this recipe
	 * @param skillXp 				The amount of skill Xp to be granted
	 * @param vanillaXp				The amount of vanilla Xp to be granted
	 * @param dirtyProgressAmount	How much dirtiness the recipe generates
	 * @param modId             	The modId for this recipe to registered under
	 * @param name					The name of this recipe
	 */
	public static void addKitchenBenchShapelessRecipe(ItemStack output, NonNullList<Ingredient> inputs,
			int toolTier, int kitchenBenchTier, int craftTime,
			String toolType, SoundEvent soundOfCraft,
			String research, Skill skillUsed, int skillXp, float vanillaXp,
			int dirtyProgressAmount, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_KITCHEN_BENCH.addRecipe(new KitchenBenchShapelessRecipe(output, inputs,
				toolTier, kitchenBenchTier, craftTime, toolType, soundOfCraft, research, skillUsed, skillXp, vanillaXp,
				dirtyProgressAmount), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a Quern Recipe with all variables
	 *
	 * @param inputs			Input Ingredients
	 * @param inputPots			Input Pot Ingredients (what type of pot should be in the pot slot)
	 * @param output			Output ItemStack
	 * @param consumePot		Should the ItemStack in the pot slot be consumed
	 * @param requiredResearch  The required research for this recipe
	 * @param skill				The Skill to grant xp to
	 * @param skillXp			The amount of xp to give to Skill
	 * @param vanillaXp			The amount of vanilla xp to give
	 * @param modId				The modId for this recipe to registered under
	 * @param name				The name of this recipe
	 */
	public static void addQuernRecipe(NonNullList<Ingredient> inputs, NonNullList<Ingredient> inputPots,
			ItemStack output, boolean consumePot, String requiredResearch, Skill skill, int skillXp, float vanillaXp,
			String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_QUERN.addRecipe(new QuernRecipeBase(output, inputs, inputPots, consumePot,
				requiredResearch, skill, skillXp, vanillaXp), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a Roast Recipe with all variables
	 *
	 * @param output 			Cooked output ItemStack
	 * @param inputs			Input Ingredients
	 * @param burntOutput		Burnt output ItemStack
	 * @param minTemp			Minimum heat temperature
	 * @param maxTemp			Maximum heat temperature
	 * @param cookTime			How long it takes to cook this recipe
	 * @param burnTime			How long till the recipe burns and outputs burntOutput
	 * @param canBurn			Can this recipe burn?
	 * @param isOvenRecipe		Is this an oven recipe (true) or a stovetop recipe (false)
	 * @param requiredResearch  The required research for this recipe
	 * @param skill 			The Skill to grant xp to
	 * @param skillXp           The amount of xp to give to Skill
	 * @param vanillaXp         The amount of vanilla xp to give
	 * @param modId             The modId for this recipe to registered under
	 * @param name				The name of this recipe
	 */
	public static void addRoastRecipe(ItemStack output, NonNullList<Ingredient> inputs, ItemStack burntOutput, int minTemp, int maxTemp,
			int cookTime, int burnTime, boolean canBurn, boolean isOvenRecipe,
			String requiredResearch, Skill skill, int skillXp, float vanillaXp, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_ROAST.addRecipe(new RoastRecipeBase(output, inputs, burntOutput, minTemp, maxTemp,
				cookTime, burnTime, canBurn, isOvenRecipe,
				requiredResearch, skill, skillXp, vanillaXp), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a Salvage Standard Recipe with all variables
	 *
	 * @param input					The Input ItemStack to be salvaged
	 * @param outputs				The Output Ingredients that result from the salvage
	 * @param requiredResearch		The required research for this recipe
	 * @param skill 				The Skill to grant xp to
	 * @param skillXp           	The amount of xp to give to Skill
	 * @param vanillaXp         	The amount of vanilla xp to give
	 * @param modId             	The modId for this recipe to registered under
	 * @param name					The name of this recipe
	 */
	public static void addSalvageRecipeStandard(ItemStack input, NonNullList<Ingredient> outputs,
			String requiredResearch, Skill skill, int skillXp, float vanillaXp, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_SALVAGE.addRecipe(new SalvageRecipeStandard(input, outputs, requiredResearch,
				skill, skillXp, vanillaXp), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a Salvage Shared Recipe with all variables
	 * Shared Recipes have multiple possible inputs that result in the same output
	 *
	 * @param input					The Input ItemStack to be salvaged
	 * @param outputs				The Output Ingredients that result from the salvage
	 * @param shared 				The List of ItemStacks that the recipe should be shared with (standard to dragonforged, ornate, etc.
	 * @param requiredResearch		The required research for this recipe
	 * @param skill 				The Skill to grant xp to
	 * @param skillXp           	The amount of xp to give to Skill
	 * @param vanillaXp         	The amount of vanilla xp to give
	 * @param modId             	The modId for this recipe to registered under
	 * @param name					The name of this recipe
	 */
	public static void addSalvageRecipeShared(ItemStack input, NonNullList<Ingredient> outputs, NonNullList<ItemStack> shared,
			String requiredResearch, Skill skill, int skillXp, float vanillaXp, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_SALVAGE.addRecipe(new SalvageRecipeShared(input, outputs, shared,
				requiredResearch, skill, skillXp, vanillaXp), true, new ResourceLocation(modId, name));
	}

	/**
	 *  Adds a Special Design Recipe with all variables
	 *  Currently its only Ornate and Dragonforged
	 *
	 * @param input				The input Ingredient
	 * @param specialInput		The Required Special input Ingredient for the recipe (dragon heart, ornate crest, etc.)
	 * @param output			The Output ItemStack
	 * @param research			The required research for this recipe
	 * @param design			The Design of the output item, Ornate or Dragonforged
	 * @param modId             The modId for this recipe to registered under
	 * @param name				The name of this recipe
	 */
	public static void addSpecialRecipe(Ingredient input, Ingredient specialInput, ItemStack output,
			String research, String design, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_SPECIAL.addRecipe(new SpecialRecipeBase(input, specialInput, output,
				research, design), true, new ResourceLocation(modId, name));
	}

	/**
	 *  Adds a Tanner Recipe with all variables
	 *
	 * @param output				The Output ItemStack
	 * @param inputs				The Input Ingredients
	 * @param toolType				The type of the tool required for this recipe
	 * @param tannerTier			The required tier of the tanner
	 * @param craftTime				How long should this recipe take to make
	 * @param requiredResearch  	The required research for this recipe
	 * @param skill 				The Skill to grant xp to
	 * @param skillXp           	The amount of xp to give to Skill
	 * @param vanillaXp         	The amount of vanilla xp to give
	 * @param modId             	The modId for this recipe to registered under
	 * @param name					The name of this recipe
	 */
	public static void addTannerRecipe(ItemStack output, NonNullList<Ingredient> inputs, String toolType, int tannerTier,
			int craftTime, String requiredResearch, Skill skill, int skillXp, float vanillaXp, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_TANNER.addRecipe(new TannerRecipeBase(output, inputs, toolType, tannerTier,
				craftTime, requiredResearch, skill, skillXp, vanillaXp), true, new ResourceLocation(modId, name));
	}

	/**
	 *  Adds a standard Transformation Recipe with all variables
	 *
	 * @param tool						The Tool that this Recipe is created with
	 * @param inputs					The Input Block Ingredients
	 * @param output					The output Block ItemStack
	 * @param consumableStacks			The List of ItemStacks in the player inventory that must be present, and will be consumed, for this recipe
	 * @param dropStack					The ItemStack dropped as a result of the block transformation
	 * @param offhandStack				The ItemStack that is required in the player's offhand
	 * @param skill 					The Skill to grant xp to
	 * @param research  				The required research for this recipe
	 * @param skillXp           		The amount of xp to give to Skill
	 * @param vanillaXp         		The amount of vanilla xp to give
	 * @param maxProgress				The amount of hits required to complete the recipe
	 * @param soundName					The Sound each hit will make
	 * @param blockStateProperties		The resulting Block's BlockState Properties
	 * @param modId             		The modId for this recipe to registered under
	 * @param name						The name of this recipe
	 */
	public static void addStandardTransformationRecipe(
			Tool tool, NonNullList<Ingredient> inputs, ItemStack output, List<ItemStack> consumableStacks, ItemStack dropStack,
			ItemStack offhandStack, Skill skill, String research, int skillXp, float vanillaXp, int maxProgress, String soundName,
			List<String> blockStateProperties, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_TRANSFORMATION.addRecipe(new TransformationRecipeStandard(tool, inputs, output,
				consumableStacks, dropStack, offhandStack, skill, research, skillXp, vanillaXp, maxProgress, soundName,
				blockStateProperties), true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds a BlockState Transformation Recipe with all variables
	 *
	 * @param input						The Input BlockState for the block to be transformed
	 * @param output					The Output BlockState for the Input block to be transformed into
	 * @param tool						The Tool that this Recipe is created with
	 * @param consumableStacks			The List of ItemStacks in the player inventory that must be present, and will be consumed, for this recipe
	 * @param dropStack					The ItemStack dropped as a result of the block transformation
	 * @param offhandStack				The ItemStack that is required in the player's offhand
	 * @param skill 					The Skill to grant xp to
	 * @param research  				The required research for this recipe
	 * @param skillXp           		The amount of xp to give to Skill
	 * @param vanillaXp         		The amount of vanilla xp to give
	 * @param progressMax				The amount of hits required to complete the recipe
	 * @param soundName					The Sound each hit will make
	 * @param modId             		The modId for this recipe to registered under
	 * @param name						The name of this recipe
	 */
	public static void addBlockStateTransformationRecipe(
			IBlockState input, IBlockState output, Tool tool, List<ItemStack> consumableStacks, ItemStack dropStack,
			ItemStack offhandStack, Skill skill, String research, int skillXp, float vanillaXp, int progressMax,
			String soundName, String modId, String name) {
		MineFantasyReforged.CRAFTING_MANAGER_TRANSFORMATION.addRecipe(new TransformationRecipeBlockState(input, output, tool,
				consumableStacks, dropStack, offhandStack, skill, research, skillXp, vanillaXp, progressMax, soundName),
				true, new ResourceLocation(modId, name));
	}

	/**
	 * Adds an item as a valid forge & big furnace fuel.
	 * It is recommended you add forge fuels via JSON files, but this exists to bypass that method
	 *
	 * @param item 		input ItemStack
	 * @param duration 	how much time the fuel will burn for
	 * @param heat		what temperature the fuel will burn at
	 * @param willLight	if the fuel will light the forge when added
	 * @param refined	if the fuel counts as refined
	 */
	public static void addForgeFuel(ItemStack item, int duration, int heat, boolean willLight, boolean refined) {
		ArrayList<Object> key = new ArrayList<>();
		key.add(item.getItem());
		key.add(item.getMetadata());
		ForgeFuel fuel = new ForgeFuel(item, duration, heat, willLight, refined);

		ForgeFuelRegistry.forgeFuelRegistry.put(key, fuel);
	}

	/**
	 * Adds an item as a valid forge & big furnace fuel.
	 * It is recommended you add firepit fuels via JSON files, but this exists to bypass that method
	 *
	 * @param item
	 * @param duration
	 * @param heat
	 * @param willLight
	 */
	public static void addFirepitFuel(ItemStack item, int duration, int heat, boolean willLight) {
		ArrayList<Object> key = new ArrayList<>();
		key.add(item.getItem());
		key.add(item.getMetadata());
		FirepitFuel fuel = new FirepitFuel(item, duration, heat, willLight);

		FirepitFuelRegistry.firepitFuelRegistry.put(key, fuel);
	}

	public static void registerFuelHandler(IFuelHandler handler) {
		fuelHandlers.add(handler);
	}

	public static int getFuelValue(ItemStack itemStack) {
		int fuelValue = 0;
		for (IFuelHandler handler : fuelHandlers) {
			fuelValue = Math.max(fuelValue, handler.getBurnTime(itemStack));
		}
		return fuelValue;
	}

	/**
	 * Allows an item to be heated
	 *
	 * @param item     the item to heat
	 * @param min      the minimum heat to forge with(celcius)
	 * @param max      the maximum heat until the item is ruined(celcius)
	 * @param unstable when the ingot is unstable(celcius)
	 */
	public static void setHeatableStats(ItemStack item, int min, int unstable, int max) {
		Heatable.addItem(item, min, unstable, max);
	}

	/**
	 * Allows an item to be heated ignoring subId
	 *
	 * @param id       the item to heat
	 * @param min      the minimum heat to forge with(celcius)
	 * @param max      the maximum heat until the item is ruined(celcius)
	 * @param unstable when the ingot is unstable(celcius)
	 */
	public static void setHeatableStats(Item id, int min, int unstable, int max) {
		Heatable.addItem(new ItemStack(id, 1, OreDictionary.WILDCARD_VALUE), min, unstable, max);
	}

	public static void setHeatableStats(String oredict, int min, int unstable, int max) {
		for (ItemStack item : OreDictionary.getOres(oredict)) {
			setHeatableStats(item, min, unstable, max);
		}
	}

	/**
	 * Adds a crossbow part Make sure to do this when adding the item
	 */
	public static void registerCrossbowPart(ICrossbowPart part) {
		ICrossbowPart.components.put(part.getComponentType() + part.getID(), part);
	}

	/**
	 * For Hardcore Crafting: Can remove smelting recipes
	 *
	 * @param input can be an "Item", "Block" or "ItemStack"
	 */
	public static boolean removeSmelting(Object input) {
		ItemStack object = ItemStack.EMPTY;
		if (input instanceof Item) {
			object = new ItemStack((Item) input, 1, 32767);
		} else if (input instanceof Block) {
			object = new ItemStack((Block) input, 1, 32767);
		} else if (input instanceof ItemStack) {
			object = (ItemStack) input;
		}
		if (!object.isEmpty()) {
			return removeFurnaceInput(object);
		}
		return false;
	}

	private static boolean removeFurnaceInput(ItemStack input) {
		Iterator iterator = FurnaceRecipes.instance().getSmeltingList().entrySet().iterator();
		Entry entry;

		do {
			if (!iterator.hasNext()) {
				return false;
			}

			entry = (Entry) iterator.next();
		} while (!checkMatch(input, (ItemStack) entry.getKey()));

		FurnaceRecipes.instance().getSmeltingList().remove(entry.getKey());
		return true;
	}

	private static boolean checkMatch(ItemStack item1, ItemStack item2) {
		return item2.getItem() == item1.getItem() && (item2.getItemDamage() == 32767 || item2.getItemDamage() == item1.getItemDamage());
	}
}
