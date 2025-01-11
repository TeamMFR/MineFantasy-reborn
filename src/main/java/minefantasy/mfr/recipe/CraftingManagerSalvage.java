package minefantasy.mfr.recipe;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.mechanics.knowledge.ResearchLogic;
import minefantasy.mfr.recipe.factories.SalvageRecipeFactory;
import minefantasy.mfr.recipe.types.SalvageRecipeType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CraftingManagerSalvage extends CraftingManagerBase<SalvageRecipeBase> {

	private static final IForgeRegistry<SalvageRecipeBase> SALVAGE_RECIPES =
			new RegistryBuilder<SalvageRecipeBase>()
					.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "salvage_recipes"))
					.setType(SalvageRecipeBase.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();

	public CraftingManagerSalvage() {
		super(new SalvageRecipeFactory(),
				SalvageRecipeType.NONE,
				Constants.ASSET_DIRECTORY + "/recipes_mfr/salvage_recipes/",
				"config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/salvage_recipes/");
	}

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<SalvageRecipeBase> getRecipes() {
		return SALVAGE_RECIPES.getValuesCollection();
	}

	public void addRecipe(SalvageRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		ItemStack itemStack = recipe.getInput();
		if (ConfigCrafting.isSalvageRecipeEnabled(key)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			recipe.setRegistryName(key);
			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getInput().isItemEqual(s))
					&& (!checkForExistence || !SALVAGE_RECIPES.containsKey(recipe.getRegistryName()))) {
				SALVAGE_RECIPES.register(recipe);
			}
		}
	}

	public static SalvageRecipeBase findMatchingRecipe(ItemStack input, EntityPlayer user) {
		//// Normal, registered recipes.

		for (SalvageRecipeBase rec : getRecipes()) {
			if (rec.matches(input)) {
				String requiredResearch = rec.getRequiredResearch();
				if (requiredResearch.equals("none") ||
						ResearchLogic.getResearchCheck(user, ResearchLogic.getResearch(requiredResearch))) {
					return rec;
				}
			}
		}
		return null;
	}

	public static SalvageRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!SALVAGE_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Salvage Recipe Registry does not contain recipe: {}", name);
		}
		return SALVAGE_RECIPES.getValue(resourceLocation);
	}

	public static List<SalvageRecipeBase> getRecipesByName(String modId, String... names) {
		List<SalvageRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static SalvageRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return SALVAGE_RECIPES.getValue(resourceLocation);
	}
}
