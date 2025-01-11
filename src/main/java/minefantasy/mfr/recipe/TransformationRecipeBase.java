package minefantasy.mfr.recipe;

import minefantasy.mfr.api.tool.TransformationBlockWrapper;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.data.IStoredVariable;
import minefantasy.mfr.data.Persistence;
import minefantasy.mfr.data.PlayerData;
import minefantasy.mfr.util.InventoryUtils;
import minefantasy.mfr.util.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class TransformationRecipeBase extends IForgeRegistryEntry.Impl<TransformationRecipeBase> implements IRecipeMFR{
	public static IStoredVariable<TransformationBlockWrapper> TRANSFORMATION_BLOCK = IStoredVariable.StoredVariable
			.ofTransformationBlockWrapper("transformationBlock", Persistence.NEVER)
			.setSynced();

	protected Tool tool;
	protected NonNullList<Ingredient> consumableStacks;
	protected Ingredient dropStack;
	protected boolean shouldDropOnProgress;
	protected Ingredient offhandStack;
	protected int maxProgress;
	protected SoundEvent sound;
	protected String requiredResearch;
	protected Skill skill;
	protected Integer skillXp;
	protected float vanillaXp;


	public TransformationRecipeBase(
			Tool tool,
			NonNullList<Ingredient> consumableStacks,
			Ingredient dropStack,
			boolean shouldDropOnProgress,
			Ingredient offhandStack,
			Skill skill,
			String research,
			int skillXp,
			float vanillaXp,
			int maxProgress,
			String soundName) {
		this.tool = tool;
		this.consumableStacks = consumableStacks;
		this.dropStack = dropStack;
		this.shouldDropOnProgress = shouldDropOnProgress;
		this.offhandStack = offhandStack;
		this.skill = skill;
		this.requiredResearch = research;
		this.skillXp = skillXp;
		this.vanillaXp = vanillaXp;
		this.maxProgress = maxProgress;
		this.sound = SoundEvent.REGISTRY.getObject(new ResourceLocation(soundName));
	}

	static {
		PlayerData.registerStoredVariables(TRANSFORMATION_BLOCK);
	}

	abstract boolean matches(ItemStack tool, ItemStack input, IBlockState state);

	@Override
	public String getResourceLocation() {
		return this.getRegistryName().toString();
	}

	public abstract EnumActionResult onUsedWithBlock(
			World world,
			BlockPos pos,
			IBlockState oldState,
			ItemStack item,
			EntityPlayer player,
			EnumFacing facing);

	protected EnumActionResult handleProgressTransformation(
			World world, BlockPos pos, ItemStack item, EntityPlayer player,
			TransformationRecipeBase recipe, NonNullList<Ingredient> consumableStacks,
			IBlockState newState, IBlockState oldState) {
		PlayerData data = PlayerData.get(player);
		String displayName = newState != Blocks.AIR.getDefaultState() ? newState.getBlock().getLocalizedName() : dropStack.getMatchingStacks()[0].getDisplayName();
		if (data != null) {
			TransformationBlockWrapper transformationBlock = data.getVariable(TRANSFORMATION_BLOCK);
			// Update Transformation Block
			if (transformationBlock != null
					// Checks the recorded transformation block has the same state and position as the requested
					&& TransformationBlockWrapper.checkTransformationBlock(transformationBlock, oldState, pos)) {

				transformationBlock.setTool(item);
				transformationBlock.setPos(pos);
				transformationBlock.setState(oldState);
				//Add Progress
				transformationBlock.setProgress(transformationBlock.getProgress() + 1);
				transformationBlock.setMaxProgress(this.maxProgress);
				transformationBlock.setDisplayName(displayName);
				data.setVariable(TRANSFORMATION_BLOCK, transformationBlock);

				// Check if progress has hit the maxProgress of the recipe
				if (transformationBlock.getProgress() >= recipe.getMaxProgress()) {
					performTransformation(world, pos, item, player, consumableStacks, recipe, newState);

					//Don't set to null, it won't sync to the client properly
					data.setVariable(TRANSFORMATION_BLOCK, new TransformationBlockWrapper(
							ItemStack.EMPTY, pos, newState, 0, this.maxProgress, ""));
				}
			}
			// Create Transformation Block
			else {
				data.setVariable(TRANSFORMATION_BLOCK,
						new TransformationBlockWrapper(item, pos, oldState, 1, this.maxProgress, displayName));
			}
			// Check if the drop stack should be dropped on each progression
			if (shouldDropOnProgress) {
				// Handle drop stack
				handleDropStack(world, pos, recipe);
			}
			//Sync with client
			data.sync();
			return EnumActionResult.PASS;
		}
		return EnumActionResult.FAIL;
	}

	protected EnumActionResult performTransformation(World world, BlockPos pos, ItemStack item, EntityPlayer player,
			NonNullList<Ingredient> consumableStacks, TransformationRecipeBase recipe, IBlockState newState) {
		if (!player.capabilities.isCreativeMode) {
			//Handle consumable stack
			if (!consumableStacks.isEmpty()) {
				for (Ingredient recipeIngredient : consumableStacks) {
					for (ItemStack ingredientStack : recipeIngredient.getMatchingStacks()) {
						ItemStack consumableStack = ingredientStack.copy();
						// consume stack
						player.inventory.decrStackSize(PlayerUtils.getSlotFor(player, consumableStack),
								consumableStack.getCount());
						// determine return item for the consumable, aka its container item
						ItemStack returnItem = consumableStack.getItem().getContainerItem() != null
								? new ItemStack(consumableStack.getItem().getContainerItem())
								: ItemStack.EMPTY;

						// return the return stack to inventory or drop if inventory full
						if (!returnItem.isEmpty() && !player.inventory.addItemStackToInventory(returnItem)) {
							player.entityDropItem(returnItem, 0F);
						}
					}
				}
			}
		}

		// Add skill
		if (!world.isRemote) {
			recipe.giveVanillaXp(player, 0, 1);
			recipe.giveSkillXp(player, 0);
		}
		// Damage tool
		item.damageItem(recipe.getMaxProgress(), player);

		// Handle drop stack
		if (!shouldDropOnProgress) {
			handleDropStack(world, pos, recipe);
		}

		world.destroyBlock(pos, false);
		world.setBlockState(pos, newState);
		return EnumActionResult.PASS;
	}

	protected static void handleDropStack(World world, BlockPos pos, TransformationRecipeBase recipe) {
		// Handle drop stack
		Ingredient dropStack = recipe.getDropStack();
		if (dropStack != Ingredient.EMPTY) {
			InventoryUtils.dropItemInWorld(world, dropStack.getMatchingStacks()[0].copy(), pos.add(0, 1, 0));
		}
	}

	public Tool getTool() {
		return tool;
	}

	public NonNullList<Ingredient> getConsumableStacks() {
		return consumableStacks;
	}

	public Ingredient getDropStack() {
		return dropStack;
	}

	public Ingredient getOffhandStack() {
		return offhandStack;
	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public SoundEvent getSound() {
		return sound;
	}

	@Override
	public String getRequiredResearch() {
		return requiredResearch;
	}

	@Override
	public Skill getSkill() {
		return skill;
	}

	@Override
	public int getSkillXp() {
		return skillXp;
	}

	@Override
	public float getVanillaXp() {
		return vanillaXp;
	}

	@Override
	public boolean shouldSlotGiveSkillXp() {
		return false;
	}
}
