package minefantasy.mfr.recipe;

import minefantasy.mfr.api.heating.Heatable;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.item.ItemHeated;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.util.CustomToolHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class AnvilShapedCustomMaterialRecipe extends AnvilRecipeBase {
	protected int width;
	protected int height;
	protected boolean tierModifyOutputCount;
	public AnvilShapedCustomMaterialRecipe(NonNullList<Ingredient> inputs, ItemStack output,
			String toolType, int craftTime, int hammerTier, int anvilTier, boolean hotOutput,
			String requiredResearch, Skill requiredSkill,
			int skillXp, float vanillaXp,
			int width, int height, boolean tierModifyOutputCount) {
		super(inputs, output, toolType, craftTime, hammerTier, anvilTier, hotOutput,
				requiredResearch, requiredSkill, skillXp, vanillaXp);
		this.width = width;
		this.height = height;
		this.tierModifyOutputCount = tierModifyOutputCount;
	}

	@Override
	public boolean matches(AnvilCraftMatrix matrix, World worldIn) {
		for (int i = 0; i <= matrix.getWidth() - this.width; ++i) {
			for (int j = 0; j <= matrix.getHeight() - this.height; ++j) {
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
		if (!modifyTiers(matrix, metal, true)) {
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

		String wood = null;
		String metal = null;
		for (int i = 0; i < matrix.getSizeInventory(); i++) {
			ItemStack item = matrix.getStackInSlot(i);
			if (!item.isEmpty()) {
				String component_wood = CustomToolHelper.getComponentMaterial(item, CustomMaterialType.WOOD_MATERIAL);
				String component_metal = CustomToolHelper.getComponentMaterial(item, CustomMaterialType.METAL_MATERIAL);

				for (CustomMaterial material : CustomMaterialRegistry.getList(CustomMaterialType.METAL_MATERIAL)){
					Ingredient materialIngredient = material.getMaterialIngredient();
					if (materialIngredient.apply(ItemHeated.getStack(item))) {
						component_metal = material.getName();
					}
				}

				if (wood == null && component_wood != null) {
					wood = component_wood;
				}
				if (metal == null && component_metal != null) {
					metal = component_metal;
				}
			}
		}
		if (metal != null && !tierModifyOutputCount) {
			CustomMaterialRegistry.addMaterial(result, CustomToolHelper.slot_main, metal);
		}
		if (wood != null && !tierModifyOutputCount) {
			CustomMaterialRegistry.addMaterial(result, CustomToolHelper.slot_haft, wood);
		}

		if (tierModifyOutputCount) {
			int modifiedCount = MathHelper.clamp(
					CustomMaterialRegistry.getMaterial(metal).getTier() * result.getCount(),
					1,
					result.getMaxStackSize());
			result.setCount(modifiedCount);
		}

		return result;
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
		return true;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isTierModifyOutputCount() {
		return tierModifyOutputCount;
	}
}
