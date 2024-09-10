package minefantasy.mfr.container;

import minefantasy.mfr.api.crafting.MineFantasyCarbons;
import minefantasy.mfr.container.slots.SlotRestrictive;
import minefantasy.mfr.tile.blastfurnace.TileEntityBlastChamber;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerBlastChamber extends ContainerBase {
	private final TileEntityBlastChamber tile;

	public ContainerBlastChamber(InventoryPlayer playerInventory, TileEntityBlastChamber tile) {
		super(playerInventory, tile);
		this.tile = tile;

		this.addSlotToContainer(new SlotRestrictive(tile, 0, 80, 30));
		this.addSlotToContainer(new SlotRestrictive(tile, 1, 80, 68));

		addPlayerSlots(playerInventory, 8, 184);
	}

	@Override
	public void detectAndSendChanges() {
		for (int i = 0; i < this.inventorySlots.size(); ++i) {
			ItemStack itemstack = this.inventorySlots.get(i).getStack();
			ItemStack itemstack1 = this.inventoryItemStacks.get(i);

			if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {

				itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
				this.inventoryItemStacks.set(i, itemstack1);

				for (IContainerListener listener : this.listeners) {
					listener.sendSlotContents(this, i, itemstack1);
				}
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.tile.isUsableByPlayer(player);
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(EntityPlayer user, int clicked) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(clicked);
		// TOTAL SLOTS: 38 = 2+27+9
		// Chamber = 0-1
		// Inv = 2-28
		// Bar = 29-47

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (clicked > 1)// INVENTORY
			{
				if (MineFantasyCarbons.isCarbon(itemstack1)) {
					if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (tile.isInput(itemstack1)) {
					if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (clicked >= 2 && clicked < 29)// INVENTORY
				{
					if (!this.mergeItemStack(itemstack1, 29, 38, false)) {
						return ItemStack.EMPTY;
					}
				}
				// BAR
				else if (clicked >= 29 && clicked < 38 && !this.mergeItemStack(itemstack1, 2, 29, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 2, 38, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(user, itemstack1);
		}

		return itemstack;
	}
}