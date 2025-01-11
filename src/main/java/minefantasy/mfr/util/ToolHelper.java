package minefantasy.mfr.util;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.crafting.CustomCrafterEntry;
import minefantasy.mfr.api.tier.IToolMaterial;
import minefantasy.mfr.api.tool.IToolMFR;
import minefantasy.mfr.api.weapon.ISharpenable;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.item.ItemWashCloth;
import minefantasy.mfr.recipe.CraftingManagerTransformation;
import minefantasy.mfr.recipe.TransformationRecipeBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ToolHelper {
	// CRAFTER TOOLS//

	public static final String sharpnessLevelNBT = "MF_Sharpness_Level";
	private static String specialItem = "MF_SpecialItemType";

	public static boolean shouldShowTooltip(ItemStack stack) {
		Tool tool = getToolTypeFromStack(stack);
		return !(tool == Tool.HANDS || tool == Tool.OTHER);
	}

	public static float getCrafterEfficiency(ItemStack tool) {
		if (tool.isEmpty()) {
			return 1F;
		}
		if (tool.getItem() instanceof IToolMFR) {
			return ((IToolMFR) tool.getItem()).getEfficiency(tool);
		}
		return CustomCrafterEntry.getEntryEfficiency(tool);
	}

	// MATERIALS//

	public static int getCrafterTier(ItemStack tool) {
		if (tool.isEmpty()) {
			return 0;
		}
		if (tool.getItem() instanceof IToolMFR) {
			return ((IToolMFR) tool.getItem()).getTier(tool);
		}
		return CustomCrafterEntry.getEntryTier(tool);
	}

	public static boolean isStackValidWashTool(ItemStack stack) {
		return ToolHelper.getToolTypeFromStack(stack) == Tool.WASH && stack.getItemDamage() != ToolHelper.getWashMaxUses(stack);
	}

	public static int getWashMaxUses(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		}
		if (stack.getItem() instanceof ItemWashCloth) {
			return ((ItemWashCloth) stack.getItem()).getMaxUses();
		}
		return 0; //Todo add Custom Wash Entry
	}

	/**
	 * Conduct Block Transformation on {@link Item#onItemUse(EntityPlayer, World, BlockPos, EnumHand, EnumFacing, float, float, float)}
	 * @param player The Player using the item
	 * @param world The World
	 * @param pos The Block Position of the block being transformed
	 * @param hand The hand the Player is using the item with
	 * @param facing The facing of the action
	 * @return The {@link EnumActionResult} which represents the result of the transformation
	 */
	public static EnumActionResult performBlockTransformation(
			EntityPlayer player,
			World world,
			BlockPos pos,
			EnumHand hand,
			EnumFacing facing) {

		ItemStack tool = player.getHeldItem(hand);
		IBlockState oldState = world.getBlockState(pos);

		// Find Recipe
		ItemStack input = new ItemStack(oldState.getBlock());
		if (input.getItem().getHasSubtypes()) {
			input = new ItemStack(oldState.getBlock(), 1, oldState.getBlock().getMetaFromState(oldState));
		}
		TransformationRecipeBase recipe = CraftingManagerTransformation
				.findMatchingRecipe(tool, input, oldState, pos, player, facing);

		if (recipe == null) {
			return EnumActionResult.FAIL;
		}

		if (!world.isRemote) {
			//We assume the recipe's transformation is valid
			return recipe.onUsedWithBlock(world, pos, oldState, tool, player, facing);
		}
		else {
			player.swingArm(hand);
			if (recipe.getSound() != null) {
				world.playSound(player, pos, recipe.getSound(), SoundCategory.BLOCKS, 1F, 1F);
			}
		}
		return EnumActionResult.FAIL;
	}

	/**
	 * Checks the specified ItemStack's Tool type. Returns OTHER if it has no valid tool nbt tag
	 *
	 * @param stack The item stack to check
	 * @return
	 */
	public static Tool getToolTypeFromStack(ItemStack stack) {
		if (stack == null) {
			MineFantasyReforged.LOG.warn("Attempted to get the tool type of a null ItemStack");
			return Tool.HANDS;
		}

		if (stack.isEmpty()) {
			return Tool.HANDS;
		}

		if (stack.getItem() instanceof IToolMFR) {
			return ((IToolMFR) stack.getItem()).getToolType(stack);
		}

		if (CustomCrafterEntry.getEntry(stack) != null) {
			return CustomCrafterEntry.getEntryType(stack);
		}

		return Tool.OTHER;
	}

	/**
	 * Compares an itemstack to see if the item is made from the said material ONLY
	 * APPLIES TO IToolMaterial in api.
	 */
	public static boolean isItemMaterial(ItemStack stack, ToolMaterial material) {
		if (!stack.isEmpty()) {
			// You need the item to implement this so it can see
			if (stack.getItem() instanceof IToolMaterial) {
				IToolMaterial mat = (IToolMaterial) stack.getItem();

				return mat.getMaterial() != null && mat.getMaterial() == material;
			}
		}
		return false;
	}

	public static ItemStack setQuality(ItemStack item, float qualityLvl) {
		if (item.getMaxStackSize() > 0)
			return item;

		NBTTagCompound nbt = getOrCreateNBT(item);
		nbt.setFloat("MFCraftQuality", qualityLvl);

		return item;
	}

	/**
	 * Gets how well an item has been crafted: 100 is default ranging from 0-200
	 */
	public static float getQualityLevel(ItemStack stack) {
		if (stack.getMaxStackSize() == 1) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("MFCraftQuality")) {
				return stack.getTagCompound().getFloat("MFCraftQuality");
			}
		}
		return 100.0F;
	}

	public static int setDuraOnQuality(ItemStack item, int dura) {
		float quality = getQualityLevel(item);
		if (item.hasTagCompound() && item.getTagCompound().hasKey("MF_Inferior")) {
			if (item.getTagCompound().getBoolean("MF_Inferior")) {
				dura /= 2;
			} else {
				dura *= 2;
			}
		}

		if (quality > 100) {
			dura += ((dura) / 100F * (quality - 100));// This means 100+ adds to 2x durability at level 200
		}
		if (quality < 100) {
			dura -= ((dura * 0.75) / 100 * (100F - quality));// This means 100- takes upto 75% from it's max durability
			// lvl0 is 25%dura
		}
		return dura;
	}

	public static float modifyDigOnQuality(ItemStack item, float digspeed) {
		if (item.hasTagCompound() && item.getTagCompound().hasKey("MF_Inferior")) {
			if (item.getTagCompound().getBoolean("MF_Inferior")) {
				digspeed /= 1.25F;
			} else {
				digspeed *= 1.25F;
			}
		}
		float quality = getQualityLevel(item);

		if (quality > 100) {
			digspeed += ((digspeed * 0.5F) / 100F * (quality - 100));// This means 100+ adds 50% speed at level 200
		}
		if (quality < 100) {
			digspeed -= ((digspeed * 0.5) / 100 * (100F - quality));// This means 100- takes upto 50% from it's max
			// speed lvl0 is 50% speed
		}
		return digspeed;
	}

	// NBT//

	public static float modifyDamOnQuality(ItemStack item, float damage) {
		float quality = getQualityLevel(item);
		if (item.hasTagCompound() && item.getTagCompound().hasKey("MF_Inferior")) {
			if (item.getTagCompound().getBoolean("MF_Inferior")) {
				damage /= 1.25F;
			} else {
				damage *= 1.25F;
			}
		}
		if (quality > 100) {
			damage += ((damage * 0.25F) / 100F * (quality - 100));// This means 100+ adds 25% damage at level 200
		}
		if (quality < 100) {
			damage -= ((damage * 0.25) / 100 * (100F - quality));// This means 100- takes upto 25% from it's max damage
			// lvl0 is 75% damage
		}
		return damage;
	}

	public static float modifyArmourRating(ItemStack item, float rating) {
		float quality = getQualityLevel(item);
		if (item.hasTagCompound() && item.getTagCompound().hasKey("MF_Inferior")) {
			if (item.getTagCompound().getBoolean("MF_Inferior")) {
				rating /= 1.25F;
			} else {
				rating *= 1.25F;
			}
		}

		if (quality > 100) {
			rating += ((rating * 0.5F) / 100F * (quality - 100));// This means 100+ adds 50% armour at level 200
		}
		if (quality < 100) {
			rating -= ((rating * 0.5) / 100 * (100F - quality));// This means 100- takes upto 50% from it's max armour
			// lvl0 is 50% armour
		}
		return rating;
	}

	private static NBTTagCompound getOrCreateNBT(ItemStack item) {
		if (!item.hasTagCompound()) {
			item.setTagCompound(new NBTTagCompound());
		}

		return item.getTagCompound();
	}

	public static boolean hasCustomQualityTag(ItemStack item) {
		return item.hasTagCompound() && item.getTagCompound().hasKey("MFCraftQuality");
	}

	public static void setSpecial(ItemStack item, String type) {
		if (!item.hasTagCompound()) {
			item.setTagCompound(new NBTTagCompound());
		}
		item.getTagCompound().setString(specialItem, type);
	}

	public static boolean isSpecial(ItemStack item, String type) {
		return getSpecial(item) != null && getSpecial(item).equals(type);
	}

	public static String getSpecial(ItemStack item) {
		if (item.hasTagCompound() && item.getTagCompound().hasKey(specialItem)) {
			return item.getTagCompound().getString(specialItem);
		}
		return null;
	}

	public static void setToolSharpness(ItemStack item, float level) {
		NBTTagCompound nbt = getOrCreateNBT(item);
		float currentLevel = getSharpnessLevel(item);
		float maxLevel = getMaxSharpness(item);
		nbt.setFloat(sharpnessLevelNBT, Math.min(maxLevel, currentLevel + level));
	}

	public static float getSharpnessLevel(ItemStack item) {
		NBTTagCompound nbt = item.getTagCompound();
		if (nbt != null && nbt.hasKey(sharpnessLevelNBT)) {
			return nbt.getFloat(sharpnessLevelNBT);
		}
		return 0F;
	}

	public static boolean canBeSharpened(ItemStack itemstack, float level) {
		if (itemstack.isEmpty())
			return false;
		return false;
	}

	/**
	 * The max amount of uses of sharpness
	 */
	public static float getMaxSharpness(ItemStack item) {
		return getSharpnessTraits(item)[0];
	}

	/**
	 * The max percent that it can increase damage
	 */
	public static float getMaxSharpnessPercent(ItemStack item) {
		return getSharpnessTraits(item)[1];
	}

	/**
	 * The modifier for how much percent is added each use
	 */
	public static float getSharpUsesModifier(ItemStack item) {
		return getSharpnessTraits(item)[2];
	}

	// maxSharpness, maxBuff, sharpnessModifier
	public static float[] getSharpnessTraits(ItemStack item) {
		float[] list = new float[] {100, 20F, 3F};

		if (!item.isEmpty() && item.getItem() instanceof ISharpenable) {
			ISharpenable instance = (ISharpenable) item.getItem();
			list[0] = instance.getMaxSharpness(item);
			list[1] = instance.getDamagePercentMax(item);
			list[2] = instance.getSharpUsesModifier(item);
		}
		return list;
	}

	public static String[] breakdownLineForResearchArray(String string) {
		String temp = "";
		ArrayList<String> entries = new ArrayList<String>();

		for (int a = 0; a < string.length(); a++) {
			if (a == string.length() - 1) {
				temp = temp + string.charAt(a);
			}
			if (string.charAt(a) == " ".charAt(0) || a == string.length() - 1) {
				entries.add(temp);
			} else {
				if (string.charAt(a) != " ".charAt(0)) {
					temp = temp + string.charAt(a);
				}
			}
		}
		int size = entries.size();
		String[] stringList = new String[size];
		for (int i = 0; i < size; i++) {
			stringList[i] = entries.get(i);
		}
		return stringList;
	}

	@Deprecated
	public static boolean isToolSufficient(ItemStack heldItem, String toolNeeded, int toolTierNeeded) {
		Tool tool = getToolTypeFromStack(heldItem);

		int tier = getCrafterTier(heldItem);

		return tool.getName().equals(toolNeeded) && tier >= toolTierNeeded;
	}

	public static boolean isToolSufficient(ItemStack heldItem, Tool toolNeeded, int toolTierNeeded) {
		Tool tool = getToolTypeFromStack(heldItem);

		int tier = getCrafterTier(heldItem);

		return tool == toolNeeded && tier >= toolTierNeeded;
	}

	public static void setUnbreakable(ItemStack tool, boolean isUnbreakable) {
		if (!tool.hasTagCompound()) {
			tool.setTagCompound(new NBTTagCompound());
		}
		tool.getTagCompound().setBoolean(Constants.UNBREAKABLE_TAG, isUnbreakable);
	}
}
