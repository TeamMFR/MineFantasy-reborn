package minefantasy.mfr.item;

import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.mechanics.knowledge.IArtefact;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;

public class ItemJewel extends ItemBaseMFR implements IArtefact {
	public static final String MYTHIC = "mythic_artefacts";
	public static final String DWARVEN = "dwarven_artefacts";
	public static final String GNOMISH = "gnomish_artefacts";

	public final int studyTime;
	public final IRarity rarity;
	public final String[] researches;
	public final String lootType;
	public final int dropWeight;

	public ItemJewel(String name, IRarity rarity, String lootType, int dropChance) {
		this(name, 0, rarity, lootType, dropChance);
	}

	public ItemJewel(String name, int studyTime, IRarity rarity, String lootType, int dropChance, String... researches) {
		super(name);
		this.studyTime = studyTime;
		this.rarity = rarity;
		this.researches = researches;
		this.lootType = lootType;
		this.dropWeight = dropChance;

		this.setCreativeTab(MineFantasyTabs.tabMaterials);
	}

	@Override
	public IRarity getForgeRarity(ItemStack stack) {
		return rarity;
	}

	@Override
	public int getStudyTime(ItemStack item) {
		return studyTime;
	}

	@Override
	public String[] getResearches() {
		return researches;
	}
}
