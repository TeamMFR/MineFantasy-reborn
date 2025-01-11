package minefantasy.mfr.item;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.tier.IToolMaterial;
import minefantasy.mfr.api.tool.IToolMFR;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.init.MineFantasyMaterials;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.proxy.IClientRegister;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.ModelLoaderHelper;
import minefantasy.mfr.util.ToolHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anonymous Productions
 */
public class ItemShearsMFR extends ItemShears implements IToolMaterial, IToolMFR, IClientRegister {
	protected Rarity itemRarity;
	private final ToolMaterial toolMaterial;
	private final int tier;
	// ===================================================== CUSTOM START
	// =============================================================\\
	private boolean isCustom = false;
	private float efficiencyMod = 1.0F;

	public ItemShearsMFR(String name, ToolMaterial material, Rarity rarity, int tier) {
		super();
		this.tier = tier;
		itemRarity = rarity;
		toolMaterial = material;
		setCreativeTab(MineFantasyTabs.tabOldTools);
		this.setMaxDamage(material.getMaxUses());
		setRegistryName(name);
		setTranslationKey(name);

		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	public ItemShearsMFR setCustom() {
		canRepair = false;
		isCustom = true;
		return this;
	}

	public ItemShearsMFR setEfficiencyMod(float efficiencyMod) {
		this.efficiencyMod = efficiencyMod;
		return this;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer user, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return ToolHelper.performBlockTransformation(user, world, pos, hand, facing);
	}

	protected float getWeightModifier(ItemStack stack) {
		return CustomToolHelper.getWeightModifier(stack, 1.0F);
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

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
		return CustomToolHelper.getHarvestLevel(stack, super.getHarvestLevel(stack, toolClass, player, blockState));
	}

	/**
	 * ItemStack sensitive version of getItemEnchantability
	 *
	 * @param stack The ItemStack
	 * @return the item echantability value
	 */
	@Override
	public int getItemEnchantability(ItemStack stack) {
		return CustomToolHelper.getCustomPrimaryMaterial(stack).getEnchantability();
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
		} else {
			super.getSubItems(tab, items);
		}
	}

	@Override
	public void addInformation(ItemStack item, World world, List<String> list, ITooltipFlag flag) {
		if (isCustom) {
			CustomToolHelper.addInformation(item, list);
		}
		super.addInformation(item, world, list, flag);
	}

	@Override
	public float getEfficiency(ItemStack item) {
		return CustomToolHelper.getEfficiency(item, toolMaterial.getEfficiency(), efficiencyMod);
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
	public Tool getToolType(ItemStack stack) {
		return Tool.SHEARS;
	}

	@Override
	public ToolMaterial getMaterial() {
		return toolMaterial;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this);
	}
}
