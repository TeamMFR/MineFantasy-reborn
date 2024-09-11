package minefantasy.mfr.recipe;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.factories.BlastFurnaceRecipeFactory;
import minefantasy.mfr.recipe.types.BlastFurnaceRecipeType;
import minefantasy.mfr.util.CustomToolHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CraftingManagerBlastFurnace extends CraftingManagerBase<BlastFurnaceRecipeBase> {

	private static final IForgeRegistry<BlastFurnaceRecipeBase> BLAST_FURNACE_RECIPES =
			new RegistryBuilder<BlastFurnaceRecipeBase>()
					.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "blast_furnace_recipes"))
					.setType(BlastFurnaceRecipeBase.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();
	private static final Set<String> BLAST_FURNACE_RESEARCHES = new HashSet<>();

	public CraftingManagerBlastFurnace() {
		super(new BlastFurnaceRecipeFactory(),
				BlastFurnaceRecipeType.NONE,
				Constants.ASSET_DIRECTORY + "/recipes_mfr/blast_furnace_recipes/",
				"config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/blast_furnace_recipes/");
	}

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<BlastFurnaceRecipeBase> getRecipes() {
		return BLAST_FURNACE_RECIPES.getValuesCollection();
	}

	public void addRecipe(BlastFurnaceRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		ItemStack itemStack = recipe.getBlastFurnaceRecipeOutput();
		if (ConfigCrafting.isBlastFurnaceItemCraftable(itemStack)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			recipe.setRegistryName(key);
			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getBlastFurnaceRecipeOutput().isItemEqual(s))
					&& (!checkForExistence || !BLAST_FURNACE_RECIPES.containsKey(recipe.getRegistryName()))) {
				BLAST_FURNACE_RECIPES.register(recipe);
				String requiredResearch = recipe.getRequiredResearch();
				if (!requiredResearch.equals("none")) {
					BLAST_FURNACE_RESEARCHES.add(requiredResearch);
				}
			}
		}
	}

	public static BlastFurnaceRecipeBase findMatchingRecipe(ItemStack input, Set<String> knownResearches) {
		//// Normal, registered recipes.

		for (BlastFurnaceRecipeBase rec : getRecipes()) {
			if (rec.matches(input)) {
				if (rec.getRequiredResearch().equals("none")
						|| knownResearches.contains(rec.getRequiredResearch())) {
					return rec;
				}
			}
		}
		return null;
	}

	public static BlastFurnaceRecipeBase findRecipeByOutput(ItemStack output) {
		for (BlastFurnaceRecipeBase recipe : getRecipes()) {
			if (CustomToolHelper.areEqual(recipe.getBlastFurnaceRecipeOutput(), output)) {
				return recipe;
			}
		}
		return null;
	}

	public static BlastFurnaceRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!BLAST_FURNACE_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Blast Furnace Recipe Registry does not contain recipe: {}", name);
		}
		return BLAST_FURNACE_RECIPES.getValue(resourceLocation);
	}

	public static List<BlastFurnaceRecipeBase> getRecipesByName(String modId, String... names) {
		List<BlastFurnaceRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static BlastFurnaceRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return BLAST_FURNACE_RECIPES.getValue(resourceLocation);
	}

	public static Set<String> getBlastFurnaceResearches() {
		return BLAST_FURNACE_RESEARCHES;
	}
}
