package minefantasy.mfr.recipe;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.factories.AnvilRecipeFactory;
import minefantasy.mfr.recipe.types.AnvilRecipeType;
import minefantasy.mfr.recipe.types.RecipeType;
import minefantasy.mfr.tile.TileEntityAnvil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CraftingManagerAnvil extends CraftingManagerBase<AnvilRecipeBase> {

	private static final IForgeRegistry<AnvilRecipeBase> ANVIL_RECIPES =
			new RegistryBuilder<AnvilRecipeBase>()
					.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "anvil_recipes"))
					.setType(AnvilRecipeBase.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();

	public CraftingManagerAnvil() {
		super(new AnvilRecipeFactory(),
				AnvilRecipeType.NONE,
				Constants.ASSET_DIRECTORY + "/recipes_mfr/anvil_recipes/",
				"config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/anvil_recipes/");
	}

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<AnvilRecipeBase> getRecipes() {
		return ANVIL_RECIPES.getValuesCollection();
	}

	@Override
	public void addRecipe(AnvilRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		ItemStack itemStack = recipe.getAnvilRecipeOutput();
		if (ConfigCrafting.isAnvilRecipeEnabled(key) && !BlockedRecipeManager.isRecipeBlocked(RecipeType.ANVIL_RECIPES, key)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			recipe.setRegistryName(key);
			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getAnvilRecipeOutput().isItemEqual(s))
					&& (!checkForExistence || !ANVIL_RECIPES.containsKey(recipe.getRegistryName()))) {
				ANVIL_RECIPES.register(recipe);
			}
		}

	}

	public static AnvilRecipeBase findMatchingRecipe(TileEntityAnvil anvil, AnvilCraftMatrix matrix, World world) {
		int time;
		int anvilTier;
		int toolTier;

		//// Normal, registered recipes.
		Iterator<AnvilRecipeBase> recipeIterator = getRecipes().iterator();
		AnvilRecipeBase anvilRecipeBase = null;

		while (recipeIterator.hasNext()) {
			AnvilRecipeBase rec = recipeIterator.next();

			if (rec.matches(matrix, world)) {
				anvilRecipeBase = rec;
				break;
			}
		}

		if (anvilRecipeBase != null) {
			time = anvilRecipeBase.getCraftTime();
			toolTier = anvilRecipeBase.getToolTier();
			anvilTier = anvilRecipeBase.getAnvilTier();

			if (!anvilRecipeBase.useCustomTiers()){
				anvil.setProgressMax(time);
				anvil.setRequiredToolTier(toolTier);
				anvil.setRequiredAnvilTier(anvilTier);
			}

			if (!anvilRecipeBase.getRequiredResearch().equalsIgnoreCase("tier")){
				anvil.setRequiredResearch(anvilRecipeBase.getRequiredResearch());
			}

			return anvilRecipeBase;
		}
		return null;
	}

	public static AnvilRecipeBase findRecipeByOutput(Ingredient output) {
		for (AnvilRecipeBase anvilRecipe : getRecipes()) {
			if (output.apply(anvilRecipe.getAnvilRecipeOutput())) {
				return anvilRecipe;
			}
		}
		return null;
	}

	public static AnvilRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!ANVIL_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Anvil Recipe Registry does not contain recipe: {}", name);
		}
		return ANVIL_RECIPES.getValue(resourceLocation);
	}

	public static List<AnvilRecipeBase> getRecipesByName(String modId, String... names) {
		List<AnvilRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static AnvilRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return ANVIL_RECIPES.getValue(resourceLocation);
	}
}
