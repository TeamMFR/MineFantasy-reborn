package minefantasy.mfr.recipe;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.factories.SpecialRecipeFactory;
import minefantasy.mfr.recipe.types.SpecialRecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CraftingManagerSpecial extends CraftingManagerBase<SpecialRecipeBase> {

	private static final IForgeRegistry<SpecialRecipeBase> SPECIAL_RECIPES =
			new RegistryBuilder<SpecialRecipeBase>()
					.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "special_recipes"))
					.setType(SpecialRecipeBase.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();

	public CraftingManagerSpecial() {
		super(new SpecialRecipeFactory(),
				SpecialRecipeType.NONE,
				Constants.ASSET_DIRECTORY + "/recipes_mfr/special_recipes/",
				"config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/special_recipes/");
	}

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<SpecialRecipeBase> getRecipes() {
		return SPECIAL_RECIPES.getValuesCollection();
	}

	public void addRecipe(SpecialRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		ItemStack itemStack = recipe.getOutput();
		if (ConfigCrafting.isSpecialItemCraftable(itemStack)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			recipe.setRegistryName(key);
			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getOutput().isItemEqual(s))
					&& (!checkForExistence || !SPECIAL_RECIPES.containsKey(recipe.getRegistryName()))) {
				SPECIAL_RECIPES.register(recipe);
			}
		}
	}

	public static SpecialRecipeBase findMatchingRecipe(ItemStack recipeInput, ItemStack craftInput) {
		//// Normal, registered recipes.

		for (SpecialRecipeBase rec : getRecipes()) {
			if (rec.matches(recipeInput, craftInput)) {
				return rec;
			}
		}
		return null;
	}

	public static SpecialRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!SPECIAL_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Special Recipe Registry does not contain recipe: {}", name);
		}
		return SPECIAL_RECIPES.getValue(resourceLocation);
	}

	public static List<SpecialRecipeBase> getRecipesByName(String modId, String... names) {
		List<SpecialRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static SpecialRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return SPECIAL_RECIPES.getValue(resourceLocation);
	}
}
