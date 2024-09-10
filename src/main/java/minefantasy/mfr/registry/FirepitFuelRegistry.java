package minefantasy.mfr.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import minefantasy.mfr.api.heating.FirepitFuel;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.util.FileUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class FirepitFuelRegistry extends DataLoader {

    public static HashMap<ArrayList<Object>, FirepitFuel> firepitFuelRegistry = new HashMap<>();

    private static final String FIREPIT_FUEL_TYPES = "firepit_fuel_types";

    public static final FirepitFuelRegistry INSTANCE = new FirepitFuelRegistry();

    private static final String TYPE = "firepit fuel";
    private static final String DEFAULT_RECIPE_DIRECTORY = Constants.ASSET_DIRECTORY +"/fuels_mfr";
    private static final String CUSTOM_RECIPE_DIRECTORY = "config/" + Constants.CONFIG_DIRECTORY +"/custom/registry";

    public static FirepitFuel getFuelStats(ItemStack item) {
        ArrayList<Object> key = new ArrayList<>();
        key.add(item.getItem());
        key.add(item.getMetadata());
        return FirepitFuelRegistry.firepitFuelRegistry.get(key);
    }

    public void init() {
        createCustomDataDirectory(CUSTOM_RECIPE_DIRECTORY);
        loadRegistry(TYPE, DEFAULT_RECIPE_DIRECTORY, CUSTOM_RECIPE_DIRECTORY);
    }

    public void loadRegistryFiles(File source, String base, String type) {
        FileUtils.findFiles(source, base, (root, file) -> {
            String extensions = FilenameUtils.getExtension(file.toString());

            if (!extensions.equals(JSON_FILE_EXT)) {
                return;
            }

            Path relative = root.relativize(file);
            if (relative.getNameCount() > 1) {
                String modName = relative.getName(0).toString();
                String fileName = FilenameUtils.removeExtension(relative.getFileName().toString());

                if (!Loader.isModLoaded(modName) || !fileName.equals(FIREPIT_FUEL_TYPES)) {
                    return;
                }

                JsonObject jsonObject = fileToJsonObject(file, relative, type);
                parse(fileName, jsonObject);
            }
        });
    }

    @Override
    protected void parse(String name, JsonObject json) {
        JsonArray fuels = JsonUtils.getJsonArray(json, "fuels");

        for (JsonElement e : fuels) {
            JsonObject fuel = JsonUtils.getJsonObject(e, "");
            parseFuel(fuel);
        }
    }

    private void parseFuel(JsonObject json) {
        String name = JsonUtils.getString(json, "name");

        Item item = Item.getByNameOrId(JsonUtils.getString(json, "inputItem"));
        int inputItemMeta = JsonUtils.getInt(json, "inputItemMeta");
        ItemStack itemStack = new ItemStack(item, 1, inputItemMeta);

        ArrayList<Object> key = new ArrayList<>();
        key.add(item);
        key.add(inputItemMeta);

        if (firepitFuelRegistry.containsKey(key)) {
            return;
        }

        JsonObject properties = JsonUtils.getJsonObject(json, "properties");
        float burnTime = JsonUtils.getFloat(properties, "burn_time");
        int baseHeat = JsonUtils.getInt(properties, "base_heat");
        boolean doesLight = JsonUtils.getBoolean(properties, "does_light");
        FirepitFuel fuel = new FirepitFuel(itemStack, burnTime, baseHeat, doesLight);
        firepitFuelRegistry.put(key, fuel);
    }
}
