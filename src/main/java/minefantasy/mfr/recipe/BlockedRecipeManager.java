package minefantasy.mfr.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.types.RecipeType;
import minefantasy.mfr.util.FileUtils;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BlockedRecipeManager {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	protected static HashMap<RecipeType, HashSet<ResourceLocation>> blockedRecipes = new HashMap<>();

	public void loadBlockedRecipes() {
		ModContainer modContainer = Loader.instance().activeModContainer();

		FileUtils.createCustomDataDirectory("config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/blocked_recipes.json");

		Loader.instance().getActiveModList().forEach(m ->
				loadBlockedRecipes(m.getSource(),
						String.format(Constants.ASSET_DIRECTORY + "/recipes_mfr/blocked_recipes.json", m.getModId())));

		Loader.instance().setActiveModContainer(modContainer);
	}

	public void loadBlockedRecipes(File source, String base) {

		FileUtils.findFiles(source, base, (root, file) -> {
			String extension = FilenameUtils.getExtension(file.toString());

			if (!extension.equals(Constants.JSON_FILE_EXT)) {
				return;
			}

			if (file.toFile().isFile()) {
				String fileName = FilenameUtils.removeExtension(file.getFileName().toString());

				BufferedReader reader = null;
				try {
					reader = Files.newBufferedReader(file);
					JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);

					parse(json);
				}
				catch (JsonParseException e) {
					MineFantasyReforged.LOG.error("Parsing error loading blocked recipe entry in {} in {}", fileName, file, e);
				}
				catch (IOException e) {
					MineFantasyReforged.LOG.error("Couldn't read blocked recipe entry in {} in {}", fileName, file, e);
				}
				finally {
					IOUtils.closeQuietly(reader);
				}
			}
		});
	}

	public void parse(JsonObject jsonObject) {
		JsonArray blockedRecipeTypes = JsonUtils.getJsonArray(jsonObject, "blocked_recipe_types");
		for (JsonElement e : blockedRecipeTypes) {
			JsonObject blockedRecipeType = JsonUtils.getJsonObject(e, "");
			RecipeType type = RecipeType.deserialize(JsonUtils.getString(blockedRecipeType, "type"));
			if (type != RecipeType.NONE) {
				JsonUtils.getJsonArray(blockedRecipeType, "blocked_recipes")
						.forEach((element) -> parseBlockedRecipeEntry(type, element));
			}
		}
	}

	private void parseBlockedRecipeEntry(RecipeType type, JsonElement element) {
		if (element.isJsonPrimitive()) {
			addBlockedRecipeToType(type, element.getAsString());
		}
		else {
			JsonObject blockedRecipeObject = JsonUtils.getJsonObject(element, "");

			List<String> modlist = StreamSupport
					.stream(JsonUtils.getJsonArray(blockedRecipeObject, "mods").spliterator(), false)
					.map(JsonElement::getAsString)
					.collect(Collectors.toList());
			boolean allModsPresent = JsonUtils.getBoolean(blockedRecipeObject, "all_mods_present");
			String recipeKey = JsonUtils.getString(blockedRecipeObject, "recipe_key");

			if (allModsPresent) {
				if (modlist.stream().allMatch(Loader::isModLoaded)) {
					addBlockedRecipeToType(type, recipeKey);
				}
			}
			else {
				if (modlist.stream().anyMatch(Loader::isModLoaded)) {
					addBlockedRecipeToType(type, recipeKey);
				}
			}
		}
	}

	public void addBlockedRecipeToType(RecipeType type, String recipe) {
		addBlockedRecipeToType(type, new ResourceLocation(recipe));
	}

	public void addBlockedRecipeToType(RecipeType type, ResourceLocation recipe) {
		blockedRecipes.computeIfAbsent(type, k -> new HashSet<>())
				.add(recipe);
	}

	public static boolean isRecipeBlocked(RecipeType type, ResourceLocation key) {
		return blockedRecipes.get(type) != null && blockedRecipes.get(type).contains(key);
	}
}
