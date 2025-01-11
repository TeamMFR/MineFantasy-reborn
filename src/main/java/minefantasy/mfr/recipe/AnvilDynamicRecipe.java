package minefantasy.mfr.recipe;

import minefantasy.mfr.api.heating.Heatable;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.item.ItemHeated;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.material.MetalMaterial;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.util.CustomToolHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnvilDynamicRecipe extends AnvilRecipeBase {
	protected int width;
	protected int height;
	public boolean modifyOutput;
	protected boolean shouldModifyTiers;

	public AnvilDynamicRecipe(NonNullList<Ingredient> inputs, ItemStack output,
			String toolType, int craftTime, int hammerTier, int anvilTier, boolean hotOutput,
			String requiredResearch, Skill requiredSkill,
			int skillXp, float vanillaXp, boolean modifyOutput, boolean shouldModifyTiers,
			int width, int height) {
		super(inputs, output, toolType, craftTime, hammerTier, anvilTier, hotOutput,
				requiredResearch, requiredSkill, skillXp, vanillaXp);
		this.width = width;
		this.height = height;
		this.modifyOutput = modifyOutput;
		this.shouldModifyTiers = shouldModifyTiers;
	}

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(AnvilCraftMatrix matrix, World worldIn) {
		for (int i = 0; i <= matrix.getWidth() - width; ++i) {
			for (int j = 0; j <= matrix.getHeight() - height; ++j) {
				if (this.checkMatch(matrix, i, j, true)) {
					return true;
				}

				if (this.checkMatch(matrix, i, j, false)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks if the region of a crafting inventory is match for the recipe.
	 */
	protected boolean checkMatch(AnvilCraftMatrix matrix, int x, int y, boolean mirror) {
		String wood = null;
		String metal = null;

		for (int matrixX = 0; matrixX < MAX_WIDTH; ++matrixX) {
			for (int matrixY = 0; matrixY < MAX_HEIGHT; ++matrixY) {
				int recipeX = matrixX - x;
				int recipeY = matrixY - y;
				Ingredient ingredient = Ingredient.EMPTY;

				if (recipeX >= 0 && recipeY >= 0 && recipeX < width && recipeY < height) {
					if (mirror) {
						ingredient = inputs.get(width - recipeX - 1 + recipeY * width);
					} else {
						ingredient = inputs.get(recipeX + recipeY * width);
					}
				}

				ItemStack inputItem = matrix.getStackInRowAndColumn(matrixX, matrixY);

				if (!inputItem.isEmpty() || !ingredient.apply(ItemStack.EMPTY)) {

					String component_wood = CustomToolHelper.getComponentMaterial(inputItem, CustomMaterialType.WOOD_MATERIAL);
					String component_metal = CustomToolHelper.getComponentMaterial(inputItem, CustomMaterialType.METAL_MATERIAL);

					// CHECK CUSTOM METAL
					if (component_metal != null) {
						if (metal == null) {
							metal = component_metal;
						} else {
							if (!metal.equalsIgnoreCase(component_metal)) {
								return false;
							}
						}
					}
					// CHECK CUSTOM WOOD
					if (component_wood != null) {
						if (wood == null) {
							wood = component_wood;
						} else {
							if (!wood.equalsIgnoreCase(component_wood)) {
								return false;
							}
						}
					}

					// HEATING
					if (Heatable.requiresHeating && Heatable.canHeatItem(inputItem)) {
						return false;
					}
					if (!Heatable.isWorkable(inputItem)) {
						return false;
					}
					inputItem = getHotItem(inputItem);

					//ingot to bar material matching
					for (CustomMaterial material : CustomMaterialRegistry.getList(CustomMaterialType.METAL_MATERIAL)){
						Ingredient materialIngredient = material.getMaterialIngredient();
						if (materialIngredient.apply(ItemHeated.getStack(inputItem))) {
							metal = material.getName();
						}
					}

					if (inputItem == null && ingredient != null || inputItem != null && ingredient == null) {
						return false;
					}

					if (inputItem.isEmpty()) {
						return false;
					}

					if (!ingredient.apply(inputItem)) {
						return false;
					}

					if (!CustomToolHelper.doesMatchForRecipe(ingredient, inputItem)) {
						return false;
					}
				}
			}
		}
		if (shouldModifyTiers && !modifyTiers(matrix, metal, true)) {
			modifyTiers(matrix, wood, false);
		}

		return true;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(AnvilCraftMatrix matrix) {
		ItemStack result = super.getCraftingResult(matrix);

		String metal = null;
		for (int i = 0; i < matrix.getSizeInventory(); i++) {
			ItemStack inputItem = matrix.getStackInSlot(i);
			String component_metal = CustomToolHelper.getComponentMaterial(inputItem, CustomMaterialType.METAL_MATERIAL);

			if (metal == null && component_metal != null) {
				metal = component_metal;
			}

			if (modifyOutput){
				for (CustomMaterial material : CustomMaterialRegistry.getList(CustomMaterialType.METAL_MATERIAL)){
					if (material instanceof MetalMaterial) {
						if (material.getName().equals(metal)){
							ItemStack[] oreDictItemStacks = material.getMaterialIngredient().getMatchingStacks();
							result = oreDictItemStacks[0];
							metal = null;
						}
					}
				}
			}
			else {
				for (CustomMaterial material : CustomMaterialRegistry.getList(CustomMaterialType.METAL_MATERIAL)){
					Ingredient materialIngredient = material.getMaterialIngredient();
					if (materialIngredient.apply(ItemHeated.getStack(inputItem))) {
						metal = material.getName();
					}
				}
			}
		}

		if (metal != null) {
			CustomMaterialRegistry.addMaterial(result, CustomToolHelper.slot_main, metal);
		}

		return result;
	}

	/**
	 * Returns all ItemStacks that are the result of this recipe
	 **/
	public static List<ItemStack> getOutputsFromGridMap(
			List<List<ItemStack>> ingredients,
			boolean modifyOutput,
			ItemStack result) {

		List<ItemStack> outputs = new ArrayList<>();

		//Bar to Ingot
		if (modifyOutput){
			for (CustomMaterial material : CustomMaterialRegistry.getList(CustomMaterialType.METAL_MATERIAL)) {
				if (material instanceof MetalMaterial) {
					outputs.addAll(Arrays.asList(material.getMaterialIngredient().getMatchingStacks()));
				}
			}
		}
		else {
			String metal = null;
			for (List<ItemStack> stacks : ingredients) {
				//Ingot to Bar
				for (ItemStack inputItem : stacks) {
					ItemStack resultCopy = result.copy();
					String component_metal = CustomToolHelper.getComponentMaterial(inputItem, CustomMaterialType.METAL_MATERIAL);

					if (metal == null && component_metal != null) {
						metal = component_metal;
					}

					//ingot to bar material matching
					for (CustomMaterial material : CustomMaterialRegistry.getList(CustomMaterialType.METAL_MATERIAL)){
						Ingredient materialIngredient = material.getMaterialIngredient();
						if (materialIngredient.apply(ItemHeated.getStack(inputItem))) {
							metal = material.getName();
						}
					}

					if (metal != null) {
						CustomMaterialRegistry.addMaterial(resultCopy, CustomToolHelper.slot_main, metal);
					}
					outputs.add(resultCopy);
				}
			}
		}

		return outputs;
	}

	/**
	 * Returns all ItemStacks that are the result of this recipe
	 **/
	public static List<ItemStack> getOutputsFromIngredients(
			List<Ingredient> ingredients,
			boolean modifyOutput,
			ItemStack result) {

		List<ItemStack> outputs = new ArrayList<>();

		String metal = null;
		//Bar to Ingot
		if (modifyOutput){
			for (CustomMaterial material : CustomMaterialRegistry.getList(CustomMaterialType.METAL_MATERIAL)) {
				if (material instanceof MetalMaterial) {
					outputs.addAll(Arrays.asList(material.getMaterialIngredient().getMatchingStacks()));
				}
			}
		}
		else {
			//Ingot to Bar
			for (Ingredient ingredient : ingredients) {
				for (ItemStack stack : ingredient.getMatchingStacks()) {
					ItemStack resultCopy = result.copy();
					String component_metal = CustomToolHelper.getComponentMaterial(stack, CustomMaterialType.METAL_MATERIAL);

					if (metal == null && component_metal != null) {
						metal = component_metal;
					}

					//ingot to bar material matching
					for (CustomMaterial material : CustomMaterialRegistry.getList(CustomMaterialType.METAL_MATERIAL)){
						Ingredient materialIngredient = material.getMaterialIngredient();
						if (materialIngredient.apply(ItemHeated.getStack(stack))) {
							metal = material.getName();
						}
					}


					if (metal != null) {
						CustomMaterialRegistry.addMaterial(resultCopy, CustomToolHelper.slot_main, metal);
					}
					outputs.add(resultCopy);
				}
			}
		}

		return outputs;
	}

	/**
	 * Returns the size of the recipe area
	 */
	@Override
	public int getRecipeSize() {
		return width * height;
	}

	@Override
	public boolean useCustomTiers() {
		return shouldModifyTiers;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
