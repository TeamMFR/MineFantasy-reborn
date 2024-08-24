package minefantasy.mfr.container;

import minefantasy.mfr.container.slots.SlotCraftingOutput;
import minefantasy.mfr.tile.TileEntityBigFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * @author Anonymous Productions
 * <p>
 * Sources are provided for educational reasons. though small bits of
 * code, or methods can be used in your own creations.
 */
public class ContainerBigFurnace extends ContainerBase {
	public TileEntityBigFurnace smelter;
	public int lastProgress;
	public int lastFuel;
	public int lastMaxFuel;
	public int lastHeat;
	public int lastMaxHeat;

	public ContainerBigFurnace(EntityPlayer player, TileEntityBigFurnace tile) {
		super(player.inventory, tile);
		lastProgress = 0;
		lastFuel = 0;
		lastMaxFuel = 0;
		lastHeat = 0;
		lastMaxHeat = 0;
		smelter = tile;
		tile.openChest();

		if (smelter.isHeater()) {
			addSlotToContainer(new SlotItemHandler(smelter.getInventory(), 0, 59, 44));
		} else {
			// IN
			addSlotToContainer(new SlotItemHandler(smelter.getInventory(), 0, 36, 26));
			addSlotToContainer(new SlotItemHandler(smelter.getInventory(), 1, 54, 26));
			addSlotToContainer(new SlotItemHandler(smelter.getInventory(), 2, 36, 44));
			addSlotToContainer(new SlotItemHandler(smelter.getInventory(), 3, 54, 44));

			// OUT
			addSlotToContainer(new SlotCraftingOutput(tile, player, smelter.getInventory(), 4, 106, 26));
			addSlotToContainer(new SlotCraftingOutput(tile, player, smelter.getInventory(), 5, 124, 26));
			addSlotToContainer(new SlotCraftingOutput(tile, player, smelter.getInventory(), 6, 106, 44));
			addSlotToContainer(new SlotCraftingOutput(tile, player, smelter.getInventory(), 7, 124, 44));
		}

		addPlayerSlots(player.inventory, 8, 142);
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		this.smelter.closeChest();
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (IContainerListener listener : this.listeners) {

			if (this.lastProgress != this.smelter.progress) {
				listener.sendWindowProperty(this, 0, this.smelter.progress);
			}

			if (this.lastFuel != this.smelter.fuel) {
				listener.sendWindowProperty(this, 1, this.smelter.fuel);
			}

			if (this.lastMaxFuel != this.smelter.maxFuel) {
				listener.sendWindowProperty(this, 2, (int) this.smelter.maxFuel);
			}

			if (this.lastHeat != this.smelter.heat) {
				listener.sendWindowProperty(this, 3, this.smelter.heat);
			}

			if (this.lastMaxHeat != this.smelter.maxHeat) {
				listener.sendWindowProperty(this, 4, this.smelter.maxHeat);
			}

		}

		this.lastProgress = this.smelter.progress;
		this.lastFuel = this.smelter.fuel;
		this.lastMaxFuel = this.smelter.maxFuel;
		this.lastHeat = this.smelter.heat;
		this.lastMaxHeat = this.smelter.maxHeat;
	}

	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value) {
		if (id == 0) {
			this.smelter.progress = value;
		}

		if (id == 1) {
			this.smelter.fuel = value;
		}

		if (id == 2) {
			this.smelter.maxFuel = value;
		}

		if (id == 3) {
			this.smelter.heat = value;
		}

		if (id == 4) {
			this.smelter.maxHeat = value;
		}
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int num) {
		int invSize = 8;
		if (smelter.isHeater()) {
			invSize = 1;
		}
		ItemStack placedItem = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(num);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemSlot = slot.getStack();
			placedItem = itemSlot.copy();

			// Take
			if (num < invSize) {
				if (!this.mergeItemStack(itemSlot, invSize, 36 + invSize, true)) {
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(itemSlot, placedItem);
			}
			// Put
			else {
				if (smelter.isHeater()) {
					if (smelter.isItemFuel(itemSlot)) {
						if (!this.mergeItemStack(itemSlot, 0, 1, false)) {
							return ItemStack.EMPTY;
						}
					}
				} else {
					if (!smelter.getResult(itemSlot).isEmpty()) {
						if (!this.mergeItemStack(itemSlot, 0, 4, false)) {
							return ItemStack.EMPTY;
						}
					}
				}

				slot.onSlotChange(itemSlot, placedItem);
			}

			if (itemSlot.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemSlot.getCount() == placedItem.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, placedItem);
		}

		return placedItem;
	}
}
