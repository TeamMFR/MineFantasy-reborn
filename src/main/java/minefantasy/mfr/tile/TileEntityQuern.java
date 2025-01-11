package minefantasy.mfr.tile;

import minefantasy.mfr.container.ContainerBase;
import minefantasy.mfr.container.ContainerQuern;
import minefantasy.mfr.init.MineFantasySounds;
import minefantasy.mfr.network.NetworkHandler;
import minefantasy.mfr.recipe.CraftingManagerQuern;
import minefantasy.mfr.recipe.IRecipeMFR;
import minefantasy.mfr.recipe.QuernRecipeBase;
import minefantasy.mfr.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class TileEntityQuern extends TileEntityBase implements ITickable {
	private int postUseTicks;
	private int turnAngle;
	private Set<String> knownResearches = new HashSet<>();

	public static int getMaxRevs() {
		return 100;
	}

	public final ItemStackHandler inventory = createInventory();

	@Override
	protected ItemStackHandler createInventory() {
		return new ItemStackHandler(3);
	}

	@Override
	public ItemStackHandler getInventory() {
		return this.inventory;
	}

	@Override
	public ContainerBase createContainer(EntityPlayer player) {
		return new ContainerQuern(player, player.inventory, this);
	}

	@Override
	protected int getGuiId() {
		return NetworkHandler.GUI_QUERN;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void update() {
		int max = getMaxRevs();
		int levels = max / 4;

		if (postUseTicks > 0) {
			--postUseTicks;
		}
		if (postUseTicks > 0 || !((turnAngle == levels || turnAngle == levels * 2 || turnAngle == levels * 3 || turnAngle == 0))) {
			this.turnAngle++;
			if (!world.isRemote && (turnAngle == levels || turnAngle == levels * 2 || turnAngle == levels * 3 || turnAngle == max)) {
				world.playSound(null, pos, MineFantasySounds.QUERN, SoundCategory.BLOCKS, 1.0F, 1.0F);
				onRevolutionComplete();
			}
			if (turnAngle >= max) {
				turnAngle = 0;
			}
		}
	}

	public boolean isInput(ItemStack input) {
		return !input.isEmpty() && CraftingManagerQuern.findMatchingInputs(input, knownResearches);
	}

	public boolean isPot(ItemStack inputPot) {
		return !inputPot.isEmpty() && CraftingManagerQuern.findMatchingPotInputs(inputPot, knownResearches);
	}

	private QuernRecipeBase getResult(ItemStack input, ItemStack potInput, Set<String> knownResearches) {
		QuernRecipeBase quernRecipe = CraftingManagerQuern.findMatchingRecipe(input, potInput, knownResearches);
		setRecipe(quernRecipe);
		return quernRecipe;
	}

	@Override
	public IRecipeMFR getRecipeByOutput(ItemStack stack) {
		return CraftingManagerQuern.findRecipeByOutput(stack);
	}

	public void onUse() {
		if (postUseTicks == 0) {
			world.playSound(null, pos, MineFantasySounds.QUERN, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
		this.postUseTicks = 10;
	}

	public void onRevolutionComplete() {
		world.playSound(null, pos, MineFantasySounds.CRAFT_PRIMITIVE, SoundCategory.BLOCKS, 1.0F, 1.0F);
		QuernRecipeBase result = getResult(getInventory().getStackInSlot(0), getInventory().getStackInSlot(1), knownResearches);
		if (result != null && (!result.shouldConsumePot() || !getInventory().getStackInSlot(1).isEmpty())) {
			ItemStack craft = result.getQuernRecipeOutput();
			if (canFitResult(craft)) {
				if (!world.isRemote) {
					tryCraft(craft, result.shouldConsumePot());
				} else {
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F, 0F, 0.2F, 0F);
				}
			}
		}
	}

	private boolean canFitResult(ItemStack result) {
		ItemStack out = getInventory().getStackInSlot(2);
		if (out.isEmpty())
			return true;

		if (!out.isItemEqual(result))
			return false;
		return out.getCount() + result.getCount() <= result.getMaxStackSize();
	}

	private void tryCraft(ItemStack result, boolean consumePot) {
		world.playSound(null, pos, MineFantasySounds.CRAFT_PRIMITIVE, SoundCategory.BLOCKS, 1.0F, 1.0F);
		/*
		 * if(rand.nextFloat() > 0.20F)//20% success rate {
		 * worldObj.playSoundEffect(xCoord, yCoord, zCoord, "dig.gravel", 1.0F, 0.5F);
		 * return false; } else
		 */

		this.getInventory().extractItem(0, 1, false);
		if (consumePot) {
			this.getInventory().extractItem(1, 1, false);
		}
		ItemStack out = getInventory().getStackInSlot(2);
		if (out.isEmpty()) {
			this.getInventory().setStackInSlot(2, result.copy());
		} else {
			out.grow(result.getCount());
		}

	}

	public int getTurnAngle() {
		return turnAngle;
	}

	public void setKnownResearches(Set<String> knownResearches) {
		this.knownResearches = knownResearches;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		if (!item.isEmpty() && isInput(item)) {
			return slot == 0;
		}
		if (!item.isEmpty() && isPot(item)) {
			return slot == 1;
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
		ResourceLocation resourceLocation = new ResourceLocation(nbt.getString(RECIPE_RESOURCE_LOCATION_TAG));
		this.setRecipe(CraftingManagerQuern.getRecipeByResourceLocation(resourceLocation));

		knownResearches = Utils.deserializeList(nbt.getString(KNOWN_RESEARCHES_TAG));
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
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