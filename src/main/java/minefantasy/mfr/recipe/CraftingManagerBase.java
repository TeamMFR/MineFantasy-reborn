package minefantasy.mfr.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.factories.IRecipeMFRFactory;
import minefantasy.mfr.recipe.types.IRecipeMFRType;
import minefantasy.mfr.util.FileUtils;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class CraftingManagerBase<T extends IRecipeMFR> {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final String RECIPE_FOLDER_PATH;
	private final String CONFIG_RECIPE_DIRECTORY;
	private final IRecipeMFRFactory<T> recipeFactory;
	private final IRecipeMFRType recipeType;

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public CraftingManagerBase(
			IRecipeMFRFactory<T> factory,
			IRecipeMFRType type,
			String recipeFolderPath,
			String configRecipePath) {
		recipeFactory = factory;
		recipeType = type;
		RECIPE_FOLDER_PATH = recipeFolderPath;
		CONFIG_RECIPE_DIRECTORY = configRecipePath;
	}

	public void loadRecipes() {
		ModContainer modContainer = Loader.instance().activeModContainer();

		FileUtils.createCustomDataDirectory(CONFIG_RECIPE_DIRECTORY);
		Loader.instance().getActiveModList().forEach(m -> CraftingHelper
				.loadFactories(m, String.format(RECIPE_FOLDER_PATH, m.getModId()), CraftingHelper.CONDITIONS));
		//noinspection ConstantConditions
		loadRecipes(modContainer, new File(CONFIG_RECIPE_DIRECTORY), "");
		Loader.instance().getActiveModList().forEach(m ->
				loadRecipes(m, m.getSource(), String.format(RECIPE_FOLDER_PATH, m.getModId())));

		Loader.instance().setActiveModContainer(modContainer);
	}

	private void loadRecipes(ModContainer mod, File source, String base) {
		JsonContext ctx = new JsonContext(mod.getModId());

		FileUtils.findFiles(source, base, root -> FileUtils.loadConstants(source, base, ctx), (root, file) -> {
			Path relative = root.relativize(file);
			if (relative.getNameCount() > 1) {
				String extension = FilenameUtils.getExtension(file.toString());

				if (!extension.equals(Constants.JSON_FILE_EXT)) {
					return;
				}

				String modName = relative.getName(relative.getNameCount() - 2).toString();
				String fileName = FilenameUtils.removeExtension(relative.getFileName().toString());

				if (!Loader.isModLoaded(modName) || fileName.startsWith("_")) {
					return;
				}

				Loader.instance().setActiveModContainer(mod);

				if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.toString().startsWith("_"))
					return;

				ResourceLocation key = new ResourceLocation(ctx.getModId(), fileName);

				BufferedReader reader = null;
				try {
					reader = Files.newBufferedReader(file);
					JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);

					if (json.has("modIds")) {
						JsonArray modIds = JsonUtils.getJsonArray(json, "modIds");
						for (JsonElement modId : modIds) {
							if (!Loader.isModLoaded(modId.getAsString())) {
								return;
							}
						}
					}

					String type = ctx.appendModId(JsonUtils.getString(json, "type"));
					if (Loader.isModLoaded(mod.getModId())) {
						if (recipeType.getByNameWithModId(type, mod.getModId()) != recipeType) {
							T recipe = recipeFactory.parse(ctx, json);
							if (CraftingHelper.processConditions(json, "conditions", ctx)) {
								addRecipe(recipe, mod.getModId().equals(MineFantasyReforged.MOD_ID), key);
							}
						} else {
							MineFantasyReforged.LOG.info("Skipping recipe {} of type {} because it's not a MFR recipe", key, type);
						}
					}
					else {
						MineFantasyReforged.LOG.info("Skipping recipe {} of type {} because it the mod it depends on is not loaded", key, type);
					}
				}
				catch (JsonParseException e) {
					MineFantasyReforged.LOG.error("Parsing error loading recipe {}", key, e);
				}
				catch (IOException e) {
					MineFantasyReforged.LOG.error("Couldn't read recipe {} from {}", key, file, e);
				}
				finally {
					IOUtils.closeQuietly(reader);
				}
			}
		});
	}

	public abstract void addRecipe(T recipe, boolean checkForExistence, ResourceLocation key);
}
