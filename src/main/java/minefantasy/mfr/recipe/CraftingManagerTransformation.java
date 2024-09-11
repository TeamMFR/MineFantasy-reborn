package minefantasy.mfr.recipe;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.factories.TransformationRecipeFactory;
import minefantasy.mfr.recipe.types.TransformationRecipeType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CraftingManagerTransformation extends CraftingManagerBase<TransformationRecipeBase> {

	private static final IForgeRegistry<TransformationRecipeBase> TRANSFORMATION_RECIPES =
			new RegistryBuilder<TransformationRecipeBase>()
					.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "transformation_recipes"))
					.setType(TransformationRecipeBase.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();

	public CraftingManagerTransformation() {
		super(new TransformationRecipeFactory(),
				TransformationRecipeType.NONE,
				Constants.ASSET_DIRECTORY + "/recipes_mfr/transformation_recipes/",
				"config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/transformation_recipes/");
	}

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<TransformationRecipeBase> getRecipes() {
		return TRANSFORMATION_RECIPES.getValuesCollection();
	}

	public void addRecipe(TransformationRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		recipe.setRegistryName(key);
		if (recipe instanceof TransformationRecipeStandard) {
			addStandardRecipe((TransformationRecipeStandard) recipe, checkForExistence);
		}
		else if (recipe instanceof TransformationRecipeBlockState) {
			addBlockStateRecipe((TransformationRecipeBlockState) recipe, checkForExistence);
		}
	}

	private static void addBlockStateRecipe(TransformationRecipeBlockState recipe, boolean checkForExistence) {
		IBlockState state = recipe.getOutput();
		if (ConfigCrafting.isBlockStateTransformable(state)) {
			if (!checkForExistence || !TRANSFORMATION_RECIPES.containsKey(recipe.getRegistryName())) {
				TRANSFORMATION_RECIPES.register(recipe);
			}

			List<IBlockState> states = state.getBlock().getBlockState().getValidStates();

			if (states.stream().anyMatch(s -> recipe.getOutput().equals(state))
					&& (!checkForExistence || !TRANSFORMATION_RECIPES.containsKey(recipe.getRegistryName()))) {
				TRANSFORMATION_RECIPES.register(recipe);
			}
		}
	}

	private static void addStandardRecipe(TransformationRecipeStandard recipe, boolean checkForExistence) {
		ItemStack itemStack = recipe.getOutput();
		if (ConfigCrafting.isItemTransformable(itemStack)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getOutput().isItemEqual(s))
					&& (!checkForExistence || !TRANSFORMATION_RECIPES.containsKey(recipe.getRegistryName()))) {
				TRANSFORMATION_RECIPES.register(recipe);
			}
		}
	}

	public static TransformationRecipeBase findMatchingRecipe(ItemStack tool, ItemStack input, IBlockState state) {
		//// Normal, registered recipes.

		for (TransformationRecipeBase rec : getRecipes()) {
			if (rec.matches(tool, input, state)) {
				return rec;
			}
		}
		return null;
	}

	public static TransformationRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!TRANSFORMATION_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Tanner Recipe Registry does not contain recipe: {}", name);
		}
		return TRANSFORMATION_RECIPES.getValue(resourceLocation);
	}

	public static List<TransformationRecipeBase> getRecipesByName(String modId, String... names) {
		List<TransformationRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static TransformationRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return TRANSFORMATION_RECIPES.getValue(resourceLocation);
	}
}
