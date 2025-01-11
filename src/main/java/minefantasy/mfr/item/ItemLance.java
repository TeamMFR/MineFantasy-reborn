package minefantasy.mfr.item;

import minefantasy.mfr.client.render.item.RenderLance;
import minefantasy.mfr.config.ConfigStamina;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.mechanics.StaminaBar;
import minefantasy.mfr.util.ModelLoaderHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * @author Anonymous Productions
 */
@Optional.Interface(iface = "net.shadowmage.ancientwarfare.npc.item.IExtendedReachWeapon", modid = "ancientwarfarenpc")
public class ItemLance extends ItemSpear {
	public ItemLance(String name, ToolMaterial material, Rarity rarity, float weight) {
		super(name, material, rarity, weight);
		setMaxDamage(getMaxDamage(new ItemStack(this)) * 2);
	}

	@Override
	public boolean allowOffhand(EntityLivingBase entity, EnumHand hand) {
		return entity.getHeldItem(hand).getItem() instanceof ItemShield || entity.getHeldItem(hand).isEmpty();
	}

	@Override
	public void addInformation(ItemStack weapon, World world, List<String> list, ITooltipFlag flag) {
		super.addInformation(weapon, world, list, flag);

		list.add(TextFormatting.BLUE + I18n.format("attribute.modifier.plus." + 0,
				decimal_format.format(getJoustDamage(weapon)),
				I18n.format("attribute.weapon.joustDam")));
	}

	@Override
	public float getReachModifierInBlocks() {
		return 3.0F;
	}

	@Optional.Method(modid = "ancientwarfarenpc")
	public float getReach() {
		return 7.0F;
	}

	@Override
	protected float[] getWeaponRatio(ItemStack implement) {
		return heavyPiercingDamage;
	}

	@Override
	public float modifyDamage(ItemStack item, EntityLivingBase wielder, Entity hit, float initialDam, boolean properHit) {
		float dam = super.modifyDamage(item, wielder, hit, initialDam, properHit);
		if (hit instanceof EntityLivingBase) {
			return joust((EntityLivingBase) hit, wielder, dam);
		}
		return dam;
	}

	@Override
	public boolean canWeaponParry() {
		return false;
	}

	@Override
	public boolean canCounter() {
		return false;
	}

	// Higher stamina means more precice hits: Full stamina hits are perfect
	@Override
	public float getBalance(EntityLivingBase user) {
		if (ConfigStamina.isSystemActive) {
			return 0.0F + (2 * (1 - StaminaBar.getStaminaDecimal(user)));
		}
		return 0.0F;
	}

	@Override
	protected float getKnockbackStrength() {
		return 5.0F;
	}

	@Override
	protected float getStaminaMod() {
		return 5.0F;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack item) {
		return EnumAction.NONE;
	}

	public float joust(EntityLivingBase target, EntityLivingBase attacker, float dam) {
		float speedCap = 5F;

		if (attacker.isRiding()) {
			float speed = (float) (Math.hypot(attacker.moveForward, attacker.moveStrafing) * 3.5);
			if (speed > speedCap)
				speed = speedCap;

			dam += (getJoustDamage(attacker.getHeldItemMainhand()) / speedCap) * speed;

			if (attacker instanceof EntityPlayer) {
				((EntityPlayer) attacker).onCriticalHit(target);
			}

			if (target.isRiding() && speed > (speedCap / 2) && speed != speedCap) {
				target.dismountRidingEntity();
			}
		}
		return dam;
	}

	@Override
	public float getAttackSpeed(ItemStack item) {
		return super.getAttackSpeed(item) + -1F;
	}

	@Override
	protected float getMeleeDamage(ItemStack item) {
		return 2F;
	}

	protected float getJoustDamage(ItemStack item) {
		return super.getMeleeDamage(item) * 2.5F;
	}

	@Override
	public float getOffsetX(ItemStack itemstack) {
		return -0.2F;
	}

	@Override
	public float getOffsetZ(ItemStack itemstack) {
		return -0.3F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
		ModelLoaderHelper.registerWrappedItemModel(this, new RenderLance(() -> modelLocation), modelLocation);
	}
}
