package minefantasy.mfr.item;

import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.constants.WeaponClass;
import minefantasy.mfr.init.MineFantasySounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;

/**
 * @author Anonymous Productions
 */
public class ItemMace extends ItemWeaponMFR {
	private float stunChance = 0.20F;

	/**
	 * The mace does the most damage Maces also have more durability
	 * <p>
	 * These are for the player who just wants to hit stuff
	 */
	public ItemMace(String name, ToolMaterial material, Rarity rarity, float weight) {
		super(material, name, rarity, weight);
		this.setMaxDamage((int) (getMaxDamage() * 2F));
	}

	@Override
	protected int getParryDamage(float dam) {
		return (int) (dam * 2F);
	}

	@Override
	public void onProperHit(EntityLivingBase user, ItemStack weapon, Entity hit, float dam) {
		if (!user.world.isRemote && user.getRNG().nextInt(5) == 0) {
			if (hit instanceof EntityLivingBase) {
				((EntityLivingBase) hit).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 1));
			}
		}
		super.onProperHit(user, weapon, hit, dam);
	}

	@Override
	public float getDestroySpeed(ItemStack itemstack, IBlockState block) {
		return super.getDestroySpeed(itemstack, block) * 1.5F;
	}

	@Override
	public boolean playCustomParrySound(EntityLivingBase blocker, Entity attacker, ItemStack weapon) {
		blocker.world.playSound(null, blocker.getPosition(), MineFantasySounds.WOOD_PARRY, SoundCategory.NEUTRAL, 1.0F, 0.7F);
		return true;
	}

	@Override
	protected float getKnockbackStrength() {
		return 1.5F;
	}

	@Override
	public float getAttackSpeed(ItemStack item) {
		return super.getAttackSpeed(item) + speedMace;
	}

	@Override
	public float getDamageModifier() {
		return damageModMace;
	}

	@Override
	protected float[] getWeaponRatio(ItemStack implement) {
		return crushingDamage;
	}

	/**
	 * gets the time after being hit your guard will be let down
	 */
	@Override
	public int getParryCooldown(DamageSource source, float dam, ItemStack weapon) {
		return maceParryTime;
	}

	@Override
	public int getParryModifier(ItemStack weapon, EntityLivingBase user, Entity target) {
		return 40;
	}

	@Override
	protected float getStaminaMod() {
		return maceStaminaCost;
	}

	@Override
	public WeaponClass getWeaponClass() {
		return WeaponClass.BLUNT;
	}

	@Override
	public boolean canCounter() {
		return false;
	}
}
