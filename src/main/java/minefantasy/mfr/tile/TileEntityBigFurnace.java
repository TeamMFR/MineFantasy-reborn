package minefantasy.mfr.tile;

import minefantasy.mfr.api.refine.IBellowsUseable;
import minefantasy.mfr.api.refine.SmokeMechanics;
import minefantasy.mfr.block.BlockBigFurnace;
import minefantasy.mfr.container.ContainerBase;
import minefantasy.mfr.container.ContainerBigFurnace;
import minefantasy.mfr.init.MineFantasyBlocks;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.init.MineFantasySounds;
import minefantasy.mfr.network.NetworkHandler;
import minefantasy.mfr.recipe.BigFurnaceRecipeBase;
import minefantasy.mfr.recipe.CraftingManagerBigFurnace;
import minefantasy.mfr.recipe.IRecipeMFR;
import minefantasy.mfr.registry.ForgeFuelRegistry;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TileEntityBigFurnace extends TileEntityBase implements IBellowsUseable, ITickable {

	// UNIVERSAL
	public int fuel;
	public int maxFuel;
	public boolean built = false;
	private Set<String> knownResearches = new HashSet<>();
	// ANIMATE
	private boolean opening = false;
	public int numUsers;
	private float doorAngle = 0;
	private float prevDoorAngle = 0;
	// HEATER
	public int heat;
	public int maxHeat;
	public int justShared;
	// FURNACE
	public int progress;
	private Random rand = new Random();
	private int aboveType;
	private int ticksExisted;

	public final ItemStackHandler inventory = createInventory();

	@Override
	protected ItemStackHandler createInventory() {
		return new ItemStackHandler(8) {
			@Override
			protected void onContentsChanged(int slot) {
				if (slot == 0) {
					world.updateComparatorOutputLevel(pos, blockType);
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
		return new ContainerBigFurnace(player, this);
	}

	@Override
	protected int getGuiId() {
		return NetworkHandler.GUI_BIG_FURNANCE;
	}

	@Override
	public void update() {
		//ANIMATION
		if (opening) {
			prevDoorAngle = doorAngle;
			if (numUsers > 0 && getDoorAngle() < 20) {
				doorAngle++;
			}

			if (numUsers <= 0 && getDoorAngle() > 0) {
				doorAngle--;
			}
			if (getDoorAngle() < 0)
				doorAngle = 0;
			if (getDoorAngle() > 20)
				doorAngle = 20;
		}

		++ticksExisted;
		if (justShared > 0)
			justShared--;
		if (ticksExisted % 10 == 0) {
			built = structureExists();
		}
		if (isHeater()) {
			updateHeater();
		} else {
			updateFurnace();
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public float getDoorAngle() {
		return doorAngle;
	}

	public float getPrevDoorAngle() {
		return prevDoorAngle;
	}

	@Override
	public void onUsedWithBellows(float powerLevel) {
		if (isHeater()) {
			if (justShared > 0) {
				return;
			}
			justShared = 5;

			if (fuel > 0) {
				int max = (int) (maxHeat * 1.5F);
				if (heat < max) {
					heat += (int) (50 * powerLevel);
				}

				for (int a = 0; a < 10; a++) {
					world.spawnParticle(EnumParticleTypes.FLAME, pos.getX() + (rand.nextDouble() * 0.8D) + 0.1D, pos.getY() + 0.4D,
							pos.getZ() + (rand.nextDouble() * 0.8D) + 0.1D, 0, 0.01, 0);
				}
			}
			pumpBellows(-1, 0, powerLevel * 0.9F);
			pumpBellows(0, -1, powerLevel * 0.9F);
			pumpBellows(0, 1, powerLevel * 0.9F);
			pumpBellows(1, 0, powerLevel * 0.9F);
		}
	}

	private void pumpBellows(int x, int z, float pump) {
		TileEntity tile = world.getTileEntity(pos.add(x, 0, z));
		if (tile == null)
			return;

		if (tile instanceof TileEntityBigFurnace) {
			TileEntityBigFurnace furn = (TileEntityBigFurnace) tile;
			if (furn.isHeater())
				furn.onUsedWithBellows(pump);
		}
	}

	private void updateFurnace() {
		if (isBurning()) {
			if (world.isRemote && rand.nextInt(10) == 0) {
				world.spawnParticle(EnumParticleTypes.FLAME, pos.getX() + (rand.nextDouble() * 0.8D) + 0.1D, pos.getY() + 0.4D,
						pos.getZ() + (rand.nextDouble() * 0.8D) + 0.1D, 0, 0.01, 0);
			}
		}
		if (world.isRemote) {
			return;
		}
		if (isBurning()) {
			puffSmoke(new Random(), world, pos);
		}
		TileEntityBigFurnace heater = getHeater();

		if (heater != null) {
			heat = heater.heat;
		} else {
			heat -= 4;
		}
		boolean canSmelt = false;
		boolean smelted = false;

		for (int a = 0; a < 4; a++) {
			if (canSmelt(getInventory().getStackInSlot(a), getInventory().getStackInSlot(a + 4))) {
				canSmelt = true;

				if (progress >= getMaxTime()) {
					smeltItem(a, a + 4);
					smelted = true;
				}
			}
		}

		if (canSmelt) {
			progress += (int) heat;
		}
		if (!canSmelt || smelted) {
			progress = 0;
		}
	}

	public void puffSmoke(Random rand, World world, BlockPos pos) {
		if (rand.nextInt(5) != 0) {
			return;
		}
		IBlockState state = world.getBlockState(pos);
		EnumFacing facing = state.getValue(BlockBigFurnace.FACING).getOpposite();

		SmokeMechanics.emitSmokeIndirect(world, pos.add(facing.getXOffset(), (isHeater() ? 2 : 1), facing.getZOffset()), 1);
	}

	private int getMaxTime() {
		return (int) 1.0E+5;
	}

	private void smeltItem(int input, int output) {
		ItemStack res = getResult(getInventory().getStackInSlot(input)).copy();

		if (getInventory().getStackInSlot(output).isEmpty()) {
			getInventory().setStackInSlot(output, res);
		} else {
			if (CustomToolHelper.areEqual(getInventory().getStackInSlot(output), res)) {
				getInventory().getStackInSlot(output).grow(res.getCount());
			}
		}

		getInventory().extractItem(input, 1, false);
	}

	private boolean canSmelt(ItemStack in, ItemStack out) {
		if (isHeater())
			return false;

		if (!built)
			return false;

		ItemStack res = getResult(in);
		if (res.isEmpty()) {
			return false;
		}
		if (out.isEmpty()) {
			return true;
		}
		if (CustomToolHelper.areEqual(out, res)) {
			int max = res.getMaxStackSize();
			return (out.getCount() + res.getCount()) <= max;
		} else {
			return false;
		}

	}

	private TileEntityBigFurnace getHeater() {
		TileEntity tile = world.getTileEntity(pos.add(0, -1, 0));
		if (tile != null && tile instanceof TileEntityBigFurnace) {
			if (((TileEntityBigFurnace) tile).isHeater())
				return (TileEntityBigFurnace) tile;
		}
		return null;
	}

	private TileEntityBigFurnace getFurnace() {
		TileEntity tile = world.getTileEntity(pos.add(0, 1, 0));
		if (tile != null && tile instanceof TileEntityBigFurnace) {
			if (!((TileEntityBigFurnace) tile).isHeater())
				return (TileEntityBigFurnace) tile;
		}
		return null;
	}

	private void updateHeater() {
		if (world.isRemote) {
			return;
		}

		TileEntityBigFurnace furn = getFurnace();
		if (furn != null) {
			aboveType = furn.getType();
		}
		if (ticksExisted % 20 == 0) {
			IBlockState state = world.getBlockState(pos);
			if (!state.getValue(BlockBigFurnace.BURNING)) {
				if (isBurning()) {
					world.setBlockState(pos, state.withProperty(BlockBigFurnace.BURNING, true));
				}
			}
			else {
				if (!isBurning()) {
					world.setBlockState(pos, state.withProperty(BlockBigFurnace.BURNING, false));
				}
			}
		}
		if (!built) {
			heat = maxHeat = fuel = maxFuel = 0;
			return;
		}
		if (heat < maxHeat) {
			heat++;
		}
		if (heat > maxHeat) {
			heat--;
		}
		if (fuel > 0) {
			fuel--;
		} else {
			if (!getInventory().getStackInSlot(0).isEmpty() && isItemFuel(getInventory().getStackInSlot(0))) {
				fuel = maxFuel = getItemBurnTime(getInventory().getStackInSlot(0));
				maxHeat = getItemHeat(getInventory().getStackInSlot(0));
				ItemStack container = getInventory().getStackInSlot(0).getItem().getContainerItem(getInventory().getStackInSlot(0));

				if (!container.isEmpty()) {
					getInventory().setStackInSlot(0, container);

				} else {
					getInventory().extractItem(0, 1, false);
				}
			}
			if (fuel <= 0) {
				if (heat > 0) {
					heat--;
				}
				maxHeat = 0;
			}
		}
	}

	private int getItemHeat(ItemStack itemStack) {
		if (itemStack.isEmpty()){
			return 0;
		}
		if(ForgeFuelRegistry.getFuelStats(itemStack) != null) {
			return ForgeFuelRegistry.getFuelStats(itemStack).baseHeat;
		}
		return 0;
	}

	public ItemStack getResult(ItemStack item) {
		if (item.isEmpty()) {
			return ItemStack.EMPTY;
		}

		// SPECIAL SMELTING
		BigFurnaceRecipeBase recipe = CraftingManagerBigFurnace.findMatchingRecipe(item, this.knownResearches);
		if (recipe != null && recipe.getTier() <= this.getTier()) {
			setRecipe(recipe);
			return recipe.getBigFurnaceRecipeOutput();
		}

		// If no special: try vanilla
		ItemStack res = FurnaceRecipes.instance().getSmeltingResult(item);
		if (!res.isEmpty()) {
			if (res.getItem() instanceof ItemFood || item.getItem() instanceof ItemFood) {
				return new ItemStack(MineFantasyItems.BURNT_FOOD, 1);
			}
			return res;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public IRecipeMFR getRecipeByOutput(ItemStack stack) {
		return CraftingManagerBigFurnace.findRecipeByOutput(stack);
	}

	public void setKnownResearches(Set<String> knownResearches) {
		this.knownResearches = knownResearches;
	}

	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int height) {
		if (this.maxFuel == 0) {
			this.maxFuel = 200;
		}

		return this.fuel * height / this.maxFuel;
	}

	@SideOnly(Side.CLIENT)
	public int getHeatScaled(int height) {
		if (heat <= 0)
			return 0;
		int size = (int) (height / TileEntityForge.maxTemperature * this.heat);

		return Math.min(size, height);
	}

	@SideOnly(Side.CLIENT)
	public int getItemHeatScaled(int height) {
		if (maxHeat <= 0)
			return 0;
		int size = (int) (height / TileEntityForge.maxTemperature * this.maxHeat);

		return Math.min(size, height);
	}

	public boolean isBurning() {
		if (isHeater()) {
			return heat > 0;
		}
		return progress > 0 && heat > 0;
	}

	public boolean isHeater() {
		Block block = world != null ? getBlockType() : blockType;

		if (block != null && block instanceof BlockBigFurnace) {
			return ((BlockBigFurnace) block).isHeater;
		}
		return false;
	}

	public int getTier() {
		Block block = world != null ? getBlockType() : blockType;

		if (block != null && block instanceof BlockBigFurnace) {
			return ((BlockBigFurnace) block).tier;
		}
		return 0;
	}

	public int getItemBurnTime(ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return 0;
		}
		if (ForgeFuelRegistry.getFuelStats(itemstack) != null) {
			return (int) Math.ceil(ForgeFuelRegistry.getFuelStats(itemstack).burnTime / 8);
		} else {
			return 0;
		}
	}

	public void openChest() {
		if (numUsers == 0) {
			this.world.playSound(null, pos, MineFantasySounds.FURNACE_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
		}
		++numUsers;
		if (!opening) {
			opening = true;
		}
	}

	public void closeChest() {
		--numUsers;
		if (numUsers == 0 && doorAngle >= 15) {
			this.world.playSound(null, pos, MineFantasySounds.FURNACE_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
		}
	}

	/**
	 * Heater:0||Stone :1;
	 */
	public int getType() {
		return isHeater() ? 0 : 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		if (isHeater()) {
			return slot == 0 && isItemFuel(item);
		}
		return slot < 4 && !getResult(item).isEmpty();
	}

	public boolean isItemFuel(ItemStack item) {
		return this.getItemBurnTime(item) > 0;
	}

	public int getCookProgressScaled(int i) {
		return (progress * i) / getMaxTime();
	}

	public boolean isBlockValidForSide(BlockPos pos) {
		if (world == null) {
			return false;
		}

		Block block = world.getBlockState(pos).getBlock();

		if (block == null) {
			return false;
		}

		if (block == world.getBlockState(pos).getBlock()) {
			return true;
		}
		return block == MineFantasyBlocks.FIREBRICKS || block == MineFantasyBlocks.FIREBRICK_STAIRS;
	}

	public boolean isBlockValidForTop(BlockPos pos) {
		if (world == null) {
			return false;
		}
		Block block = world.getBlockState(pos).getBlock();

		if (block == null) {
			return false;
		}
		return block == MineFantasyBlocks.FIREBRICKS || block == MineFantasyBlocks.FIREBRICK_STAIRS;
	}

	public boolean isBlockValidForSide(EnumFacing side) {
		if (world == null) {
			return false;
		}

		return isBlockValidForSide(pos.add(side.getXOffset(), side.getYOffset(), side.getZOffset()));
	}

	public boolean isSolid(EnumFacing side) {
		if (world == null) {
			return false;
		}

		Material mat = world.getBlockState(pos.add(side.getXOffset(), side.getYOffset(), side.getZOffset())).getMaterial();

		return mat.isSolid();
	}

	/**
	 * Determines if the furnace is built properly HEATER must have sides blocked by
	 * stone FURNACES must have walls built to specifications
	 */
	private boolean structureExists() {
		if (world == null) {
			return false;
		}

		IBlockState state = world.getBlockState(pos);
		if (!(world.getTileEntity(pos) instanceof TileEntityBigFurnace)) {
			return false;
		}
		EnumFacing facing = state.getValue(BlockBigFurnace.FACING);
		if (isSolid(facing)) {
			return false;
		}

		if (!isHeater() && !isBlockValidForTop(pos.add(0, 1, 0))) {
			return false;
		}

		if (!isBlockValidForSide(facing.rotateY())) {
			return false;
		}
		if (!isBlockValidForSide(facing.rotateYCCW())) {
			return false;
		}
		if (!isBlockValidForSide(facing.getOpposite())) {
			return false;
		}
		return true;
	}

	public ResourceLocation getTexture() {
		if (isHeater()) {
			return new ResourceLocation("minefantasyreforged:textures/blocks/furnace_heater.png");
		}
		return new ResourceLocation("minefantasyreforged:textures/blocks/furnace_rock.png");
	}

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		inventory.deserializeNBT(nbt.getCompoundTag("inventory"));

		ResourceLocation resourceLocation = new ResourceLocation(nbt.getString(RECIPE_RESOURCE_LOCATION_TAG));
		this.setRecipe(CraftingManagerBigFurnace.getRecipeByResourceLocation(resourceLocation));

		knownResearches = Utils.deserializeList(nbt.getString(KNOWN_RESEARCHES_TAG));

		justShared = nbt.getInteger("Shared");
		built = nbt.getBoolean("Built");

		opening = nbt.getBoolean("opening");

		fuel = nbt.getInteger("fuel");
		maxFuel = nbt.getInteger("MaxFuel");

		heat = nbt.getInteger("heat");
		maxHeat = nbt.getInteger("maxHeat");

		progress = nbt.getInteger("progress");
		aboveType = nbt.getInteger("Level");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("Shared", justShared);
		nbt.setInteger("Level", aboveType);
		nbt.setBoolean("Built", built);

		nbt.setBoolean("opening", opening);

		nbt.setInteger("fuel", fuel);
		nbt.setInteger("maxFuel", maxFuel);

		nbt.setInteger("heat", heat);
		nbt.setInteger("maxHeat", maxHeat);

		nbt.setInteger("progress", progress);

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
