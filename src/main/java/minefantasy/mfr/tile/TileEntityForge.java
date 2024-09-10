package minefantasy.mfr.tile;

import minefantasy.mfr.api.crafting.IBasicMetre;
import minefantasy.mfr.api.crafting.IHeatSource;
import minefantasy.mfr.api.crafting.IHeatUser;
import minefantasy.mfr.api.heating.ForgeFuel;
import minefantasy.mfr.api.heating.Heatable;
import minefantasy.mfr.api.refine.IBellowsUseable;
import minefantasy.mfr.api.refine.SmokeMechanics;
import minefantasy.mfr.block.BlockForge;
import minefantasy.mfr.container.ContainerBase;
import minefantasy.mfr.container.ContainerForge;
import minefantasy.mfr.item.ItemHeated;
import minefantasy.mfr.network.NetworkHandler;
import minefantasy.mfr.registry.ForgeFuelRegistry;
import minefantasy.mfr.util.Functions;
import minefantasy.mfr.util.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class TileEntityForge extends TileEntityBase implements IBasicMetre, IHeatSource, IBellowsUseable, ITickable {
	public static final float maxTemperature = 5000;
	public float fuel;
	public float maxFuel = 6000;// 5m
	public float temperature;
	public float fuelTemperature;
	public int workableState = 0;
	public int exactTemperature;
	int justShared;
	private boolean isLit;
	private final Random rand = new Random();
	private int ticksExisted;

	public final ItemStackHandler inventory = createInventory();
	public final ItemStackHandler fuelInventory = createFuelInventory();

	@Override
	protected ItemStackHandler createInventory() {
		return new ItemStackHandler(1);
	}

	protected ItemStackHandler createFuelInventory() {
		return new ItemStackHandler(1) {

			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (isItemValidForSlot(slot, stack)) {
					return super.insertItem(slot, stack, simulate);
				}
				else {
					return stack;
				}
			}

			@Override
			protected void onContentsChanged(int slot) {
				ItemStack fuelStack = getStackInSlot(slot);
				ForgeFuel stats = ForgeFuelRegistry.getFuelStats(fuelStack);
				if (stats != null) {
					if (addFuel(stats, false)) {
						if (fuelStack != ItemStack.EMPTY) {
							setStackInSlot(0, ItemStack.EMPTY);
						}
					}
				}
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}
		};
	}

	@Override
	public ItemStackHandler getInventory() {
		return this.inventory;
	}

	@Override
	public ContainerBase createContainer(EntityPlayer player) {
		return new ContainerForge(player.inventory, this);
	}

	@Override
	protected int getGuiId() {
		return NetworkHandler.GUI_FORGE;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void update() {
		++ticksExisted;

		if (world.isRemote){
			return;
		}

		if (justShared > 0)
			--justShared;

		if (ticksExisted % 20 == 0) {
			ItemStack item = getInventory().getStackInSlot(0);
			if (!item.isEmpty() && !world.isRemote) {
				modifyItem(item);
			}
			ItemStack fuelItem = fuelInventory.getStackInSlot(0);
			if (!fuelItem.isEmpty() && !world.isRemote) {
				ForgeFuel stats = ForgeFuelRegistry.getFuelStats(fuelItem);
				if (stats != null) {
					if (addFuel(stats, false)) {
						if (fuelItem != ItemStack.EMPTY) {
							fuelInventory.setStackInSlot(0, ItemStack.EMPTY);
						}
					}
				}
			}
			sendUpdates();
		}

		if (!isLit() && !world.isRemote) {
			if (temperature > 0 && ticksExisted % 5 == 0) {
				temperature = 0;
			}
			return;
		}
		tickFuel();
		if (fuel <= 0) {
			this.extinguish();
			return;
		}
		boolean isBurning = isBurning();// Check if it's burning
		float maxTemp = isLit() ? (fuelTemperature + getUnderTemperature()) : 0;

		if (temperature < maxTemp) {
			float amount = 2.0F;
			temperature += amount;
			if (temperature > maxTemp) {
				temperature = maxTemp;
			}
		} else if (temperature > maxTemp && rand.nextInt(20) == 0) {
			temperature -= 10;
		}

		if (isBurning && temperature > 250 && rand.nextInt(20) == 0 && !isOutside()) {
			int val = this.getTier() == 1 ? 3 : 1;
			if (this.hasBlockAbove()) {
				SmokeMechanics.emitSmokeIndirect(world, pos.add(0, 1, 0), val);
			} else {
				SmokeMechanics.emitSmokeIndirect(world, pos, val);
			}
		}
		maxFuel = getTier() == 1 ? 12000 : 6000;
	}

	public float getFuel() {
		return fuel;
	}

	public boolean isOutside() {
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				if (!world.canBlockSeeSky(pos.add(x, 1, y))) {
					return false;
				}
			}
		}
		return true;
	}

	private void tickFuel() {
		if (fuel > 0) {
			fuel -= 0.2F;
		}

		if (fuel < 0) {
			fuel = 0;
		}
	}

	public boolean isBurning() {
		return fuel > 0 && temperature > 0;
	}

	public int getFuelCount() {
		maxFuel = getTier() == 1 ? 12000 : 6000;
		int room_left = (int) (maxFuel - fuel);
		int fuelInt;
		if (maxFuel == 6000) {
			if (room_left == 0) {
				fuelInt = 3;
			} else if (room_left > 0 && room_left < 3000) {
				fuelInt = 2;
			} else if (room_left > 3000 && room_left < 6000) {
				fuelInt = 1;
			} else {
				fuelInt = 0;
			}
		} else {
			if (room_left == 0) {
				fuelInt = 3;
			} else if (room_left > 0 && room_left < 6000) {
				fuelInt = 2;
			} else if (room_left > 6000 && room_left < 12000) {
				fuelInt = 1;
			} else {
				fuelInt = 0;
			}
		}
		return fuelInt;
	}

	private void modifyItem(ItemStack item) {
		if (item.getItem() instanceof ItemHeated) {
			int temp = ItemHeated.getTemp(item);
			if (temperature != 0) {
				if (temp > temperature) {
					temp = (int) temperature;
				} else {
					int increase = (int) (temperature / (20F * getStackModifier(item.getCount())));
					if (temp >= (temperature - increase)) {
						temp = (int) temperature;
					} else {
						temp += increase;
					}
				}
			}
			if (temp >= 0) {
				ItemHeated.setTemp(item, Math.max(0, temp));
			}
			if (temperature <= 0) {
				ItemHeated.setTemp(item, ItemHeated.getTemp(item) - 10);

				if (ItemHeated.getTemp(item) <= 0) {
					ItemStack stack = this.getInventory().getStackInSlot(0);
					ItemStack cooledStack = ItemHeated.getStack(stack);
					cooledStack.setCount(stack.getCount());
					this.getInventory().setStackInSlot(0, cooledStack);
				}

			}
		} else if (temperature > 0) {
			this.getInventory().setStackInSlot(0, ItemHeated.createHotItem(item));
		}
	}

	private float getStackModifier(int stackSize) {
		return 1F + stackSize / 16F;
	}

	public int getTier() {
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof BlockForge) {
			return ((BlockForge) block).tier;
		}
		return 0;
	}

	public float getUnderTemperature() {
		IBlockState under = world.getBlockState(pos.add(0, -1, 0));

		if (under.getMaterial() == Material.FIRE) {
			return 50F;
		}
		if (under.getMaterial() == Material.LAVA) {
			return 100F;
		}
		return 0F;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		ForgeFuel stats = ForgeFuelRegistry.getFuelStats(item);
		return stats != null;
	}

	public boolean isLit(){
		return isLit;
	}

	public void setIsLit(Boolean isLit){
		this.isLit = isLit;
	}

	/**
	 * Puts the fire out
	 */
	public void extinguish() {
		if (getTier() == 1) {
			return;
		}
		setIsLit(false);
		BlockForge.setActiveState(false, getFuelCount(), hasBlockAbove(), world, pos);
	}

	/**
	 * The principle "fire up the forge" function has been moved to the BlockForge class ~Lenvill
	 */

	public float getBellowsEffect() {
		return getTier() == 1 ? 2.0F : 1.5F;
	}

	private float getBellowsMax() {
		return Math.min(fuelTemperature * getBellowsEffect(), getMaxTemp());
	}

	public int getMaxTemp() {
		return (int) maxTemperature;
	}

	public boolean addFuel(ForgeFuel stats, boolean hand) {
		maxFuel = getTier() == 1 ? 12000 : 6000;

		boolean hasUsed = stats.baseHeat > this.fuelTemperature;

		// uses if hotter
		int room_left = (int) (maxFuel - fuel);
		if (hand && room_left > 0) {
			hasUsed = true;
			fuel = Math.min(fuel + stats.burnTime, maxFuel);// Fill as much as can fit
		} else if (!hand && (fuel == 0 || room_left >= stats.burnTime))// For hoppers: only fill when there's full room
		{
			hasUsed = true;
			fuel = Math.min(fuel + stats.burnTime, maxFuel);// Fill as much as can fit
		}
		if (stats.doesLight && !isLit()) {
			if (!(getTier() == 1)) {
				Block block = world.getBlockState(pos).getBlock();
				((BlockForge) block).igniteBlock(world, pos, world.getBlockState(pos));
			}
			hasUsed = true;
		}
		if (hasUsed) {
			fuelTemperature = stats.baseHeat;
		}
		BlockForge.setActiveState(isLit(), getFuelCount(), hasBlockAbove(), world, pos);
		return hasUsed;
	}

	@Override
	public int getMetreScale(int size) {
		if (shouldShowMetre()) {
			return (int) (size / this.maxFuel * this.fuel);
		}
		return 0;
	}

	public int[] getTempsScaled(int size) {
		int[] temps = new int[2];
		if (shouldShowMetre()) {
			temps[0] = (int) (size / maxTemperature * this.temperature);
			temps[1] = (int) (size / maxTemperature * this.fuelTemperature);
		}
		if (temps[0] > size)
			temps[0] = size;
		if (temps[1] > size)
			temps[1] = size;
		return temps;
	}

	@Override
	public boolean shouldShowMetre() {
		return true;
	}

	@Override
	public String getLocalisedName() {
		return (int) this.temperature + "C " + I18n.format(workableState >= 2 ? "state.unstable" : workableState == 1 ? "state.workable" : "forge.fuel.name");
	}

	public void onUsedWithBellows(float powerLevel) {
		if (!isBurning())
			return;
		if (justShared > 0) {
			return;
		}
		if (fuel <= 0) {
			return;
		}
		float max = getBellowsMax();
		justShared = 5;
		if (temperature < max) {
			temperature = Math.min(max, temperature + (500F * powerLevel));
		}

		for (int a = 0; a < 10; a++) {
			world.spawnParticle(EnumParticleTypes.FLAME, pos.getX() + (rand.nextDouble() * 0.8D) + 0.1D, pos.getY() + 0.4D,
					pos.getZ() + (rand.nextDouble() * 0.8D) + 0.1D, 0, 0.01, 0);
		}

		pumpBellows(-1, 0, powerLevel * 0.9F);
		pumpBellows(0, -1, powerLevel * 0.9F);
		pumpBellows(0, 1, powerLevel * 0.9F);
		pumpBellows(1, 0, powerLevel * 0.9F);

	}

	private void pumpBellows(int x, int z, float pump) {
		if (fuel <= 0)
			return;

		// int share = 2;
		TileEntity tile = world.getTileEntity(pos.add(x, 0, z));
		if (tile == null)
			return;

		if (tile instanceof TileEntityForge) {
			TileEntityForge forge = (TileEntityForge) tile;
			forge.onUsedWithBellows(pump);
		}
	}

	public float getBlockTemperature() {
		if (this.isLit()) {
			return temperature;
		}
		return 0;
	}

	public boolean hasBlockAbove() {
		if (world == null)
			return false;

		TileEntity tile = world.getTileEntity(pos.add(0, 1, 0));
		if (tile != null && tile instanceof IHeatUser) {
			return ((IHeatUser) tile).canAccept(this);
		}

		return tile instanceof TileEntityFurnace;
	}

	public boolean tryAddHeatable(ItemStack held) {
		ItemStack contents = getInventory().getStackInSlot(0);
		if (contents.isEmpty()) {
			ItemStack placed = held.copy();
			placed.setCount(1);
			getInventory().setStackInSlot(0, placed);
			return true;
		} else if (contents.getItem() instanceof ItemHeated) {
			ItemStack hot = Heatable.getItemStack(contents);
			if (!hot.isEmpty() && hot.isItemEqual(held) && ItemStack.areItemStackTagsEqual(held, hot)
					&& contents.getCount() < contents.getMaxStackSize()) {
				contents.grow(1);
				return true;
			}
		}
		return false;
	}

	public int getWorkableState() {
		if (world == null || world.isRemote) {
			return workableState;
		}
		if (!this.getInventory().getStackInSlot(0).isEmpty()) {
			return Heatable.getHeatableStage(getInventory().getStackInSlot(0));
		}
		return 0;
	}

	@Override
	public boolean canPlaceAbove() {
		return true;
	}

	@Override
	public int getHeat() {
		if (world.isRemote) {
			return exactTemperature;
		}
		int mx = (int) (temperature * 1.2F);
		int mn = (int) (temperature * 0.8F);
		return Functions.getIntervalWave1_i(ticksExisted, 400, mx, mn);
	}

	@SideOnly(Side.CLIENT)
	public String getTextureName() {
		BlockForge forge = (BlockForge) world.getBlockState(pos).getBlock();
		return "forge_" + forge.type + (isLit() ? "_active" : "");
	}

	@Override
	public void onBlockBreak() {
		if (!fuelInventory.getStackInSlot(0).isEmpty()) {
			fuelInventory.setStackInSlot(0, ItemStack.EMPTY);
		}
		InventoryUtils.dropItemsInWorld(world, getInventory(), pos);
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setFloat("temperature", temperature);
		nbt.setFloat("fuelTemperature", fuelTemperature);
		nbt.setFloat("fuel", fuel);
		nbt.setFloat("maxFuel", maxFuel);
		nbt.setInteger("workableState", getWorkableState());
		nbt.setBoolean("isLit", isLit);

		nbt.setTag("inventory", inventory.serializeNBT());
		nbt.setTag("fuelInventory", fuelInventory.serializeNBT());

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		temperature = nbt.getFloat("temperature");
		fuelTemperature = nbt.getFloat("fuelTemperature");
		fuel = nbt.getFloat("fuel");
		maxFuel = nbt.getFloat("maxFuel");
		workableState = nbt.getInteger("workableState");
		isLit = nbt.getBoolean("isLit");

		inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
		fuelInventory.deserializeNBT(nbt.getCompoundTag("fuelInventory"));
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == EnumFacing.UP) {
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(fuelInventory);
			}
			else {
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
			}
		}
		return super.getCapability(capability, facing);
	}
}
