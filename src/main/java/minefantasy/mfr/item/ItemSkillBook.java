package minefantasy.mfr.item;

import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.init.MineFantasySounds;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.mechanics.RPGElements;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemSkillBook extends ItemBaseMFR {
	private Skill skill;
	private boolean isMax = false;

	public ItemSkillBook(String name, Skill skill) {
		super(name);
		setMaxStackSize(16);
		this.setCreativeTab(MineFantasyTabs.tabGadget);
		this.skill = skill;
	}

	public Item setMax() {
		isMax = true;
		return setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack item, World world, List list, ITooltipFlag flag) {
		if (isMax) {
			list.add(I18n.format("item.skillbook_" + skill.unlocalizedName + "_max.desc"));
		} else {
			list.add(I18n.format("item.skillbook_" + skill.unlocalizedName + ".desc", 1));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		boolean used = false;
		if (skill != Skill.NONE) {
			int lvl = RPGElements.getLevel(player, skill);
			if (lvl < skill.getMaxLevel()) {
				if (isMax) {
					skill.manualLvlUp(player, 100);
				} else {
					skill.addXP(player, world.rand.nextInt(skill.getLvlXP(lvl)));
				}
				used = true;
			}
		}
		if (used) {
			player.world.playSound(player.posX, player.posY, player.posZ, MineFantasySounds.UPDATE_RESEARCH, SoundCategory.NEUTRAL, 1.0F, 1.0F, true);
			if (!player.capabilities.isCreativeMode) {
				item.shrink(1);
			}
		}
		return ActionResult.newResult(EnumActionResult.PASS, item);
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		return isMax ? Rarity.EPIC : Rarity.COMMON;
	}
}
