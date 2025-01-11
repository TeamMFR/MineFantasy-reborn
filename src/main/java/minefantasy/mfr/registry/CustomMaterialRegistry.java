package minefantasy.mfr.registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.registry.factories.CustomMaterialFactory;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.util.FileUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class CustomMaterialRegistry extends DataLoader {

	private static final IForgeRegistry<CustomMaterial> CUSTOM_MATERIALS =
			new RegistryBuilder<CustomMaterial>()
					.setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "custom_materials"))
					.setType(CustomMaterial.class)
					.setMaxID(Integer.MAX_VALUE >> 5)
					.disableSaving()
					.allowModification()
					.create();

	public static HashMap<CustomMaterialType, ArrayList<CustomMaterial>> TYPE_LIST = new HashMap<>();
	public static HashMap<CustomMaterial, ImmutablePair<JsonElement, JsonContext>> INGREDIENT_JSON_MAP = new HashMap<>();

	public static final CustomMaterial NONE = new CustomMaterial("none", CustomMaterialType.NONE, Ingredient.EMPTY, new int[] {237, 237, 237}, 0, 0,0,0,0,0,0, Rarity.COMMON,0, 0, null, null, null, null, false);

	private static final String NBT_BASE = "mf_custom_materials";
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

	private static final String DEFAULT_MATERIAL_DIRECTORY = Constants.ASSET_DIRECTORY + "/materials_mfr";
	private static final String CUSTOM_MATERIAL_DIRECTORY = "config/" + Constants.CONFIG_DIRECTORY +"/custom/registry";

	public static final CustomMaterialRegistry INSTANCE = new CustomMaterialRegistry();
	public static final CustomMaterialFactory FACTORY = new CustomMaterialFactory();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public void preInit() {
		FileUtils.createCustomDataDirectory(CUSTOM_MATERIAL_DIRECTORY);
		for (CustomMaterialType type : CustomMaterialType.values()) {
			loadRegistry(type.getName(), DEFAULT_MATERIAL_DIRECTORY, CUSTOM_MATERIAL_DIRECTORY);
		}
	}

	public void loadRegistryFiles(ModContainer mod, File source, String base, String type) {
		JsonContext context = new JsonContext(mod.getModId());
		CustomMaterialType materialType = CustomMaterialType.deserialize(type);

		FileUtils.findFiles(source, base, (root, file) -> {
			String extension = FilenameUtils.getExtension(file.toString());

			if (!extension.equals(JSON_FILE_EXT)) {
				return;
			}

			Path relative = root.relativize(file);
			if (relative.getNameCount() > 1) {
				String modName = relative.getName(0).toString();
				String fileName = FilenameUtils.removeExtension(relative.getFileName().toString());

				if (!Loader.isModLoaded(modName) || !materialType.getFileName().equals(fileName)) {
					return;
				}

				BufferedReader reader = null;
				try {
					reader = Files.newBufferedReader(file);
					JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);

					if (Loader.isModLoaded(mod.getModId())) {
						if (materialType != CustomMaterialType.NONE) {
							parse(fileName, context, json, materialType.getName());
						} else {
							MineFantasyReforged.LOG.info("Skipping Custom Material file of type {} in {} because it's not a MFR Custom Material", materialType.getName(), file);
						}
					}
					else {
						MineFantasyReforged.LOG.info("Skipping Custom Material file of type {} in {} because it the mod it depends on is not loaded", materialType.getName(), file);
					}
				}
				catch (JsonParseException e) {
					MineFantasyReforged.LOG.error("Parsing error loading Custom Material in {} in {}", fileName, file, e);
				}
				catch (IOException e) {
					MineFantasyReforged.LOG.error("Couldn't read Custom Material in {} in {}", fileName, file, e);
				}
				finally {
					IOUtils.closeQuietly(reader);
				}
			}
		});
	}

	@Override
	protected void parse(String name, JsonContext context, JsonObject json, String type) {
		JsonArray materials = JsonUtils.getJsonArray(json, type);

		for (JsonElement e : materials) {
			CustomMaterial customMaterial = FACTORY.parse(context, JsonUtils.getJsonObject(e, ""), type);

			addMaterial(customMaterial);
		}
	}

	public static void addMaterial(CustomMaterial customMaterial) {
		ResourceLocation key = new ResourceLocation(MineFantasyReforged.MOD_ID, customMaterial.getName());
		customMaterial.setRegistryName(key);

		CUSTOM_MATERIALS.register(customMaterial);
		getList(customMaterial.getType()).add(customMaterial);
	}

	/**
	 * Custom Materials must be registered BEFORE Blocks and Items are registered.
	 * But Custom Materials define Ingredients that contain Blocks and Items.
	 * This creates a paradox.
	 * This method populates the Custom Materials' Ingredients AFTER all the Blocks and Items are registered.
	 * Paradox solved wooooo.
	 */
	public void addIngredients() {
		for (CustomMaterial material : getValues()) {
			if (material.getMaterialIngredient() == null) {
				ImmutablePair<JsonElement, JsonContext> ingredientJsonInfo = INGREDIENT_JSON_MAP.get(material);

				Ingredient ingredient = CraftingHelper.getIngredient(
						ingredientJsonInfo.getLeft(),
						ingredientJsonInfo.getRight());
				material.setMaterialIngredient(ingredient);
			}
		}
	}

	public static Collection<CustomMaterial> getValues() {
		return CUSTOM_MATERIALS.getValuesCollection();
	}

	/**
	 * Gets a material by name
	 */
	public static CustomMaterial getMaterial(String name) {
		if (name == null) {
			return NONE;
		}

		ResourceLocation key = new ResourceLocation(MineFantasyReforged.MOD_ID, name.toLowerCase());
		CustomMaterial material = CUSTOM_MATERIALS.getValue(key);
		if (material == null){
			return NONE;
		}
		else {
			return material;
		}
	}

	/**
	 * Gets the list of all Materials of the given type
	 * @param type the Type of the Custom Materials
	 * @return List of Custom Materials
	 */
	public static ArrayList<CustomMaterial> getList(CustomMaterialType type) {
		TYPE_LIST.computeIfAbsent(type, k -> new ArrayList<>());
		return TYPE_LIST.get(type);
	}

	/**
	 * Adds a Custom Material to an ItemStack
	 * @param item 		The ItemStack to add the Custom Material to
	 * @param slot 		The 'position' of the Material
	 * @param material	The CustomMaterial to add
	 */
	public static void addMaterial(ItemStack item, String slot, String material) {
		if (material == null || material.isEmpty()) {
			return;
		}
		NBTTagCompound nbt = getNBT(item, true);
		nbt.setString(slot, material);
	}

	/**
	 * Gets the Custom Material of an ItemStacl
	 * @param item The ItemStack to get the Material of
	 * @param slot The 'position' of the Material
	 * @return The Custom Material
	 */

	public static CustomMaterial getMaterialFor(ItemStack item, String slot) {
		NBTTagCompound nbt = getNBT(item, false);
		if (nbt != null) {
			if (nbt.hasKey(slot)) {
				return getMaterial(nbt.getString(slot));
			}
		}
		return NONE;
	}

	public static NBTTagCompound getNBT(ItemStack item, boolean createNew) {
		if (!item.isEmpty() && item.hasTagCompound() && item.getTagCompound().hasKey(NBT_BASE)) {
			return (NBTTagCompound) item.getTagCompound().getTag(NBT_BASE);
		}
		if (createNew) {
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagCompound nbt2 = new NBTTagCompound();
			item.setTagCompound(nbt);
			nbt.setTag(NBT_BASE, nbt2);
			return nbt2;
		}
		return null;
	}

	/**
	 * Gets the formatted weight string for a given mass
	 * @param mass the mass to be formatted
	 * @return the formatted string
	 */
	@SideOnly(Side.CLIENT)
	public static String getWeightString(float mass) {
		String s = "attribute.weightKg.name";
		if (mass > 0 && mass < 1.0F) {
			s = "attribute.weightg.name";
			mass = (int) (mass * 1000F);
		} else if (mass > 1000) {
			s = "attribute.weightt.name";
			mass = (int) (mass / 1000F);
		}
		return I18n.format(s, DECIMAL_FORMAT.format(mass));
	}
}
