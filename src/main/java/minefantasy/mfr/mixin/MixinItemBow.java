package minefantasy.mfr.mixin;

import minefantasy.mfr.item.ItemArrowMFR;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBow.class)
public class MixinItemBow {
	@Inject(at = @At("HEAD"),
			method = "Lnet/minecraft/item/ItemBow;isArrow(Lnet/minecraft/item/ItemStack;)Z",
			cancellable = true)
	protected void isArrow(ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
		if (stack.getItem() instanceof ItemArrowMFR) {
			callback.setReturnValue(false);
		}
	}
}
