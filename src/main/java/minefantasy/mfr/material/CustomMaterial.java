package minefantasy.mfr.material;

import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.registry.types.CustomMaterialType;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class CustomMaterial extends IForgeRegistryEntry.Impl<CustomMaterial>{
	private static final int[] flameResistArray = new int[] {100, 300};

	private final String name;
	private final CustomMaterialType type;

	protected Ingredient materialIngredient;
	/**
	 * The material colour
	 */
	private final int[] colourRGB;
	/**
	 * Base threshold for armour rating
	 */
	private final float hardness;
	/**
	 * The Modifier for durability (1pt per 250 uses)
	 */
	private final float durability;
	/**
	 * used for bow power.. >1 weakens blunt prot, <1 weakens piercing prot
	 */
	private final float flexibility;
	/**
	 * The Efficiency modifier (Like ToolMaterial) Also does damage
	 */
	private final float sharpness;
	/**
	 * The modifier to resist elements like fire and corrosion)
	 */
	private final float resistance;
	/**
	 * The weight Kg/U (Kilogram per unit)
	 */
	private final float density;
	private final int tier;
	private final Rarity rarity;
	private final int enchantability;
	private final int crafterTier;
	private final Integer crafterAnvilTier;
	private final Float craftTimeModifier;
	private final Integer meltingPoint;
	private Float[] armourProtection; // TODO: consider making this property into a typed class
	private final boolean unbreakable;

	public CustomMaterial(String name, CustomMaterialType type, Ingredient materialIngredient, int[] colourRGB, float hardness,
			float durability, float flexibility, float sharpness, float resistance, float density, int tier, Rarity rarity,
			int enchantability, int crafterTier, Integer crafterAnvilTier, Float craftTimeModifier, Integer meltingPoint,
			Float[] armourProtection, boolean unbreakable) {
		this.name = name;
		this.type = type;
		this.materialIngredient = materialIngredient;
		this.colourRGB = colourRGB;
		this.hardness = hardness;
		this.durability = durability;
		this.flexibility = flexibility;
		this.sharpness = sharpness;
		this.resistance = resistance;
		this.density = density;
		this.tier = tier;
		this.rarity = rarity;
		this.enchantability = enchantability;
		this.crafterTier = crafterTier;
		this.crafterAnvilTier = crafterAnvilTier;
		this.craftTimeModifier = craftTimeModifier;
		this.meltingPoint = meltingPoint;
		this.armourProtection = armourProtection;
		this.unbreakable = unbreakable;
	}

	/**
	 * Gets material name
	 */
	public String getName() {
		return name.toLowerCase();
	}

	/**
	 * Gets material type
	 */
	public CustomMaterialType getType() {
		return type;
	}

	public void setMaterialIngredient(Ingredient materialIngredient) {
		this.materialIngredient = materialIngredient;
	}

	public Ingredient getMaterialIngredient() {
		return materialIngredient;
	}

	public int[] getColourRGB() {
		return colourRGB;
	}

	public int getColourInt() {
		return (colourRGB[0] << 16) + (colourRGB[1] << 8) + colourRGB[2];
	}

	public float getHardness() {
		return hardness;
	}

	public float getDurability() {
		return durability;
	}

	public float getFlexibility() {
		return flexibility;
	}

	public float getSharpness() {
		return sharpness;
	}

	public float getResistance() {
		return resistance;
	}

	public float getDensity() {
		return density;
	}

	public int getTier() {
		return tier;
	}

	public Rarity getRarity() {
		return rarity;
	}

	public int getEnchantability() {
		return enchantability;
	}

	public int getCrafterTier() {
		return crafterTier;
	}

	public int getCrafterAnvilTier() {
		return crafterAnvilTier;
	}

	public float getCraftTimeModifier() {
		return craftTimeModifier;
	}

	public Integer getMeltingPoint() {
		return meltingPoint;
	}

	public void setArmourStats(float cutting, float blunt, float piercing) {
		armourProtection = new Float[] {cutting, blunt, piercing};
	}

	public Float[] getArmourProtection() {
		return armourProtection;
	}

	public boolean isUnbreakable() {
		return unbreakable;
	}

	@SideOnly(Side.CLIENT)
	public String getMaterialString() {
		return I18n.format("materialtype." + this.type.getName() + ".name", this.crafterTier);
	}

	public float getArmourProtection(int id) {
		return armourProtection[id];
	}

	public float getFireResistance() {
		if (meltingPoint > flameResistArray[0]) {
			float max = flameResistArray[1] - flameResistArray[0];
			float heat = meltingPoint - flameResistArray[0];

			int res = (int) (heat / max * 100F);
			return Math.min(100, res);
		}
		return 0F;
	}

	// -----------------------------------BOW
	// FUNCTIONS----------------------------------------\\

	public int[] getHeatableStats() {
		int workableTemp = meltingPoint;
		int unstableTemp = (int) (workableTemp * 1.5F);
		int maxTemp = (int) (workableTemp * 2F);
		return new int[] {workableTemp, unstableTemp, maxTemp};
	}

	public boolean isHeatable() {
		return false;
	}
}
