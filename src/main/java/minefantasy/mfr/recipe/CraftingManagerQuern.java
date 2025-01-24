package minefantasy.mfr.recipe;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.factories.QuernRecipeFactory;
import minefantasy.mfr.recipe.types.QuernRecipeType;
import minefantasy.mfr.recipe.types.RecipeType;
import minefantasy.mfr.util.CustomToolHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CraftingManagerQuern extends CraftingManagerBase<QuernRecipeBase> {

	private static final IForgeRegistry<QuernRecipeBase> QUERN_RECIPES =
			new RegistryBuilder<QuernRecipeBase>()
			.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "quern_recipes"))
					.setType(QuernRecipeBase.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();
	private static final Set<String> QUERN_RESEARCHES = new HashSet<>();

	public CraftingManagerQuern() {
		super(new QuernRecipeFactory(),
				QuernRecipeType.NONE,
				Constants.ASSET_DIRECTORY + "/recipes_mfr/quern_recipes/",
				"config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/quern_recipes/");
	}

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<QuernRecipeBase> getRecipes() {
		return QUERN_RECIPES.getValuesCollection();
	}

	public void addRecipe(QuernRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		ItemStack itemStack = recipe.getQuernRecipeOutput();
		if (ConfigCrafting.isQuernRecipeEnabled(key) && !BlockedRecipeManager.isRecipeBlocked(RecipeType.QUERN_RECIPES, key)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			recipe.setRegistryName(key);
			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getQuernRecipeOutput().isItemEqual(s))
					&& (!checkForExistence || !QUERN_RECIPES.containsKey(recipe.getRegistryName()))) {
				QUERN_RECIPES.register(recipe);
				String requiredResearch = recipe.getRequiredResearch();
				if (!requiredResearch.equals("none")) {
					QUERN_RESEARCHES.add(requiredResearch);
				}
			}
		}
	}

	public static QuernRecipeBase findMatchingRecipe(ItemStack input, ItemStack potInput, Set<String> knownResearches) {
		//// Normal, registered recipes.

		for (QuernRecipeBase rec : getRecipes()) {
			if (rec.matches(input, potInput)) {
				if (rec.getRequiredResearch().equals("none")
						|| knownResearches.contains(rec.getRequiredResearch())) {
					return rec;
				}
			}
		}
		return null;
	}

	public static boolean findMatchingInputs(ItemStack input, Set<String> knownResearches) {
		for (QuernRecipeBase rec : getRecipes()) {
			if (rec.inputMatches(input)) {
				if (rec.getRequiredResearch().equals("none")
						|| knownResearches.contains(rec.getRequiredResearch())) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean findMatchingPotInputs(ItemStack potInputs, Set<String> knownResearches) {
		for (QuernRecipeBase rec : getRecipes()) {
			if (rec.inputPotMatches(potInputs)) {
				if (rec.getRequiredResearch().equals("none")
						|| knownResearches.contains(rec.getRequiredResearch())) {
					return true;
				}
			}
		}
		return false;
	}

	public static QuernRecipeBase findRecipeByOutput(ItemStack output) {
		for (QuernRecipeBase recipe : getRecipes()) {
			if (CustomToolHelper.areEqual(recipe.getQuernRecipeOutput(), output)) {
				return recipe;
			}
		}
		return null;
	}

	public static QuernRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!QUERN_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Quern Recipe Registry does not contain recipe: {}", name);
		}
		return QUERN_RECIPES.getValue(resourceLocation);
	}

	public static List<QuernRecipeBase> getRecipesByName(String modId, String... names) {
		List<QuernRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static QuernRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return QUERN_RECIPES.getValue(resourceLocation);
	}

	public static Set<String> getQuernResearches() {
		return QUERN_RESEARCHES;
	}
}
