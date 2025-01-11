package minefantasy.mfr.init;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.armour.ArmourDesign;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.item.ItemApron;
import minefantasy.mfr.item.ItemArmourMFR;
import minefantasy.mfr.material.ArmorMaterialMFR;
import minefantasy.mfr.material.BaseMaterial;
import minefantasy.mfr.util.Utils;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author Anonymous Productions
 */
// TODO
@Mod.EventBusSubscriber(modid = MineFantasyReforged.MOD_ID)
public class LeatherArmourListMFR {
	public static final String[] LEATHER_ARMOR_TYPES = new String[] {"hide", "rough_leather", "strong_leather", "stud_leather", "padded"};

	public static ArmorMaterialMFR LEATHER_MAT;
	public static ArmorMaterialMFR APRON;
	public static ItemArmourMFR[] LEATHER;
	public static Item LEATHER_APRON;

	public static Item HIDE_HELMET = Utils.nullValue();
	public static Item HIDE_CHEST = Utils.nullValue();
	public static Item HIDE_LEGS = Utils.nullValue();
	public static Item HIDE_BOOTS = Utils.nullValue();

	public static Item ROUGH_LEATHER_HELMET = Utils.nullValue();
	public static Item ROUGH_LEATHER_CHEST = Utils.nullValue();
	public static Item ROUGH_LEATHER_LEGS = Utils.nullValue();
	public static Item ROUGH_LEATHER_BOOTS = Utils.nullValue();

	public static Item STRONG_LEATHER_HELMET = Utils.nullValue();
	public static Item STRONG_LEATHER_CHEST = Utils.nullValue();
	public static Item STRONG_LEATHER_LEGS = Utils.nullValue();
	public static Item STRONG_LEATHER_BOOTS = Utils.nullValue();

	public static Item STUD_LEATHER_HELMET = Utils.nullValue();
	public static Item STUD_LEATHER_CHEST = Utils.nullValue();
	public static Item STUD_LEATHER_LEGS = Utils.nullValue();
	public static Item STUD_LEATHER_BOOTS = Utils.nullValue();

	public static Item PADDED_HELMET = Utils.nullValue();
	public static Item PADDED_CHEST = Utils.nullValue();
	public static Item PADDED_LEGS = Utils.nullValue();
	public static Item PADDED_BOOTS = Utils.nullValue();

	public static void init() {
		LEATHER_MAT = new ArmorMaterialMFR("leather", 5, 0.30F, 18, 1.00F);
		APRON = new ArmorMaterialMFR("apron", 6, 0.30F, 0, 1.00F);
		LEATHER = new ItemArmourMFR[LEATHER_ARMOR_TYPES.length * 4];
		LEATHER_APRON = new ItemApron("leather_apron", MineFantasyMaterials.LEATHER_APRON, "leatherapron_layer_1", Rarity.COMMON);

		for (int a = 0; a < LEATHER_ARMOR_TYPES.length; a++) {
			BaseMaterial baseMat = BaseMaterial.getMaterial(LEATHER_ARMOR_TYPES[a]);
			String matName = baseMat.name;
			Rarity rarity = baseMat.rarity;
			int id = a * 4;
			float bulk = baseMat.weight;
			ArmourDesign design = baseMat == MineFantasyMaterials.PADDING ? ArmourDesign.PADDING : ArmourDesign.LEATHER;

			LEATHER[id] = new ItemArmourMFR(matName.toLowerCase() + "_helmet", baseMat, design, EntityEquipmentSlot.HEAD, matName.toLowerCase() + "_layer_1", rarity, bulk);
			LEATHER[id + 1] = new ItemArmourMFR(matName.toLowerCase() + "_chestplate", baseMat, design, EntityEquipmentSlot.CHEST, matName.toLowerCase() + "_layer_1", rarity, bulk);
			LEATHER[id + 2] = new ItemArmourMFR(matName.toLowerCase() + "_leggings", baseMat, design, EntityEquipmentSlot.LEGS, matName.toLowerCase() + "_layer_2", rarity, bulk);
			LEATHER[id + 3] = new ItemArmourMFR(matName.toLowerCase() + "_boots", baseMat, design, EntityEquipmentSlot.FEET, matName.toLowerCase() + "_layer_1", rarity, bulk);
		}
	}

	@SubscribeEvent
	public static void registerLeatherArmourListMFR(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		registry.registerAll(LEATHER);
		registry.register(LEATHER_APRON);
	}

	public static ItemStack armour(ItemArmourMFR[] pool, int tier, int piece) {
		int slot = tier * 4 + piece;
		if (slot >= pool.length) {
			slot = pool.length - 1;
		}
		return new ItemStack(pool[slot]);
	}

	public static Item armourItem(ItemArmourMFR[] pool, int tier, int piece) {
		int slot = tier * 4 + piece;
		if (slot >= pool.length) {
			slot = pool.length - 1;
		}
		return pool[slot];
	}

	public static boolean isUnbreakable(BaseMaterial material) {
		return material == MineFantasyMaterials.ENDERFORGE || material == MineFantasyMaterials.IGNOTUMITE
				|| material == MineFantasyMaterials.MITHIUM;
	}
}
