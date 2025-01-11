package minefantasy.mfr.init;

import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.material.BaseMaterial;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import net.minecraft.item.crafting.Ingredient;

public class MineFantasyMaterials {

	// Cloth and leather
	public static BaseMaterial LINEN;
	public static BaseMaterial WOOL;
	public static BaseMaterial LEATHER_APRON;
	public static BaseMaterial HIDE;
	public static BaseMaterial ROUGH;
	public static BaseMaterial REINFORCED;
	public static BaseMaterial PADDING;
	public static BaseMaterial STUDDED;
	public static BaseMaterial SCALED;
	public static BaseMaterial DRAGONSCALE;
	// Misc Mats
	public static BaseMaterial STONE;
	public static BaseMaterial TIN;
	public static BaseMaterial PIG_IRON;
	public static BaseMaterial SILVER;
	public static BaseMaterial GOLD;
	public static BaseMaterial ORNATE;
	public static BaseMaterial WEAK_BLACK_STEEL;
	public static BaseMaterial WEAK_RED_STEEL;
	public static BaseMaterial WEAK_BLUE_STEEL;
	public static BaseMaterial TUNGSTEN;
	// Tiers
	public static BaseMaterial COPPER;
	public static BaseMaterial BRONZE;
	public static BaseMaterial IRON;
	public static BaseMaterial STEEL;
	public static BaseMaterial ENCRUSTED;
	public static BaseMaterial OBSIDIAN;
	public static BaseMaterial BLACK_STEEL;
	public static BaseMaterial BLUE_STEEL;
	public static BaseMaterial RED_STEEL;
	public static BaseMaterial DRAGONFORGE;
	public static BaseMaterial ADAMANTIUM;
	public static BaseMaterial MITHRIL;
	public static BaseMaterial IGNOTUMITE;
	public static BaseMaterial MITHIUM;
	public static BaseMaterial ENDERFORGE;
	// Engineer
	public static BaseMaterial COGWORKS;
	public static BaseMaterial COMPOSITE_ALLOY;


	public static class Names {

		// DEFAULT MATERIALS
		public static final String LEATHER = "leather";
		public static final String HARD_LEATHER = "hard_leather";
		public static final String MINOTAUR_SKIN = "minotaur_skin";
		public static final String DRAGON_SKIN = "dragon_skin";

		public static final String IRON = "iron";
		public static final String STONE = "stone";
		public static final String TIN = "tin";
		public static final String COPPER = "copper";
		public static final String BRONZE = "bronze";
		public static final String PIG_IRON = "pig_iron";
		public static final String STEEL = "steel";
		public static final String ENCRUSTED = "encrusted";
		public static final String OBSIDIAN = "obsidian";
		public static final String TUNGSTEN = "tungsten";
		public static final String WEAK_BLACK_STEEL = "weak_black_steel";
		public static final String BLACK_STEEL = "black_steel";
		public static final String BLUE_STEEL = "blue_steel";
		public static final String WEAK_RED_STEEL = "weak_red_steel";
		public static final String RED_STEEL = "red_steel";
		public static final String SILVER = "silver";
		public static final String GOLD = "gold";
		public static final String MITHRIL = "mithril";
		public static final String ADAMANTIUM = "adamantium";
		public static final String MITHIUM = "mithium";
		public static final String IGNOTUMITE = "ignotumite";
		public static final String ENDER = "ender";
		// WOODS
		public static final String SCRAP_WOOD = "scrap_wood";
		public static final String OAK_WOOD = "oak_wood";
		public static final String REFINED_WOOD = "refined_wood";
		public static final String IRONBARK_WOOD = "ironbark_wood";
		public static final String EBONY_WOOD = "ebony_wood";
	}


	public static void initLeatherMaterials() {
		CustomMaterialRegistry.addMaterial(new CustomMaterial(Names.LEATHER, CustomMaterialType.LEATHER_MATERIAL,
				Ingredient.EMPTY, new int[]{198, 92, 53}, 1.0F, 0.4F, 1.2F, 0.5F,
				0F, 0.1F, 0, Rarity.COMMON, 15,
				0, null, null, null, null, false));

		CustomMaterialRegistry.addMaterial(new CustomMaterial(Names.HARD_LEATHER, CustomMaterialType.LEATHER_MATERIAL,
				Ingredient.EMPTY, new int[]{154, 72, 41}, 1.5F, 0.8F, 1.0F, 0.5F,
				0F, 0.2F, 1, Rarity.UNCOMMON, 20,
				1, null, null, null, null, false));

		CustomMaterialRegistry.addMaterial(new CustomMaterial(Names.MINOTAUR_SKIN, CustomMaterialType.LEATHER_MATERIAL,
				Ingredient.EMPTY, new int[]{118, 69, 48}, 2.0F, 1.5F, 0.8F, 1.0F,
				0F, 0.5F, 2, Rarity.RARE, 25,
				2, null, null, null, null, false));

		CustomMaterialRegistry.addMaterial(new CustomMaterial(Names.DRAGON_SKIN, CustomMaterialType.LEATHER_MATERIAL,
				Ingredient.EMPTY, new int[]{154, 72, 41}, 3.0F, 2.0F, 1.0F, 1.2F,
				0F, 0.75F, 3, Rarity.EPIC, 30,
				3, null, null, null, null, false));
	}

	public static void initBaseMaterials() {
		// LEATHER AND CLOTH

		//		return addMaterial(name, tier, durability, -1, AC, -1, enchantment, weight, lvl);
		LINEN = BaseMaterial.addMaterial("linen", 0, 10, -1, 0.1F, 0, 1.00F, 0);
		WOOL = BaseMaterial.addMaterial("wool", 1, 15, -1, 0.1F, -1, 0, 1.00F, 5);

		LEATHER_APRON = BaseMaterial.addMaterial("leather_apron", 0, 100, -1, 1.5F, -1, 0, 0.50F, 0);
		HIDE = BaseMaterial.addMaterial("hide", 0, 100, -1, 1.5F, -1, 0, 1.00F, 0);
		ROUGH = BaseMaterial.addMaterial("rough_leather", 0, 150, -1, 1.5F, -1, 1, 1.00F, 0);
		REINFORCED = BaseMaterial.addMaterial("strong_leather", 1, 250, -1, 2.0F, -1, 1, 1.00F, 5);
		PADDING = BaseMaterial.addMaterial("padded", 1, 200, -1, 2.0F, -1, 0, 1.00F, 5);
		STUDDED = BaseMaterial.addMaterial("stud_leather", 1, 500, -1, 2.5F, -1, 5, 1.20F, 15);
		SCALED = BaseMaterial.addMaterial("scale_leather", 2, 1000, -1, 2.8F, -1, 8, 1.50F, 25);
		DRAGONSCALE = BaseMaterial.addMaterial("dragonscale", 3, 3000, -1, 5F, -1, 20, 1.20F, 85, Rarity.RARE);

		// name Tier dura, harvest sharpness enchant weight
		// MISC
		WEAK_BLACK_STEEL = BaseMaterial.addMaterial("weak_black_steel", -1, 250, 4, 2.0F, 0, 1.00F, 40).setForgeStats(4, 4, 4.0F, 150, 500);
		WEAK_RED_STEEL = BaseMaterial.addMaterial("weak_red_steel", -1, 400, 5, 3.0F, 0, 1.10F, 65).setForgeStats(4, 4, 4.0F, 200, 500);
		WEAK_BLUE_STEEL = BaseMaterial.addMaterial("weak_blue_steel", -1, 300, 5, 2.5F, 0, 0.90F, 65).setForgeStats(4, 4, 4.0F, 175, 500);
		STONE = BaseMaterial.addMaterial("stone", 0, 50, 0, 0.1F, 0.0F, 0, 2.00F, 0).setForgeStats(0, 0, 0.75F, 0, 0);
		TIN = BaseMaterial.addMaterial("tin", 0, 100, 0, 0.2F, 5, 0.80F, 0).setForgeStats(0, 0, 0, 85, 100);
		PIG_IRON = BaseMaterial.addMaterial("pig_iron", 0, 250, 0, 1.5F, 3, 1.00F, 0).setForgeStats(2, 2, 2.0F, 100, 400);
		SILVER = BaseMaterial.addMaterial("silver", -1, 150, 0, 0.0F, 10, 0.70F, 0).setForgeStats(1, 1, 3F, 90, 120);
		GOLD = BaseMaterial.addMaterial("gold", -1, 150, 0, 0.0F, 25, 1.50F, 0).setForgeStats(1, 1, 3F, 90, 120);
		// goldPure = addMaterial("PureGold", -1, 50 , 0, 0.0F, 50, 2.00F, 0).setRarity(1);
		ORNATE = BaseMaterial.addMaterial("ornate", -1, 300, 0, 0.0F, 30, 1.00F, 30, Rarity.UNCOMMON).setForgeStats(1, 1, 4F, 120, 150);
		TUNGSTEN = BaseMaterial.addMaterial("tungsten", 2, 600, 3, 4F, 5, 1.50F, 0, Rarity.UNCOMMON).setForgeStats(3, 3, 5.0F, 150, 300);

		// TIERS
		// Basic / Common Materials (0-2) Levels 0-50
		COPPER = BaseMaterial.addMaterial("copper", 0, 200, 1, 1.0F, 5, 1.00F, 0).setForgeStats(0, 0, 1.0F, 95, 250); // lvl 0-4
		BRONZE = BaseMaterial.addMaterial("bronze", 1, 300, 2, 1.5F, 5, 1.00F, 5).setForgeStats(1, 1, 2.5F, 100, 250); // lvl 5-14
		IRON = BaseMaterial.addMaterial("iron", 2, 500, 2, 2.0F, 5, 1.00F, 15).setForgeStats(2, 2, 2.0F, 90, 250); // lvl 15-24
		STEEL = BaseMaterial.addMaterial("steel", 3, 750, 2, 2.5F, 10, 1.00F, 25).setForgeStats(3, 3, 2.5F, 120, 250); // lvl 25-39
		ENCRUSTED = BaseMaterial.addMaterial("encrusted", 3, 2000, 3, 3.5F, 25, 1.00F, 40).setForgeStats(3, 3, 5.0F, 130, 240); // lvl 40-49
		OBSIDIAN = BaseMaterial.addMaterial("obsidian", 3, 2000, 3, 3.5F, 25, 1.00F, 40).setForgeStats(3, 3, 5.0F, 130, 240); // lvl 40-49

		// Advanced Materials (3 - 4) Levels 50-75
		BLACK_STEEL = BaseMaterial.addMaterial("black_steel", 4, 1500, 4, 4.0F, 12, 1.00F, 50).setForgeStats(4, 4, 4.0F, 150, 350);// lvl 50
		DRAGONFORGE = BaseMaterial.addMaterial("dragonforge", 4, 1500, 4, 5.0F, 14, 1.00F, 60, Rarity.UNCOMMON).setForgeStats(4, 4, 8.0F, 250, 350).setResistances(100F, 0F);// lvl 60
		RED_STEEL = BaseMaterial.addMaterial("red_steel", 5, 2000, 5, 6.0F, 1, 1.15F, 75).setForgeStats(5, 5, 6.5F, 200, 350).setResistances(20F, 0F);// lvl 75
		BLUE_STEEL = BaseMaterial.addMaterial("blue_steel", 5, 1800, 5, 5.0F, 20, 0.75F, 75).setForgeStats(5, 5, 4.5F, 175, 325).setResistances(0F, 20F);// lvl 75

		// Mythic Materials (5) Levels 75-100
		ADAMANTIUM = BaseMaterial.addMaterial("adamantium", 6, 3000, 6, 8.0F, 10, 1.25F, 90, Rarity.UNCOMMON).setForgeStats(6, 5, 9.0F, 300, 400).setResistances(35F, 0F);// lvl 90
		MITHRIL = BaseMaterial.addMaterial("mithril", 6, 2500, 6, 7.0F, 30, 0.50F, 90, Rarity.UNCOMMON).setForgeStats(6, 5, 6.0F, 280, 400).setResistances(0F, 35F);// lvl 90

		// Masterwork Materials (6) Level 100
		IGNOTUMITE = BaseMaterial.addMaterial("ignotumite", 7, 1000, 7, 14.0F, 20, 2.00F, 100, Rarity.RARE).setForgeStats(7, 5, 15.0F, 350, 400).setResistances(50F, 0F);// High damage, heavy, fire resist lvl 100
		MITHIUM = BaseMaterial.addMaterial("mithium", 7, 1000, 7, 10.0F, 40, 0.25F, 100, Rarity.RARE).setForgeStats(7, 5, 15.0F, 330, 400).setResistances(0F, 50F);// Low damage, light, magic resist lvl 100
		ENDERFORGE = BaseMaterial.addMaterial("enderforge", 7, 1000, 7, 12.0F, 20, 1.00F, 100, Rarity.RARE).setForgeStats(7, 5, 15.0F, 400, 450).setResistances(25F, 25F);// Middle lvl 100

		// Engineer Materials
		// steel = addMaterial("Steel", 3, 750, 2, 2.5F, 10, 1.00F, 25).setForgeStats(3,
		// 3, 2.5F, 120, 250); //lvl 25-39
		COGWORKS = BaseMaterial.addMaterial("cogworks", 4, 500, -1, 1.0F, -1, 10, 1.00F, 85).setForgeStats(3, 3, 2.5F, 120, 250);
		COMPOSITE_ALLOY = BaseMaterial.addMaterial("composite_alloy", 4, 1800, -1, 4.0F, -1, 10, 2.00F, 85).setForgeStats(3, 3, 2.5F, 120, 250).setResistances(95F, 85F);

	}
}
