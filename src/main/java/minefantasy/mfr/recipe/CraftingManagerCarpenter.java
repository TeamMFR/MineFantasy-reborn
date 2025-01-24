package minefantasy.mfr.recipe;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.factories.CarpenterRecipeFactory;
import minefantasy.mfr.recipe.types.CarpenterRecipeType;
import minefantasy.mfr.recipe.types.RecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CraftingManagerCarpenter extends CraftingManagerBase<CarpenterRecipeBase> {

	private static final IForgeRegistry<CarpenterRecipeBase> CARPENTER_RECIPES =
			new RegistryBuilder<CarpenterRecipeBase>()
					.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "carpenter_recipes"))
					.setType(CarpenterRecipeBase.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();

	public CraftingManagerCarpenter() {
		super(new CarpenterRecipeFactory(),
				CarpenterRecipeType.NONE,
				Constants.ASSET_DIRECTORY + "/recipes_mfr/carpenter_recipes/",
				"config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/carpenter_recipes/");
	}

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<CarpenterRecipeBase> getRecipes() {
		return CARPENTER_RECIPES.getValuesCollection();
	}

	public void addRecipe(CarpenterRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		ItemStack itemStack = recipe.getCarpenterRecipeOutput();
		if (ConfigCrafting.isCarpenterRecipeEnabled(key) && !BlockedRecipeManager.isRecipeBlocked(RecipeType.CARPENTER_RECIPES, key)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			recipe.setRegistryName(key);
			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getCarpenterRecipeOutput().isItemEqual(s))
					&& (!checkForExistence || !CARPENTER_RECIPES.containsKey(recipe.getRegistryName()))) {
				CARPENTER_RECIPES.register(recipe);
			}
		}
	}

	public static CarpenterRecipeBase findMatchingRecipe(ICarpenter carpenter, CarpenterCraftMatrix matrix, World world) {
		int time;
		int carpenterTier;
		int toolTier;

		Iterator<CarpenterRecipeBase> recipeIterator = getRecipes().iterator();
		CarpenterRecipeBase carpenterRecipeBase = null;

		while (recipeIterator.hasNext()) {
			CarpenterRecipeBase rec = recipeIterator.next();

			if (rec.matches(matrix, world)) {
				carpenterRecipeBase = rec;
				break;
			}
		}

		if (carpenterRecipeBase != null) {
			time = carpenterRecipeBase.getCraftTime();
			toolTier = carpenterRecipeBase.getToolTier();
			carpenterTier = carpenterRecipeBase.getCarpenterTier();

			if (!carpenterRecipeBase.useCustomTiers()) {
				carpenter.setProgressMax(time);
				carpenter.setRequiredToolTier(toolTier);
				carpenter.setRequiredCarpenterTier(carpenterTier);
			}

			return carpenterRecipeBase;
		}
		return null;
	}

	public static CarpenterRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!CARPENTER_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Carpenter Recipe Registry does not contain recipe: {}", name);
		}
		return CARPENTER_RECIPES.getValue(resourceLocation);
	}

	public static List<CarpenterRecipeBase> getRecipesByName(String modId, String... names) {
		List<CarpenterRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static CarpenterRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return CARPENTER_RECIPES.getValue(resourceLocation);
	}
}
