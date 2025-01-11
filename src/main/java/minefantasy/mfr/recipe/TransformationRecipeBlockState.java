package minefantasy.mfr.recipe;

import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.util.ToolHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TransformationRecipeBlockState extends TransformationRecipeBase {
	protected IBlockState input;
	protected IBlockState output;

	public TransformationRecipeBlockState(
			IBlockState input,
			IBlockState output,
			Tool tool,
			NonNullList<Ingredient> consumableStacks,
			Ingredient dropStack,
			boolean shouldDropOnProgress,
			Ingredient offhandStack,
			Skill skill,
			String research,
			int skillXp,
			float vanillaXp,
			int progressMax,
			String soundName) {
		super(tool, consumableStacks, dropStack, shouldDropOnProgress, offhandStack, skill, research, skillXp, vanillaXp, progressMax, soundName);
		this.input = input;
		this.output = output;
	}

	@Override
	public boolean matches(ItemStack tool, ItemStack input, IBlockState state) {
		return ToolHelper.getToolTypeFromStack(tool).equals(this.tool)
				&& state == this.input && state != this.output;
	}

	@Override
	public EnumActionResult onUsedWithBlock(
			World world, BlockPos pos, IBlockState oldState,
			ItemStack item, EntityPlayer player, EnumFacing facing) {
		TransformationRecipeBlockState recipe = this;
		// Proceed with transformation
		if (this.output != null) {
			IBlockState newState = this.output;

			if (recipe.getMaxProgress() > 1) {
				return handleProgressTransformation(world, pos, item, player, recipe,
						consumableStacks, newState, oldState);
			}
			else {
				return performTransformation(world, pos, item, player, consumableStacks, recipe, newState);
			}
		}

		return EnumActionResult.FAIL;
	}

	public IBlockState getInput() {
		return input;
	}

	public IBlockState getOutput() {
		return output;
	}
}
