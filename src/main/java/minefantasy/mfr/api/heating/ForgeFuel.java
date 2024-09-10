package minefantasy.mfr.api.heating;

import net.minecraft.item.ItemStack;

public class ForgeFuel {

	private String name;

	public ItemStack item;
	public float burnTime;
	public int baseHeat;
	/**
	 * Applied to lava, auto-lights the forge when placed
	 */
	public boolean doesLight;
	/**
	 * Some sources only accept refined fuel
	 */
	public boolean isRefined;

	public ForgeFuel(ItemStack item, float dura, int heat, boolean light, boolean refined) {
		this.item = item;
		this.burnTime = dura;
		this.baseHeat = heat;
		this.doesLight = light;
		this.isRefined = refined;
	}
}
