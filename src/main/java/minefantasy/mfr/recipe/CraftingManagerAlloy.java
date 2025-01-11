package minefantasy.mfr.recipe;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.factories.AlloyRecipeFactory;
import minefantasy.mfr.recipe.types.AlloyRecipeType;
import minefantasy.mfr.tile.TileEntityCrucible;
import minefantasy.mfr.util.CustomToolHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CraftingManagerAlloy extends CraftingManagerBase<AlloyRecipeBase> {

	private static final IForgeRegistry<AlloyRecipeBase> ALLOY_RECIPES =
			new RegistryBuilder<AlloyRecipeBase>()
					.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "alloy_recipes"))
					.setType(AlloyRecipeBase.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();
	private static final Set<String> ALLOY_RESEARCHES = new HashSet<>();

	public CraftingManagerAlloy() {
		super(new AlloyRecipeFactory(),
				AlloyRecipeType.NONE,
				Constants.ASSET_DIRECTORY + "/recipes_mfr/alloy_recipes/",
				"config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/alloy_recipes/");
	}

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<AlloyRecipeBase> getRecipes() {
		return ALLOY_RECIPES.getValuesCollection();
	}

	public void addRecipe(AlloyRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		ItemStack itemStack = recipe.getAlloyRecipeOutput();
		if (ConfigCrafting.isAlloyRecipeEnabled(key)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			recipe.setRegistryName(key);
			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getAlloyRecipeOutput().isItemEqual(s))
					&& (!checkForExistence || !ALLOY_RECIPES.containsKey(recipe.getRegistryName()))) {
				ALLOY_RECIPES.register(recipe);
				String requiredResearch = recipe.getRequiredResearch();
				if (!requiredResearch.equals("none")) {
					ALLOY_RESEARCHES.add(requiredResearch);
				}
			}
		}
	}

	public static AlloyRecipeBase findMatchingRecipe(
			TileEntityCrucible crucible,
			CrucibleCraftMatrix matrix,
			Set<String> knownResearches) {
		//// Normal, registered recipes.
		Iterator<AlloyRecipeBase> recipeIterator = getRecipes().iterator();
		AlloyRecipeBase alloyRecipeBase = null;

		while (recipeIterator.hasNext()) {
			AlloyRecipeBase rec = recipeIterator.next();

			if (rec.matches(matrix)) {
				alloyRecipeBase = rec;
				break;
			}
		}

		if (alloyRecipeBase != null) {
			if (alloyRecipeBase.getTier() <= crucible.getTier()) {
				if (alloyRecipeBase.getRequiredResearch().equals("none")
						|| knownResearches.contains(alloyRecipeBase.getRequiredResearch())) {
					return alloyRecipeBase;
				}
			}
		}
		return null;
	}

	public static AlloyRecipeBase findRecipeByOutput(ItemStack output) {
		for (AlloyRecipeBase recipe : getRecipes()) {
			if (CustomToolHelper.areEqual(recipe.getAlloyRecipeOutput(), output)) {
				return recipe;
			}
		}
		return null;
	}

	public static AlloyRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!ALLOY_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Alloy Recipe Registry does not contain recipe: {}", name);
		}
		return ALLOY_RECIPES.getValue(resourceLocation);
	}

	public static List<AlloyRecipeBase> getRecipesByName(String modId, String... names) {
		List<AlloyRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static AlloyRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return ALLOY_RECIPES.getValue(resourceLocation);
	}

	public static Set<String> getAlloyResearches() {
		return ALLOY_RESEARCHES;
	}
}
