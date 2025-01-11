package minefantasy.mfr.tile;

import minefantasy.mfr.api.weapon.IRackItem;
import minefantasy.mfr.container.ContainerBase;
import minefantasy.mfr.item.ItemBlockToolRack;
import minefantasy.mfr.item.ItemCrossbow;
import minefantasy.mfr.util.BlockPositionHelper;
import minefantasy.mfr.util.CustomToolHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityRack extends TileEntityWoodDecor {
	public ItemStackHandler inventory = createInventory();
	public int colorInt;

	public TileEntityRack() {
		super("rack_wood");
	}

	@Override
	protected ItemStackHandler createInventory() {
		return new ItemStackHandler(4);
	}

	@Override
	public ItemStackHandler getInventory() {
		return inventory;
	}

	@Override
	public ContainerBase createContainer(EntityPlayer player) {
		return null;
	}

	@Override
	protected int getGuiId() {
		return 0;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		if (world.getTileEntity(pos) != this) {
			return false;
		}
		return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64D;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return false;
	}

	public void setColorInt(ItemStack stack) {
		if (stack.getItem() instanceof ItemBlockToolRack) {
			this.colorInt = CustomToolHelper.getColourFromItemStack(stack, 0);
		}
	}

	public int getColorInt() {
		return colorInt;
	}

	/*
	 * North: WEST: 0=2 South: SOUTH: 1=1 West: EAST: 2=3 East: NORTH: 3=0
	 */
	public int getSlotFor(float x, float y, EnumFacing facing) {
		float offset = 1F / 16F;

		float x1 = 0.0F + offset;
		float x2 = 1.0F - offset;
		float y1 = 0.0F;
		float y2 = 1.0F;
		if (facing == EnumFacing.EAST || facing == EnumFacing.WEST) {
			x1 = 0.0F;
			x2 = 1.0F;
			y1 = 0.0F + offset;
			y2 = 1.0F - offset;
		}
		int[] coord = BlockPositionHelper.getCoordsFor(x, y, x1, x2, y1, y2, 4, 4, facing);

		if (coord == null) {
			return -1;
		}
		return coord[0];

	}

	public boolean canHang(ItemStack item, int slot) {
		if (item.isEmpty() || item.getItem() == null) {
			return false;
		}
		if (item.getItem() instanceof ItemBlock) {
			return false;
		}
		if (item.getItem() instanceof IRackItem) {
			return ((IRackItem) item.getItem()).canHang(this, item, slot);
		}
		if (item.getItem() instanceof ItemArmor) {
			return false;
		}
		if(item.getItem() instanceof ItemCrossbow) {
			return false;
		}
		return item.getItem().isDamageable();
	}

	public boolean hasRackAbove(int slot) {
		TileEntity side = world.getTileEntity(pos.add(0, 1, 0));
		return side != null && side instanceof TileEntityRack;// && ((TileEntityRack)side).getStackInSlot(slot) == null;
	}

	public boolean hasRackBelow(int slot) {
		TileEntity side = world.getTileEntity(pos.add(0, -1, 0));
		return side != null && side instanceof TileEntityRack;// && ((TileEntityRack)side).getStackInSlot(slot) == null;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		colorInt = nbt.getInteger("color_int");
		inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("color_int", colorInt);
		nbt.setTag("inventory", inventory.serializeNBT());
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
