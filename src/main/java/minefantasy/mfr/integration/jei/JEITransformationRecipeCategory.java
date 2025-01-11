package minefantasy.mfr.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.recipe.CraftingManagerTransformation;
import minefantasy.mfr.recipe.TransformationRecipeBase;
import minefantasy.mfr.recipe.TransformationRecipeBlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * JEI recipe category implementation for all "recipes" in the Transformation recipes.
 */
public class JEITransformationRecipeCategory implements IRecipeCategory<JEITransformationRecipe> {

	static final String UID = "minefantasyreforged:transformation";

	static final ResourceLocation TEXTURE = new ResourceLocation(MineFantasyReforged.MOD_ID, "textures/integration/jei/transformation_background.png");
	private final IDrawable icon;

	static final int WIDTH = 166;
	static final int HEIGHT = 58;

	private final IDrawable background;

	public JEITransformationRecipeCategory(IRecipeCategoryRegistration registry) {
		IGuiHelper iGuiHelper = registry.getJeiHelpers().getGuiHelper();
		background = iGuiHelper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
		icon = iGuiHelper.createDrawableIngredient(new ItemStack(MineFantasyItems.PAINT_BRUSH));
	}

	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return I18n.format("integration.jei.category." + UID);
	}

	@Override
	public String getModName() {
		return MineFantasyReforged.NAME;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	/**
	 * Set the {@link IRecipeLayout} properties from the {@link IRecipeWrapper} and {@link IIngredients}.
	 *
	 * @param recipeLayout  the layout that needs its properties set.
	 * @param recipeWrapper the recipeWrapper, for extra information.
	 * @param ingredients   the ingredients, already set by the recipeWrapper
	 * @since JEI 3.11.0
	 */
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, JEITransformationRecipe recipeWrapper, IIngredients ingredients) {

		// Okay, they're not technically *slots* but to all intents and purposes, that's how they behave
		IGuiItemStackGroup slots = recipeLayout.getItemStacks();

		List<ItemStack> inputs = recipeWrapper.getInputs();
		List<ItemStack> outputs = recipeWrapper.getOutputs();
		NonNullList<Ingredient> consumableStacks = recipeWrapper.getConsumableStacks();
		Ingredient offhandStack = recipeWrapper.getOffhandStack();
		Ingredient dropStack = recipeWrapper.getDropStack();

		// Init consumableStacks slots
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 3; y++) {
				int slot = y * 2 + x;
				slots.init(slot, true, 1 + x * 18, 2 + y * 18);
			}
		}

		// Init input slot
		slots.init(6, true, 66, 20);

		// Init output slots
		slots.init(7, false, 120, 20);

		// Init offhandStacks slot
		slots.init(8, true, 93, 1);

		// Init dropStack slot
		slots.init(9, true, 93, 38);

		//-----------------------------------------------------------//

		// Assign consumableStacks to slots
		for (int j = 0; j < consumableStacks.size(); j++) {
			slots.set(j, Arrays.asList(consumableStacks.get(j).getMatchingStacks()));
		}

		// Assign inputs to slot
		slots.set(6, inputs);

		// Assign outputs to slot
		slots.set(7, outputs);

		// Assign offhandStack to slot
		slots.set(8, Arrays.asList(offhandStack.getMatchingStacks()));

		// Assign dropStack to slot
		slots.set(9, Arrays.asList(dropStack.getMatchingStacks()));
	}

	/**
	 * Generates all the MFR transformation recipes for JEI.
	 */
	public static Collection<JEITransformationRecipe> generateRecipes() {
		return new ArrayList<>(generateTransformationRecipes());
	}

	@Nullable
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	private static Collection<JEITransformationRecipe> generateTransformationRecipes() {

		List<JEITransformationRecipe> recipes = new ArrayList<>();
		Collection<TransformationRecipeBase> transformationRecipes = CraftingManagerTransformation.getRecipes();

		for (TransformationRecipeBase transformationRecipe : transformationRecipes) {
			if (transformationRecipe instanceof TransformationRecipeBlockState) {
				IBlockState state = ((TransformationRecipeBlockState) transformationRecipe).getInput();
				if (!new ItemStack(state.getBlock()).isEmpty()) {
					recipes.add(new JEITransformationRecipe(transformationRecipe));
				}
			}
			else {
				recipes.add(new JEITransformationRecipe(transformationRecipe));
			}
		}

		return recipes;

	}
}
