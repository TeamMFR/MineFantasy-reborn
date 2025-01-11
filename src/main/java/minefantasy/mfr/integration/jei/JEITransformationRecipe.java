package minefantasy.mfr.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import minefantasy.mfr.recipe.TransformationRecipeBase;
import minefantasy.mfr.recipe.TransformationRecipeBlockState;
import minefantasy.mfr.recipe.TransformationRecipeStandard;
import minefantasy.mfr.util.GuiHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.NonNullList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a JEI "recipe" for the Transformation recipes.
 */
public class JEITransformationRecipe implements IRecipeWrapper {

	private final List<ItemStack> inputs;
	private final TransformationRecipeBase recipe;

	private final List<ItemStack> outputs;
	private final NonNullList<Ingredient> consumableStacks;
	private final Ingredient offhandStack;
	private final Ingredient dropStack;


	public JEITransformationRecipe(TransformationRecipeBase recipe) {
		this.recipe = recipe;
		if (recipe instanceof TransformationRecipeStandard) {
			inputs = ((TransformationRecipeStandard) recipe).getInputs()
					.stream()
					.flatMap(ingredient -> Arrays.stream(ingredient.getMatchingStacks()))
					.collect(Collectors.toList());
		}
		else {
			IBlockState input = ((TransformationRecipeBlockState) recipe).getInput();
			ItemStack inputStack = new ItemStack(input.getBlock());
			NBTTagCompound nbt = new NBTTagCompound();
			NBTUtil.writeBlockState(nbt, input);
			if (inputStack.getHasSubtypes()) {
				inputStack = new ItemStack(input.getBlock(), 1, input.getBlock().getMetaFromState(input));
			}
			if (!nbt.isEmpty()) {
				inputStack.setTagCompound(nbt);
			}

			inputs = Collections.singletonList(
					inputStack);
		}

		if (recipe instanceof TransformationRecipeStandard) {
			outputs = Collections.singletonList(((TransformationRecipeStandard) recipe).getOutput());
		}
		else {
			IBlockState output = ((TransformationRecipeBlockState) recipe).getOutput();
			ItemStack outputStack = new ItemStack(output.getBlock());
			if (outputStack.getHasSubtypes()) {
				outputStack = new ItemStack(output.getBlock(), 1, output.getBlock().getMetaFromState(output));
			}
			outputs = Collections.singletonList(
					outputStack);
		}

		consumableStacks = recipe.getConsumableStacks();
		offhandStack = recipe.getOffhandStack();
		dropStack = recipe.getDropStack();
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(this.inputs));
		ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(this.outputs));
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		GlStateManager.pushMatrix();
		GlStateManager.scale(1.5, 1.5, 1.5);

		// draw tool icon with required tier int
		GuiHelper.renderToolIcon(minecraft.currentScreen, recipe.getTool().getName(), -1,
				recipeWidth - 125, recipeHeight - 62, true, false);

		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();

		GlStateManager.scale(0.75, 0.75, 0.75);
		// display the required Skill type of for this recipe
		minecraft.fontRenderer.drawStringWithShadow(recipe.getSkill().getDisplayName(), (float)
				((recipeWidth / 2) - minecraft.fontRenderer.getStringWidth(recipe.getSkill().getDisplayName()) / 2) + 105,
				(float) 8, 16777215);
		GlStateManager.popMatrix();
	}

	/**
	 * TODO?: This could be used to open the page in the recipe book where we have this recipe
	 */
	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}

	public List<ItemStack> getInputs() {
		return inputs;
	}

	public TransformationRecipeBase getRecipe() {
		return recipe;
	}

	public List<ItemStack> getOutputs() {
		return outputs;
	}

	public NonNullList<Ingredient> getConsumableStacks() {
		return consumableStacks;
	}

	public Ingredient getOffhandStack() {
		return offhandStack;
	}

	public Ingredient getDropStack() {
		return dropStack;
	}
}
