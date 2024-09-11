package minefantasy.mfr.tile.blastfurnace;

import minefantasy.mfr.api.MineFantasyReforgedAPI;
import minefantasy.mfr.api.crafting.MineFantasyCarbons;
import minefantasy.mfr.block.BlockBlastHeater;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.config.ConfigHardcore;
import minefantasy.mfr.container.ContainerBase;
import minefantasy.mfr.container.ContainerBlastHeater;
import minefantasy.mfr.entity.EntityFireBlast;
import minefantasy.mfr.network.NetworkHandler;
import minefantasy.mfr.tile.TileEntityCrucible;
import minefantasy.mfr.util.CustomToolHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityBlastHeater extends TileEntityBlastChamber {
	public static float maxProgress = 1200;

	public int fuel;
	public int maxFuel;
	public float heat;
	public float progress;

	public final ItemStackHandler fuelInventory = createInventory();

	@Override
	protected ItemStackHandler createInventory() {
		return new ItemStackHandler(1);
	}

	@Override
	public ItemStackHandler getInventory() {
		return this.fuelInventory;
	}

	@Override
	public ContainerBase createContainer(EntityPlayer player) {
		return new ContainerBlastHeater(player.inventory, this);
	}

	@Override
	protected int getGuiId() {
		return NetworkHandler.GUI_BLAST_HEATER;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public static boolean isFuel(ItemStack item) {
		return getItemBurnTime(item) > 0;
	}

	public static int getItemBurnTime(ItemStack fuel) {
		return MineFantasyReforgedAPI.getFuelValue(fuel) / 4;
	}

	@Override
	public void update() {
		super.update();

		boolean wasBurning = isBurning();
		if (fuel > 0) {
			--fuel;
		}
		if (isBuilt && smokeStorage < getMaxSmokeStorage()) {
			if (fuel > 0) {
				++progress;
				float maxProgress = getMaxProgress();
				if (progress >= maxProgress) {
					progress -= maxProgress;
					smeltItem();
				}
				if (ticksExisted % 10 == 0) {
					smokeStorage += 2;
				}
			} else if (!world.isRemote) {
				if (isFuel(getInventory().getStackInSlot(0))) {
					fuel = maxFuel = getItemBurnTime(getInventory().getStackInSlot(0));
					getInventory().extractItem(0, 1, false);
				}
			}
		}
		if (fireTime > 0) {
			if (fuel > 0)
				--fuel;
			smokeStorage++;
			fireTime--;
			if (ticksExisted % 2 == 0)
				shootFire();

		}
		if (!world.isRemote && wasBurning != isBurning()) {
			BlockBlastHeater.setActiveState(isBurning(), this.world, this.pos);
		}
	}

	private float getMaxProgress() {
		return maxProgress;
	}

	private void smeltItem() {
		for (int y = 0; y < ConfigCrafting.maxFurnaceHeight; y++) {
			TileEntity tileEntity = world.getTileEntity(pos.add(0, y + 1, 0));
			if (tileEntity instanceof TileEntityBlastChamber && !(tileEntity instanceof TileEntityBlastHeater)) {
				ItemStack result = getSmeltedResult((TileEntityBlastChamber) tileEntity);
				if (!result.isEmpty()) {
					dropItem(result);
				}
			} else {
				break;
			}
		}
		fireTime = 20;
		world.playSound(null, this.pos.add(0.5, 0.5, 0.5), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 2.0F, 0.5F);
		world.playSound(null, this.pos.add(0.5, 0.5, 0.5), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 0.75F);
		startFire(1, 0);
		startFire(-1, 0);
		startFire(0, 1);
		startFire(0, -1);
	}

	private void dropItem(ItemStack result) {
		TileEntity under = world.getTileEntity(pos.add(0, -1, 0));
		if (under instanceof TileEntityCrucible) {
			TileEntityCrucible crucible = (TileEntityCrucible) under;
			int slot = crucible.getInventory().getSlots() - 1;
			{
				if (crucible.getInventory().getStackInSlot(slot).isEmpty()) {
					crucible.getInventory().setStackInSlot(slot, result);
					return;
				} else if (CustomToolHelper.areEqual(crucible.getInventory().getStackInSlot(slot), result)) {
					int freeSpace = crucible.getInventory().getStackInSlot(slot).getMaxStackSize() - crucible.getInventory().getStackInSlot(slot).getCount();
					if (freeSpace >= result.getCount()) {
						crucible.getInventory().getStackInSlot(slot).grow(result.getCount());
						return;
					} else {
						crucible.getInventory().getStackInSlot(slot).grow(freeSpace);
						result.shrink(freeSpace);
					}
				}
			}
		}
		if (result.getCount() <= 0)
			return;

		if (ConfigHardcore.HCCreduceIngots && rand.nextInt(3) == 0) {
			EntityItem entity = new EntityItem(world, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, result);
			world.spawnEntity(entity);
		}
	}

	private void startFire(int x, int z) {
		if (!world.isRemote && world.isAirBlock(new BlockPos(x, 0, z))) {
			world.setBlockState(this.pos.add(x, 0, z), Blocks.FIRE.getDefaultState());
		}
	}

	private void shootFire() {
		if (!world.isRemote) {
			shootFire(-1, 0);
			shootFire(1, 0);
			shootFire(0, -1);
			shootFire(0, 1);
		}
	}

	private void shootFire(int x, int z) {
		double v = 0.125D;
		EntityFireBlast fireball = new EntityFireBlast(world, this.pos.getX() + 0.5 + x, this.pos.getY(), this.pos.getZ() + 0.5 + z, x * v, 0 * v, z * v);
		fireball.getEntityData().setString("Preset", "BlastFurnace");
		fireball.modifySpeed(0.5F);
		world.spawnEntity(fireball);
	}

	private ItemStack getSmeltedResult(TileEntityBlastChamber shaft) {
		if (shaft.getIsBuilt()) {
			ItemStack input = shaft.getInventory().getStackInSlot(1);
			ItemStack carbonStack = shaft.getInventory().getStackInSlot(0);
			if (shaft.tempUses <= 0 && carbonStack.isEmpty()
					|| !MineFantasyCarbons.isCarbon(carbonStack)) {
				return ItemStack.EMPTY;
			}
			if (!input.isEmpty()) {
				ItemStack result = getResult(input);
				if (!result.isEmpty()) {
					for (int a = 0; a < shaft.getInventory().getSlots(); a++) {
						if (a > 0 || shaft.shouldRemoveCarbon()) {
							shaft.getInventory().extractItem(a, 1, false);
						}
					}
					return result;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	protected void interact(TileEntityBlastChamber tile) {

	}

	@Override
	public void updateBuild() {
		isBuilt = getIsBuilt();
	}

	@Override
	protected boolean getIsBuilt() {
		return (isFirebrick(-1, -1) && isFirebrick(1, -1) && isFirebrick(-1, 1) && isFirebrick(1, -1))
				&& (isAir(-1, 0) && isAir(1, 0) && isAir(0, -1) && isAir(0, 1));
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		if (slot == 0) {
			return isFuel(item);
		}
		return false;
	}

	public boolean isBurning() {
		return fuel > 0;
	}

	public int getBurnTimeRemainingScaled(int i) {
		if (this.maxFuel <= 0) {
			return 0;
		}

		return fuel * i / maxFuel;
	}

	@Override
	public int getMaxSmokeStorage() {
		return 10;
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setFloat("progress", progress);
		nbt.setInteger("fuel", fuel);
		nbt.setInteger("maxFuel", maxFuel);

		nbt.setTag("inventory", fuelInventory.serializeNBT());
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		progress = nbt.getFloat("progress");
		fuel = nbt.getInteger("fuel");
		maxFuel = nbt.getInteger("maxFuel");

		fuelInventory.deserializeNBT(nbt.getCompoundTag("inventory"));
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
