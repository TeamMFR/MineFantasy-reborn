package minefantasy.mfr.item;

import minefantasy.mfr.constants.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ItemMultiFood extends ItemFoodMFR {

	public ItemMultiFood(String name, int bites, int hunger, float saturation, boolean meat, Rarity rarity) {
		super(name, hunger, saturation, meat, rarity);
		setMaxStackSize(1);
		setMaxDamage(bites - 1);

		this.addPropertyOverride(new ResourceLocation("bites"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return getDamage(stack);
			}
		});
	}

	@Override
	public boolean isRepairable() {
		return false;
	}
}
