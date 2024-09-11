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
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.recipe.BlastFurnaceRecipeBase;
import minefantasy.mfr.recipe.CraftingManagerBlastFurnace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JEIBlastFurnaceRecipeCategory implements IRecipeCategory<JEIBlastFurnaceRecipe> {
	static final String UID = "minefantasyreforged:blast_furnace";

	static final ResourceLocation TEXTURE = new ResourceLocation(MineFantasyReforged.MOD_ID, "textures/integration/jei/blast_furnace_background.png");
	static final ResourceLocation DOWN_ARROW_TEXTURE = new ResourceLocation(MineFantasyReforged.MOD_ID, "textures/integration/jei/gui_assets.png");
	private final IDrawable icon;

	static final int WIDTH = 47;
	static final int HEIGHT = 88;

	private final IDrawable background;
	protected final IDrawableStatic staticFlame;
	protected final IDrawableAnimated animatedFlameOne;
	protected final IDrawableAnimated animatedFlameTwo;
	protected final IDrawableAnimated arrow;

	public JEIBlastFurnaceRecipeCategory(IRecipeCategoryRegistration registry, GuiHelper guiHelper) {
		IGuiHelper iGuiHelper = registry.getJeiHelpers().getGuiHelper();
		background = iGuiHelper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
		icon = iGuiHelper.createDrawableIngredient(new ItemStack(MineFantasyBlocks.BLAST_CHAMBER));
		staticFlame = guiHelper.createDrawable(Constants.RECIPE_GUI_VANILLA, 82, 114, 14, 14);
		animatedFlameOne = guiHelper.createAnimatedDrawable(staticFlame, 300, IDrawableAnimated.StartDirection.TOP, true);
		animatedFlameTwo = guiHelper.createAnimatedDrawable(staticFlame, 300, IDrawableAnimated.StartDirection.TOP, true);

		arrow = guiHelper.drawableBuilder(DOWN_ARROW_TEXTURE, 0, 0, 17, 24)
				.buildAnimated(200, IDrawableAnimated.StartDirection.TOP, false);
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
		animatedFlameOne.draw(minecraft, 1, 43);
		animatedFlameTwo.draw(minecraft, 33, 43);
		arrow.draw(minecraft, 14, 42);
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
	public void setRecipe(IRecipeLayout recipeLayout, JEIBlastFurnaceRecipe recipeWrapper, IIngredients ingredients) {
		// Okay, they're not technically *slots* but to all intents and purposes, that's how they behave
		IGuiItemStackGroup slots = recipeLayout.getItemStacks();

		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
		List<List<ItemStack>> outputList = ingredients.getOutputs(VanillaTypes.ITEM);
		List<ItemStack> fuels = recipeWrapper.getFuelItemStacks().stream().filter(MineFantasyCarbons::isCarbon).collect(Collectors.toList());
		fuels.add(new ItemStack(MineFantasyItems.COAL_FLUX));

		slots.init(0, true, 15, 1);//inputs
		slots.init(1, true, 15, 23);//fuels
		slots.init(2, false, 15, 69);//outputs, also, nice

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
	 * Generates all the MFR Blast Furnace recipes for JEI.
	 */
	public static Collection<JEIBlastFurnaceRecipe> generateRecipes(IStackHelper stackHelper, List<ItemStack> fuelItemStacks) {
		return new ArrayList<>(generateBlastFurnaceRecipes(stackHelper, fuelItemStacks));
	}

	private static Collection<JEIBlastFurnaceRecipe> generateBlastFurnaceRecipes(IStackHelper stackHelper, List<ItemStack> fuelItemStacks) {
		List<JEIBlastFurnaceRecipe> recipes = new ArrayList<>();
		Collection<BlastFurnaceRecipeBase> blastFurnaceRecipes = CraftingManagerBlastFurnace.getRecipes();

		for (BlastFurnaceRecipeBase recipe : blastFurnaceRecipes) {
			recipes.add(new JEIBlastFurnaceRecipe(recipe, stackHelper, fuelItemStacks));
		}

		return recipes;
	}
}
