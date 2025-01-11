package minefantasy.mfr.item;

import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasySounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.Optional;

/**
 * @author Anonymous Productions
 */
@Optional.Interface(iface = "net.shadowmage.ancientwarfare.npc.item.IExtendedReachWeapon", modid = "ancientwarfarenpc")
public class ItemHalbeard extends ItemSpear {
	/**
	 * The halbeard is the heavy counterpart for the spear: It has increased damage,
	 * knockback distance and parry arc
	 * <p>
	 * Halbeards use a swinging attack rather than a stab, but will still stab when
	 * sprinting
	 */
	public ItemHalbeard(String name, ToolMaterial material, Rarity rarity, float weight) {
		super(name, material, rarity, weight);
		setMaxDamage(getMaxDamage() * 2);
	}

	@Override
	public boolean allowOffhand(EntityLivingBase entity, EnumHand hand) {
		return entity.getHeldItem(hand).isEmpty();
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
	public boolean playCustomParrySound(EntityLivingBase blocker, Entity attacker, ItemStack weapon) {
		blocker.world.playSound(null, blocker.getPosition(), MineFantasySounds.WOOD_PARRY, SoundCategory.NEUTRAL, 1.0F, 0.7F);
		return true;
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
		return user.isSneaking() ? 1.5F : 1.0F;
	}

	@Override
	public float getBalance() {
		return 0.8F;
	}

	@Override
	protected float getKnockbackStrength() {
		return 3.5F;
	}

	@Override
	public float getAttackSpeed(ItemStack item) {
		return super.getAttackSpeed(item) + -0.3F;
	}

	/**
	 * gets the time after being hit your guard will be let down
	 */
	@Override
	public int getParryCooldown(DamageSource source, float dam, ItemStack weapon) {
		return spearParryTime + heavyParryTime;
	}

	@Override
	protected float getStaminaMod() {
		return heavyStaminaCost * spearStaminaCost;
	}

	@Override
	protected float[] getWeaponRatio(ItemStack implement, EntityLivingBase user) {
		if (user.isSprinting()) {
			return piercingDamage;
		}
		return getWeaponRatio(implement);
	}

	@Override
	public float[] getCounterRatio() {
		return piercingDamage;
	}

	@Override
	protected float[] getWeaponRatio(ItemStack implement) {
		return heavyHackingDamage;
	}

	@Override
	protected float getMeleeDamage(ItemStack item) {
		return super.getMeleeDamage(item) * 1.5F;
	}

	@Override
	protected float getMountedDamage() {
		return 0;
	}

	@Override
	public float getScale(ItemStack itemstack) {
		return 3.0F;
	}

	@Override
	public float getOffsetX(ItemStack itemstack) {
		return 1.85F;
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
	public boolean flip(ItemStack itemStack) {
		return true;
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
