package minefantasy.mfr.api;

import com.google.common.collect.Lists;
import minefantasy.mfr.api.crafting.engineer.ICrossbowPart;
import minefantasy.mfr.api.heating.FirepitFuel;
import minefantasy.mfr.api.heating.ForgeFuel;
import minefantasy.mfr.api.heating.Heatable;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.recipe.AlloyRatioRecipe;
import minefantasy.mfr.recipe.AlloyShapedRecipe;
import minefantasy.mfr.recipe.AnvilShapedCustomMaterialRecipe;
import minefantasy.mfr.recipe.AnvilShapedRecipe;
import minefantasy.mfr.recipe.AnvilShapelessCustomMaterialRecipe;
import minefantasy.mfr.recipe.AnvilShapelessRecipe;
import minefantasy.mfr.recipe.BigFurnaceRecipeBase;
import minefantasy.mfr.recipe.BlastFurnaceRecipeBase;
import minefantasy.mfr.recipe.CraftingManagerAlloy;
import minefantasy.mfr.recipe.CraftingManagerAnvil;
import minefantasy.mfr.recipe.CraftingManagerBigFurnace;
import minefantasy.mfr.recipe.CraftingManagerBlastFurnace;
import minefantasy.mfr.recipe.CraftingManagerQuern;
import minefantasy.mfr.recipe.CraftingManagerRoast;
import minefantasy.mfr.recipe.QuernRecipeBase;
import minefantasy.mfr.recipe.RoastRecipeBase;
import minefantasy.mfr.registry.FirepitFuelRegistry;
import minefantasy.mfr.registry.ForgeFuelRegistry;
import minefantasy.mfr.util.MFRLogUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
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
	@Deprecated
	public static void addShapedAnvilRecipe(NonNullList<Ingredient> inputs, ItemStack output, Tool tool,
			int craftTime, int hammerTier, int anvilTier, boolean outputHot, String requiredResearch,
			Skill skill, int skillXp, float vanillaXp,
			int width, int height, String modId, String name) {
		CraftingManagerAnvil.addRecipe(new AnvilShapedRecipe(inputs, output, tool.getName(),
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
	@Deprecated
	public static void addShapelessAnvilRecipe(NonNullList<Ingredient> inputs, ItemStack output, Tool tool,
			int craftTime, int hammerTier, int anvilTier, boolean outputHot, String requiredResearch,
			Skill requiredSkill, int skillXp, float vanillaXp, String modId, String name) {
		CraftingManagerAnvil.addRecipe(new AnvilShapelessRecipe(inputs, output, tool.getName(),
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
	@Deprecated
	public static void addShapedCustomMaterialAnvilRecipe(NonNullList<Ingredient> inputs, ItemStack output, Tool tool,
			int craftTime, int hammerTier, int anvilTier, boolean outputHot, String requiredResearch, Skill requiredSkill,
			int skillXp, float vanillaXp,
			int width, int height, boolean tierModifyOutputCount, String modId, String name) {
		CraftingManagerAnvil.addRecipe(new AnvilShapedCustomMaterialRecipe(inputs, output, tool.getName(),
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
	@Deprecated
	public static void addShapelessCustomMaterialAnvilRecipe(NonNullList<Ingredient> inputs, ItemStack output, Tool tool,
			int craftTime, int hammerTier, int anvilTier, boolean outputHot, String requiredResearch,
			Skill requiredSkill, int skillXp, float vanillaXp,
			boolean tierModifyOutputCount, String modId, String name) {
		CraftingManagerAnvil.addRecipe(new AnvilShapelessCustomMaterialRecipe(inputs, output, tool.getName(),
				craftTime, hammerTier, anvilTier, outputHot, requiredResearch, requiredSkill, skillXp, vanillaXp,
				tierModifyOutputCount), true, new ResourceLocation(modId, name));
	}

	/**
	 * Add a Blast Furnace Recipe
	 * @param inputs			Input Ingredients
	 * @param output			Output ItemStack
	 * @param requiredResearch  The Required Research for this Recipe
	 * @param skill				The Skill of this recipe
	 * @param skillXp			The amount of Skill Xp to be granted to the Recipe Skill
	 * @param vanillaXp			The amount of vanilla Xp to be granted
	 * @param modId             The modId for this recipe to registered under
	 * @param name				The name of this recipe
	 */
	@Deprecated
	public static void addBlastFurnaceRecipe(NonNullList<Ingredient> inputs, ItemStack output,
			String requiredResearch, Skill skill, int skillXp, float vanillaXp, String modId, String name) {
		CraftingManagerBlastFurnace.addRecipe(new BlastFurnaceRecipeBase(output, inputs, requiredResearch,
				skill, skillXp, vanillaXp), true, new ResourceLocation(modId, name));
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
	@Deprecated
	public static void addAlloyRatioRecipe(ItemStack out, NonNullList<Ingredient> inputs, int tier,
			String requiredResearch, Skill skill, int skillXp, float vanillaXp,
			int repeatAmount, String modId, String name) {
		CraftingManagerAlloy.addRecipe(new AlloyRatioRecipe(out, inputs, tier,
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
	@Deprecated
	public static void addAlloyShapedRecipe(ItemStack out, NonNullList<Ingredient> inputs, int tier,
			String requiredResearch, Skill skill, int skillXp, float vanillaXp,
			int height, int width, String modId, String name) {
		CraftingManagerAlloy.addRecipe(new AlloyShapedRecipe(out, inputs, tier,
				requiredResearch, skill, skillXp, vanillaXp,
				height, width), true, new ResourceLocation(modId, name));
	}

	/**
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
	@Deprecated
	public static void addRoastRecipe(ItemStack output, NonNullList<Ingredient> inputs, ItemStack burntOutput, int minTemp, int maxTemp,
			int cookTime, int burnTime, boolean canBurn, boolean isOvenRecipe,
			String requiredResearch, Skill skill, int skillXp, float vanillaXp, String modId, String name) {
		CraftingManagerRoast.addRecipe(new RoastRecipeBase(output, inputs, burntOutput, minTemp, maxTemp,
				cookTime, burnTime, canBurn, isOvenRecipe,
				requiredResearch, skill, skillXp, vanillaXp), true, new ResourceLocation(modId, name));
	}

	@Deprecated
	public static void addQuernRecipe(NonNullList<Ingredient> inputs, NonNullList<Ingredient> inputPots,
			ItemStack output, boolean consumePot, String requiredResearch, Skill skill, int skillXp, float vanillaXp,
			String modId, String name) {
		CraftingManagerQuern.addRecipe(new QuernRecipeBase(output, inputs, inputPots, consumePot,
				requiredResearch, skill, skillXp, vanillaXp), true, new ResourceLocation(modId, name));
	}

	@Deprecated
	public static void addBigFurnaceRecipe(NonNullList<Ingredient> input, ItemStack output, int tier,
			String requiredResearch, Skill skill, int skillXp, float vanillaXp, String modId, String name) {
		CraftingManagerBigFurnace.addRecipe(new BigFurnaceRecipeBase(output, input, tier,
				requiredResearch, skill, skillXp, vanillaXp), true, new ResourceLocation(modId, name));
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
