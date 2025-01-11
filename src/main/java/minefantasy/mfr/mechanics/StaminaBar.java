package minefantasy.mfr.mechanics;

import minefantasy.mfr.api.MineFantasyReforgedAPI;
import minefantasy.mfr.api.stamina.IHeldStaminaItem;
import minefantasy.mfr.api.stamina.IWornStaminaItem;
import minefantasy.mfr.config.ConfigStamina;
import minefantasy.mfr.data.IStoredVariable;
import minefantasy.mfr.data.Persistence;
import minefantasy.mfr.data.PlayerData;
import minefantasy.mfr.mechanics.knowledge.ResearchLogic;
import minefantasy.mfr.util.ArmourCalculator;
import minefantasy.mfr.util.PowerArmour;
import minefantasy.mfr.util.TacticalManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class StaminaBar {

	public static final IStoredVariable<Float> STAMINA_REGEN_KEY = IStoredVariable.StoredVariable.ofFloat("staminaRegen", Persistence.DIMENSION_CHANGE);
	public static final IStoredVariable<Integer> STAMINA_REGEN_TICKS_KEY = IStoredVariable.StoredVariable.ofInt("staminaRegenTicks", Persistence.DIMENSION_CHANGE);
	public static final IStoredVariable<Float> STAMINA_BONUS_KEY = IStoredVariable.StoredVariable.ofFloat("staminaBonus", Persistence.DIMENSION_CHANGE);
	public static final IStoredVariable<Integer> STAMINA_BONUS_TICKS_KEY = IStoredVariable.StoredVariable.ofInt("staminaBonusTicks", Persistence.DIMENSION_CHANGE);
	public static final IStoredVariable<Float> STAMINA_MAX_KEY = IStoredVariable.StoredVariable.ofFloat("staminaMax", Persistence.DIMENSION_CHANGE);
	public static final IStoredVariable<Float> STAMINA_VALUE_KEY = IStoredVariable.StoredVariable.ofFloat("staminaValue", Persistence.DIMENSION_CHANGE);
	public static final IStoredVariable<Float> STAMINA_IDLE_KEY = IStoredVariable.StoredVariable.ofFloat("staminaIdle", Persistence.DIMENSION_CHANGE);
	public static final IStoredVariable<Float> STAMINA_FLASHING_KEY = IStoredVariable.StoredVariable.ofFloat("staminaFlashing", Persistence.DIMENSION_CHANGE);

	/**
	 * Modifies the decay speed for armour, this scales to what a full suit of plate
	 * does
	 */
	private static final float armourWeightModifierClimbing = 5.0F;


	public static float regenModifier = 1.5F;
	public static boolean restrictSystem = true;
	/**
	 * Modifies the regen rate slowdown for armour
	 */
	private static float bulkModifier = 1.0F;

	static {
		PlayerData.registerStoredVariables(STAMINA_REGEN_KEY, STAMINA_REGEN_TICKS_KEY, STAMINA_BONUS_KEY, STAMINA_BONUS_TICKS_KEY, STAMINA_MAX_KEY, STAMINA_VALUE_KEY, STAMINA_IDLE_KEY, STAMINA_FLASHING_KEY);
	}

	// MAX STAMINA//
	public static float getTotalMaxStamina(EntityLivingBase user) {
		return getBaseMaxStamina(user) + getBonusStamina(user);
	}

	public static boolean isStaminaAtMax(EntityLivingBase user) {
		return StaminaBar.getStaminaValue(user) == StaminaBar.getTotalMaxStamina(user);
	}

	public static float getBaseMaxStamina(EntityLivingBase user) {
		float bonus = getStaminaLevelBoost(user);
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			if (data != null) {
				if (data.getVariable(STAMINA_MAX_KEY) == null) {
					setMaxStamina(data, getDefaultMax() + bonus);
				}
				return data.getVariable(STAMINA_MAX_KEY) + bonus;
			}
		}
		return getDefaultMax() + bonus;
	}

	public static float getDefaultMax() {
		return ConfigStamina.defaultMax;
	}

	public static void setMaxStamina(PlayerData data, float value) {
		if (data != null) {
			data.setVariable(STAMINA_MAX_KEY, value);
		}
	}

	// REGEN BUFF STAMINA//
	public static float getBonusStaminaRegen(EntityLivingBase user) {
		float level = 0F;
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			if (data != null) {
				if (data.getVariable(STAMINA_REGEN_KEY) == null) {
					setBonusStaminaRegen(data, 0F);
				}
				level += data.getVariable(STAMINA_REGEN_KEY);
			}
		}
		if (user.getActivePotionEffect(MobEffects.REGENERATION) != null) {
			level += (user.getActivePotionEffect(MobEffects.REGENERATION).getAmplifier() + 1) * 2.5F;
		}
		return level;
	}

	public static void setBonusStaminaRegen(PlayerData data, float value) {
		if (data != null) {
			data.setVariable(STAMINA_REGEN_KEY, value);
		}
	}

	public static int getBonusStaminaRegenTicks(EntityLivingBase user) {
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			if (data != null) {
				if (data.getVariable(STAMINA_REGEN_TICKS_KEY) == null) {
					setBonusStaminaRegenTicks(data, 0);
				}
				return data.getVariable(STAMINA_REGEN_TICKS_KEY);
			}
		}
		return 0;
	}

	public static void setBonusStaminaRegenTicks(PlayerData data, int value) {
		if (data != null) {
			data.setVariable(STAMINA_REGEN_TICKS_KEY, value);
		}
	}

	public static float getRegenBonus(EntityLivingBase user) {
		return getBonusStaminaRegen(user) / 20F;
	}

	// TEMP STAMINA//
	public static float getBonusStamina(EntityLivingBase user) {
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			if (data != null) {
				if (data.getVariable(STAMINA_BONUS_KEY) == null) {
					setBonusStamina(data, 0F);
				}
				return data.getVariable(STAMINA_BONUS_KEY);
			}
		}
		return 0F;
	}

	private static float getStaminaLevelBoost(EntityLivingBase user) {
		if (user.world.isRemote)
			return 0;

		float amount = 0F;
		if (user instanceof EntityPlayer && ConfigStamina.levelUp && ConfigStamina.levelAmount > 0) {
			EntityPlayer player = (EntityPlayer) user;
			if (player.experienceLevel > 0) {
				amount += (player.experienceLevel * ConfigStamina.levelAmount);
			}
		}
		return amount;
	}

	public static void setBonusStamina(PlayerData data, float value) {
		if (data != null) {
			data.setVariable(STAMINA_BONUS_KEY, value);
		}
	}

	public static int getBonusStaminaTicks(EntityLivingBase user) {
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			if (data != null) {
				if (data.getVariable(STAMINA_BONUS_TICKS_KEY) == null) {
					setBonusStaminaTicks(data, 0);
				}
				return data.getVariable(STAMINA_BONUS_TICKS_KEY);
			}
		}
		return 0;
	}

	public static void setBonusStaminaTicks(PlayerData data, int value) {
		if (data != null) {
			data.setVariable(STAMINA_BONUS_TICKS_KEY, value);
		}
	}

	public static void tickBonus(EntityLivingBase user) {
		if (user.world.isRemote)
			return;
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			int currentTicks = getBonusStaminaTicks(user);
			currentTicks--;
			if (currentTicks <= 0) {
				setBonusStaminaTicks(data, 0);
				setBonusStamina(data, 0);

				if (getStaminaValue(user) > getTotalMaxStamina(user)) {
					setStaminaValue(data, getTotalMaxStamina(user));
				}
				return;
			}
			setBonusStaminaTicks(data, currentTicks);
		}
	}

	public static void tickBonusRegen(EntityLivingBase user) {
		if (user.world.isRemote)
			return;
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			int currentTicks = getBonusStaminaRegenTicks(user);
			currentTicks--;
			if (currentTicks <= 0) {
				setBonusStaminaRegenTicks(data, 0);
				setBonusStaminaRegen(data, 0);
				return;
			}
			setBonusStaminaRegenTicks(data, currentTicks);
		}
	}

	/**
	 * Call this to buff stamina temporarily, it will not override more potent
	 * effects
	 *
	 * @param user    the entity to buff
	 * @param mod     the amount of stamina added
	 * @param seconds the time it takes to wear off
	 */
	public static void buffStamina(EntityLivingBase user, float mod, int seconds) {
		if (!ConfigStamina.isSystemActive) {
			return;
		}
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			int ticks = seconds * 20;
			float current = getBonusStamina(user);
			if (mod >= current) {
				setBonusStaminaTicks(data, ticks);
				setBonusStamina(data, mod);
			}
		}
	}

	public static void buffStaminaRegen(EntityLivingBase user, float mod, int seconds) {
		if (!ConfigStamina.isSystemActive) {
			return;
		}
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			int ticks = seconds * 20;
			float current = getBonusStaminaRegen(user);
			int currentTicks = getBonusStaminaRegenTicks(user);
			if (mod >= current && ticks >= currentTicks) {
				setBonusStaminaRegenTicks(data, ticks);
				setBonusStaminaRegen(data, mod);
			}
		}
	}

	// STAMINA VALUE//
	public static float getStaminaValue(EntityLivingBase user) {
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			if (data != null) {
				if (data.getVariable(STAMINA_VALUE_KEY) == null) {
					setStaminaValue(data, getDefaultMax());
				}
				return data.getVariable(STAMINA_VALUE_KEY);
			}
		}
		return getDefaultMax();
	}

	public static void setStaminaValue(PlayerData data, float value) {
		if (data != null) {
			data.setVariable(STAMINA_VALUE_KEY, value);
		}
	}

	public static void modifyStaminaValue(EntityLivingBase user, float mod) {
		if (mod < 0 && PowerArmour.isPowered(user)) {
			return;
		}
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			setStaminaValue(data, getStaminaValue(user) + mod);

			if (mod < 0 && getStaminaValue(user) < 0) {
				setStaminaValue(data, 0);
			}
			float max = getTotalMaxStamina(user);
			if (getStaminaValue(user) > max) {
				setStaminaValue(data, max);
			}
		}
	}

	// DECIMAL PERCENT VALUE//
	public static float getStaminaDecimal(EntityLivingBase user) {
		return getStaminaValue(user) / getTotalMaxStamina(user);
	}

	// INIT//
	public static void refreshSystem(EntityLivingBase user) {
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			if (data != null) {

				if (data.getVariable(STAMINA_MAX_KEY) == null) {
					data.setVariable(STAMINA_MAX_KEY, getDefaultMax());
				}
				if (data.getVariable(STAMINA_VALUE_KEY) == null) {
					data.setVariable(STAMINA_VALUE_KEY, getDefaultMax());
				}
				if (data.getVariable(STAMINA_FLASHING_KEY) == null) {
					data.setVariable(STAMINA_FLASHING_KEY, 0F);
				}
			}
		}
	}

	// STAMINA IDLE//
	public static float getIdleTime(EntityLivingBase user) {
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			if (data != null) {
				if (data.getVariable(STAMINA_IDLE_KEY) == null) {
					setIdleTime(data, 0);
				}
				return data.getVariable(STAMINA_IDLE_KEY);
			}
		}
		return 0;
	}

	public static void setIdleTime(PlayerData data, float value) {
		if (data != null) {
			data.setVariable(STAMINA_IDLE_KEY, value);
		}
	}

	public static void ModifyIdleTime(EntityLivingBase user, float mod) {
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			setIdleTime(data, getIdleTime(user) + mod);
		}
	}

	// STAMINA FLASH//
	public static float getFlashTime(EntityLivingBase user) {
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			if (data != null) {
				if (data.getVariable(STAMINA_FLASHING_KEY) == null) {
					setFlashTime(data, 0);
				}
				return data.getVariable(STAMINA_FLASHING_KEY);
			}
		}
		return 0;
	}

	/**
	 * determines if the stamina can take a specific value
	 */

	public static void setFlashTime(PlayerData data, float value) {
		if (data != null) {
			data.setVariable(STAMINA_FLASHING_KEY, value);
		}
	}

	public static void ModifyFlashTime(PlayerData data, EntityLivingBase user, float mod) {
		setFlashTime(data, getFlashTime(user) + mod);
	}

	/**
	 * Checks if stamina is available
	 *
	 * @param onUse whether or not stamina is trying to be used
	 * @return isStaminaAvailable or not
	 */
	public static boolean isStaminaAvailable(EntityLivingBase user, float level, boolean onUse) {
		if (getStaminaValue(user) >= level) {
			return true;
		}
		if (user instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) user);
			if (onUse) {
				setFlashTime(data, 40);
			}
		}

		return false;
	}

	public static boolean isAnyStamina(EntityLivingBase user, boolean flash) {
		return isStaminaAvailable(user, 1, flash);
	}

	public static boolean isPercentStamAvailable(EntityLivingBase user, float decimal, boolean flash) {
		if (PowerArmour.isPowered(user)) {
			return true;// Cogwork infinite power
		}

		return isStaminaAvailable(user, getTotalMaxStamina(user) * decimal, flash);
	}

	public static float getBaseDecayModifier(EntityPlayer player, boolean countArmour, boolean countHeld) {
		if (PowerArmour.isPowered(player)) {
			return 0F;
		}
		float value = getDecayModifier(player.world);
		float AM = 1.0F;
		value *= getBasePerkStaminaModifier(value, player);
		AM = getPerkArmModifier(value, player);
		if (TacticalManager.isImmuneToWeight(player)) {
			return value * 0.5F;
		}

		if (countHeld) {
			if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof IHeldStaminaItem) {
				value *= (((IHeldStaminaItem) player.getHeldItemMainhand().getItem()).getDecayMod(player, player.getHeldItemMainhand()));
			}
		}
		if (countArmour) {
			float armourMod = 1.0F;
			Iterable<ItemStack> armour = player.getArmorInventoryList();
			for (ItemStack stack : armour) {
				if (!stack.isEmpty() && stack.getItem() instanceof IWornStaminaItem) {
					armourMod += (((IWornStaminaItem) stack.getItem()).getDecayModifier(player, stack));
				}
			}
			float min = ArmourCalculator.encumberanceArray[0];
			float max = ArmourCalculator.encumberanceArray[1];
			float mass = ArmourCalculator.getTotalWeightOfWorn(player, false) - min;

			if (mass > 0F) {
				armourMod += ((mass + min) / max);
			}
			value *= armourMod;
		}
		return value;
	}

	public static float getClimbinbDecayModifier(EntityPlayer player, boolean countArmour) {
		float value = getDecayModifier(player.world);

		if (!TacticalManager.isImmuneToWeight(player) && countArmour) {
			float armourMod = 1.0F;
			Iterable<ItemStack> armour = player.getArmorInventoryList();
			for (ItemStack stack : armour) {
				if (!stack.isEmpty() && stack.getItem() instanceof IWornStaminaItem) {
					armourMod += ((IWornStaminaItem) stack.getItem()).getDecayModifier(player, stack);
				}
			}
			float min = ArmourCalculator.encumberanceArray[0];
			float max = ArmourCalculator.encumberanceArray[1];
			float mass = ArmourCalculator.getTotalWeightOfWorn(player, false) - min;
			if (mass > 0) {
				value *= (1 + ((min + mass) / max * ConfigStamina.configArmourWeightModifier * (armourWeightModifierClimbing - 1)));
			}
			value *= armourMod;
		}
		return value;
	}

	public static float getBaseRegenModifier(EntityPlayer player, boolean countArmour, boolean countHeld) {
		float value = ConfigStamina.configRegenModifier * regenModifier * (5F / 3F);

		if (!TacticalManager.isImmuneToWeight(player)) {
			if (countHeld) {
				if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof IHeldStaminaItem) {
					value *= ((IHeldStaminaItem) player.getHeldItemMainhand().getItem()).getRegenModifier(player,
							player.getHeldItemMainhand());
				}
			}
			if (countArmour) {
				float AM;

				AM = getPerkArmModifier(value, player);

				float armourMod = 1.0F;

				Iterable<ItemStack> armour = player.getArmorInventoryList();
				for (ItemStack stack : armour) {
					if (!stack.isEmpty() && stack.getItem() instanceof IWornStaminaItem) {
						armourMod += ((IWornStaminaItem) stack.getItem()).getRegenModifier(player, stack);
					}
				}
				float weightMod = ArmourCalculator.getTotalWeightOfWorn(player, false) * bulkModifier * ConfigStamina.configBulk;
				if (weightMod > 0) {
					float min = ArmourCalculator.encumberanceArray[0];
					float max = ArmourCalculator.encumberanceArray[1];
					value /= (1.0F + ((min + weightMod) / (max / 2) * AM * 0.5F));
				}
				value *= armourMod;
			}
		}
		if (player.isSneaking()) {
			value *= 1.5F;
		}

		return value;
	}

	public static float getBaseIdleModifier(EntityLivingBase user, boolean countArmour, boolean countHeld) {
		float value = ConfigStamina.pauseModifier;

		if (!TacticalManager.isImmuneToWeight(user)) {
			if (countHeld) {
				if (!user.getHeldItemMainhand().isEmpty() && user.getHeldItemMainhand().getItem() instanceof IHeldStaminaItem) {
					value *= ((IHeldStaminaItem) user.getHeldItemMainhand().getItem()).getIdleModifier(user,
							user.getHeldItemMainhand());
				}
			}
			if (countArmour) {
				float armourMod = 1.0F;

				Iterable<ItemStack> armour = user.getArmorInventoryList();
				for (ItemStack stack : armour) {
					if (!stack.isEmpty() && stack.getItem() instanceof IWornStaminaItem) {
						armourMod += ((IWornStaminaItem) stack.getItem()).getIdleModifier(user, stack);
					}
				}
			}
		}

		return value;
	}

	/**
	 * This scales the decay modifier across all fields on difficulty It's base
	 * level is on normal with easy,peaceful being 25% - 50% slower. Hard is 25%
	 * faster
	 */
	public static float getDecayModifier(World world) {
		if (!ConfigStamina.scaleDifficulty) {
			return ConfigStamina.decayModifierCfg * ConfigStamina.decayModifierBase;
		}
		int difficultyMod = world.getDifficulty().getId() - 2;
		return (ConfigStamina.decayModifierCfg * ConfigStamina.decayModifierBase) + (0.25F * difficultyMod);
	}

	public static boolean doesAffectEntity(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer p = (EntityPlayer) entity;
			return !p.capabilities.isCreativeMode;
		} else if (restrictSystem) {
			return false;
		}
		return MineFantasyReforgedAPI.isInDebugMode || !entity.isEntityUndead();
	}

	private static float getPerkArmModifier(float value, EntityPlayer user) {
		if (ResearchLogic.hasInfoUnlocked(user, "armour_pro")) {
			value *= 0.5F;
		}
		return value;
	}

	private static float getBasePerkStaminaModifier(float value, EntityPlayer user) {
		if (ResearchLogic.hasInfoUnlocked(user, "fitness")) {
			value *= 0.75F;
		}
		return value;
	}
}
