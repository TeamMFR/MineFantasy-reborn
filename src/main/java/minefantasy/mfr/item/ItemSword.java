package minefantasy.mfr.item;

import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.constants.WeaponClass;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

/**
 * @author Anonymous Productions
 */
public class ItemSword extends ItemWeaponMFR {
	public ItemSword(String name, ToolMaterial material, Rarity rarity, float weight) {
		super(material, name, rarity, weight);
	}

	@Override
	protected int getParryDamage(float dam) {
		return 1;
	}

	/**
	 * Gets the angle the weapon can parry
	 */
	@Override
	public float getParryAngleModifier(EntityLivingBase user) {
		return 1.0F;
	}

	/**
	 * Gets the multiplier for the parry threshold
	 */
	@Override
	public float getParryDamageModifier(EntityLivingBase user) {
		return user instanceof EntityPlayer ? 2.0F : 1.5F;
	}

	/**
	 * Determines if the weapon can do those cool ninja evades
	 */
	@Override
	protected boolean canAnyMobParry() {
		return true;
	}

	@Override
	public float getAttackSpeed(ItemStack item) {
		return super.getAttackSpeed(item) + speedSword;
	}

	/**
	 * gets the time after being hit your guard will be let down
	 */
	@Override
	public int getParryCooldown(DamageSource source, float dam, ItemStack weapon) {
		return swordParryTime;
	}

	@Override
	protected float getStaminaMod() {
		return swordStaminaCost;
	}

	@Override
	public WeaponClass getWeaponClass() {
		return WeaponClass.BLADE;
	}
}
