package minefantasy.mfr.item;

import com.google.common.collect.Multimap;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.tier.IToolMaterial;
import minefantasy.mfr.config.ConfigItemRegistry;
import minefantasy.mfr.init.MineFantasyMaterials;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.material.BaseMaterial;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.proxy.IClientRegister;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.ModelLoaderHelper;
import minefantasy.mfr.util.ToolHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static minefantasy.mfr.material.CustomMaterial.decimal_format;

/**
 * @author Anonymous Productions
 */
public class ItemPickMFR extends ItemPickaxe implements IToolMaterial, IClientRegister {
	protected int itemRarity;
	private float baseDamage = 2F;
	// ===================================================== CUSTOM START
	// =============================================================\\
	private boolean isCustom = false;
	private float efficiencyMod = 1.0F;

	public ItemPickMFR(String name, ToolMaterial material, int rarity) {
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

	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItemMainhand();

		if (!world.isRemote) {
			return super.onItemRightClick(world, player, hand);
		}

		if (!player.getHeldItemOffhand().isEmpty()) {
			return super.onItemRightClick(world, player, hand);
		}

		RayTraceResult rayTraceResult = this.rayTrace(world, player, true);

		if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {

			if (!world.canMineBlockBody(player, rayTraceResult.getBlockPos())) {
				return super.onItemRightClick(world, player, hand);
			}

			if (!player.canPlayerEdit(rayTraceResult.getBlockPos(), rayTraceResult.sideHit, item)) {
				return super.onItemRightClick(world, player, hand);
			}

			IBlockState state = world.getBlockState(rayTraceResult.getBlockPos());
			int blockTier = state.getBlock().getHarvestLevel(world.getBlockState(rayTraceResult.getBlockPos()));

			//Check if the pick is an MFR stone pick, and have it pull up its custom check for harvest level
			//Mods make this necessary by not having soft copper and tin ore blocks
			int harvestLevel = CustomToolHelper.getHarvestLevel(item, toolMaterial.getHarvestLevel());
			if (ToolHelper.isItemMaterial(item, BaseMaterial.getMaterial("stone").getToolMaterial())) {
				harvestLevel = getStonePickHarvestLevel(state);
			}

			if (player.world.isRemote) {
				if (blockTier > harvestLevel) {
					String msg = I18n.format("prospect.cannotmine", harvestLevel, blockTier);
					player.sendMessage(new TextComponentString(TextFormatting.RED + msg));
				} else {
					String msg = I18n.format("prospect.canmine", harvestLevel, blockTier);
					player.sendMessage(new TextComponentString(TextFormatting.GREEN + msg));
				}
			}
		}

		return super.onItemRightClick(world, player, hand);
	}

	public ItemPickMFR setCustom(String s) {
		canRepair = false;
		isCustom = true;
		return this;
	}

	public ItemPickMFR setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
		return this;
	}

	public ItemPickMFR setEfficiencyMod(float efficiencyMod) {
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
				new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.8F, 0));
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
		CustomMaterial material = CustomToolHelper.getCustomPrimaryMaterial(stack);
		float efficiency = material.hardness > 0 ? material.hardness : this.efficiency;
		return !state.getBlock().isToolEffective("pickaxe", state)
				? super.getDestroySpeed(stack, state)
				: CustomToolHelper.getEfficiency(stack, efficiency, efficiencyMod / 2F);
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
		if (ToolHelper.isItemMaterial(stack, BaseMaterial.getMaterial("stone").getToolMaterial())) {
			return getStonePickHarvestLevel(blockState);
		}
		return CustomToolHelper.getHarvestLevel(stack, super.getHarvestLevel(stack, toolClass, player, blockState));
	}

	private int getStonePickHarvestLevel(IBlockState state) {
		if (state != null && ConfigItemRegistry.customStonePickOverride.length > 0) {
			if (Arrays.stream(ConfigItemRegistry.customStonePickOverride).anyMatch(s -> state.toString().equalsIgnoreCase(s))) {
				return state.getBlock().getHarvestLevel(state);
			}
		}
		return 0;
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

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this);
	}

	// ====================================================== CUSTOM END
	// ==============================================================\\
}
