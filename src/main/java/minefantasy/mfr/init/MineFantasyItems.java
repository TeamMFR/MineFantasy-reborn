package minefantasy.mfr.init;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.MineFantasyReforgedAPI;
import minefantasy.mfr.api.armour.ArmourDesign;
import minefantasy.mfr.api.mining.RandomDigs;
import minefantasy.mfr.api.mining.RandomOre;
import minefantasy.mfr.config.ConfigHardcore;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.item.AdvancedFuelHandler;
import minefantasy.mfr.item.ArrowType;
import minefantasy.mfr.item.EnumBowType;
import minefantasy.mfr.item.FuelHandlerMF;
import minefantasy.mfr.item.ItemArmourMFR;
import minefantasy.mfr.item.ItemArrowMFR;
import minefantasy.mfr.item.ItemAxeMFR;
import minefantasy.mfr.item.ItemBandage;
import minefantasy.mfr.item.ItemBaseMFR;
import minefantasy.mfr.item.ItemBasicCraftTool;
import minefantasy.mfr.item.ItemBattleaxe;
import minefantasy.mfr.item.ItemBomb;
import minefantasy.mfr.item.ItemBombComponent;
import minefantasy.mfr.item.ItemBowMFR;
import minefantasy.mfr.item.ItemBowl;
import minefantasy.mfr.item.ItemBurntFood;
import minefantasy.mfr.item.ItemClimbingPick;
import minefantasy.mfr.item.ItemComponentMFR;
import minefantasy.mfr.item.ItemCrossbow;
import minefantasy.mfr.item.ItemCrossbowPart;
import minefantasy.mfr.item.ItemCrudeBomb;
import minefantasy.mfr.item.ItemCustomArmour;
import minefantasy.mfr.item.ItemDagger;
import minefantasy.mfr.item.ItemEAnvilTools;
import minefantasy.mfr.item.ItemExplodingArrow;
import minefantasy.mfr.item.ItemExplodingBolt;
import minefantasy.mfr.item.ItemFilledMould;
import minefantasy.mfr.item.ItemFoodMFR;
import minefantasy.mfr.item.ItemGreatsword;
import minefantasy.mfr.item.ItemHalbeard;
import minefantasy.mfr.item.ItemHammer;
import minefantasy.mfr.item.ItemHandpick;
import minefantasy.mfr.item.ItemHeated;
import minefantasy.mfr.item.ItemHeavyPick;
import minefantasy.mfr.item.ItemHeavyShovel;
import minefantasy.mfr.item.ItemHide;
import minefantasy.mfr.item.ItemHoeMFR;
import minefantasy.mfr.item.ItemJewel;
import minefantasy.mfr.item.ItemJug;
import minefantasy.mfr.item.ItemKatana;
import minefantasy.mfr.item.ItemKnife;
import minefantasy.mfr.item.ItemLance;
import minefantasy.mfr.item.ItemLighter;
import minefantasy.mfr.item.ItemLootSack;
import minefantasy.mfr.item.ItemLumberAxe;
import minefantasy.mfr.item.ItemMace;
import minefantasy.mfr.item.ItemMattock;
import minefantasy.mfr.item.ItemMetalComponent;
import minefantasy.mfr.item.ItemMine;
import minefantasy.mfr.item.ItemMobSpawner;
import minefantasy.mfr.item.ItemMultiFood;
import minefantasy.mfr.item.ItemNeedle;
import minefantasy.mfr.item.ItemPaintBrush;
import minefantasy.mfr.item.ItemParachute;
import minefantasy.mfr.item.ItemPersistentComponentMarker;
import minefantasy.mfr.item.ItemPickMFR;
import minefantasy.mfr.item.ItemResearchBook;
import minefantasy.mfr.item.ItemSaw;
import minefantasy.mfr.item.ItemScythe;
import minefantasy.mfr.item.ItemShearsMFR;
import minefantasy.mfr.item.ItemSkillBook;
import minefantasy.mfr.item.ItemSpade;
import minefantasy.mfr.item.ItemSpanner;
import minefantasy.mfr.item.ItemSpear;
import minefantasy.mfr.item.ItemSpecialDesign;
import minefantasy.mfr.item.ItemSpyglass;
import minefantasy.mfr.item.ItemSword;
import minefantasy.mfr.item.ItemSyringe;
import minefantasy.mfr.item.ItemTongs;
import minefantasy.mfr.item.ItemTrow;
import minefantasy.mfr.item.ItemUnfinishedFood;
import minefantasy.mfr.item.ItemWaraxe;
import minefantasy.mfr.item.ItemWarhammer;
import minefantasy.mfr.item.ItemWashCloth;
import minefantasy.mfr.item.ItemWeaponMFR;
import minefantasy.mfr.item.ItemWoodComponent;
import minefantasy.mfr.item.ItemWorldGenPlacer;
import minefantasy.mfr.material.BaseMaterial;
import minefantasy.mfr.util.Utils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(MineFantasyReforged.MOD_ID)
@Mod.EventBusSubscriber(modid = MineFantasyReforged.MOD_ID)
public class MineFantasyItems {

	public static Item.ToolMaterial ORNATE = EnumHelper.addToolMaterial("ornate", 2, 250, 6.0F, 2.0F, 100);
	public static Item.ToolMaterial DRAGONFORGED = EnumHelper.addToolMaterial("dragonforged", 2, 250, 6.0F, 2.0F, 15);

	public static EnumRarity POOR;
	public static EnumRarity UNIQUE;
	public static EnumRarity RARE;
	public static EnumRarity[] RARITY;

	// COMPONENTS
	public static Item PIE_TRAY_UNCOOKED = Utils.nullValue();
	public static Item COPPER_INGOT = Utils.nullValue();
	public static Item TIN_INGOT = Utils.nullValue();
	public static Item BRONZE_INGOT = Utils.nullValue();
	public static Item PIG_IRON_INGOT = Utils.nullValue();
	public static Item STEEL_INGOT = Utils.nullValue();
	public static Item ENCRUSTED_INGOT = Utils.nullValue();
	public static Item BLACK_STEEL_WEAK_INGOT = Utils.nullValue();
	public static Item BLACK_STEEL_INGOT = Utils.nullValue();
	public static Item SILVER_INGOT = Utils.nullValue();
	public static Item RED_STEEL_WEAK_INGOT = Utils.nullValue();
	public static Item RED_STEEL_INGOT = Utils.nullValue();
	public static Item BLUE_STEEL_WEAK_INGOT = Utils.nullValue();
	public static Item BLUE_STEEL_INGOT = Utils.nullValue();
	public static Item ADAMANTIUM_INGOT = Utils.nullValue();
	public static Item MITHRIL_INGOT = Utils.nullValue();
	public static Item IGNOTUMITE_INGOT = Utils.nullValue();
	public static Item MITHIUM_INGOT = Utils.nullValue();
	public static Item ENDER_INGOT = Utils.nullValue();
	public static Item TUNGSTEN_INGOT = Utils.nullValue();
	public static Item OBSIDIAN_INGOT = Utils.nullValue();
	public static Item COMPOSITE_ALLOY_INGOT = Utils.nullValue();
	public static Item BAR = Utils.nullValue();
	public static ItemComponentMFR TIMBER = Utils.nullValue();
	public static Item TIMBER_CUT = Utils.nullValue();
	public static Item TIMBER_PANE = Utils.nullValue();
	public static Item PERSISTENT_COMPONENT_FLAG = Utils.nullValue();
	public static Item CLAY_POT = Utils.nullValue();
	public static Item CLAY_POT_UNCOOKED = Utils.nullValue();
	public static Item INGOT_MOULD = Utils.nullValue();
	public static Item INGOT_MOULD_UNCOOKED = Utils.nullValue();
	public static Item VINE = Utils.nullValue();
	public static Item SHARP_ROCK = Utils.nullValue();
	public static Item FLUX = Utils.nullValue();
	public static Item FLUX_STRONG = Utils.nullValue();
	public static Item COAL_DUST = Utils.nullValue();
	public static Item NITRE = Utils.nullValue();
	public static Item SULFUR = Utils.nullValue();
	public static Item PREPARED_IRON = Utils.nullValue();
	public static Item BLACKPOWDER = Utils.nullValue();
	public static Item BLACKPOWDER_ADVANCED = Utils.nullValue();
	public static Item FLETCHING = Utils.nullValue();
	public static Item SHRAPNEL = Utils.nullValue();
	public static Item MAGMA_CREAM_REFINED = Utils.nullValue();
	public static Item BOMB_FUSE = Utils.nullValue();
	public static Item BOMB_FUSE_LONG = Utils.nullValue();
	public static Item BOMB_CASING_UNCOOKED = Utils.nullValue();
	public static Item BOMB_CASING_CERAMIC = Utils.nullValue();
	public static Item MINE_CASING_UNCOOKED = Utils.nullValue();
	public static Item MINE_CASING_CERAMIC = Utils.nullValue();
	public static Item BOMB_CASING_IRON = Utils.nullValue();
	public static Item MINE_CASING_IRON = Utils.nullValue();
	public static Item BOMB_CASING_OBSIDIAN = Utils.nullValue();
	public static Item MINE_CASING_OBSIDIAN = Utils.nullValue();
	public static Item BOMB_CASING_CRYSTAL = Utils.nullValue();
	public static Item MINE_CASING_CRYSTAL = Utils.nullValue();
	public static Item BOMB_CASING_ARROW = Utils.nullValue();
	public static Item BOMB_CASING_BOLT = Utils.nullValue();
	public static Item COKE = Utils.nullValue();
	public static Item DIAMOND_SHARDS = Utils.nullValue();
	public static Item CLAY_BRICK = Utils.nullValue();
	public static Item KAOLINITE = Utils.nullValue();
	public static Item KAOLINITE_DUST = Utils.nullValue();
	public static Item FIRECLAY = Utils.nullValue();
	public static Item FIRECLAY_BRICK = Utils.nullValue();
	public static Item STRONG_BRICK = Utils.nullValue();
	public static Item HIDE_SMALL = Utils.nullValue();
	public static Item HIDE_MEDIUM = Utils.nullValue();
	public static Item HIDE_LARGE = Utils.nullValue();
	public static Item RAWHIDE_SMALL = Utils.nullValue();
	public static Item RAWHIDE_MEDIUM = Utils.nullValue();
	public static Item RAWHIDE_LARGE = Utils.nullValue();
	public static Item DRAGON_HEART = Utils.nullValue();
	public static Item LEATHER_STRIP = Utils.nullValue();
	public static Item NAIL = Utils.nullValue();
	public static Item RIVET = Utils.nullValue();
	public static Item THREAD = Utils.nullValue();
	public static Item OBSIDIAN_ROCK = Utils.nullValue();
	public static Item ORE_COPPER = Utils.nullValue();
	public static Item ORE_TIN = Utils.nullValue();
	public static Item ORE_IRON = Utils.nullValue();
	public static Item ORE_SILVER = Utils.nullValue();
	public static Item ORE_GOLD = Utils.nullValue();
	public static Item ORE_TUNGSTEN = Utils.nullValue();
	public static Item HOT_ITEM = Utils.nullValue();
	public static Item JUG_PLANT_OIL = Utils.nullValue();
	public static Item TALISMAN_LESSER = Utils.nullValue();
	public static Item TALISMAN_GREATER = Utils.nullValue();
	public static Item BOLT = Utils.nullValue();
	public static Item IRON_FRAME = Utils.nullValue();
	public static Item IRON_STRUT = Utils.nullValue();
	public static Item BRONZE_GEARS = Utils.nullValue();
	public static Item TUNGSTEN_GEARS = Utils.nullValue();
	public static Item STEEL_TUBE = Utils.nullValue();
	public static Item COGWORK_SHAFT = Utils.nullValue();
	public static Item PREPARED_COAL = Utils.nullValue();
	public static Item INGOT_MOULD_FILLED = Utils.nullValue();
	public static Item CROSSBOW_STOCK_WOOD = Utils.nullValue();
	public static Item CROSSBOW_STOCK_IRON = Utils.nullValue();
	public static Item CROSSBOW_HANDLE_WOOD = Utils.nullValue();
	public static Item CROSSBOW_ARMS_BASIC = Utils.nullValue();
	public static Item CROSSBOW_ARMS_LIGHT = Utils.nullValue();
	public static Item CROSSBOW_ARMS_HEAVY = Utils.nullValue();
	public static Item CROSSBOW_ARMS_ADVANCED = Utils.nullValue();
	public static Item CROSSBOW_BAYONET = Utils.nullValue();
	public static Item CROSSBOW_AMMO = Utils.nullValue();
	public static Item CROSSBOW_SCOPE = Utils.nullValue();
	public static Item CROSSBOW_STRING_UNLOADED = Utils.nullValue();
	public static Item CROSSBOW_STRING_LOADED = Utils.nullValue();
	public static Item CHAIN_MESH = Utils.nullValue();
	public static Item SCALE_MESH = Utils.nullValue();
	public static Item SPLINT_MESH = Utils.nullValue();
	public static Item PLATE = Utils.nullValue();
	public static Item PLATE_HUGE = Utils.nullValue();
	public static Item METAL_HUNK = Utils.nullValue();
	public static Item ARROWHEAD = Utils.nullValue();
	public static Item BODKIN_HEAD = Utils.nullValue();
	public static Item BROAD_HEAD = Utils.nullValue();
	public static Item COGWORK_ARMOUR = Utils.nullValue();
	public static Item FLUX_POT = Utils.nullValue();
	public static Item COAL_FLUX = Utils.nullValue();
	public static Item COPPER_COIN = Utils.nullValue();
	public static Item SILVER_COIN = Utils.nullValue();
	public static Item GOLD_COIN = Utils.nullValue();
	public static Item HINGE = Utils.nullValue();
	public static Item COGWORK_PULLEY = Utils.nullValue();
	public static ItemJewel ANCIENT_JEWEL_MITHRIL = Utils.nullValue();
	public static ItemJewel ANCIENT_JEWEL_ADAMANT = Utils.nullValue();
	public static ItemJewel ANCIENT_JEWEL_MASTER = Utils.nullValue();
	public static ItemJewel TRILOGY_JEWEL = Utils.nullValue();
	public static Item ORNATE_ITEMS = Utils.nullValue();

	// STANDARD ARMOR
	public static ItemArmourMFR STANDARD_SCALE_HELMET = Utils.nullValue();
	public static ItemArmourMFR STANDARD_SCALE_CHESTPLATE = Utils.nullValue();
	public static ItemArmourMFR STANDARD_SCALE_LEGGINGS = Utils.nullValue();
	public static ItemArmourMFR STANDARD_SCALE_BOOTS = Utils.nullValue();
	public static ItemArmourMFR STANDARD_CHAIN_HELMET = Utils.nullValue();
	public static ItemArmourMFR STANDARD_CHAIN_CHESTPLATE = Utils.nullValue();
	public static ItemArmourMFR STANDARD_CHAIN_LEGGINGS = Utils.nullValue();
	public static ItemArmourMFR STANDARD_CHAIN_BOOTS = Utils.nullValue();
	public static ItemArmourMFR STANDARD_SPLINT_HELMET = Utils.nullValue();
	public static ItemArmourMFR STANDARD_SPLINT_CHESTPLATE = Utils.nullValue();
	public static ItemArmourMFR STANDARD_SPLINT_LEGGINGS = Utils.nullValue();
	public static ItemArmourMFR STANDARD_SPLINT_BOOTS = Utils.nullValue();
	public static ItemArmourMFR STANDARD_PLATE_HELMET = Utils.nullValue();
	public static ItemArmourMFR STANDARD_PLATE_CHESTPLATE = Utils.nullValue();
	public static ItemArmourMFR STANDARD_PLATE_LEGGINGS = Utils.nullValue();
	public static ItemArmourMFR STANDARD_PLATE_BOOTS = Utils.nullValue();

	// DRAGONFORGED TOOLS
	public static ItemWeaponMFR DRAGONFORGED_SWORD = Utils.nullValue();
	public static ItemWeaponMFR DRAGONFORGED_WARAXE = Utils.nullValue();
	public static ItemWeaponMFR DRAGONFORGED_MACE = Utils.nullValue();
	public static ItemWeaponMFR DRAGONFORGED_DAGGER = Utils.nullValue();
	public static ItemWeaponMFR DRAGONFORGED_SPEAR = Utils.nullValue();
	public static ItemWeaponMFR DRAGONFORGED_GREATSWORD = Utils.nullValue();
	public static ItemWeaponMFR DRAGONFORGED_BATTLEAXE = Utils.nullValue();
	public static ItemWeaponMFR DRAGONFORGED_WARHAMMER = Utils.nullValue();
	public static ItemWeaponMFR DRAGONFORGED_KATANA = Utils.nullValue();
	public static ItemWeaponMFR DRAGONFORGED_HALBEARD = Utils.nullValue();
	public static ItemWeaponMFR DRAGONFORGED_LANCE = Utils.nullValue();
	public static ItemPickMFR DRAGONFORGED_PICK = Utils.nullValue();
	public static ItemAxeMFR DRAGONFORGED_AXE = Utils.nullValue();
	public static ItemSpade DRAGONFORGED_SPADE = Utils.nullValue();
	public static ItemHoeMFR DRAGONFORGED_HOE = Utils.nullValue();
	public static ItemHeavyPick DRAGONFORGED_HEAVY_PICK = Utils.nullValue();
	public static ItemHeavyShovel DRAGONFORGED_HEAVY_SHOVEL = Utils.nullValue();
	public static ItemHandpick DRAGONFORGED_HANDPICK = Utils.nullValue();
	public static ItemTrow DRAGONFORGED_TROW = Utils.nullValue();
	public static ItemScythe DRAGONFORGED_SCYTHE = Utils.nullValue();
	public static ItemMattock DRAGONFORGED_MATTOCK = Utils.nullValue();
	public static ItemLumberAxe DRAGONFORGED_LUMBER = Utils.nullValue();
	public static ItemHammer DRAGONFORGED_HAMMER = Utils.nullValue();
	public static ItemHammer DRAGONFORGED_HEAVY_HAMMER = Utils.nullValue();
	public static ItemTongs DRAGONFORGED_TONGS = Utils.nullValue();
	public static ItemShearsMFR DRAGONFORGED_SHEARS = Utils.nullValue();
	public static ItemKnife DRAGONFORGED_KNIFE = Utils.nullValue();
	public static ItemNeedle DRAGONFORGED_NEEDLE = Utils.nullValue();
	public static ItemSaw DRAGONFORGED_SAW = Utils.nullValue();
	public static ItemSpanner DRAGONFORGED_SPANNER = Utils.nullValue();
	public static ItemBowMFR DRAGONFORGED_BOW = Utils.nullValue();

	// DRAGONFORGED ARMOR
	public static ItemCustomArmour DRAGONFORGED_SCALE_HELMET = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_SCALE_CHESTPLATE = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_SCALE_LEGGINGS = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_SCALE_BOOTS = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_CHAIN_HELMET = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_CHAIN_CHESTPLATE = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_CHAIN_LEGGINGS = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_CHAIN_BOOTS = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_SPLINT_HELMET = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_SPLINT_CHESTPLATE = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_SPLINT_LEGGINGS = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_SPLINT_BOOTS = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_PLATE_HELMET = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_PLATE_CHESTPLATE = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_PLATE_LEGGINGS = Utils.nullValue();
	public static ItemCustomArmour DRAGONFORGED_PLATE_BOOTS = Utils.nullValue();

	// STANDARD CUSTOM TOOLS
	public static ItemWeaponMFR STANDARD_SWORD = Utils.nullValue();
	public static ItemWeaponMFR STANDARD_WARAXE = Utils.nullValue();
	public static ItemWeaponMFR STANDARD_MACE = Utils.nullValue();
	public static ItemWeaponMFR STANDARD_DAGGER = Utils.nullValue();
	public static ItemWeaponMFR STANDARD_SPEAR = Utils.nullValue();
	public static ItemWeaponMFR STANDARD_GREATSWORD = Utils.nullValue();
	public static ItemWeaponMFR STANDARD_BATTLEAXE = Utils.nullValue();
	public static ItemWeaponMFR STANDARD_WARHAMMER = Utils.nullValue();
	public static ItemWeaponMFR STANDARD_KATANA = Utils.nullValue();
	public static ItemWeaponMFR STANDARD_HALBEARD = Utils.nullValue();
	public static ItemWeaponMFR STANDARD_LANCE = Utils.nullValue();
	public static ItemPickMFR STANDARD_PICK = Utils.nullValue();
	public static ItemAxeMFR STANDARD_AXE = Utils.nullValue();
	public static ItemSpade STANDARD_SPADE = Utils.nullValue();
	public static ItemHoeMFR STANDARD_HOE = Utils.nullValue();
	public static ItemHeavyPick STANDARD_HEAVY_PICK = Utils.nullValue();
	public static ItemHeavyShovel STANDARD_HEAVY_SHOVEL = Utils.nullValue();
	public static ItemHandpick STANDARD_HANDPICK = Utils.nullValue();
	public static ItemTrow STANDARD_TROW = Utils.nullValue();
	public static ItemScythe STANDARD_SCYTHE = Utils.nullValue();
	public static ItemMattock STANDARD_MATTOCK = Utils.nullValue();
	public static ItemLumberAxe STANDARD_LUMBER = Utils.nullValue();
	public static ItemHammer STANDARD_HAMMER = Utils.nullValue();
	public static ItemHammer STANDARD_HEAVY_HAMMER = Utils.nullValue();
	public static ItemTongs STANDARD_TONGS = Utils.nullValue();
	public static ItemShearsMFR STANDARD_SHEARS = Utils.nullValue();
	public static ItemKnife STANDARD_KNIFE = Utils.nullValue();
	public static ItemNeedle STANDARD_NEEDLE = Utils.nullValue();
	public static ItemSaw STANDARD_SAW = Utils.nullValue();
	public static ItemBasicCraftTool STANDARD_SPOON = Utils.nullValue();
	public static ItemBasicCraftTool STANDARD_MALLET = Utils.nullValue();
	public static ItemSpanner STANDARD_SPANNER = Utils.nullValue();
	public static ItemWashCloth WASH_CLOTH_WOOL = Utils.nullValue();
	public static ItemBowMFR STANDARD_BOW = Utils.nullValue();
	public static ItemArrowMFR STANDARD_ARROW = Utils.nullValue();
	public static ItemArrowMFR STANDARD_BOLT = Utils.nullValue();
	public static ItemArrowMFR STANDARD_ARROW_BODKIN = Utils.nullValue();
	public static ItemArrowMFR STANDARD_ARROW_BROAD = Utils.nullValue();

	// MORSELS
	public static Item WOLF_RAW = Utils.nullValue();
	public static Item WOLF_COOKED = Utils.nullValue();
	public static Item HORSE_RAW = Utils.nullValue();
	public static Item HORSE_COOKED = Utils.nullValue();
	public static Item GENERIC_MEAT_UNCOOKED = Utils.nullValue();
	public static Item GENERIC_MEAT_COOKED = Utils.nullValue();
	public static Item GENERIC_MEAT_STRIP_UNCOOKED = Utils.nullValue();
	public static Item GENERIC_MEAT_STRIP_COOKED = Utils.nullValue();
	public static Item GENERIC_MEAT_CHUNK_UNCOOKED = Utils.nullValue();
	public static Item GENERIC_MEAT_CHUNK_COOKED = Utils.nullValue();
	public static Item GENERIC_MEAT_MINCE_UNCOOKED = Utils.nullValue();
	public static Item GENERIC_MEAT_MINCE_COOKED = Utils.nullValue();
	public static Item FLOUR = Utils.nullValue();
	public static Item BREADCRUMBS = Utils.nullValue();
	public static Item GUTS = Utils.nullValue();
	public static Item BREADROLL = Utils.nullValue();
	public static Item BREAD_SLICE = Utils.nullValue();
	public static Item CURDS = Utils.nullValue();
	public static Item CHEESE_POT = Utils.nullValue();
	public static Item CHEESE_SLICE = Utils.nullValue();
	// T1 (basic mixing)
	// Util: Roast, Prep Block (Stone-Bronze Age)
	public static Item STEW = Utils.nullValue();
	public static Item OATS = Utils.nullValue();
	// T2 (Basic baking, stone oven, processed mixing)
	// Util: Stone Oven, Prep Block (Bronze Age - Early Iron Age)
	public static Item CHEESE_ROLL = Utils.nullValue();
	public static Item JERKY = Utils.nullValue();
	public static Item SAUSAGE_RAW = Utils.nullValue();
	public static Item SAUSAGE_COOKED = Utils.nullValue();
	public static Item SWEETROLL_UNICED = Utils.nullValue();
	public static Item SWEETROLL = Utils.nullValue();
	public static Item SANDWITCH_MEAT = Utils.nullValue();
	public static Item SANDWITCH_BIG = Utils.nullValue();
	// T3 (Quality baking, metal oven)
	// Util: Metal Oven, Prep Block, Steel Tools (Mid Iron Age)
	public static Item MEATPIE_SLICE = Utils.nullValue();
	public static Item PIESLICE_APPLE = Utils.nullValue();
	public static Item PIESLICE_BERRY = Utils.nullValue();
	// T4 (Advanced baking, multiple processes, temperature regulation)
	// Util : Metal Oven, Prep Block, Full tool set, Proper kitchen setup (Mid Iron
	// Age)
	public static Item PIESLICE_SHEPARDS = Utils.nullValue();
	public static Item CAKE_SLICE = Utils.nullValue();
	public static Item CARROTCAKE_SLICE = Utils.nullValue();
	// T5 (Advanced baking, multiple process, temperature regulation, many
	// ingreedients)
	public static Item CHOCCAKE_SLICE = Utils.nullValue();
	// T6 (Perfected meals, extremely difficult to create)
	public static Item BFCAKE_SLICE = Utils.nullValue();
	public static Item ECLAIR_UNICED = Utils.nullValue();
	public static Item ECLAIR_EMPTY = Utils.nullValue();
	public static Item ECLAIR = Utils.nullValue();
	// MISC
	public static Item CAKE_TIN = Utils.nullValue();
	public static Item PIE_TRAY = Utils.nullValue();
	public static Item ICING = Utils.nullValue();
	public static Item CUSTARD = Utils.nullValue();
	public static Item COCA_POWDER = Utils.nullValue();
	public static Item CHOCOLATE = Utils.nullValue();
	public static Item CHOC_CHIPS = Utils.nullValue();
	public static Item BERRIES = Utils.nullValue();
	public static Item BERRIES_JUICY = Utils.nullValue();
	public static Item SWEETROLL_RAW = Utils.nullValue();
	public static Item ECLAIR_RAW = Utils.nullValue();
	public static Item CAKE_RAW = Utils.nullValue();
	public static Item CAKE_SIMPLE_RAW = Utils.nullValue();
	public static Item CAKE_CARROT_RAW = Utils.nullValue();
	public static Item CAKE_CHOC_RAW = Utils.nullValue();
	public static Item CAKE_BF_RAW = Utils.nullValue();
	public static Item CAKE_UNICED = Utils.nullValue();
	public static Item CAKE_SIMPLE_UNICED = Utils.nullValue();
	public static Item CAKE_CARROT_UNICED = Utils.nullValue();
	public static Item CAKE_CHOC_UNICED = Utils.nullValue();
	public static Item CAKE_BF_UNICED = Utils.nullValue();
	public static Item PIE_MEAT_UNCOOKED = Utils.nullValue();
	public static Item PIE_APPLE_UNCOOKED = Utils.nullValue();
	public static Item PIE_BERRY_UNCOOKED = Utils.nullValue();
	public static Item PIE_SHEPARD_UNCOOKED = Utils.nullValue();
	public static Item PIE_PUMPKIN_UNCOOKED = Utils.nullValue();
	public static Item PIE_MEAT_COOKED = Utils.nullValue();
	public static Item PIE_APPLE_COOKED = Utils.nullValue();
	public static Item PIE_BERRY_COOKED = Utils.nullValue();
	public static Item PIE_SHEPARD_COOKED = Utils.nullValue();
	public static Item PIE_PUMPKIN_COOKED = Utils.nullValue();
	public static Item SALT = Utils.nullValue();
	public static Item SUGAR_POT = Utils.nullValue();
	public static Item BOWL_WATER_SALT = Utils.nullValue();
	public static Item DOUGH = Utils.nullValue();
	public static Item PASTRY = Utils.nullValue();
	public static Item RAW_BREAD = Utils.nullValue();
	public static Item JUG_UNCOOKED = Utils.nullValue();
	public static Item JUG_EMPTY = Utils.nullValue();
	public static Item JUG_WATER = Utils.nullValue();
	public static Item JUG_MILK = Utils.nullValue();
	public static Item BURNT_FOOD = Utils.nullValue();
	public static Item BURNT_POT = Utils.nullValue();
	public static Item BURNT_PIE = Utils.nullValue();
	public static Item BURNT_CAKE = Utils.nullValue();

	// ORNATE TOOLS AND WEAPONS
	public static ItemWeaponMFR ORNATE_SWORD = Utils.nullValue();
	public static ItemWeaponMFR ORNATE_WARAXE = Utils.nullValue();
	public static ItemWeaponMFR ORNATE_MACE = Utils.nullValue();
	public static ItemWeaponMFR ORNATE_DAGGER = Utils.nullValue();
	public static ItemWeaponMFR ORNATE_SPEAR = Utils.nullValue();
	public static ItemWeaponMFR ORNATE_GREATSWORD = Utils.nullValue();
	public static ItemWeaponMFR ORNATE_BATTLEAXE = Utils.nullValue();
	public static ItemWeaponMFR ORNATE_WARHAMMER = Utils.nullValue();
	public static ItemWeaponMFR ORNATE_KATANA = Utils.nullValue();
	public static ItemWeaponMFR ORNATE_HALBEARD = Utils.nullValue();
	public static ItemWeaponMFR ORNATE_LANCE = Utils.nullValue();
	public static ItemPickMFR ORNATE_PICK = Utils.nullValue();
	public static ItemAxeMFR ORNATE_AXE = Utils.nullValue();
	public static ItemSpade ORNATE_SPADE = Utils.nullValue();
	public static ItemHoeMFR ORNATE_HOE = Utils.nullValue();
	public static ItemHeavyPick ORNATE_HEAVY_PICK = Utils.nullValue();
	public static ItemHeavyShovel ORNATE_HEAVY_SHOVEL = Utils.nullValue();
	public static ItemHandpick ORNATE_HANDPICK = Utils.nullValue();
	public static ItemTrow ORNATE_TROW = Utils.nullValue();
	public static ItemScythe ORNATE_SCYTHE = Utils.nullValue();
	public static ItemMattock ORNATE_MATTOCK = Utils.nullValue();
	public static ItemLumberAxe ORNATE_LUMBER = Utils.nullValue();
	public static ItemHammer ORNATE_HAMMER = Utils.nullValue();
	public static ItemHammer ORNATE_HEAVY_HAMMER = Utils.nullValue();
	public static ItemTongs ORNATE_TONGS = Utils.nullValue();
	public static ItemShearsMFR ORNATE_SHEARS = Utils.nullValue();
	public static ItemKnife ORNATE_KNIFE = Utils.nullValue();
	public static ItemNeedle ORNATE_NEEDLE = Utils.nullValue();
	public static ItemSaw ORNATE_SAW = Utils.nullValue();
	public static ItemSpanner ORNATE_SPANNER = Utils.nullValue();
	public static ItemBowMFR ORNATE_BOW = Utils.nullValue();

	// ORNATE ARMOR
	public static ItemCustomArmour ORNATE_SCALE_HELMET = Utils.nullValue();
	public static ItemCustomArmour ORNATE_SCALE_CHESTPLATE = Utils.nullValue();
	public static ItemCustomArmour ORNATE_SCALE_LEGGINGS = Utils.nullValue();
	public static ItemCustomArmour ORNATE_SCALE_BOOTS = Utils.nullValue();
	public static ItemCustomArmour ORNATE_CHAIN_HELMET = Utils.nullValue();
	public static ItemCustomArmour ORNATE_CHAIN_CHESTPLATE = Utils.nullValue();
	public static ItemCustomArmour ORNATE_CHAIN_LEGGINGS = Utils.nullValue();
	public static ItemCustomArmour ORNATE_CHAIN_BOOTS = Utils.nullValue();
	public static ItemCustomArmour ORNATE_SPLINT_HELMET = Utils.nullValue();
	public static ItemCustomArmour ORNATE_SPLINT_CHESTPLATE = Utils.nullValue();
	public static ItemCustomArmour ORNATE_SPLINT_LEGGINGS = Utils.nullValue();
	public static ItemCustomArmour ORNATE_SPLINT_BOOTS = Utils.nullValue();
	public static ItemCustomArmour ORNATE_PLATE_HELMET = Utils.nullValue();
	public static ItemCustomArmour ORNATE_PLATE_CHESTPLATE = Utils.nullValue();
	public static ItemCustomArmour ORNATE_PLATE_LEGGINGS = Utils.nullValue();
	public static ItemCustomArmour ORNATE_PLATE_BOOTS = Utils.nullValue();

	// Tools
	public static Item TRAINING_SWORD = Utils.nullValue();
	public static Item TRAINING_WARAXE = Utils.nullValue();
	public static Item TRAINING_MACE = Utils.nullValue();
	public static Item TRAINING_SPEAR = Utils.nullValue();
	public static Item STONE_KNIFE = Utils.nullValue();
	public static Item STONE_HAMMER = Utils.nullValue();
	public static Item STONE_TONGS = Utils.nullValue();
	public static Item BONE_NEEDLE = Utils.nullValue();
	public static Item STONE_PICK = Utils.nullValue();
	public static Item STONE_AXE = Utils.nullValue();
	public static Item STONE_SPADE = Utils.nullValue();
	public static Item STONE_HOE = Utils.nullValue();
	public static Item STONE_SWORD = Utils.nullValue();
	public static Item STONE_MACE = Utils.nullValue();
	public static Item STONE_WARAXE = Utils.nullValue();
	public static Item STONE_SPEAR = Utils.nullValue();
	public static Item BANDAGE_CRUDE = Utils.nullValue();
	public static Item BANDAGE_WOOL = Utils.nullValue();
	public static Item BANDAGE_TOUGH = Utils.nullValue();
	public static ItemCrudeBomb BOMB_CRUDE = Utils.nullValue();
	public static ItemBomb BOMB_CUSTOM = Utils.nullValue();
	public static ItemMine MINE_CUSTOM = Utils.nullValue();
	public static ItemResearchBook RESEARCH_BOOK = Utils.nullValue();
	public static Item DRY_ROCKS = Utils.nullValue();
	public static Item TINDERBOX = Utils.nullValue();
	public static Item SKILLBOOK_ARTISANRY = Utils.nullValue();
	public static Item SKILLBOOK_CONSTRUCTION = Utils.nullValue();
	public static Item SKILLBOOK_PROVISIONING = Utils.nullValue();
	public static Item SKILLBOOK_ENGINEERING = Utils.nullValue();
	public static Item SKILLBOOK_COMBAT = Utils.nullValue();
	public static Item SKILLBOOK_ARTISANRY_MAX = Utils.nullValue();
	public static Item SKILLBOOK_CONSTRUCTION_MAX = Utils.nullValue();
	public static Item SKILLBOOK_PROVISIONING_MAX = Utils.nullValue();
	public static Item SKILLBOOK_ENGINEERING_MAX = Utils.nullValue();
	public static Item SKILLBOOK_COMBAT_MAX = Utils.nullValue();
	public static Item ENGIN_ANVIL_TOOLS = Utils.nullValue();
	public static Item EXPLODING_ARROW = Utils.nullValue();
	public static Item SPYGLASS = Utils.nullValue();
	public static Item CLIMBING_PICK_BASIC = Utils.nullValue();
	public static Item PARACHUTE = Utils.nullValue();
	public static Item SYRINGE = Utils.nullValue();
	public static Item SYRINGE_EMPTY = Utils.nullValue();
	public static Item LOOT_SACK_COMMON = Utils.nullValue();
	public static Item LOOT_SACK_VALUABLE = Utils.nullValue();
	public static Item LOOT_SACK_EXQUISITE = Utils.nullValue();
	public static ItemCrossbow CROSSBOW_CUSTOM = Utils.nullValue();
	public static Item EXPLODING_BOLT = Utils.nullValue();
	public static Item PAINT_BRUSH = Utils.nullValue();
	public static Item DEBUG_PLACE_ANCIENT_FORGE = Utils.nullValue();
	public static Item DEBUG_PLACE_ANCIENT_ALTAR = Utils.nullValue();
	public static Item DEBUG_PLACE_DWARVEN_STRONGHOLD = Utils.nullValue();
	public static Item SPAWNER_DRAGON = Utils.nullValue();
	public static Item SPAWNER_MINOTAUR = Utils.nullValue();
	public static Item SPAWNER_HOUND = Utils.nullValue();
	public static Item SPAWNER_COGWORK = Utils.nullValue();

	public static void initFood() {
		// MORSELS
		WOLF_RAW = new ItemFoodMFR("wolf_raw", 2, 0.2F, true);
		WOLF_COOKED = new ItemFoodMFR("wolf_cooked", 6, 0.6F, true);
		HORSE_RAW = new ItemFoodMFR("horse_raw", 4, 0.4F, true).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 500, 0), 0.5F);
		HORSE_COOKED = new ItemFoodMFR("horse_cooked", 7, 0.6F, true);
		GENERIC_MEAT_UNCOOKED = new ItemFoodMFR("generic_meat_uncooked", 2, 0.2F, true);
		GENERIC_MEAT_COOKED = new ItemFoodMFR("generic_meat_cooked", 5, 0.5F, true);
		GENERIC_MEAT_STRIP_UNCOOKED = new ItemFoodMFR("generic_meat_strip_uncooked", 2, 0.2F, true);
		GENERIC_MEAT_STRIP_COOKED = new ItemFoodMFR("generic_meat_strip_cooked", 5, 0.5F, true);
		GENERIC_MEAT_CHUNK_UNCOOKED = new ItemFoodMFR("generic_meat_chunk_uncooked", 2, 0.2F, true);
		GENERIC_MEAT_CHUNK_COOKED = new ItemFoodMFR("generic_meat_chunk_cooked", 5, 0.5F, true);
		GENERIC_MEAT_MINCE_UNCOOKED = new ItemFoodMFR("generic_meat_mince_uncooked", 2, 0.2F, true).setContainerItem(CLAY_POT);
		GENERIC_MEAT_MINCE_COOKED = new ItemFoodMFR("generic_meat_mince_cooked", 5, 0.5F, true).setContainerItem(CLAY_POT);
		FLOUR = new ItemComponentMFR("flour", 0).setCreativeTab(MineFantasyTabs.tabFood).setContainerItem(CLAY_POT);
		BREADCRUMBS = new ItemComponentMFR("breadcrumbs", 0).setCreativeTab(MineFantasyTabs.tabFood).setContainerItem(CLAY_POT);
		GUTS = new ItemComponentMFR("guts", 0).setCreativeTab(MineFantasyTabs.tabFood);
		BREADROLL = new ItemFoodMFR("breadroll", 5, 1.0F, false);
		BREAD_SLICE = new ItemFoodMFR("bread_slice", 2, 1.0F, false);
		CURDS = new ItemUnfinishedFood("curds");
		CHEESE_POT = new ItemUnfinishedFood("cheese_pot").setContainerItem(CLAY_POT);
		CHEESE_SLICE = new ItemFoodMFR("cheese_slice", 4, 1.0F, false, 0).setFoodStats(1, 0.0F, 0.0F, 1.0F);

		// T1 (basic mixing)
		// Util: Roast, Prep Block (Stone-Bronze Age)
		STEW = new ItemFoodMFR("stew", 5, 0.7F, false, 0).setFoodStats(1, 0.0F, 0.0F, 1.0F).setReturnItem(Items.BOWL).setMaxStackSize(1);
		OATS = new ItemFoodMFR("oats", 5, 0.7F, false, 0).setFoodStats(1, 0.0F, 0.8F, 0.2F).setReturnItem(Items.BOWL).setMaxStackSize(1);

		// T2 (Basic baking, stone oven, processed mixing)
		// Util: Stone Oven, Prep Block (Bronze Age - Early Iron Age)
		CHEESE_ROLL = new ItemFoodMFR("cheese_roll", 6, 0.8F, false, 0).setFoodStats(2, 0.0F, 0.4F, 0.6F).setMaxStackSize(4);
		JERKY = new ItemFoodMFR("jerky", 6, 0.8F, true, 0).setFoodStats(2, 0.0F, 0.0F, 1.0F).setMaxStackSize(8);
		SAUSAGE_RAW = new ItemFoodMFR("saussage_raw", 4, 0.8F, true, 0).setFoodStats(2, 0.0F, 0.1F, 0.6F).setMaxStackSize(16);
		SAUSAGE_COOKED = new ItemFoodMFR("saussage_cooked", 8, 0.8F, true, 0).setFoodStats(2, 0.0F, 0.2F, 0.8F).setMaxStackSize(16);
		SWEETROLL_UNICED = new ItemFoodMFR("sweetroll_uniced", 5, 0.8F, false, 0).setFoodStats(2, 0.5F, 0.0F, 0.2F).setEatTime(16).setAlwaysEdible().setMaxStackSize(64);
		SWEETROLL = new ItemMultiFood("sweetroll", 2, 3, 0.8F, false, 0).setFoodStats(2, 1.0F, 0.0F, 0.2F).setEatTime(16).setAlwaysEdible();
		SANDWITCH_MEAT = new ItemMultiFood("sandwitch_meat", 2, 6, 0.8F, false, 0).setFoodStats(2, 0.0F, 0.5F, 0.5F).setTranslationKey("sandwitch");
		SANDWITCH_BIG = new ItemMultiFood("sandwitch_big", 4, 6, 0.8F, false, 1).setFoodStats(2, 0.0F, 0.5F, 0.5F);

		// T3 (Quality baking, metal oven)
		// Util: Metal Oven, Prep Block, Steel Tools (Mid Iron Age)
		MEATPIE_SLICE = new ItemFoodMFR("meatpie_slice", 8, 1.0F, false, 0).setFoodStats(3, 0.0F, 0.2F, 0.8F).setMaxStackSize(16);
		PIESLICE_APPLE = new ItemFoodMFR("pieslice_apple", 5, 1.0F, false, 0).setFoodStats(3, 0.8F, 0.2F, 0.5F).setEatTime(16).setAlwaysEdible().setAlwaysEdible();
		PIESLICE_BERRY = new ItemFoodMFR("pieslice_berry", 5, 1.0F, false, 0).setFoodStats(3, 1.0F, 0.0F, 0.5F).setEatTime(16).setAlwaysEdible().setAlwaysEdible();

		// T4 (Advanced baking, multiple processes, temperature regulation)
		// Util : Metal Oven, Prep Block, Full tool set, Proper kitchen setup (Mid Iron
		// Age)
		PIESLICE_SHEPARDS = new ItemFoodMFR("pieslice_shepards", 10, 1.0F, false, 1).setFoodStats(4, 0.0F, 0.5F, 0.7F);
		CAKE_SLICE = new ItemFoodMFR("cake_slice", 5, 0.8F, false, 0).setFoodStats(3, 1.0F, 0.0F, 1.0F).setEatTime(16).setAlwaysEdible().setMaxStackSize(4);
		CARROTCAKE_SLICE = new ItemFoodMFR("carrotcake_slice", 6, 0.8F, false, 0).setFoodStats(4, 1.0F, 0.0F, 0.8F).setEatTime(16).setAlwaysEdible().setMaxStackSize(4);

		// T5 (Advanced baking, multiple process, temperature regulation, many
		// ingredients)
		CHOCCAKE_SLICE = new ItemFoodMFR("choccake_slice", 6, 0.8F, false, 0).setFoodStats(4, 1.0F, 0.0F, 1.2F).setEatTime(16).setAlwaysEdible().setMaxStackSize(4);

		// T6 (Perfected meals, extremely difficult to create)
		BFCAKE_SLICE = new ItemFoodMFR("bfcake_slice", 7, 1.0F, false, 1).setFoodStats(5, 1.0F, 0.0F, 1.3F).setEatTime(16).setAlwaysEdible().setMaxStackSize(4);
		ECLAIR_UNICED = new ItemFoodMFR("eclair_uniced", 5, 1.0F, false, 0).setFoodStats(5, 0.3F, 0.0F, 0.4F).setEatTime(16).setAlwaysEdible().setMaxStackSize(64);
		ECLAIR_EMPTY = new ItemFoodMFR("eclair_empty", 5, 1.0F, false, 0).setFoodStats(5, 0.5F, 0.0F, 0.6F).setEatTime(16).setAlwaysEdible().setMaxStackSize(64);
		ECLAIR = new ItemMultiFood("eclair", 4, 7, 1.0F, false, 1).setFoodStats(5, 1.0F, 0.0F, 1.5F).setEatTime(16).setAlwaysEdible().setMaxStackSize(1);

		// MISC
		CAKE_TIN = new ItemComponentMFR("cake_tin", 0).setCreativeTab(MineFantasyTabs.tabFood);
		PIE_TRAY = new ItemComponentMFR("pie_tray", 0).setStoragePlacement(Constants.StorageTextures.BIGPLATE, Constants.StorageTextures.TRAY).setCreativeTab(MineFantasyTabs.tabFood);
		ICING = new ItemComponentMFR("icing", 0).setCreativeTab(MineFantasyTabs.tabFood).setContainerItem(CLAY_POT);
		CUSTARD = new ItemComponentMFR("custard", 0).setCreativeTab(MineFantasyTabs.tabFood).setContainerItem(CLAY_POT);
		COCA_POWDER = new ItemComponentMFR("coca_powder", 0).setCreativeTab(MineFantasyTabs.tabFood).setContainerItem(CLAY_POT);
		CHOCOLATE = new ItemComponentMFR("chocolate", 0).setCreativeTab(MineFantasyTabs.tabFood).setContainerItem(CLAY_POT);
		CHOC_CHIPS = new ItemComponentMFR("choc_chips", 0).setCreativeTab(MineFantasyTabs.tabFood).setContainerItem(CLAY_POT);
		BERRIES = new ItemFoodMFR("berries", 2, 0.5F, false).setEatTime(10).setStaminaRestore(10F).setShouldRepeatPenaltyCheck().setAlwaysEdible();
		BERRIES_JUICY = new ItemFoodMFR("berries_juicy", 3, 0.5F, false).setEatTime(10).setStaminaRestore(25F).setRarity(1).setShouldRepeatPenaltyCheck().setAlwaysEdible();
		SWEETROLL_RAW = new ItemUnfinishedFood("sweetroll_raw").setMaxStackSize(64);
		ECLAIR_RAW = new ItemUnfinishedFood("eclair_raw").setMaxStackSize(64);
		CAKE_RAW = new ItemUnfinishedFood("cake_raw");
		CAKE_SIMPLE_RAW = new ItemUnfinishedFood("cake_simple_raw");
		CAKE_CARROT_RAW = new ItemUnfinishedFood("cake_carrot_raw");
		CAKE_CHOC_RAW = new ItemUnfinishedFood("cake_choc_raw");
		CAKE_BF_RAW = new ItemUnfinishedFood("cake_bf_raw");
		CAKE_UNICED = new ItemUnfinishedFood("cake_uniced").setContainerItem(CAKE_TIN);
		CAKE_SIMPLE_UNICED = new ItemUnfinishedFood("cake_simple_uniced").setContainerItem(CAKE_TIN);
		CAKE_CARROT_UNICED = new ItemUnfinishedFood("cake_carrot_uniced").setContainerItem(CAKE_TIN);
		CAKE_CHOC_UNICED = new ItemUnfinishedFood("cake_choc_uniced").setContainerItem(CAKE_TIN);
		CAKE_BF_UNICED = new ItemUnfinishedFood("cake_bf_uniced").setContainerItem(CAKE_TIN);
		PIE_MEAT_UNCOOKED = new ItemUnfinishedFood("pie_meat_uncooked");
		PIE_APPLE_UNCOOKED = new ItemUnfinishedFood("pie_apple_uncooked");
		PIE_BERRY_UNCOOKED = new ItemUnfinishedFood("pie_berry_uncooked");
		PIE_SHEPARD_UNCOOKED = new ItemUnfinishedFood("pie_shepard_uncooked");
		PIE_PUMPKIN_UNCOOKED = new ItemUnfinishedFood("pie_pumpkin_uncooked");
		PIE_MEAT_COOKED = new ItemUnfinishedFood("pie_meat_cooked").setContainerItem(PIE_TRAY);
		PIE_APPLE_COOKED = new ItemUnfinishedFood("pie_apple_cooked").setContainerItem(PIE_TRAY);
		PIE_BERRY_COOKED = new ItemUnfinishedFood("pie_berry_cooked").setContainerItem(PIE_TRAY);
		PIE_SHEPARD_COOKED = new ItemUnfinishedFood("pie_shepard_cooked").setContainerItem(PIE_TRAY);
		PIE_PUMPKIN_COOKED = new ItemUnfinishedFood("pie_pumpkin_cooked").setContainerItem(PIE_TRAY);
		SALT = new ItemComponentMFR("salt", 0).setCreativeTab(MineFantasyTabs.tabFood).setContainerItem(CLAY_POT);
		SUGAR_POT = new ItemComponentMFR("sugar_pot", 0).setCreativeTab(MineFantasyTabs.tabFood).setContainerItem(CLAY_POT);
		BOWL_WATER_SALT = new ItemComponentMFR("bowl_water_salt", 0).setCreativeTab(MineFantasyTabs.tabFood);
		DOUGH = new ItemUnfinishedFood("dough").setMaxStackSize(64);
		PASTRY = new ItemUnfinishedFood("pastry").setMaxStackSize(64);
		RAW_BREAD = new ItemUnfinishedFood("raw_bread").setMaxStackSize(64);
		JUG_UNCOOKED = new ItemJug("uncooked");
		JUG_WATER = new ItemJug("water").setStoragePlacement(Constants.StorageTextures.JUG, Constants.StorageTextures.JUG_WATER).setContainerItem(JUG_EMPTY);
		JUG_MILK = new ItemJug("milk").setStoragePlacement(Constants.StorageTextures.JUG, Constants.StorageTextures.JUG_MILK).setContainerItem(JUG_EMPTY);
		BURNT_FOOD = new ItemBurntFood("burnt_food");
		BURNT_POT = new ItemBurntFood("burnt_pot").setContainerItem(CLAY_POT);
		BURNT_PIE = new ItemBurntFood("burnt_pie").setContainerItem(PIE_TRAY);

		//SPECIAL RECIPES
		BURNT_CAKE = new ItemBurntFood("burnt_cake").setContainerItem(CAKE_TIN);
	}

	public static void initCustomArmor() {
		String standard = "standard";
		CreativeTabs tab = MineFantasyTabs.tabArmour;

		STANDARD_SCALE_HELMET = (ItemArmourMFR) new ItemCustomArmour(standard, "scale_helmet", ArmourDesign.SCALEMAIL, EntityEquipmentSlot.HEAD, "scale_layer_1", 0).setCreativeTab(tab);
		STANDARD_SCALE_CHESTPLATE = (ItemArmourMFR) new ItemCustomArmour(standard, "scale_chestplate", ArmourDesign.SCALEMAIL, EntityEquipmentSlot.CHEST, "scale_layer_1", 0).setCreativeTab(tab);
		STANDARD_SCALE_LEGGINGS = (ItemArmourMFR) new ItemCustomArmour(standard, "scale_leggings", ArmourDesign.SCALEMAIL, EntityEquipmentSlot.LEGS, "scale_layer_2", 0).setCreativeTab(tab);
		STANDARD_SCALE_BOOTS = (ItemArmourMFR) new ItemCustomArmour(standard, "scale_boots", ArmourDesign.SCALEMAIL, EntityEquipmentSlot.FEET, "scale_layer_1", 0).setCreativeTab(tab);

		STANDARD_CHAIN_HELMET = (ItemArmourMFR) new ItemCustomArmour(standard, "chain_helmet", ArmourDesign.CHAINMAIL, EntityEquipmentSlot.HEAD, "chain_layer_1", 0).setCreativeTab(tab);
		STANDARD_CHAIN_CHESTPLATE = (ItemArmourMFR) new ItemCustomArmour(standard, "chain_chestplate", ArmourDesign.CHAINMAIL, EntityEquipmentSlot.CHEST, "chain_layer_1", 0).setCreativeTab(tab);
		STANDARD_CHAIN_LEGGINGS = (ItemArmourMFR) new ItemCustomArmour(standard, "chain_leggings", ArmourDesign.CHAINMAIL, EntityEquipmentSlot.LEGS, "chain_layer_2", 0).setCreativeTab(tab);
		STANDARD_CHAIN_BOOTS = (ItemArmourMFR) new ItemCustomArmour(standard, "chain_boots", ArmourDesign.CHAINMAIL, EntityEquipmentSlot.FEET, "chain_layer_1", 0).setCreativeTab(tab);

		STANDARD_SPLINT_HELMET = (ItemArmourMFR) new ItemCustomArmour(standard, "splint_helmet", ArmourDesign.SPLINTMAIL, EntityEquipmentSlot.HEAD, "splint_layer_1", 0).setCreativeTab(tab);
		STANDARD_SPLINT_CHESTPLATE = (ItemArmourMFR) new ItemCustomArmour(standard, "splint_chestplate", ArmourDesign.SPLINTMAIL, EntityEquipmentSlot.CHEST, "splint_layer_1", 0).setCreativeTab(tab);
		STANDARD_SPLINT_LEGGINGS = (ItemArmourMFR) new ItemCustomArmour(standard, "splint_leggings", ArmourDesign.SPLINTMAIL, EntityEquipmentSlot.LEGS, "splint_layer_2", 0).setCreativeTab(tab);
		STANDARD_SPLINT_BOOTS = (ItemArmourMFR) new ItemCustomArmour(standard, "splint_boots", ArmourDesign.SPLINTMAIL, EntityEquipmentSlot.FEET, "splint_layer_1", 0).setCreativeTab(tab);

		STANDARD_PLATE_HELMET = (ItemArmourMFR) new ItemCustomArmour(standard, "plate_helmet", ArmourDesign.FIELDPLATE, EntityEquipmentSlot.HEAD, "plate_layer_1", 0).setCreativeTab(tab);
		STANDARD_PLATE_CHESTPLATE = (ItemArmourMFR) new ItemCustomArmour(standard, "plate_chestplate", ArmourDesign.FIELDPLATE, EntityEquipmentSlot.CHEST, "plate_layer_1", 0).setCreativeTab(tab);
		STANDARD_PLATE_LEGGINGS = (ItemArmourMFR) new ItemCustomArmour(standard, "plate_leggings", ArmourDesign.FIELDPLATE, EntityEquipmentSlot.LEGS, "plate_layer_2", 0).setCreativeTab(tab);
		STANDARD_PLATE_BOOTS = (ItemArmourMFR) new ItemCustomArmour(standard, "plate_boots", ArmourDesign.FIELDPLATE, EntityEquipmentSlot.FEET, "plate_layer_1", 0).setCreativeTab(tab);
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {

		IForgeRegistry<Item> registry = event.getRegistry();

		// Components
		registry.register(COPPER_INGOT);
		registry.register(TIN_INGOT);
		registry.register(BRONZE_INGOT);
		registry.register(PIG_IRON_INGOT);
		registry.register(STEEL_INGOT);
		registry.register(ENCRUSTED_INGOT);
		registry.register(BLACK_STEEL_WEAK_INGOT);
		registry.register(BLACK_STEEL_INGOT);
		registry.register(SILVER_INGOT);
		registry.register(RED_STEEL_WEAK_INGOT);
		registry.register(RED_STEEL_INGOT);
		registry.register(BLUE_STEEL_WEAK_INGOT);
		registry.register(BLUE_STEEL_INGOT);
		registry.register(ADAMANTIUM_INGOT);
		registry.register(MITHRIL_INGOT);
		registry.register(IGNOTUMITE_INGOT);
		registry.register(MITHIUM_INGOT);
		registry.register(ENDER_INGOT);
		registry.register(TUNGSTEN_INGOT);
		registry.register(OBSIDIAN_INGOT);
		registry.register(COMPOSITE_ALLOY_INGOT);
		registry.register(BAR);
		registry.register(TIMBER);
		registry.register(TIMBER_CUT);
		registry.register(TIMBER_PANE);
		registry.register(PERSISTENT_COMPONENT_FLAG);

		registry.register(CLAY_POT);
		registry.register(CLAY_POT_UNCOOKED);
		registry.register(INGOT_MOULD);
		registry.register(INGOT_MOULD_UNCOOKED);
		registry.register(PIE_TRAY_UNCOOKED);
		registry.register(VINE);
		registry.register(SHARP_ROCK);

		registry.register(FLUX);
		registry.register(FLUX_STRONG);

		registry.register(COAL_DUST);
		registry.register(NITRE);
		registry.register(SULFUR);
		registry.register(PREPARED_IRON);
		registry.register(BLACKPOWDER);
		registry.register(BLACKPOWDER_ADVANCED);
		registry.register(FLETCHING);
		registry.register(SHRAPNEL);
		registry.register(MAGMA_CREAM_REFINED);
		registry.register(BOMB_FUSE);
		registry.register(BOMB_FUSE_LONG);
		registry.register(BOMB_CASING_UNCOOKED);
		registry.register(BOMB_CASING_CERAMIC);
		registry.register(MINE_CASING_UNCOOKED);
		registry.register(MINE_CASING_CERAMIC);
		registry.register(BOMB_CASING_IRON);
		registry.register(MINE_CASING_IRON);
		registry.register(BOMB_CASING_OBSIDIAN);
		registry.register(MINE_CASING_OBSIDIAN);
		registry.register(BOMB_CASING_CRYSTAL);
		registry.register(MINE_CASING_CRYSTAL);
		registry.register(BOMB_CASING_ARROW);
		registry.register(BOMB_CASING_BOLT);

		registry.register(COKE);
		registry.register(DIAMOND_SHARDS);

		registry.register(CLAY_BRICK);
		registry.register(KAOLINITE);
		registry.register(KAOLINITE_DUST);
		registry.register(FIRECLAY);
		registry.register(FIRECLAY_BRICK);
		registry.register(STRONG_BRICK);

		registry.register(HIDE_SMALL);
		registry.register(HIDE_MEDIUM);
		registry.register(HIDE_LARGE);
		registry.register(RAWHIDE_SMALL);
		registry.register(RAWHIDE_MEDIUM);
		registry.register(RAWHIDE_LARGE);

		registry.register(DRAGON_HEART);

		registry.register(LEATHER_STRIP);
		registry.register(NAIL);
		registry.register(RIVET);
		registry.register(THREAD);
		registry.register(OBSIDIAN_ROCK);

		registry.register(ORE_COPPER);
		registry.register(ORE_TIN);
		registry.register(ORE_IRON);
		registry.register(ORE_SILVER);
		registry.register(ORE_GOLD);
		registry.register(ORE_TUNGSTEN);

		registry.register(HOT_ITEM);

		registry.register(JUG_PLANT_OIL);

		registry.register(TALISMAN_LESSER);
		registry.register(TALISMAN_GREATER);

		registry.register(BOLT);

		registry.register(IRON_FRAME);
		registry.register(IRON_STRUT);
		registry.register(BRONZE_GEARS);
		registry.register(TUNGSTEN_GEARS);
		registry.register(STEEL_TUBE);
		registry.register(COGWORK_SHAFT);

		registry.register(PREPARED_COAL);

		registry.register(INGOT_MOULD_FILLED);

		registry.register(CROSSBOW_STOCK_WOOD);
		registry.register(CROSSBOW_STOCK_IRON);
		registry.register(CROSSBOW_HANDLE_WOOD);

		registry.register(CROSSBOW_ARMS_BASIC);
		registry.register(CROSSBOW_ARMS_LIGHT);
		registry.register(CROSSBOW_ARMS_HEAVY);
		registry.register(CROSSBOW_ARMS_ADVANCED);

		registry.register(CROSSBOW_BAYONET);

		registry.register(CROSSBOW_AMMO);
		registry.register(CROSSBOW_SCOPE);

		registry.register(CROSSBOW_STRING_UNLOADED);
		registry.register(CROSSBOW_STRING_LOADED);

		registry.register(CHAIN_MESH);
		registry.register(SCALE_MESH);
		registry.register(SPLINT_MESH);
		registry.register(PLATE);
		registry.register(PLATE_HUGE);
		registry.register(METAL_HUNK);
		registry.register(ARROWHEAD);
		registry.register(BODKIN_HEAD);
		registry.register(BROAD_HEAD);
		registry.register(COGWORK_ARMOUR);

		registry.register(FLUX_POT);
		registry.register(COAL_FLUX);

		registry.register(COPPER_COIN);
		registry.register(SILVER_COIN);
		registry.register(GOLD_COIN);

		registry.register(HINGE);

		registry.register(COGWORK_PULLEY);

		registry.register(ANCIENT_JEWEL_MITHRIL);
		registry.register(ANCIENT_JEWEL_ADAMANT);
		registry.register(ANCIENT_JEWEL_MASTER);
		registry.register(TRILOGY_JEWEL);

		registry.register(ORNATE_ITEMS);

		// MORSELS
		registry.register(WOLF_RAW);
		registry.register(WOLF_COOKED);
		registry.register(HORSE_RAW);
		registry.register(HORSE_COOKED);
		registry.register(GENERIC_MEAT_UNCOOKED);
		registry.register(GENERIC_MEAT_COOKED);
		registry.register(GENERIC_MEAT_STRIP_UNCOOKED);
		registry.register(GENERIC_MEAT_STRIP_COOKED);
		registry.register(GENERIC_MEAT_CHUNK_UNCOOKED);
		registry.register(GENERIC_MEAT_CHUNK_COOKED);
		registry.register(GENERIC_MEAT_MINCE_UNCOOKED);
		registry.register(GENERIC_MEAT_MINCE_COOKED);
		registry.register(FLOUR);
		registry.register(BREADCRUMBS);
		registry.register(GUTS);
		registry.register(BREADROLL);
		registry.register(BREAD_SLICE);
		registry.register(CURDS);
		registry.register(CHEESE_POT);
		registry.register(CHEESE_SLICE);

		// T1 (basic mixing)
		// Util: Roast, Prep Block (Stone-Bronze Age)
		registry.register(STEW);
		registry.register(OATS);

		// T2 (Basic baking, stone oven, processed mixing)
		// Util: Stone Oven, Prep Block (Bronze Age - Early Iron Age)
		registry.register(CHEESE_ROLL);
		registry.register(JERKY);
		registry.register(SAUSAGE_RAW);
		registry.register(SAUSAGE_COOKED);
		registry.register(SWEETROLL_UNICED);

		registry.register(SWEETROLL);
		registry.register(SANDWITCH_MEAT);
		registry.register(SANDWITCH_BIG);

		// T3 (Quality baking, metal oven)
		// Util: Metal Oven, Prep Block, Steel Tools (Mid Iron Age)
		registry.register(MEATPIE_SLICE);
		registry.register(PIESLICE_APPLE);
		registry.register(PIESLICE_BERRY);

		// T4 (Advanced baking, multiple processes, temperature regulation)
		// Util : Metal Oven, Prep Block, Full tool set, Proper kitchen setup (Mid Iron
		// Age)
		registry.register(PIESLICE_SHEPARDS);
		registry.register(CAKE_SLICE);
		registry.register(CARROTCAKE_SLICE);

		// T5 (Advanced baking, multiple process, temperature regulation, many
		// ingreedients)
		registry.register(CHOCCAKE_SLICE);

		// T6 (Perfeted meals, extremely difficylt to create)
		registry.register(BFCAKE_SLICE);
		registry.register(ECLAIR_UNICED);
		registry.register(ECLAIR_EMPTY);

		registry.register(ECLAIR);

		// MISC
		registry.register(CAKE_TIN);
		registry.register(PIE_TRAY);
		registry.register(ICING);
		registry.register(CUSTARD);
		registry.register(COCA_POWDER);
		registry.register(CHOCOLATE);
		registry.register(CHOC_CHIPS);
		registry.register(BERRIES);
		registry.register(BERRIES_JUICY);
		registry.register(SWEETROLL_RAW);
		registry.register(ECLAIR_RAW);
		registry.register(CAKE_RAW);
		registry.register(CAKE_SIMPLE_RAW);
		registry.register(CAKE_CARROT_RAW);
		registry.register(CAKE_CHOC_RAW);
		registry.register(CAKE_BF_RAW);
		registry.register(CAKE_UNICED);
		registry.register(CAKE_SIMPLE_UNICED);
		registry.register(CAKE_CARROT_UNICED);
		registry.register(CAKE_CHOC_UNICED);
		registry.register(CAKE_BF_UNICED);
		registry.register(PIE_MEAT_UNCOOKED);
		registry.register(PIE_APPLE_UNCOOKED);
		registry.register(PIE_BERRY_UNCOOKED);
		registry.register(PIE_SHEPARD_UNCOOKED);
		registry.register(PIE_PUMPKIN_UNCOOKED);
		registry.register(PIE_MEAT_COOKED);
		registry.register(PIE_APPLE_COOKED);
		registry.register(PIE_BERRY_COOKED);
		registry.register(PIE_SHEPARD_COOKED);
		registry.register(PIE_PUMPKIN_COOKED);
		registry.register(SALT);
		registry.register(SUGAR_POT);
		registry.register(BOWL_WATER_SALT);
		registry.register(DOUGH);
		registry.register(PASTRY);
		registry.register(RAW_BREAD);
		registry.register(JUG_UNCOOKED);
		registry.register(JUG_EMPTY);
		registry.register(JUG_WATER);
		registry.register(JUG_MILK);
		registry.register(BURNT_FOOD);
		registry.register(BURNT_POT);
		registry.register(BURNT_PIE);

		// SPECIAL RECIPES
		registry.register(BURNT_CAKE);

		// Custom Armor
		registry.register(STANDARD_SCALE_HELMET);
		registry.register(STANDARD_SCALE_CHESTPLATE);
		registry.register(STANDARD_SCALE_LEGGINGS);
		registry.register(STANDARD_SCALE_BOOTS);

		registry.register(STANDARD_CHAIN_HELMET);
		registry.register(STANDARD_CHAIN_CHESTPLATE);
		registry.register(STANDARD_CHAIN_LEGGINGS);
		registry.register(STANDARD_CHAIN_BOOTS);

		registry.register(STANDARD_SPLINT_HELMET);
		registry.register(STANDARD_SPLINT_CHESTPLATE);
		registry.register(STANDARD_SPLINT_LEGGINGS);
		registry.register(STANDARD_SPLINT_BOOTS);

		registry.register(STANDARD_PLATE_HELMET);
		registry.register(STANDARD_PLATE_CHESTPLATE);
		registry.register(STANDARD_PLATE_LEGGINGS);
		registry.register(STANDARD_PLATE_BOOTS);

		// Standard Weapons
		registry.register(STANDARD_SWORD);
		registry.register(STANDARD_WARAXE);
		registry.register(STANDARD_MACE);
		registry.register(STANDARD_DAGGER);
		registry.register(STANDARD_SPEAR);
		registry.register(STANDARD_GREATSWORD);
		registry.register(STANDARD_BATTLEAXE);
		registry.register(STANDARD_WARHAMMER);
		registry.register(STANDARD_KATANA);
		registry.register(STANDARD_HALBEARD);
		registry.register(STANDARD_LANCE);

		// Standard Tools
		registry.register(STANDARD_PICK);
		registry.register(STANDARD_AXE);
		registry.register(STANDARD_SPADE);
		registry.register(STANDARD_HOE);

		registry.register(STANDARD_HEAVY_PICK);
		registry.register(STANDARD_HEAVY_SHOVEL);
		registry.register(STANDARD_HANDPICK);
		registry.register(STANDARD_TROW);
		registry.register(STANDARD_SCYTHE);
		registry.register(STANDARD_MATTOCK);
		registry.register(STANDARD_LUMBER);

		// Standard Crafters
		registry.register(STANDARD_HAMMER);
		registry.register(STANDARD_HEAVY_HAMMER);
		registry.register(STANDARD_TONGS);
		registry.register(STANDARD_SHEARS);
		registry.register(STANDARD_KNIFE);
		registry.register(STANDARD_NEEDLE);
		registry.register(STANDARD_SAW);

		registry.register(STANDARD_SPOON);
		registry.register(STANDARD_MALLET);
		registry.register(STANDARD_SPANNER);

		registry.register(STANDARD_BOW);
		registry.register(STANDARD_ARROW);
		registry.register(STANDARD_BOLT);
		registry.register(STANDARD_ARROW_BODKIN);
		registry.register(STANDARD_ARROW_BROAD);

		// Dragonforged
		// Weapons
		registry.register(DRAGONFORGED_SWORD);
		registry.register(DRAGONFORGED_WARAXE);
		registry.register(DRAGONFORGED_MACE);
		registry.register(DRAGONFORGED_DAGGER);
		registry.register(DRAGONFORGED_SPEAR);
		registry.register(DRAGONFORGED_GREATSWORD);
		registry.register(DRAGONFORGED_BATTLEAXE);
		registry.register(DRAGONFORGED_WARHAMMER);
		registry.register(DRAGONFORGED_KATANA);
		registry.register(DRAGONFORGED_HALBEARD);
		registry.register(DRAGONFORGED_LANCE);

		registry.register(DRAGONFORGED_BOW);

		// Tools
		registry.register(DRAGONFORGED_PICK);
		registry.register(DRAGONFORGED_AXE);
		registry.register(DRAGONFORGED_SPADE);
		registry.register(DRAGONFORGED_HOE);

		registry.register(DRAGONFORGED_HEAVY_PICK);
		registry.register(DRAGONFORGED_HEAVY_SHOVEL);
		registry.register(DRAGONFORGED_HANDPICK);
		registry.register(DRAGONFORGED_TROW);
		registry.register(DRAGONFORGED_SCYTHE);
		registry.register(DRAGONFORGED_MATTOCK);
		registry.register(DRAGONFORGED_LUMBER);
		registry.register(DRAGONFORGED_HAMMER);
		registry.register(DRAGONFORGED_HEAVY_HAMMER);

		// Crafters
		registry.register(DRAGONFORGED_TONGS);
		registry.register(DRAGONFORGED_SHEARS);
		registry.register(DRAGONFORGED_KNIFE);
		registry.register(DRAGONFORGED_NEEDLE);
		registry.register(DRAGONFORGED_SAW);
		registry.register(DRAGONFORGED_SPANNER);

		// Armours
		registry.register(DRAGONFORGED_SCALE_HELMET);
		registry.register(DRAGONFORGED_SCALE_CHESTPLATE);
		registry.register(DRAGONFORGED_SCALE_LEGGINGS);
		registry.register(DRAGONFORGED_SCALE_BOOTS);

		registry.register(DRAGONFORGED_CHAIN_HELMET);
		registry.register(DRAGONFORGED_CHAIN_CHESTPLATE);
		registry.register(DRAGONFORGED_CHAIN_LEGGINGS);
		registry.register(DRAGONFORGED_CHAIN_BOOTS);

		registry.register(DRAGONFORGED_SPLINT_HELMET);
		registry.register(DRAGONFORGED_SPLINT_CHESTPLATE);
		registry.register(DRAGONFORGED_SPLINT_LEGGINGS);
		registry.register(DRAGONFORGED_SPLINT_BOOTS);

		registry.register(DRAGONFORGED_PLATE_HELMET);
		registry.register(DRAGONFORGED_PLATE_CHESTPLATE);
		registry.register(DRAGONFORGED_PLATE_LEGGINGS);
		registry.register(DRAGONFORGED_PLATE_BOOTS);

		// Ornate
		// Weapons
		registry.register(ORNATE_SWORD);
		registry.register(ORNATE_WARAXE);
		registry.register(ORNATE_MACE);
		registry.register(ORNATE_DAGGER);
		registry.register(ORNATE_SPEAR);
		registry.register(ORNATE_GREATSWORD);
		registry.register(ORNATE_BATTLEAXE);
		registry.register(ORNATE_WARHAMMER);
		registry.register(ORNATE_KATANA);
		registry.register(ORNATE_HALBEARD);
		registry.register(ORNATE_LANCE);

		registry.register(ORNATE_BOW);

		// Tools
		registry.register(ORNATE_PICK);
		registry.register(ORNATE_AXE);
		registry.register(ORNATE_SPADE);
		registry.register(ORNATE_HOE);

		registry.register(ORNATE_HEAVY_PICK);
		registry.register(ORNATE_HEAVY_SHOVEL);
		registry.register(ORNATE_HANDPICK);
		registry.register(ORNATE_TROW);
		registry.register(ORNATE_SCYTHE);
		registry.register(ORNATE_MATTOCK);
		registry.register(ORNATE_LUMBER);
		registry.register(ORNATE_HAMMER);
		registry.register(ORNATE_HEAVY_HAMMER);

		// Crafters
		registry.register(ORNATE_TONGS);
		registry.register(ORNATE_SHEARS);
		registry.register(ORNATE_KNIFE);
		registry.register(ORNATE_NEEDLE);
		registry.register(ORNATE_SAW);
		registry.register(ORNATE_SPANNER);

		// Armours
		registry.register(ORNATE_SCALE_HELMET);
		registry.register(ORNATE_SCALE_CHESTPLATE);
		registry.register(ORNATE_SCALE_LEGGINGS);
		registry.register(ORNATE_SCALE_BOOTS);

		registry.register(ORNATE_CHAIN_HELMET);
		registry.register(ORNATE_CHAIN_CHESTPLATE);
		registry.register(ORNATE_CHAIN_LEGGINGS);
		registry.register(ORNATE_CHAIN_BOOTS);

		registry.register(ORNATE_SPLINT_HELMET);
		registry.register(ORNATE_SPLINT_CHESTPLATE);
		registry.register(ORNATE_SPLINT_LEGGINGS);
		registry.register(ORNATE_SPLINT_BOOTS);

		registry.register(ORNATE_PLATE_HELMET);
		registry.register(ORNATE_PLATE_CHESTPLATE);
		registry.register(ORNATE_PLATE_LEGGINGS);
		registry.register(ORNATE_PLATE_BOOTS);

	}

	public static void initComponent() {
		TIMBER = new ItemWoodComponent("timber").setCustom(1, "wood")
				.setStoragePlacement(Constants.StorageTextures.PLANK, Constants.StorageTextures.PLANK);
		TIMBER_CUT = new ItemWoodComponent("timber_cut").setCustom(1, "wood")
				.setStoragePlacement(Constants.StorageTextures.PLANK, Constants.StorageTextures.PLANK_CUT);
		TIMBER_PANE = new ItemWoodComponent("timber_pane").setCustom(6, "wood")
				.setStoragePlacement(Constants.StorageTextures.SHEET, Constants.StorageTextures.WOOD_PANE);
		PERSISTENT_COMPONENT_FLAG = new ItemPersistentComponentMarker("persistent_component_flag");

		COPPER_INGOT = new ItemBaseMFR("copper_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		TIN_INGOT = new ItemBaseMFR("tin_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		BRONZE_INGOT = new ItemBaseMFR("bronze_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		PIG_IRON_INGOT = new ItemBaseMFR("pig_iron_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		STEEL_INGOT = new ItemBaseMFR("steel_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		ENCRUSTED_INGOT = new ItemBaseMFR("encrusted_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		BLACK_STEEL_WEAK_INGOT = new ItemBaseMFR("black_steel_weak_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		BLACK_STEEL_INGOT = new ItemBaseMFR("black_steel_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		SILVER_INGOT = new ItemBaseMFR("silver_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		RED_STEEL_WEAK_INGOT = new ItemBaseMFR("red_steel_weak_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		RED_STEEL_INGOT = new ItemBaseMFR("red_steel_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		BLUE_STEEL_WEAK_INGOT = new ItemBaseMFR("blue_steel_weak_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		BLUE_STEEL_INGOT = new ItemBaseMFR("blue_steel_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		ADAMANTIUM_INGOT = new ItemBaseMFR("adamantium_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		MITHRIL_INGOT = new ItemBaseMFR("mithril_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		IGNOTUMITE_INGOT = new ItemBaseMFR("ignotumite_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		MITHIUM_INGOT = new ItemBaseMFR("mithium_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		ENDER_INGOT = new ItemBaseMFR("ender_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		TUNGSTEN_INGOT = new ItemBaseMFR("tungsten_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		OBSIDIAN_INGOT = new ItemBaseMFR("obsidian_ingot").setCreativeTab(MineFantasyTabs.tabMaterials);
		COMPOSITE_ALLOY_INGOT = new ItemBaseMFR("composite_alloy_ingot", Rarity.UNCOMMON).setCreativeTab(MineFantasyTabs.tabMaterials);
		BAR = new ItemMetalComponent("bar", 1F, "metal").setStoragePlacement("bar", "bar").setCreativeTab(MineFantasyTabs.tabMaterials);

		CLAY_POT = new ItemBowl("clay_pot").setStoragePlacement(Constants.StorageTextures.POT, Constants.StorageTextures.POT);
		CLAY_POT_UNCOOKED = new ItemBaseMFR("clay_pot_uncooked").setCreativeTab(MineFantasyTabs.tabMaterials);
		INGOT_MOULD = new ItemComponentMFR("ingot_mould").setStoragePlacement(Constants.StorageTextures.BAR, Constants.StorageTextures.MOULD);
		INGOT_MOULD_UNCOOKED = new ItemBaseMFR("ingot_mould_uncooked").setCreativeTab(MineFantasyTabs.tabMaterials);
		PIE_TRAY_UNCOOKED = new ItemComponentMFR("pie_tray_uncooked", 0);

		VINE = new ItemBaseMFR("vine").setCreativeTab(MineFantasyTabs.tabMaterials);
		SHARP_ROCK = new ItemBaseMFR("sharp_rock").setCreativeTab(MineFantasyTabs.tabMaterials);

		FLUX = new ItemBaseMFR("flux").setCreativeTab(MineFantasyTabs.tabMaterials);
		FLUX_STRONG = new ItemBaseMFR("flux_strong").setCreativeTab(MineFantasyTabs.tabMaterials);

		COAL_DUST = new ItemBaseMFR("coal_dust").setCreativeTab(MineFantasyTabs.tabMaterials).setContainerItem(CLAY_POT);
		NITRE = new ItemBaseMFR("nitre").setCreativeTab(MineFantasyTabs.tabMaterials);
		SULFUR = new ItemBaseMFR("sulfur").setCreativeTab(MineFantasyTabs.tabMaterials);
		PREPARED_IRON = new ItemBaseMFR("prepared_iron").setCreativeTab(MineFantasyTabs.tabMaterials);

		FLETCHING = new ItemBaseMFR("fletching").setCreativeTab(MineFantasyTabs.tabMaterials);

		BOMB_CASING_UNCOOKED = new ItemBaseMFR("bomb_casing_uncooked").setCreativeTab(MineFantasyTabs.tabMaterials);
		MINE_CASING_UNCOOKED = new ItemBaseMFR("mine_casing_uncooked").setCreativeTab(MineFantasyTabs.tabMaterials);

		BLACKPOWDER = new ItemBombComponent("blackpowder", Rarity.COMMON, "powder", "black_powder", 0).setContainerItem(CLAY_POT);
		BLACKPOWDER_ADVANCED = new ItemBombComponent("blackpowder_advanced", Rarity.UNCOMMON, "powder", "advanced_black_powder", 1).setContainerItem(CLAY_POT);

		SHRAPNEL = new ItemBombComponent("shrapnel", Rarity.COMMON, "filling", "shrapnel", 1).setContainerItem(CLAY_POT);
		MAGMA_CREAM_REFINED = new ItemBombComponent("magma_cream_refined", Rarity.UNCOMMON, "filling", "fire", 2).setContainerItem(CLAY_POT);

		BOMB_FUSE = new ItemBombComponent("bomb_fuse", Rarity.COMMON, "fuse", "basic",0);
		BOMB_FUSE_LONG = new ItemBombComponent("bomb_fuse_long", Rarity.COMMON, "fuse", "long",1);

		BOMB_CASING_CERAMIC = new ItemBombComponent("bomb_casing_ceramic", Rarity.COMMON, "bombcase", "ceramic",0);
		MINE_CASING_CERAMIC = new ItemBombComponent("mine_casing_ceramic", Rarity.COMMON, "minecase", "ceramic",0);
		BOMB_CASING_IRON = new ItemBombComponent("bomb_casing_iron", Rarity.COMMON, "bombcase", "iron", 1);
		MINE_CASING_IRON = new ItemBombComponent("mine_casing_iron", Rarity.COMMON, "minecase", "iron", 1);
		BOMB_CASING_OBSIDIAN = new ItemBombComponent("bomb_casing_obsidian", Rarity.UNCOMMON, "bombcase", "obsidian",2);
		MINE_CASING_OBSIDIAN = new ItemBombComponent("mine_casing_obsidian", Rarity.UNCOMMON, "minecase", "obsidian", 2);
		BOMB_CASING_CRYSTAL = new ItemBombComponent("bomb_casing_crystal", Rarity.UNCOMMON, "bombcase", "crystal", 3);
		MINE_CASING_CRYSTAL = new ItemBombComponent("mine_casing_crystal", Rarity.UNCOMMON, "minecase", "crystal", 3);

		BOMB_CASING_ARROW = new ItemBombComponent("bomb_casing_arrow", Rarity.UNCOMMON, "arrow", "arrow", 0);
		BOMB_CASING_BOLT = new ItemBombComponent("bomb_casing_bolt", Rarity.UNCOMMON, "bolt", "ceramic", 0);

		COKE = new ItemBaseMFR("coke", Rarity.UNCOMMON).setCreativeTab(MineFantasyTabs.tabMaterials);
		DIAMOND_SHARDS = new ItemBaseMFR("diamond_shards").setCreativeTab(MineFantasyTabs.tabMaterials);

		CLAY_BRICK = new ItemBaseMFR("clay_brick").setCreativeTab(MineFantasyTabs.tabMaterials);
		KAOLINITE = new ItemBaseMFR("kaolinite").setCreativeTab(MineFantasyTabs.tabMaterials);
		KAOLINITE_DUST = new ItemBaseMFR("kaolinite_dust").setCreativeTab(MineFantasyTabs.tabMaterials).setContainerItem(CLAY_POT);
		FIRECLAY = new ItemBaseMFR("fireclay").setCreativeTab(MineFantasyTabs.tabMaterials);
		FIRECLAY_BRICK = new ItemBaseMFR("fireclay_brick").setCreativeTab(MineFantasyTabs.tabMaterials);
		STRONG_BRICK = new ItemComponentMFR("strong_brick", 0).setStoragePlacement(Constants.StorageTextures.BAR, Constants.StorageTextures.FIREBRICK);

		HIDE_SMALL = new ItemBaseMFR("hide_small").setCreativeTab(MineFantasyTabs.tabMaterials);
		HIDE_MEDIUM = new ItemBaseMFR("hide_medium").setCreativeTab(MineFantasyTabs.tabMaterials);
		HIDE_LARGE = new ItemBaseMFR("hide_large").setCreativeTab(MineFantasyTabs.tabMaterials);
		RAWHIDE_SMALL = new ItemHide("rawhide_small", HIDE_SMALL, 1.0F);
		RAWHIDE_MEDIUM = new ItemHide("rawhide_medium", HIDE_MEDIUM, 1.5F);
		RAWHIDE_LARGE = new ItemHide("rawhide_large", HIDE_LARGE, 3.0F);

		DRAGON_HEART = new ItemSpecialDesign("dragon_heart", Rarity.UNCOMMON, "dragon").setCreativeTab(MineFantasyTabs.tabMaterials);

		LEATHER_STRIP = new ItemBaseMFR("leather_strip").setCreativeTab(MineFantasyTabs.tabMaterials);
		NAIL = new ItemBaseMFR("nail").setCreativeTab(MineFantasyTabs.tabMaterials);
		RIVET = new ItemBaseMFR("rivet").setCreativeTab(MineFantasyTabs.tabMaterials);
		THREAD = new ItemBaseMFR("thread").setCreativeTab(MineFantasyTabs.tabMaterials);
		OBSIDIAN_ROCK = new ItemBaseMFR("obsidian_rock").setCreativeTab(MineFantasyTabs.tabMaterials);

		ORE_COPPER = new ItemBaseMFR("ore_copper", Rarity.POOR).setCreativeTab(MineFantasyTabs.tabMaterials);
		ORE_TIN = new ItemBaseMFR("ore_tin", Rarity.POOR).setCreativeTab(MineFantasyTabs.tabMaterials);
		ORE_IRON = new ItemBaseMFR("ore_iron", Rarity.POOR).setCreativeTab(MineFantasyTabs.tabMaterials);
		ORE_SILVER = new ItemBaseMFR("ore_silver").setCreativeTab(MineFantasyTabs.tabMaterials);
		ORE_GOLD = new ItemBaseMFR("ore_gold").setCreativeTab(MineFantasyTabs.tabMaterials);
		ORE_TUNGSTEN = new ItemBaseMFR("ore_tungsten", Rarity.RARE).setCreativeTab(MineFantasyTabs.tabMaterials);

		HOT_ITEM = new ItemHeated();
		
		JUG_EMPTY = new ItemJug("empty").setStoragePlacement(Constants.StorageTextures.JUG, Constants.StorageTextures.JUG);
		JUG_PLANT_OIL = new ItemComponentMFR("jug_plant_oil", 0).setStoragePlacement(Constants.StorageTextures.JUG, Constants.StorageTextures.JUG_OIL).setContainerItem(JUG_EMPTY);

		TALISMAN_LESSER = new ItemBaseMFR("talisman_lesser", Rarity.RARE).setCreativeTab(MineFantasyTabs.tabMaterials);
		TALISMAN_GREATER = new ItemBaseMFR("talisman_greater", Rarity.EPIC).setCreativeTab(MineFantasyTabs.tabMaterials);

		BOLT = new ItemBaseMFR("bolt").setCreativeTab(MineFantasyTabs.tabMaterials);

		IRON_FRAME = new ItemBaseMFR("iron_frame").setCreativeTab(MineFantasyTabs.tabMaterials);
		IRON_STRUT = new ItemBaseMFR("iron_strut").setCreativeTab(MineFantasyTabs.tabMaterials);
		BRONZE_GEARS = new ItemBaseMFR("bronze_gears").setCreativeTab(MineFantasyTabs.tabMaterials);
		TUNGSTEN_GEARS = new ItemBaseMFR("tungsten_gears", Rarity.UNCOMMON).setCreativeTab(MineFantasyTabs.tabMaterials);
		STEEL_TUBE = new ItemBaseMFR("steel_tube").setCreativeTab(MineFantasyTabs.tabMaterials);
		COGWORK_SHAFT = new ItemBaseMFR("cogwork_shaft", Rarity.UNCOMMON).setCreativeTab(MineFantasyTabs.tabMaterials);

		PREPARED_COAL = new ItemBaseMFR("prepared_coal").setCreativeTab(MineFantasyTabs.tabMaterials);

		INGOT_MOULD_FILLED = new ItemFilledMould();

		CROSSBOW_STOCK_WOOD = new ItemCrossbowPart("crossbow_stock_wood", "stock").addSpeed(1.0F).addRecoil(0F);
		CROSSBOW_STOCK_IRON = new ItemCrossbowPart("crossbow_stock_iron", "stock").addSpeed(1.0F).addRecoil(-2F).addDurability(150);
		CROSSBOW_HANDLE_WOOD = new ItemCrossbowPart("crossbow_handle_wood", "stock").addSpeed(0.5F).addRecoil(2F).addSpread(1.0F).setHandCrossbow(true);

		CROSSBOW_ARMS_BASIC = new ItemCrossbowPart("crossbow_arms_basic", "mechanism").addPower(1.00F).addSpeed(0.50F).addRecoil(4F).addSpread(1.00F);
		CROSSBOW_ARMS_LIGHT = new ItemCrossbowPart("crossbow_arms_light", "mechanism").addPower(0.85F).addSpeed(0.25F).addRecoil(2F).addSpread(0.50F);
		CROSSBOW_ARMS_HEAVY = new ItemCrossbowPart("crossbow_arms_heavy", "mechanism").addPower(1.15F).addSpeed(1.00F).addRecoil(8F).addSpread(2.00F);
		CROSSBOW_ARMS_ADVANCED = new ItemCrossbowPart("crossbow_arms_advanced", "mechanism").addPower(1.15F).addSpeed(1.00F).addRecoil(6F).addSpread(0.25F).addDurability(150);

		CROSSBOW_BAYONET = new ItemCrossbowPart("crossbow_bayonet", "muzzle").addBash(4.0F).addRecoil(-1F).addSpeed(0.5F);

		CROSSBOW_AMMO = new ItemCrossbowPart("crossbow_ammo", "mod").addCapacity(5).addSpread(2.00F);
		CROSSBOW_SCOPE = new ItemCrossbowPart("crossbow_scope", "mod").setScope(0.75F);

		CROSSBOW_STRING_UNLOADED = new ItemCrossbowPart("crossbow_string_unloaded", "string_unloaded");
		CROSSBOW_STRING_LOADED = new ItemCrossbowPart("crossbow_string_loaded", "string_load");

		CHAIN_MESH = new ItemMetalComponent("chain_mesh", 1F, "metal").setStoragePlacement("sheet", "mail");
		SCALE_MESH = new ItemMetalComponent("scale_mesh", 1F, "metal").setStoragePlacement("sheet", "scale");
		SPLINT_MESH = new ItemMetalComponent("splint_mesh", 1F, "metal").setStoragePlacement("sheet", "splint");
		PLATE = new ItemMetalComponent("plate", 2F, "metal").setStoragePlacement("sheet", "plate");
		PLATE_HUGE = new ItemMetalComponent("plate_huge", 8F, "metal").setStoragePlacement("bigplate", "bigplate");
		METAL_HUNK = new ItemMetalComponent("metal_hunk", 0.25F, "metal");
		ARROWHEAD = new ItemMetalComponent("arrowhead", 1 / 4F, "metal");
		BODKIN_HEAD = new ItemMetalComponent("bodkin_head", 1 / 4F, "metal");
		BROAD_HEAD = new ItemMetalComponent("broad_head", 1 / 4F, "metal");

		COGWORK_ARMOUR = new ItemMetalComponent("cogwork_armour", 30F, "metal").setCanDamage().setCreativeTab(MineFantasyTabs.tabGadget).setMaxStackSize(1);

		FLUX_POT = new ItemBaseMFR("flux_pot").setCreativeTab(MineFantasyTabs.tabMaterials).setContainerItem(CLAY_POT);
		COAL_FLUX = new ItemBaseMFR("coal_flux").setCreativeTab(MineFantasyTabs.tabMaterials);

		COPPER_COIN = new ItemBaseMFR("copper_coin").setCreativeTab(MineFantasyTabs.tabMaterials);
		SILVER_COIN = new ItemBaseMFR("silver_coin").setCreativeTab(MineFantasyTabs.tabMaterials);
		GOLD_COIN = new ItemBaseMFR("gold_coin").setCreativeTab(MineFantasyTabs.tabMaterials);

		HINGE = new ItemBaseMFR("hinge").setCreativeTab(MineFantasyTabs.tabMaterials);
		COGWORK_PULLEY = new ItemBaseMFR("cogwork_pulley", Rarity.UNCOMMON).setCreativeTab(MineFantasyTabs.tabGadget);

		ANCIENT_JEWEL_MITHRIL = new ItemJewel("ancient_jewel_mithril", 20, EnumRarity.RARE, ItemJewel.MYTHIC, 2, "smelt_mithril", "smelt_master");
		ANCIENT_JEWEL_ADAMANT = new ItemJewel("ancient_jewel_adamant", 20, EnumRarity.RARE, ItemJewel.MYTHIC, 2, "smelt_adamantium", "smelt_master");
		ANCIENT_JEWEL_MASTER = new ItemJewel("ancient_jewel_master", 30, EnumRarity.EPIC, ItemJewel.MYTHIC, 1, "smelt_master");
		TRILOGY_JEWEL = new ItemJewel("trilogy_jewel", EnumRarity.EPIC, null, 1);

		ORNATE_ITEMS = new ItemSpecialDesign("ornate_items", Rarity.UNCOMMON, "ornate").setCreativeTab(MineFantasyTabs.tabMaterials);
	}

	public static void loadComponent() {

		Items.POTIONITEM.setContainerItem(Items.GLASS_BOTTLE);
		GameRegistry.registerFuelHandler(new FuelHandlerMF());
		MineFantasyReforgedAPI.registerFuelHandler(new AdvancedFuelHandler());
	}

	public static void addRandomDrops() {
		RandomOre.addOre(new ItemStack(KAOLINITE), 1.5F, "stone", -1, 32, 128, false);
		RandomOre.addOre(new ItemStack(FLUX), 2F, "stone", -1, 0, 128, false);
		RandomOre.addOre(new ItemStack(FLUX_STRONG), 1F, "stone", 2, 0, 128, false);
		RandomOre.addOre(new ItemStack(FLUX), 20F, "stoneLimestone", -1, 0, 256, true);
		RandomOre.addOre(new ItemStack(FLUX_STRONG), 10F, "stoneLimestone", 2, 0, 256, true);
		RandomOre.addOre(new ItemStack(Items.COAL), 2F, "stone", -1, 0, 128, false);
		RandomOre.addOre(new ItemStack(SULFUR), 2F, "stone", -1, 0, 16, false);
		RandomOre.addOre(new ItemStack(NITRE), 3F, "stone", -1, 0, 64, false);
		RandomOre.addOre(new ItemStack(Items.REDSTONE), 5F, "stone", 2, 0, 16, false);
		RandomOre.addOre(new ItemStack(Items.FLINT), 1F,"stone", -1, 0, 64, false);
		RandomOre.addOre(new ItemStack(DIAMOND_SHARDS), 0.2F,"stone", 2, 0, 16, false);
		RandomOre.addOre(new ItemStack(Items.QUARTZ), 0.5F, "stone", 3, 0, 16, false);

		RandomOre.addOre(new ItemStack(SULFUR), 10F, Blocks.NETHERRACK, -1, 0, 512, false);
		RandomOre.addOre(new ItemStack(Items.GLOWSTONE_DUST), 5F, Blocks.NETHERRACK, -1, 0, 512, false);
		RandomOre.addOre(new ItemStack(Items.QUARTZ), 5F, Blocks.NETHERRACK, -1, 0, 512, false);
		RandomOre.addOre(new ItemStack(Items.BLAZE_POWDER), 5F, Blocks.NETHERRACK, -1, 0, 512, false);
		RandomOre.addOre(new ItemStack(Items.NETHER_WART), 1F, Blocks.NETHERRACK, -1, 0, 512, false);
		RandomOre.addOre(new ItemStack(Items.SKULL, 1, 1), 0.01F, Blocks.NETHERRACK, -1, 0, 512, false);

		RandomDigs.addOre(new ItemStack(Blocks.SKULL, 1, 1), 0.1F, Blocks.SOUL_SAND, 3, 0, 256, false);
		RandomDigs.addOre(new ItemStack(Items.BONE), 5F, Blocks.DIRT, -1, 0, 256, false);
		RandomDigs.addOre(new ItemStack(Items.ROTTEN_FLESH), 2F, Blocks.DIRT, -1, 0, 256, false);
		RandomDigs.addOre(new ItemStack(Items.COAL, 1, 1), 1F, Blocks.DIRT, -1, 32, 64, false);

		RandomDigs.addOre(new ItemStack(Items.MELON_SEEDS), 5F, Blocks.GRASS, -1, 0, 256, false);
		RandomDigs.addOre(new ItemStack(Items.PUMPKIN_SEEDS), 8F, Blocks.GRASS, -1, 0, 256, false);

		RandomOre.addOre(new ItemStack(ORE_COPPER), 4F, "stone", 0, 48, 96, false);
		RandomOre.addOre(new ItemStack(ORE_TIN), 2F, "stone", 0, 48, 96, false);
		RandomOre.addOre(new ItemStack(ORE_IRON), 5F, "stone", 0, 0, 64, false);
		RandomOre.addOre(new ItemStack(ORE_SILVER), 1.5F, "stone", 0, 0, 32, false);
		RandomOre.addOre(new ItemStack(ORE_GOLD), 1F, "stone", 0, 0, 32, false);

		RandomOre.addOre(new ItemStack(ORE_TUNGSTEN), 2F, "stone", 3, 0, 16, false, "tungsten");
	}

	public static ItemStack bar(String material) {
		return bar(material, 1);
	}

	public static ItemStack bar(String material, int stackSize) {
		return ((ItemMetalComponent)BAR).createComponentItemStack(material, stackSize);
	}

	public static void initCustomTool() {
		String standard = "standard";
		CreativeTabs tab = MineFantasyTabs.tabWeapon;

		// Standard Weapons
		STANDARD_SWORD = new ItemSword(standard + "_sword", Item.ToolMaterial.IRON, 0, 1F).setCustom(standard).setTab(tab);
		STANDARD_WARAXE = new ItemWaraxe(standard + "_waraxe", Item.ToolMaterial.IRON, 0, 1F).setCustom(standard).setTab(tab);
		STANDARD_MACE = new ItemMace(standard + "_mace", Item.ToolMaterial.IRON, 0, 1F).setCustom(standard).setTab(tab);
		STANDARD_DAGGER = new ItemDagger(standard + "_dagger", Item.ToolMaterial.IRON, 0, 1F).setCustom(standard).setTab(tab);
		STANDARD_SPEAR = new ItemSpear(standard + "_spear", Item.ToolMaterial.IRON, 0, 1F).setCustom(standard).setTab(tab);
		STANDARD_GREATSWORD = new ItemGreatsword(standard + "_greatsword", Item.ToolMaterial.IRON, 0, 1F).setCustom(standard).setTab(tab);
		STANDARD_BATTLEAXE = new ItemBattleaxe(standard + "_battleaxe", Item.ToolMaterial.IRON, 0, 1F).setCustom(standard).setTab(tab);
		STANDARD_WARHAMMER = new ItemWarhammer(standard + "_warhammer", Item.ToolMaterial.IRON, 0, 1F).setCustom(standard).setTab(tab);
		STANDARD_KATANA = new ItemKatana(standard + "_katana", Item.ToolMaterial.IRON, 0, 1F).setCustom(standard).setTab(tab);
		STANDARD_HALBEARD = new ItemHalbeard(standard + "_halbeard", Item.ToolMaterial.IRON, 0, 1F).setCustom(standard).setTab(tab);
		STANDARD_LANCE = new ItemLance(standard + "_lance", Item.ToolMaterial.IRON, 0, 1F).setCustom(standard).setTab(tab);

		// Standard Tools
		tab = MineFantasyTabs.tabTool;
		STANDARD_PICK = (ItemPickMFR) new ItemPickMFR(standard + "_pick", Item.ToolMaterial.IRON, 0).setCustom(standard).setCreativeTab(tab);
		STANDARD_AXE = (ItemAxeMFR) new ItemAxeMFR(standard + "_axe", Item.ToolMaterial.IRON, 0).setCustom(standard).setCreativeTab(tab);
		STANDARD_SPADE = (ItemSpade) new ItemSpade(standard + "_spade", Item.ToolMaterial.IRON, 0).setCustom(standard).setCreativeTab(tab);
		STANDARD_HOE = (ItemHoeMFR) new ItemHoeMFR(standard + "_hoe", Item.ToolMaterial.IRON, 0).setCustom(standard).setCreativeTab(tab);

		tab = MineFantasyTabs.tabToolAdvanced;
		STANDARD_HEAVY_PICK = (ItemHeavyPick) new ItemHeavyPick(standard + "_heavy_pick", Item.ToolMaterial.IRON, 0).setCustom(standard).setCreativeTab(tab);
		STANDARD_HEAVY_SHOVEL = (ItemHeavyShovel) new ItemHeavyShovel(standard + "_heavy_shovel", Item.ToolMaterial.IRON, 0).setCustom(standard).setCreativeTab(tab);
		STANDARD_HANDPICK = (ItemHandpick) new ItemHandpick(standard + "_handpick", Item.ToolMaterial.IRON, 0).setCustom(standard).setCreativeTab(tab);
		STANDARD_TROW = (ItemTrow) new ItemTrow(standard + "_trow", Item.ToolMaterial.IRON, 0).setCustom(standard).setCreativeTab(tab);
		STANDARD_SCYTHE = (ItemScythe) new ItemScythe(standard + "_scythe", Item.ToolMaterial.IRON, 0).setCustom(standard).setCreativeTab(tab);
		STANDARD_MATTOCK = (ItemMattock) new ItemMattock(standard + "_mattock", Item.ToolMaterial.IRON, 0).setCustom(standard).setCreativeTab(tab);
		STANDARD_LUMBER = (ItemLumberAxe) new ItemLumberAxe(standard + "_lumber", Item.ToolMaterial.IRON, 0).setCustom(standard).setCreativeTab(tab);

		// Standard Crafters
		tab = MineFantasyTabs.tabCraftTool;
		STANDARD_HAMMER = (ItemHammer) new ItemHammer(standard + "_hammer", Item.ToolMaterial.IRON, false, 0, 0).setCustom(standard).setCreativeTab(tab);
		STANDARD_HEAVY_HAMMER = (ItemHammer) new ItemHammer(standard + "_heavy_hammer", Item.ToolMaterial.IRON, true, 0, 0).setCustom(standard).setCreativeTab(tab);
		STANDARD_TONGS = (ItemTongs) new ItemTongs(standard + "_tongs", Item.ToolMaterial.IRON, 0).setCustom(standard).setCreativeTab(tab);
		STANDARD_SHEARS = (ItemShearsMFR) new ItemShearsMFR(standard + "_shears", Item.ToolMaterial.IRON, 0, 0).setCustom().setCreativeTab(tab);
		STANDARD_KNIFE = (ItemKnife) new ItemKnife(standard + "_knife", Item.ToolMaterial.IRON, 0, 1F, 0).setCustom(standard).setCreativeTab(tab);
		STANDARD_NEEDLE = (ItemNeedle) new ItemNeedle(standard + "_needle", Item.ToolMaterial.IRON, 0, 0).setCustom(standard).setCreativeTab(tab);
		STANDARD_SAW = (ItemSaw) new ItemSaw(standard + "_saw", Item.ToolMaterial.IRON, 0, 0).setCustom(standard).setCreativeTab(tab);

		STANDARD_SPOON = (ItemBasicCraftTool) new ItemBasicCraftTool(standard + "_spoon", Tool.SPOON, 0, 64).setCustom(standard).setCreativeTab(tab);
		STANDARD_MALLET = (ItemBasicCraftTool) new ItemBasicCraftTool(standard + "_mallet", Tool.MALLET, 0, 64).setCustom(standard).setCreativeTab(tab);
		STANDARD_SPANNER = (ItemSpanner) new ItemSpanner(standard + "_spanner", 0, 0).setCustom(standard).setCreativeTab(tab);

		WASH_CLOTH_WOOL = new ItemWashCloth("wash_cloth_wool", 1).setMaxUses(6);

		tab = MineFantasyTabs.tabArchery;
		STANDARD_BOW = (ItemBowMFR) new ItemBowMFR(standard + "_bow", EnumBowType.SHORTBOW).setCustom(standard).setCreativeTab(tab);
		STANDARD_BOLT = (ItemArrowMFR) new ItemArrowMFR(standard, ArrowType.BOLT, 20).setCustom().setAmmoType("bolt").setCreativeTab(tab);
		STANDARD_ARROW = (ItemArrowMFR) new ItemArrowMFR(standard, ArrowType.NORMAL, 16).setCustom().setCreativeTab(tab);
		STANDARD_ARROW_BODKIN = (ItemArrowMFR) new ItemArrowMFR(standard, ArrowType.BODKIN, 16).setCustom().setCreativeTab(tab);
		STANDARD_ARROW_BROAD = (ItemArrowMFR) new ItemArrowMFR(standard, ArrowType.BROADHEAD, 16).setCustom().setCreativeTab(tab);
	}

	public static void initOrnate() {
		String ornate = "ornate";
		CreativeTabs tab = MineFantasyTabs.tabOrnate;
		float ratingMod = 0.8F;

		// Weapons
		ORNATE_SWORD = new ItemSword(ornate + "_sword", ORNATE, 0, 1F).setCustom(ornate).setTab(tab).modifyBaseDamage(-1);
		ORNATE_WARAXE = new ItemWaraxe(ornate + "_waraxe", ORNATE, 0, 1F).setCustom(ornate).setTab(tab).modifyBaseDamage(-1);
		ORNATE_MACE = new ItemMace(ornate + "_mace", ORNATE, 0, 1F).setCustom(ornate).setTab(tab).modifyBaseDamage(-1);
		ORNATE_DAGGER = new ItemDagger(ornate + "_dagger", ORNATE, 0, 1F).setCustom(ornate).setTab(tab).modifyBaseDamage(-1);
		ORNATE_SPEAR = new ItemSpear(ornate + "_spear", ORNATE, 0, 1F).setCustom(ornate).setTab(tab).modifyBaseDamage(-1);
		ORNATE_GREATSWORD = new ItemGreatsword(ornate + "_greatsword", ORNATE, 0, 1F).setCustom(ornate).setTab(tab).modifyBaseDamage(-1);
		ORNATE_BATTLEAXE = new ItemBattleaxe(ornate + "_battleaxe", ORNATE, 0, 1F).setCustom(ornate).setTab(tab).modifyBaseDamage(-1);
		ORNATE_WARHAMMER = new ItemWarhammer(ornate + "_warhammer", ORNATE, 0, 1F).setCustom(ornate).setTab(tab).modifyBaseDamage(-1);
		ORNATE_KATANA = new ItemKatana(ornate + "_katana", ORNATE, 0, 1F).setCustom(ornate).setTab(tab).modifyBaseDamage(-1);
		ORNATE_HALBEARD = new ItemHalbeard(ornate + "_halbeard", ORNATE, 0, 1F).setCustom(ornate).setTab(tab).modifyBaseDamage(-1);
		ORNATE_LANCE = new ItemLance(ornate + "_lance", ORNATE, 0, 1F).setCustom(ornate).setTab(tab).modifyBaseDamage(-1);

		ORNATE_BOW = (ItemBowMFR) new ItemBowMFR(ornate + "_bow", ORNATE, EnumBowType.SHORTBOW, 1).setCustom(ornate).setCreativeTab(tab);

		// Tools
		ORNATE_PICK = (ItemPickMFR) new ItemPickMFR(ornate + "_pick", ORNATE, 0).setEfficiencyMod(1.25F).setCustom(ornate).setCreativeTab(tab);
		ORNATE_AXE = (ItemAxeMFR) new ItemAxeMFR(ornate + "_axe", ORNATE, 0).setCustom(ornate).setCreativeTab(tab);
		ORNATE_SPADE = (ItemSpade) new ItemSpade(ornate + "_spade", ORNATE, 0).setCustom(ornate).setCreativeTab(tab);
		ORNATE_HOE = (ItemHoeMFR) new ItemHoeMFR(ornate + "_hoe", ORNATE, 0).setCustom(ornate).setCreativeTab(tab);

		ORNATE_HEAVY_PICK = (ItemHeavyPick) new ItemHeavyPick(ornate + "_heavy_pick", ORNATE, 0).setCustom(ornate).setCreativeTab(tab);
		ORNATE_HEAVY_SHOVEL = (ItemHeavyShovel) new ItemHeavyShovel(ornate + "_heavy_shovel", ORNATE, 0).setCustom(ornate).setCreativeTab(tab);
		ORNATE_HANDPICK = (ItemHandpick) new ItemHandpick(ornate + "_handpick", ORNATE, 0).setCustom(ornate).setCreativeTab(tab);
		ORNATE_TROW = (ItemTrow) new ItemTrow(ornate + "_trow", ORNATE, 0).setCustom(ornate).setCreativeTab(tab);
		ORNATE_SCYTHE = (ItemScythe) new ItemScythe(ornate + "_scythe", ORNATE, 0).setCustom(ornate).setCreativeTab(tab);
		ORNATE_MATTOCK = (ItemMattock) new ItemMattock(ornate + "_mattock", ORNATE, 0).setCustom(ornate).setCreativeTab(tab);
		ORNATE_LUMBER = (ItemLumberAxe) new ItemLumberAxe(ornate + "_lumber", ORNATE, 0).setCustom(ornate).setCreativeTab(tab);
		ORNATE_HAMMER = (ItemHammer) new ItemHammer(ornate + "_hammer", ORNATE, false, 0, 0).setCustom(ornate).setCreativeTab(tab);
		ORNATE_HEAVY_HAMMER = (ItemHammer) new ItemHammer(ornate + "_heavy_hammer", ORNATE, true, 0, 0).setCustom(ornate).setCreativeTab(tab);

		// Crafters
		ORNATE_TONGS = (ItemTongs) new ItemTongs(ornate + "_tongs", ORNATE, 0).setCustom(ornate).setCreativeTab(tab);
		ORNATE_SHEARS = (ItemShearsMFR) new ItemShearsMFR(ornate + "_shears", ORNATE, 0, 0).setCustom().setCreativeTab(tab);
		ORNATE_KNIFE = (ItemKnife) new ItemKnife(ornate + "_knife", ORNATE, 0, 1F, 0).setCustom(ornate).setCreativeTab(tab);
		ORNATE_NEEDLE = (ItemNeedle) new ItemNeedle(ornate + "_needle", ORNATE, 0, 0).setCustom(ornate).setCreativeTab(tab);
		ORNATE_SAW = (ItemSaw) new ItemSaw(ornate + "_saw", ORNATE, 0, 0).setCustom(ornate).setCreativeTab(tab);
		ORNATE_SPANNER = (ItemSpanner) new ItemSpanner(ornate + "_spanner", 0, 0).setCustom(ornate).setCreativeTab(tab);

		// Armours
		ORNATE_SCALE_HELMET = (ItemCustomArmour) new ItemCustomArmour(ornate, "scale_helmet", ArmourDesign.SCALEMAIL, EntityEquipmentSlot.HEAD, "scale_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		ORNATE_SCALE_CHESTPLATE = (ItemCustomArmour) new ItemCustomArmour(ornate, "scale_chestplate", ArmourDesign.SCALEMAIL, EntityEquipmentSlot.CHEST, "scale_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		ORNATE_SCALE_LEGGINGS = (ItemCustomArmour) new ItemCustomArmour(ornate, "scale_leggings", ArmourDesign.SCALEMAIL, EntityEquipmentSlot.LEGS, "scale_layer_2", 0).modifyRating(ratingMod).setCreativeTab(tab);
		ORNATE_SCALE_BOOTS = (ItemCustomArmour) new ItemCustomArmour(ornate, "scale_boots", ArmourDesign.SCALEMAIL, EntityEquipmentSlot.FEET, "scale_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);

		ORNATE_CHAIN_HELMET = (ItemCustomArmour) new ItemCustomArmour(ornate, "chain_helmet", ArmourDesign.CHAINMAIL, EntityEquipmentSlot.HEAD, "chain_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		ORNATE_CHAIN_CHESTPLATE = (ItemCustomArmour) new ItemCustomArmour(ornate, "chain_chestplate", ArmourDesign.CHAINMAIL, EntityEquipmentSlot.CHEST, "chain_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		ORNATE_CHAIN_LEGGINGS = (ItemCustomArmour) new ItemCustomArmour(ornate, "chain_leggings", ArmourDesign.CHAINMAIL, EntityEquipmentSlot.LEGS, "chain_layer_2", 0).modifyRating(ratingMod).setCreativeTab(tab);
		ORNATE_CHAIN_BOOTS = (ItemCustomArmour) new ItemCustomArmour(ornate, "chain_boots", ArmourDesign.CHAINMAIL, EntityEquipmentSlot.FEET, "chain_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);

		ORNATE_SPLINT_HELMET = (ItemCustomArmour) new ItemCustomArmour(ornate, "splint_helmet", ArmourDesign.SPLINTMAIL, EntityEquipmentSlot.HEAD, "splint_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		ORNATE_SPLINT_CHESTPLATE = (ItemCustomArmour) new ItemCustomArmour(ornate, "splint_chestplate", ArmourDesign.SPLINTMAIL, EntityEquipmentSlot.CHEST, "splint_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		ORNATE_SPLINT_LEGGINGS = (ItemCustomArmour) new ItemCustomArmour(ornate, "splint_leggings", ArmourDesign.SPLINTMAIL, EntityEquipmentSlot.LEGS, "splint_layer_2", 0).modifyRating(ratingMod).setCreativeTab(tab);
		ORNATE_SPLINT_BOOTS = (ItemCustomArmour) new ItemCustomArmour(ornate, "splint_boots", ArmourDesign.SPLINTMAIL, EntityEquipmentSlot.FEET, "splint_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);

		ORNATE_PLATE_HELMET = (ItemCustomArmour) new ItemCustomArmour(ornate, "plate_helmet", ArmourDesign.FIELDPLATE, EntityEquipmentSlot.HEAD, "plate_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		ORNATE_PLATE_CHESTPLATE = (ItemCustomArmour) new ItemCustomArmour(ornate, "plate_chestplate", ArmourDesign.FIELDPLATE, EntityEquipmentSlot.CHEST, "plate_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		ORNATE_PLATE_LEGGINGS = (ItemCustomArmour) new ItemCustomArmour(ornate, "plate_leggings", ArmourDesign.FIELDPLATE, EntityEquipmentSlot.LEGS, "plate_layer_2", 0).modifyRating(ratingMod).setCreativeTab(tab);
		ORNATE_PLATE_BOOTS = (ItemCustomArmour) new ItemCustomArmour(ornate, "plate_boots", ArmourDesign.FIELDPLATE, EntityEquipmentSlot.FEET, "plate_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
	}

	public static void initDragonforged() {
		String dragonforged = "dragonforged";
		CreativeTabs tab = MineFantasyTabs.tabDragonforged;
		float ratingMod = 1.2F;

		// Weapons
		DRAGONFORGED_SWORD = new ItemSword(dragonforged + "_sword", DRAGONFORGED, 0, 1F).setCustom(dragonforged).setTab(tab).modifyBaseDamage(1);
		DRAGONFORGED_WARAXE = new ItemWaraxe(dragonforged + "_waraxe", DRAGONFORGED, 0, 1F).setCustom(dragonforged).setTab(tab).modifyBaseDamage(1);
		DRAGONFORGED_MACE = new ItemMace(dragonforged + "_mace", DRAGONFORGED, 0, 1F).setCustom(dragonforged).setTab(tab).modifyBaseDamage(1);
		DRAGONFORGED_DAGGER = new ItemDagger(dragonforged + "_dagger", DRAGONFORGED, 0, 1F).setCustom(dragonforged).setTab(tab).modifyBaseDamage(1);
		DRAGONFORGED_SPEAR = new ItemSpear(dragonforged + "_spear", DRAGONFORGED, 0, 1F).setCustom(dragonforged).setTab(tab).modifyBaseDamage(1);
		DRAGONFORGED_GREATSWORD = new ItemGreatsword(dragonforged + "_greatsword", DRAGONFORGED, 0, 1F).setCustom(dragonforged).setTab(tab).modifyBaseDamage(1);
		DRAGONFORGED_BATTLEAXE = new ItemBattleaxe(dragonforged + "_battleaxe", DRAGONFORGED, 0, 1F).setCustom(dragonforged).setTab(tab).modifyBaseDamage(1);
		DRAGONFORGED_WARHAMMER = new ItemWarhammer(dragonforged + "_warhammer", DRAGONFORGED, 0, 1F).setCustom(dragonforged).setTab(tab).modifyBaseDamage(1);
		DRAGONFORGED_KATANA = new ItemKatana(dragonforged + "_katana", DRAGONFORGED, 0, 1F).setCustom(dragonforged).setTab(tab).modifyBaseDamage(1);
		DRAGONFORGED_HALBEARD = new ItemHalbeard(dragonforged + "_halbeard", DRAGONFORGED, 0, 1F).setCustom(dragonforged).setTab(tab).modifyBaseDamage(1);
		DRAGONFORGED_LANCE = new ItemLance(dragonforged + "_lance", DRAGONFORGED, 0, 1F).setCustom(dragonforged).setTab(tab).modifyBaseDamage(1);

		DRAGONFORGED_BOW = (ItemBowMFR) new ItemBowMFR(dragonforged + "_bow", DRAGONFORGED, EnumBowType.SHORTBOW, 1).setCustom(dragonforged).setCreativeTab(tab);

		// Tools
		DRAGONFORGED_PICK = (ItemPickMFR) new ItemPickMFR(dragonforged + "_pick", DRAGONFORGED, 0).setEfficiencyMod(1.25F).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_AXE = (ItemAxeMFR) new ItemAxeMFR(dragonforged + "_axe", DRAGONFORGED, 0).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_SPADE = (ItemSpade) new ItemSpade(dragonforged + "_spade", DRAGONFORGED, 0).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_HOE = (ItemHoeMFR) new ItemHoeMFR(dragonforged + "_hoe", DRAGONFORGED, 0).setCustom(dragonforged).setCreativeTab(tab);

		DRAGONFORGED_HEAVY_PICK = (ItemHeavyPick) new ItemHeavyPick(dragonforged + "_heavy_pick", DRAGONFORGED, 0).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_HEAVY_SHOVEL = (ItemHeavyShovel) new ItemHeavyShovel(dragonforged + "_heavy_shovel", DRAGONFORGED, 0).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_HANDPICK = (ItemHandpick) new ItemHandpick(dragonforged + "_handpick", DRAGONFORGED, 0).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_TROW = (ItemTrow) new ItemTrow(dragonforged + "_trow", DRAGONFORGED, 0).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_SCYTHE = (ItemScythe) new ItemScythe(dragonforged + "_scythe", DRAGONFORGED, 0).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_MATTOCK = (ItemMattock) new ItemMattock(dragonforged + "_mattock", DRAGONFORGED, 0).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_LUMBER = (ItemLumberAxe) new ItemLumberAxe(dragonforged + "_lumber", DRAGONFORGED, 0).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_HAMMER = (ItemHammer) new ItemHammer(dragonforged + "_hammer", DRAGONFORGED, false, 0, 0).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_HEAVY_HAMMER = (ItemHammer) new ItemHammer(dragonforged + "_heavy_hammer", DRAGONFORGED, true, 0, 0).setCustom(dragonforged).setCreativeTab(tab);

		// Crafters
		DRAGONFORGED_TONGS = (ItemTongs) new ItemTongs(dragonforged + "_tongs", DRAGONFORGED, 0).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_SHEARS = (ItemShearsMFR) new ItemShearsMFR(dragonforged + "_shears", DRAGONFORGED, 0, 0).setCustom().setCreativeTab(tab);
		DRAGONFORGED_KNIFE = (ItemKnife) new ItemKnife(dragonforged + "_knife", DRAGONFORGED, 0, 1F, 0).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_NEEDLE = (ItemNeedle) new ItemNeedle(dragonforged + "_needle", DRAGONFORGED, 0, 0).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_SAW = (ItemSaw) new ItemSaw(dragonforged + "_saw", DRAGONFORGED, 0, 0).setCustom(dragonforged).setCreativeTab(tab);
		DRAGONFORGED_SPANNER = (ItemSpanner) new ItemSpanner(dragonforged + "_spanner", 0, 0).setCustom(dragonforged).setCreativeTab(tab);

		// Armours
		DRAGONFORGED_SCALE_HELMET = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "scale_helmet", ArmourDesign.SCALEMAIL, EntityEquipmentSlot.HEAD, "scale_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		DRAGONFORGED_SCALE_CHESTPLATE = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "scale_chestplate", ArmourDesign.SCALEMAIL, EntityEquipmentSlot.CHEST, "scale_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		DRAGONFORGED_SCALE_LEGGINGS = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "scale_leggings", ArmourDesign.SCALEMAIL, EntityEquipmentSlot.LEGS, "scale_layer_2", 0).modifyRating(ratingMod).setCreativeTab(tab);
		DRAGONFORGED_SCALE_BOOTS = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "scale_boots", ArmourDesign.SCALEMAIL, EntityEquipmentSlot.FEET, "scale_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);

		DRAGONFORGED_CHAIN_HELMET = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "chain_helmet", ArmourDesign.CHAINMAIL, EntityEquipmentSlot.HEAD, "chain_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		DRAGONFORGED_CHAIN_CHESTPLATE = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "chain_chestplate", ArmourDesign.CHAINMAIL, EntityEquipmentSlot.CHEST, "chain_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		DRAGONFORGED_CHAIN_LEGGINGS = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "chain_leggings", ArmourDesign.CHAINMAIL, EntityEquipmentSlot.LEGS, "chain_layer_2", 0).modifyRating(ratingMod).setCreativeTab(tab);
		DRAGONFORGED_CHAIN_BOOTS = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "chain_boots", ArmourDesign.CHAINMAIL, EntityEquipmentSlot.FEET, "chain_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);

		DRAGONFORGED_SPLINT_HELMET = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "splint_helmet", ArmourDesign.SPLINTMAIL, EntityEquipmentSlot.HEAD, "splint_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		DRAGONFORGED_SPLINT_CHESTPLATE = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "splint_chestplate", ArmourDesign.SPLINTMAIL, EntityEquipmentSlot.CHEST, "splint_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		DRAGONFORGED_SPLINT_LEGGINGS = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "splint_leggings", ArmourDesign.SPLINTMAIL, EntityEquipmentSlot.LEGS, "splint_layer_2", 0).modifyRating(ratingMod).setCreativeTab(tab);
		DRAGONFORGED_SPLINT_BOOTS = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "splint_boots", ArmourDesign.SPLINTMAIL, EntityEquipmentSlot.FEET, "splint_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);

		DRAGONFORGED_PLATE_HELMET = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "plate_helmet", ArmourDesign.FIELDPLATE, EntityEquipmentSlot.HEAD, "plate_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		DRAGONFORGED_PLATE_CHESTPLATE = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "plate_chestplate", ArmourDesign.FIELDPLATE, EntityEquipmentSlot.CHEST, "plate_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
		DRAGONFORGED_PLATE_LEGGINGS = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "plate_leggings", ArmourDesign.FIELDPLATE, EntityEquipmentSlot.LEGS, "plate_layer_2", 0).modifyRating(ratingMod).setCreativeTab(tab);
		DRAGONFORGED_PLATE_BOOTS = (ItemCustomArmour) new ItemCustomArmour(dragonforged, "plate_boots", ArmourDesign.FIELDPLATE, EntityEquipmentSlot.FEET, "plate_layer_1", 0).modifyRating(ratingMod).setCreativeTab(tab);
	}

	public static void initTool() {
		TRAINING_SWORD = new ItemSword("training_sword", Item.ToolMaterial.WOOD, -1, 0.8F);
		TRAINING_WARAXE = new ItemWaraxe("training_waraxe", Item.ToolMaterial.WOOD, -1, 0.8F);
		TRAINING_MACE = new ItemMace("training_mace", Item.ToolMaterial.WOOD, -1, 0.8F);
		TRAINING_SPEAR = new ItemSpear("training_spear", Item.ToolMaterial.WOOD, -1, 0.8F);

		STONE_KNIFE = new ItemKnife("stone_knife", BaseMaterial.getMaterial("stone").getToolMaterial(), -1, 3.5F, 0);
		STONE_HAMMER = new ItemHammer("stone_hammer", BaseMaterial.getMaterial("stone").getToolMaterial(), false, -1, 0);
		STONE_TONGS = new ItemTongs("stone_tongs", BaseMaterial.getMaterial("stone").getToolMaterial(), -1);
		BONE_NEEDLE = new ItemNeedle("bone_needle", BaseMaterial.getMaterial("stone").getToolMaterial(), -1, 0);
		STONE_PICK = new ItemPickMFR("stone_pick", BaseMaterial.getMaterial("stone").getToolMaterial(), -1);
		STONE_AXE = new ItemAxeMFR("stone_axe", BaseMaterial.getMaterial("stone").getToolMaterial(), -1);
		STONE_SPADE = new ItemSpade("stone_spade", BaseMaterial.getMaterial("stone").getToolMaterial(), -1);
		STONE_HOE = new ItemHoeMFR("stone_hoe", BaseMaterial.getMaterial("stone").getToolMaterial(), -1);
		STONE_SWORD = new ItemSword("stone_sword", BaseMaterial.getMaterial("stone").getToolMaterial(), -1, 2.0F);
		STONE_MACE = new ItemMace("stone_mace", BaseMaterial.getMaterial("stone").getToolMaterial(), -1, 2.0F);
		STONE_WARAXE = new ItemWaraxe("stone_waraxe", BaseMaterial.getMaterial("stone").getToolMaterial(), -1, 2.0F);
		STONE_SPEAR = new ItemSpear("stone_spear", BaseMaterial.getMaterial("stone").getToolMaterial(), -1, 2.0F);

		BANDAGE_CRUDE = new ItemBandage("bandage_crude", 5F);
		BANDAGE_WOOL = new ItemBandage("bandage_wool", 8F);
		BANDAGE_TOUGH = new ItemBandage("bandage_tough", 12F);

		BOMB_CRUDE = new ItemCrudeBomb("bomb_crude");
		BOMB_CUSTOM = new ItemBomb("bomb_custom");
		MINE_CUSTOM = new ItemMine("mine_custom");

		RESEARCH_BOOK = new ItemResearchBook();

		DRY_ROCKS = new ItemLighter("dryrocks", 0.1F, 16);
		TINDERBOX = new ItemLighter("tinderbox", 0.5F, 100);

		SKILLBOOK_ARTISANRY = new ItemSkillBook("skillbook_artisanry", Skill.ARTISANRY);
		SKILLBOOK_CONSTRUCTION = new ItemSkillBook("skillbook_construction", Skill.CONSTRUCTION);
		SKILLBOOK_PROVISIONING = new ItemSkillBook("skillbook_provisioning", Skill.PROVISIONING);
		SKILLBOOK_ENGINEERING = new ItemSkillBook("skillbook_engineering", Skill.ENGINEERING);
		SKILLBOOK_COMBAT = new ItemSkillBook("skillbook_combat", Skill.COMBAT);

		SKILLBOOK_ARTISANRY_MAX = new ItemSkillBook("skillbook_artisanry_max", Skill.ARTISANRY).setMax();
		SKILLBOOK_CONSTRUCTION_MAX = new ItemSkillBook("skillbook_construction_max", Skill.CONSTRUCTION).setMax();
		SKILLBOOK_PROVISIONING_MAX = new ItemSkillBook("skillbook_provisioning_max", Skill.PROVISIONING).setMax();
		SKILLBOOK_ENGINEERING_MAX = new ItemSkillBook("skillbook_engineering_max", Skill.ENGINEERING).setMax();
		SKILLBOOK_COMBAT_MAX = new ItemSkillBook("skillbook_combat_max", Skill.COMBAT).setMax();

		ENGIN_ANVIL_TOOLS = new ItemEAnvilTools("engin_anvil_tools", 64);

		EXPLODING_ARROW = new ItemExplodingArrow();
		SPYGLASS = new ItemSpyglass();
		CLIMBING_PICK_BASIC = new ItemClimbingPick("climbing_pick_basic", Item.ToolMaterial.IRON, 0);
		PARACHUTE = new ItemParachute();

		SYRINGE = new ItemSyringe("syringe");
		SYRINGE_EMPTY = new ItemBaseMFR("syringe_empty").setCreativeTab(MineFantasyTabs.tabGadget);

		LOOT_SACK_COMMON = new ItemLootSack("loot_sack_common", 0);
		LOOT_SACK_VALUABLE = new ItemLootSack("loot_sack_valuable", 1);
		LOOT_SACK_EXQUISITE = new ItemLootSack("loot_sack_exquisite", 2);

		CROSSBOW_CUSTOM = new ItemCrossbow();
		EXPLODING_BOLT = new ItemExplodingBolt();

		PAINT_BRUSH = new ItemPaintBrush("paint_brush", 256);

		DEBUG_PLACE_ANCIENT_FORGE = new ItemWorldGenPlacer("world_gen_placer_ancient_forge", "world_gen_ancient_forge");
		DEBUG_PLACE_ANCIENT_ALTAR = new ItemWorldGenPlacer("world_gen_placer_ancient_altar", "world_gen_ancient_altar");
		DEBUG_PLACE_DWARVEN_STRONGHOLD = new ItemWorldGenPlacer("world_gen_placer_dwarven_stronghold", "world_gen_dwarven_stronghold");

		SPAWNER_DRAGON = new ItemMobSpawner("dragon");
		SPAWNER_MINOTAUR = new ItemMobSpawner("minotaur");
		SPAWNER_HOUND = new ItemMobSpawner("hound");
		SPAWNER_COGWORK = new ItemMobSpawner("cogwork");
	}

	@SubscribeEvent
	public static void registerItemTool(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		registry.register(TRAINING_SWORD);
		registry.register(TRAINING_WARAXE);
		registry.register(TRAINING_MACE);
		registry.register(TRAINING_SPEAR);

		registry.register(STONE_KNIFE);
		registry.register(STONE_HAMMER);
		registry.register(STONE_TONGS);
		registry.register(BONE_NEEDLE);
		registry.register(STONE_PICK);
		registry.register(STONE_AXE);
		registry.register(STONE_SPADE);
		registry.register(STONE_HOE);
		registry.register(STONE_SWORD);
		registry.register(STONE_MACE);
		registry.register(STONE_WARAXE);
		registry.register(STONE_SPEAR);

		registry.register(BANDAGE_CRUDE);
		registry.register(BANDAGE_WOOL);
		registry.register(BANDAGE_TOUGH);

		registry.register(BOMB_CRUDE);
		registry.register(BOMB_CUSTOM);
		registry.register(MINE_CUSTOM);

		registry.register(RESEARCH_BOOK);

		registry.register(DRY_ROCKS);
		registry.register(TINDERBOX);

		registry.register(SKILLBOOK_ARTISANRY);
		registry.register(SKILLBOOK_CONSTRUCTION);
		registry.register(SKILLBOOK_PROVISIONING);
		registry.register(SKILLBOOK_ENGINEERING);
		registry.register(SKILLBOOK_COMBAT);

		registry.register(SKILLBOOK_ARTISANRY_MAX);
		registry.register(SKILLBOOK_CONSTRUCTION_MAX);
		registry.register(SKILLBOOK_PROVISIONING_MAX);
		registry.register(SKILLBOOK_ENGINEERING_MAX);
		registry.register(SKILLBOOK_COMBAT_MAX);

		registry.register(ENGIN_ANVIL_TOOLS);

		registry.register(EXPLODING_ARROW);
		registry.register(SPYGLASS);
		registry.register(CLIMBING_PICK_BASIC);
		registry.register(PARACHUTE);

		registry.register(SYRINGE);
		registry.register(SYRINGE_EMPTY);

		registry.register(LOOT_SACK_COMMON);
		registry.register(LOOT_SACK_VALUABLE);
		registry.register(LOOT_SACK_EXQUISITE);

		registry.register(CROSSBOW_CUSTOM);
		registry.register(EXPLODING_BOLT);

		registry.register(PAINT_BRUSH);

		registry.register(DEBUG_PLACE_ANCIENT_FORGE);
		registry.register(DEBUG_PLACE_ANCIENT_ALTAR);
		registry.register(DEBUG_PLACE_DWARVEN_STRONGHOLD);

		registry.register(SPAWNER_DRAGON);
		registry.register(SPAWNER_MINOTAUR);
		registry.register(SPAWNER_HOUND);
		registry.register(SPAWNER_COGWORK);

		registry.register(WASH_CLOTH_WOOL);
	}

	public static void loadTool() {
		POOR = EnumHelper.addRarity("Poor", TextFormatting.DARK_GRAY, "poor");
		UNIQUE = EnumHelper.addRarity("Unique", TextFormatting.DARK_GREEN, "unique");
		RARE = EnumHelper.addRarity("Rare", TextFormatting.DARK_BLUE, "rare");

		RARITY = new EnumRarity[] {POOR, EnumRarity.COMMON, EnumRarity.UNCOMMON, EnumRarity.RARE, EnumRarity.EPIC};

		if (ConfigHardcore.HCCWeakItems) {
			weakenItems();
		}
	}

	private static void weakenItems() {
		weakenItem(Items.WOODEN_PICKAXE, 5);
		weakenItem(Items.WOODEN_AXE, 5);
		weakenItem(Items.WOODEN_SHOVEL, 5);
		weakenItem(Items.WOODEN_SWORD, 5);
		weakenItem(Items.WOODEN_HOE, 5);

		weakenItem(Items.LEATHER_HELMET);
		weakenItem(Items.LEATHER_CHESTPLATE);
		weakenItem(Items.LEATHER_LEGGINGS);
		weakenItem(Items.LEATHER_BOOTS);

		weakenItem(Items.STONE_PICKAXE, 10);
		weakenItem(Items.STONE_AXE, 10);
		weakenItem(Items.STONE_SHOVEL, 10);
		weakenItem(Items.STONE_SWORD, 10);
		weakenItem(Items.STONE_HOE, 10);

		weakenItem(Items.IRON_PICKAXE, 25);
		weakenItem(Items.IRON_AXE, 25);
		weakenItem(Items.IRON_SHOVEL, 25);
		weakenItem(Items.IRON_SWORD, 25);
		weakenItem(Items.IRON_HOE, 25);
		weakenItem(Items.IRON_HELMET);
		weakenItem(Items.IRON_CHESTPLATE);
		weakenItem(Items.IRON_LEGGINGS);
		weakenItem(Items.IRON_BOOTS);

		weakenItem(Items.GOLDEN_PICKAXE, 1);
		weakenItem(Items.GOLDEN_AXE, 1);
		weakenItem(Items.GOLDEN_SHOVEL, 1);
		weakenItem(Items.GOLDEN_SWORD, 1);
		weakenItem(Items.GOLDEN_HOE, 1);
		weakenItem(Items.GOLDEN_HELMET);
		weakenItem(Items.GOLDEN_CHESTPLATE);
		weakenItem(Items.GOLDEN_LEGGINGS);
		weakenItem(Items.GOLDEN_BOOTS);

		weakenItem(Items.DIAMOND_PICKAXE, 100);
		weakenItem(Items.DIAMOND_AXE, 100);
		weakenItem(Items.DIAMOND_SHOVEL, 100);
		weakenItem(Items.DIAMOND_SWORD, 100);
		weakenItem(Items.DIAMOND_HOE, 100);
		weakenItem(Items.DIAMOND_HELMET);
		weakenItem(Items.DIAMOND_CHESTPLATE);
		weakenItem(Items.DIAMOND_LEGGINGS);
		weakenItem(Items.DIAMOND_BOOTS);
	}

	private static void weakenItem(Item item) {
		weakenItem(item, (item.getMaxDamage() / 10) + 1);
	}

	private static void weakenItem(Item item, int hp) {
		if (item.isDamageable()) {
			item.setMaxDamage(hp);
		}
	}

	public static void initEnumActions(){
		EnumHelper.addAction("mfr_block");
	}
}
