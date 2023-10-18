package minefantasy.mfr.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.factories.QuernRecipeFactory;
import minefantasy.mfr.recipe.types.QuernRecipeType;
import minefantasy.mfr.util.FileUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CraftingManagerQuern {

	public static final String CONFIG_RECIPE_DIRECTORY = "config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/quern_recipes/";

	public CraftingManagerQuern() {

	}

	private static final IForgeRegistry<QuernRecipeBase> QUERN_RECIPES = (new RegistryBuilder<QuernRecipeBase>()).setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "quern_recipes")).setType(QuernRecipeBase.class).setMaxID(Integer.MAX_VALUE >> 5).disableSaving().allowModification().create();

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<QuernRecipeBase> getRecipes() {
		return QUERN_RECIPES.getValuesCollection();
	}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final QuernRecipeFactory factory = new QuernRecipeFactory();
	private static final Method LOAD_CONSTANTS = ReflectionHelper.findMethod(JsonContext.class, "loadConstants", null, JsonObject[].class);

	public static void loadRecipes() {
		ModContainer modContainer = Loader.instance().activeModContainer();

		FileUtils.createCustomDataDirectory(CONFIG_RECIPE_DIRECTORY);
		Loader.instance().getActiveModList().forEach(m -> CraftingHelper.loadFactories(m,"assets/" + m.getModId() + "/quern_recipes", CraftingHelper.CONDITIONS));
		//noinspection ConstantConditions
		loadRecipes(modContainer, new File(CONFIG_RECIPE_DIRECTORY), "");
		Loader.instance().getActiveModList().forEach(m -> CraftingManagerQuern.loadRecipes(m, m.getSource(), "assets/" + m.getModId() + "/quern_recipes"));

		Loader.instance().setActiveModContainer(modContainer);
	}

	private static void loadRecipes(ModContainer mod, File source, String base) {
		JsonContext ctx = new JsonContext(mod.getModId());

		FileUtils.findFiles(source, base, root -> {
			Path fPath = root.resolve("_constants.json");
			if (fPath != null && Files.exists(fPath)) {
				BufferedReader reader = null;
				try {
					reader = Files.newBufferedReader(fPath);
					JsonObject[] json = JsonUtils.fromJson(GSON, reader, JsonObject[].class);
					LOAD_CONSTANTS.invoke(ctx, new Object[] {json});
				}
				catch (IOException | IllegalAccessException | InvocationTargetException e) {
					MineFantasyReforged.LOG.error("Error loading _constants.json: ", e);
					return false;
				}
				finally {
					IOUtils.closeQuietly(reader);
				}
			}
			return true;
		}, (root, file) -> {
			Loader.instance().setActiveModContainer(mod);

			String relative = root.relativize(file).toString();
			if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
				return;

			String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
			ResourceLocation key = new ResourceLocation(ctx.getModId(), name);

			BufferedReader reader = null;
			try {
				reader = Files.newBufferedReader(file);
				JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);

				String type = ctx.appendModId(JsonUtils.getString(json, "type"));
				if (Loader.isModLoaded(mod.getModId())) {
					if (QuernRecipeType.getByNameWithModId(type, mod.getModId()) != QuernRecipeType.NONE) {
						QuernRecipeBase recipe = factory.parse(ctx, json);
						recipe.setRegistryName(key);
						if (CraftingHelper.processConditions(json, "conditions", ctx)) {
							addRecipe(recipe, mod.getModId().equals(MineFantasyReforged.MOD_ID));
						}
					} else {
						MineFantasyReforged.LOG.info("Skipping recipe {} of type {} because it's not a MFR Quern recipe", key, type);
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
		});
	}

	public static void addRecipe(QuernRecipeBase recipe, boolean checkForExistence) {
		ItemStack itemStack = recipe.getQuernRecipeOutput();
		if (ConfigCrafting.isQuernItemCraftable(itemStack)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getQuernRecipeOutput().isItemEqual(s))
					&& (!checkForExistence || !QUERN_RECIPES.containsKey(recipe.getRegistryName()))) {
				QUERN_RECIPES.register(recipe);
			}
		}
	}

	public static QuernRecipeBase findMatchingRecipe(ItemStack input, ItemStack potInput) {
		//// Normal, registered recipes.

		for (QuernRecipeBase rec : getRecipes()) {
			if (rec.matches(input, potInput)) {
				return rec;
			}
		}
		return null;
	}

	public static boolean findMatchingInputs(ItemStack input) {
		for (QuernRecipeBase rec : getRecipes()) {
			if (rec.inputMatches(input)) {
				return true;
			}
		}
		return false;
	}

	public static boolean findMatchingPotInputs(ItemStack potInputs) {
		for (QuernRecipeBase rec : getRecipes()) {
			if (rec.inputPotMatches(potInputs)) {
				return true;
			}
		}
		return false;
	}

	public static QuernRecipeBase getRecipeByName(String name) {
		ResourceLocation resourceLocation = new ResourceLocation(MineFantasyReforged.MOD_ID + ":" + name);
		if (!QUERN_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Quern Recipe Registry does not contain recipe: {}", name);
		}
		return QUERN_RECIPES.getValue(resourceLocation);
	}

	public static List<QuernRecipeBase> getRecipesByName(String... names) {
		List<QuernRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(name));
		}
		return recipes;
	}
}
