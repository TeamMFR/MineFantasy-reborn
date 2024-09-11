package minefantasy.mfr.item;

import com.google.common.collect.Multimap;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.tier.IToolMaterial;
import minefantasy.mfr.api.tool.IToolMFR;
import minefantasy.mfr.api.weapon.IDamageType;
import minefantasy.mfr.api.weapon.IRackItem;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.init.MineFantasyMaterials;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.proxy.IClientRegister;
import minefantasy.mfr.tile.TileEntityRack;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.ModelLoaderHelper;
import minefantasy.mfr.util.ToolHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anonymous Productions
 */
public class ItemSaw extends ItemAxe implements IToolMaterial, IDamageType, IToolMFR, IRackItem, IClientRegister {
	protected int itemRarity;
	private final float hitDamage;
	private float baseDamage;
	private final String name;
	private final int tier;
	// ===================================================== CUSTOM START
	// =============================================================\\
	private boolean isCustom = false;
	private float efficiencyMod = 1.0F;

	/**
	 *
	 */
	public ItemSaw(String name, ToolMaterial material, int rarity, int tier) {
		super(material, material.getAttackDamage(), 1.0F);
		this.tier = tier;
		itemRarity = rarity;
		setCreativeTab(MineFantasyTabs.tabOldTools);
		this.hitDamage = (2.0F + material.getAttackDamage()) / 2F;
		setRegistryName(name);
		setTranslationKey(name);

		this.name = name;
		this.setHarvestLevel("axe", material.getHarvestLevel());

		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	@Override
	public ToolMaterial getMaterial() {
		return toolMaterial;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		if (slot != EntityEquipmentSlot.MAINHAND) {
			return super.getAttributeModifiers(slot, stack);
		}

		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
				new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", getMeleeDamage(stack), 0));
		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
				new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3F, 0));
		return multimap;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer user, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return ToolHelper.performBlockTransformation(user, world, pos, hand, facing);
	}

	@Override
	public float[] getDamageRatio(Object... implement) {
		return new float[] {1, 0, 0};
	}

	@Override
	public Tool getToolType(ItemStack stack) {
		return Tool.SAW;
	}

	@Override
	public float getPenetrationLevel(Object implement) {
		return 0.5F;
	}

	public ItemSaw setCustom(String s) {
		canRepair = false;
		isCustom = true;
		return this;
	}

	public ItemSaw setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
		return this;
	}

	@Override
	public float getEfficiency(ItemStack item) {
		return CustomToolHelper.getEfficiency(item, toolMaterial.getEfficiency(), efficiencyMod);
	}

	@Override
	public int getTier(ItemStack item) {
		return CustomToolHelper.getCrafterTier(item, tier);
	}

	public ItemSaw setEfficiencyMod(float efficiencyMod) {
		this.efficiencyMod = efficiencyMod;
		return this;
	}

	/**
	 * Gets a stack-sensitive value for the melee dmg
	 */
	protected float getMeleeDamage(ItemStack item) {
		return baseDamage + CustomToolHelper.getMeleeDamage(item, toolMaterial.getAttackDamage());
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

	public float getDigSpeed(ItemStack stack, Block block, World world, BlockPos pos, EntityPlayer player) {
		if (!ForgeHooks.isToolEffective(world, pos, stack)) {
			return this.getDestroySpeed(stack, block);
		}
		float digSpeed = player.getDigSpeed(block.getDefaultState(), pos);
		return CustomToolHelper.getEfficiency(stack, digSpeed, efficiencyMod / 10);
	}

	public float getDestroySpeed(ItemStack stack, Block block) {
		return block.getMaterial(block.getDefaultState()) != Material.IRON && block.getMaterial(block.getDefaultState()) != Material.ANVIL
				&& block.getMaterial(block.getDefaultState()) != Material.ROCK ? super.getDestroySpeed(stack, block.getDefaultState())
				: CustomToolHelper.getEfficiency(stack, this.efficiency, efficiencyMod / 2);
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass, EntityPlayer player, IBlockState state) {
		return CustomToolHelper.getHarvestLevel(stack, super.getHarvestLevel(stack, toolClass, player, state));
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
	public void addInformation(ItemStack item, World world, List list, ITooltipFlag flag) {
		if (isCustom) {
			CustomToolHelper.addInformation(item, list);
		}
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
	public float getScale(ItemStack itemstack) {
		return 1.0F;
	}

	@Override
	public float getOffsetX(ItemStack itemstack) {
		return 0.45F;
	}

	@Override
	public float getOffsetY(ItemStack itemstack) {
		return 1F;
	}

	@Override
	public float getOffsetZ(ItemStack itemstack) {
		return 0.5F;
	}

	@Override
	public float getRotationOffset(ItemStack itemstack) {
		return 90F;
	}

	@Override
	public boolean canHang(TileEntityRack rack, ItemStack item, int slot) {
		return true;
	}

	@Override
	public boolean flip(ItemStack itemStack) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this);
	}
}
