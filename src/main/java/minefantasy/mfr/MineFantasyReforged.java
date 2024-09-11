package minefantasy.mfr;

import codechicken.lib.CodeChickenLib;
import minefantasy.mfr.api.MineFantasyReforgedAPI;
import minefantasy.mfr.commands.CommandMFR;
import minefantasy.mfr.config.ConfigArmour;
import minefantasy.mfr.config.ConfigClient;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.config.ConfigFarming;
import minefantasy.mfr.config.ConfigHardcore;
import minefantasy.mfr.config.ConfigIntegration;
import minefantasy.mfr.config.ConfigItemRegistry;
import minefantasy.mfr.config.ConfigMobs;
import minefantasy.mfr.config.ConfigSpecials;
import minefantasy.mfr.config.ConfigStamina;
import minefantasy.mfr.config.ConfigTools;
import minefantasy.mfr.config.ConfigWeapon;
import minefantasy.mfr.config.ConfigWorldGen;
import minefantasy.mfr.data.PlayerData;
import minefantasy.mfr.init.LeatherArmourListMFR;
import minefantasy.mfr.init.MineFantasyArmorCustomEntries;
import minefantasy.mfr.init.MineFantasyBlocks;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.init.MineFantasyKnowledgeList;
import minefantasy.mfr.init.MineFantasyLoot;
import minefantasy.mfr.init.MineFantasyMaterials;
import minefantasy.mfr.init.MineFantasyOreDict;
import minefantasy.mfr.material.MetalMaterial;
import minefantasy.mfr.network.NetworkHandler;
import minefantasy.mfr.proxy.CommonProxy;
import minefantasy.mfr.recipe.CraftingManagerAlloy;
import minefantasy.mfr.recipe.CraftingManagerAnvil;
import minefantasy.mfr.recipe.CraftingManagerBigFurnace;
import minefantasy.mfr.recipe.CraftingManagerBlastFurnace;
import minefantasy.mfr.recipe.CraftingManagerBloomery;
import minefantasy.mfr.recipe.CraftingManagerCarpenter;
import minefantasy.mfr.recipe.CraftingManagerKitchenBench;
import minefantasy.mfr.recipe.CraftingManagerQuern;
import minefantasy.mfr.recipe.CraftingManagerRoast;
import minefantasy.mfr.recipe.CraftingManagerSalvage;
import minefantasy.mfr.recipe.CraftingManagerSpecial;
import minefantasy.mfr.recipe.CraftingManagerTanner;
import minefantasy.mfr.recipe.CraftingManagerTransformation;
import minefantasy.mfr.recipe.RecipeRemover;
import minefantasy.mfr.recipe.ingredients.IngredientCount;
import minefantasy.mfr.recipe.ingredients.IngredientOreCount;
import minefantasy.mfr.registry.FirepitFuelRegistry;
import minefantasy.mfr.registry.ForgeFuelRegistry;
import minefantasy.mfr.registry.MetalMaterialRegistry;
import minefantasy.mfr.registry.WoodMaterialRegistry;
import minefantasy.mfr.world.gen.feature.WorldGenBiological;
import minefantasy.mfr.world.gen.feature.WorldGenGeological;
import minefantasy.mfr.world.gen.structure.WorldGenStructure;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = MineFantasyReforged.MOD_ID, name = MineFantasyReforged.NAME, version = "@VERSION@", dependencies = "required:forge@[0.000.000.001,);" + CodeChickenLib.MOD_VERSION_DEP + "required-after:mixinbooter;")
public class MineFantasyReforged {
	public static final boolean shouldRemap = false;//DO NOT COMMIT AS FALSE
	public static final String MOD_ID = "minefantasyreforged";
	public static final String NAME = "MineFantasy Reforged";

	@SidedProxy(clientSide = "minefantasy.mfr.proxy.ClientProxy", serverSide = "minefantasy.mfr.proxy.ServerProxy")
	public static CommonProxy PROXY;

	@Mod.Instance
	public static MineFantasyReforged INSTANCE;

	public static final CraftingManagerAnvil CRAFTING_MANAGER_ANVIL = new CraftingManagerAnvil();
	public static final CraftingManagerCarpenter CRAFTING_MANAGER_CARPENTER = new CraftingManagerCarpenter();
	public static final CraftingManagerBigFurnace CRAFTING_MANAGER_BIG_FURNACE = new CraftingManagerBigFurnace();
	public static final CraftingManagerAlloy CRAFTING_MANAGER_ALLOY = new CraftingManagerAlloy();
	public static final CraftingManagerBloomery CRAFTING_MANAGER_BLOOMERY = new CraftingManagerBloomery();
	public static final CraftingManagerBlastFurnace CRAFTING_MANAGER_BLAST_FURNACE = new CraftingManagerBlastFurnace();
	public static final CraftingManagerQuern CRAFTING_MANAGER_QUERN = new CraftingManagerQuern();
	public static final CraftingManagerTanner CRAFTING_MANAGER_TANNER = new CraftingManagerTanner();
	public static final CraftingManagerRoast CRAFTING_MANAGER_ROAST = new CraftingManagerRoast();
	public static final CraftingManagerKitchenBench CRAFTING_MANAGER_KITCHEN_BENCH = new CraftingManagerKitchenBench();
	public static final CraftingManagerSalvage CRAFTING_MANAGER_SALVAGE = new CraftingManagerSalvage();
	public static final CraftingManagerTransformation CRAFTING_MANAGER_TRANSFORMATION = new CraftingManagerTransformation();
	public static final CraftingManagerSpecial CRAFTING_MANAGER_SPECIAL = new CraftingManagerSpecial();

	@SideOnly(Side.CLIENT)
	private static ConfigClient configClient;
	private static ConfigArmour configArmour;
	private static ConfigSpecials configSpecials;
	private static ConfigHardcore configHardcore;
	private static ConfigIntegration configIntegration;
	private static ConfigTools configTools;
	private static ConfigWeapon configWeapon;
	private static ConfigStamina configStamina;
	private static ConfigItemRegistry configItemRegistry;
	private static ConfigFarming configFarming;
	private static ConfigWorldGen configWorldGen;
	public static ConfigCrafting configCrafting;
	private static ConfigMobs configMobs;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);

	public static boolean isDebug() {
		return ConfigSpecials.debug.equals("AU32-Db42-Acf6-Ggh9-9E8d");
	}

	/**
	 * Determines if a player name is that of a MF modder
	 */
	public static boolean isNameModder(String name) {
		return name.equals("Galactic_Hiker") || name.equals("tim4200") || name.equals("Sirse");
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent preEvent) {
		NetworkHandler.INSTANCE.registerNetwork();

		if (preEvent.getSide() == Side.CLIENT) {
			configClient = new ConfigClient("Client");
		}
		configArmour = new ConfigArmour("Armours");
		configSpecials = new ConfigSpecials("Specials");
		configHardcore = new ConfigHardcore("Hardcore");
		configIntegration = new ConfigIntegration("Integration");
		configTools = new ConfigTools("Tools");
		configWeapon = new ConfigWeapon("Weapons");
		configStamina = new ConfigStamina("Stamina_System");
		configItemRegistry = new ConfigItemRegistry("Item_Registry");
		configFarming = new ConfigFarming("Farming");
		configWorldGen = new ConfigWorldGen("WorldGen");
		configCrafting = new ConfigCrafting("Crafting");
		configMobs = new ConfigMobs("Mobs");

		PlayerData.register();
		MineFantasyItems.initEnumActions();

		MineFantasyReforgedAPI.isInDebugMode = isDebug();
		MineFantasyReforged.LOG.info("API Debug mode updated: " + MineFantasyReforgedAPI.isInDebugMode);

		MineFantasyMaterials.initBaseMaterials();
		WoodMaterialRegistry.INSTANCE.preInit();
		MetalMaterialRegistry.INSTANCE.preInit();
		MineFantasyMaterials.initLeatherMaterials();

		MineFantasyLoot.load();

		MineFantasyItems.initComponent();
		MineFantasyItems.initTool();
		MineFantasyItems.initCustomTool();
		MineFantasyItems.initCustomArmor();
		LeatherArmourListMFR.init();
		MineFantasyItems.initFood();
		MineFantasyItems.initDragonforged();
		MineFantasyItems.initOrnate();

		MineFantasyBlocks.init();
		MineFantasyBlocks.load();
		MineFantasyItems.loadComponent();
		MineFantasyItems.loadTool();

		PROXY.registerTickHandlers();

		PROXY.registerMain();

		PROXY.preInit(preEvent);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		GameRegistry.registerWorldGenerator(new WorldGenBiological(), 5);
		GameRegistry.registerWorldGenerator(new WorldGenGeological(), 5);
		GameRegistry.registerWorldGenerator(new WorldGenStructure(), 5);

		registerIngredients();

		CRAFTING_MANAGER_ANVIL.loadRecipes();
		CRAFTING_MANAGER_CARPENTER.loadRecipes();
		CRAFTING_MANAGER_BIG_FURNACE.loadRecipes();
		CRAFTING_MANAGER_ALLOY.loadRecipes();
		CRAFTING_MANAGER_BLOOMERY.loadRecipes();
		CRAFTING_MANAGER_BLAST_FURNACE.loadRecipes();
		CRAFTING_MANAGER_QUERN.loadRecipes();
		CRAFTING_MANAGER_TANNER.loadRecipes();
		CRAFTING_MANAGER_ROAST.loadRecipes();
		CRAFTING_MANAGER_KITCHEN_BENCH.loadRecipes();
		CRAFTING_MANAGER_SALVAGE.loadRecipes();
		CRAFTING_MANAGER_TRANSFORMATION.loadRecipes();
		CRAFTING_MANAGER_SPECIAL.loadRecipes();

		PROXY.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent postEvent) {
		MineFantasyArmorCustomEntries.initVanillaArmorEntries();
		MineFantasyArmorCustomEntries.initModdedArmorCustomEntries();
		ForgeFuelRegistry.INSTANCE.init();
		FirepitFuelRegistry.INSTANCE.init();
		ConfigItemRegistry.readCustoms();

		for (Biome biome : Biome.REGISTRY) {
			registerBiomeStuff(biome);
		}

		MineFantasyKnowledgeList.init();
		MineFantasyKnowledgeList.ArtefactListMFR.init();
		//Exporters go here
		RecipeRemover.removeSmeltingRecipes();

		MetalMaterial.addHeatables();

		//See the MFREventHandler.oreDictRegistry for the rest of the OreDict registrations
		MineFantasyOreDict.registerOreDictCommonIngotEntry();
		MineFantasyItems.addRandomDrops();

		if (postEvent.getSide() == Side.CLIENT) {
			configClient.save();
		}
		configArmour.save();
		configSpecials.save();
		configHardcore.save();
		configIntegration.save();
		configTools.save();
		configWeapon.save();
		configStamina.save();
		configItemRegistry.save();
		configFarming.save();
		configWorldGen.save();
		configCrafting.save();
		configMobs.save();

		PROXY.postInit(postEvent);
		PROXY.postInit();

	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandMFR());
	}

	@EventHandler
	public final void serverStarted(FMLServerStartedEvent event) {

	}

	@SubscribeEvent
	public void createRegistry(RegistryEvent.NewRegistry evt) {
		CraftingManagerAnvil.init();
		CraftingManagerCarpenter.init();
		CraftingManagerBigFurnace.init();
		CraftingManagerAlloy.init();
		CraftingManagerBloomery.init();
		CraftingManagerBlastFurnace.init();
		CraftingManagerQuern.init();
		CraftingManagerTanner.init();
		CraftingManagerRoast.init();
		CraftingManagerKitchenBench.init();
		CraftingManagerSalvage.init();
		CraftingManagerTransformation.init();
		CraftingManagerSpecial.init();
	}

	public static void registerIngredients() {
		CraftingHelper.register(new ResourceLocation(MOD_ID, "item_count"), (IIngredientFactory) (c, j) -> new IngredientCount(CraftingHelper.getItemStack(j, c)));
		CraftingHelper.register(new ResourceLocation(MOD_ID, "ore_dict_count"), (IIngredientFactory) (c, j) -> new IngredientOreCount(JsonUtils.getString(j, "ore"), JsonUtils.getInt(j, "count", 1)));
	}

	@SubscribeEvent
	public void configChangedEvent(ConfigChangedEvent.OnConfigChangedEvent evt) {
		if (MOD_ID.equals(evt.getModID())) {
			configClient.save();
			configArmour.save();
			configSpecials.save();
			configHardcore.save();
			configIntegration.save();
			configTools.save();
			configWeapon.save();
			configStamina.save();
			configItemRegistry.save();
			configFarming.save();
			configWorldGen.save();
			configCrafting.save();
			configMobs.save();
		}
	}

	private void registerBiomeStuff(Biome biome) {
		if (WorldGenBiological.isBiomeInConstraint(biome, ConfigWorldGen.berryMinTemp, ConfigWorldGen.berryMaxTemp,
				ConfigWorldGen.berryMinRain, ConfigWorldGen.berryMaxRain)) {
			biome.addFlower(MineFantasyBlocks.BERRY_BUSH.getDefaultState(), 5);
		}
	}
}
