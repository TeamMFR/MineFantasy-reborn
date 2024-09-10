package minefantasy.mfr.tile;

import minefantasy.mfr.api.crafting.IBasicMetre;
import minefantasy.mfr.api.crafting.IHeatSource;
import minefantasy.mfr.api.crafting.IHeatUser;
import minefantasy.mfr.api.heating.FirepitFuel;
import minefantasy.mfr.block.BlockFirepit;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.container.ContainerBase;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.mechanics.RPGElements;
import minefantasy.mfr.registry.FirepitFuelRegistry;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.Functions;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
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

public class TileEntityFirepit extends TileEntityBase implements ITickable, IBasicMetre, IHeatSource {
	private boolean isLit;
	private final int maxFuel = 12000; // 10 minutes
	public int fuel = 0;
	private float charcoal = 0;
	private int ticksExisted;
	private final Random rand = new Random();

	public final ItemStackHandler inventory = createInventory();

	@Override
	protected ItemStackHandler createInventory() {
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
				if (getItemBurnTime(fuelStack) > 0 && addFuel(fuelStack)) {
					if (fuelStack != ItemStack.EMPTY) {
						setStackInSlot(0, ItemStack.EMPTY);
						sendUpdates();
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
		return inventory;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	/**
	 * Gets the burn time from the FirepitFuelRegistry
	 * <p>
	 * Data-driven woods receive a burn-time modifier based on their density
	 */
	public static int getItemBurnTime(ItemStack input) {
		if (!input.isEmpty()) {
			Item i = input.getItem();
			FirepitFuel fuel = FirepitFuelRegistry.getFuelStats(input);

			if (i == MineFantasyItems.TIMBER || i == MineFantasyItems.TIMBER_CUT || i == MineFantasyItems.TIMBER_PANE) {
				return (int) (fuel.burnTime * CustomToolHelper.getBurnModifier(input));
			}
			return (int) fuel.burnTime;
		}
		return 0;
	}

	@Override
	public void update() {
		if (world.isRemote) {
			if (isBurning()) {
				fuel--;
			}
			return;
		}

		++ticksExisted;
		if (ticksExisted % 20 == 0) {
			ItemStack fuelItem = getInventory().getStackInSlot(0);
			if (!fuelItem.isEmpty() && !world.isRemote) {
				if (addFuel(fuelItem)) {
					if (fuelItem != ItemStack.EMPTY) {
						getInventory().setStackInSlot(0, ItemStack.EMPTY);
					}
				}
			}
		}
		if (getIsLit()) {
			if (isWet()) {
				extinguish();
				return;
			}

			if (fuel > 0) {
				fuel--;
				charcoal += (0.25F / 20F / 60F);
			}

			if (fuel <= 0) {
				setIsLit(false);
				BlockFirepit.setActiveState(isBurning(), fuel > 0, hasBlockAbove(), world, pos);
			}
		} else if (fuel > 0 && ticksExisted % 10 == 0) {
			tryLight();
		}
	}

	public boolean isWet() {
		if (isWater(-1, 0, 0) || isWater(1, 0, 0) || isWater(0, 0, -1) || isWater(0, 0, 1) || isWater(0, 1, 0)) {
			return true;
		}

		return world.isRainingAt(pos.add(0, 1, 0));
	}

	public boolean isBurning() {
		return getIsLit() && fuel > 0;
	}

	public boolean getIsLit() {
		return isLit;
	}

	public void setIsLit(boolean lit) {
		isLit = lit;
		ticksExisted = 0;
	}

	private void tryLight() {
		if (isFire(-1, 0, 0) || isFire(1, 0, 0) || isFire(0, 0, -1) || isFire(0, 0, 1) || isFire(0, -1, 0) || isFire(0, 1, 0)) {
			Block block = world.getBlockState(pos).getBlock();
			((BlockFirepit) block).igniteBlock(world, pos, world.getBlockState(pos));
		}
	}

	private boolean isFire(int x, int y, int z) {
		return world.getBlockState(pos.add(x, y, z)).getMaterial() == Material.FIRE;
	}

	private boolean isWater(int x, int y, int z) {
		return world.getBlockState(pos.add(x, y, z)).getMaterial() == Material.WATER;
	}

	public boolean addFuel(ItemStack input) {
		int amount = getItemBurnTime(input);
		if (amount > 0 && fuel < maxFuel && fuel + amount <= maxFuel) {
			fuel += amount;
			if (fuel > maxFuel) {
				fuel = maxFuel;
			}
			BlockFirepit.setActiveState(isBurning(), fuel > 0, hasBlockAbove(), world, pos);
			return true;
		}
		return false;
	}

	@Override
	public boolean canPlaceAbove() {
		return true;
	}

	@Override
	public int getHeat() {
		if (!isBurning()) {
			return 0;
		}
		return Functions.getIntervalWave1_i(ticksExisted, 30, 100, 200);// 100*-300*
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldShowMetre() {
		return fuel > 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getMetreScale(int width) {
		return this.fuel * width / (maxFuel);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getLocalisedName() {
		int seconds = (int) Math.floor(fuel / 20F);
		int mins = (int) Math.floor(seconds / 60F);
		seconds -= mins * 60;

		String s = "";
		if (seconds < 10) {
			s += "0";
		}

		return "150C " + I18n.format("forge.fuel.name") + " " + mins + ":" + s + seconds;
	}

	public void extinguish() {
		world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
		world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
		world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);

		setIsLit(false);
		BlockFirepit.setActiveState(isBurning(), fuel > 0, hasBlockAbove(), world, pos);
	}

	public boolean tryCook(EntityPlayer player, ItemStack held) {
		if (held.getItem() instanceof ItemFood) {
			player.swingArm(EnumHand.MAIN_HAND);
			if (world.isRemote) {
				return false;
			}
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(held);
			if (!result.isEmpty()) {
				if (rand.nextInt(10) != 0) {
					return false;
				}
				float chance = 75F;
				if (RPGElements.isSystemActive) {
					int skill = RPGElements.getLevel(player, Skill.PROVISIONING);
					chance += (int) ((float) skill / 4);
				}
				boolean success = (rand.nextFloat() * 100) < chance;
				ItemStack creation = success ? result.copy() : new ItemStack(MineFantasyItems.BURNT_FOOD);
				dropItem(player, creation);
				Skill.PROVISIONING.addXP(player, success ? 2 : 1);
				return true;
			}
		}
		return false;
	}

	public void dropItem(EntityPlayer player, ItemStack item) {
		EntityItem drop = new EntityItem(world, player.posX, player.posY, player.posZ, item);
		drop.setPickupDelay(10);
		drop.motionX = drop.motionY = drop.motionZ = 0;
		world.spawnEntity(drop);
	}

	public boolean hasBlockAbove() {
		if (world == null)
			return false;

		TileEntity tile = world.getTileEntity(pos.add(0, 1, 0));
		if (tile instanceof IHeatUser) {
			return ((IHeatUser) tile).canAccept(this);
		}

		return false;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("fuel", fuel);
		tag.setBoolean("isLit", isLit);
		return new SPacketUpdateTileEntity(pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		fuel = packet.getNbtCompound().getInteger("fuel");
		isLit = packet.getNbtCompound().getBoolean("isLit");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("fuel", fuel);
		nbt.setInteger("ticksExisted", ticksExisted);
		nbt.setBoolean("isLit", isLit);
		nbt.setFloat("charcoal", charcoal);
		nbt.setTag("inventory", inventory.serializeNBT());
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		fuel = nbt.getInteger("fuel");
		ticksExisted = nbt.getInteger("ticksExisted");
		isLit = nbt.getBoolean("isLit");
		charcoal = nbt.getFloat("charcoal");
		inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
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
		int amount = getItemBurnTime(item);
		return amount > 0 && fuel < maxFuel && fuel + amount <= maxFuel;
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
