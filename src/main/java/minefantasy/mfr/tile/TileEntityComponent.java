package minefantasy.mfr.tile;

import minefantasy.mfr.block.BlockComponent;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.container.ContainerBase;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.item.ItemComponentMFR;
import minefantasy.mfr.item.ItemPersistentComponentMarker;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.util.CustomToolHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityComponent extends TileEntityBase {
	public int max;
	public String type = "bar";
	public String tex = "bar";
	public CustomMaterial material;

	public ItemStackHandler inventory = createInventory();

	public TileEntityComponent() {
	}

	@Override
	protected ItemStackHandler createInventory() {
		return new ItemStackHandler(1) {
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (inventory.getStackInSlot(0).getItem() instanceof ItemPersistentComponentMarker) {
					ItemStack copy = stack.copy();
					ItemComponentMFR componentItem = (ItemComponentMFR) copy.getItem();
					copy.setCount(1);
					setItem(copy,
							componentItem.getStorageType(),
							componentItem.getBlockTexture(),
							BlockComponent.getStorageSize(componentItem.getStorageType()));
					stack.shrink(1);
				}
				return super.insertItem(slot, stack, simulate);
			}

			@Nonnull
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				if (stacks.get(0).getItem() != MineFantasyItems.PERSISTENT_COMPONENT_FLAG) {
					return super.extractItem(slot, amount, simulate);
				}
				else {
					return ItemStack.EMPTY;
				}
			}

			@Override
			protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
				return BlockComponent.getStorageSize(type);
			}

			@Override
			protected void onContentsChanged(int slot) {
				checkStack();
				sendUpdates();
			}
		};
	}

	@Override
	public ItemStackHandler getInventory() {
		return inventory;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return item.getItem() instanceof ItemComponentMFR;
	}

	public void setItem(ItemStack item, String type, String tex, int max) {
		this.getInventory().setStackInSlot(0, item);
		this.max = max;
		this.type = type;
		this.tex = tex;
		this.material = CustomToolHelper.getCustomPrimaryMaterial(item);
	}

	public void interact(EntityPlayer user, ItemStack held, boolean leftClick) {
		ItemStack stack = getInventory().getStackInSlot(0);
		if (!stack.isEmpty() && stack.getCount() > 0) {
			// Take
			if (leftClick && !(stack.getItem() instanceof ItemPersistentComponentMarker)) {
				int amount = user.isSneaking() ? stack.getCount() : 1;
				if (held.isEmpty()) {
					held = stack.copy();
					held.setCount(amount);
					user.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, held);
					stack.shrink(amount);
				} else {
					ItemStack newItem = stack.copy();
					newItem.setCount(amount);
					if (user.inventory.addItemStackToInventory(newItem)) {
						stack.shrink(amount);
					}
				}
				//Check if the Placed Component should persist for automated interactions
				//If not persisting and component is empty, remove the block and tile
				//If persisting, set the component to the Persist Flag
				boolean shouldPersist = world.getBlockState(pos).getValue(BlockComponent.PERSIST);
				if (!shouldPersist && (getInventory().getStackInSlot(0).isEmpty() || stack.getCount() <= 0)) {
					world.setBlockToAir(pos);
					world.removeTileEntity(pos);
				}
				if (shouldPersist) {
					if (!(stack.getItem() instanceof ItemPersistentComponentMarker)) {
						this.setItem(
								new ItemStack(MineFantasyItems.PERSISTENT_COMPONENT_FLAG),
								Constants.StorageTextures.PERSIST_FLAG,
								Constants.StorageTextures.PERSIST_FLAG,
								0);
					}
				}

			}
			// PLACE
			else if (!held.isEmpty()) {
				if (stack.getCount() < max && getInventory().getStackInSlot(0).isItemEqual(held)
						&& ItemStack.areItemStackTagsEqual(getInventory().getStackInSlot(0), held)) {
					int amount = user.isSneaking() ? held.getCount() : 1;
					if (amount != 1) {
						amount = amount < max ? Math.min(amount, (max - stack.getCount())) : (max - stack.getCount());
					}
					stack.grow(amount);
					held.shrink(amount);
				}

				if (held.getCount() <= 0) {
					user.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
				}
			}
		}
		checkStack();
		sendUpdates();
	}

	public void checkStack() {
		if (!BlockComponent.canBuildOn(world, pos.add(0, -1, 0))) {
			world.setBlockToAir(pos);
		}
		TileEntity tile = world.getTileEntity(pos.add(0, 1, 0));
		if (tile instanceof TileEntityComponent) {
			((TileEntityComponent) tile).checkStack();
		}
		if (world.getBlockState(pos).getBlock() instanceof  BlockComponent
				&& world.getBlockState(pos).getValue(BlockComponent.PERSIST)
				&& getInventory().getStackInSlot(0).isEmpty()) {

			this.setItem(
					new ItemStack(MineFantasyItems.PERSISTENT_COMPONENT_FLAG),
					Constants.StorageTextures.PERSIST_FLAG,
					Constants.StorageTextures.PERSIST_FLAG,
					0);
		}
	}

	public boolean isFull() {
		return getInventory().getStackInSlot(0).getCount() >= max;
	}

	public float getBlockHeight() {
		ItemStack stack = getInventory().getStackInSlot(0);

		if (type.equalsIgnoreCase(Constants.StorageTextures.SHEET)) {
			return stack.getCount() * 0.0625F;
		}
		if (type.equalsIgnoreCase(Constants.StorageTextures.PLANK)) {
			float f = 0.125F;
			if (stack.getCount() > 42)
				return 8F * f;
			if (stack.getCount() > 36)
				return 7F * f;
			if (stack.getCount() > 30)
				return 6F * f;
			if (stack.getCount() > 24)
				return 5F * f;
			if (stack.getCount() > 18)
				return 4F * f;
			if (stack.getCount() > 12)
				return 3F * f;
			if (stack.getCount() > 6)
				return 2F * f;

			return f;
		}
		if (type.equalsIgnoreCase(Constants.StorageTextures.BAR)) {
			float f = 0.125F;
			if (stack.getCount() > 50)
				return 8F * f;
			if (stack.getCount() > 48)
				return 7F * f;
			if (stack.getCount() > 34)
				return 6F * f;
			if (stack.getCount() > 32)
				return 5F * f;
			if (stack.getCount() > 18)
				return 4F * f;
			if (stack.getCount() > 16)
				return 3F * f;
			if (stack.getCount() > 2)
				return 2F * f;

			return f;
		}
		if (type.equalsIgnoreCase(Constants.StorageTextures.POT)) {
			float f = 0.25F;
			if (stack.getCount() > 48)
				return 4F * f;
			if (stack.getCount() > 32)
				return 3F * f;
			if (stack.getCount() > 16)
				return 2F * f;

			return f;
		}
		if (type.equalsIgnoreCase(Constants.StorageTextures.BIGPLATE)) {
			return Math.max(0.125F, stack.getCount() * 0.125F);
		}
		if (type.equalsIgnoreCase(Constants.StorageTextures.JUG)) {
			return stack.getCount() > 16 ? 1.0F : 0.5F;
		}
		if (type.equalsIgnoreCase(Constants.StorageTextures.PERSIST_FLAG)) {
			return 0.125F;
		}

		return 1.0F;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		max = nbt.getInteger("max_stack");
		type = nbt.getString("type");
		tex = nbt.getString("tex");
		inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
		if (nbt.hasKey("material_name")) {
			this.material = CustomMaterialRegistry.getMaterial(nbt.getString("material_name"));
		} else {
			this.material = CustomMaterialRegistry.NONE;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("max_stack", max);
		nbt.setString("type", type);
		nbt.setString("tex", tex);

		if (!getInventory().getStackInSlot(0).isEmpty()) {
			nbt.setTag("inventory", inventory.serializeNBT());
		}
		if (material != CustomMaterialRegistry.NONE) {
			nbt.setString("material_name", material.getName());
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

	@Override
	public ContainerBase createContainer(EntityPlayer player) {
		return null;
	}

	@Override
	protected int getGuiId() {
		return 0;
	}
}
