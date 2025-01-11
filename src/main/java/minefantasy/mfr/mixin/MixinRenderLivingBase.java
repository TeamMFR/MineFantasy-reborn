package minefantasy.mfr.mixin;

import minefantasy.mfr.MineFantasyReforged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@SideOnly(Side.CLIENT)
@Mixin(value = RenderLivingBase.class)
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T> {

	protected MixinRenderLivingBase(RenderManager renderManager) {
		super(renderManager);
	}

		/**
	 * @author ThatPolishKid99
		 * Purpose: Fix a small vanilla bug where entity limb swing is disabled when riding an entity
		 * 		even if that entity returns false for shouldRiderSit(). This allows controlling Passenger limb swing if the
		 * 		ridden Entity allows shouldRiderSit()
		 * Modified: Args for limbSwingAmount and LimbSwing in the method RenderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor)
		 * 		are modified to account for shouldRiderSit() returning false.
	 */
	@ModifyArgs(method = {"doRender*"},
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderLivingBase;renderModel(Lnet/minecraft/entity/EntityLivingBase;FFFFFF)V", ordinal = 1))
	private void setRenderModelArgs(Args args) {
		EntityLivingBase entity = args.get(0);
		float limbSwing;
		float limbSwingAmount;

		float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();

		if (entity.getRidingEntity() != null && !entity.getRidingEntity().shouldRiderSit()) {
				limbSwingAmount = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
				limbSwing = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

				if (entity.isChild()) {
					limbSwing *= 3.0F;
				}

				if (limbSwingAmount > 1.0F) {
					limbSwingAmount = 1.0F;
				}

			args.set(1, limbSwing);
			args.set(2, limbSwingAmount);
		}
	}

	@ModifyArgs(method = {"doRender*"},
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderLivingBase;renderLayers(Lnet/minecraft/entity/EntityLivingBase;FFFFFFF)V", ordinal = 1))
	private void setRenderLayerArgs(Args args) {
		EntityLivingBase entity = args.get(0);
		float limbSwing;
		float limbSwingAmount;

		float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();

		if (entity.getRidingEntity() != null && !entity.getRidingEntity().shouldRiderSit()) {
			limbSwingAmount = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
			limbSwing = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

			if (entity.isChild()) {
				limbSwing *= 3F;
			}

			if (limbSwingAmount > 1.0F) {
				limbSwingAmount = 1.0F;
			}

			args.set(1, limbSwing);
			args.set(2, limbSwingAmount);
		}
	}
}
