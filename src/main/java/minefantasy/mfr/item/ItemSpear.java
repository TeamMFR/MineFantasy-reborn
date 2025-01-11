package minefantasy.mfr.item;

import minefantasy.mfr.api.weapon.IExtendedReachWeapon;
import minefantasy.mfr.client.render.item.RenderSpear;
import minefantasy.mfr.config.ConfigWeapon;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.constants.WeaponClass;
import minefantasy.mfr.init.MineFantasySounds;
import minefantasy.mfr.util.ModelLoaderHelper;
import minefantasy.mfr.util.TacticalManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
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
public class ItemSpear extends ItemWeaponMFR implements IExtendedReachWeapon {
	/**
	 * The spear is for the defensive player, it has a long reach, knockback and can
	 * be thrown. Spears are good for keeping enemies at a distance, Parrying is
	 * easier when sneaking
	 * <p>
	 * These are for the defensive player
	 */
	public ItemSpear(String name, ToolMaterial material, Rarity rarity, float weight) {
		super(material, name, rarity, weight);
	}

	@Override
	public boolean allowOffhand(EntityLivingBase entity, EnumHand hand) {
		ItemStack offhandStack = entity.getHeldItem(hand);
		return offhandStack.getItem().isShield(offhandStack, entity) || offhandStack.isEmpty();
	}

	@Override
	public boolean isHeavyWeapon() {
		return true;
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
	public void addInformation(ItemStack item, World world, List<String> list, ITooltipFlag flag) {
		super.addInformation(item, world, list, flag);

		if (getMountedDamage() > 0) {
			list.add(TextFormatting.DARK_GREEN + I18n.format(
					"attribute.modifier.plus." + 0, decimal_format.format(getMountedDamage()),
					I18n.format("attribute.weapon.mountedBonus")));
		}
	}

	@Override
	public float modifyDamage(ItemStack item, EntityLivingBase wielder, Entity hit, float initialDam, boolean properHit) {
		float damage = super.modifyDamage(item, wielder, hit, initialDam, properHit);

		EntityLivingBase target = (EntityLivingBase) hit;

		if (wielder instanceof EntityPlayer && wielder.isRiding() && tryPerformAbility(wielder, charge_cost)) {
			ItemWaraxe.brutalise(wielder, target, 1.0F);
			return damage + getMountedDamage();
		}
		if (!wielder.isRiding() && wielder.isSprinting()) {
			if (this instanceof ItemHalbeard) {
				return Math.max(damage / 1.25F, 1.0F);
			} else {
				return damage * 1.25F;
			}
		}
		return damage;
	}

	@Override
	public boolean playCustomParrySound(EntityLivingBase blocker, Entity attacker, ItemStack weapon) {
		blocker.world.playSound(null, blocker.getPosition(), MineFantasySounds.WOOD_PARRY, SoundCategory.NEUTRAL, 1.0F, 0.7F);
		return true;
	}

	@Override
	public void onParry(DamageSource source, EntityLivingBase user, Entity attacker, float dam) {
		super.onParry(source, user, attacker, dam);
		if (ConfigWeapon.useBalance && user instanceof EntityPlayer) {
			TacticalManager.throwPlayerOffBalance((EntityPlayer) user, getBalance());
		}
	}

	protected float getMountedDamage() {
		if (material == Item.ToolMaterial.WOOD) {
			return 0;
		}
		return 4F;
	}

	@Override
	protected int getParryDamage(float dam) {
		return (int) dam;
	}

	/**
	 * Gets the angle the weapon can parry
	 */
	@Override
	public float getParryAngleModifier(EntityLivingBase user) {
		return user.isSneaking() ? 1.2F : 0.85F;
	}

	@Override
	public float getBalance() {
		return 0.5F;
	}

	@Override
	protected float getKnockbackStrength() {
		return 2.5F;
	}

	@Override
	protected boolean canAnyMobParry() {
		return false;
	}

	@Override
	public float getAttackSpeed(ItemStack item) {
		return super.getAttackSpeed(item) + speedSpear;
	}

	/**
	 * gets the time after being hit your guard will be let down
	 */
	@Override
	public int getParryCooldown(DamageSource source, float dam, ItemStack weapon) {
		return spearParryTime;
	}

	@Override
	protected float getStaminaMod() {
		return spearStaminaCost;
	}

	@Override
	public WeaponClass getWeaponClass() {
		return WeaponClass.POLEARM;
	}

	@Override
	protected float[] getWeaponRatio(ItemStack implement) {
		return piercingDamage;
	}

	@Override
	public boolean canCounter() {
		return true;
	}

	@Override
	public float[] getCounterRatio() {
		return crushingDamage;
	}

	@Override
	public float getScale(ItemStack itemstack) {
		return 3.0F;
	}

	@Override
	public float getOffsetX(ItemStack itemstack) {
		return -0.2F;
	}

	@Override
	public float getOffsetY(ItemStack itemstack) {
		return 3F;
	}

	@Override
	public float getOffsetZ(ItemStack itemstack) {
		return -0.3F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
		ModelLoaderHelper.registerWrappedItemModel(this, new RenderSpear(() -> modelLocation), modelLocation);
	}
}