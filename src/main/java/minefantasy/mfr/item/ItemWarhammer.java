package minefantasy.mfr.item;

import minefantasy.mfr.client.render.item.RenderBigTool;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.constants.WeaponClass;
import minefantasy.mfr.init.MineFantasySounds;
import minefantasy.mfr.util.ModelLoaderHelper;
import minefantasy.mfr.util.TacticalManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Anonymous Productions
 */
public class ItemWarhammer extends ItemHeavyWeapon {
	private float stunChance = 0.20F;

	/**
	 * Warhammers are heavy counterparts to maces: They have more damage and
	 * knockback
	 * <p>
	 * They can also strike heavy forces, being rather satisfying
	 */
	public ItemWarhammer(String name, ToolMaterial material, Rarity rarity, float weight) {
		super(material, name, rarity, weight);
		this.setMaxDamage((int) (getMaxDamage() * 2F));
	}

	@Override
	protected int getParryDamage(float dam) {
		return (int) (dam * 2F);
	}

	@Override
	public float getDestroySpeed(ItemStack itemstack, IBlockState block) {
		return super.getDestroySpeed(itemstack, block) * 1.5F;
	}

	@Override
	public void onProperHit(EntityLivingBase user, ItemStack weapon, Entity hit, float dam) {
		if (!user.world.isRemote && user.getRNG().nextInt(5) == 0) {
			hit.world.createExplosion(user, hit.posX, hit.posY, hit.posZ, 0.0F, false);
			TacticalManager.knockbackEntity(hit, user, 2.0F, 1.5F);
			if (hit instanceof EntityLivingBase) {
				((EntityLivingBase) hit).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 10));
				((EntityLivingBase) hit).addPotionEffect(new PotionEffect(MobEffects.HASTE, 200, 1));
			}
		}
		super.onProperHit(user, weapon, hit, dam);
	}

	@Override
	public boolean playCustomParrySound(EntityLivingBase blocker, Entity attacker, ItemStack weapon) {
		blocker.world.playSound(null, blocker.getPosition(), MineFantasySounds.WOOD_PARRY, SoundCategory.NEUTRAL, 1.0F, 0.7F);
		return true;
	}

	@Override
	protected float getKnockbackStrength() {
		return 2.0F;
	}

	@Override
	protected float getStaminaMod() {
		return heavyStaminaCost * maceStaminaCost;
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
		return maceParryTime + heavyParryTime;
	}

	@Override
	public int getParryModifier(ItemStack weapon, EntityLivingBase user, Entity target) {
		return 80;
	}

	@Override
	public WeaponClass getWeaponClass() {
		return WeaponClass.BLUNT;
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
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
		ModelLoaderHelper.registerWrappedItemModel(this, new RenderBigTool(() -> modelLocation, 2F, -0.27F), modelLocation);
	}
}
