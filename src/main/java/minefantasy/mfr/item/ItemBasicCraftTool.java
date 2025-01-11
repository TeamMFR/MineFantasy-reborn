package minefantasy.mfr.item;

import com.google.common.collect.Sets;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.tier.IToolMaterial;
import minefantasy.mfr.api.tool.IToolMFR;
import minefantasy.mfr.api.weapon.IDamageType;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.init.MineFantasyMaterials;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.proxy.IClientRegister;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.ModelLoaderHelper;
import minefantasy.mfr.util.ToolHelper;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * @author Anonymous Productions
 */
public class ItemBasicCraftTool extends ItemTool implements IToolMaterial, IToolMFR, IDamageType, IClientRegister {
	protected Rarity itemRarity;
	private int tier;
	private Tool toolType;
	// ===================================================== CUSTOM START
	// =============================================================\\
	private boolean isCustom = false;

	public ItemBasicCraftTool(String name, Tool type, int tier, int uses) {
		super(1.0F, 1.0F, ToolMaterial.WOOD, Sets.newHashSet(new Block[] {}));
		this.tier = tier;
		this.itemRarity = Rarity.COMMON;
		setCreativeTab(MineFantasyTabs.tabCraftTool);

		toolType = type;
		setRegistryName(name);
		setTranslationKey(name);

		setMaxDamage(uses);
		this.setMaxStackSize(1);

		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer user, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return ToolHelper.performBlockTransformation(user, world, pos, hand, facing);
	}

	@Override
	public ToolMaterial getMaterial() {
		return toolMaterial;
	}

	@Override
	public Tool getToolType(ItemStack stack) {
		return toolType;
	}

	@Override
	public float[] getDamageRatio(Object... implement) {
		return new float[] {0, 1, 0};
	}

	@Override
	public float getPenetrationLevel(Object implement) {
		return 0F;
	}

	public ItemBasicCraftTool setCustom(String s) {
		canRepair = false;
		isCustom = true;
		return this;
	}

	public ItemStack construct(String main) {
		return CustomToolHelper.construct(this, main);
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return CustomToolHelper.getMaxDamage(stack, super.getMaxDamage(stack));
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab)) {
			return;
		}
		if (isCustom) {
			items.add(this.construct(MineFantasyMaterials.Names.OAK_WOOD));
			items.add(this.construct(MineFantasyMaterials.Names.IRONBARK_WOOD));
			items.add(this.construct(MineFantasyMaterials.Names.EBONY_WOOD));
		} else {
			super.getSubItems(tab, items);
		}
	}

	@Override
	public void addInformation(ItemStack item, World world, List<String> list, ITooltipFlag flag) {
		super.addInformation(item, world, list, flag);
	}

	@Override
	public float getEfficiency(ItemStack item) {
		float efficiencyMod = 1.0F;
		return CustomToolHelper.getEfficiencyForHds(item, toolMaterial.getEfficiency(), efficiencyMod);
	}

	@Override
	public int getTier(ItemStack item) {
		return CustomToolHelper.getCrafterTier(item, tier);
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		String unlocalName = this.getUnlocalizedNameInefficiently(item) + ".name";
		return CustomToolHelper.getLocalisedName(item, unlocalName);
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		return CustomToolHelper.getRarity(item, itemRarity);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this);
	}
}
