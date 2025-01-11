package minefantasy.mfr.tile;

import minefantasy.mfr.api.archery.IAmmo;
import minefantasy.mfr.api.archery.IFirearm;
import minefantasy.mfr.api.crafting.IBasicMetre;
import minefantasy.mfr.api.tool.IStorageBlock;
import minefantasy.mfr.block.BlockAmmoBox;
import minefantasy.mfr.container.ContainerBase;
import minefantasy.mfr.item.ItemBandage;
import minefantasy.mfr.item.ItemSyringe;
import minefantasy.mfr.mechanics.AmmoMechanics;
import minefantasy.mfr.network.AmmoBoxCommandPacket;
import minefantasy.mfr.network.NetworkHandler;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityAmmoBox extends TileEntityWoodDecor implements ITickable, IBasicMetre {
	public int angle, stock;
	public ItemStack inventoryStack = ItemStack.EMPTY;

	public TileEntityAmmoBox() {
		super("ammo_box_basic", CustomMaterialRegistry.NONE);
	}

	@Override
	public void update() {
		if (angle > 0)
			--angle;
	}

	public boolean interact(EntityPlayer user, ItemStack held) {
		if (!held.isEmpty()) {
			if (this.getStorageType() == 1 && !inventoryStack.isEmpty() && held.getItem() instanceof IFirearm && loadGun(held)) {
				open();
				syncData();
				return true;
			}
			if (canAcceptItem(held)) {
				int max = this.getMaxAmmo(!inventoryStack.isEmpty() ? inventoryStack : held);
				if (inventoryStack.isEmpty()) {
					open();
					placeInEmpty(user, held, max);
				} else if (areItemStacksEqual(held, inventoryStack) && stock < max) {
					open();
					addToBox(user, held, max);
				}

				syncData();
				return true;
			}
			return false;
		} else if (!inventoryStack.isEmpty() && (held.getItem() == Items.AIR)) {
			open();
			takeStack(user);
			syncData();
			return true;
		}
		syncData();
		return false;
	}

	private boolean canAcceptItem(ItemStack held) {
		if (held.getItem() instanceof IStorageBlock) {
			return false;
		}
		byte type = this.getStorageType();

		return type == 0 ? isFood(held) : type == 1 ? held.getItem() instanceof IAmmo : type == 2;
	}

	private boolean isFood(ItemStack held) {
		return held.getItem() instanceof ItemFood || held.getItem() instanceof ItemBandage || held.getItem() instanceof ItemSyringe;
	}

	private boolean loadGun(ItemStack held) {
		IFirearm gun = (IFirearm) held.getItem();
		if (gun.canAcceptAmmo(held, getAmmoClass())) {
			ItemStack loaded = AmmoMechanics.getAmmo(held);
			if (loaded.isEmpty()) {
				int ss = Math.min(inventoryStack.getMaxStackSize(), stock);
				ItemStack newloaded = inventoryStack.copy();
				newloaded.setCount(ss);
				AmmoMechanics.setAmmo(held, newloaded);
				stock -= ss;
				if (stock <= 0) {
					inventoryStack = ItemStack.EMPTY;
				}
				return true;
			} else if (areItemStacksEqual(loaded, inventoryStack)) {
				int room_left = loaded.getMaxStackSize() - loaded.getCount();
				if (stock > room_left) {
					stock -= room_left;
					loaded.grow(room_left);
					AmmoMechanics.setAmmo(held, loaded);
				} else {
					loaded.grow(stock);
					AmmoMechanics.setAmmo(held, loaded);
					stock = 0;
					inventoryStack = ItemStack.EMPTY;
				}

				return true;
			}
		}
		return false;
	}

	private void addToBox(EntityPlayer user, ItemStack held, int max) {
		int room_left = max - stock;
		if (held.getCount() <= room_left) {
			stock += held.getCount();
			user.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
		} else {
			held.shrink(room_left);
			stock += room_left;
		}
	}

	private void takeStack(EntityPlayer user) {
		int ss = stock;
		ItemStack taken = inventoryStack.copy();
		if (ss > taken.getMaxStackSize()) {
			ss = taken.getMaxStackSize();
		}
		taken.setCount(ss);
		stock -= ss;
		user.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, taken);
		if (stock <= 0) {
			inventoryStack = ItemStack.EMPTY;
		}
	}

	/**
	 * Place ammo in empty box
	 */
	private void placeInEmpty(EntityPlayer user, ItemStack held, int max) {
		inventoryStack = held.copy();
		int ss = held.getCount();
		if (held.getCount() <= max)// Place all
		{
			user.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
		} else // Fill as much
		{
			stock = max;
			held.shrink(max);
		}
		stock = ss;
	}

	private void open() {
		angle = 16;
		this.world.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 1.4F);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("stock", stock);
		if (!inventoryStack.isEmpty()) {
			NBTTagCompound itemsave = new NBTTagCompound();
			inventoryStack.writeToNBT(itemsave);
			nbt.setTag("storage", itemsave);
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		stock = nbt.getInteger("stock");
		if (nbt.hasKey("storage")) {
			NBTTagCompound itemsave = nbt.getCompoundTag("storage");
			inventoryStack = new ItemStack(itemsave);
		}
	}

	public void syncData() {
		if (world.isRemote)
			return;
		NetworkHandler.sendToAllTrackingChunk(world, pos.getX() >> 4, pos.getZ() >> 4, new AmmoBoxCommandPacket(this));
		sendUpdates();
	}

	public int getMaxAmmo(ItemStack ammo) {
		return ammo.getMaxStackSize() * getCapacity(getMaterial().getTier());
	}

	@Override
	public int getMetreScale(int size) {
		if (!inventoryStack.isEmpty()) {
			return (int) Math.min(size, (float) size / (float) getMaxAmmo(inventoryStack) * stock);
		}
		return 0;
	}

	@Override
	public boolean shouldShowMetre() {
		return !inventoryStack.isEmpty();
	}

	@Override
	public String getLocalisedName() {
		if (!inventoryStack.isEmpty()) {
			return inventoryStack.getDisplayName() + " x" + stock;
		}
		return "";
	}

	private boolean areItemStacksEqual(ItemStack i1, ItemStack i2) {
		if (i1.isEmpty() || i2.isEmpty())
			return false;

		return i1.isItemEqual(i2) && ItemStack.areItemStackTagsEqual(i1, i2);
	}

	private String getAmmoClass() {
		if (!inventoryStack.isEmpty() && inventoryStack.getItem() instanceof IAmmo) {
			return ((IAmmo) inventoryStack.getItem()).getAmmoType(inventoryStack);
		}
		return "null";
	}

	public byte getStorageType() {
		if (world != null) {
			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof BlockAmmoBox) {
				return ((BlockAmmoBox) block).storageType;
			}
		}
		return (byte) -1;
	}

	@Override
	protected ItemStackHandler createInventory() {
		return null;
	}

	@Override
	public ItemStackHandler getInventory() {
		return null;
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
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return false;
	}
}
