package minefantasy.mfr.item;

import com.google.common.collect.Multimap;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.tier.IToolMaterial;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyBlocks;
import minefantasy.mfr.init.MineFantasyMaterials;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.proxy.IClientRegister;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.ModelLoaderHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static minefantasy.mfr.registry.CustomMaterialRegistry.DECIMAL_FORMAT;

/**
 * @author Anonymous Productions
 */
public class ItemMattock extends ItemPickaxe implements IToolMaterial, IClientRegister {
	protected Rarity itemRarity;
	private float baseDamage = 1.0F;
	private boolean isCustom = false;
	private float efficiencyMod = 1.0F;

	public ItemMattock(String name, ToolMaterial material, Rarity rarity) {
		super(material);
		itemRarity = rarity;
		setRegistryName(name);
		setTranslationKey(name);

		setCreativeTab(MineFantasyTabs.tabOldTools);
		this.setHarvestLevel("pickaxe", Math.max(0, material.getHarvestLevel() - 2));

		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	@Override
	public ToolMaterial getMaterial() {
		return toolMaterial;
	}

	@Override
	public boolean canHarvestBlock(IBlockState block, ItemStack stack) {
		return super.canHarvestBlock(block, stack) || Items.IRON_SHOVEL.canHarvestBlock(block, stack);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack mattock = player.getHeldItem(hand);
		if (!player.canPlayerEdit(pos, facing, mattock)) {
			return EnumActionResult.FAIL;
		}
		// TODO: currently 100% chance to break blocks, add a material efficiency + RNG based chance to create a road Block
		Block targetBlock = world.getBlockState(pos).getBlock();
		BlockPos offsetUp = new BlockPos(pos.offset(EnumFacing.UP));
		if (world.getBlockState(offsetUp).getBlock() != Blocks.AIR || !world.getBlockState(offsetUp).getBlock().isReplaceable(world, offsetUp)) {
			return EnumActionResult.FAIL;
		}

		if (targetBlock == Blocks.GRASS || targetBlock == Blocks.DIRT || targetBlock == Blocks.GRASS_PATH) {
			Block roadBlock = MineFantasyBlocks.MUD_ROAD;
			placeRoad(player, world, pos, mattock, roadBlock);
		}
		if (targetBlock == Blocks.COBBLESTONE) {
			Block roadBlock = MineFantasyBlocks.COBBLESTONE_ROAD;
			placeRoad(player, world, pos, mattock, roadBlock);
		}
		if (targetBlock == MineFantasyBlocks.LIMESTONE_COBBLE) {
			Block roadBlock = MineFantasyBlocks.LIMESTONE_ROAD;
			placeRoad(player, world, pos, mattock, roadBlock);
		}
		return EnumActionResult.FAIL;
	}

	private EnumActionResult placeRoad(EntityPlayer player, World world, BlockPos pos, ItemStack mattock, Block roadBlock) {

		world.playSound(player, pos.add(0.5F, 0.5F, 0.5F), roadBlock.getSoundType().getHitSound(), SoundCategory.AMBIENT, (roadBlock.getSoundType().getVolume() + 1.0F) / 2.0F, roadBlock.getSoundType().getPitch() * 0.8F);

		if (world.isRemote) {
			return EnumActionResult.PASS;
		} else {
			world.setBlockState(pos, roadBlock.getDefaultState(), 2);
			mattock.damageItem(1, player);
			return EnumActionResult.PASS;
		}
	}

	public ItemMattock setCustom(String s) {
		canRepair = false;
		isCustom = true;
		return this;
	}

	public ItemMattock setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
		return this;
	}

	public ItemMattock setEfficiencyMod(float efficiencyMod) {
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
				new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", getMeleeDamage(stack), 0));
		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
				new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3F, 0));
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
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		return CustomToolHelper.getEfficiency(stack, super.getDestroySpeed(stack, state), efficiencyMod / 2F);
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

		CustomMaterial material = CustomToolHelper.getCustomPrimaryMaterial(item);
		float efficiency = material.getHardness() > 0 ? material.getHardness() : this.efficiency;
		list.add(TextFormatting.GREEN + I18n.format("attribute.tool.digEfficiency.name",
				DECIMAL_FORMAT.format(CustomToolHelper.getEfficiency(item, efficiency, efficiencyMod / 2F))));

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
