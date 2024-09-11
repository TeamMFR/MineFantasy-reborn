package minefantasy.mfr.tile.blastfurnace;

import minefantasy.mfr.api.crafting.MineFantasyCarbons;
import minefantasy.mfr.api.refine.ISmokeCarrier;
import minefantasy.mfr.api.refine.SmokeMechanics;
import minefantasy.mfr.container.ContainerBase;
import minefantasy.mfr.container.ContainerBlastChamber;
import minefantasy.mfr.init.MineFantasyBlocks;
import minefantasy.mfr.network.NetworkHandler;
import minefantasy.mfr.recipe.BlastFurnaceRecipeBase;
import minefantasy.mfr.recipe.CraftingManagerBlastFurnace;
import minefantasy.mfr.tile.TileEntityBase;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.MFRLogUtil;
import minefantasy.mfr.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TileEntityBlastChamber extends TileEntityBase implements ITickable, ISmokeCarrier {
	protected int ticksExisted;
	private boolean contentsChanged;
	protected boolean isBuilt = false;
	protected int fireTime;
	protected int tempUses;
	protected int smokeStorage;
	protected Random rand = new Random();
	private Set<String> knownResearches = new HashSet<>();

	public final ItemStackHandler inventory = createInventory();

	@Override
	protected ItemStackHandler createInventory() {
		return new ItemStackHandler(2) {
			@Override
			protected void onContentsChanged(int slot) {
				if (!world.isRemote) {
					contentsChanged = true;
				}
			}
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (isItemValidForSlot(slot, stack)) {
					return super.insertItem(slot, stack, simulate);
				}
				else {
					return stack;
				}
			}
		};
	}

	@Override
	public ItemStackHandler getInventory() {
		return this.inventory;
	}

	@Override
	public ContainerBase createContainer(EntityPlayer player) {
		return new ContainerBlastChamber(player.inventory, this);
	}

	@Override
	protected int getGuiId() {
		return NetworkHandler.GUI_BLAST_CHAMBER;
	}

	public boolean isInput(ItemStack item) {
		return !getResult(item).isEmpty();
	}

	protected ItemStack getResult(ItemStack input) {
		BlastFurnaceRecipeBase recipe = CraftingManagerBlastFurnace.findMatchingRecipe(input, this.knownResearches);
		if (recipe != null) {
			setRecipe(recipe);
			return recipe.getBlastFurnaceRecipeOutput().copy();
		}
		else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void update() {

		++ticksExisted;

		if (!world.isRemote && contentsChanged) {
			this.contentsChanged = false;
			TileEntity neighbour = world.getTileEntity(pos.add(0, -1, 0));
			if (neighbour instanceof TileEntityBlastChamber) {
				((TileEntityBlastChamber) neighbour).setKnownResearches(this.knownResearches);
				if (!(neighbour instanceof TileEntityBlastHeater)) {
					interact((TileEntityBlastChamber) neighbour);
				}
			}
		}

		if (ticksExisted % 200 == 0) {
			updateBuild();
		}
		if (smokeStorage > 0) {
			SmokeMechanics.emitSmokeFromCarrier(world, pos, this, 5);
		}
		if (!world.isRemote && smokeStorage > getMaxSmokeStorage() && rand.nextInt(1000) == 0) {
			world.newExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 5F, true, true);
		}
	}

	protected void interact(TileEntityBlastChamber tile) {
		if (!(tile instanceof TileEntityBlastHeater)) {
			if (!tile.isBuilt) {
				return;
			}

			for (int a = 0; a < getInventory().getSlots(); a++) {
				ItemStack mySlot = getInventory().getStackInSlot(a);

				if (!mySlot.isEmpty() && canShare(mySlot, a)) {
					ItemStack theirSlot = tile.getInventory().getStackInSlot(a);
					if (theirSlot.isEmpty()) {
						ItemStack copy = mySlot.copy();
						copy.setCount(1);
						tile.getInventory().setStackInSlot(a, copy);
						getInventory().extractItem(a, 1, false);
					} else if (CustomToolHelper.areEqual(theirSlot, mySlot)) {
						if ((theirSlot.getCount()) < getMaxStackSizeForDistribute()) {
							theirSlot.grow(1);
							getInventory().extractItem(a, 1, false);
							tile.getInventory().setStackInSlot(a, theirSlot);
						}
					}
				}
			}
		}
	}

	public void setKnownResearches(Set<String> knownResearches) {
		this.knownResearches = knownResearches;
		this.contentsChanged = true;
	}

	private boolean canShare(ItemStack mySlot, int a) {
		if (a == 1)
			return isInput(mySlot);
		return MineFantasyCarbons.isCarbon(mySlot);
	}

	public void updateBuild() {
		isBuilt = getIsBuilt();
	}

	protected boolean getIsBuilt() {
		return (isFirebrick(-1, 0) && isFirebrick(1, 0) && isFirebrick(0, -1) && isFirebrick(0, 1));
	}

	protected boolean isFirebrick(int x, int z) {
		Block block = world.getBlockState(pos.add(x, 0, z)).getBlock();
		return block == MineFantasyBlocks.FIREBRICKS;
	}

	protected boolean isAir(int x, int z) {
		return !world.isBlockNormalCube(pos.add(x, 0, z), false);
	}

	private int getMaxStackSizeForDistribute() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		if (slot == 0) {
			return MineFantasyCarbons.isCarbon(item);
		}
		return isInput(item);
	}

	@Override
	public int getSmokeValue() {
		return smokeStorage;
	}

	@Override
	public void setSmokeValue(int smoke) {
		smokeStorage = smoke;
	}

	@Override
	public int getMaxSmokeStorage() {
		return 10;
	}

	@Override
	public boolean canAbsorbIndirect() {
		return false;
	}

	public boolean shouldRemoveCarbon() {
		if (tempUses > 0) {
			--tempUses;
			MFRLogUtil.logDebug("Decr Carbon Uses: " + tempUses);
			return false;
		} else {
			int carb = MineFantasyCarbons.getCarbon(getInventory().getStackInSlot(0)) - 1;
			if (carb > 0) {
				tempUses = carb;
				MFRLogUtil.logDebug("Set Carbon Uses: " + tempUses);
			}
			return true;
		}
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("fireTime", fireTime);
		nbt.setBoolean("isBuilt", isBuilt);
		nbt.setInteger("ticksExisted", ticksExisted);
		nbt.setInteger("StoredSmoke", smokeStorage);

		nbt.setTag("inventory", inventory.serializeNBT());

		if (getRecipe() != null) {
			nbt.setString(RECIPE_RESOURCE_LOCATION_TAG, getRecipe().getResourceLocation());
		}

		nbt.setString(KNOWN_RESEARCHES_TAG, Utils.serializeList(knownResearches));

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		fireTime = nbt.getInteger("fireTime");
		isBuilt = nbt.getBoolean("isBuilt");
		ticksExisted = nbt.getInteger("ticksExisted");
		smokeStorage = nbt.getInteger("StoredSmoke");

		inventory.deserializeNBT(nbt.getCompoundTag("inventory"));

		if (!nbt.getString(RECIPE_RESOURCE_LOCATION_TAG).isEmpty()) {
			ResourceLocation resourceLocation = new ResourceLocation(nbt.getString(RECIPE_RESOURCE_LOCATION_TAG));
			this.setRecipe(CraftingManagerBlastFurnace.getRecipeByResourceLocation(resourceLocation));
		}

		knownResearches = Utils.deserializeList(nbt.getString(KNOWN_RESEARCHES_TAG));
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