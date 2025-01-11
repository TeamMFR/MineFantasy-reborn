package minefantasy.mfr.recipe;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.factories.BloomeryRecipeFactory;
import minefantasy.mfr.recipe.types.BloomeryRecipeType;
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

public class CraftingManagerBloomery extends CraftingManagerBase<BloomeryRecipeBase> {

	private static final IForgeRegistry<BloomeryRecipeBase> BLOOMERY_RECIPES =
			new RegistryBuilder<BloomeryRecipeBase>()
					.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "bloomery_recipes"))
					.setType(BloomeryRecipeBase.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();
	private static final Set<String> BLOOMERY_RESEARCHES = new HashSet<>();

	public CraftingManagerBloomery() {
		super(new BloomeryRecipeFactory(),
				BloomeryRecipeType.NONE,
				Constants.ASSET_DIRECTORY + "/recipes_mfr/bloomery_recipes/",
				"config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/bloomery_recipes/");
	}

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<BloomeryRecipeBase> getRecipes() {
		return BLOOMERY_RECIPES.getValuesCollection();
	}

	public void addRecipe(BloomeryRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		ItemStack itemStack = recipe.getBloomeryRecipeOutput();
		if (ConfigCrafting.isBloomeryRecipeEnabled(key)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			recipe.setRegistryName(key);
			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getBloomeryRecipeOutput().isItemEqual(s))
					&& (!checkForExistence || !BLOOMERY_RECIPES.containsKey(recipe.getRegistryName()))) {
				BLOOMERY_RECIPES.register(recipe);
				String requiredResearch = recipe.getRequiredResearch();
				if (!requiredResearch.equals("none")) {
					BLOOMERY_RESEARCHES.add(requiredResearch);
				}
			}
		}
	}

	public static BloomeryRecipeBase findMatchingRecipe(ItemStack input, Set<String> knownResearches) {
		//// Normal, registered recipes.

		for (BloomeryRecipeBase rec : getRecipes()) {
			if (rec.matches(input)) {
				if (rec.getRequiredResearch().equals("none")
						|| knownResearches.contains(rec.getRequiredResearch())) {
					return rec;
				}
			}
		}
		return null;
	}

	public static BloomeryRecipeBase findRecipeByOutput(ItemStack output) {
		for (BloomeryRecipeBase recipe : getRecipes()) {
			if (CustomToolHelper.areEqual(recipe.getBloomeryRecipeOutput(), output)) {
				return recipe;
			}
		}
		return null;
	}

	public static BloomeryRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!BLOOMERY_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Bloomery Recipe Registry does not contain recipe: {}", name);
		}
		return BLOOMERY_RECIPES.getValue(resourceLocation);
	}

	public static List<BloomeryRecipeBase> getRecipesByName(String modId, String... names) {
		List<BloomeryRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static BloomeryRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return BLOOMERY_RECIPES.getValue(resourceLocation);
	}

	public static Set<String> getBloomeryResearches() {
		return BLOOMERY_RESEARCHES;
	}
}
