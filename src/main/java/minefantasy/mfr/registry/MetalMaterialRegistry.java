package minefantasy.mfr.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.material.MetalMaterial;
import minefantasy.mfr.util.FileUtils;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;

public class MetalMaterialRegistry extends DataLoader {
	private static final String METAL_TYPES = "metal_types";

	public static final MetalMaterialRegistry INSTANCE = new MetalMaterialRegistry();

	private static final String TYPE = "metal material";
	private static final String DEFAULT_RECIPE_DIRECTORY = Constants.ASSET_DIRECTORY + "/materials_mfr";
	private static final String CUSTOM_RECIPE_DIRECTORY = "config/" + Constants.CONFIG_DIRECTORY +"/custom/registry";

	public void preInit() {
		createCustomDataDirectory(CUSTOM_RECIPE_DIRECTORY);
		loadRegistry(TYPE, DEFAULT_RECIPE_DIRECTORY, CUSTOM_RECIPE_DIRECTORY);
	}

	public void loadRegistryFiles(File source, String base, String type) {
		FileUtils.findFiles(source, base, (root, file) -> {
			String extension = FilenameUtils.getExtension(file.toString());

			if (!extension.equals(JSON_FILE_EXT)) {
				return;
			}

			Path relative = root.relativize(file);
			if (relative.getNameCount() > 1) {
				String modName = relative.getName(0).toString();
				String fileName = FilenameUtils.removeExtension(relative.getFileName().toString());

				if (!Loader.isModLoaded(modName) || !fileName.equals(METAL_TYPES)) {
					return;
				}

				JsonObject jsonObject = fileToJsonObject(file, relative, type);
				parse(fileName, jsonObject);
			}
		});
	}

	@Override
	protected void parse(String name, JsonObject json) {
		JsonArray metals = JsonUtils.getJsonArray(json, "metals");

		for (JsonElement e : metals) {
			JsonObject metal = JsonUtils.getJsonObject(e, "");
			parseMetal(metal);
		}
	}

	private void parseMetal(JsonObject json) {
		String name = JsonUtils.getString(json, "name");

		String oreDictList = JsonUtils.getString(json, "oreDictList");

		if (MetalMaterial.getMaterial(name) != CustomMaterial.NONE) {
			return;
		}

		JsonObject properties = JsonUtils.getJsonObject(json, "properties");
		float durability = JsonUtils.getFloat(properties, "durability");
		float flexibility = JsonUtils.getFloat(properties, "flexibility");
		float sharpness = JsonUtils.getFloat(properties, "sharpness");
		float hardness = JsonUtils.getFloat(properties, "hardness");
		float resistance = JsonUtils.getFloat(properties, "resistance");
		float density = JsonUtils.getFloat(properties, "density");
		int tier = JsonUtils.getInt(properties, "tier");
		int meltingPoint = JsonUtils.getInt(properties, "melting_point");
		int rarity = JsonUtils.getInt(properties, "rarity");
		int enchantability = JsonUtils.getInt(properties, "enchantability");
		int craftTier = JsonUtils.getInt(properties, "craft_tier");
		int craftTimeModifier = JsonUtils.getInt(properties, "craft_time_modifier");
		boolean unbreakable = JsonUtils.getBoolean(properties, "unbreakable");

		JsonObject armourStats = JsonUtils.getJsonObject(json, "armour_stats");
		float cuttingProtection = JsonUtils.getFloat(armourStats, "cutting");
		float bluntProtection = JsonUtils.getFloat(armourStats, "blunt");
		float piercingProtection = JsonUtils.getFloat(armourStats, "piercing");
		float[] armour = {cuttingProtection, bluntProtection, piercingProtection};

		JsonObject color = JsonUtils.getJsonObject(json, "color");
		int red = JsonUtils.getInt(color, "red");
		int green = JsonUtils.getInt(color, "green");
		int blue = JsonUtils.getInt(color, "blue");
		int[] colors = {red, green, blue};

		MetalMaterial metal = new MetalMaterial(name, tier, hardness, durability, flexibility, sharpness, resistance, density, enchantability, armour, colors, oreDictList);
		metal.setMeltingPoint(meltingPoint);
		metal.setRarity(rarity);
		metal.setCrafterTiers(craftTier);
		metal.craftTimeModifier = craftTimeModifier;
		metal.setUnbreakable(unbreakable);
		metal.register();
	}
}
