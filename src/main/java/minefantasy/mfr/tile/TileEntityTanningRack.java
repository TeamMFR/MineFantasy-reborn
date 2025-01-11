package minefantasy.mfr.tile;

import minefantasy.mfr.block.BlockEngineerTanner;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.container.ContainerBase;
import minefantasy.mfr.init.MineFantasyBlocks;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.mechanics.RPGElements;
import minefantasy.mfr.recipe.CraftingManagerTanner;
import minefantasy.mfr.recipe.TannerRecipeBase;
import minefantasy.mfr.util.InventoryUtils;
import minefantasy.mfr.util.ToolHelper;
import minefantasy.mfr.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TileEntityTanningRack extends TileEntityBase implements ITickable {
	public float progress;
	public float maxProgress;
	public String tex = "";
	public float acTime;
	private final Random rand = new Random();
	private Set<String> knownResearches = new HashSet<>();

	public TileEntityTanningRack() {

	}

	private final ItemStackHandler inputInventory = createInventoryComparatorUpdate();
	private final ItemStackHandler recipeInventory = createInventory();

	@Override
	protected ItemStackHandler createInventory() {
		return new ItemStackHandler(1);
	}

	protected ItemStackHandler createInventoryComparatorUpdate() {
		return new ItemStackHandler(1) {
			@Override
			protected void onContentsChanged(int slot) {
				if (slot == 0) {
					world.updateComparatorOutputLevel(pos, blockType);
				}
				updateRecipe();
			}
		};
	}

	@Override
	public ItemStackHandler getInventory() {
		return new ItemStackHandler(NonNullList.from(
				inputInventory.getStackInSlot(0),
				recipeInventory.getStackInSlot(0))
		);
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
	public void update() {
		if (isAutomated()) {
			if (acTime > 0) {
				acTime -= (1F / 20);
			}
		}
	}

	public boolean interact(EntityPlayer player, boolean leftClick, boolean leverPull) {
		if (leverPull && acTime > 0) {
			return true;
		}
		if (player != null) {
			ItemStack held = player.getHeldItemMainhand();

			// Interaction
			Tool heldTool = ToolHelper.getToolTypeFromStack(held);
			TannerRecipeBase tannerRecipe = (TannerRecipeBase) getRecipe();
			if (!recipeInventory.getStackInSlot(0).isEmpty()
					&& (leverPull || heldTool == tannerRecipe.getToolType())) {

				if (leverPull || ToolHelper.getCrafterTier(held) >= tannerRecipe.getTannerTier()) {
					if (!leverPull) {
						held.damageItem(1, player);
						if (held.getItemDamage() >= held.getMaxDamage()) {
							player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
						}
					} else {
						world.playSound(player, pos.add(0.5D, 0.5D, 0.5D), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.AMBIENT, 0.75F, 0.85F);
						acTime = 1.0F;
					}

					float efficiency = leverPull ? 100F : ToolHelper.getCrafterEfficiency(held);
					if (!leverPull && player.swingProgress > 0 && player.swingProgress <= 1.0) {
						efficiency *= (0.5F - player.swingProgress);
					}

					if (efficiency > 0) {
						progress += efficiency;
					}
					if (tannerRecipe.getToolType() == Tool.SHEARS) {
						world.playSound(player, pos.add(0.5D, 0.5D, 0.5D), SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.AMBIENT, 1.0F, 1.0F);
					} else {
						world.playSound(player, pos.add(0.5D, 0.5D, 0.5D), SoundEvents.BLOCK_CLOTH_BREAK, SoundCategory.AMBIENT, 1.0F, 1.0F);
					}
					if (progress >= maxProgress) {
						progress = 0;
						int count = !inputInventory.getStackInSlot(0).isEmpty()
								? inputInventory.getStackInSlot(0).getCount()
								: 1;
						if (RPGElements.isSystemActive && !world.isRemote) {
							tannerRecipe.giveSkillXpPerCount(player, 1, count);
							tannerRecipe.giveVanillaXp(player, 0, count);
						}
						ItemStack out = recipeInventory.getStackInSlot(0).copy();
						out.setCount(out.getCount() * count);
						inputInventory.setStackInSlot(0, out);
						updateRecipe();
						if (isShabbyRack() && rand.nextInt(10) == 0 && !world.isRemote) {
							for (int a = 0; a < rand.nextInt(10); a++) {
								ItemStack plank = MineFantasyItems.TIMBER.construct(Constants.SCRAP_WOOD_TAG);
								world.playSound(player, pos.add(0.5D, 0.5D, 0.5D), SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, SoundCategory.AMBIENT, 1.0F, 1.0F);
								player.dropItem(plank, false);
							}
							world.setBlockToAir(pos);
							return true;
						}
					}
				}
				return true;
			}
			if (!leftClick && (heldTool == Tool.OTHER || heldTool == Tool.HANDS)) {
				// Item placement
				ItemStack item = inputInventory.getStackInSlot(0);
				if (item.isEmpty()) {
					if (!held.isEmpty() && !(held.getItem() instanceof ItemBlock)
							&& CraftingManagerTanner.findMatchingRecipe(held, this.knownResearches) != null) {
						ItemStack item2 = held.copy();
						item2.setCount(1);
						inputInventory.setStackInSlot(0, item2);
						tryDecrMainItem(player);
						updateRecipe();
						world.playSound(player, pos.add(0.5D, 0.5D, 0.5D), SoundEvents.ENTITY_HORSE_SADDLE, SoundCategory.AMBIENT, 1.0F, 1.0F);
						return true;
					}
				} else {
					if (!player.inventory.addItemStackToInventory(item)) {
						player.entityDropItem(item, 0.0F);
					}
					inputInventory.setStackInSlot(0, ItemStack.EMPTY);
					updateRecipe();
					world.playSound(player, pos.add(0.5D, 0.5D, 0.5D), SoundEvents.ENTITY_HORSE_SADDLE, SoundCategory.AMBIENT, 1.0F, 1.0F);
					return true;
				}
			}
		}
		else if (leverPull) {
			//Handle Automated interaction
			if (!recipeInventory.getStackInSlot(0).isEmpty() && getRecipe() instanceof TannerRecipeBase) {
				TannerRecipeBase tannerRecipe = (TannerRecipeBase) getRecipe();
				world.playSound(null, pos.add(0.5D, 0.5D, 0.5D), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.AMBIENT, 0.75F, 0.85F);
				acTime = 1.0F;

				float efficiency = 100F;

				progress += efficiency;
				if (progress >= tannerRecipe.getCraftTime()) {
					progress = 0;
					int ss = !inputInventory.getStackInSlot(0).isEmpty() ? inputInventory.getStackInSlot(0).getCount() : 1;
					ItemStack out = recipeInventory.getStackInSlot(0).copy();
					out.setCount(out.getCount() * ss);
					inputInventory.setStackInSlot(0, out);
					updateRecipe();
				}
				return true;
			}
		}
		return false;
	}

	public void setKnownResearches(Set<String> knownResearches) {
		this.knownResearches = knownResearches;
	}

	public boolean isAutomated() {
		if (world == null) {
			return tex.equalsIgnoreCase("metal");
		}
		return world.getBlockState(pos).getBlock() instanceof BlockEngineerTanner;
	}

	private void tryDecrMainItem(EntityPlayer player) {
		int held = player.inventory.currentItem;
		if (held >= 0 && held < 9) {
			player.inventory.decrStackSize(held, 1);
		}
	}

	public void updateRecipe() {
		TannerRecipeBase recipe = CraftingManagerTanner.findMatchingRecipe(inputInventory.getStackInSlot(0), this.knownResearches);
		if (recipe == null) {
			recipeInventory.setStackInSlot(0, ItemStack.EMPTY);
			maxProgress = 0;
		} else {
			setRecipe(recipe);
			recipeInventory.setStackInSlot(0, recipe.getTannerRecipeOutput().copy());
			maxProgress = recipe.getCraftTime();
		}
		progress = 0;
		sendUpdates();
	}

	public ItemStackHandler getInputInventory() {
		return inputInventory;
	}

	public ItemStackHandler getRecipeInventory() {
		return recipeInventory;
	}

	public boolean doesPlayerKnowCraft() {
		return true;
	}

	public int getProgressBar(int i) {
		if (maxProgress <= 0)
			return 0;
		return (int) (i / maxProgress * progress);
	}

	public String getResultName() {
		if (!recipeInventory.getStackInSlot(0).isEmpty()) {
			return recipeInventory.getStackInSlot(0).getDisplayName();
		}
		return "";
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return false;
	}

	private boolean isShabbyRack() {
		return world.getBlockState(pos).getBlock() == MineFantasyBlocks.TANNER;
	}

	@Override
	public void onBlockBreak() {
		if (!recipeInventory.getStackInSlot(0).isEmpty() && !(recipeInventory.getStackInSlot(0).getItem() instanceof ItemBlock)) {
			recipeInventory.setStackInSlot(0, ItemStack.EMPTY);
		}
		InventoryUtils.dropItemsInWorld(world, inputInventory, pos);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		acTime = nbt.getFloat("acTime");
		tex = nbt.getString("tex");
		progress = nbt.getFloat("Progress");
		maxProgress = nbt.getFloat("maxProgress");

		inputInventory.deserializeNBT(nbt.getCompoundTag("inputInventory"));
		recipeInventory.deserializeNBT(nbt.getCompoundTag("outputInventory"));

		ResourceLocation resourceLocation = new ResourceLocation(nbt.getString(RECIPE_RESOURCE_LOCATION_TAG));
		this.setRecipe(CraftingManagerTanner.getRecipeByResourceLocation(resourceLocation));

		knownResearches = Utils.deserializeList(nbt.getString(KNOWN_RESEARCHES_TAG));
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setFloat("acTime", acTime);
		nbt.setString("tex", tex);
		nbt.setFloat("Progress", progress);
		nbt.setFloat("maxProgress", maxProgress);
		nbt.setTag("inputInventory", inputInventory.serializeNBT());
		nbt.setTag("outputInventory", recipeInventory.serializeNBT());
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
			//if the block isn't itself, its broken, allow full access
			if(facing == null || world != null && world.getBlockState(pos).getBlock() != getBlockType()) {
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getInventory());
			}
			else {
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getInputInventory());
			}
		}
		return super.getCapability(capability, facing);
	}
}
