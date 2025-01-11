package minefantasy.mfr.tile;

import minefantasy.mfr.api.crafting.IHeatUser;
import minefantasy.mfr.api.refine.SmokeMechanics;
import minefantasy.mfr.block.BlockCrucible;
import minefantasy.mfr.config.ConfigHardcore;
import minefantasy.mfr.container.ContainerBase;
import minefantasy.mfr.container.ContainerCrucible;
import minefantasy.mfr.init.MineFantasyBlocks;
import minefantasy.mfr.network.NetworkHandler;
import minefantasy.mfr.recipe.AlloyRecipeBase;
import minefantasy.mfr.recipe.CraftingManagerAlloy;
import minefantasy.mfr.recipe.CraftingManagerBlastFurnace;
import minefantasy.mfr.recipe.CrucibleCraftMatrix;
import minefantasy.mfr.recipe.IRecipeMFR;
import minefantasy.mfr.tile.blastfurnace.TileEntityBlastHeater;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.InventoryUtils;
import minefantasy.mfr.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TileEntityCrucible extends TileEntityBase implements IHeatUser, ITickable {
	private int progress = 0;
	private int progressMax = 400;
	private int temperature;
	private final Random rand = new Random();

	private final int OUT_SLOT = 9;

	private ContainerCrucible syncCrucible;
	private CrucibleCraftMatrix craftMatrix;

	public final ItemStackHandler inventory = createInventory();

	private Set<String> knownResearches = new HashSet<>();

	public TileEntityCrucible() {
		setContainer(new ContainerCrucible(this));
	}

	@Override
	protected ItemStackHandler createInventory() {
		return new ItemStackHandler(10) {
			@Override
			protected void onContentsChanged(int slot) {
				if (slot == OUT_SLOT) {
					ItemStack output = this.getStackInSlot(slot);
					IRecipeMFR recipe = CraftingManagerAlloy.findRecipeByOutput(output);
					if (recipe == null) {
						recipe = CraftingManagerBlastFurnace.findRecipeByOutput(output);
					}
					if (recipe != null) {
						setRecipe(recipe);
					}
				}
			}
		};
	}

	@Override
	public ItemStackHandler getInventory() {
		return this.inventory;
	}

	@Override
	public ContainerBase createContainer(final EntityPlayer player) {
		return new ContainerCrucible(player, player.inventory, this);
	}

	public void setContainer(ContainerCrucible container) {
		syncCrucible = container;
		craftMatrix = new CrucibleCraftMatrix(syncCrucible, AlloyRecipeBase.MAX_WIDTH, AlloyRecipeBase.MAX_HEIGHT);
	}

	@Override
	protected int getGuiId() {
		return NetworkHandler.GUI_CRUCIBLE;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void update() {
		boolean isHot = getIsHot();
		temperature = getTemperature();

		if (getTier() >= 2) {
			progressMax = 2000;
		}

		/*
		 * int time = 400; for(int a = 1; a < getSizeInventory()-1; a ++) { if(inv[a] !=
		 * null) { time += 50; } } if(!worldObj.isRemote) { progressMax = time; }
		 */

		if (isHot && canSmelt()) {
			progress += Math.max(1, Math.round(temperature / 600F));
			if (progress >= progressMax) {
				progress = 0;
				smeltItem();
				if (isAuto()) {
					onAutoSmelt();
				}
			}
		} else {
			progress = 0;
		}
		if (progress > 0 && rand.nextInt(4) == 0 && !isOutside() && this.getTier() < 2) {
			SmokeMechanics.emitSmokeIndirect(world, getPos(), 1);
		}
	}

	public boolean getIsHot() {
		if (this.getTier() >= 2) {
			return this.isCoated();
		}
		return getTemperature() > 0;
	}

	private void onAutoSmelt() {
		world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
		world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	private boolean isOutside() {
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				if (!world.canBlockSeeSky(new BlockPos(pos.getX() + x, pos.getY() + 1, pos.getZ() + y))) {
					return false;
				}
			}
		}
		return true;
	}

	public void smeltItem() {
		if (!canSmelt()) {
			return;
		}

		ItemStack itemstack = getResult();

		if (inventory.getStackInSlot(OUT_SLOT).isEmpty()) {
			inventory.setStackInSlot(OUT_SLOT, itemstack.copy());
		} else if (CustomToolHelper.areEqual(inventory.getStackInSlot(OUT_SLOT), itemstack)) {
			inventory.getStackInSlot(OUT_SLOT).grow(itemstack.getCount());
		}

		for (int a = 0; a < (inventory.getSlots() - 1); a++) {
			if (!inventory.getStackInSlot(a).isEmpty()) {
				inventory.getStackInSlot(a).shrink(1);
				if (inventory.getStackInSlot(a).getCount() <= 0) {
					inventory.setStackInSlot(a, ItemStack.EMPTY);
				}
			}
		}
		if (world.isRemote && getTier() >= 2) {
			spawnParticle(-3, 0, 0);
			spawnParticle(3, 0, 0);
			spawnParticle(0, 0, -3);
			spawnParticle(0, 0, 3);
		}
	}

	private void spawnParticle(int x, int y, int z) {
		world.playEvent(2003, pos.add(x, y, z), 0);
	}

	private boolean canSmelt() {
		if (temperature <= 0) {
			return false;
		}

		ItemStack result = getResult();

		if (result.isEmpty()) {
			return false;
		}

		if (inventory.getStackInSlot(OUT_SLOT).isEmpty())
			return true;
		return !inventory.getStackInSlot(OUT_SLOT).isEmpty() && CustomToolHelper.areEqual(inventory.getStackInSlot(OUT_SLOT), result)
				&& inventory.getStackInSlot(OUT_SLOT).getCount() < (inventory.getStackInSlot(OUT_SLOT).getMaxStackSize() - (result.getCount() - 1));
	}

	private ItemStack getResult() {
		if (isBlastOutput()) {
			return ItemStack.EMPTY;
		}

		if (syncCrucible == null || craftMatrix == null) {
			return ItemStack.EMPTY;
		}

		for (int a = 0; a < OUT_SLOT; a++) {
			craftMatrix.setInventorySlotContents(a, getInventory().getStackInSlot(a));
		}

		AlloyRecipeBase recipe = CraftingManagerAlloy.findMatchingRecipe(this, craftMatrix, knownResearches);
		if (recipe != null) {
			return recipe.getCraftingResult(craftMatrix);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public IRecipeMFR getRecipeByOutput(ItemStack stack) {
		return CraftingManagerAlloy.findRecipeByOutput(stack);
	}

	public int getTier() {
		if (this.getBlockType() != null && this.getBlockType() instanceof BlockCrucible) {
			return ((BlockCrucible) this.getBlockType()).tier;
		}
		return 0;
	}

	public boolean isAuto() {
		if (this.getBlockType() != null && this.getBlockType() instanceof BlockCrucible) {
			return ((BlockCrucible) this.getBlockType()).isAuto;
		}
		return false;
	}

	public int getTemperature() {
		if (this.getTier() >= 1 && !isCoated()) {
			return 0;
		}
		if (getTier() >= 2) {
			return 500;
		}
		IBlockState under = world.getBlockState(pos.add(0, -1, 0));

		if (under.getMaterial() == Material.FIRE) {
			return 10;
		}
		if (under.getMaterial() == Material.LAVA) {
			return 50;
		}
		TileEntity tile = world.getTileEntity(pos.add(0, -1, 0));
		if (tile instanceof TileEntityForge) {
			return Math.round(((TileEntityForge) tile).getBlockTemperature());
		}
		return 0;
	}

	public int getProgress() {
		return progress;
	}

	public int getProgressMax() {
		return progressMax;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void setProgressMax(int progressMax) {
		this.progressMax = progressMax;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public boolean isCoated() {
		if (this.getTier() >= 2) {
			return isEnderAltar(-1, -1, -3) && isEnderAltar(-1, -1, 3) && isEnderAltar(-3, -1, -1)
					&& isEnderAltar(3, -1, -1) && isEnderAltar(1, -1, -3) && isEnderAltar(1, -1, 3)
					&& isEnderAltar(-3, -1, 1) && isEnderAltar(3, -1, 1);
		}
		return isFirebrick(0, 0, -1) && isFirebrick(0, 0, 1) && isFirebrick(-1, 0, 0) && isFirebrick(1, 0, 0);
	}

	private boolean isFirebrick(int x, int y, int z) {
		Block block = world.getBlockState(pos.add(x, y, z)).getBlock();
		return block == MineFantasyBlocks.FIREBRICKS || block == MineFantasyBlocks.FIREBRICK_STAIRS;
	}
	// INVENTORY

	private boolean isEnderAltar(int x, int y, int z) {
		IBlockState state = world.getBlockState(pos.add(x, y, z));
		Block block = state.getBlock();
		return block == Blocks.END_PORTAL_FRAME && state.getValue(BlockEndPortalFrame.EYE);
	}

	private boolean isBlastOutput() {
		if (world.isRemote)
			return false;
		TileEntity tile = world.getTileEntity(pos.add(0, 1, 0));
		return tile instanceof TileEntityBlastHeater;
	}

	public void setKnownResearches(Set<String> knownResearches) {
		this.knownResearches = knownResearches;
	}

	@Override
	public void onBlockBreak() {
		if (!inventory.getStackInSlot(inventory.getSlots() - 1).isEmpty() && !(inventory.getStackInSlot(inventory.getSlots() - 1).getItem() instanceof ItemBlock)
				&& ConfigHardcore.HCCreduceIngots && !isAuto()) {
			inventory.setStackInSlot(OUT_SLOT, ItemStack.EMPTY);
		}
		InventoryUtils.dropItemsInWorld(world, inventory, pos);
	}

	@Override
	public boolean canAccept(TileEntity tile) {
		return tile instanceof TileEntityForge;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		progress = nbt.getInteger("progress");
		progressMax = nbt.getInteger("progressMax");

		inventory.deserializeNBT(nbt.getCompoundTag("inventory"));

		this.setRecipe(getRecipeByResourceLocation(nbt));
		knownResearches = Utils.deserializeList(nbt.getString(KNOWN_RESEARCHES_TAG));
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("progress", progress);
		nbt.setInteger("progressMax", progressMax);

		nbt.setTag("inventory", inventory.serializeNBT());

		if (getRecipe() != null) {
			nbt.setString(RECIPE_RESOURCE_LOCATION_TAG, getRecipe().getResourceLocation());
		}
		else {
			nbt.setString(RECIPE_RESOURCE_LOCATION_TAG, "");
		}
		nbt.setString(KNOWN_RESEARCHES_TAG, Utils.serializeList(knownResearches));

		return nbt;
	}

	private static IRecipeMFR getRecipeByResourceLocation(NBTTagCompound nbt) {
		ResourceLocation recipeResourceLocation = new ResourceLocation(nbt.getString(RECIPE_RESOURCE_LOCATION_TAG));
		IRecipeMFR recipe;

		recipe = CraftingManagerAlloy.getRecipeByResourceLocation(recipeResourceLocation);
		if (recipe == null) {
			recipe = CraftingManagerBlastFurnace.getRecipeByResourceLocation(recipeResourceLocation);
		}

		return recipe;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getInventory());
		}
		return super.getCapability(capability, facing);
	}
}

