package minefantasy.mfr.recipe;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.factories.KitchenBenchRecipeFactory;
import minefantasy.mfr.recipe.types.KitchenBenchRecipeType;
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

public class CraftingManagerKitchenBench extends CraftingManagerBase<KitchenBenchRecipeBase> {

	private static final IForgeRegistry<KitchenBenchRecipeBase> KITCHEN_BENCH_RECIPES =
			new RegistryBuilder<KitchenBenchRecipeBase>()
					.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "kitchen_bench_recipes"))
					.setType(KitchenBenchRecipeBase.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();

	public CraftingManagerKitchenBench() {
		super(new KitchenBenchRecipeFactory(),
				KitchenBenchRecipeType.NONE,
				Constants.ASSET_DIRECTORY + "/recipes_mfr/kitchen_bench_recipes/",
				"config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/kitchen_bench_recipes/");
	}

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<KitchenBenchRecipeBase> getRecipes() {
		return KITCHEN_BENCH_RECIPES.getValuesCollection();
	}

	public void addRecipe(KitchenBenchRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		ItemStack itemStack = recipe.getKitchenBenchRecipeOutput();
		if (ConfigCrafting.isKitchenBenchRecipeEnabled(key) && !BlockedRecipeManager.isRecipeBlocked(RecipeType.KITCHEN_BENCH_RECIPES, key)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			recipe.setRegistryName(key);
			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getKitchenBenchRecipeOutput().isItemEqual(s))
					&& (!checkForExistence || !KITCHEN_BENCH_RECIPES.containsKey(recipe.getRegistryName()))) {
				KITCHEN_BENCH_RECIPES.register(recipe);
			}
		}
	}

	public static KitchenBenchRecipeBase findMatchingRecipe(IKitchenBench kitchenBench, KitchenBenchCraftMatrix matrix, World world) {
		Iterator<KitchenBenchRecipeBase> recipeIterator = getRecipes().iterator();
		KitchenBenchRecipeBase kitchenBenchRecipeBase = null;

		while (recipeIterator.hasNext()) {
			KitchenBenchRecipeBase rec = recipeIterator.next();

			if (rec.matches(matrix, world)) {
				kitchenBenchRecipeBase = rec;
				break;
			}
		}

		if (kitchenBenchRecipeBase != null) {
			kitchenBench.setProgressMax(kitchenBenchRecipeBase.getCraftTime());

			return kitchenBenchRecipeBase;
		}
		return null;
	}

	public static KitchenBenchRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!KITCHEN_BENCH_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Kitchen Bench Recipe Registry does not contain recipe: {}", name);
		}
		return KITCHEN_BENCH_RECIPES.getValue(resourceLocation);
	}

	public static List<KitchenBenchRecipeBase> getRecipesByName(String modId, String... names) {
		List<KitchenBenchRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static KitchenBenchRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return KITCHEN_BENCH_RECIPES.getValue(resourceLocation);
	}
}
