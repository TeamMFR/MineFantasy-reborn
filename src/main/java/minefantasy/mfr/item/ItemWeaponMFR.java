package minefantasy.mfr.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.crafting.exotic.ISpecialDesign;
import minefantasy.mfr.api.stamina.IHeldStaminaItem;
import minefantasy.mfr.api.stamina.IStaminaWeapon;
import minefantasy.mfr.api.tier.IToolMaterial;
import minefantasy.mfr.api.weapon.IDamageModifier;
import minefantasy.mfr.api.weapon.IDamageType;
import minefantasy.mfr.api.weapon.IExtendedReachWeapon;
import minefantasy.mfr.api.weapon.IKnockbackWeapon;
import minefantasy.mfr.api.weapon.IParryable;
import minefantasy.mfr.api.weapon.IPowerAttack;
import minefantasy.mfr.api.weapon.IRackItem;
import minefantasy.mfr.api.weapon.ISpecialCombatMob;
import minefantasy.mfr.api.weapon.ISpecialEffect;
import minefantasy.mfr.api.weapon.IWeaponClass;
import minefantasy.mfr.api.weapon.IWeightedWeapon;
import minefantasy.mfr.config.ConfigStamina;
import minefantasy.mfr.config.ConfigWeapon;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.data.PlayerData;
import minefantasy.mfr.entity.EntityCogwork;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.init.MineFantasyMaterials;
import minefantasy.mfr.init.MineFantasySounds;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.mechanics.PlayerTickHandler;
import minefantasy.mfr.mechanics.StaminaBar;
import minefantasy.mfr.mechanics.knowledge.ResearchLogic;
import minefantasy.mfr.proxy.IClientRegister;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.tile.TileEntityRack;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.MFRLogUtil;
import minefantasy.mfr.util.ModelLoaderHelper;
import minefantasy.mfr.util.TacticalManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Made this extend the sword class (allows them to be enchanted)
public abstract class ItemWeaponMFR extends ItemSword implements ISpecialDesign, IPowerAttack, IDamageType,
		IKnockbackWeapon, IHeldStaminaItem, IStaminaWeapon, IToolMaterial,
		IWeightedWeapon, IParryable, ISpecialEffect, IDamageModifier, IWeaponClass, IRackItem, IClientRegister {
	public static final DecimalFormat decimal_format = new DecimalFormat("#.#");
	public static float axeAPModifier = -0.1F;
	protected static float speedModHeavy = -0.4F;
	protected static float speedSword = -2.4F;
	protected static float speedAxe = -2.6F;
	protected static float speedMace = -2.8F;
	protected static float speedKatana = -2F;
	protected static float speedDagger = -1.5F;
	protected static float speedSpear = -2.5F;
	protected static float damageModSword = 0.0F;
	protected static float damageModAxe = 0.5F;
	protected static float damageModMace = 1.0F;
	protected final ToolMaterial material;
	public String designType = "standard";
	protected String name;
	protected Random rand = new Random();
	protected float lunge_cost = 25;

	// MECHANICS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	protected float charge_cost = 10;
	protected float jump_cost = 2;
	protected float cleave_cost = 40;
	protected float[] slashingDamage = new float[] {1F, 0F, 0F};
	protected float[] crushingDamage = new float[] {0F, 1F, 0F};
	protected float[] hackingDamage = new float[] {4F, 1F, 0F};
	protected float[] heavyHackingDamage = new float[] {3F, 1F, 0F};
	protected float[] piercingDamage = new float[] {0F, 0F, 1F};
	protected float[] heavyPiercingDamage = new float[] {0F, 1F, 9F};
	protected float[] heavySlashingDamage = new float[] {9F, 1F, 0F};
	protected int defParryTime = 15;
	protected int swordParryTime = 10;
	protected int axeParryTime = 15;
	protected int maceParryTime = 15;
	protected int daggerParryTime = 5;
	protected int spearParryTime = 20;
	protected int heavyParryTime = 10;
	protected float heavyParryFatigue = 2.0F;
	protected float daggerStaminaCost = 0.50F;
	protected float swordStaminaCost = 1.00F;
	protected float katanaStaminaCost = 0.85F;
	protected float axeStaminaCost = 1.20F;
	protected float maceStaminaCost = 1.50F;
	protected float spearStaminaCost = 1.40F;
	protected float heavyStaminaCost = 2.50F;
	protected Rarity itemRarity;
	/**
	 * The damage of the weapon without material modifiers
	 */
	private float baseDamage;
	private float materialWeight = 1.0F;
	// ===================================================== CUSTOM START
	// =============================================================\\
	private boolean isCustom = false;

	/**
	 * The base file for Weapons in MineFantasy
	 * <p>
	 * Size Varients, (Normal and Heavy) Normal weapons are as is, regular damage
	 * and weight Heavy weapons do more damage, exhaust more and have balance offset
	 * (+50% dam)
	 * <p>
	 * Weapon Types:
	 * Blade: Parry Defensive, average damage and speed
	 * Axe: Brutal Offensive, slower than sword, more damage, Good against Armour
	 * Blunt: Simple Offensive, slower than axe, more damage, Good against Medium Armour
	 * Polearm: Ranged Defensive, Good against Heavy Armour
	 * Lightblade: Fast Offensive, Better against Unarmoured
	 */
	public ItemWeaponMFR(ToolMaterial material, String named, Rarity rarity, float weight) {
		super(material);
		materialWeight = weight;
		itemRarity = rarity;
		// May be unsafe, but will allow others to add weapons using custom materials
		// (also more efficent)
		name = named;
		this.material = material;
		setCreativeTab(MineFantasyTabs.tabOldTools);
		setRegistryName(name);
		setTranslationKey(name);

		this.baseDamage = 4 + getDamageModifier();

		if (material == ToolMaterial.WOOD) {
			baseDamage = 0F;
		}

		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	public static int getParry(ItemStack item) {
		if (item.hasTagCompound()) {
			if (item.getTagCompound().hasKey("ParryAnimation")) {
				return item.getTagCompound().getInteger("ParryAnimation");
			}
		}
		return -1;
	}

	public static void setParry(ItemStack item, int i) {
		NBTTagCompound nbt = getOrCreateNBT(item);

		nbt.setInteger("ParryAnimation", i);
	}

	public static NBTTagCompound getOrCreateNBT(ItemStack item) {
		if (!item.hasTagCompound()) {
			item.setTagCompound(new NBTTagCompound());
		}
		return item.getTagCompound();
	}

	public static boolean canPerformAbility(EntityLivingBase user, float points) {
		return tryPerformAbility(user, points, false, true, true, false);
	}

	public static boolean tryPerformAbility(EntityLivingBase user, float points) {
		return tryPerformAbility(user, points, true, true);
	}

	public static boolean tryPerformAbility(EntityLivingBase user, float points, boolean armour, boolean weapon) {
		return tryPerformAbility(user, points, true, armour, weapon, true);
	}

	public static boolean tryPerformAbility(EntityLivingBase user, float points, boolean flash, boolean armour, boolean weapon, boolean takePoints) {
		if (user instanceof EntityPlayer && ConfigStamina.isSystemActive && StaminaBar.doesAffectEntity(user)) {
			points *= StaminaBar.getBaseDecayModifier((EntityPlayer) user, armour, weapon);
			if (StaminaBar.isStaminaAvailable(user, points, flash)) {
				if (takePoints && !user.world.isRemote) {
					applyFatigue(user, points);
				}
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	public static void applyFatigue(EntityLivingBase user, float points) {
		applyFatigue(user, points, 50F);
	}

	public static void applyFatigue(EntityLivingBase user, float points, float pause) {
		if (ConfigStamina.isSystemActive && StaminaBar.doesAffectEntity(user)) {
			float stam = StaminaBar.getStaminaValue(user);
			if (stam > 0) {
				StaminaBar.modifyStaminaValue(user, -points);
			}
			if (user instanceof EntityPlayer) {
				StaminaBar.setIdleTime(PlayerData.get((EntityPlayer) user), pause * ConfigStamina.pauseModifier);
			}

			if (!user.world.isRemote) {
				MFRLogUtil.logDebug("Spent " + points + " Stamina Pts");
			}
		}
	}

	/**
	 * Gets the amount more damaged added to each item
	 */
	public float getDamageModifier() {
		return 0;
	}

	/**
	 * Determines if the weapon can parry
	 */
	public boolean canWeaponParry() {
		return true;
	}

	/**
	 * Gets the angle the weapon can parry
	 */
	public float getParryAngleModifier(EntityLivingBase user) {
		return 0.75F;
	}

	/**
	 * Gets the multiplier for the parry threshold
	 *
	 * @return Parry Damage Modifier
	 */
	public float getParryDamageModifier(EntityLivingBase user) {
		return 1.0F;
	}

	/**
	 * Determines if the weapon can do those cool ninja evades
	 *
	 * @return if the weapon can evade
	 */
	public boolean canWeaponEvade() {
		return true;
	}

	@Override
	public ToolMaterial getMaterial() {
		return this.material;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack weapon, World world, List<String> list, ITooltipFlag flag) {
		super.addInformation(weapon, world, list, flag);

		if (material == ToolMaterial.WOOD) {
			return;
		}

		if (isCustom) {
			CustomToolHelper.addInformation(weapon, list);
		}

		if (this instanceof IExtendedReachWeapon) {
			list.add("");

			float reach = ((IExtendedReachWeapon) this).getReachModifierInBlocks();

			if (reach > 0) {
				list.add(TextFormatting.DARK_GREEN + I18n.format(
						"attribute.modifier.plus." + 0, decimal_format.format(reach),
						I18n.format("attribute.weapon.extendedReach")));
			} else {
				list.add(TextFormatting.RED + I18n.format(
						"attribute.modifier.take." + 0, decimal_format.format(-1 * reach),
						I18n.format("attribute.weapon.extendedReach")));
			}
		}
	}

	public boolean isHeavyWeapon() {
		return false;
	}

	/**
	 * Called when the player Left Clicks (attacks) an entity.
	 * Processed before damage is done, if return value is true further processing is canceled
	 * and the entity is not attacked.
	 *
	 * @param stack The Item being used
	 * @param entityLiving The player that is attacking
	 * @return True to cancel the rest of the interaction.
	 */
	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		if (entityLiving instanceof EntityPlayer) {
			PlayerData data = PlayerData.get((EntityPlayer) entityLiving);
			float balance = data.getVariable(PlayerTickHandler.BALANCE_PITCH_KEY);

			if (!allowOffhand(entityLiving, EnumHand.OFF_HAND)) {
				if (entityLiving.isRiding() && entityLiving.getRidingEntity() instanceof EntityCogwork) {
					return false;
				}
				if (!entityLiving.world.isRemote && balance == 0) {
					entityLiving.sendMessage(new TextComponentTranslation("info.offBalance.message"));
				}
				TacticalManager.throwPlayerOffBalance((EntityPlayer) entityLiving, 5F);
				StaminaBar.modifyStaminaValue(entityLiving, -95F);
			}
		}
		return false;
	}

	/**
	 * Called when the player Left Clicks (attacks) an entity.
	 * Processed before damage is done, if return value is true further processing is canceled
	 * and the entity is not attacked.
	 *
	 * @param stack  The Item being used
	 * @param player The player that is attacking
	 * @param entity The entity being attacked
	 * @return True to cancel the rest of the interaction.
	 */
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		return player.isHandActive() && stack.getItemUseAction() == EnumAction.valueOf("mfr_block");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!(player.getHeldItemOffhand().getItem() instanceof ItemShield)) {
			player.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack item) {
		return 72000;
	}

	public boolean allowOffhand(EntityLivingBase entity, EnumHand hand) {
		return true;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack item) {
		//if it is an MFR item in the offhand, it should remove the block option
		if (item.getTagCompound() != null && item.getTagCompound().hasKey("mfr_offhand")) {
			return EnumAction.NONE;
		}
		else {
			return EnumAction.valueOf("mfr_block");
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;

			//Handle adding and removing the mfr_offhand tag when the current MFR item matches the offhand
			ItemStack offhand = player.getHeldItemOffhand();
			if (stack.getItem() instanceof ItemWeaponMFR && offhand.getItem() instanceof ItemWeaponMFR) {
				if (stack.getTagCompound() != null && !stack.getTagCompound().hasKey("mfr_offhand")) {
					stack.getOrCreateSubCompound("mfr_offhand");
				}
			}
			else {
				if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("mfr_offhand")) {
					stack.getTagCompound().removeTag("mfr_offhand");
				}
			}
		}
	}

	protected void addXp(EntityLivingBase user, int chance) {
		if (ConfigWeapon.xpTrain && user instanceof EntityPlayer && material == ToolMaterial.WOOD) {
			if (chance == 0 || user.getRNG().nextInt(chance) == 0) {
				((EntityPlayer) user).addExperience(1);
			}
		}
	}

	@Override
	public void onParry(DamageSource source, EntityLivingBase user, Entity attacker, float dam) {
		ItemStack weapon = user.getHeldItemMainhand();
		int pd = getParryDamage(dam);
		if (pd > 0) {
			weapon.damageItem(pd, user);
		}

		addXp(user, 30);
	}

	/**
	 * Gets the amount of durability lost when parrying
	 */
	protected int getParryDamage(float dam) {
		return (int) dam;
	}

	@Override
	public float getMaxDamageParry(EntityLivingBase user, ItemStack weapon) {
		float mod = 1.0F;
		return getMeleeDamage(weapon) * getParryDamageModifier(user) * mod;
	}

	@Override
	public float getParryAngle(DamageSource source, EntityLivingBase blocker, ItemStack item) {
		float base = 30;

		if (source.isProjectile()) {
			if (!(blocker instanceof EntityPlayer)) {
				base = 0F;
			}
			base *= 0.25F;
		}
		if (blocker instanceof EntityZombie && !(blocker instanceof EntityPigZombie)) {
			base *= 2;
		}
		return base * getParryAngleModifier(blocker);
	}

	@Override
	public void onProperHit(EntityLivingBase user, ItemStack weapon, Entity hit, float dam) {
		if (ConfigWeapon.xpTrain && user != null && material == ToolMaterial.WOOD) {
			addXp(user, 50);
		}
	}

	protected float getKnockbackStrength() {
		return 0;
	}

	@Override
	public boolean playCustomParrySound(EntityLivingBase blocker, Entity attacker, ItemStack weapon) {
		if (material == ToolMaterial.WOOD) {
			blocker.world.playSound(null, blocker.getPosition(), MineFantasySounds.WOOD_PARRY, SoundCategory.NEUTRAL, 1.0F, 1.0F);
			return true;
		}
		if (material == MineFantasyMaterials.STONE.getToolMaterial()) {
			blocker.world.playSound(null, blocker.getPosition(), MineFantasySounds.WOOD_PARRY, SoundCategory.NEUTRAL, 1.0F, 0.5F);
			return true;
		}
		return false;
	}

	@Override
	public boolean canParry(DamageSource source, EntityLivingBase blocker, ItemStack item) {
		return canWeaponParry();
	}

	@Override
	public float getBalance(EntityLivingBase user) {
		return getBalance();
	}

	public float getBalance() {
		return 0.0F;
	}

	@Override
	public float getStaminaDrainOnHit(EntityLivingBase user, ItemStack item) {
		return 2F * getStaminaMod() * getWeightModifier(item);
	}

	protected float getStaminaMod() {
		return 1.0F;
	}

	@Override
	public float getDecayMod(EntityLivingBase user, ItemStack item) {
		return getDecayModifier(user, item) * getWeightModifier(item);
	}

	/**
	 * The Modifier for the weapon
	 */
	public float getDecayModifier(EntityLivingBase user, ItemStack item) {
		return 1.0F;
	}

	@Override
	public float getRegenModifier(EntityLivingBase user, ItemStack item) {
		return 1.0F;
	}

	@Override
	public float getIdleModifier(EntityLivingBase user, ItemStack item) {
		return 1.0F;
	}

	protected void hurtInRange(EntityLivingBase user, double range) {
		AxisAlignedBB bb = user.getEntityBoundingBox().expand(range, range, range);
		List<Entity> hurt = user.world.getEntitiesWithinAABBExcludingEntity(user, bb);
		for (Entity hit : hurt) {
			if (user.canEntityBeSeen(hit)) {
				TacticalManager.knockbackEntity(hit, user, 1.5F, 0.2F);
				if (ConfigStamina.isSystemActive) {
					if (user instanceof EntityPlayer) {
						StaminaBar.setIdleTime(PlayerData.get((EntityPlayer) user), 60);
					}
				}
				if (hit instanceof EntityLivingBase) {
					for (int a = 0; a < 4; a++) {
						hit.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, hit.posX, hit.posY + hit.getEyeHeight(), hit.posZ, rand.nextDouble() / 2D, rand.nextDouble() / 2D, rand.nextDouble() / 2D);
					}
					((EntityLivingBase) hit).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 0));
				}
			}
		}
	}

	@Override
	public boolean canUserParry(EntityLivingBase user) {
		return user instanceof EntityPlayer || user instanceof ISpecialCombatMob || (canAnyMobParry() || rand.nextFloat() < 0.20F);
	}

	protected boolean canAnyMobParry() {
		return false;
	}

	@Override
	public float getAddedKnockback(EntityLivingBase user, ItemStack item) {
		return getKnockbackStrength();
	}

	@Override
	public float[] getDamageRatio(Object... implement) {
		if (implement.length > 1) {
			return getWeaponRatio((ItemStack) implement[0], (EntityLivingBase) implement[1]);
		}
		return getWeaponRatio((ItemStack) implement[0]);
	}

	@Override
	public float getPenetrationLevel(Object implement) {
		ItemStack item = (ItemStack) implement;
		return 0F;
	}

	protected float[] getWeaponRatio(ItemStack implement) {
		return slashingDamage;
	}

	protected float[] getWeaponRatio(ItemStack implement, EntityLivingBase user) {
		if (canCounter(user, implement) == 1) {
			return getCounterRatio();
		}
		return getWeaponRatio(implement);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab)) {
			return;
		}
		if (isCustom) {
			ArrayList<CustomMaterial> metal = CustomMaterialRegistry.getList(CustomMaterialType.METAL_MATERIAL);
			for (CustomMaterial customMat : metal) {
				if (MineFantasyReforged.isDebug() || customMat.getMaterialIngredient() != Ingredient.EMPTY) {
					items.add(this.construct(customMat.getName(), MineFantasyMaterials.Names.OAK_WOOD));
				}
			}
			return;
		}

		if (this == MineFantasyItems.TRAINING_SWORD) {
			items.add(new ItemStack(MineFantasyItems.TRAINING_SWORD));
		}
		if (this == MineFantasyItems.TRAINING_WARAXE) {
			items.add(new ItemStack(MineFantasyItems.TRAINING_WARAXE));
		}
		if (this == MineFantasyItems.TRAINING_MACE) {
			items.add(new ItemStack(MineFantasyItems.TRAINING_MACE));
		}
		if (this == MineFantasyItems.TRAINING_SPEAR) {
			items.add(new ItemStack(MineFantasyItems.TRAINING_SPEAR));
		}
		if (this == MineFantasyItems.STONE_KNIFE) {
			items.add(new ItemStack(MineFantasyItems.STONE_KNIFE));
		}
		if (this == MineFantasyItems.STONE_SWORD) {
			items.add(new ItemStack(MineFantasyItems.STONE_SWORD));
		}
		if (this == MineFantasyItems.STONE_WARAXE) {
			items.add(new ItemStack(MineFantasyItems.STONE_WARAXE));
		}
		if (this == MineFantasyItems.STONE_MACE) {
			items.add(new ItemStack(MineFantasyItems.STONE_MACE));
		}
		if (this == MineFantasyItems.STONE_SPEAR) {
			items.add(new ItemStack(MineFantasyItems.STONE_SPEAR));
		}
	}

	@Override
	public float modifyDamage(ItemStack item, EntityLivingBase wielder, Entity hit, float initialDam, boolean properHit) {
		if (canCounter(wielder, item) == 1) {
			initialDam *= getCounterDamage();
		}
		if (canCounter(wielder, item) == 0) {
			initialDam *= getFailCounterDamage();
		}
		return initialDam;
	}

	@Override
	public float getParryStaminaDecay(DamageSource source, ItemStack weapon) {
		return 1.0F;
	}

	@Override
	public int getParryCooldown(DamageSource source, float dam, ItemStack weapon) {
		return 15;
	}

	@Override
	public int getParryModifier(ItemStack weapon, EntityLivingBase user, Entity target) {
		return 30;
	}

	@Override
	public void onPowerAttack(float dam, EntityLivingBase user, Entity target, boolean properHit) {

	}

	public boolean canCounter() {
		return true;
	}

	public float[] getCounterRatio() {
		return piercingDamage;
	}

	public float getCounterDamage() {
		return 0.75F;
	}

	public float getFailCounterDamage() {
		return 0.5F;
	}

	/*
	 * 0 = Cannot Counter 1 = Can Counter -1 = Not Possible
	 */
	public static int canCounter(EntityLivingBase user, ItemStack item) {
		if (user instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) user;
			if (getParry(item) > 0 && item.getItem() instanceof ItemWeaponMFR) {
				if (ResearchLogic.hasInfoUnlocked(player, "counter_attack") && ((ItemWeaponMFR)item.getItem()).canCounter()) {
					return 1;// Can
				}
				return 0;// Cannot
			}
		}
		return -1;// N/A
	}

	public ItemWeaponMFR setCustom(String s) {
		canRepair = false;
		designType = s;
		isCustom = true;
		return this;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		if (slot != EntityEquipmentSlot.MAINHAND) {
			return super.getAttributeModifiers(slot, stack);
		}

		Multimap<String, AttributeModifier> map = HashMultimap.create();
		map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", getMeleeDamage(stack), 0));
		map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", getAttackSpeed(stack), 0));
		return map;
	}

	public ItemWeaponMFR modifyBaseDamage(float mod) {
		this.baseDamage = Math.max(1.0F, baseDamage + mod);
		return this;
	}

	public ItemWeaponMFR setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
		return this;
	}

	/**
	 * Gets a stack-sensitive value for the melee dmg
	 */
	protected float getMeleeDamage(ItemStack item) {
		return baseDamage + CustomToolHelper.getMeleeDamage(item, material.getAttackDamage());
	}

	/**
	 * Gets a stack-sensitive value for the attack speed
	 */
	public float getAttackSpeed(ItemStack item) {
		return 0;
	}

	protected float getWeightModifier(ItemStack stack) {
		return CustomToolHelper.getWeightModifier(stack, materialWeight);
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return CustomToolHelper.getMaxDamage(stack, super.getMaxDamage(stack));
	}

	public ItemStack construct(String main, String haft) {
		return CustomToolHelper.construct(this, main, haft);
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		return CustomToolHelper.getRarity(item, itemRarity);
	}

	/**
	 * ItemStack sensitive version of getItemEnchantability
	 *
	 * @param stack The ItemStack
	 * @return the item enchantability value
	 */
	@Override
	public int getItemEnchantability(ItemStack stack) {
		return CustomToolHelper.getCustomPrimaryMaterial(stack).getEnchantability();
	}

	// ====================================================== CUSTOM END
	// ==============================================================\\

	@Override
	public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
		if (ItemStack.areItemsEqualIgnoreDurability(oldStack, newStack)) {
			return true;
		}
		return super.canContinueUsing(oldStack, newStack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		// This method does some VERY strange things! Despite its name, it also seems to affect the updating of NBT...

		if(!oldStack.isEmpty() || !newStack.isEmpty()){
			// We only care about the situation where we specifically want the animation NOT to play.
			if(oldStack.getItem() == newStack.getItem() && !slotChanged)
				return false;
		}

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		String unlocalName = this.getUnlocalizedNameInefficiently(item) + ".name";
		return CustomToolHelper.getLocalisedName(item, unlocalName);
	}

	public ItemWeaponMFR setTab(CreativeTabs tab) {
		setCreativeTab(tab);
		return this;
	}

	@Override
	public float getScale(ItemStack itemstack) {
		return 1.0F;
	}

	@Override
	public float getOffsetX(ItemStack itemstack) {
		return 0;
	}

	@Override
	public float getOffsetY(ItemStack itemstack) {
		if (this.getScale(itemstack) > 1.5F) {
			return 2.0F;
		}
		return 0;
	}

	@Override
	public float getOffsetZ(ItemStack itemstack) {
		if (this.getScale(itemstack) > 1.5F) {
			return -0.1F;
		}
		return 0;
	}

	@Override
	public float getRotationOffset(ItemStack itemstack) {
		if (getScale(itemstack) > 1.5F){
			return 90;
		}
		return 0;
	}

	@Override
	public boolean canHang(TileEntityRack rack, ItemStack item, int slot) {
		float scale = this.getScale(item);
		if (scale > 1.5F && !rack.hasRackBelow(slot)) {
			return false;
		}
		return !(scale > 2.5F) || rack.hasRackAbove(slot);
	}

	@Override
	public boolean flip(ItemStack itemStack) {
		return false;
	}

	@Override
	public String getDesign(ItemStack item) {
		return designType;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this);
	}
}

