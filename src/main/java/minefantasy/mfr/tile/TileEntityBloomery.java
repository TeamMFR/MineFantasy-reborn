package minefantasy.mfr.tile;

import minefantasy.mfr.api.crafting.MineFantasyCarbons;
import minefantasy.mfr.api.refine.SmokeMechanics;
import minefantasy.mfr.block.BlockBloomery;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.container.ContainerBase;
import minefantasy.mfr.container.ContainerBloomery;
import minefantasy.mfr.entity.EntityItemHeated;
import minefantasy.mfr.init.MineFantasySounds;
import minefantasy.mfr.item.ItemHeated;
import minefantasy.mfr.mechanics.RPGElements;
import minefantasy.mfr.network.NetworkHandler;
import minefantasy.mfr.recipe.BloomeryRecipeBase;
import minefantasy.mfr.recipe.CraftingManagerBloomery;
import minefantasy.mfr.recipe.IRecipeMFR;
import minefantasy.mfr.util.InventoryUtils;
import minefantasy.mfr.util.MFRLogUtil;
import minefantasy.mfr.util.ToolHelper;
import minefantasy.mfr.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
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
import java.util.Random;
import java.util.Set;

public class TileEntityBloomery extends TileEntityBase implements ITickable {
	private int ticksExisted;
	private float progress;
	private float progressMax;
	private boolean hasBloom;
	private boolean isActive;
	private Set<String> knownResearches = new HashSet<>();
	private final Random rand = new Random();

	private final int OUT_SLOT = 2;

	public final ItemStackHandler inventory = createInventory();

	@Override
	protected ItemStackHandler createInventory() {
		return new ItemStackHandler(3) {
			@Override
			protected void onContentsChanged(int slot) {
				IBlockState iblockstate = world.getBlockState(pos);
				world.setBlockState(pos, iblockstate.withProperty(BlockBloomery.BLOOM, hasBloom()));
				markDirty();
			}
		};
	}

	@Override
	public ItemStackHandler getInventory() {
		return this.inventory;
	}

	@Override
	public ContainerBase createContainer(EntityPlayer player) {
		return new ContainerBloomery(player.inventory, this);
	}

	@Override
	protected int getGuiId() {
		return NetworkHandler.GUI_BLOOMERY;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public boolean isActive() {
		return isActive;
	}

	public void setIsActive(boolean active) {
		isActive = active;
	}

	public void setProgressMax(float newMax) {
		progressMax = newMax;
	}

	public float getSmeltTime() {
		float smeltTime = getInventory().getStackInSlot(0).getCount() * getTime(getInventory().getStackInSlot(0));// 15s per item
		return smeltTime;
	}

	private int getTime(ItemStack itemStack) {
		return 300;
	}

	public boolean isInput(ItemStack input) {
		return !getResult(input).isEmpty();
	}

	private ItemStack getResult(ItemStack input) {
		BloomeryRecipeBase recipe = CraftingManagerBloomery.findMatchingRecipe(input, this.knownResearches);
		if (recipe != null) {
			this.setRecipe(recipe);
			return recipe.getBloomeryRecipeOutput().copy();
		}
		else {
			return ItemStack.EMPTY;
		}
	}

	public ItemStack getResult() {
		ItemStack input = inventory.getStackInSlot(0);
		ItemStack coal = inventory.getStackInSlot(1);

		if (hasBloom())
			return ItemStack.EMPTY;// Cannot smelt if a bloom exists
		if (input.isEmpty() || coal.isEmpty())
			return ItemStack.EMPTY;// Needs input
		if (!hasEnoughCarbon(input, coal)) {
			return ItemStack.EMPTY;
		}

		return getResult(input);
	}

	private boolean hasEnoughCarbon(ItemStack input, ItemStack coal) {
		int amount = input.getCount();
		int uses = MineFantasyCarbons.getCarbon(coal);
		if (uses > 0) {
			int coalNeeded = (int) Math.ceil((float) amount / (float) uses);
			MFRLogUtil.logDebug("Required Coal: " + coalNeeded);
			return coal.getCount() == coalNeeded;
		}
		return false;
	}

	@Override
	public void update() {

		if (isActive && progressMax > 0) {
			if (!world.canBlockSeeSky(pos.add(0, 1, 0))) {
				progressMax = progress = 0;
				isActive = false;
				return;
			}
			ticksExisted++;
			if (!world.isRemote) {
				++progress;
				if (progress >= progressMax) {
					smeltItem();
				}
				if (rand.nextInt(4) == 0) {
					SmokeMechanics.spawnSmoke(world, pos, 1);
				}
				if (ticksExisted % 20 == 0) {
					sendUpdates();
				}
			}
		} else if (isActive) {
			isActive = false;
		}
	}

	/**
	 * Consumes ALL input and sets output
	 */
	public void smeltItem() {
		ItemStack result = getResult();
		if (!result.isEmpty()) {
			ItemStack result2 = result.copy();
			result2.setCount(getInventory().getStackInSlot(0).getCount());
			getInventory().setStackInSlot(0, ItemStack.EMPTY);
			getInventory().setStackInSlot(1, ItemStack.EMPTY);
			getInventory().setStackInSlot(OUT_SLOT, result2);
		}
		isActive = false;
		progress = progressMax = 0;
	}

	public boolean tryHammer(EntityPlayer user) {
		if (world.getBlockState(pos.add(0, 1, 0)).getMaterial().isSolid()) {
			return false;
		}
		ItemStack held = user.getHeldItemMainhand();
		if (!hasBloom() || isActive) {
			return false;
		}

		Tool tool = ToolHelper.getToolTypeFromStack(held);
		float pwr = ToolHelper.getCrafterEfficiency(held);
		if (tool == Tool.HAMMER || tool == Tool.HEAVY_HAMMER) {
			if (user.world.isRemote)
				return true;

			held.damageItem(1, user);
			if (held.getItemDamage() >= held.getMaxDamage()) {
				user.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}

			if (rand.nextFloat() * 10F < pwr) {
				ItemStack drop = inventory.getStackInSlot(OUT_SLOT).copy();
				IRecipeMFR recipe = CraftingManagerBloomery.findRecipeByOutput(drop);
				if (recipe == null) {
					return false;
				}
				inventory.extractItem(OUT_SLOT, 1, false);
				if (inventory.getStackInSlot(OUT_SLOT).getCount() <= 0) {
					inventory.setStackInSlot(OUT_SLOT, ItemStack.EMPTY);
				}
				// Only gain xp up to level 20
				if (RPGElements.isSystemActive && RPGElements.getLevel(user, recipe.getSkill()) <= 20) {
					recipe.giveVanillaXp(user, 0, 1);
					recipe.giveSkillXp(user, 0);
				}
				drop.setCount(1);
				drop = ItemHeated.createHotItem(drop, 1200);
				entityDropItem(world, pos, drop);
			}
			world.playSound(null, pos, MineFantasySounds.ANVIL_SUCCEED, SoundCategory.AMBIENT, 0.25F, 1.0F);

			return true;
		}
		return false;
	}

	public EntityItem entityDropItem(World world, BlockPos pos, ItemStack item) {
		if (item.getCount() != 0 && !item.isEmpty()) {
			EntityItem entityitem = new EntityItemHeated(world, pos.getX() + 0.5D, pos.getY() + 1.25F, pos.getZ() + 0.5D, item);
			entityitem.setPickupDelay(10);
			item.getTagCompound().setBoolean("bloomery", true);
			world.spawnEntity(entityitem);
			entityitem.motionX = entityitem.motionY = entityitem.motionZ = 0;
			return entityitem;
		} else {
			return null;
		}
	}

	public void setKnownResearches(Set<String> knownResearches) {
		this.knownResearches = knownResearches;
	}

	public boolean hasBloom() {
		if (world.isRemote) {
			return hasBloom;
		}
		return !getInventory().getStackInSlot(OUT_SLOT).isEmpty();
	}

	public boolean hasNoBloom() {
		return getInventory().getStackInSlot(OUT_SLOT).isEmpty();
	}

	public boolean isItemValidForSlot(int slot, ItemStack item) {
		if (!item.isEmpty() && MineFantasyCarbons.isCarbon(item)) {
			return slot == 1;
		}
		if (!item.isEmpty() && !getResult(item).isEmpty()) {
			return slot == 0;
		}
		return false;
	}

	@Override
	public void onBlockBreak() {
		if (!inventory.getStackInSlot(inventory.getSlots() - 1).isEmpty() && !(inventory.getStackInSlot(inventory.getSlots() - 1).getItem() instanceof ItemBlock)) {
			inventory.setStackInSlot(OUT_SLOT, ItemStack.EMPTY);
		}
		InventoryUtils.dropItemsInWorld(world, inventory, pos);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		inventory.deserializeNBT(nbt.getCompoundTag("inventory"));

		ResourceLocation resourceLocation = new ResourceLocation(nbt.getString(RECIPE_RESOURCE_LOCATION_TAG));
		this.setRecipe(CraftingManagerBloomery.getRecipeByResourceLocation(resourceLocation));

		knownResearches = Utils.deserializeList(nbt.getString(KNOWN_RESEARCHES_TAG));

		progress = nbt.getFloat("Progress");
		progressMax = nbt.getFloat("ProgressMax");
		isActive = nbt.getBoolean("isActive");
		hasBloom = nbt.getBoolean("hasBloom");
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

		nbt.setFloat("Progress", progress);
		nbt.setFloat("ProgressMax", progressMax);
		nbt.setBoolean("isActive", isActive);
		nbt.setBoolean("hasBloom", hasBloom);
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
