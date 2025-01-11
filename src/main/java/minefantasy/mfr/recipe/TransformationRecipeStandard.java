package minefantasy.mfr.recipe;

import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.util.BlockUtils;
import minefantasy.mfr.util.ToolHelper;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class TransformationRecipeStandard extends TransformationRecipeBase {
	protected NonNullList<Ingredient> inputs;
	protected ItemStack output;
	protected List<String> blockStateProperties;
	public TransformationRecipeStandard(
			Tool tool,
			NonNullList<Ingredient> inputs,
			ItemStack output,
			NonNullList<Ingredient> consumableStacks,
			Ingredient dropStack,
			boolean shouldDropOnProgress,
			Ingredient offhandStack,
			Skill skill,
			String research,
			int skillXp,
			float vanillaXp,
			int maxProgress,
			String soundName,
			List<String> blockStateProperties) {
		super(tool, consumableStacks, dropStack, shouldDropOnProgress, offhandStack, skill, research, skillXp, vanillaXp, maxProgress, soundName);
		this.inputs = inputs;
		this.output = output;
		this.blockStateProperties = blockStateProperties;
	}

	public boolean matches(ItemStack tool, ItemStack input, IBlockState state) {
		return ToolHelper.getToolTypeFromStack(tool).equals(this.tool)
				&& inputs.stream().anyMatch(ingredient -> ingredient.apply(input))
				&& !ItemStack.areItemStacksEqual(input, output);
	}

	@Override
	public EnumActionResult onUsedWithBlock(
			World world, BlockPos pos, IBlockState oldState,
			ItemStack item, EntityPlayer player, EnumFacing facing) {
		TransformationRecipeStandard recipe = this;

		// Proceed with transformation
		Block newBlock = null;
		ItemStack output = recipe.getOutput().copy();
		if (!output.isEmpty() && output.getItem() instanceof ItemBlock) {
			newBlock = Block.getBlockFromItem(output.getItem());
		}

		if (newBlock != null) {
			// Determine new BlockState, including migrating from old BlockState properties over to the new block
			IBlockState newState = handleBlockStates(oldState, newBlock.getDefaultState(), recipe);

			if (recipe.getMaxProgress() > 1) {
				return handleProgressTransformation(world, pos, item, player, recipe,
						consumableStacks, newState, oldState);
			}
			else {
				return performTransformation(world, pos, item, player, consumableStacks, recipe, newState);
			}
		}
		else {
			return EnumActionResult.FAIL;
		}
	}

	private static IBlockState handleBlockStates(IBlockState oldState, IBlockState newState, TransformationRecipeStandard recipe) {

		List<String> blockStateProperties = recipe.getBlockStateProperties();

		for (String recipeProp : blockStateProperties) {
			IProperty<?> property = oldState.getBlock().getBlockState().getProperty(recipeProp);
			if (property != null) {
				newState = BlockUtils.transferOldToNewProperty(newState, oldState, property);
			}
		}

		return newState;
	}

	public NonNullList<Ingredient> getInputs() {
		return inputs;
	}

	public ItemStack getOutput() {
		return output;
	}

	public List<String> getBlockStateProperties() {
		return blockStateProperties;
	}
}
