package minefantasy.mfr.recipe;

import com.google.gson.JsonObject;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.material.WoodMaterial;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.RecipeHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class RecipeTimberDynamic extends ShapedOreRecipe {

	public RecipeTimberDynamic(ResourceLocation group, @Nonnull ItemStack result, CraftingHelper.ShapedPrimer primer) {
		super(group, result, primer);
	}

	@Override
	protected boolean checkMatch(InventoryCrafting matrix, int startX, int startY, boolean mirror) {
		ItemStack inputItemStack = ItemStack.EMPTY;
		ItemStack belowInputItemStack = ItemStack.EMPTY;
		for (int x = 0; x < matrix.getWidth(); x++) {
			for (int y = 0; y < matrix.getHeight(); y++) {
				int subX = x - startX;
				int subY = y - startY;
				Ingredient target = Ingredient.EMPTY;

				if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
					if (mirror) {
						target = input.get(width - subX - 1 + subY * width);
					} else {
						target = input.get(subX + subY * width);
					}
				}
				if (!target.apply(matrix.getStackInRowAndColumn(x, y))) {
					return false;
				} else {
					if (!matrix.getStackInRowAndColumn(x, y).isEmpty()) {
						inputItemStack = matrix.getStackInRowAndColumn(x, y);
						belowInputItemStack = matrix.getStackInRowAndColumn(x, y - 1);
					}
				}
			}
		}

		return (!inputItemStack.isEmpty() && inputItemStack.isItemEqual(belowInputItemStack));
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting matrix) {
		ItemStack inputStack = ItemStack.EMPTY;
		for (int i = 0; i < matrix.getSizeInventory(); i++) {
			ItemStack stackInSlot = matrix.getStackInSlot(i);
			if (!stackInSlot.isEmpty() && getIngredients().get(0).apply(stackInSlot)) {
				inputStack = stackInSlot;
			}
		}
		ItemStack outputModified = output.copy();
		CustomMaterial inputMaterial = CustomMaterialRegistry.NONE;
		for (CustomMaterial material : CustomMaterialRegistry.getList(CustomMaterialType.WOOD_MATERIAL)) {
			if (material instanceof WoodMaterial) {
				Ingredient materialIngredient = material.getMaterialIngredient();
				if (materialIngredient.apply(inputStack)) {
					inputMaterial = material;
				}
			}
		}
		if (inputMaterial == CustomMaterialRegistry.NONE) {
			inputMaterial = CustomMaterialRegistry.getMaterial(Constants.SCRAP_WOOD_TAG);
		}
		CustomMaterialRegistry.addMaterial(outputModified, CustomToolHelper.slot_main, inputMaterial.getName());
		return outputModified;
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		return output.copy();
	}

	public static class Factory implements IRecipeFactory {
		@Override
		public IRecipe parse(JsonContext context, JsonObject json) {

			final String group = JsonUtils.getString(json, "group", "");
			final CraftingHelper.ShapedPrimer primer = RecipeHelper.parseShaped(context, json);
			final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

			return new RecipeTimberDynamic(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
		}
	}
}
