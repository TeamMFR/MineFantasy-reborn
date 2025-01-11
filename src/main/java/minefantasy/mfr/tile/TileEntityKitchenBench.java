package minefantasy.mfr.tile;

import minefantasy.mfr.config.ConfigHardcore;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.container.ContainerBase;
import minefantasy.mfr.container.ContainerKitchenBench;
import minefantasy.mfr.mechanics.RPGElements;
import minefantasy.mfr.mechanics.knowledge.ResearchLogic;
import minefantasy.mfr.network.NetworkHandler;
import minefantasy.mfr.recipe.CraftingManagerKitchenBench;
import minefantasy.mfr.recipe.IKitchenBench;
import minefantasy.mfr.recipe.IRecipeMFR;
import minefantasy.mfr.recipe.KitchenBenchCraftMatrix;
import minefantasy.mfr.recipe.KitchenBenchRecipeBase;
import minefantasy.mfr.util.ToolHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static minefantasy.mfr.constants.Constants.CRAFTED_BY_NAME_TAG;

public class TileEntityKitchenBench extends TileEntityBase implements IKitchenBench {

	private static final String DIRTY_PROGRESS_TAG = "dirty_progress";

	// the tier of this cooking bench block
	private int tier;
	public static final int WIDTH = 4;
	public static final int HEIGHT = 4;

	public float progressMax;
	public float progress;
	private final float dirtyProgressMax = ConfigHardcore.dirtyProgressMax;
	private float dirtyProgress;
	private ContainerKitchenBench syncKitchenBench;
	private KitchenBenchCraftMatrix craftMatrix;
	private String lastPlayerHit = "";

	public final ItemStackHandler inventory = createInventory();

	public TileEntityKitchenBench() {
		setContainer(new ContainerKitchenBench(this));
	}

	@Override
	protected ItemStackHandler createInventory() {
		return new ItemStackHandler(WIDTH * HEIGHT + 5);
	}

	@Override
	public ItemStackHandler getInventory() {
		return this.inventory;
	}

	@Override
	public ContainerBase createContainer(EntityPlayer player) {
		return new ContainerKitchenBench(player, this);
	}

	@Override
	protected int getGuiId() {
		return NetworkHandler.GUI_KITCHEN_BENCH;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return true;
	}

	public void onInventoryChanged() {
		updateCraftingData();
		IBlockState iblockstate = world.getBlockState(getPos());
		world.notifyBlockUpdate(getPos(), iblockstate, iblockstate, 3);
		markDirty();
	}

	public boolean tryCraft(EntityPlayer user) {
		if (user == null) {
			return false;
		}

		if (getRecipe() == null || !(getRecipe() instanceof KitchenBenchRecipeBase)) {
			return false;
		}

		KitchenBenchRecipeBase kitchenBenchRecipe = (KitchenBenchRecipeBase) getRecipe();

		ItemStack held = user.getHeldItemMainhand();
		Tool tool = ToolHelper.getToolTypeFromStack(held);
		int toolTier = ToolHelper.getCrafterTier(held);
		float efficiency = ToolHelper.getCrafterEfficiency(held);
		if (!(tool == Tool.OTHER)) {
			if (world.isRemote) {
				return true;
			}
			if (!held.isEmpty()) {
				held.damageItem(1, user);
			}

			if (doesPlayerKnowCraft(user)
					&& canCraft(kitchenBenchRecipe)
					&& tool == kitchenBenchRecipe.getToolType()
					&& tier >= kitchenBenchRecipe.getKitchenBenchTier()
					&& toolTier >= kitchenBenchRecipe.getToolTier()) {
				world.playSound(null, pos, getUseSound(kitchenBenchRecipe), SoundCategory.AMBIENT, 1.0F, 1.0F);

				if (user.swingProgress > 0 && user.swingProgress <= 1.0) {
					efficiency *= (0.5F - user.swingProgress);
				}

				progress += Math.max(0.2F, efficiency);
				if (progress >= progressMax) {
					craftItem(user, kitchenBenchRecipe);
				}
			} else {
				if (ToolHelper.isStackValidWashTool(held)) {
					cleanBench(held);
				}
				world.playSound(null, pos, SoundEvents.BLOCK_STONE_STEP, SoundCategory.BLOCKS, 1.25F, 1.5F);
			}
			lastPlayerHit = user.getName();
			updateCraftingData();
			return true;
		}
		updateCraftingData();
		return false;
	}

	public void cleanBench(ItemStack stack) {
		if (dirtyProgress > 0) {
			float efficiency = ToolHelper.getCrafterEfficiency(stack);
			dirtyProgress -= efficiency;
			if (dirtyProgress < 0) {
				dirtyProgress = 0;
			}
			updateCraftingData();
		}
	}

	private SoundEvent getUseSound(KitchenBenchRecipeBase kitchenBenchRecipe) {
		if (kitchenBenchRecipe.getSound().toString().equalsIgnoreCase("engineering")) {
			if (world.rand.nextInt(5) == 0) {
				return SoundEvents.UI_BUTTON_CLICK;
			}
			if (world.rand.nextInt(20) == 0) {
				return SoundEvents.BLOCK_WOODEN_DOOR_OPEN;
			}
			return SoundEvents.BLOCK_WOOD_STEP;
		}
		return kitchenBenchRecipe.getSound();
	}

	private void craftItem(EntityPlayer user, KitchenBenchRecipeBase kitchenBenchRecipe) {
		if (this.canCraft(kitchenBenchRecipe)) {
			addXP(user, kitchenBenchRecipe);
			ItemStack result = kitchenBenchRecipe.getCraftingResult().copy();
			int output = getOutputSlotNum();

			if (this.getInventory().getStackInSlot(output).isEmpty()) {
				if (result.getMaxStackSize() == 1 && !lastPlayerHit.isEmpty()) {
					getNBT(result).setString(CRAFTED_BY_NAME_TAG, lastPlayerHit);
				}
				this.getInventory().setStackInSlot(output, result);
			} else if (this.getInventory().getStackInSlot(output).getItem() == result.getItem()) {
				this.getInventory().getStackInSlot(output).grow(result.getCount()); // Forge BugFix: Results may have multiple items
			}
			consumeResources(user);
			float currentLevel = RPGElements.getLevel(user, Skill.PROVISIONING);
			// More provisioning skill means less mess.
			float skillMod = ((currentLevel / Skill.PROVISIONING.getMaxLevel()) / ConfigHardcore.dirtyProgressSkillModifier);
			float dirtyProgressAmountMod = kitchenBenchRecipe.getDirtyProgressAmount()
					- (kitchenBenchRecipe.getDirtyProgressAmount() * skillMod);
			addDirtyProgress(dirtyProgressAmountMod);
		}
		onInventoryChanged();
		progress = 0;
	}

	public int getOutputSlotNum() {
		return getInventory().getSlots() - 5;
	}

	private NBTTagCompound getNBT(ItemStack item) {
		if (!item.hasTagCompound()) {
			item.setTagCompound(new NBTTagCompound());
		}
		return item.getTagCompound();
	}

	public void consumeResources(EntityPlayer player) {
		for (int slot = 0; slot < getOutputSlotNum(); slot++) {
			ItemStack item = getInventory().getStackInSlot(slot);
			if (!item.isEmpty() && !item.getItem().getContainerItem(item).isEmpty()) {
				if (item.getCount() == 1) {
					getInventory().setStackInSlot(slot, item.getItem().getContainerItem(item));
				} else {
					ItemStack drop = processSurplus(item.getItem().getContainerItem(item));
					if (!drop.isEmpty() && player != null) {
						EntityItem entityItem = player.dropItem(drop, false);
						if (entityItem != null){
							entityItem.setPosition(player.posX, player.posY, player.posZ);
							entityItem.setNoPickupDelay();
						}
					}
					this.getInventory().extractItem(slot, 1, false);
				}
			} else {
				this.getInventory().extractItem(slot, 1, false);
			}
		}
		this.onInventoryChanged();
	}

	private ItemStack processSurplus(ItemStack item) {
		for (int a = 0; a < 4; a++) {
			if (item.isEmpty()) {
				return ItemStack.EMPTY;// If item was sorted
			}

			int slot = getInventory().getSlots() - 4 + a;
			ItemStack stackInSlot = getInventory().getStackInSlot(slot);
			if (stackInSlot.isEmpty()) {
				getInventory().setStackInSlot(slot, item);
				return ItemStack.EMPTY;// All Placed
			} else {
				if (stackInSlot.isItemEqual(item) && stackInSlot.getCount() < stackInSlot.getMaxStackSize()) {
					if (stackInSlot.getCount() + item.getCount() <= stackInSlot.getMaxStackSize()) {
						stackInSlot.grow(item.getCount());
						return ItemStack.EMPTY;// All Shared
					} else {
						int room_left = stackInSlot.getMaxStackSize() - stackInSlot.getCount();
						stackInSlot.grow(room_left);
						item.shrink(room_left);// Share
					}
				}
			}
		}
		return item;
	}

	private boolean canFitResult(ItemStack result) {
		ItemStack resSlot = getInventory().getStackInSlot(getOutputSlotNum());
		if (!resSlot.isEmpty() && !result.isEmpty()) {
			if (!resSlot.isItemEqual(result)) {
				return false;
			}
			if (resSlot.getCount() + result.getCount() > resSlot.getMaxStackSize()) {
				return false;
			}
		}
		return true;
	}

	// CRAFTING CODE
	public KitchenBenchRecipeBase getResult() {
		if (syncKitchenBench == null || craftMatrix == null) {
			return null;
		}

		for (int a = 0; a < getOutputSlotNum(); a++) {
			craftMatrix.setInventorySlotContents(a, getInventory().getStackInSlot(a));
		}

		return CraftingManagerKitchenBench.findMatchingRecipe(this, craftMatrix, world);
	}

	public String getResultName() {
		if (!(getRecipe() instanceof KitchenBenchRecipeBase)) {
			return I18n.format("gui.no_project_set");
		}
		else {
			KitchenBenchRecipeBase kitchenBenchRecipe = (KitchenBenchRecipeBase) getRecipe();
			return kitchenBenchRecipe.getCraftingResult().getDisplayName();
		}
	}

	public void updateCraftingData() {
		if (!world.isRemote && (getRecipe() instanceof KitchenBenchRecipeBase || getRecipe() == null)) {
			KitchenBenchRecipeBase oldRecipe = (KitchenBenchRecipeBase) getRecipe();
			KitchenBenchRecipeBase newRecipe = getResult();
			setRecipe(newRecipe);
			// syncItems();

			if (!canCraft(newRecipe) && progress > 0) {
				progress = 0;
				// quality = 100;
			}
			if (newRecipe != null && oldRecipe != null && !newRecipe.equals(oldRecipe)) {
				progress = 0;
			}
			if (progress > progressMax)
				progress = progressMax - 1;
		}
	}

	public boolean canCraft(KitchenBenchRecipeBase kitchenBenchRecipe) {
		if (kitchenBenchRecipe == null) {
			return false;
		}
		if (dirtyProgress >= dirtyProgressMax) {
			return false;
		}
		if (progressMax > 0) {
			return this.canFitResult(kitchenBenchRecipe.getCraftingResult());
		}
		return false;
	}

	@Override
	public void setProgressMax(int i) {
		progressMax = i;
	}

	public void setContainer(ContainerKitchenBench container) {
		syncKitchenBench = container;
		craftMatrix = new KitchenBenchCraftMatrix(this, syncKitchenBench, KitchenBenchRecipeBase.MAX_WIDTH, KitchenBenchRecipeBase.MAX_HEIGHT);
	}

	public int getProgressBar(int i) {
		return (int) Math.ceil(i / progressMax * progress);
	}

	public int getDirtyProgressBar(int i) {
		return (int) Math.ceil(i / dirtyProgressMax * dirtyProgress);
	}

	public float getDirtyProgress() {
		return dirtyProgress;
	}

	public void addDirtyProgress(float amount) {
		dirtyProgress += amount;
	}

	public float getDirtyProgressMax() {
		return dirtyProgressMax;
	}

	public boolean doesPlayerKnowCraft(EntityPlayer user) {
		IRecipeMFR recipe = getRecipe();
		if (!(recipe instanceof KitchenBenchRecipeBase)
				|| (recipe.getRequiredResearch().equals("none"))) {
			return true;
		}
		return ResearchLogic.getResearchCheck(user, ResearchLogic.getResearch(recipe.getRequiredResearch()));
	}

	private void addXP(EntityPlayer smith, KitchenBenchRecipeBase kitchenBenchRecipe) {
		if (!world.isRemote) {
			if (kitchenBenchRecipe.getSkill() != Skill.NONE) {
				float baseXP = this.progressMax / 10F;
				kitchenBenchRecipe.giveSkillXp(smith, (int) baseXP + 1);
				kitchenBenchRecipe.giveVanillaXp(smith, baseXP, 1);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		tier = nbt.getInteger(TIER_TAG);
		inventory.deserializeNBT(nbt.getCompoundTag(INVENTORY_TAG));
		progress = nbt.getFloat(PROGRESS_TAG);
		progressMax = nbt.getFloat(PROGRESS_MAX_TAG);
		dirtyProgress = nbt.getFloat(DIRTY_PROGRESS_TAG);
		ResourceLocation resourceLocation = new ResourceLocation(nbt.getString(RECIPE_RESOURCE_LOCATION_TAG));
		this.setRecipe(CraftingManagerKitchenBench.getRecipeByResourceLocation(resourceLocation));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger(TIER_TAG, tier);
		nbt.setTag(INVENTORY_TAG, inventory.serializeNBT());
		nbt.setFloat(PROGRESS_TAG, progress);
		nbt.setFloat(PROGRESS_MAX_TAG, progressMax);
		nbt.setFloat(DIRTY_PROGRESS_TAG, dirtyProgress);
		if (getRecipe() != null) {
			nbt.setString(RECIPE_RESOURCE_LOCATION_TAG, getRecipe().getResourceLocation());
		}
		else {
			nbt.setString(RECIPE_RESOURCE_LOCATION_TAG, "");
		}
		return nbt;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
		}
		return super.getCapability(capability, facing);
	}
}
