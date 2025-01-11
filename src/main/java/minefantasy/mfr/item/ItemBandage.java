package minefantasy.mfr.item;

import minefantasy.mfr.MFREventHandler;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.data.IStoredVariable;
import minefantasy.mfr.data.Persistence;
import minefantasy.mfr.data.PlayerData;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.mechanics.knowledge.ResearchLogic;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBandage extends ItemBaseMFR {
	public static final IStoredVariable<Integer> BANDAGE_PROGRESS = IStoredVariable.StoredVariable.ofInt("bandage_progress", Persistence.DIMENSION_CHANGE);
	private static final String healingID = "MF_Bandage_progress";

	static {
		PlayerData.registerStoredVariables(BANDAGE_PROGRESS);
	}

	private final float healPower;
	private final float secondsToUse = 5F;

	public ItemBandage(String name, float healAmount) {
		super(name);
		healPower = healAmount;
		setMaxStackSize(16);
		setCreativeTab(MineFantasyTabs.tabGadget);
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		if (healPower <= 5) {
			return Rarity.POOR;
		}
		return super.getForgeRarity(item);
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed.
	 * Args: itemStack, world, entityPlayer
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		if (!player.isSneaking() && canHeal(player) && player.hurtResistantTime <= 0) {
			player.setActiveHand(hand);
			return ActionResult.newResult(EnumActionResult.PASS, item);
		}
		else{
			return ActionResult.newResult(EnumActionResult.FAIL, item);
		}
	}

	/**
	 * returns the action that specifies what animation to play when the items is
	 * being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack item) {
		return EnumAction.valueOf("mfr_block");
	}

	@Override
	public int getMaxItemUseDuration(ItemStack item) {
		return (int) (20F * secondsToUse);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack item, World world, EntityLivingBase player) {
		return heal((EntityPlayer) player, player, item);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		super.onUsingTick(stack, player, count);

		if (!canHeal(player) && !player.world.isRemote) {
			player.stopActiveHand();
		}
		if (count % 5 == 0) {
			player.playSound(SoundEvents.BLOCK_CLOTH_BREAK, 1F, 0.005F);
		}
		if (player instanceof EntityPlayer){
			int time = 0;
			if (ResearchLogic.hasInfoUnlocked((EntityPlayer) player, "first_aid")) {
				time = getMaxItemUseDuration(stack) / 3;
			}
			if (count == time){
				onItemUseFinish(stack, player.world, player);
			}
		}

		player.swingArm(player.getActiveHand());
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
		if (player.isSneaking() && isReadyToHeal(player, entity)) {
			heal(player, entity, itemstack);
			return true;
		}
		return super.itemInteractionForEntity(itemstack, player, entity, hand);
	}

	private ItemStack heal(EntityPlayer player, EntityLivingBase toHeal, ItemStack item) {
		if (player.world.isRemote) {
			return item;
		}

		if (toHeal != null) {
			if (canHeal(toHeal)) {
				toHeal.world.playSound(player, player.getPosition(), SoundEvents.BLOCK_CLOTH_BREAK, SoundCategory.AMBIENT, 1.0F, 3.0F);
				player.swingArm(player.getActiveHand());

				if (!player.world.isRemote) {
					float power = healPower;
					if (ResearchLogic.hasInfoUnlocked(player, "doctor")) {
						power *= 1.5F;
					}
					toHeal.heal(power);

					if (!player.capabilities.isCreativeMode) {
						item.shrink(1);
					}
				}
				toHeal.getEntityData().setInteger(Constants.INJURED_TAG, 0);
			}
		}
		return item;
	}

	private boolean canHeal(EntityLivingBase toHeal) {
		return toHeal.hurtResistantTime <= 0
				&& (toHeal.getHealth() <= (toHeal.getMaxHealth() - 1F) || MFREventHandler.getInjuredTime(toHeal) > 0)
				&& !toHeal.isBurning();
	}

	public boolean isReadyToHeal(EntityPlayer player, EntityLivingBase patient) {
		int time = getUserHealTime(patient) + 1;
		if (ResearchLogic.hasInfoUnlocked(player, "first_aid")) {
			time += 3;
		}
		patient.playSound(SoundEvents.BLOCK_CLOTH_BREAK, 1F, 0.005F);
		player.swingArm(player.getActiveHand());

		if (time >= (int) (4 * secondsToUse)) {
			setUserHealTime(patient, 0);
			return true;
		}

		setUserHealTime(patient, time);
		return false;
	}

	public void setUserHealTime(EntityLivingBase user, int value) {
		if (user instanceof EntityPlayer){
			PlayerData.get((EntityPlayer) user).setVariable(BANDAGE_PROGRESS, value);
			return;
		}
		if (user.getEntityData() != null) {
			user.getEntityData().setInteger(healingID, value);
		}
	}

	public int getUserHealTime(EntityLivingBase user) {
		if (user.world.isRemote) {
			return 0;
		}
		if (user instanceof EntityPlayer && PlayerData.get((EntityPlayer) user) != null) {
			return PlayerData.get((EntityPlayer) user).getVariable(BANDAGE_PROGRESS);
		}
		if (user.getEntityData() != null) {
			if (user.getEntityData().hasKey(healingID)) {
				return user.getEntityData().getInteger(healingID);
			}
		}
		return 0;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("item.bandage_heal_amount", healPower / 2));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
}
