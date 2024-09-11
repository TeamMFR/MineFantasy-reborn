package minefantasy.mfr.api.heating;

import net.minecraft.item.ItemStack;

public class FirepitFuel {
    private String name;

    public ItemStack item;
    public float burnTime;
    public int baseHeat;
    /**
     * Applied to fuels that will auto-light the firepit when added
     */
    public boolean doesLight;

    public FirepitFuel(ItemStack item, float dura, int heat, boolean doesLight){
        this.item = item;
        this.burnTime = dura;
        this.baseHeat = heat;
        this.doesLight = doesLight;
    }
}
