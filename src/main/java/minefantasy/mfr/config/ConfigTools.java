package minefantasy.mfr.config;

public class ConfigTools extends ConfigurationBaseMF {
	public static final String CATEGORY_BONUS = "Bonuses";
	public static final String CATEGORY_PENALTIES = "Penalties";
	public static final String CATEGORY_INCOMPATIBILITY = "Incompatibilities";
	public static float handpickBonus;
	public static boolean handpickFortune;
	public static int lumberAxeMaxLogs;
	public static String[] lumberAxeIncompatibleModList = new String[0];
	public static float heavy_tool_drop_chance;

	public ConfigTools(String name) {
		super(name);
	}

	@Override
	protected void initializeCategories() {
		config.addCustomCategoryComment(CATEGORY_BONUS, "Controls Tool Bonuses");
		config.addCustomCategoryComment(CATEGORY_PENALTIES, "Controls Tool Penalties");
		config.addCustomCategoryComment(CATEGORY_INCOMPATIBILITY, "Controls Tool Incompatibilities");
	}

	@Override
	protected void initializeValues() {
		handpickBonus = Float.parseFloat(config.get(CATEGORY_BONUS, "Handpick double-drop chance", 20F,
				"This is the percent value (0-100) handpicks double item drops").getString());

		handpickFortune = Boolean.getBoolean(config.get(CATEGORY_BONUS, "Fortune enchantment for handpick", false,
				"Be careful, may ruin balance!").getString());

		lumberAxeMaxLogs = Integer.parseInt(config.get(CATEGORY_BONUS, "Lumber Axe max log break limit", 150,
						"This is the total amount of log blocks the lumber axe can break, regardless of durability")
				.getString());

		heavy_tool_drop_chance = Float.parseFloat(config.get(CATEGORY_PENALTIES, "Heavy ruin-drop chance", 25F,
				"This is the percent value (0-100) heavy picks/shovels and lumber axe do NOT drop blocks on break")
				.getString());


		lumberAxeIncompatibleModList = config.get(
				CATEGORY_INCOMPATIBILITY,
				"Lumberaxe Mod Incompatibility List",
				new String[]{"dynamictrees"},
				"This is a list of Mod Ids that should cause the lumberaxe to not try to break the whole tree.\nAny of these mods being loaded will cause this effect.")
				.getStringList();
	}
}