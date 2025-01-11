package minefantasy.mfr.mixin;

import minefantasy.mfr.config.ConfigClient;
import minefantasy.mfr.item.ItemHalbeard;
import minefantasy.mfr.item.ItemHeavyWeapon;
import minefantasy.mfr.item.ItemKatana;
import minefantasy.mfr.item.ItemSpear;
import minefantasy.mfr.item.ItemWaraxe;
import minefantasy.mfr.item.ItemWarhammer;
import minefantasy.mfr.item.ItemWeaponMFR;
import minefantasy.mfr.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Inspiration/Author Credit: Fuzs_ in the Golden Age Combat mod/Sword Blocking Mechanics mod
 * Github Link: <a href="https://github.com/Fuzss/swordblockingmechanics/tree/1.12">https://github.com/Fuzss/swordblockingmechanics/tree/1.12</a>
 * Modified from Fuzs_'s work and used under the free use public license, thank you so much to Fuzs_!
 */
@SideOnly(Side.CLIENT)
@Mixin(value = LayerHeldItem.class)
public abstract class MixinLayerHeldItem implements LayerRenderer<EntityLivingBase> {

	@Shadow
	abstract void translateToHand(EnumHandSide side);

	@Inject(at = @At("HEAD"),
			method = "Lnet/minecraft/client/renderer/entity/layers/LayerHeldItem;renderHeldItem("
				+ "Lnet/minecraft/entity/EntityLivingBase;"
				+ "Lnet/minecraft/item/ItemStack;"
				+ "Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;"
				+ "Lnet/minecraft/util/EnumHandSide;)V",
			cancellable = true)
	private void renderHeldItem(EntityLivingBase entityLivingBase, ItemStack stack, ItemCameraTransforms.TransformType transform, EnumHandSide handSide, CallbackInfo ci) {

		if (!stack.isEmpty()) {

			if (entityLivingBase instanceof EntityPlayer && stack.getItem() instanceof ItemWeaponMFR) {
				GlStateManager.pushMatrix();
				boolean leftHand = handSide == EnumHandSide.LEFT;
				if (entityLivingBase.isSneaking()) {

					GlStateManager.translate(0.0F, 0.2F, 0.0F);
				}

				// Forge: moved this call down, fixes incorrect offset while sneaking.
				translateToHand(handSide);
				if (entityLivingBase.isHandActive() && PlayerUtils.shouldItemStackBlock(stack, entityLivingBase.getHeldItemOffhand()) && entityLivingBase.getActiveHand() == (leftHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND)) {
					GlStateManager.translate((float) (leftHand ? 1 : -1) / 16.0F, 0.4375F, 0.0625F);

					// blocking
					GlStateManager.translate(leftHand ? -0.035F : 0.05F, leftHand ? 0.045F : 0.0F, leftHand ? -0.135F : -0.1F);
					GlStateManager.rotate((leftHand ? -1 : 1) * -50.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(-10.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate((leftHand ? -1 : 1) * -60.0F, 0.0F, 0.0F, 1.0F);

					// old item layer
					GlStateManager.translate(0.0F, 0.1875F, 0.0F);
					GlStateManager.scale(0.625F, -0.625F, 0.625F);
					GlStateManager.rotate(-100.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate(leftHand ? 35.0F : 45.0F, 0.0F, 1.0F, 0.0F);

					// old item renderer
					GlStateManager.translate(0.0F, -0.3F, 0.0F);
					GlStateManager.scale(1.5F, 1.5F, 1.5F);
					GlStateManager.rotate(50.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(335.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.translate(-0.9375F, -0.0625F, 0.0F);
					GlStateManager.translate(0.5F, 0.5F, 0.25F);
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.translate(0.0F, 0.0F, 0.28125F);

					applyTransformReverse(new ItemTransformVec3f(new Vector3f(0.0F, (leftHand ? 1 : -1) * 90.0F, (leftHand ? -1 : 1) * 55.0F), new Vector3f(0.0F, 0.25F, 0.03125F), new Vector3f(0.85F, 0.85F, 0.85F)), leftHand);
				} else {
					if (ConfigClient.shouldUseMfrCustomAnimations) {
						performCounterAttackAnimation((EntityPlayer) entityLivingBase, stack, leftHand);
					}
					GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.translate((float) (leftHand ? -1 : 1) / 16.0F, 0.125F, -0.625F);
				}

				Minecraft.getMinecraft().getItemRenderer().renderItemSide(entityLivingBase, stack, transform, leftHand);
				GlStateManager.popMatrix();
				ci.cancel();
			}
		}

	}

	private static void applyTransformReverse(net.minecraft.client.renderer.block.model.ItemTransformVec3f vec, boolean leftHand) {

		if (vec != net.minecraft.client.renderer.block.model.ItemTransformVec3f.DEFAULT) {

			int i = leftHand ? -1 : 1;
			GlStateManager.scale(1.0F / vec.scale.x, 1.0F / vec.scale.y, 1.0F / vec.scale.z);
			float x = vec.rotation.x;
			float y = vec.rotation.y;
			float z = vec.rotation.z;

			if (leftHand) {

				y = -y;
				z = -z;
			}

			Quaternion quat = makeQuaternion(x, y, z);
			GlStateManager.rotate(quat.negate(quat));
			GlStateManager.translate((float) i * (-vec.translation.x), -vec.translation.y, -vec.translation.z);
		}
	}

	private static Quaternion makeQuaternion(float p_188035_0_, float p_188035_1_, float p_188035_2_) {

		float f = p_188035_0_ * 0.017453292F;
		float f1 = p_188035_1_ * 0.017453292F;
		float f2 = p_188035_2_ * 0.017453292F;
		float f3 = MathHelper.sin(0.5F * f);
		float f4 = MathHelper.cos(0.5F * f);
		float f5 = MathHelper.sin(0.5F * f1);
		float f6 = MathHelper.cos(0.5F * f1);
		float f7 = MathHelper.sin(0.5F * f2);
		float f8 = MathHelper.cos(0.5F * f2);

		return new Quaternion(f3 * f6 * f8 + f4 * f5 * f7, f4 * f5 * f8 - f3 * f6 * f7, f3 * f5 * f8 + f4 * f6 * f7, f4 * f6 * f8 - f3 * f5 * f7);
	}

	private void performCounterAttackAnimation(EntityPlayer player, ItemStack stack, boolean leftHand) {
		boolean hasParried = false;
		//Parry Animations
		if (player != null && !stack.isEmpty()) {
			hasParried = ItemWeaponMFR.canCounter(player, stack) == 1;
		}

		if (hasParried) {
			Item item = stack.getItem();
			if (item instanceof ItemWaraxe) {
				GlStateManager.rotate(180F, 0F, 0F, 1F);
				GlStateManager.translate(leftHand ? -0.1F : 0.1F, -1.1F, 0F);
			}
			else if (item instanceof ItemSpear && !(item instanceof ItemHalbeard)) {
				GlStateManager.translate(leftHand ? 0.1F : -0.1F, 0.1F, leftHand ? -0.5F : -0.4F);
				GlStateManager.rotate((leftHand ? -1F : 1F) * -50.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(-25.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate((leftHand ? -1F : 1F) * -60.0F, 0.0F, 0.0F, 1.0F);
			}
			else if (item instanceof ItemHeavyWeapon) {
				if (item instanceof ItemKatana) {
					GlStateManager.rotate(180, 1F, 0F, 0F);
					GlStateManager.translate(0F, -1F, 0.1F);
				}
				else if (item instanceof ItemWarhammer) {
					GlStateManager.rotate(180F, 0F, 0F, 1F);
					GlStateManager.translate(leftHand ? -0.1F : 0.1F, -1.1F, 0F);
				}
				else { //Greatsword, Battleaxe
					GlStateManager.rotate(-100, 1F, 0F, 0F);
					GlStateManager.translate(0F, -0.5F,  0.8F);
				}
			}
			else { //Sword, Mace, Halberd
				GlStateManager.rotate(80, 1F, 0F, 0F);
				GlStateManager.translate(0F, -0.4F, -0.4F);
			}
		}
	}
}
