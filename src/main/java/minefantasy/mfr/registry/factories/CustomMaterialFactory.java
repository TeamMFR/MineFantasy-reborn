package minefantasy.mfr.registry.factories;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.material.MetalMaterial;
import minefantasy.mfr.material.WoodMaterial;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class CustomMaterialFactory {

	public CustomMaterial parse(JsonContext context, JsonObject json, String type) {
		CustomMaterialType materialType = CustomMaterialType.deserialize(type);
		switch (materialType) {
			case WOOD_MATERIAL:
				return parseWoodMaterial(context, json);
			case METAL_MATERIAL:
				return parseMetalMaterial(context, json);
			default:
				return null;
		}
	}

	private CustomMaterial parseMetalMaterial(JsonContext context, JsonObject json) {
		String name = JsonUtils.getString(json, "name");

		JsonElement ingredientJson = json.get("materialIngredient");

		JsonObject properties = JsonUtils.getJsonObject(json, "properties");
		float durability = JsonUtils.getFloat(properties, "durability");
		float flexibility = JsonUtils.getFloat(properties, "flexibility");
		float sharpness = JsonUtils.getFloat(properties, "sharpness");
		float hardness = JsonUtils.getFloat(properties, "hardness");
		float resistance = JsonUtils.getFloat(properties, "resistance");
		float density = JsonUtils.getFloat(properties, "density");
		int tier = JsonUtils.getInt(properties, "tier");
		int meltingPoint = JsonUtils.getInt(properties, "melting_point");
		String rarity = JsonUtils.getString(properties, "rarity");
		int enchantability = JsonUtils.getInt(properties, "enchantability");
		int craftTier = JsonUtils.getInt(properties, "craft_tier");
		int craftTimeModifier = JsonUtils.getInt(properties, "craft_time_modifier");
		boolean unbreakable = JsonUtils.getBoolean(properties, "unbreakable");

		JsonObject armourStats = JsonUtils.getJsonObject(json, "armour_stats");
		float cuttingProtection = JsonUtils.getFloat(armourStats, "cutting");
		float bluntProtection = JsonUtils.getFloat(armourStats, "blunt");
		float piercingProtection = JsonUtils.getFloat(armourStats, "piercing");
		Float[] armour = {cuttingProtection, bluntProtection, piercingProtection};

		JsonObject color = JsonUtils.getJsonObject(json, "color");
		int red = JsonUtils.getInt(color, "red");
		int green = JsonUtils.getInt(color, "green");
		int blue = JsonUtils.getInt(color, "blue");
		int[] colors = {red, green, blue};

		CustomMaterial material = new MetalMaterial(name, null, colors, hardness, durability,
				flexibility, sharpness, resistance, density, tier, Rarity.valueOf(rarity), enchantability, craftTier,
				craftTimeModifier, meltingPoint, armour, unbreakable);

		CustomMaterialRegistry.INGREDIENT_JSON_MAP.put(material, ImmutablePair.of(ingredientJson, context));

		return material;
	}

	private CustomMaterial parseWoodMaterial(JsonContext context, JsonObject json) {
		String name = JsonUtils.getString(json, "name");

		JsonElement ingredientJson = json.get("materialIngredient");

		JsonObject properties = JsonUtils.getJsonObject(json, "properties");
		float durability = JsonUtils.getFloat(properties, "durability");
		float flexibility = JsonUtils.getFloat(properties, "flexibility");
		float hardness = JsonUtils.getFloat(properties, "hardness");
		float resistance = JsonUtils.getFloat(properties, "resistance");
		float density = JsonUtils.getFloat(properties, "density");
		int tier = JsonUtils.getInt(properties, "tier");
		String rarity = JsonUtils.getString(properties, "rarity");
		int craftTier = JsonUtils.getInt(properties, "craft_tier");
		int craftTimeModifier = JsonUtils.getInt(properties, "craft_time_modifier");

		JsonObject color = JsonUtils.getJsonObject(json, "color");
		int red = JsonUtils.getInt(color, "red");
		int green = JsonUtils.getInt(color, "green");
		int blue = JsonUtils.getInt(color, "blue");
		int[] colors = {red, green, blue};

		CustomMaterial material = new WoodMaterial(name, null,  colors, hardness,
				durability, flexibility, 0F, resistance, density, tier, Rarity.valueOf(rarity), 0, craftTier,
				craftTimeModifier * 4F, false);

		CustomMaterialRegistry.INGREDIENT_JSON_MAP.put(material, ImmutablePair.of(ingredientJson, context));

		return material;
	}
}
