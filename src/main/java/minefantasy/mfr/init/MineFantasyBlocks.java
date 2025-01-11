package minefantasy.mfr.init;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.block.BasicBlockMF;
import minefantasy.mfr.block.BlockAmmoBox;
import minefantasy.mfr.block.BlockAnvilMF;
import minefantasy.mfr.block.BlockBellows;
import minefantasy.mfr.block.BlockBerryBush;
import minefantasy.mfr.block.BlockBigFurnace;
import minefantasy.mfr.block.BlockBlastChamber;
import minefantasy.mfr.block.BlockBlastHeater;
import minefantasy.mfr.block.BlockBloomery;
import minefantasy.mfr.block.BlockBombBench;
import minefantasy.mfr.block.BlockBombPress;
import minefantasy.mfr.block.BlockCakeMFR;
import minefantasy.mfr.block.BlockCarpenter;
import minefantasy.mfr.block.BlockCheeseWheel;
import minefantasy.mfr.block.BlockChimney;
import minefantasy.mfr.block.BlockChimneyPipe;
import minefantasy.mfr.block.BlockCogwork;
import minefantasy.mfr.block.BlockComponent;
import minefantasy.mfr.block.BlockCrossbowBench;
import minefantasy.mfr.block.BlockCrucible;
import minefantasy.mfr.block.BlockEngineerTanner;
import minefantasy.mfr.block.BlockFirepit;
import minefantasy.mfr.block.BlockForge;
import minefantasy.mfr.block.BlockFrame;
import minefantasy.mfr.block.BlockFrameHolder;
import minefantasy.mfr.block.BlockKitchenBench;
import minefantasy.mfr.block.BlockLeavesMF;
import minefantasy.mfr.block.BlockLogMF;
import minefantasy.mfr.block.BlockMetalBarsMF;
import minefantasy.mfr.block.BlockMetalMF;
import minefantasy.mfr.block.BlockMythicDecor;
import minefantasy.mfr.block.BlockMythicOre;
import minefantasy.mfr.block.BlockOreMF;
import minefantasy.mfr.block.BlockPaneMF;
import minefantasy.mfr.block.BlockPie;
import minefantasy.mfr.block.BlockQuern;
import minefantasy.mfr.block.BlockRack;
import minefantasy.mfr.block.BlockRepairKit;
import minefantasy.mfr.block.BlockResearchBench;
import minefantasy.mfr.block.BlockRoad;
import minefantasy.mfr.block.BlockRoast;
import minefantasy.mfr.block.BlockSalvage;
import minefantasy.mfr.block.BlockSaplingMF;
import minefantasy.mfr.block.BlockSchematic;
import minefantasy.mfr.block.BlockSlab;
import minefantasy.mfr.block.BlockTanningRack;
import minefantasy.mfr.block.BlockTileEntity;
import minefantasy.mfr.block.BlockTrough;
import minefantasy.mfr.block.BlockWorldGenMarker;
import minefantasy.mfr.block.ConstructionBlockMF;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.item.ItemBlockAmmoBox;
import minefantasy.mfr.item.ItemBlockAnvil;
import minefantasy.mfr.item.ItemBlockBase;
import minefantasy.mfr.item.ItemBlockBerries;
import minefantasy.mfr.item.ItemBlockCake;
import minefantasy.mfr.item.ItemBlockOreMFR;
import minefantasy.mfr.item.ItemBlockRepairKit;
import minefantasy.mfr.item.ItemBlockSalvage;
import minefantasy.mfr.item.ItemBlockSlab;
import minefantasy.mfr.item.ItemBlockSpecialRender;
import minefantasy.mfr.item.ItemBlockToolRack;
import minefantasy.mfr.item.ItemBlockTrough;
import minefantasy.mfr.item.ItemSchematic;
import minefantasy.mfr.tile.TileEntityAmmoBox;
import minefantasy.mfr.tile.TileEntityAnvil;
import minefantasy.mfr.tile.TileEntityBellows;
import minefantasy.mfr.tile.TileEntityBigFurnace;
import minefantasy.mfr.tile.TileEntityBloomery;
import minefantasy.mfr.tile.TileEntityBombBench;
import minefantasy.mfr.tile.TileEntityBombPress;
import minefantasy.mfr.tile.TileEntityCarpenter;
import minefantasy.mfr.tile.TileEntityChimney;
import minefantasy.mfr.tile.TileEntityComponent;
import minefantasy.mfr.tile.TileEntityCrossbowBench;
import minefantasy.mfr.tile.TileEntityCrucible;
import minefantasy.mfr.tile.TileEntityFirepit;
import minefantasy.mfr.tile.TileEntityForge;
import minefantasy.mfr.tile.TileEntityKitchenBench;
import minefantasy.mfr.tile.TileEntityQuern;
import minefantasy.mfr.tile.TileEntityRack;
import minefantasy.mfr.tile.TileEntityResearchBench;
import minefantasy.mfr.tile.TileEntityRoast;
import minefantasy.mfr.tile.TileEntityTanningRack;
import minefantasy.mfr.tile.TileEntityTrough;
import minefantasy.mfr.tile.TileEntityWorldGenMarker;
import minefantasy.mfr.tile.blastfurnace.TileEntityBlastChamber;
import minefantasy.mfr.tile.blastfurnace.TileEntityBlastHeater;
import minefantasy.mfr.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = MineFantasyReforged.MOD_ID)
public class MineFantasyBlocks {

	public static Block COPPER_ORE = Utils.nullValue();
	public static Block TIN_ORE = Utils.nullValue();
	public static Block SILVER_ORE = Utils.nullValue();
	public static Block MYTHIC_ORE = Utils.nullValue();

	public static Block KAOLINITE_ORE = Utils.nullValue();
	public static Block NITRE_ORE = Utils.nullValue();
	public static Block SULFUR_ORE = Utils.nullValue();
	public static Block BORAX_ORE = Utils.nullValue();
	public static Block TUNGSTEN_ORE = Utils.nullValue();
	public static Block CLAY_ORE = Utils.nullValue();
	public static Block COAL_RICH_ORE = Utils.nullValue();

	public static Block MUD_BRICK = Utils.nullValue();
	public static Block MUD_BRICK_SLAB = Utils.nullValue();
	public static Block MUD_ROAD = Utils.nullValue();
	public static Block MUD_ROAD_BLOCK = Utils.nullValue();
	public static Block MUD_ROAD_BLOCK_SLAB = Utils.nullValue();

	public static Block COBBLE_BRICK = Utils.nullValue();
	public static Block COBBLE_BRICK_SLAB = Utils.nullValue();
	public static Block COBBLESTONE_ROAD = Utils.nullValue();
	public static Block COBBLESTONE_ROAD_BLOCK = Utils.nullValue();
	public static Block COBBLESTONE_ROAD_BLOCK_SLAB = Utils.nullValue();

	public static Block WINDOW = Utils.nullValue();
	public static Block FRAMED_GLASS = Utils.nullValue();
	public static Block FRAMED_GLASS_PANE = Utils.nullValue();
	public static Block WINDOW_PANE = Utils.nullValue();

	public static Block THATCH = Utils.nullValue();
	public static Block THATCH_SLAB = Utils.nullValue();
	public static Block THATCH_STAIR = Utils.nullValue();

	public static Block LIMESTONE = Utils.nullValue();
	public static Block LIMESTONE_SLAB = Utils.nullValue();
	public static Block LIMESTONE_COBBLE = Utils.nullValue();
	public static Block LIMESTONE_COBBLE_SLAB = Utils.nullValue();
	public static Block LIMESTONE_BRICK = Utils.nullValue();
	public static Block LIMESTONE_BRICK_SLAB = Utils.nullValue();
	public static Block LIMESTONE_ROAD = Utils.nullValue();
	public static Block LIMESTONE_ROAD_BLOCK = Utils.nullValue();
	public static Block LIMESTONE_ROAD_BLOCK_SLAB = Utils.nullValue();
	public static Block LIMESTONE_STAIRS = Utils.nullValue();
	public static Block LIMESTONE_COBBLE_STAIRS = Utils.nullValue();
	public static Block LIMESTONE_BRICK_STAIRS = Utils.nullValue();

	public static Block FIREBRICKS = Utils.nullValue();
	public static Block FIREBRICKS_SLAB = Utils.nullValue();
	public static Block CLAY_WALL = Utils.nullValue();
	public static Block CLAY_WALL_SLAB = Utils.nullValue();
	public static Block CLAY_WALL_CROSS = Utils.nullValue();
	public static Block CLAY_WALL_RIGHT_DIAGONAL = Utils.nullValue();
	public static Block CLAY_WALL_LEFT_DIAGONAL = Utils.nullValue();

	public static BlockMetalBarsMF IRON_BARS = Utils.nullValue();
	public static BlockMetalBarsMF BRONZE_BARS = Utils.nullValue();
	public static BlockMetalBarsMF STEEL_BARS = Utils.nullValue();
	public static BlockMetalBarsMF BLACK_STEEL_BARS = Utils.nullValue();
	public static BlockMetalBarsMF RED_STEEL_BARS = Utils.nullValue();
	public static BlockMetalBarsMF BLUE_STEEL_BARS = Utils.nullValue();

	public static BlockMetalMF COPPER_STORAGE = Utils.nullValue();
	public static BlockMetalMF TIN_STORAGE = Utils.nullValue();
	public static BlockMetalMF SILVER_STORAGE = Utils.nullValue();
	public static BlockMetalMF BRONZE_STORAGE = Utils.nullValue();
	public static BlockMetalMF PIG_IRON_STORAGE = Utils.nullValue();
	public static BlockMetalMF STEEL_STORAGE = Utils.nullValue();
	public static BlockMetalMF BLACK_STEEL_STORAGE = Utils.nullValue();
	public static BlockMetalMF RED_STEEL_STORAGE = Utils.nullValue();
	public static BlockMetalMF BLUE_STEEL_STORAGE = Utils.nullValue();
	public static BlockMetalMF ADAMANTIUM_STORAGE = Utils.nullValue();
	public static BlockMetalMF MITHRIL_STORAGE = Utils.nullValue();
	public static BlockMetalMF IGNOTUMITE_STORAGE = Utils.nullValue();
	public static BlockMetalMF MITHIUM_STORAGE = Utils.nullValue();
	public static BlockMetalMF ENDER_STORAGE = Utils.nullValue();

	public static BlockAnvilMF ANVIL_STONE = Utils.nullValue();
	public static BlockAnvilMF ANVIL_BRONZE = Utils.nullValue();
	public static BlockAnvilMF ANVIL_IRON = Utils.nullValue();
	public static BlockAnvilMF ANVIL_STEEL = Utils.nullValue();
	public static BlockAnvilMF ANVIL_BLACK_STEEL = Utils.nullValue();
	public static BlockAnvilMF ANVIL_BLUE_STEEL = Utils.nullValue();
	public static BlockAnvilMF ANVIL_RED_STEEL = Utils.nullValue();

	public static BlockCarpenter CARPENTER = Utils.nullValue();
	public static BlockKitchenBench KITCHEN_BENCH_GRANITE = Utils.nullValue();
	public static BlockKitchenBench KITCHEN_BENCH_ANDESITE = Utils.nullValue();
	public static BlockKitchenBench KITCHEN_BENCH_DIORITE = Utils.nullValue();
	public static BlockBombBench BOMB_BENCH = Utils.nullValue();
	public static BlockCrossbowBench CROSSBOW_BENCH = Utils.nullValue();

	public static Block CHEESE_WHEEL = Utils.nullValue();

	public static Block CAKE_VANILLA = Utils.nullValue();
	public static Block CAKE_CARROT = Utils.nullValue();
	public static Block CAKE_CHOCOLATE = Utils.nullValue();
	public static Block CAKE_BF = Utils.nullValue();

	public static Block PIE_MEAT = Utils.nullValue();

	public static Block PIE_APPLE = Utils.nullValue();
	public static Block PIE_BERRY = Utils.nullValue();

	public static Block PIE_SHEPARDS = Utils.nullValue();

	public static BlockBerryBush BERRY_BUSH = Utils.nullValue();

	public static Block BLAST_CHAMBER = Utils.nullValue();
	public static Block BLAST_HEATER = Utils.nullValue();

	public static Block CRUCIBLE_STONE = Utils.nullValue();
	public static Block CRUCIBLE_FIRECLAY = Utils.nullValue();
	public static Block CRUCIBLE_AUTO = Utils.nullValue();
	public static Block CRUCIBLE_MYTHIC = Utils.nullValue();
	public static Block CRUCIBLE_MASTER = Utils.nullValue();

	public static Block CHIMNEY_STONE = Utils.nullValue();
	public static Block CHIMNEY_STONE_WIDE = Utils.nullValue();
	public static Block CHIMNEY_STONE_EXTRACTOR = Utils.nullValue();
	public static Block CHIMNEY_PIPE = Utils.nullValue();

	public static Block TANNER = Utils.nullValue();
	public static Block TANNER_REFINED = Utils.nullValue();
	public static BlockTileEntity<TileEntityTanningRack> TANNER_METAL = Utils.nullValue();

	public static Block FORGE = Utils.nullValue();
	public static Block FORGE_METAL = Utils.nullValue();

	public static Block REPAIR_BASIC = Utils.nullValue();
	public static Block REPAIR_ADVANCED = Utils.nullValue();
	public static Block REPAIR_ORNATE = Utils.nullValue();

	public static BlockTileEntity<TileEntityBellows> BELLOWS = Utils.nullValue();

	public static Block REFINED_PLANKS = Utils.nullValue();
	public static Block REFINED_PLANKS_SLAB = Utils.nullValue();
	public static Block NAILED_PLANKS = Utils.nullValue();
	public static Block NAILED_PLANKS_SLAB = Utils.nullValue();
	public static Block REFINED_PLANKS_STAIR = Utils.nullValue();
	public static Block NAILED_PLANKS_STAIR = Utils.nullValue();

	public static Block REINFORCED_STONE = Utils.nullValue();
	public static Block REINFORCED_STONE_SLAB = Utils.nullValue();
	public static Block REINFORCED_STONE_ENGRAVED_0 = Utils.nullValue();
	public static Block REINFORCED_STONE_ENGRAVED_1 = Utils.nullValue();
	public static Block REINFORCED_STONE_ENGRAVED_2 = Utils.nullValue();
	public static Block REINFORCED_STONE_ENGRAVED_3 = Utils.nullValue();

	public static Block REINFORCED_STONE_BRICKS = Utils.nullValue();
	public static Block REINFORCED_STONE_BRICKS_SLAB = Utils.nullValue();
	public static Block REINFORCED_STONE_BRICKS_MOSSY = Utils.nullValue();
	public static Block REINFORCED_STONE_BRICKS_MOSSY_SLAB = Utils.nullValue();
	public static Block REINFORCED_STONE_BRICKS_CRACKED = Utils.nullValue();
	public static Block REINFORCED_STONE_BRICKS_CRACKED_SLAB = Utils.nullValue();

	public static Block REINFORCED_STONE_FRAMED = Utils.nullValue();
	public static Block REINFORCED_STONE_FRAMED_IRON = Utils.nullValue();

	public static Block RESEARCH = Utils.nullValue();
	public static Block TROUGH_WOOD = Utils.nullValue();

	public static BlockTileEntity<TileEntityBombPress> BOMB_PRESS = Utils.nullValue();

	public static Block SALVAGE_BASIC = Utils.nullValue();
	public static Block BLOOMERY = Utils.nullValue();
	public static Block LOG_YEW = Utils.nullValue();
	public static Block LOG_IRONBARK = Utils.nullValue();
	public static Block LOG_EBONY = Utils.nullValue();

	public static Block LEAVES_YEW = Utils.nullValue();
	public static Block LEAVES_IRONBARK = Utils.nullValue();
	public static Block LEAVES_EBONY = Utils.nullValue();

	public static Block SAPLING_YEW = Utils.nullValue();
	public static Block SAPLING_IRONBARK = Utils.nullValue();
	public static Block SAPLING_EBONY = Utils.nullValue();

	public static Block YEW_PLANKS = Utils.nullValue();
	public static Block YEW_PLANKS_SLAB = Utils.nullValue();
	public static Block IRONBARK_PLANKS = Utils.nullValue();
	public static Block IRONBARK_PLANKS_SLAB = Utils.nullValue();
	public static Block EBONY_PLANKS = Utils.nullValue();
	public static Block EBONY_PLANKS_SLAB = Utils.nullValue();

	public static BlockTileEntity<TileEntityQuern> QUERN = Utils.nullValue();

	public static Block MUD_BRICK_STAIRS = Utils.nullValue();
	public static Block COBBLE_BRICK_STAIRS = Utils.nullValue();
	public static Block FIREBRICK_STAIRS = Utils.nullValue();
	public static Block REINFORCED_STONE_STAIRS = Utils.nullValue();
	public static Block REINFORCED_STONE_BRICK_STAIRS = Utils.nullValue();

	public static Block YEW_STAIRS = Utils.nullValue();
	public static Block IRONBARK_STAIRS = Utils.nullValue();
	public static Block EBONY_STAIRS = Utils.nullValue();

	public static Block FIREPIT = Utils.nullValue();
	public static Block STOVE = Utils.nullValue();
	public static Block OVEN = Utils.nullValue();

	public static BlockTileEntity<TileEntityBigFurnace> FURNACE_HEATER = Utils.nullValue();
	public static BlockTileEntity<TileEntityBigFurnace> FURNACE_STONE = Utils.nullValue();

	public static Block TOOL_RACK_WOOD = Utils.nullValue();

	public static BlockAmmoBox FOOD_BOX_BASIC = Utils.nullValue();
	public static BlockAmmoBox AMMO_BOX_BASIC = Utils.nullValue();
	public static BlockAmmoBox CRATE_BASIC = Utils.nullValue();

	public static Block BLOCK_COGWORK_HELM = Utils.nullValue();
	public static Block BLOCK_COGWORK_LEGS = Utils.nullValue();
	public static Block BLOCK_COGWORK_CHESTPLATE = Utils.nullValue();
	public static Block FRAME_BLOCK = Utils.nullValue();
	public static Block COGWORK_HOLDER = Utils.nullValue();

	public static Block MYTHIC_STONE_FRAMED = Utils.nullValue();
	public static Block MYTHIC_STONE_DECORATED = Utils.nullValue();
	public static Block WG_MARK = Utils.nullValue();
	public static Block COMPONENTS = Utils.nullValue();
	public static Block SCHEMATIC_ALLOY = Utils.nullValue();
	public static Block SCHEMATIC_BOMB = Utils.nullValue();
	public static Block SCHEMATIC_CROSSBOW = Utils.nullValue();
	public static Block SCHEMATIC_COGWORK = Utils.nullValue();
	public static Block SCHEMATIC_FORGE = Utils.nullValue();
	public static Block SCHEMATIC_GEARS = Utils.nullValue();

	public static ItemSchematic SCHEMATIC_ALLOY_ITEM = Utils.nullValue();
	public static ItemSchematic SCHEMATIC_BOMB_ITEM = Utils.nullValue();
	public static ItemSchematic SCHEMATIC_CROSSBOW_ITEM = Utils.nullValue();
	public static ItemSchematic SCHEMATIC_COGWORK_ITEM = Utils.nullValue();
	public static ItemSchematic SCHEMATIC_FORGE_ITEM = Utils.nullValue();
	public static ItemSchematic SCHEMATIC_GEARS_ITEM = Utils.nullValue();

	public static void init() {

		COPPER_ORE = new BlockOreMF("copper_ore", 0, Rarity.POOR).setHardness(2.0F).setResistance(3.0F);
		TIN_ORE = new BlockOreMF("tin_ore", 0).setHardness(2.5F).setResistance(4.0F);
		SILVER_ORE = new BlockOreMF("silver_ore", 2).setHardness(3.0F).setResistance(5.0F);
		MYTHIC_ORE = new BlockMythicOre("mythic_ore", false).setHardness(10.0F).setResistance(100.0F);

		KAOLINITE_ORE = new BlockOreMF("kaolinite_ore", 1, Rarity.COMMON, MineFantasyItems.KAOLINITE, 1, 1, 1).setHardness(3.0F).setResistance(5.0F);
		NITRE_ORE = new BlockOreMF("nitre_ore", 2, Rarity.COMMON, MineFantasyItems.NITRE, 1, 2, 1).setHardness(3.0F).setResistance(5.0F);
		SULFUR_ORE = new BlockOreMF("sulfur_ore", 2, Rarity.COMMON, MineFantasyItems.SULFUR, 1, 4, 2).setHardness(3.0F).setResistance(2.0F);
		BORAX_ORE = new BlockOreMF("borax_ore", 2, Rarity.UNCOMMON, MineFantasyItems.FLUX_STRONG, 1, 8, 4).setHardness(3.0F).setResistance(2.0F);
		TUNGSTEN_ORE = new BlockOreMF("tungsten_ore", 3, Rarity.UNCOMMON, MineFantasyItems.ORE_TUNGSTEN, 1, 1, 4).setHardness(4.0F).setResistance(2.5F);
		CLAY_ORE = new BlockOreMF("clay_ore", 0, Rarity.COMMON, Items.CLAY_BALL, 1, 4, 1, Material.GROUND).setBlockSoundType(SoundType.GROUND).setHardness(0.5F);
		COAL_RICH_ORE = new BlockOreMF("coal_rich_ore", 2, Rarity.UNCOMMON, Items.COAL, 2, 6, 2).setHardness(5.0F).setResistance(10.0F);

		MUD_BRICK = new BasicBlockMF("mud_brick", Material.GROUND).setHardness(1.0F).setResistance(0.5F);
		MUD_BRICK_SLAB = new BlockSlab("mud_brick_slab", Material.GROUND, SoundType.STONE).setHardness(1.0F).setResistance(0.5F);
		MUD_ROAD = new BlockRoad("mud_road", Material.GROUND, SoundType.GROUND).setHardness(0.5F);
		MUD_ROAD_BLOCK = new BasicBlockMF("mud_road_block", Material.GROUND).setHardness(0.5F);
		MUD_ROAD_BLOCK_SLAB = new BlockSlab("mud_road_block_slab", Material.GROUND, SoundType.GROUND).setHardness(0.5F);

		COBBLE_BRICK = new BasicBlockMF("cobble_brick", Material.ROCK).setBlockSoundType(SoundType.STONE).setHardness(2.5F).setResistance(12.0F);
		COBBLE_BRICK_SLAB = new BlockSlab("cobble_brick_slab", Material.ROCK, SoundType.STONE).setHardness(2.5F).setResistance(12.0F);
		COBBLESTONE_ROAD = new BlockRoad("cobblestone_road", Material.ROCK, SoundType.STONE).setHardness(2.0F).setResistance(10.0F);
		COBBLESTONE_ROAD_BLOCK = new BasicBlockMF("cobblestone_road_block", Material.ROCK).setHardness(2.0F).setResistance(10.0F);
		COBBLESTONE_ROAD_BLOCK_SLAB = new BlockSlab("cobblestone_road_block_slab", Material.ROCK, SoundType.STONE).setHardness(2.0F).setResistance(10.0F);
		
		WINDOW = new BasicBlockMF("window", Material.GLASS).setBlockSoundType(SoundType.GLASS).setHardness(0.9F).setResistance(0.1F);
		FRAMED_GLASS = new BasicBlockMF("framed_glass", Material.GLASS).setBlockSoundType(SoundType.GLASS).setHardness(0.6F).setResistance(0.2F);
		FRAMED_GLASS_PANE = new BlockPaneMF("framed_glass_pane", Material.GLASS, true).setBlockSoundType(SoundType.GLASS).setHardness(0.6F).setResistance(0.1F);
		WINDOW_PANE = new BlockPaneMF("window_pane", Material.GLASS, true).setBlockSoundType(SoundType.GLASS).setHardness(0.9F).setResistance(0.2F);

		THATCH = new BasicBlockMF("thatch", Material.LEAVES).setBlockSoundType(SoundType.GROUND).setHardness(1.0F);
		THATCH_SLAB = new BlockSlab("thatch_slab", Material.LEAVES, SoundType.PLANT).setHardness(1.0F);
		THATCH_STAIR = new ConstructionBlockMF.StairsConstBlock("thatch_stairs", THATCH);

		LIMESTONE_COBBLE = new BasicBlockMF("limestone_cobble", Material.ROCK).setBlockSoundType(SoundType.STONE).setHardness(1.2F).setResistance(8F);
		LIMESTONE_COBBLE_SLAB = new BlockSlab("limestone_cobble_slab", Material.ROCK, SoundType.STONE).setHardness(1.2F).setResistance(8F);
		LIMESTONE = new BasicBlockMF("limestone", Material.ROCK, LIMESTONE_COBBLE).setBlockSoundType(SoundType.STONE).setHardness(1.2F).setResistance(8F);
		LIMESTONE_SLAB = new BlockSlab("limestone_slab", Material.ROCK, SoundType.STONE).setHardness(1.2F).setResistance(8F);
		LIMESTONE_BRICK = new BasicBlockMF("limestone_brick", Material.ROCK).setBlockSoundType(SoundType.STONE).setHardness(1.2F).setResistance(8F);
		LIMESTONE_BRICK_SLAB = new BlockSlab("limestone_brick_slab", Material.ROCK, SoundType.STONE).setHardness(1.2F).setResistance(8F);
		LIMESTONE_ROAD = new BlockRoad("limestone_road", Material.ROCK, SoundType.STONE).setHardness(1.2F).setResistance(8F);
		LIMESTONE_ROAD_BLOCK = new BasicBlockMF("limestone_road_block", Material.ROCK, SoundType.STONE).setHardness(1.2F).setResistance(8F);
		LIMESTONE_ROAD_BLOCK_SLAB = new BlockSlab("limestone_road_block_slab", Material.ROCK, SoundType.STONE).setHardness(1.2F).setResistance(8F);

		LIMESTONE_STAIRS = new ConstructionBlockMF.StairsConstBlock("limestone_stairs", LIMESTONE);
		LIMESTONE_COBBLE_STAIRS = new ConstructionBlockMF.StairsConstBlock("limestone_cobble_stairs", LIMESTONE_COBBLE);
		LIMESTONE_BRICK_STAIRS = new ConstructionBlockMF.StairsConstBlock("limestone_brick_stairs", LIMESTONE_BRICK);

		FIREBRICKS = new BasicBlockMF("firebricks", Material.ROCK).setBlockSoundType(SoundType.STONE).setHardness(5.0F).setResistance(15.0F);
		FIREBRICKS_SLAB = new BlockSlab("firebricks_slab", Material.ROCK, SoundType.STONE).setHardness(5.0F).setResistance(15.0F);
		CLAY_WALL = new BasicBlockMF("clay_wall", Material.WOOD).setBlockSoundType(SoundType.WOOD).setHardness(1.0F).setResistance(1.0F);
		CLAY_WALL_SLAB = new BlockSlab("clay_wall_slab", Material.WOOD, SoundType.WOOD).setHardness(1.0F).setResistance(1.0F);
		CLAY_WALL_CROSS = new BasicBlockMF("clay_wall_cross", Material.WOOD).setHardness(1.0F).setResistance(1.0F);
		CLAY_WALL_RIGHT_DIAGONAL = new BasicBlockMF("clay_wall_right_diagonal", Material.WOOD).setHardness(1.0F).setResistance(1.0F);
		CLAY_WALL_LEFT_DIAGONAL = new BasicBlockMF("clay_wall_left_diagonal", Material.WOOD).setHardness(1.0F).setResistance(1.0F);

		IRON_BARS = new BlockMetalBarsMF(MineFantasyMaterials.IRON);
		BRONZE_BARS = new BlockMetalBarsMF(MineFantasyMaterials.BRONZE);
		STEEL_BARS = new BlockMetalBarsMF(MineFantasyMaterials.STEEL);
		BLACK_STEEL_BARS = new BlockMetalBarsMF(MineFantasyMaterials.BLACK_STEEL);
		RED_STEEL_BARS = new BlockMetalBarsMF(MineFantasyMaterials.RED_STEEL);
		BLUE_STEEL_BARS = new BlockMetalBarsMF(MineFantasyMaterials.BLUE_STEEL);

		COPPER_STORAGE = new BlockMetalMF(MineFantasyMaterials.COPPER);
		TIN_STORAGE = new BlockMetalMF(MineFantasyMaterials.TIN);
		SILVER_STORAGE = new BlockMetalMF(MineFantasyMaterials.SILVER);
		BRONZE_STORAGE = new BlockMetalMF(MineFantasyMaterials.BRONZE);
		PIG_IRON_STORAGE = new BlockMetalMF(MineFantasyMaterials.PIG_IRON);
		STEEL_STORAGE = new BlockMetalMF(MineFantasyMaterials.STEEL);
		BLACK_STEEL_STORAGE = new BlockMetalMF(MineFantasyMaterials.BLACK_STEEL);
		RED_STEEL_STORAGE = new BlockMetalMF(MineFantasyMaterials.RED_STEEL);
		BLUE_STEEL_STORAGE = new BlockMetalMF(MineFantasyMaterials.BLUE_STEEL);
		ADAMANTIUM_STORAGE = new BlockMetalMF(MineFantasyMaterials.ADAMANTIUM);
		MITHRIL_STORAGE = new BlockMetalMF(MineFantasyMaterials.MITHRIL);
		IGNOTUMITE_STORAGE = new BlockMetalMF(MineFantasyMaterials.IGNOTUMITE);
		MITHIUM_STORAGE = new BlockMetalMF(MineFantasyMaterials.MITHIUM);
		ENDER_STORAGE = new BlockMetalMF(MineFantasyMaterials.ENDERFORGE);

		ANVIL_STONE = new BlockAnvilMF(MineFantasyMaterials.STONE);
		ANVIL_BRONZE = new BlockAnvilMF(MineFantasyMaterials.BRONZE);
		ANVIL_IRON = new BlockAnvilMF(MineFantasyMaterials.IRON);
		ANVIL_STEEL = new BlockAnvilMF(MineFantasyMaterials.STEEL);
		ANVIL_BLACK_STEEL = new BlockAnvilMF(MineFantasyMaterials.BLACK_STEEL);
		ANVIL_BLUE_STEEL = new BlockAnvilMF(MineFantasyMaterials.BLUE_STEEL);
		ANVIL_RED_STEEL = new BlockAnvilMF(MineFantasyMaterials.RED_STEEL);

		CARPENTER = new BlockCarpenter();
		KITCHEN_BENCH_GRANITE = new BlockKitchenBench("kitchen_bench_granite");
		KITCHEN_BENCH_ANDESITE = new BlockKitchenBench("kitchen_bench_andesite");
		KITCHEN_BENCH_DIORITE = new BlockKitchenBench("kitchen_bench_diorite");
		BOMB_BENCH = new BlockBombBench();
		CROSSBOW_BENCH = new BlockCrossbowBench();

		CHEESE_WHEEL = new BlockCheeseWheel("cheese_wheel", MineFantasyItems.CHEESE_SLICE);

		CAKE_VANILLA = new BlockCakeMFR("cake_vanilla", MineFantasyItems.CAKE_SLICE);
		CAKE_CARROT = new BlockCakeMFR("cake_carrot", MineFantasyItems.CARROTCAKE_SLICE);
		CAKE_CHOCOLATE = new BlockCakeMFR("cake_chocolate", MineFantasyItems.CHOCCAKE_SLICE);
		CAKE_BF = new BlockCakeMFR("cake_bf", MineFantasyItems.BFCAKE_SLICE);

		PIE_MEAT = new BlockPie("pie_meat", MineFantasyItems.MEATPIE_SLICE);

		PIE_APPLE = new BlockPie("pie_apple", MineFantasyItems.PIESLICE_APPLE);
		PIE_BERRY = new BlockPie("pie_berry", MineFantasyItems.PIESLICE_BERRY);

		PIE_SHEPARDS = new BlockPie("pie_shepards", MineFantasyItems.PIESLICE_SHEPARDS);

		BERRY_BUSH = new BlockBerryBush("berry_bush");

		BLAST_CHAMBER = new BlockBlastChamber();
		BLAST_HEATER = new BlockBlastHeater();

		CRUCIBLE_STONE = new BlockCrucible("stone", 0);
		CRUCIBLE_FIRECLAY = new BlockCrucible("fireclay", 1);
		CRUCIBLE_AUTO = new BlockCrucible("auto", 1).setAuto().setHardness(12F);
		CRUCIBLE_MYTHIC = new BlockCrucible("mythic", 2).setAuto().setBlockUnbreakable();
		CRUCIBLE_MASTER = new BlockCrucible("master", 3).setAuto().setBlockUnbreakable();

		CHIMNEY_STONE = new BlockChimney("stone", false, false, 5);
		CHIMNEY_STONE_WIDE = new BlockChimney("stone", true, false, 10);
		CHIMNEY_STONE_EXTRACTOR = new BlockChimney("stone_extractor", true, true, 15);
		CHIMNEY_PIPE = new BlockChimneyPipe(false, 10).setPipe();

		TANNER = new BlockTanningRack(0, "");
		TANNER_REFINED = new BlockTanningRack(1, "_refined");
		TANNER_METAL = new BlockEngineerTanner(2, "_metal");

		FORGE = new BlockForge("stone", 0);
		FORGE_METAL = new BlockForge("metal", 1);

		REPAIR_BASIC = new BlockRepairKit("basic", 0.25F, 0.05F, 0.2F);
		REPAIR_ADVANCED = new BlockRepairKit("advanced", 1.0F, 0.2F, 0.05F);
		REPAIR_ORNATE = new BlockRepairKit("ornate", 1.0F, 0.25F, 0.03F).setOrnate(0.5F);

		BELLOWS = new BlockBellows();

		REFINED_PLANKS = new BasicBlockMF("refined_planks", Material.WOOD).setBlockSoundType(SoundType.WOOD).setHardness(2.5F).setResistance(10F);
		REFINED_PLANKS_SLAB = new BlockSlab("refined_planks_slab", Material.WOOD, SoundType.WOOD).setHardness(2.5F).setResistance(10F);
		NAILED_PLANKS = new BasicBlockMF("nailed_planks", Material.WOOD).setBlockSoundType(SoundType.WOOD).setHardness(1.5F).setResistance(7F);
		NAILED_PLANKS_SLAB = new BlockSlab("nailed_planks_slab", Material.WOOD, SoundType.WOOD).setHardness(1.5F).setResistance(7F);
		REFINED_PLANKS_STAIR = new ConstructionBlockMF.StairsConstBlock("refined_planks_stairs", REFINED_PLANKS);
		NAILED_PLANKS_STAIR = new ConstructionBlockMF.StairsConstBlock("nailed_planks_stairs", NAILED_PLANKS);

		REINFORCED_STONE = new BasicBlockMF("reinforced_stone", Material.ROCK).setBlockSoundType(SoundType.STONE).setHardness(2.0F).setResistance(15F);
		REINFORCED_STONE_SLAB = new BlockSlab("reinforced_stone_slab", Material.ROCK, SoundType.STONE).setHardness(2.0F).setResistance(15F);
		REINFORCED_STONE_ENGRAVED_0 = new BasicBlockMF("reinforced_stone_engraved_0", Material.ROCK).setBlockSoundType(SoundType.STONE).setHardness(2.0F).setResistance(15F);
		REINFORCED_STONE_ENGRAVED_1 = new BasicBlockMF("reinforced_stone_engraved_1", Material.ROCK).setBlockSoundType(SoundType.STONE).setHardness(2.0F).setResistance(15F);
		REINFORCED_STONE_ENGRAVED_2 = new BasicBlockMF("reinforced_stone_engraved_2", Material.ROCK).setBlockSoundType(SoundType.STONE).setHardness(2.0F).setResistance(15F);
		REINFORCED_STONE_ENGRAVED_3 = new BasicBlockMF("reinforced_stone_engraved_3", Material.ROCK).setBlockSoundType(SoundType.STONE).setHardness(2.0F).setResistance(15F);

		REINFORCED_STONE_BRICKS = new BasicBlockMF("reinforced_stone_bricks", Material.ROCK).setBlockSoundType(SoundType.STONE).setHardness(2.0F).setResistance(15F);
		REINFORCED_STONE_BRICKS_SLAB = new BlockSlab("reinforced_stone_bricks_slab", Material.ROCK, SoundType.STONE).setHardness(2.0F).setResistance(15F);
		REINFORCED_STONE_BRICKS_MOSSY = new BasicBlockMF("reinforced_stone_bricks_mossy", Material.ROCK).setBlockSoundType(SoundType.STONE).setHardness(2.0F).setResistance(15F);
		REINFORCED_STONE_BRICKS_MOSSY_SLAB = new BlockSlab("reinforced_stone_bricks_mossy_slab", Material.ROCK, SoundType.STONE).setHardness(2.0F).setResistance(15F);
		REINFORCED_STONE_BRICKS_CRACKED = new BasicBlockMF("reinforced_stone_bricks_cracked", Material.ROCK).setBlockSoundType(SoundType.STONE).setHardness(2.0F).setResistance(15F);
		REINFORCED_STONE_BRICKS_CRACKED_SLAB = new BlockSlab("reinforced_stone_bricks_cracked_slab", Material.ROCK, SoundType.STONE).setHardness(2.0F).setResistance(15F);

		REINFORCED_STONE_FRAMED = new BasicBlockMF("reinforced_stone_framed", Material.ROCK).setBlockSoundType(SoundType.STONE).setHardness(2.5F).setResistance(20F);
		REINFORCED_STONE_FRAMED_IRON = new BasicBlockMF("reinforced_stone_framed_iron", Material.ROCK).setBlockSoundType(SoundType.STONE).setHardness(2.5F).setResistance(20F);

		RESEARCH = new BlockResearchBench();
		TROUGH_WOOD = new BlockTrough("trough_wood");

		BOMB_PRESS = new BlockBombPress();

		SALVAGE_BASIC = new BlockSalvage("basic", 1.0F);
		BLOOMERY = new BlockBloomery();

		LOG_YEW = new BlockLogMF("yew").setHardness(2F).setResistance(6F);
		LOG_IRONBARK = new BlockLogMF("ironbark").setHardness(3F).setResistance(8F);
		LOG_EBONY = new BlockLogMF("ebony").setHardness(5F).setResistance(10F);

		LEAVES_YEW = new BlockLeavesMF("yew", 100);
		LEAVES_IRONBARK = new BlockLeavesMF("ironbark", 40).setHardness(0.3F);
		LEAVES_EBONY = new BlockLeavesMF("ebony", 1000).setHardness(0.5F);

		SAPLING_YEW = new BlockSaplingMF("yew", LOG_YEW, LEAVES_YEW, 4F);
		SAPLING_IRONBARK = new BlockSaplingMF("ironbark", LOG_IRONBARK, LEAVES_IRONBARK, 8F);
		SAPLING_EBONY = new BlockSaplingMF("ebony", LOG_EBONY, LEAVES_EBONY, 10F);

		YEW_PLANKS = new BasicBlockMF("yew_planks", Material.WOOD).setBlockSoundType(SoundType.WOOD).setHardness(3F).setResistance(6F);
		YEW_PLANKS_SLAB = new BlockSlab("yew_planks_slab", Material.WOOD, SoundType.WOOD).setHardness(3F).setResistance(6F);
		IRONBARK_PLANKS = new BasicBlockMF("ironbark_planks", Material.WOOD).setBlockSoundType(SoundType.WOOD).setHardness(4F).setResistance(10F);
		IRONBARK_PLANKS_SLAB = new BlockSlab("ironbark_planks_slab", Material.WOOD, SoundType.WOOD).setHardness(4F).setResistance(10F);
		EBONY_PLANKS = new BasicBlockMF("ebony_planks", Material.WOOD).setBlockSoundType(SoundType.WOOD).setHardness(6F).setResistance(12F);
		EBONY_PLANKS_SLAB = new BlockSlab("ebony_planks_slab", Material.WOOD, SoundType.WOOD).setHardness(6F).setResistance(12F);

		QUERN = new BlockQuern("quern");

		MUD_BRICK_STAIRS = new ConstructionBlockMF.StairsConstBlock("mud_brick_stairs", MUD_BRICK);
		COBBLE_BRICK_STAIRS = new ConstructionBlockMF.StairsConstBlock("cobble_brick_stairs", COBBLE_BRICK);
		FIREBRICK_STAIRS = new ConstructionBlockMF.StairsConstBlock("firebrick_stairs", FIREBRICKS);
		REINFORCED_STONE_STAIRS = new ConstructionBlockMF.StairsConstBlock("reinforced_stone_stairs", REINFORCED_STONE);
		REINFORCED_STONE_BRICK_STAIRS = new ConstructionBlockMF.StairsConstBlock("reinforced_stone_brick_stairs", REINFORCED_STONE_BRICKS);

		YEW_STAIRS = new ConstructionBlockMF.StairsConstBlock("yew_stairs", YEW_PLANKS);
		IRONBARK_STAIRS = new ConstructionBlockMF.StairsConstBlock("ironbark_stairs", IRONBARK_PLANKS);
		EBONY_STAIRS = new ConstructionBlockMF.StairsConstBlock("ebony_stairs", EBONY_PLANKS);

		FIREPIT = new BlockFirepit();
		STOVE = new BlockRoast(false);
		OVEN = new BlockRoast(true);

		FURNACE_HEATER = new BlockBigFurnace("furnace_heater", true, -1);
		FURNACE_STONE = new BlockBigFurnace("furnace_stone", false, 0);

		TOOL_RACK_WOOD = new BlockRack("rack_wood");

		FOOD_BOX_BASIC = new BlockAmmoBox("food_box_basic", (byte) 0);
		AMMO_BOX_BASIC = new BlockAmmoBox("ammo_box_basic", (byte) 1);
		CRATE_BASIC = new BlockAmmoBox("crate_basic", (byte) 2);

		BLOCK_COGWORK_HELM = new BlockCogwork("block_cogwork_helm", false);
		BLOCK_COGWORK_LEGS = new BlockCogwork("block_cogwork_legs", false);
		BLOCK_COGWORK_CHESTPLATE = new BlockCogwork("block_cogwork_chestplate", true);
		FRAME_BLOCK = new BlockFrame("frame_block");
		COGWORK_HOLDER = new BlockFrameHolder("cogwork_holder", FRAME_BLOCK);

		MYTHIC_STONE_FRAMED = new BlockMythicDecor("mythic_stone_framed");
		MYTHIC_STONE_DECORATED = new BlockMythicDecor("mythic_stone_decorated");
		WG_MARK = new BlockWorldGenMarker("world_gen_flag");
		COMPONENTS = new BlockComponent();
		SCHEMATIC_ALLOY = new BlockSchematic("schematic_alloy");
		SCHEMATIC_BOMB = new BlockSchematic("schematic_bomb");
		SCHEMATIC_CROSSBOW = new BlockSchematic("schematic_crossbow");
		SCHEMATIC_FORGE = new BlockSchematic("schematic_forge");
		SCHEMATIC_COGWORK = new BlockSchematic("schematic_cogwork");
		SCHEMATIC_GEARS = new BlockSchematic("schematic_gears");

		SCHEMATIC_ALLOY_ITEM = new ItemSchematic(MineFantasyBlocks.SCHEMATIC_BOMB,"bomb_obsidian", "mine_obsidian");
		SCHEMATIC_BOMB_ITEM = new ItemSchematic(MineFantasyBlocks.SCHEMATIC_CROSSBOW,"crossbow_shaft_advanced", "crossbow_head_advanced");
		SCHEMATIC_CROSSBOW_ITEM = new ItemSchematic(MineFantasyBlocks.SCHEMATIC_FORGE, "advanced_forge", "advanced_crucible");
		SCHEMATIC_FORGE_ITEM = new ItemSchematic(MineFantasyBlocks.SCHEMATIC_GEARS,"cogwork_armour");
		SCHEMATIC_COGWORK_ITEM = new ItemSchematic(MineFantasyBlocks.SCHEMATIC_COGWORK, "cogwork_armour");
		SCHEMATIC_GEARS_ITEM = new ItemSchematic(MineFantasyBlocks.SCHEMATIC_ALLOY,"composite_alloy");
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();

		registry.register(COPPER_ORE);
		registry.register(TIN_ORE);
		registry.register(SILVER_ORE);
		registry.register(MYTHIC_ORE);

		registry.register(KAOLINITE_ORE);
		registry.register(NITRE_ORE);
		registry.register(SULFUR_ORE);
		registry.register(BORAX_ORE);
		registry.register(TUNGSTEN_ORE);
		registry.register(CLAY_ORE);
		registry.register(COAL_RICH_ORE);

		registry.register(MUD_BRICK);
		registry.register(MUD_BRICK_SLAB);
		registry.register(MUD_ROAD);
		registry.register(MUD_ROAD_BLOCK);
		registry.register(MUD_ROAD_BLOCK_SLAB);

		registry.register(COBBLE_BRICK);
		registry.register(COBBLE_BRICK_SLAB);
		registry.register(COBBLESTONE_ROAD);
		registry.register(COBBLESTONE_ROAD_BLOCK);
		registry.register(COBBLESTONE_ROAD_BLOCK_SLAB);

		registry.register(WINDOW);
		registry.register(FRAMED_GLASS);
		registry.register(FRAMED_GLASS_PANE);
		registry.register(WINDOW_PANE);

		registry.register(THATCH);
		registry.register(THATCH_SLAB);
		registry.register(THATCH_STAIR);

		registry.register(LIMESTONE);
		registry.register(LIMESTONE_SLAB);
		registry.register(LIMESTONE_COBBLE);
		registry.register(LIMESTONE_COBBLE_SLAB);
		registry.register(LIMESTONE_BRICK);
		registry.register(LIMESTONE_BRICK_SLAB);
		registry.register(LIMESTONE_ROAD);
		registry.register(LIMESTONE_ROAD_BLOCK);
		registry.register(LIMESTONE_ROAD_BLOCK_SLAB);

		registry.register(LIMESTONE_STAIRS);
		registry.register(LIMESTONE_COBBLE_STAIRS);
		registry.register(LIMESTONE_BRICK_STAIRS);

		registry.register(FIREBRICKS);
		registry.register(FIREBRICKS_SLAB);
		registry.register(CLAY_WALL);
		registry.register(CLAY_WALL_SLAB);
		registry.register(CLAY_WALL_CROSS);
		registry.register(CLAY_WALL_RIGHT_DIAGONAL);
		registry.register(CLAY_WALL_LEFT_DIAGONAL);

		registry.register(IRON_BARS);
		registry.register(BRONZE_BARS);
		registry.register(STEEL_BARS);
		registry.register(BLACK_STEEL_BARS);
		registry.register(RED_STEEL_BARS);
		registry.register(BLUE_STEEL_BARS);

		registry.register(COPPER_STORAGE);
		registry.register(TIN_STORAGE);
		registry.register(SILVER_STORAGE);
		registry.register(BRONZE_STORAGE);
		registry.register(PIG_IRON_STORAGE);
		registry.register(STEEL_STORAGE);
		registry.register(BLACK_STEEL_STORAGE);
		registry.register(RED_STEEL_STORAGE);
		registry.register(BLUE_STEEL_STORAGE);
		registry.register(ADAMANTIUM_STORAGE);
		registry.register(MITHRIL_STORAGE);
		registry.register(IGNOTUMITE_STORAGE);
		registry.register(MITHIUM_STORAGE);
		registry.register(ENDER_STORAGE);

		registry.register(CHEESE_WHEEL);

		registry.register(CAKE_VANILLA);
		registry.register(CAKE_CARROT);
		registry.register(CAKE_CHOCOLATE);
		registry.register(CAKE_BF);

		registry.register(PIE_MEAT);

		registry.register(PIE_APPLE);
		registry.register(PIE_BERRY);

		registry.register(PIE_SHEPARDS);

		registry.register(REPAIR_BASIC);
		registry.register(REPAIR_ADVANCED);
		registry.register(REPAIR_ORNATE);

		registry.register(REFINED_PLANKS);
		registry.register(REFINED_PLANKS_SLAB);
		registry.register(NAILED_PLANKS);
		registry.register(NAILED_PLANKS_SLAB);
		registry.register(REFINED_PLANKS_STAIR);
		registry.register(NAILED_PLANKS_STAIR);

		registry.register(REINFORCED_STONE);
		registry.register(REINFORCED_STONE_SLAB);
		registry.register(REINFORCED_STONE_ENGRAVED_0);
		registry.register(REINFORCED_STONE_ENGRAVED_1);
		registry.register(REINFORCED_STONE_ENGRAVED_2);
		registry.register(REINFORCED_STONE_ENGRAVED_3);

		registry.register(REINFORCED_STONE_BRICKS);
		registry.register(REINFORCED_STONE_BRICKS_SLAB);
		registry.register(REINFORCED_STONE_BRICKS_MOSSY);
		registry.register(REINFORCED_STONE_BRICKS_MOSSY_SLAB);
		registry.register(REINFORCED_STONE_BRICKS_CRACKED);
		registry.register(REINFORCED_STONE_BRICKS_CRACKED_SLAB);

		registry.register(REINFORCED_STONE_FRAMED);
		registry.register(REINFORCED_STONE_FRAMED_IRON);

		registry.register(LOG_YEW);
		registry.register(LOG_IRONBARK);
		registry.register(LOG_EBONY);

		registry.register(LEAVES_YEW);
		registry.register(LEAVES_IRONBARK);
		registry.register(LEAVES_EBONY);

		registry.register(SAPLING_YEW);
		registry.register(SAPLING_IRONBARK);
		registry.register(SAPLING_EBONY);

		registry.register(YEW_PLANKS);
		registry.register(YEW_PLANKS_SLAB);
		registry.register(IRONBARK_PLANKS);
		registry.register(IRONBARK_PLANKS_SLAB);
		registry.register(EBONY_PLANKS);
		registry.register(EBONY_PLANKS_SLAB);

		registry.register(MUD_BRICK_STAIRS);
		registry.register(COBBLE_BRICK_STAIRS);
		registry.register(FIREBRICK_STAIRS);
		registry.register(REINFORCED_STONE_STAIRS);
		registry.register(REINFORCED_STONE_BRICK_STAIRS);

		registry.register(YEW_STAIRS);
		registry.register(IRONBARK_STAIRS);
		registry.register(EBONY_STAIRS);

		registry.register(BLOCK_COGWORK_HELM);
		registry.register(BLOCK_COGWORK_LEGS);
		registry.register(BLOCK_COGWORK_CHESTPLATE);
		registry.register(FRAME_BLOCK);
		registry.register(COGWORK_HOLDER);

		registry.register(MYTHIC_STONE_FRAMED);
		registry.register(MYTHIC_STONE_DECORATED);
		registry.register(SCHEMATIC_ALLOY);
		registry.register(SCHEMATIC_BOMB);
		registry.register(SCHEMATIC_CROSSBOW);
		registry.register(SCHEMATIC_FORGE);
		registry.register(SCHEMATIC_COGWORK);
		registry.register(SCHEMATIC_GEARS);

		registry.register(SALVAGE_BASIC);

		registry.register(OVEN);

		//Tile Entities

		registry.register(ANVIL_STONE);
		registry.register(ANVIL_BRONZE);
		registry.register(ANVIL_IRON);
		registry.register(ANVIL_STEEL);
		registry.register(ANVIL_BLACK_STEEL);
		registry.register(ANVIL_BLUE_STEEL);
		registry.register(ANVIL_RED_STEEL);
		registerTile(TileEntityAnvil.class, "anvil_tile");
		registry.register(CARPENTER);
		registerTile(TileEntityCarpenter.class, "carpenter_tile");
		registry.register(KITCHEN_BENCH_GRANITE);
		registry.register(KITCHEN_BENCH_ANDESITE);
		registry.register(KITCHEN_BENCH_DIORITE);
		registerTile(TileEntityKitchenBench.class, "kitchen_bench_tile");
		registry.register(BOMB_BENCH);
		registerTile(TileEntityBombBench.class, "bomb_bench_tile");
		registry.register(CROSSBOW_BENCH);
		registerTile(TileEntityCrossbowBench.class, "crossbow_bench_tile");

		registry.register(BERRY_BUSH);

		registry.register(BLAST_CHAMBER);
		registerTile(TileEntityBlastChamber.class, "blast_furnace_chamber_tile");
		registry.register(BLAST_HEATER);
		registerTile(TileEntityBlastHeater.class, "blast_furnace_heater_tile");

		registry.register(CRUCIBLE_STONE);
		registry.register(CRUCIBLE_FIRECLAY);
		registry.register(CRUCIBLE_AUTO);
		registry.register(CRUCIBLE_MYTHIC);
		registry.register(CRUCIBLE_MASTER);
		registerTile(TileEntityCrucible.class, "crucible_tile");

		registry.register(CHIMNEY_STONE);
		registry.register(CHIMNEY_STONE_WIDE);
		registry.register(CHIMNEY_STONE_EXTRACTOR);
		registry.register(CHIMNEY_PIPE);
		registerTile(TileEntityChimney.class, "chimney_tile");

		registry.register(TANNER);
		registry.register(TANNER_REFINED);
		registry.register(TANNER_METAL);
		registerTile(TileEntityTanningRack.class, "tanning_rack_tile");

		registry.register(FORGE);
		registry.register(FORGE_METAL);
		registerTile(TileEntityForge.class, "forge_tile");

		registry.register(BELLOWS);
		registerTile(TileEntityBellows.class, "bellows_tile");

		registry.register(RESEARCH);
		registerTile(TileEntityResearchBench.class, "research_table_tile");
		registry.register(BOMB_PRESS);
		registerTile(TileEntityBombPress.class, "bomb_press_tile");

		registry.register(BLOOMERY);
		registerTile(TileEntityBloomery.class, "bloomery_tile");

		registry.register(QUERN);
		registerTile(TileEntityQuern.class, "quern_tile");

		registry.register(FIREPIT);
		registerTile(TileEntityFirepit.class, "firepit_tile");
		registry.register(STOVE);
		registerTile(TileEntityRoast.class, "roast_tile");

		registry.register(FURNACE_HEATER);
		registry.register(FURNACE_STONE);
		registerTile(TileEntityBigFurnace.class, "big_furnace_tile");

		registry.register(TOOL_RACK_WOOD);
		registerTile(TileEntityRack.class, "tool_rack_tile");
		registry.register(TROUGH_WOOD);
		registerTile(TileEntityTrough.class, "trough_tile");
		registry.register(FOOD_BOX_BASIC);
		registry.register(AMMO_BOX_BASIC);
		registry.register(CRATE_BASIC);
		registerTile(TileEntityAmmoBox.class, "ammo_box_tile");

		registry.register(WG_MARK);
		registerTile(TileEntityWorldGenMarker.class, "world_gen_marker_tile");
		registry.register(COMPONENTS);
		registerTile(TileEntityComponent.class, "component_tile");

	}

	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		registry.register(new ItemBlockOreMFR(COPPER_ORE));
		registry.register(new ItemBlockOreMFR(TIN_ORE));
		registry.register(new ItemBlockOreMFR(SILVER_ORE));
		registry.register(new ItemBlockOreMFR(MYTHIC_ORE));

		registry.register(new ItemBlockOreMFR(KAOLINITE_ORE));
		registry.register(new ItemBlockOreMFR(NITRE_ORE));
		registry.register(new ItemBlockOreMFR(SULFUR_ORE));
		registry.register(new ItemBlockOreMFR(BORAX_ORE));
		registry.register(new ItemBlockOreMFR(TUNGSTEN_ORE));
		registry.register(new ItemBlockOreMFR(CLAY_ORE));
		registry.register(new ItemBlockOreMFR(COAL_RICH_ORE));

		registry.register(new ItemBlockBase(MUD_BRICK));
		registry.register(new ItemBlockSlab((BlockSlab) MUD_BRICK_SLAB));
		registry.register(new ItemBlockBase(MUD_ROAD));
		registry.register(new ItemBlockBase(MUD_ROAD_BLOCK));
		registry.register(new ItemBlockSlab((BlockSlab) MUD_ROAD_BLOCK_SLAB));

		registry.register(new ItemBlockBase(COBBLE_BRICK));
		registry.register(new ItemBlockSlab((BlockSlab) COBBLE_BRICK_SLAB));
		registry.register(new ItemBlockBase(COBBLESTONE_ROAD));
		registry.register(new ItemBlockBase(COBBLESTONE_ROAD_BLOCK));
		registry.register(new ItemBlockSlab((BlockSlab) COBBLESTONE_ROAD_BLOCK_SLAB));

		registry.register(new ItemBlockBase(WINDOW));
		registry.register(new ItemBlockBase(FRAMED_GLASS));
		registry.register(new ItemBlockBase(FRAMED_GLASS_PANE));
		registry.register(new ItemBlockBase(WINDOW_PANE));

		registry.register(new ItemBlockBase(THATCH));
		registry.register(new ItemBlockSlab((BlockSlab) THATCH_SLAB));
		registry.register(new ItemBlockBase(THATCH_STAIR));

		registry.register(new ItemBlockBase(LIMESTONE_COBBLE));
		registry.register(new ItemBlockSlab((BlockSlab) LIMESTONE_COBBLE_SLAB));
		registry.register(new ItemBlockBase(LIMESTONE));

		registry.register(new ItemBlockBase(LIMESTONE_BRICK));
		registry.register(new ItemBlockSlab((BlockSlab) LIMESTONE_BRICK_SLAB));
		registry.register(new ItemBlockBase(LIMESTONE_ROAD));
		registry.register(new ItemBlockBase(LIMESTONE_ROAD_BLOCK));
		registry.register(new ItemBlockSlab((BlockSlab) LIMESTONE_ROAD_BLOCK_SLAB));

		registry.register(new ItemBlockBase(LIMESTONE_STAIRS));
		registry.register(new ItemBlockBase(LIMESTONE_COBBLE_STAIRS));
		registry.register(new ItemBlockBase(LIMESTONE_BRICK_STAIRS));

		registry.register(new ItemBlockBase(FIREBRICKS));
		registry.register(new ItemBlockSlab((BlockSlab) FIREBRICKS_SLAB));
		registry.register(new ItemBlockBase(CLAY_WALL));
		registry.register(new ItemBlockSlab((BlockSlab) CLAY_WALL_SLAB));
		registry.register(new ItemBlockBase(CLAY_WALL_CROSS));
		registry.register(new ItemBlockBase(CLAY_WALL_RIGHT_DIAGONAL));
		registry.register(new ItemBlockBase(CLAY_WALL_LEFT_DIAGONAL));

		registry.register(new ItemBlockBase(IRON_BARS));
		registry.register(new ItemBlockBase(BRONZE_BARS));
		registry.register(new ItemBlockBase(STEEL_BARS));
		registry.register(new ItemBlockBase(BLACK_STEEL_BARS));
		registry.register(new ItemBlockBase(RED_STEEL_BARS));
		registry.register(new ItemBlockBase(BLUE_STEEL_BARS));

		registry.register(new ItemBlockBase(COPPER_STORAGE));
		registry.register(new ItemBlockBase(TIN_STORAGE));
		registry.register(new ItemBlockBase(SILVER_STORAGE));
		registry.register(new ItemBlockBase(BRONZE_STORAGE));
		registry.register(new ItemBlockBase(PIG_IRON_STORAGE));
		registry.register(new ItemBlockBase(STEEL_STORAGE));
		registry.register(new ItemBlockBase(BLACK_STEEL_STORAGE));
		registry.register(new ItemBlockBase(RED_STEEL_STORAGE));
		registry.register(new ItemBlockBase(BLUE_STEEL_STORAGE));
		registry.register(new ItemBlockBase(ADAMANTIUM_STORAGE));
		registry.register(new ItemBlockBase(MITHRIL_STORAGE));
		registry.register(new ItemBlockBase(IGNOTUMITE_STORAGE));
		registry.register(new ItemBlockBase(MITHIUM_STORAGE));
		registry.register(new ItemBlockBase(ENDER_STORAGE));

		registry.register(new ItemBlockAnvil(ANVIL_STONE));
		registry.register(new ItemBlockAnvil(ANVIL_BRONZE));
		registry.register(new ItemBlockAnvil(ANVIL_IRON));
		registry.register(new ItemBlockAnvil(ANVIL_STEEL));
		registry.register(new ItemBlockAnvil(ANVIL_BLACK_STEEL));
		registry.register(new ItemBlockAnvil(ANVIL_BLUE_STEEL));
		registry.register(new ItemBlockAnvil(ANVIL_RED_STEEL));

		registry.register(new ItemBlockBase(CARPENTER));
		registry.register(new ItemBlockBase(KITCHEN_BENCH_GRANITE));
		registry.register(new ItemBlockBase(KITCHEN_BENCH_ANDESITE));
		registry.register(new ItemBlockBase(KITCHEN_BENCH_DIORITE));
		registry.register(new ItemBlockBase(BOMB_BENCH));
		registry.register(new ItemBlockBase(CROSSBOW_BENCH));

		registry.register(new ItemBlockCake(CHEESE_WHEEL));
		registry.register(new ItemBlockCake(CAKE_VANILLA));
		registry.register(new ItemBlockCake(CAKE_CARROT));
		registry.register(new ItemBlockCake(CAKE_CHOCOLATE));
		registry.register(new ItemBlockCake(CAKE_BF));

		registry.register(new ItemBlockBase(PIE_MEAT));

		registry.register(new ItemBlockBase(PIE_APPLE));
		registry.register(new ItemBlockBase(PIE_BERRY));

		registry.register(new ItemBlockBase(PIE_SHEPARDS));

		registry.register(new ItemBlockBerries(BERRY_BUSH));

		registry.register(new ItemBlockBase(BLAST_CHAMBER));
		registry.register(new ItemBlockBase(BLAST_HEATER));

		registry.register(new ItemBlockBase(CRUCIBLE_STONE));
		registry.register(new ItemBlockBase(CRUCIBLE_FIRECLAY));
		registry.register(new ItemBlockBase(CRUCIBLE_AUTO));
		registry.register(new ItemBlockBase(CRUCIBLE_MYTHIC));
		registry.register(new ItemBlockBase(CRUCIBLE_MASTER));

		registry.register(new ItemBlockBase(CHIMNEY_STONE));
		registry.register(new ItemBlockBase(CHIMNEY_STONE_WIDE));
		registry.register(new ItemBlockBase(CHIMNEY_STONE_EXTRACTOR));
		registry.register(new ItemBlockBase(CHIMNEY_PIPE));

		registry.register(new ItemBlockBase(TANNER));
		registry.register(new ItemBlockBase(TANNER_REFINED));
		registry.register(new ItemBlockSpecialRender(TANNER_METAL));

		registry.register(new ItemBlockBase(FORGE));
		registry.register(new ItemBlockBase(FORGE_METAL));

		registry.register(new ItemBlockRepairKit(REPAIR_BASIC));
		registry.register(new ItemBlockRepairKit(REPAIR_ADVANCED));
		registry.register(new ItemBlockRepairKit(REPAIR_ORNATE));

		registry.register(new ItemBlockSpecialRender(BELLOWS));

		registry.register(new ItemBlockBase(REFINED_PLANKS));
		registry.register(new ItemBlockSlab((BlockSlab) REFINED_PLANKS_SLAB));
		registry.register(new ItemBlockBase(NAILED_PLANKS));
		registry.register(new ItemBlockSlab((BlockSlab) NAILED_PLANKS_SLAB));
		registry.register(new ItemBlockBase(REFINED_PLANKS_STAIR));
		registry.register(new ItemBlockBase(NAILED_PLANKS_STAIR));

		registry.register(new ItemBlockBase(REINFORCED_STONE));
		registry.register(new ItemBlockSlab((BlockSlab) REINFORCED_STONE_SLAB));
		registry.register(new ItemBlockBase(REINFORCED_STONE_ENGRAVED_0));
		registry.register(new ItemBlockBase(REINFORCED_STONE_ENGRAVED_1));
		registry.register(new ItemBlockBase(REINFORCED_STONE_ENGRAVED_2));
		registry.register(new ItemBlockBase(REINFORCED_STONE_ENGRAVED_3));

		registry.register(new ItemBlockBase(REINFORCED_STONE_BRICKS));
		registry.register(new ItemBlockSlab((BlockSlab) REINFORCED_STONE_BRICKS_SLAB));
		registry.register(new ItemBlockBase(REINFORCED_STONE_BRICKS_MOSSY));
		registry.register(new ItemBlockSlab((BlockSlab) REINFORCED_STONE_BRICKS_MOSSY_SLAB));
		registry.register(new ItemBlockBase(REINFORCED_STONE_BRICKS_CRACKED));
		registry.register(new ItemBlockSlab((BlockSlab) REINFORCED_STONE_BRICKS_CRACKED_SLAB));
		registry.register(new ItemBlockBase(REINFORCED_STONE_FRAMED));
		registry.register(new ItemBlockBase(REINFORCED_STONE_FRAMED_IRON));

		registry.register(new ItemBlockBase(RESEARCH));
		registry.register(new ItemBlockTrough(TROUGH_WOOD));

		registry.register(new ItemBlockSpecialRender(BOMB_PRESS));

		registry.register(new ItemBlockSalvage(SALVAGE_BASIC));

		registry.register(new ItemBlockBase(BLOOMERY));
		registry.register(new ItemBlockBase(LOG_YEW));
		registry.register(new ItemBlockBase(LOG_IRONBARK));
		registry.register(new ItemBlockBase(LOG_EBONY));

		registry.register(new ItemBlockBase(LEAVES_YEW));
		registry.register(new ItemBlockBase(LEAVES_IRONBARK));
		registry.register(new ItemBlockBase(LEAVES_EBONY));

		registry.register(new ItemBlockBase(SAPLING_YEW));
		registry.register(new ItemBlockBase(SAPLING_IRONBARK));
		registry.register(new ItemBlockBase(SAPLING_EBONY));

		registry.register(new ItemBlockBase(YEW_PLANKS));
		registry.register(new ItemBlockSlab((BlockSlab) YEW_PLANKS_SLAB));
		registry.register(new ItemBlockBase(IRONBARK_PLANKS));
		registry.register(new ItemBlockSlab((BlockSlab) IRONBARK_PLANKS_SLAB));
		registry.register(new ItemBlockBase(EBONY_PLANKS));
		registry.register(new ItemBlockSlab((BlockSlab) EBONY_PLANKS_SLAB));

		registry.register(new ItemBlockSpecialRender(QUERN));

		registry.register(new ItemBlockBase(MUD_BRICK_STAIRS));
		registry.register(new ItemBlockBase(COBBLE_BRICK_STAIRS));
		registry.register(new ItemBlockBase(FIREBRICK_STAIRS));
		registry.register(new ItemBlockBase(REINFORCED_STONE_STAIRS));
		registry.register(new ItemBlockBase(REINFORCED_STONE_BRICK_STAIRS));

		registry.register(new ItemBlockBase(YEW_STAIRS));
		registry.register(new ItemBlockBase(IRONBARK_STAIRS));
		registry.register(new ItemBlockBase(EBONY_STAIRS));

		registry.register(new ItemBlockBase(FIREPIT));
		registry.register(new ItemBlockBase(STOVE));
		registry.register(new ItemBlockBase(OVEN));

		registry.register(new ItemBlockSpecialRender(FURNACE_HEATER));
		registry.register(new ItemBlockSpecialRender(FURNACE_STONE));

		registry.register(new ItemBlockToolRack(TOOL_RACK_WOOD));

		registry.register(new ItemBlockAmmoBox(FOOD_BOX_BASIC));
		registry.register(new ItemBlockAmmoBox(AMMO_BOX_BASIC));
		registry.register(new ItemBlockAmmoBox(CRATE_BASIC));

		registry.register(new ItemBlockBase(BLOCK_COGWORK_HELM));
		registry.register(new ItemBlockBase(BLOCK_COGWORK_LEGS));
		registry.register(new ItemBlockBase(BLOCK_COGWORK_CHESTPLATE));
		registry.register(new ItemBlockBase(FRAME_BLOCK));
		registry.register(new ItemBlockBase(COGWORK_HOLDER));

		registry.register(new ItemBlockBase(MYTHIC_STONE_FRAMED));
		registry.register(new ItemBlockBase(MYTHIC_STONE_DECORATED));
		registry.register(new ItemBlockBase(WG_MARK));
		registry.register(new ItemBlockBase(COMPONENTS));

		registry.register(SCHEMATIC_ALLOY_ITEM);
		registry.register(SCHEMATIC_BOMB_ITEM);
		registry.register(SCHEMATIC_CROSSBOW_ITEM);
		registry.register(SCHEMATIC_FORGE_ITEM);
		registry.register(SCHEMATIC_COGWORK_ITEM);
		registry.register(SCHEMATIC_GEARS_ITEM);
	}

	public static void load() {
		// 5:20 default planks
		Blocks.FIRE.setFireInfo(REFINED_PLANKS, 3, 10);
		Blocks.FIRE.setFireInfo(LOG_IRONBARK, 2, 8);// IRONBARK: Logs are resistant to fire but raw planks are not
		Blocks.FIRE.setFireInfo(IRONBARK_PLANKS, 5, 30);
		Blocks.FIRE.setFireInfo(LOG_EBONY, 3, 10);// EBONY: Resistant
		Blocks.FIRE.setFireInfo(EBONY_PLANKS, 3, 10);
	}

	private static void registerTile(Class<? extends TileEntity> teClass, String teId) {
		GameRegistry.registerTileEntity(teClass, new ResourceLocation(MineFantasyReforged.MOD_ID, teId));
	}
}


