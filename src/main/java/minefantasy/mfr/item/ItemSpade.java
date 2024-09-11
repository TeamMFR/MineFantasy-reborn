package minefantasy.mfr.item;

import com.google.common.collect.Multimap;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.tier.IToolMaterial;
import minefantasy.mfr.init.MineFantasyMaterials;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.proxy.IClientRegister;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.ModelLoaderHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static minefantasy.mfr.material.CustomMaterial.decimal_format;

/**
 * @author Anonymous Productions
 */
public class ItemSpade extends net.minecraft.item.ItemSpade implements IToolMaterial, IClientRegister {
	protected int itemRarity;
	private float baseDamage = 1F;
	// ===================================================== CUSTOM START
	// =============================================================\\
	private boolean isCustom = false;
	private float efficiencyMod = 1.0F;

	public ItemSpade(String name, ToolMaterial material, int rarity) {
		super(material);
		itemRarity = rarity;
		setCreativeTab(MineFantasyTabs.tabOldTools);
		setRegistryName(name);
		setTranslationKey(name);

		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	@Override
	public ToolMaterial getMaterial() {
		return toolMaterial;
	}

	public ItemSpade setCustom(String s) {
		canRepair = false;
		isCustom = true;
		return this;
	}

	public ItemSpade setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
		return this;
	}

	public ItemSpade setEfficiencyMod(float efficiencyMod) {
		this.efficiencyMod = efficiencyMod;
		return this;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		if (slot != EntityEquipmentSlot.MAINHAND) {
			return super.getAttributeModifiers(slot, stack);
		}

		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
				new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", baseDamage, 0));
		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
				new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3F, 0));
		return multimap;
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
	public EnumRarity getRarity(ItemStack item) {
		return CustomToolHelper.getRarity(item, itemRarity);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		if (!state.getBlock().isToolEffective("shovel", state)) {
			return this.getSpadeDestroySpeed(stack, state);
		}
		return CustomToolHelper.getEfficiency(stack, super.getDestroySpeed(stack, state), efficiencyMod);
	}

	public float getSpadeDestroySpeed(ItemStack stack, IBlockState block) {
		float base = super.getDestroySpeed(stack, block);
		return base <= 1.0F ? base : CustomToolHelper.getEfficiency(stack, this.efficiency, efficiencyMod);
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
		return CustomToolHelper.getCustomPrimaryMaterial(stack).enchantability;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab)) {
			return;
		}
		if (isCustom) {
			ArrayList<CustomMaterial> metal = CustomMaterial.getList("metal");
			for (CustomMaterial customMat : metal) {
				if (MineFantasyReforged.isDebug() || !customMat.getItemStack().isEmpty()) {
					items.add(this.construct(customMat.name, MineFantasyMaterials.Names.OAK_WOOD));
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

		CustomMaterial material = CustomToolHelper.getCustomPrimaryMaterial(item);
		float efficiency = material.hardness > 0 ? material.hardness : this.efficiency;
		list.add(TextFormatting.GREEN + I18n.format("attribute.tool.digEfficiency.name",
				decimal_format.format(CustomToolHelper.getEfficiency(item, efficiency, efficiencyMod / 2F))));

		super.addInformation(item, world, list, flag);
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		String unlocalName = this.getUnlocalizedNameInefficiently(item) + ".name";
		return CustomToolHelper.getLocalisedName(item, unlocalName);
	}
	// ====================================================== CUSTOM END
	// ==============================================================\\

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this);
	}
}
