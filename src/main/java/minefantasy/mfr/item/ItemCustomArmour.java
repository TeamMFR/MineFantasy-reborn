package minefantasy.mfr.item;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.armour.ArmourDesign;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.init.MineFantasyMaterials;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.mechanics.CombatMechanics;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.util.ArmourCalculator;
import minefantasy.mfr.util.CustomToolHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.List;

public class ItemCustomArmour extends ItemArmourMFR {
	private final String specialDesign;
	private float ratingModifier = 1.0F;

	public ItemCustomArmour(String craftDesign, String name, ArmourDesign armourDesign, EntityEquipmentSlot slot,
			String tex, Rarity rarity) {
		super(craftDesign + "_" + name, MineFantasyMaterials.IRON, armourDesign, slot, craftDesign + "_" + tex, rarity);
		this.specialDesign = craftDesign;
		canRepair = false;
	}

	public ItemCustomArmour modifyRating(float rating) {
		this.ratingModifier = rating;
		return this;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
		super.damageArmor(entity, stack, source, damage, slot);
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		String unlocalName = this.getUnlocalizedNameInefficiently(item) + ".name";
		return CustomToolHelper.getLocalisedName(item, unlocalName);
	}

	@Override
	public String getArmourTextureName(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		String tex = MineFantasyReforged.MOD_ID + ":textures/models/armour/custom/" + specialDesign + "/" + texture;
		if (type == null)// bottom layer
		{
			return tex;// COLOUR LAYER
		}
		return tex + "_detail";// STATIC LAYER
	}

	@Override
	public boolean hasColor(ItemStack item) {
		return true;
	}

	@Override
	public boolean canColour() {
		return true;
	}

	/**
	 * Return the colour of the material it is made of.
	 */
	@Override
	public int getBaseColour(ItemStack item) {
		CustomMaterial material = getCustomMaterial(item);
		if (material == CustomMaterialRegistry.NONE) {
			return (255 << 16) + (255 << 8) + 255;
		}
		return material.getColourInt();
	}

	@Override
	public CustomMaterial getCustomMaterial(ItemStack item) {
		return CustomMaterialRegistry.getMaterialFor(item, CustomToolHelper.slot_main);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab)) {
			return;
		}
		ArrayList<CustomMaterial> metal = CustomMaterialRegistry.getList(CustomMaterialType.METAL_MATERIAL);
		if (items.stream().noneMatch(stack -> stack.getItem() instanceof ItemCustomArmour)) {
			for (CustomMaterial customMat : metal) {
				if (MineFantasyReforged.isDebug() || customMat.getMaterialIngredient() != Ingredient.EMPTY) {
					if (tab == MineFantasyTabs.tabArmour) {
						addSuits(items, customMat.getName());
					}
					if (tab == MineFantasyTabs.tabDragonforged) {
						addDragonforgedSuits(items, customMat.getName());
					}
					if (tab == MineFantasyTabs.tabOrnate) {
						addOrnateSuits(items, customMat.getName());
					}
					if (tab == CreativeTabs.SEARCH) {
						addSuits(items, customMat.getName());
						addDragonforgedSuits(items, customMat.getName());
						addOrnateSuits(items, customMat.getName());
					}
				}
			}
		}
	}

	public static void addSuits(List<ItemStack> list, String material) {
		list.add(MineFantasyItems.STANDARD_CHAIN_HELMET.construct(material));
		list.add(MineFantasyItems.STANDARD_CHAIN_CHESTPLATE.construct(material));
		list.add(MineFantasyItems.STANDARD_CHAIN_LEGGINGS.construct(material));
		list.add(MineFantasyItems.STANDARD_CHAIN_BOOTS.construct(material));

		list.add(MineFantasyItems.STANDARD_SCALE_HELMET.construct(material));
		list.add(MineFantasyItems.STANDARD_SCALE_CHESTPLATE.construct(material));
		list.add(MineFantasyItems.STANDARD_SCALE_LEGGINGS.construct(material));
		list.add((MineFantasyItems.STANDARD_SCALE_BOOTS).construct(material));

		list.add((MineFantasyItems.STANDARD_SPLINT_HELMET).construct(material));
		list.add((MineFantasyItems.STANDARD_SPLINT_CHESTPLATE).construct(material));
		list.add((MineFantasyItems.STANDARD_SPLINT_LEGGINGS).construct(material));
		list.add((MineFantasyItems.STANDARD_SPLINT_BOOTS).construct(material));

		list.add((MineFantasyItems.STANDARD_PLATE_HELMET).construct(material));
		list.add((MineFantasyItems.STANDARD_PLATE_CHESTPLATE).construct(material));
		list.add((MineFantasyItems.STANDARD_PLATE_LEGGINGS).construct(material));
		list.add((MineFantasyItems.STANDARD_PLATE_BOOTS).construct(material));
	}

	public static void addDragonforgedSuits(List<ItemStack> list, String material) {
		list.add(MineFantasyItems.DRAGONFORGED_CHAIN_HELMET.construct(material));
		list.add(MineFantasyItems.DRAGONFORGED_CHAIN_CHESTPLATE.construct(material));
		list.add(MineFantasyItems.DRAGONFORGED_CHAIN_LEGGINGS.construct(material));
		list.add(MineFantasyItems.DRAGONFORGED_CHAIN_BOOTS.construct(material));

		list.add(MineFantasyItems.DRAGONFORGED_SCALE_HELMET.construct(material));
		list.add(MineFantasyItems.DRAGONFORGED_SCALE_CHESTPLATE.construct(material));
		list.add(MineFantasyItems.DRAGONFORGED_SCALE_LEGGINGS.construct(material));
		list.add((MineFantasyItems.DRAGONFORGED_SCALE_BOOTS).construct(material));

		list.add((MineFantasyItems.DRAGONFORGED_SPLINT_HELMET).construct(material));
		list.add((MineFantasyItems.DRAGONFORGED_SPLINT_CHESTPLATE).construct(material));
		list.add((MineFantasyItems.DRAGONFORGED_SPLINT_LEGGINGS).construct(material));
		list.add((MineFantasyItems.DRAGONFORGED_SPLINT_BOOTS).construct(material));

		list.add((MineFantasyItems.DRAGONFORGED_PLATE_HELMET).construct(material));
		list.add((MineFantasyItems.DRAGONFORGED_PLATE_CHESTPLATE).construct(material));
		list.add((MineFantasyItems.DRAGONFORGED_PLATE_LEGGINGS).construct(material));
		list.add((MineFantasyItems.DRAGONFORGED_PLATE_BOOTS).construct(material));
	}

	public static void addOrnateSuits(List<ItemStack> list, String material) {
		list.add(MineFantasyItems.ORNATE_CHAIN_HELMET.construct(material));
		list.add(MineFantasyItems.ORNATE_CHAIN_CHESTPLATE.construct(material));
		list.add(MineFantasyItems.ORNATE_CHAIN_LEGGINGS.construct(material));
		list.add(MineFantasyItems.ORNATE_CHAIN_BOOTS.construct(material));

		list.add(MineFantasyItems.ORNATE_SCALE_HELMET.construct(material));
		list.add(MineFantasyItems.ORNATE_SCALE_CHESTPLATE.construct(material));
		list.add(MineFantasyItems.ORNATE_SCALE_LEGGINGS.construct(material));
		list.add((MineFantasyItems.ORNATE_SCALE_BOOTS).construct(material));

		list.add((MineFantasyItems.ORNATE_SPLINT_HELMET).construct(material));
		list.add((MineFantasyItems.ORNATE_SPLINT_CHESTPLATE).construct(material));
		list.add((MineFantasyItems.ORNATE_SPLINT_LEGGINGS).construct(material));
		list.add((MineFantasyItems.ORNATE_SPLINT_BOOTS).construct(material));

		list.add((MineFantasyItems.ORNATE_PLATE_HELMET).construct(material));
		list.add((MineFantasyItems.ORNATE_PLATE_CHESTPLATE).construct(material));
		list.add((MineFantasyItems.ORNATE_PLATE_LEGGINGS).construct(material));
		list.add((MineFantasyItems.ORNATE_PLATE_BOOTS).construct(material));
	}


	@Override
	public float getPieceWeight(ItemStack item, EntityEquipmentSlot slot) {
		float baseWeight = armourWeight * ArmourCalculator.sizes[slot.getIndex()];
		CustomMaterial material = this.getCustomMaterial(item);
		if (material != CustomMaterialRegistry.NONE) {
			baseWeight *= material.getDensity();
		}
		return baseWeight;
	}

	public int getMaxDamage(ItemStack stack) {
		CustomMaterial material = this.getCustomMaterial(stack);
		if (material != CustomMaterialRegistry.NONE) {
			return (int) ((material.getDurability() * 250) * (design.getDurability() / 2F));
		}
		return getMaxDamage();
	}

	public boolean isCustom() {
		return true;
	}

	@Override
	protected float getSpecialModifier(ItemStack armour, DamageSource source) {
		float modifier = CombatMechanics.getSpecialModifier(this.getCustomMaterial(armour), this.specialDesign, source.getTrueSource(), false);

		return super.getSpecialModifier(armour, source) * modifier;
	}

	@Override
	protected float getProtectionRatio(ItemStack item) {
		return super.getProtectionRatio(item) * ratingModifier;
	}
}
