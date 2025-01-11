package minefantasy.mfr.item;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.tier.IToolMaterial;
import minefantasy.mfr.api.tool.IToolMFR;
import minefantasy.mfr.api.weapon.IDamageType;
import minefantasy.mfr.block.BlockRack;
import minefantasy.mfr.block.BlockRepairKit;
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
import minefantasy.mfr.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockLever;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Anonymous Productions
 */

public class ItemSpanner extends ItemTool implements IToolMaterial, IToolMFR, IDamageType, IClientRegister {
	protected Rarity itemRarity;
	private final ToolMaterial material;
	private final int tier;
	private float baseDamage;
	private boolean isCustom = false;
	private float efficiencyMod = 1.0F;

	private final Set<Class<? extends Block>> shiftRotations = new HashSet<Class<? extends Block>>();
	private final Set<Class<? extends Block>> blacklistedRotations = new HashSet<Class<? extends Block>>();

	public ItemSpanner(String name, Rarity rarity, int tier) {
		super(2.0F, 1.0F, ToolMaterial.IRON, Sets.newHashSet(new Block[] {}));
		this.material = ToolMaterial.IRON;
		itemRarity = rarity;
		setCreativeTab(MineFantasyTabs.tabOldTools);
		this.setMaxDamage(material.getMaxUses() * 2);
		this.tier = tier;
		setRegistryName(name);
		this.setTranslationKey(name);

		shiftRotations.add(BlockLever.class);
		shiftRotations.add(BlockButton.class);
		shiftRotations.add(BlockChest.class);
		blacklistedRotations.add(BlockBed.class);
		blacklistedRotations.add(BlockRack.class);
		blacklistedRotations.add(BlockRepairKit.class);

		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (block.hasTileEntity(state)){
			return EnumActionResult.PASS;
		}

		if (block == null || isClass(blacklistedRotations, block.getClass())) {
			return EnumActionResult.PASS;
		}

		if (player.isSneaking() != isClass(shiftRotations, block.getClass())) {
			return EnumActionResult.FAIL;
		}

		if (block instanceof BlockChest && Utils.getOtherDoubleChest(world.getTileEntity(pos)) != null) {
			return EnumActionResult.FAIL;
		}

		if (block.rotateBlock(world, pos, side)) {
			player.swingArm(hand);
			if (!world.isRemote) {
				return EnumActionResult.PASS;
			}
		}
		return EnumActionResult.FAIL;
	}

	private boolean isClass(Set<Class<? extends Block>> set, Class<? extends Block> cls) {
		for (Class<? extends Block> shift : set) {
			if (shift.isAssignableFrom(cls)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	@Override
	public ToolMaterial getMaterial() {
		return toolMaterial;
	}

	@Override
	public Tool getToolType(ItemStack stack) {
		return Tool.SPANNER;
	}

	@Override
	public float[] getDamageRatio(Object... implement) {
		return new float[] {0, 1, 0};
	}

	private void addSet(List list, Item[] items) {
		for (Item item : items) {
			list.add(new ItemStack(item));
		}
	}

	@Override
	public float getPenetrationLevel(Object implement) {
		return 0F;
	}

	public ItemSpanner setCustom(String s) {
		canRepair = false;
		isCustom = true;
		return this;
	}

	public ItemSpanner setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
		return this;
	}

	public ItemSpanner setEfficiencyMod(float efficiencyMod) {
		this.efficiencyMod = efficiencyMod;
		return this;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer user, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return ToolHelper.performBlockTransformation(user, world, pos, hand, facing);
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
				new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", 1F, 0));
		return multimap;
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
	public IRarity getForgeRarity(ItemStack item) {
		return CustomToolHelper.getRarity(item, itemRarity);
	}

	@Override
	public float getEfficiency(ItemStack item) {
		return CustomToolHelper.getEfficiency(item, material.getEfficiency(), efficiencyMod);
	}

	@Override
	public int getTier(ItemStack item) {
		return CustomToolHelper.getCrafterTier(item, tier);
	}

	public float getDigSpeed(ItemStack stack, Block block, World world, BlockPos pos, EntityPlayer player) {
		if (!ForgeHooks.isToolEffective(world, pos, stack)) {
			return this.getDestroySpeed(stack, block);
		}
		float digSpeed = player.getDigSpeed(block.getDefaultState(), pos);
		return CustomToolHelper.getEfficiency(stack, digSpeed, efficiencyMod);
	}

	public float getDestroySpeed(ItemStack stack, Block block) {
		return block.getMaterial(block.getDefaultState()) != Material.IRON && block.getMaterial(block.getDefaultState()) != Material.ANVIL
				&& block.getMaterial(block.getDefaultState()) != Material.ROCK ? super.getDestroySpeed(stack, block.getDefaultState())
				: CustomToolHelper.getEfficiency(stack, this.efficiency, efficiencyMod / 2);
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

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this);
	}
}