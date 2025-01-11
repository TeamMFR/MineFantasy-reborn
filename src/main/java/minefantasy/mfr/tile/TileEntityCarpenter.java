package minefantasy.mfr.tile;

import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.container.ContainerBase;
import minefantasy.mfr.container.ContainerCarpenter;
import minefantasy.mfr.item.ItemArmourMFR;
import minefantasy.mfr.mechanics.knowledge.ResearchLogic;
import minefantasy.mfr.network.NetworkHandler;
import minefantasy.mfr.recipe.CarpenterCraftMatrix;
import minefantasy.mfr.recipe.CarpenterRecipeBase;
import minefantasy.mfr.recipe.CraftingManagerCarpenter;
import minefantasy.mfr.recipe.ICarpenter;
import minefantasy.mfr.recipe.IRecipeMFR;
import minefantasy.mfr.util.ToolHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
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

public class TileEntityCarpenter extends TileEntityBase implements ICarpenter {

	private static final String TOOL_TIER_REQUIRED_TAG = "tool_tier_required";

	// the tier of this carpenter block
	private int tier;
	public static final int WIDTH = 4;
	public static final int HEIGHT = 4;

	public float progressMax;
	public float progress;
	private ContainerCarpenter syncCarpenter;
	private CarpenterCraftMatrix craftMatrix;
	private ItemStack resultStack = ItemStack.EMPTY;
	private String lastPlayerHit = "";
	private int requiredToolTier;
	private int requiredCarpenterTier;

	public final ItemStackHandler inventory = createInventory();

	public TileEntityCarpenter() {
		setContainer(new ContainerCarpenter(this));
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
		return new ContainerCarpenter(player, this);
	}

	@Override
	protected int getGuiId() {
		return NetworkHandler.GUI_CARPENTER_BENCH;
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

		if (getRecipe() == null || !(getRecipe() instanceof CarpenterRecipeBase)) {
			return false;
		}

		CarpenterRecipeBase carpenterRecipe = (CarpenterRecipeBase) getRecipe();

		Tool tool = ToolHelper.getToolTypeFromStack(user.getHeldItemMainhand());
		int toolTier = ToolHelper.getCrafterTier(user.getHeldItemMainhand());
		if (!(tool == Tool.OTHER)) {
			if (!user.getHeldItemMainhand().isEmpty()) {
				user.getHeldItemMainhand().damageItem(1, user);
				if (user.getHeldItemMainhand().getItemDamage() >= user.getHeldItemMainhand().getMaxDamage()) {
					if (world.isRemote)
						user.renderBrokenItemStack(user.getHeldItemMainhand());
					user.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
				}
			}
			if (world.isRemote) {
				return true;
			}

			if (doesPlayerKnowCraft(user)
					&& canCraft(carpenterRecipe)
					&& tool == carpenterRecipe.getToolType()
					&& tier >= requiredCarpenterTier
					&& toolTier >= requiredToolTier) {
				world.playSound(null, pos, getUseSound(carpenterRecipe), SoundCategory.AMBIENT, 1.0F, 1.0F);
				float efficiency = ToolHelper.getCrafterEfficiency(user.getHeldItemMainhand());

				if (user.swingProgress > 0 && user.swingProgress <= 1.0) {
					efficiency *= (0.5F - user.swingProgress);
				}

				progress += Math.max(0.2F, efficiency);
				if (progress >= progressMax) {
					craftItem(user, carpenterRecipe);
				}
			} else {
				world.playSound(null, pos, SoundEvents.BLOCK_STONE_STEP, SoundCategory.BLOCKS, 1.25F, 1.5F);
			}
			lastPlayerHit = user.getName();
			updateCraftingData();
			return true;
		}
		updateCraftingData();
		return false;
	}

	private SoundEvent getUseSound(CarpenterRecipeBase recipe) {
		if (recipe.getSound() != null) {
			return recipe.getSound();
		}
		else {
			return SoundEvents.BLOCK_WOOD_HIT;
		}
	}

	private void craftItem(EntityPlayer user, CarpenterRecipeBase carpenterRecipe) {
		if (this.canCraft(carpenterRecipe)) {
			addXP(user, carpenterRecipe);

			ItemStack result = carpenterRecipe.getCraftingResult(craftMatrix).copy();
			if (!result.isEmpty() && result.getItem() instanceof ItemArmourMFR) {
				result = modifyArmour(result);
			}
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
		}
		onInventoryChanged();
		progress = 0;
	}

	public int getOutputSlotNum() {
		return getInventory().getSlots() - 5;
	}

	private ItemStack modifyArmour(ItemStack result) {
		ItemArmourMFR item = (ItemArmourMFR) result.getItem();
		boolean canColour = item.canColour();
		int colour = -1;
		for (int a = 0; a < getOutputSlotNum(); a++) {
			ItemStack slot = getInventory().getStackInSlot(a);
			if (!slot.isEmpty() && slot.getItem() instanceof ItemArmor) {
				ItemArmor slotItem = (ItemArmor) slot.getItem();
				if (canColour && slotItem.hasColor(slot)) {
					colour = slotItem.getColor(slot);
				}
				if (result.isItemStackDamageable()) {
					result.setItemDamage(slot.getItemDamage());
				}
			}
		}
		if (colour != -1 && canColour) {
			item.setColor(result, colour);
		}
		return result;
	}

	private NBTTagCompound getNBT(ItemStack item) {
		if (!item.hasTagCompound()) {
			item.setTagCompound(new NBTTagCompound());
		}
		return item.getTagCompound();
	}

	public int getToolTierNeeded() {
		return this.requiredToolTier;
	}

	public int getCarpenterTierNeeded() {
		return this.requiredCarpenterTier;
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
	private CarpenterRecipeBase getResult() {
		if (syncCarpenter == null || craftMatrix == null) {
			return null;
		}

		for (int a = 0; a < getOutputSlotNum(); a++) {
			craftMatrix.setInventorySlotContents(a, getInventory().getStackInSlot(a));
		}

		CarpenterRecipeBase recipe = CraftingManagerCarpenter.findMatchingRecipe(this, craftMatrix, world);

		resultStack = recipe != null
				? recipe.getCraftingResult(craftMatrix)
				: ItemStack.EMPTY;

		return recipe;
	}

	public String getResultName() {
		return resultStack.isEmpty() || resultStack.getItem() == Item.getItemFromBlock(Blocks.AIR)
				? I18n.format("gui.no_project_set")
				: resultStack.getDisplayName();
	}

	public void updateCraftingData() {
		if (!world.isRemote && (getRecipe() instanceof CarpenterRecipeBase || getRecipe() == null)) {
			CarpenterRecipeBase oldRecipe = (CarpenterRecipeBase) getRecipe();
			CarpenterRecipeBase newRecipe = getResult();
			setRecipe(newRecipe);

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

	public boolean canCraft(CarpenterRecipeBase carpenterRecipe) {
		if (progressMax > 0 && carpenterRecipe != null) {
			return this.canFitResult(carpenterRecipe.getCraftingResult(craftMatrix));
		}
		return false;
	}

	@Override
	public void setProgressMax(int i) {
		progressMax = i;
	}

	@Override
	public void setRequiredToolTier(int i) {
		requiredToolTier = i;
	}

	@Override
	public void setRequiredCarpenterTier(int i) {
		requiredCarpenterTier = i;
	}

	public void setContainer(ContainerCarpenter container) {
		syncCarpenter = container;
		craftMatrix = new CarpenterCraftMatrix(this, syncCarpenter, CarpenterRecipeBase.MAX_WIDTH, CarpenterRecipeBase.MAX_HEIGHT);
	}

	public int getProgressBar(int i) {
		return (int) Math.ceil(i / progressMax * progress);
	}

	public boolean doesPlayerKnowCraft(EntityPlayer user) {
		IRecipeMFR recipe = getRecipe();
		if (!(recipe instanceof CarpenterRecipeBase)
				|| recipe.getRequiredResearch().equals("none")) {
			return true;
		}
		return ResearchLogic.getResearchCheck(user, ResearchLogic.getResearch(recipe.getRequiredResearch()));
	}

	private void addXP(EntityPlayer player, CarpenterRecipeBase carpenterRecipe) {
		if (carpenterRecipe.getSkill() != Skill.NONE) {
			float baseXP = this.progressMax / 10F;
			carpenterRecipe.giveSkillXp(player, baseXP + 1);
			carpenterRecipe.giveVanillaXp(player, baseXP, 1);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		tier = nbt.getInteger(TIER_TAG);
		inventory.deserializeNBT(nbt.getCompoundTag(INVENTORY_TAG));
		progress = nbt.getFloat(PROGRESS_TAG);
		progressMax = nbt.getFloat(PROGRESS_MAX_TAG);
		requiredToolTier = nbt.getInteger(TOOL_TIER_REQUIRED_TAG);
		ResourceLocation resourceLocation = new ResourceLocation(nbt.getString(RECIPE_RESOURCE_LOCATION_TAG));
		this.setRecipe(CraftingManagerCarpenter.getRecipeByResourceLocation(resourceLocation));
		resultStack = new ItemStack(nbt.getCompoundTag(RESULT_STACK_TAG));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger(TIER_TAG, tier);
		nbt.setTag(INVENTORY_TAG, inventory.serializeNBT());
		nbt.setFloat(PROGRESS_TAG, progress);
		nbt.setFloat(PROGRESS_MAX_TAG, progressMax);
		nbt.setInteger(TOOL_TIER_REQUIRED_TAG, requiredToolTier);
		if (getRecipe() != null) {
			nbt.setString(RECIPE_RESOURCE_LOCATION_TAG, getRecipe().getResourceLocation());
		}
		else {
			nbt.setString(RECIPE_RESOURCE_LOCATION_TAG, "");
		}
		nbt.setTag(RESULT_STACK_TAG, resultStack.writeToNBT(new NBTTagCompound()));
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
