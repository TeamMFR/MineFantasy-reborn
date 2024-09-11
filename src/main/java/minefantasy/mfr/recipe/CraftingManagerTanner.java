package minefantasy.mfr.recipe;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.factories.TannerRecipeFactory;
import minefantasy.mfr.recipe.types.TannerRecipeType;
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

public class CraftingManagerTanner extends CraftingManagerBase<TannerRecipeBase> {

	private static final IForgeRegistry<TannerRecipeBase> TANNER_RECIPES =
			new RegistryBuilder<TannerRecipeBase>()
					.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "tanner_recipes"))
					.setType(TannerRecipeBase.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();
	private static final Set<String> TANNER_RESEARCHES = new HashSet<>();

	public CraftingManagerTanner() {
		super(new TannerRecipeFactory(),
				TannerRecipeType.NONE,
				Constants.ASSET_DIRECTORY +  "/recipes_mfr/tanner_recipes/",
				"config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/tanner_recipes/");
	}

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<TannerRecipeBase> getRecipes() {
		return TANNER_RECIPES.getValuesCollection();
	}

	public void addRecipe(TannerRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		ItemStack itemStack = recipe.getTannerRecipeOutput();
		if (ConfigCrafting.isTannerItemCraftable(itemStack)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			recipe.setRegistryName(key);
			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getTannerRecipeOutput().isItemEqual(s))
					&& (!checkForExistence || !TANNER_RECIPES.containsKey(recipe.getRegistryName()))) {
				TANNER_RECIPES.register(recipe);
				TANNER_RESEARCHES.add(recipe.getRequiredResearch());
			}
		}
	}

	public static TannerRecipeBase findMatchingRecipe(ItemStack input, Set<String> knownResearches) {
		//// Normal, registered recipes.

		for (TannerRecipeBase rec : getRecipes()) {
			if (rec.matches(input)) {
				if (rec.getRequiredResearch().equals("none")
						|| knownResearches.contains(rec.getRequiredResearch())) {
					return rec;
				}
			}
		}
		return null;
	}

	public static TannerRecipeBase findRecipeByOutput(ItemStack output) {
		for (TannerRecipeBase recipe : getRecipes()) {
			if (CustomToolHelper.areEqual(recipe.getTannerRecipeOutput(), output)) {
				return recipe;
			}
		}
		return null;
	}

	public static TannerRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!TANNER_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Tanner Recipe Registry does not contain recipe: {}", name);
		}
		return TANNER_RECIPES.getValue(resourceLocation);
	}

	public static List<TannerRecipeBase> getRecipesByName(String modId, String... names) {
		List<TannerRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static TannerRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return TANNER_RECIPES.getValue(resourceLocation);
	}

	public static Set<String> getTannerResearches() {
		return TANNER_RESEARCHES;
	}
}
