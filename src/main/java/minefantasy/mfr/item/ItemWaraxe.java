package minefantasy.mfr.item;

import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.constants.WeaponClass;
import minefantasy.mfr.init.MineFantasySounds;
import minefantasy.mfr.util.TacticalManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;

/**
 * @author Anonymous Productions
 */
public class ItemWaraxe extends ItemWeaponMFR {
	private float injuryChance = 0.05F;

	/**
	 * The waraxe is for brutal players. It does more damage and a higher bonus when
	 * sprinting or jumping. Waraxes are pure damage
	 * <p>
	 * These are for the brutal player
	 */
	public ItemWaraxe(String name, ToolMaterial material, Rarity rarity, float weight) {
		super(material, name, rarity, weight);
	}

	public static void brutalise(EntityLivingBase entityHitting, EntityLivingBase entityHit, float power) {
		if (!entityHit.world.isRemote) {
			if (entityHitting instanceof EntityPlayer) {
				((EntityPlayer) entityHitting).onCriticalHit(entityHit);
			}
		}
	}

	@Override
	public float modifyDamage(ItemStack item, EntityLivingBase wielder, Entity hit, float initialDam,
			boolean properHit) {
		float damage = super.modifyDamage(item, wielder, hit, initialDam, properHit);
		if (!(hit instanceof EntityLivingBase)) {
			return damage;
		}
		EntityLivingBase target = (EntityLivingBase) hit;

		if (wielder.isSprinting() && wielder instanceof EntityPlayer) {
			if (wielder.fallDistance > 0 && canLunge(wielder)
					&& tryPerformAbility(wielder, lunge_cost, !properHit, true, true, true))// LUNGE: 20 Points
			{
				return chargeAt(wielder, target, true, damage, properHit);
			} else if (wielder.onGround && getHeightGap(target, wielder) <= 0.5F
					&& tryPerformAbility(wielder, charge_cost))// CHARGE: 10 points
			{
				return chargeAt(wielder, target, false, damage, properHit);
			}
		} else if (wielder.fallDistance > 0 && !wielder.isOnLadder() && tryPerformAbility(wielder, jump_cost))// JUMP: 2
		// points
		{
			if (properHit)
				brutalise(wielder, target, 1.0F);
			return damage * 1.25F;
		}
		return damage;
	}

	private float chargeAt(EntityLivingBase entityHitting, EntityLivingBase entityHit, boolean lunge, float dam,
			boolean properHit) {
		if (properHit)
			brutalise(entityHitting, entityHit, 1.0F);

		if (lunge) {
			float fallBonus = (Math.min(entityHitting.fallDistance, getMeleeDamage(entityHitting.getHeldItemMainhand()))) / 2F;
			if (entityHit.onGround && !properHit) {
				entityHitting.hurtResistantTime = 20;
				TacticalManager.knockbackEntity(entityHit, entityHitting, 4F, 0.5F);
				TacticalManager.lungeEntity(entityHitting, entityHit, 2F, 0.1F);
			}
			if (properHit) {
				if (!entityHit.isPotionActive(MobEffects.SLOWNESS)) {
					entityHit.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 400, 3));
					entityHit.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 1));
				}

				entityHitting.fallDistance -= fallBonus;
			}
			if (fallBonus > 0) {
				dam += fallBonus;
			}
		} else {
			if (properHit) {
				if (entityHitting.onGround && entityHit.onGround && !properHit) {
					entityHitting.hurtResistantTime = 20;
					TacticalManager.lungeEntity(entityHitting, entityHit, 1F, 0.0F);
				}
				if (properHit) {
					if (!entityHit.isPotionActive(MobEffects.SLOWNESS)) {
						entityHit.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 3));
						entityHit.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 100, 0));
					}
				}
			}
		}
		return dam * 1.25F;
	}

	@Override
	public void onProperHit(EntityLivingBase user, ItemStack weapon, Entity hit, float dam) {
		if (rand.nextFloat() < injuryChance && dam > 0) {
			hit.getEntityData().setInteger(Constants.INJURED_TAG, 200 + rand.nextInt(600));
		}
		super.onProperHit(user, weapon, hit, dam);
	}

	/**
	 * Determines if sprint-jump lunges work
	 */
	private boolean canLunge(EntityLivingBase entityHitting) {
		return true;
	}

	private double getHeightGap(EntityLivingBase entityHit, EntityLivingBase entityHitting) {
		double a = entityHitting.posY - entityHit.posY;
		return Math.max(a, -a);
	}

	@Override
	public float getDestroySpeed(ItemStack itemstack, IBlockState block) {
		if (Items.IRON_AXE.getDestroySpeed(itemstack, block) > 2.0F) {
			return material.getEfficiency() * 0.75F;
		}
		return super.getDestroySpeed(itemstack, block);
	}

	@Override
	public boolean playCustomParrySound(EntityLivingBase blocker, Entity attacker, ItemStack weapon) {
		blocker.world.playSound(null, blocker.getPosition(), MineFantasySounds.WOOD_PARRY, SoundCategory.NEUTRAL, 1.0F, 0.7F);
		return true;
	}

	@Override
	public float getAttackSpeed(ItemStack item) {
		return super.getAttackSpeed(item) + speedAxe;
	}

	@Override
	public float getDamageModifier() {
		return damageModAxe;
	}

	@Override
	protected float[] getWeaponRatio(ItemStack implement) {
		return hackingDamage;
	}

	@Override
	public float getPenetrationLevel(Object implement) {
		return axeAPModifier;
	}

	/**
	 * gets the time after being hit your guard will be let down
	 */
	@Override
	public int getParryCooldown(DamageSource source, float dam, ItemStack weapon) {
		return axeParryTime;
	}

	@Override
	protected float getStaminaMod() {
		return axeStaminaCost;
	}

	@Override
	public WeaponClass getWeaponClass() {
		return WeaponClass.AXE;
	}

	@Override
	public boolean canCounter() {
		return true;
	}

	@Override
	public float[] getCounterRatio() {
		return piercingDamage;
	}

	@Override
	public float getCounterDamage() {
		return 1.0F;
	}

	/**
	 * Can this Item disable a shield
	 *
	 * @param stack    The ItemStack
	 * @param shield   The shield in question
	 * @param entity   The EntityLivingBase holding the shield
	 * @param attacker The EntityLivingBase holding the ItemStack
	 * @retrun True if this ItemStack can disable the shield in question.
	 */
	public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
		return true;
	}

}
