package minefantasy.mfr.mechanics;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.weapon.IExtendedReachWeapon;
import minefantasy.mfr.network.ExtendedReachPacket;
import minefantasy.mfr.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MineFantasyReforged.MOD_ID)
public class ExtendedReach {

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public static void onEvent(MouseEvent event) {
		// ensure custom MouseHelper is active
		Minecraft mc = Minecraft.getMinecraft();

		int attackKeybinding = Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode() + 100;

		if (event.getButton() == attackKeybinding && event.isButtonstate()) {
			EntityPlayer player = mc.player;
			if (player != null) {
				ItemStack itemstack = player.getHeldItemMainhand();
				IExtendedReachWeapon extendedReachWeapon;
				if (!itemstack.isEmpty()) {
					if (itemstack.getItem() instanceof IExtendedReachWeapon) {
						extendedReachWeapon = (IExtendedReachWeapon) itemstack.getItem();
					} else {
						extendedReachWeapon = null;
					}

					if (extendedReachWeapon != null) {
						float reach = extendedReachWeapon.getReachModifierInBlocks() + 4;
						RayTraceResult mouseOverExtended = getMouseOverExtended(reach);

						if (mouseOverExtended != null) {
							if (mouseOverExtended.entityHit != null && mouseOverExtended.entityHit.hurtResistantTime == 0) {
								if (mouseOverExtended.entityHit != player) {
									NetworkHandler.sendToServer(new ExtendedReachPacket(mouseOverExtended.entityHit.getEntityId()));
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the mouse over extended.
	 *
	 * @param dist the dist
	 * @return the mouse over extended
	 */
	// This is mostly copied from the EntityRenderer#getMouseOver() method
	public static RayTraceResult getMouseOverExtended(float dist) {
		Minecraft mc = FMLClientHandler.instance().getClient();
		Entity theRenderViewEntity = mc.getRenderViewEntity();
		AxisAlignedBB theViewBoundingBox = new AxisAlignedBB(
				theRenderViewEntity.posX - 0.5D,
				theRenderViewEntity.posY - 0.0D,
				theRenderViewEntity.posZ - 0.5D,
				theRenderViewEntity.posX + 0.5D,
				theRenderViewEntity.posY + 1.5D,
				theRenderViewEntity.posZ + 0.5D);
		RayTraceResult returnMOP = null;
		if (mc.world != null) {
			double var2 = dist;
			returnMOP = theRenderViewEntity.rayTrace(var2, 0);
			double calcdist = var2;
			Vec3d pos = theRenderViewEntity.getPositionEyes(0);
			var2 = calcdist;
			if (returnMOP != null) {
				calcdist = returnMOP.hitVec.distanceTo(pos);
			}

			Vec3d lookvec = theRenderViewEntity.getLook(0);
			Vec3d var8 = pos.add(lookvec.x * var2, lookvec.y * var2, lookvec.z * var2);
			Entity pointedEntity = null;
			float var9 = 1.0F;
			List<Entity> list = mc.world.getEntitiesWithinAABBExcludingEntity(theRenderViewEntity,
					theViewBoundingBox.expand(lookvec.x * var2, lookvec.y * var2, lookvec.z * var2).grow(var9, var9, var9));
			double d = calcdist;

			for (Entity entity : list) {
				if (entity.canBeCollidedWith()) {
					float bordersize = entity.getCollisionBorderSize();
					AxisAlignedBB aabb = new AxisAlignedBB(entity.posX - entity.width / 2, entity.posY, entity.posZ - entity.width / 2,
							entity.posX + entity.width / 2, entity.posY + entity.height, entity.posZ + entity.width / 2);
					aabb.grow(bordersize, bordersize, bordersize);
					RayTraceResult mop0 = aabb.calculateIntercept(pos, var8);

					if (aabb.contains(pos)) {
						if (0.0D < d || d == 0.0D) {
							pointedEntity = entity;
							d = 0.0D;
						}
					} else if (mop0 != null) {
						double d1 = pos.distanceTo(mop0.hitVec);

						if (d1 < d || d == 0.0D) {
							pointedEntity = entity;
							d = d1;
						}
					}
				}
			}

			if (pointedEntity != null && (d < calcdist || returnMOP == null)) {
				returnMOP = new RayTraceResult(pointedEntity);
			}

		}
		return returnMOP;
	}

}
