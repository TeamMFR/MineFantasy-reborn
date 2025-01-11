package minefantasy.mfr.recipe;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.factories.BigFurnaceRecipeFactory;
import minefantasy.mfr.recipe.types.BigFurnaceRecipeType;
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

public class CraftingManagerBigFurnace extends CraftingManagerBase<BigFurnaceRecipeBase> {

	private static final IForgeRegistry<BigFurnaceRecipeBase> BIG_FURNACE_RECIPES =
			new RegistryBuilder<BigFurnaceRecipeBase>()
					.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "big_furnace_recipes"))
					.setType(BigFurnaceRecipeBase.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();

	private static final Set<String> BIG_FURNACE_RESEARCHES = new HashSet<>();

	public CraftingManagerBigFurnace() {
		super(new BigFurnaceRecipeFactory(),
				BigFurnaceRecipeType.NONE,
				Constants.ASSET_DIRECTORY + "/recipes_mfr/big_furnace_recipes/",
				"config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/big_furnace_recipes/");
	}

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<BigFurnaceRecipeBase> getRecipes() {
		return BIG_FURNACE_RECIPES.getValuesCollection();
	}

	@Override
	public void addRecipe(BigFurnaceRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		ItemStack itemStack = recipe.getBigFurnaceRecipeOutput();
		if (ConfigCrafting.isBigFurnaceRecipeEnabled(key)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			recipe.setRegistryName(key);
			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getBigFurnaceRecipeOutput().isItemEqual(s))
					&& (!checkForExistence || !BIG_FURNACE_RECIPES.containsKey(recipe.getRegistryName()))) {
				BIG_FURNACE_RECIPES.register(recipe);
				String requiredResearch = recipe.getRequiredResearch();
				if (!requiredResearch.equals("none")) {
					BIG_FURNACE_RESEARCHES.add(requiredResearch);
				}
			}
		}
	}

	public static BigFurnaceRecipeBase findMatchingRecipe(ItemStack input, Set<String> knownResearches) {
		//// Normal, registered recipes.

		for (BigFurnaceRecipeBase rec : getRecipes()) {
			if (rec.matches(input)) {
				if (rec.getRequiredResearch().equals("none")
						|| knownResearches.contains(rec.getRequiredResearch())) {
					return rec;
				}
			}
		}
		return null;
	}

	public static BigFurnaceRecipeBase findRecipeByOutput(ItemStack output) {
		for (BigFurnaceRecipeBase recipe : getRecipes()) {
			if (CustomToolHelper.areEqual(recipe.getBigFurnaceRecipeOutput(), output)) {
				return recipe;
			}
		}
		return null;
	}

	public static BigFurnaceRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!BIG_FURNACE_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Big Furnace Recipe Registry does not contain recipe: {}", name);
		}
		return BIG_FURNACE_RECIPES.getValue(resourceLocation);
	}

	public static List<BigFurnaceRecipeBase> getRecipesByName(String modId, String... names) {
		List<BigFurnaceRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static BigFurnaceRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return BIG_FURNACE_RECIPES.getValue(resourceLocation);
	}

	public static Set<String> getBigFurnaceResearches() {
		return BIG_FURNACE_RESEARCHES;
	}
}
