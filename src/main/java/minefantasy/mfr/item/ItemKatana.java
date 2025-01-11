package minefantasy.mfr.item;

import minefantasy.mfr.client.render.item.RenderBigTool;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.constants.WeaponClass;
import minefantasy.mfr.util.ArmourCalculator;
import minefantasy.mfr.util.ModelLoaderHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Anonymous Productions
 */
public class ItemKatana extends ItemHeavyWeapon {

	/**
	 * Katanas are heavy counterparts to Tantos, unlike most heavy weapons: these
	 * act more like light-weapons
	 */
	public ItemKatana(String name, ToolMaterial material, Rarity rarity, float weight) {
		super(material, name, rarity, weight);
	}

	@Override
	public boolean allowOffhand(EntityLivingBase entity, EnumHand hand) {
		return true;
	}

	/**
	 * Determines if the weapon can parry
	 */
	@Override
	public boolean canWeaponParry() {
		return true;
	}

	@Override
	protected int getParryDamage(float dam) {
		return 1;
	}

	/**
	 * gets the time after being hit your guard will be let down
	 */
	@Override
	public int getParryCooldown(EntityLivingBase user) {
		return 8;
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
	 *
	 * @return Parry Damage modifier
	 */
	@Override
	public float getParryDamageModifier(EntityLivingBase user) {
		return 1.5F;
	}

	@Override
	public boolean playCustomParrySound(EntityLivingBase blocker, Entity attacker, ItemStack weapon) {
		blocker.world.playSound(null, blocker.getPosition(), SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.NEUTRAL, 1.0F, 0.75F);
		return true;
	}

	@Override
	public float getBalance() {
		return 0.0F;
	}

	@Override
	public float getDecayModifier(EntityLivingBase user, ItemStack item) {
		return 0.75F;
	}

	@Override
	public float getRegenModifier(EntityLivingBase user, ItemStack item) {
		return user.world.getDifficulty().getId() < 3 ? 1.25F : 1.0F;
	}

	@Override
	public void onProperHit(EntityLivingBase user, ItemStack weapon, Entity hit, float dam) {
		if (user.motionY < 0 && !user.onGround && ((user instanceof EntityPlayer) || user.isSneaking())
				&& tryPerformAbility((EntityPlayer) user, cleave_cost)) {
			hurtInRange(user, 4D);
			if (hit instanceof EntityLivingBase) {
				ArmourCalculator.damageArmour((EntityLivingBase) hit, (int) (dam * 10));
			}
			user.setSneaking(false);
		}
		super.onProperHit(user, weapon, hit, dam);
	}

	@Override
	public float getAttackSpeed(ItemStack item) {
		return speedKatana;
	}

	@Override
	protected float getKnockbackStrength() {
		return -0.5F;
	}

	@Override
	public float getParryStaminaDecay(DamageSource source, ItemStack weapon) {
		return 1.0F;
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
		return katanaStaminaCost;
	}

	@Override
	public WeaponClass getWeaponClass() {
		return WeaponClass.BLADE;
	}

	@Override
	protected float[] getWeaponRatio(ItemStack implement) {
		return slashingDamage;
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
		return 1.5F;
	}

	@Override
	protected float getMeleeDamage(ItemStack item) {
		return super.getMeleeDamage(item) * 0.5F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
		ModelLoaderHelper.registerWrappedItemModel(this, new RenderBigTool(() -> modelLocation, 2F, -0.4F, -15, 0.26f), modelLocation);
	}
}
