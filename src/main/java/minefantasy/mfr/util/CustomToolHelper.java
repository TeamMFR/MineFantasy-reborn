package minefantasy.mfr.util;

import minefantasy.mfr.api.crafting.ITieredComponent;
import minefantasy.mfr.api.crafting.exotic.ISpecialDesign;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyMaterials;
import minefantasy.mfr.item.ItemHeated;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.List;

public class CustomToolHelper {
	public static final String slot_main = "main_material";
	public static final String slot_haft = "haft_material";
	/**
	 * A bit of the new system, gets custom materials for the head
	 */
	public static CustomMaterial getCustomPrimaryMaterial(ItemStack item) {
		if (item.isEmpty())
			return CustomMaterialRegistry.NONE;

		return CustomMaterialRegistry.getMaterialFor(item, slot_main);
	}

	public static CustomMaterial getCustomSecondaryMaterial(ItemStack item) {
		if (item.isEmpty())
			return CustomMaterialRegistry.NONE;

		CustomMaterial material = CustomMaterialRegistry.getMaterialFor(item, slot_haft);
		return material;
	}

	public static ItemStack construct(Item base, String main) {
		return construct(base, main, MineFantasyMaterials.Names.OAK_WOOD);
	}

	public static ItemStack construct(Item base, String main, String haft) {
		ItemStack item = new ItemStack(base);
		CustomMaterialRegistry.addMaterial(item, slot_main, main.toLowerCase());
		if (haft != null) {
			CustomMaterialRegistry.addMaterial(item, slot_haft, haft.toLowerCase());
		}
		return item;
	}

	public static ItemStack constructSingleColoredLayer(Item base, String main) {
		return constructSingleColoredLayer(base, main, 1);
	}

	public static ItemStack constructSingleColoredLayer(Item base, String main, int stacksize) {
		ItemStack item = new ItemStack(base, stacksize);
		CustomMaterialRegistry.addMaterial(item, slot_main, main.toLowerCase());
		return item;
	}

	/**
	 * Gets the rarity for a custom item
	 *
	 * @param itemRarity is the default id
	 */
	public static IRarity getRarity(ItemStack item, Rarity itemRarity) {
		int lvl = itemRarity.getRarityValue();
		CustomMaterial material = CustomMaterialRegistry.getMaterialFor(item, slot_main);
		if (material != null) {
			lvl = material.getRarity().getRarityValue();
		}

		if (item.isItemEnchanted()) {
			if (lvl == 0) {
				lvl++;
			}
			lvl++;
		}
		if (lvl >= Rarity.values().length) {
			lvl = Rarity.values().length - 1;
		}
		return Rarity.getRarityByValue(lvl);
	}

	/**
	 * Gets the max durability
	 *
	 * @param dura is the default dura
	 */
	public static int getMaxDamage(ItemStack stack, int dura) {
		CustomMaterial head = getCustomPrimaryMaterial(stack);
		CustomMaterial haft = getCustomSecondaryMaterial(stack);
		if (head != null && head != CustomMaterialRegistry.NONE) {
			dura = (int) (head.getDurability() * 100);
		}
		if (haft != null && haft != CustomMaterialRegistry.NONE) {
			dura += (int) (haft.getDurability() * 100);// Hafts add 50% to the durability
		}
		return ToolHelper.setDuraOnQuality(stack, dura);
	}

	/**
	 * Gets the colour for a layer
	 *
	 * @param layer 0 is base, haft is 1, 2 is detail
	 */
	public static int getColourFromItemStack(ItemStack item, int layer) {
		if (layer == 0) {
			CustomMaterial material = CustomMaterialRegistry.getMaterialFor(item, slot_main);
			if (material != null && material != CustomMaterialRegistry.NONE) {
				return material.getColourInt();
			}
		}
		if (layer == 1) {
			CustomMaterial material = CustomMaterialRegistry.getMaterialFor(item, slot_haft);
			if (material != null && material != CustomMaterialRegistry.NONE) {
				return material.getColourInt();
			}
		}
		return CustomMaterialRegistry.NONE.getColourInt();
	}

	public static float getWeightModifier(ItemStack item, float base) {
		CustomMaterial metal = getCustomPrimaryMaterial(item);
		CustomMaterial wood = getCustomSecondaryMaterial(item);

		if (metal != CustomMaterialRegistry.NONE) {
			base = (metal.getDensity() / 2.5F) * base;
		}
		if (wood != CustomMaterialRegistry.NONE) {
			base += (wood.getDensity() / 2.5F);
		}
		return base;
	}

	/**
	 * Gets the material modifier if it exists
	 *
	 * @param defaultModifier default if no material exists
	 */
	public static float getMeleeDamage(ItemStack item, float defaultModifier) {
		CustomMaterial custom = getCustomPrimaryMaterial(item);
		if (custom != null && custom != CustomMaterialRegistry.NONE) {
			return custom.getSharpness();
		}
		return defaultModifier;
	}

	public static float getBowDamage(ItemStack item, float defaultModifier) {
		CustomMaterial base = getCustomSecondaryMaterial(item);
		CustomMaterial joints = getCustomPrimaryMaterial(item);

		if (base != null && base != CustomMaterialRegistry.NONE) {
			defaultModifier = base.getFlexibility();
		}
		if (joints != null && joints != CustomMaterialRegistry.NONE) {
			defaultModifier *= joints.getFlexibility();
		}
		return defaultModifier;
	}

	/**
	 * The total damage of a bow and arrow
	 */
	public static float getBaseDamages(ItemStack item, float defaultModifier) {
		CustomMaterial custom = getCustomPrimaryMaterial(item);
		if (custom != null && custom != CustomMaterialRegistry.NONE) {
			return getBaseDamage(custom.getSharpness() * custom.getFlexibility());
		}
		return getBaseDamage(defaultModifier);
	}

	/**
	 * The damage a bow and arrow should do (same as a sword)
	 */
	public static float getBaseDamage(float modifier) {
		return 4F + modifier;
	}

	public static float getEfficiencyForHds(ItemStack item, float value, float mod) {
		CustomMaterial custom = getCustomPrimaryMaterial(item);
		if (custom != null && custom != CustomMaterialRegistry.NONE) {
			value = 2.0F + (custom.getHardness() * 4F);// Efficiency starts at 2 and each point of sharpness adds 2
		}
		return ToolHelper.modifyDigOnQuality(item, value) * mod;
	}

	public static float getEfficiency(ItemStack item, float value, float mod) {
		CustomMaterial custom = getCustomPrimaryMaterial(item);
		if (custom != null && custom != CustomMaterialRegistry.NONE) {
			value = 2.0F + (custom.getSharpness() * 2F);// Efficiency starts at 2 and each point of sharpness adds 2
		}
		return ToolHelper.modifyDigOnQuality(item, value) * mod;
	}

	public static int getCrafterTier(ItemStack item, int value) {
		CustomMaterial custom = getCustomPrimaryMaterial(item);
		if (custom != null && custom != CustomMaterialRegistry.NONE) {
			return custom.getCrafterTier();
		}
		return value;
	}

	public static int getHarvestLevel(ItemStack item, int value) {
		if (value <= 0) {
			return value;// If its not effective
		}

		CustomMaterial custom = getCustomPrimaryMaterial(item);
		if (custom != null && custom != CustomMaterialRegistry.NONE) {
			if (custom.getTier() == 0)
				return 1;
			if (custom.getTier() <= 2)
				return 2;
			return Math.max(custom.getTier(), 2);
		}
		return value;
	}

	@SideOnly(Side.CLIENT)
	public static void addInformation(ItemStack item, List<String> list) {
		CustomMaterial secondaryMaterial = getCustomSecondaryMaterial(item);

		if (materialOnTooltip()) {
			CustomMaterial mainMaterial = getCustomPrimaryMaterial(item);
			if (mainMaterial != null && mainMaterial != CustomMaterialRegistry.NONE) {
				String matName = I18n.translateToLocal(I18n.translateToLocal(Utils.convertSnakeCaseToSplitCapitalized(mainMaterial.getName())));
				list.add(TextFormatting.GOLD + matName);
			}
		}

		if (secondaryMaterial != null && secondaryMaterial != CustomMaterialRegistry.NONE) {
			String name;
			String localized_material;
			name = secondaryMaterial.getName();
			localized_material = I18n.translateToLocal("material." + name + ".name");
			if (!localized_material.endsWith(".name")) {
				name = localized_material;
			}
			String matName = I18n.translateToLocalFormatted("item.mod_haft.name", I18n.translateToLocal(Utils.convertSnakeCaseToSplitCapitalized(name)));
			list.add(TextFormatting.GOLD + matName);
		}

	}

	/**
	 * Gets if the language puts tiers in the tooltip, leaving the name blank
	 *
	 * @return material boolean
	 */
	public static boolean materialOnTooltip() {
		String cfg = I18n.translateToLocal("languagecfg.tooltiptier");
		return cfg.equalsIgnoreCase("true");
	}

	@SideOnly(Side.CLIENT)
	public static void addBowInformation(ItemStack item, List<String> list) {

		CustomMaterial material = getCustomPrimaryMaterial(item);
		if (material != null && material != CustomMaterialRegistry.NONE) {
			String name;
			String localized_material;
			name = material.getName();
			localized_material = net.minecraft.client.resources.I18n.format("material." + name + ".name");
			if (!localized_material.endsWith(".name")) {
				name = localized_material;
			}
			String matName = net.minecraft.client.resources.I18n.format(
					"item.mod_joint.name",
					net.minecraft.client.resources.I18n.format(Utils.convertSnakeCaseToSplitCapitalized(name)));
			list.add(TextFormatting.GOLD + matName);
		}

	}

	public static String getSecondaryLocalisedName(ItemStack item, String unlocalizedName) {
		if (materialOnTooltip()) {
			I18n.translateToLocal(unlocalizedName);
		}

		CustomMaterial material = getCustomSecondaryMaterial(item);
		String name = "any";
		String localized_material = null;
		if (material != null && material != CustomMaterialRegistry.NONE) {
			name = material.getName();
			localized_material = I18n.translateToLocal("material." + name + ".name");
		}
		if (localized_material != null && !localized_material.endsWith(".name")) {
			name = localized_material;
		}
		return I18n.translateToLocalFormatted(
				unlocalizedName,
				I18n.translateToLocal(Utils.convertSnakeCaseToSplitCapitalized(name)));
	}

	public static String getLocalisedName(ItemStack item, String unlocalizedName) {
		if (materialOnTooltip()) {
			return I18n.translateToLocal(unlocalizedName);
		}

		CustomMaterial material = getCustomPrimaryMaterial(item);
		String name = "any";
		String localized_material = null;
		if (material != null && material != CustomMaterialRegistry.NONE) {
			name = material.getName();
			localized_material = I18n.translateToLocal("material." + name + ".name");
		}
		if (localized_material != null && !localized_material.endsWith(".name")) {
			name = localized_material;
		}
		return I18n.translateToLocalFormatted(
				unlocalizedName,
				I18n.translateToLocal(Utils.convertSnakeCaseToSplitCapitalized(name)));
	}

	public static boolean areEqual(ItemStack recipeItem, ItemStack inputItem) {
		if (recipeItem.isEmpty()) {
			return inputItem.isEmpty();
		}
		if (inputItem.isEmpty())
			return false;

		return recipeItem.isItemEqual(inputItem) && doesMainMatchForRecipe(recipeItem, inputItem)
				&& doesHaftMatchForRecipe(recipeItem, inputItem);
	}

	/**
	 * Checks if two items' materials match
	 */
	public static boolean doesMatchForRecipe(ItemStack recipeItem, ItemStack inputItem) {
		return doesMainMatchForRecipe(recipeItem, inputItem) && doesHaftMatchForRecipe(recipeItem, inputItem);
	}

	/**
	 * Checks if two items' materials match
	 */
	public static boolean doesMatchForRecipe(Ingredient ingredient, ItemStack inputItem) {
		return Arrays.stream(ingredient.getMatchingStacks())
				.anyMatch(itemStack -> doesMainMatchForRecipe(itemStack, inputItem) && doesHaftMatchForRecipe(itemStack, inputItem));
	}

	public static boolean doesMainMatchForRecipe(ItemStack recipeItem, ItemStack inputItem) {
		CustomMaterial recipeMat = CustomToolHelper.getCustomPrimaryMaterial(recipeItem);
		CustomMaterial inputMat = CustomToolHelper.getCustomPrimaryMaterial(inputItem);

		if (recipeMat == null || recipeMat == CustomMaterialRegistry.NONE) {
			return true;
		}

		if ((inputMat == null || inputMat == CustomMaterialRegistry.NONE) && recipeMat != null) {
			return false;
		}
		return recipeMat == inputMat;
	}

	public static boolean doesHaftMatchForRecipe(ItemStack recipeItem, ItemStack inputItem) {
		CustomMaterial recipeMat = CustomToolHelper.getCustomSecondaryMaterial(recipeItem);
		CustomMaterial inputMat = CustomToolHelper.getCustomSecondaryMaterial(inputItem);

		if (recipeMat == null || recipeMat == CustomMaterialRegistry.NONE) {
			return true;
		}

		if ((inputMat == null || inputMat == CustomMaterialRegistry.NONE) && recipeMat != null) {
			return false;
		}
		return recipeMat == inputMat;
	}

	@SideOnly(Side.CLIENT)
	public static void addComponentString(List<String> list, CustomMaterial base) {
		addComponentString(list, base, 1);
	}

	@SideOnly(Side.CLIENT)
	public static void addComponentString(List<String> list, CustomMaterial base, float units) {
		if (base != null ) {
			float mass = base.getDensity() * units;
			if (base != CustomMaterialRegistry.NONE) {
				list.add(TextFormatting.GOLD + base.getMaterialString());
			}
			if (mass > 0) {
				list.add(CustomMaterialRegistry.getWeightString(mass));
			}

			if (base.isHeatable()) {
				int maxTemp = base.getHeatableStats()[0];
				int beyondMax = base.getHeatableStats()[1];
				list.add(net.minecraft.client.resources.I18n.format("materialtype.workable.name", maxTemp, beyondMax));
			}
		}
	}

	public static float getBurnModifier(ItemStack fuel) {
		CustomMaterial mat = CustomMaterialRegistry.getMaterialFor(fuel, slot_main);
		if (mat != null && mat != CustomMaterialRegistry.NONE && mat.getType() == CustomMaterialType.WOOD_MATERIAL) {
			return (2 * mat.getDensity()) + 0.5F;
		}
		return 1.0F;
	}

	public static String getReferenceName(ItemStack item) {
		String dam = "any";
		int d = item.getItemDamage();
		if (d != OreDictionary.WILDCARD_VALUE) {
			dam = "" + d;
		}

		return getReferenceName(item, dam);
	}

	public static String getReferenceName(ItemStack item, String dam) {
		return getReferenceName(item, dam, true);
	}

	public static String getReferenceName(ItemStack item, String dam, boolean tiered) {
		String reference = getSimpleReferenceName(item.getItem(), dam);

		if (tiered) {
			CustomMaterial base = getCustomPrimaryMaterial(item);
			CustomMaterial haft = getCustomSecondaryMaterial(item);

			if (base != null && base != CustomMaterialRegistry.NONE) {
				reference += "_" + base.getName();
			}
			if (haft != null && haft != CustomMaterialRegistry.NONE) {
				reference += "_" + haft.getName();
			}
		}

		return reference;
	}

	public static String getSimpleReferenceName(Item item, String dam) {
		String reference = String.valueOf(Item.REGISTRY.getNameForObject(item));
		if (reference == null) {
			return "";
		}
		return reference.toLowerCase() + "_@" + dam;
	}

	public static String getSimpleReferenceName(Item item) {
		return getSimpleReferenceName(item, "any");
	}

	public static boolean areToolsSame(ItemStack item1, ItemStack item2) {
		CustomMaterial mainMaterial1 = getCustomPrimaryMaterial(item1);
		CustomMaterial secondaryMaterial1 = getCustomSecondaryMaterial(item2);
		CustomMaterial mainMaterial2 = getCustomPrimaryMaterial(item1);
		CustomMaterial secondaryMaterial2 = getCustomSecondaryMaterial(item2);
		if (((mainMaterial1 == null || mainMaterial1 == CustomMaterialRegistry.NONE) && secondaryMaterial1 != null && secondaryMaterial1 != CustomMaterialRegistry.NONE) || ((secondaryMaterial1 == null || secondaryMaterial1 == CustomMaterialRegistry.NONE) && mainMaterial1 != null && mainMaterial1 != CustomMaterialRegistry.NONE))
			return false;
		if (((mainMaterial2 == null || mainMaterial2 == CustomMaterialRegistry.NONE) && secondaryMaterial2 != null && secondaryMaterial2 != CustomMaterialRegistry.NONE) || ((secondaryMaterial2 == null || secondaryMaterial2 == CustomMaterialRegistry.NONE) && mainMaterial2 != null && mainMaterial2 != CustomMaterialRegistry.NONE))
			return false;

		if (mainMaterial1 != null && secondaryMaterial1 != null && mainMaterial1 != secondaryMaterial1)
			return false;
		return mainMaterial2 == null || secondaryMaterial2 == null || mainMaterial2 == secondaryMaterial2;
	}

	public static boolean isMythic(ItemStack result) {
		CustomMaterial main1 = getCustomPrimaryMaterial(result);
		CustomMaterial haft1 = getCustomPrimaryMaterial(result);
		if (main1 != null  && main1 != CustomMaterialRegistry.NONE && main1.isUnbreakable()) {
			return true;
		}
		return haft1 != null && haft1 != CustomMaterialRegistry.NONE && haft1.isUnbreakable();
	}

	public static String getComponentMaterial(ItemStack item, CustomMaterialType type) {
		if (item.isEmpty() || type == null)
			return null;

		if (item.getItem() instanceof ItemHeated) {
			return getComponentMaterial(ItemHeated.getStack(item), type);
		}

		CustomMaterial material = CustomToolHelper.getCustomPrimaryMaterial(item);
		if (material != null && material != CustomMaterialRegistry.NONE) {
			return material.getType() == type ? material.getName() : null;
		}
		return null;
	}

	public static boolean hasAnyMaterial(ItemStack item) {
		return (getCustomPrimaryMaterial(item) != null && getCustomPrimaryMaterial(item) != CustomMaterialRegistry.NONE) || (getCustomSecondaryMaterial(item) != null && getCustomSecondaryMaterial(item) != CustomMaterialRegistry.NONE);
	}

	public static void tryDeconstruct(ItemStack newitem, ItemStack mainItem) {
		CustomMaterialType type = null;
		if (!newitem.isEmpty() && newitem.getItem() instanceof ITieredComponent) {
			type = ((ITieredComponent) newitem.getItem()).getMaterialType(newitem);
		}

		if (type != null) {
			CustomMaterial primary = CustomToolHelper.getCustomPrimaryMaterial(mainItem);
			CustomMaterial secondary = CustomToolHelper.getCustomSecondaryMaterial(mainItem);

			if (primary != null && primary != CustomMaterialRegistry.NONE && primary.getType() == type) {
				CustomMaterialRegistry.addMaterial(newitem, slot_main, primary.getName());
			} else {
				if (secondary != null && secondary != CustomMaterialRegistry.NONE && secondary.getType() == type) {
					CustomMaterialRegistry.addMaterial(newitem, slot_main, secondary.getName());
				}
			}
		}
	}

	public static String getCustomStyle(ItemStack weapon) {
		if (!weapon.isEmpty() && weapon.getItem() instanceof ISpecialDesign) {
			return ((ISpecialDesign) weapon.getItem()).getDesign(weapon);
		}
		return null;
	}

}
