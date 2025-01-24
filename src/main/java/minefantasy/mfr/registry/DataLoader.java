package minefantasy.mfr.registry;

import com.google.gson.JsonObject;
import minefantasy.mfr.MineFantasyReforged;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.File;

public abstract class DataLoader {

	public void loadRegistry(String type, String defaultDirectory, String configDirectory) {
		ModContainer modContainer = Loader.instance().activeModContainer();

		MineFantasyReforged.LOG.info("Loading " + type + " registry entries from config directory");
		loadRegistryFiles(modContainer, new File(configDirectory), "", type);

		MineFantasyReforged.LOG.info("Loading default " + type + " registry entries");
		Loader.instance().getActiveModList().forEach(m ->
				loadRegistryFiles(m, m.getSource(), String.format(defaultDirectory, m.getModId()), type));
	}

	public abstract void loadRegistryFiles(ModContainer mod, File source, String base, String type);

	protected abstract void parse(String name, JsonContext context, JsonObject json, String type);
}
