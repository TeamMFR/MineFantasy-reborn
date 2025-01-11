package minefantasy.mfr.item;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.client.ClientItemsMFR;
import minefantasy.mfr.config.ConfigHardcore;
import minefantasy.mfr.config.ConfigStamina;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.data.IStoredVariable;
import minefantasy.mfr.data.Persistence;
import minefantasy.mfr.data.PlayerData;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.mechanics.StaminaBar;
import minefantasy.mfr.proxy.IClientRegister;
import minefantasy.mfr.util.ModelLoaderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.DecimalFormat;
import java.util.List;

public class ItemFoodMFR extends ItemFood implements IClientRegister {
	public static final DecimalFormat decimal_format = new DecimalFormat("#.#");
	public static final IStoredVariable<Float> EAT_DELAY = IStoredVariable.StoredVariable.ofFloat("eatDelay", Persistence.RESPAWN);
	public static final IStoredVariable<Float> FAT_ACCUMULATION = IStoredVariable.StoredVariable.ofFloat("fatAccumulation", Persistence.ALWAYS).setSynced();
	public static final IStoredVariable<NBTTagCompound> REPEATED_FOOD_NBT = IStoredVariable.StoredVariable.ofNBT("repeatedFoodCount", Persistence.ALWAYS);
	public final Rarity itemRarity;
	protected int hungerLevel;
	private float staminaRestore = 0F;
	private boolean hasEffect = false;
	private float staminaBuff = 0F;
	private int staminaSeconds = 0;
	private boolean staminaInMinutes = false;
	private boolean staminaInHours = false;
	private boolean shouldRepeatCheck = false;
	private float staminaRegenBuff = 0F;
	private int staminaRegenSeconds = 0;
	private boolean staminaRegenInMinutes = false;
	private float eatDelay = 0;
	private float fatAccumulation = 0;
	private int useTime = 32;

	private Item returnItem;

	public ItemFoodMFR(String name, int hunger, float saturation, boolean isMeat) {
		this(name, hunger, saturation, isMeat, Rarity.COMMON);
	}

	public ItemFoodMFR(String name, int hunger, float saturation, boolean isMeat, Rarity rarity) {
		super(hunger, saturation, isMeat);

		hungerLevel = hunger;
		this.itemRarity = rarity;

		setRegistryName(name);
		setTranslationKey(name);

		setCreativeTab(MineFantasyTabs.tabFood);

		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	static {
		PlayerData.registerStoredVariables(EAT_DELAY, FAT_ACCUMULATION, REPEATED_FOOD_NBT);
	}

	public ItemFoodMFR setReturnItem(Item item) {
		this.returnItem = item;
		return this;
	}

	public ItemFoodMFR setShouldRepeatPenaltyCheck() {
		this.shouldRepeatCheck = true;
		return this;
	}

	public static void onTick(EntityPlayer player) {
		float time = getEatDelay(player);
		if (time > 0) {
			time--;
			setEatDelay(player, time);
		}
	}

	private static void setEatDelay(EntityPlayer player, float time) {
		PlayerData data = PlayerData.get(player);
		if (data != null) {
			data.setVariable(EAT_DELAY, time);
		}
	}

	private static float getEatDelay(EntityPlayer player) {
		PlayerData data = PlayerData.get(player);
		if (data != null) {
			if (data.getVariable(EAT_DELAY) == null) {
				setEatDelay(player, 0);
			}
			return data.getVariable(EAT_DELAY);
		}
		return 0;
	}

	public static void setFatAccumulation(EntityPlayer player, float fat) {
		PlayerData data = PlayerData.get(player);
		if (data != null) {
			data.setVariable(FAT_ACCUMULATION, fat);
			data.sync();
		}
	}

	public static float getFatAccumulation(EntityPlayer player) {
		PlayerData data = PlayerData.get(player);
		if (data != null) {
			if (data.getVariable(FAT_ACCUMULATION) == null) {
				setFatAccumulation(player, 0);
			}
			return data.getVariable(FAT_ACCUMULATION);
		}
		return 0;
	}

	public static void decrementFatAccumulation(EntityPlayer player, float decrement) {
		if (!player.world.isRemote) {
			float fat_accumulation = ItemFoodMFR.getFatAccumulation(player);
			if (fat_accumulation > 0) {
				fat_accumulation -= decrement;
				ItemFoodMFR.setFatAccumulation(player, fat_accumulation);
			}
			if (fat_accumulation < 0) {
				ItemFoodMFR.setFatAccumulation(player, 0);
			}
		}
	}

	protected void onMFFoodEaten(ItemStack food, EntityPlayer consumer) {
		if (ConfigStamina.isSystemActive) {
			if (staminaRestore > 0) {
				StaminaBar.modifyStaminaValue(consumer, staminaRestore);
			}
			if (staminaSeconds > 0) {
				StaminaBar.buffStamina(consumer, staminaBuff, staminaSeconds);
			}
			if (staminaRegenSeconds > 0) {
				StaminaBar.buffStaminaRegen(consumer, staminaRegenBuff, staminaRegenSeconds);
			}
		}
		if (eatDelay > 0) {
			setEatDelay(consumer, eatDelay);
		}
		setFatAccumulation(consumer, fatAccumulation + getFatAccumulation(consumer));

		//MFR repeated eat penalty
		PlayerData data = PlayerData.get(consumer);
		if (data != null) {
			NBTTagCompound nbt = data.getVariable(REPEATED_FOOD_NBT);
			if (nbt == null) {
				nbt = new NBTTagCompound();
				nbt.setInteger("repeat_count", 0);
				food.writeToNBT(nbt);
			}
			else {
				ItemStack lastEatenFood = new ItemStack(nbt);
				if (lastEatenFood.isItemEqual(food)) {
					if (shouldRepeatCheck) {
						int repeat_count = nbt.getInteger("repeat_count");
						nbt.setInteger("repeat_count", repeat_count + 1);
						if (repeat_count >= ConfigHardcore.foodRepeatPenaltyLimit) {
							consumer.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 300, 1));
							consumer.addPotionEffect(new PotionEffect(MobEffects.POISON, 120, 900));
							consumer.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 120, 120));
							if (!consumer.world.isRemote && repeat_count == 3) {
								consumer.sendMessage(new TextComponentTranslation("info.vomiting.message", food.getDisplayName()));
							}
						}
					}
				}
				else {
					nbt.setInteger("repeat_count", 0);
					food.writeToNBT(nbt);
				}
			}
			data.setVariable(REPEATED_FOOD_NBT, nbt);
		}

		if (this == MineFantasyItems.BERRIES_JUICY) {
			PotionEffect poison = consumer.getActivePotionEffect(MobEffects.POISON);
			if (poison != null) {
				poison.addCurativeItem(food);
				consumer.curePotionEffects(food);
			}
		}
	}

	public static void onCustomFoodEaten(EntityPlayer consumer, float staminaRestore, int staminaSeconds, float staminaBuff, int staminaRegenSeconds, float staminaRegenBuff, float eatDelay, float fatAccumulation) {
		if (ConfigStamina.isSystemActive) {
			if (staminaRestore > 0) {
				StaminaBar.modifyStaminaValue(consumer, staminaRestore);
			}
			if (staminaSeconds > 0) {
				StaminaBar.buffStamina(consumer, staminaBuff, staminaSeconds);
			}
			if (staminaRegenSeconds > 0) {
				StaminaBar.buffStaminaRegen(consumer, staminaRegenBuff, staminaRegenSeconds);
			}
		}
		if (eatDelay > 0) {
			setEatDelay(consumer, eatDelay);
		}
		setFatAccumulation(consumer, fatAccumulation + getFatAccumulation(consumer));
	}

	public ItemFoodMFR setStaminaRestore(float f) {
		staminaRestore = f;
		hasEffect = true;
		return this;
	}

	/**
	 * Grade Sugar, Carbs and Fats from 0-1 and tier does the rest 0 means
	 * non-existent
	 *
	 * @param sugar restore stamina and add regen
	 * @param carbs increase max stamina for 1 hr
	 * @param fats  saturation
	 */
	public ItemFoodMFR setFoodStats(int tier, float sugar, float carbs, float fats) {
		return setFoodStats(sugar * tier, carbs * tier, fats * tier);
	}

	/**
	 * New system for setting food vales: Values should be based on tier value (t1,
	 * t2, t3,...,tx) 0 means non-existent
	 *
	 * @param sugar restore stamina and add regen
	 * @param carbs increase max stamina for 1 hr
	 * @param fats  fat based eat delay
	 */
	public ItemFoodMFR setFoodStats(float sugar, float carbs, float fats) {
		if (sugar > 0) {
			setStaminaRestore((int) (sugar * 50));
			setStaminaRegenModifier((int) sugar, sugar / 30);
		}
		if (carbs > 0) {
			setStaminaModifier(50 * carbs, carbs);
		}
		if (fats > 0) {
			setEatDelayModifier(fats * ConfigStamina.eatDelayModifier);
			fatAccumulation = fats * ConfigStamina.fatAccumulationModifier;
		}
		return this;
	}

	public void setStaminaModifier(float buff, float hours) {
		int secondsLasting = (int) (hours * 3600F);
		staminaBuff = buff;
		staminaSeconds = secondsLasting;
		staminaInMinutes = secondsLasting > 60;
		staminaInHours = secondsLasting > 3600;
		hasEffect = true;
	}

	public void setStaminaRegenModifier(float buff, float minutesLasting) {
		int secondsLasting = (int) (minutesLasting * 60F);
		staminaRegenBuff = buff;
		staminaRegenSeconds = secondsLasting;
		staminaRegenInMinutes = secondsLasting > 60;
		hasEffect = true;
	}

	public void setEatDelayModifier(float fat) {
		eatDelay = fat * 10;
		if (eatDelay > 0) {
			hasEffect = true;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack food, World world, List<String> list, ITooltipFlag flag) {
		super.addInformation(food, world, list, flag);
		list.add(I18n.format("food.stat.hunger.name", hungerLevel));

		if (hasEffect && ClientItemsMFR.showSpecials(list)) {
			list.add("");
			list.add(TextFormatting.WHITE + I18n.format("food.stat.list.name"));
			if (eatDelay > 0) {
				list.add(I18n.format("food.stat.eatDelay.name", decimal_format.format(eatDelay / 20)));
			}
			if (staminaRestore > 0) {
				list.add(I18n.format("food.stat.staminaPlus.name", (int) staminaRestore));
			}
			if (staminaBuff > 0) {
				if (staminaInHours) {
					list.add(I18n.format("food.stat.staminabuffHours.name", decimal_format.format(staminaBuff), decimal_format.format(staminaSeconds / 3600F)));
				} else if (staminaInMinutes) {
					list.add(I18n.format("food.stat.staminabuffMinutes.name", decimal_format.format(staminaBuff), decimal_format.format(staminaSeconds / 60F)));
				} else {
					list.add(I18n.format("food.stat.staminabuffSeconds.name", decimal_format.format(staminaBuff), decimal_format.format(staminaSeconds)));
				}
			}
			if (staminaRegenBuff > 0) {
				if (staminaRegenInMinutes) {
					list.add(I18n.format("food.stat.staminabuffRegenMinutes.name", decimal_format.format(staminaRegenBuff), decimal_format.format(staminaRegenSeconds / 60F)));
				} else {
					list.add(I18n.format("food.stat.staminabuffRegenSeconds.name", decimal_format.format(staminaRegenBuff), decimal_format.format(staminaRegenSeconds)));
				}
			}
			if (fatAccumulation > 0) {
				list.add(I18n.format("food.stat.fatAccumulation.name", decimal_format.format(fatAccumulation)));
			}
		}
		if (this == MineFantasyItems.BERRIES_JUICY) {
			list.add(I18n.format("food.stat.cure.poison"));
		}
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		return Rarity.getForgeRarity(item, itemRarity);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack food, World world, EntityLivingBase consumer) {
		if (consumer instanceof EntityPlayer) {
			((EntityPlayer) consumer).getFoodStats().addStats(this, food);
			world.playSound((EntityPlayer) consumer, consumer.getPosition(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.AMBIENT, 1.0F, 1.0F);
			this.onMFFoodEaten(food, (EntityPlayer) consumer);

			if (this.isDamageable() && consumer instanceof EntityPlayerMP) {
				if (food.attemptDamageItem(1, consumer.getRNG(), (EntityPlayerMP) consumer)) {
					food.shrink(1);
					if (food.getCount() < 0) {
						food.setCount(0);
					}
				}
			} else {
				food.shrink(1);
			}

			if (returnItem != null) {
				return new ItemStack(returnItem);
			}
		}

		return food;
	}

	public ItemFoodMFR setEatTime(int i) {
		useTime = i;
		return this;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack item) {
		return useTime;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack food = player.getHeldItem(hand);
		if (getEatDelay(player) > 0) {
			return ActionResult.newResult(EnumActionResult.FAIL, food);
		}
		return super.onItemRightClick(world, player, hand);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this);
	}
}
