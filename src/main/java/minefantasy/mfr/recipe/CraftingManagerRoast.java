package minefantasy.mfr.recipe;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.recipe.factories.RoastRecipeFactory;
import minefantasy.mfr.recipe.types.RoastRecipeType;
import minefantasy.mfr.util.CustomToolHelper;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CraftingManagerRoast extends CraftingManagerBase<RoastRecipeBase> {

	private static final IForgeRegistry<RoastRecipeBase> ROAST_RECIPES =
			new RegistryBuilder<RoastRecipeBase>()
					.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "roast_recipes"))
					.setType(RoastRecipeBase.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();
	private static final Set<String> ROAST_RESEARCHES = new HashSet<>();

	public CraftingManagerRoast() {
		super(new RoastRecipeFactory(),
				RoastRecipeType.NONE,
				Constants.ASSET_DIRECTORY + "/recipes_mfr/roast_recipes/",
				"config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/roast_recipes/");
	}

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<RoastRecipeBase> getRecipes() {
		return ROAST_RECIPES.getValuesCollection();
	}

	public void addRecipe(RoastRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		ItemStack itemStack = recipe.getRoastRecipeOutput();
		if (ConfigCrafting.isRoastRecipeEnabled(key)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			recipe.setRegistryName(key);
			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getRoastRecipeOutput().isItemEqual(s))
					&& (!checkForExistence || !ROAST_RECIPES.containsKey(recipe.getRegistryName()))) {
				ROAST_RECIPES.register(recipe);
				String requiredResearch = recipe.getRequiredResearch();
				if (!requiredResearch.equals("none")) {
					ROAST_RESEARCHES.add(requiredResearch);
				}
			}
		}
	}

	public static RoastRecipeBase findMatchingRecipe(ItemStack input, boolean isOven, Set<String> knownResearches) {
		//// Normal, registered recipes.

		for (RoastRecipeBase rec : getRecipes()) {
			if (rec.matches(input, isOven)) {
				if (rec.getRequiredResearch().equals("none")
						|| knownResearches.contains(rec.getRequiredResearch())) {
					return rec;
				}
			}
		}

		if (ConfigCrafting.canCookBasics && !isOven) {
			ItemStack output = FurnaceRecipes.instance().getSmeltingResult(input);
			if (!output.isEmpty() && output.getItem() instanceof ItemFood) {

				Ingredient ingredient = Ingredient.fromStacks(input);
				NonNullList<Ingredient> ingredients= NonNullList.create();
				ingredients.add(ingredient);

				RoastRecipeBase vanillaRecipe = new RoastRecipeBase(
						output, ingredients, new ItemStack(MineFantasyItems.BURNT_FOOD),
						100, 300, 20, 80,true, false,
						"none", Skill.PROVISIONING, 1, 0.3F);

				addToRegistry(vanillaRecipe, output);

				return vanillaRecipe;
			}
		}
		return null;
	}

	private static void addToRegistry(RoastRecipeBase vanillaRecipe, ItemStack output) {
		ForgeRegistry<RoastRecipeBase> registry = RegistryManager.ACTIVE.getRegistry(new ResourceLocation(MineFantasyReforged.MOD_ID, "roast_recipes"));
		registry.unfreeze();
		MineFantasyReforged.CRAFTING_MANAGER_ROAST.addRecipe(vanillaRecipe, true,
				new ResourceLocation(MineFantasyReforged.MOD_ID, output.getItem().getRegistryName().getPath()));
		registry.freeze();
	}

	public static RoastRecipeBase findRecipeByOutput(ItemStack output) {
		for (RoastRecipeBase recipe : getRecipes()) {
			if (CustomToolHelper.areEqual(recipe.getRoastRecipeOutput(), output)) {
				return recipe;
			}
		}
		return null;
	}

	public static RoastRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!ROAST_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Roast Recipe Registry does not contain recipe: {}", name);
		}
		return ROAST_RECIPES.getValue(resourceLocation);
	}

	public static List<RoastRecipeBase> getRecipesByName(String modId, String... names) {
		List<RoastRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static RoastRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return ROAST_RECIPES.getValue(resourceLocation);
	}

	public static Set<String> getRoastResearches() {
		return ROAST_RESEARCHES;
	}
}
