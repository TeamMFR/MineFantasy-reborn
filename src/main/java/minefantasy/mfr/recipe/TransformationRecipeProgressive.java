package minefantasy.mfr.recipe;

import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.util.ToolHelper;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TransformationRecipeProgressive extends TransformationRecipeBlockState {

	protected PropertyInteger property;
	protected boolean shouldIncrement;

	public TransformationRecipeProgressive(
			PropertyInteger property,
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
			String soundName,
			boolean shouldIncrement) {
		super(input, output, tool, consumableStacks, dropStack, shouldDropOnProgress, offhandStack, skill, research, skillXp, vanillaXp, progressMax, soundName);

		this.property = property;
		this.shouldIncrement = shouldIncrement;
	}

	@Override
	public boolean matches(ItemStack tool, ItemStack input, IBlockState state) {
		return ToolHelper.getToolTypeFromStack(tool).equals(this.tool)
				&& state.getBlock() == this.input.getBlock() && state != this.output;
	}

	@Override
	public EnumActionResult onUsedWithBlock(
			World world, BlockPos pos, IBlockState oldState,
			ItemStack item, EntityPlayer player, EnumFacing facing) {
		TransformationRecipeProgressive recipe = this;
		// Proceed with transformation
		if (this.output != null) {

			Integer value = oldState.getValue(property);
			if (shouldIncrement) {
				value++;
			}
			else {
				value--;
			}
			IBlockState newState = value >= recipe.getMaxProgress() ? this.output : oldState.withProperty(property, value);

			handleDropStack(world, pos, recipe);

			return performTransformation(world, pos, item, player, consumableStacks, recipe, newState);
		}

		return EnumActionResult.FAIL;
	}
}
