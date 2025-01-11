package minefantasy.mfr.material;

import com.google.common.base.CaseFormat;
import minefantasy.mfr.api.MineFantasyReforgedAPI;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.util.MFRLogUtil;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;

public class MetalMaterial extends CustomMaterial {

	public MetalMaterial(String name, CustomMaterialType type, Ingredient materialIngredient, int[] colourRGB, float hardness,
			float durability, float flexibility, float sharpness, float resistance, float density, int tier, Rarity rarity,
			int enchantability, int crafterTier, Integer crafterAnvilTier, Float craftTimeModifier, Integer meltingPoint,
			Float[] armourProtection, boolean unbreakable) {

		super(name, type, materialIngredient, colourRGB, hardness, durability, flexibility, sharpness, resistance, density, tier,
				rarity, enchantability, crafterTier, crafterAnvilTier, craftTimeModifier,
				meltingPoint, armourProtection, unbreakable);

		setArmourStats(1.0F, flexibility, 1F / flexibility);// Harder materials absorb blunt less but resist cutting and piercing more

		// Adding this is necessary to preserve the old system where defaults are dynamically calculated above with setArmourStats and non-default values take precedence over the calculated values
		// old formula: hardness = ((sharpness + 5F) / 2F) - 1F;
		for (float value : armourProtection) {
			if (value != 1.0) {
				setArmourStats(armourProtection[0], armourProtection[1], armourProtection[2]);
				break;
			}
		}
	}

	public static void addHeatables() {
		ArrayList<CustomMaterial> metal = CustomMaterialRegistry.getList(CustomMaterialType.METAL_MATERIAL);
		for (CustomMaterial customMat : metal) {
			int[] stats = customMat.getHeatableStats();
			MFRLogUtil.logDebug("Set Heatable Stats for " + customMat.getName() + ": " + stats[0] + "," + stats[1] + "," + stats[2]);

			MineFantasyReforgedAPI.setHeatableStats(customMat.materialIngredient, stats[0], stats[1], stats[2]);
			MineFantasyReforgedAPI.setHeatableStats("hunk" + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, customMat.getName()), stats[0], stats[1], stats[2]);
		}

		MineFantasyReforgedAPI.setHeatableStats(MineFantasyItems.RIVET, 1000, 2000, 3000);
		MineFantasyReforgedAPI.setHeatableStats(MineFantasyItems.METAL_HUNK, -1, -1, -1);
		MineFantasyReforgedAPI.setHeatableStats(MineFantasyItems.BAR, -1, -1, -1);
	}

	@Override
	public boolean isHeatable() {
		return true;
	}
}
