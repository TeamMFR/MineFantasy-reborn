package minefantasy.mfr.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.config.Constants;
import mezz.jei.gui.GuiHelper;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.crafting.MineFantasyCarbons;
import minefantasy.mfr.init.MineFantasyBlocks;
import minefantasy.mfr.recipe.BloomeryRecipeBase;
import minefantasy.mfr.recipe.CraftingManagerBloomery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JEIBloomeryRecipeCategory implements IRecipeCategory<JEIBloomeryRecipe> {
	static final String UID = "minefantasyreforged:bloomery";

	static final ResourceLocation TEXTURE = new ResourceLocation(MineFantasyReforged.MOD_ID, "textures/integration/jei/bloomery_background.png");
	private final IDrawable icon;

	static final int WIDTH = 66;
	static final int HEIGHT = 53;

	private final IDrawable background;
	protected final IDrawableStatic staticFlame;
	protected final IDrawableAnimated animatedFlame;
	protected final IDrawableAnimated arrow;

	public JEIBloomeryRecipeCategory(IRecipeCategoryRegistration registry, GuiHelper guiHelper) {
		IGuiHelper iGuiHelper = registry.getJeiHelpers().getGuiHelper();
		background = iGuiHelper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
		icon = iGuiHelper.createDrawableIngredient(new ItemStack(MineFantasyBlocks.BLOOMERY));
		staticFlame = guiHelper.createDrawable(Constants.RECIPE_GUI_VANILLA, 82, 114, 14, 14);
		animatedFlame = guiHelper.createAnimatedDrawable(staticFlame, 300, IDrawableAnimated.StartDirection.TOP, true);

		arrow = guiHelper.drawableBuilder(Constants.RECIPE_GUI_VANILLA, 82, 128, 24, 17)
				.buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
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

	@Override
	public void drawExtras(Minecraft minecraft) {
		animatedFlame.draw(minecraft, 3, 19);
		arrow.draw(minecraft, 22, 18);
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
	public void setRecipe(IRecipeLayout recipeLayout, JEIBloomeryRecipe recipeWrapper, IIngredients ingredients) {
		// Okay, they're not technically *slots* but to all intents and purposes, that's how they behave
		IGuiItemStackGroup slots = recipeLayout.getItemStacks();

		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
		List<List<ItemStack>> outputList = ingredients.getOutputs(VanillaTypes.ITEM);
		List<ItemStack> fuels = recipeWrapper.getFuelItemStacks().stream().filter(MineFantasyCarbons::isCarbon).collect(Collectors.toList());

		slots.init(0, true, 1, 1);
		slots.init(1, true, 1, 34);
		slots.init(2, false, 47, 18);

		slots.set(0, inputs.get(0));
		slots.set(1, fuels);
		slots.set(2, outputList.get(0));
	}

	@Nullable
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	/**
	 * Generates all the MFR Bloomery recipes for JEI.
	 */
	public static Collection<JEIBloomeryRecipe> generateRecipes(IStackHelper stackHelper, List<ItemStack> fuelItemStacks) {
		return new ArrayList<>(generateBloomeryRecipes(stackHelper, fuelItemStacks));
	}

	private static Collection<JEIBloomeryRecipe> generateBloomeryRecipes(IStackHelper stackHelper, List<ItemStack> fuelItemStacks) {
		List<JEIBloomeryRecipe> recipes = new ArrayList<>();
		Collection<BloomeryRecipeBase> bloomeryRecipes = CraftingManagerBloomery.getRecipes();

		for (BloomeryRecipeBase recipe : bloomeryRecipes) {
			recipes.add(new JEIBloomeryRecipe(recipe, stackHelper, fuelItemStacks));
		}

		return recipes;
	}
}
