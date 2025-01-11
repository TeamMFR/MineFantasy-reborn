package minefantasy.mfr.recipe;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.mechanics.knowledge.ResearchLogic;
import minefantasy.mfr.recipe.factories.TransformationRecipeFactory;
import minefantasy.mfr.recipe.types.TransformationRecipeType;
import minefantasy.mfr.util.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CraftingManagerTransformation extends CraftingManagerBase<TransformationRecipeBase> {

	private static final IForgeRegistry<TransformationRecipeBase> TRANSFORMATION_RECIPES =
			new RegistryBuilder<TransformationRecipeBase>()
					.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "transformation_recipes"))
					.setType(TransformationRecipeBase.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();

	public CraftingManagerTransformation() {
		super(new TransformationRecipeFactory(),
				TransformationRecipeType.NONE,
				Constants.ASSET_DIRECTORY + "/recipes_mfr/transformation_recipes/",
				"config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/transformation_recipes/");
	}

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<TransformationRecipeBase> getRecipes() {
		return TRANSFORMATION_RECIPES.getValuesCollection();
	}

	public void addRecipe(TransformationRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		recipe.setRegistryName(key);
		if (recipe instanceof TransformationRecipeStandard) {
			addStandardRecipe((TransformationRecipeStandard) recipe, checkForExistence, key);
		}
		else if (recipe instanceof TransformationRecipeBlockState) {
			addBlockStateRecipe((TransformationRecipeBlockState) recipe, checkForExistence, key);
		}
	}

	private static void addBlockStateRecipe(TransformationRecipeBlockState recipe, boolean checkForExistence, ResourceLocation key) {
		IBlockState state = recipe.getOutput();
		if (ConfigCrafting.isTransformationRecipeEnabled(key)) {
			if (!checkForExistence || !TRANSFORMATION_RECIPES.containsKey(recipe.getRegistryName())) {
				TRANSFORMATION_RECIPES.register(recipe);
			}

			List<IBlockState> states = state.getBlock().getBlockState().getValidStates();

			if (states.stream().anyMatch(s -> recipe.getOutput().equals(state))
					&& (!checkForExistence || !TRANSFORMATION_RECIPES.containsKey(recipe.getRegistryName()))) {
				TRANSFORMATION_RECIPES.register(recipe);
			}
		}
	}

	private static void addStandardRecipe(TransformationRecipeStandard recipe, boolean checkForExistence, ResourceLocation key) {
		ItemStack itemStack = recipe.getOutput();
		if (ConfigCrafting.isTransformationRecipeEnabled(key)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getOutput().isItemEqual(s))
					&& (!checkForExistence || !TRANSFORMATION_RECIPES.containsKey(recipe.getRegistryName()))) {
				TRANSFORMATION_RECIPES.register(recipe);
			}
		}
	}

	public static TransformationRecipeBase findMatchingRecipe(
			ItemStack tool,
			ItemStack input,
			IBlockState state,
			BlockPos pos,
			EntityPlayer player,
			EnumFacing facing) {

		List<TransformationRecipeBase> validRecipes = new ArrayList<>();
		for (TransformationRecipeBase rec : getRecipes()) {
			if (rec.matches(tool, input, state)) {
				validRecipes.add(rec);
			}
		}

		if (validRecipes.isEmpty()) {
			return null;
		}

		Optional<TransformationRecipeBase> optionalRecipe = validRecipes
				.stream()
				.filter(r -> validateTransformation(pos, tool, player, facing, r))
				.findFirst();

		return optionalRecipe.orElse(null);
	}

	/**
	 * Makes all required checks to see if the transformation is valid:
	 * Can Player edit, does Player have research, does Player have offhand stack, and does player have all consumable stacks
	 *
	 * @param pos 		The BlockPos of the block being transformed
	 * @param tool		The ItemStack of the tool being used to perform the transformation
	 * @param player	The EntityPlayer performing the action
	 * @param facing	The EnumFacing of the action
	 * @param recipe	The TransformationRecipe being validated
	 * @return true if the recipe is valid, false if the recipe is invalid
	 */
	public static boolean validateTransformation(BlockPos pos, ItemStack tool, EntityPlayer player,
			EnumFacing facing, TransformationRecipeBase recipe) {

		// Check Player can change block and has Recipe Research unlocked
		if (player.canPlayerEdit(pos, facing, tool)) {
			String requiredResearch = recipe.getRequiredResearch();
			if (requiredResearch.equals("none")
					|| ResearchLogic.getResearchCheck(player, ResearchLogic.getResearch(requiredResearch))) {
				Ingredient offhand = recipe.getOffhandStack();
				// Check if the offhand stack is in the offhand slot or bypass if empty
				if (offhand == Ingredient.EMPTY || offhand.apply(player.getHeldItemOffhand())) {

					NonNullList<Ingredient> consumables = recipe.getConsumableStacks();
					// Check if the consumable stack is present in player inventory or bypass if empty
					return consumables.isEmpty() || consumables.stream()
							.allMatch(i -> PlayerUtils.playerInventoryHasIngredient(player.inventory, i));
				}
			}
			else {
				player.sendMessage(new TextComponentTranslation("knowledge.unknownUse"));
				return false;
			}
		}
		return false;
	}

	public static TransformationRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!TRANSFORMATION_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Tanner Recipe Registry does not contain recipe: {}", name);
		}
		return TRANSFORMATION_RECIPES.getValue(resourceLocation);
	}

	public static List<TransformationRecipeBase> getRecipesByName(String modId, String... names) {
		List<TransformationRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static TransformationRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return TRANSFORMATION_RECIPES.getValue(resourceLocation);
	}
}
