package minefantasy.mfr.material;

import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.util.MFRLogUtil;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

import java.util.HashMap;

/**
 * This is used to create both tool and armour materials. Variables needed:
 * Durability, protection, sharpness, enchantment, weight, harvestLvl
 */
public class BaseMaterial {
	/**
	 * This scales armour so a sword hitting full mail of equal tier has this as a
	 * result Used so armour can scale up with weapon damage
	 */
	private static final float armourVsSwordBalance = 2.0F;
	/**
	 * The base damage for swords used with players. (Swords are 4+dam, adding 1 dam
	 * for player base hit dam)
	 */
	private static final float SWORD_DAMAGE = 5F;
	public static HashMap<String, BaseMaterial> materialMap = new HashMap<String, BaseMaterial>();

	private static float ACrounding = 10F; // round to nearest 10
	/*
	 * WOOD(0, 59, 2.0F, 0.0F, 15), STONE(1, 131, 4.0F, 1.0F, 5), IRON(2, 250, 6.0F,
	 * 2.0F, 14), EMERALD(3, 1561, 8.0F, 3.0F, 10), GOLD(0, 32, 12.0F, 0.0F, 22);
	 *
	 * To get variables: X = sword damage, Y = Armour Ratio(+1 of value), Z = Damage
	 *
	 * X = Z * Y Y = X / Z Z = X / Y
	 */
	// Rounding off to nearest 0.5 makes about an 0.03 difference, but the AR is
	// cleaner
	// Hardness isn't added, it calculats armour itself to match sharpness
	// name dura, AC enchant weight

	public String name;

	/** The number of uses this material allows. (wood = 59, stone = 131, iron = 250, diamond = 1561, gold = 32) */
	public int maxUses;
	/** The level of material this tool can harvest (e.g. 3 = DIAMOND, 2 = IRON, 1 = STONE, 0 = WOOD/GOLD) */
	public int harvestLevel;
	/**  This variable determines the protection : Hardness and sharpness are normally relevant to each other (so armour vs weapon equal tier has the same
	 * after-calculation damage) */
	public float hardness;
	/** Damage versus entities. Also used in efficiency calculation. */
	public float attackDamage;
	/** Defines the natural enchantability factor of the material. */
	public int enchantability;
	public float weight;
	/** The required level to craft */
	public int requiredLevel;
	/** The tier the material is on */
	public int tier;

	// FORGING
	public int hammerTier;
	public int anvilTier;
	public float craftTimeModifier = 2.0F;

	// TIERING
	public int workableTemp = 100;
	public int unstableTemp = 150;
	// RESISTANCES
	public float fireResistance;
	public float arcaneResistance;
	public boolean isMythic = false;
	public Rarity rarity;
	// SPECIALS
	private ArmorMaterialMFR armourConversion;
	private ToolMaterial toolConversion;

	public BaseMaterial(String name, int tier, int durability, int harvestLevel, float hardness, float sharpness,
			int enchantability, float weight, int lvl, Rarity rarity) {
		this.requiredLevel = lvl;
		this.name = name;
		this.tier = tier;
		this.maxUses = durability;
		this.hardness = hardness;
		this.attackDamage = sharpness;
		this.enchantability = enchantability;
		this.weight = weight;
		this.harvestLevel = harvestLevel;
		this.rarity = rarity;
	}

	public static BaseMaterial addMaterial(String name, int tier, int durability, int harvestLevel, float sharpness, int enchantment, float weight, int lvl, Rarity rarity) {
		float armorClass;
		armorClass = ((sharpness + SWORD_DAMAGE) / armourVsSwordBalance) - 1.0F;
		MFRLogUtil.logDebug("Added Ratio Armour Material " + name + " AR = " + armorClass);
		float initAc = armorClass;

		armorClass = Math.round(armorClass * (100F / ACrounding));
		armorClass = armorClass / (100F / ACrounding);

		if (initAc != armorClass) {
			MFRLogUtil.logDebug("Auto-Calculated ArmourRating for tier: " + name + ", modified to " + armorClass);
		}
		return addMaterial(name, tier, durability, harvestLevel, armorClass, sharpness, enchantment, weight, lvl, rarity);
	}

	public static BaseMaterial addMaterial(String name, int tier, int durability, int harvestLevel, float sharpness, int enchantment, float weight, int lvl) {
		float armorClass;
		armorClass = ((sharpness + SWORD_DAMAGE) / armourVsSwordBalance) - 1.0F;
		MFRLogUtil.logDebug("Added Ratio Armour Material " + name + " AR = " + armorClass);
		float initAc = armorClass;

		armorClass = Math.round(armorClass * (100F / ACrounding));
		armorClass = armorClass / (100F / ACrounding);

		if (initAc != armorClass) {
			MFRLogUtil.logDebug("Auto-Calculated ArmourRating for tier: " + name + ", modified to " + armorClass);
		}
		return addMaterial(name, tier, durability, harvestLevel, armorClass, sharpness, enchantment, weight, lvl);
	}

	public static BaseMaterial addMaterial(String name, int tier, int durability, int harvestLevel, float hardness,
			float sharpness, int enchantment, float weight, int lvl, Rarity rarity) {
		return register(new BaseMaterial(name, tier, durability, harvestLevel, hardness, sharpness, enchantment, weight, lvl, rarity));
	}

	public static BaseMaterial addMaterial(String name, int tier, int durability, int harvestLevel, float hardness,
			float sharpness, int enchantment, float weight, int lvl) {
		return register(new BaseMaterial(name, tier, durability, harvestLevel, hardness, sharpness, enchantment, weight, lvl, Rarity.COMMON));
	}

	public static BaseMaterial register(BaseMaterial material) {
		materialMap.put(material.name.toLowerCase(), material);
		return material;
	}

	public static ArmorMaterialMFR getMFRArmourMaterial(BaseMaterial material) {
		return material.convertToMFArmour();
	}

	public BaseMaterial setForgeStats(int hammer, int anvil, float timer, int workable, int unstable) {
		hammerTier = hammer;
		anvilTier = anvil;
		craftTimeModifier = (timer * 2F) + 2.0F;
		workableTemp = workable;
		unstableTemp = unstable;
		return this;
	}

	public BaseMaterial setResistances(float fire, float arcane) {
		fireResistance = fire;
		arcaneResistance = arcane;
		return this;
	}

	private ArmorMaterialMFR convertToMFArmour() {
		return new ArmorMaterialMFR("MF" + name, maxUses, hardness, enchantability, weight)
				.setFireResistance(fireResistance).setMagicResistance(arcaneResistance).setMythic(isMythic);
	}

	public ArmorMaterialMFR getArmourConversion() {
		if (armourConversion == null) {
			armourConversion = getMFRArmourMaterial(this);
		}
		return armourConversion;
	}

	public static BaseMaterial getMaterial(String name) {
		return materialMap.get(name.toLowerCase());
	}

	private ToolMaterial registerAsToolMaterial() {
		return EnumHelper.addToolMaterial("MF" + name, harvestLevel, maxUses, 2.0F + (attackDamage * 2F), attackDamage, enchantability);
	}

	public ToolMaterial getToolMaterial() {
		if (toolConversion == null) {
			toolConversion = this.registerAsToolMaterial();
		}
		return toolConversion;
	}
}
