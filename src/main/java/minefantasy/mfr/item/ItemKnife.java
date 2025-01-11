package minefantasy.mfr.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.tool.IHuntingItem;
import minefantasy.mfr.api.tool.IToolMFR;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.constants.WeaponClass;
import minefantasy.mfr.init.MineFantasyMaterials;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.ToolHelper;
import minefantasy.mfr.util.XSTRandom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.common.IShearable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anonymous Productions
 */
public class ItemKnife extends ItemWeaponMFR implements IToolMFR, IHuntingItem {
	protected Rarity itemRarity;
	private final int tier;
	private float baseDamage;
	private boolean isCustom = false;
	private float efficiencyMod = 1.0F;

	/**
	 * Knives are weapons used for hunting, and tools used for processing
	 */
	public ItemKnife(String name, ToolMaterial material, Rarity rarity, float weight, int tier) {
		super(material, name, rarity, weight);
		this.tier = tier;
		this.itemRarity = Rarity.COMMON;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
		if (!player.world.isRemote) {
			IBlockState block = player.world.getBlockState(pos);
			if (block instanceof IShearable) {
				IShearable target = (IShearable) block;
				if (target.isShearable(itemstack, player.world, pos)) {
					XSTRandom rand = new XSTRandom();
					if (rand.nextInt(10) < 3) { //knife has reduced drop and can't be used to shear entity's
						List<ItemStack> drops = target.onSheared(itemstack, player.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemstack));
						for (ItemStack stack : drops) {
							float f = 0.7F;
							double d = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
							double d1 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
							double d2 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
							EntityItem entityitem = new EntityItem(player.world, (double) pos.getX() + d, (double) pos.getY() + d1, (double) pos.getZ() + d2, stack);
							entityitem.setPickupDelay(10);
							player.world.spawnEntity(entityitem);
						}

						itemstack.damageItem(1, player);
						player.addStat(StatList.MINE_BLOCK_STATS.get(Block.getIdFromBlock(block.getBlock())), 1);
					}
				}
			}
		}
		return false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		return ActionResult.newResult(EnumActionResult.PASS, item);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer user, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return ToolHelper.performBlockTransformation(user, world, pos, hand, facing);
	}

	@Override
	public boolean canRetrieveDrops(ItemStack item) {
		return true;
	}

	@Override
	public Tool getToolType(ItemStack stack) {
		return Tool.KNIFE;
	}

	/**
	 * Determines if the weapon can parry
	 */
	@Override
	public boolean canWeaponParry() {
		return false;
	}

	@Override
	protected float getStaminaMod() {
		return 0.5F;
	}

	@Override
	public WeaponClass getWeaponClass() {
		return null;
	}

	public ItemKnife setCustom(String s) {
		canRepair = false;
		isCustom = true;
		return this;
	}

	public ItemKnife setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
		return this;
	}

	@Override
	public float getEfficiency(ItemStack item)  	 {
		return CustomToolHelper.getEfficiency(item, material.getEfficiency(), efficiencyMod);
	}

	@Override
	public int getTier(ItemStack item) {
		return CustomToolHelper.getCrafterTier(item, tier);
	}

	public ItemKnife setEfficiencyMod(float efficiencyMod) {
		this.efficiencyMod = efficiencyMod;
		return this;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		if (slot != EntityEquipmentSlot.MAINHAND) {
			return super.getAttributeModifiers(slot, stack);
		}

		Multimap<String, AttributeModifier> map = HashMultimap.create();
		map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", getMeleeDamage(stack), 0));
		map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -0.75F, 0));
		return map;
	}

	/**
	 * Gets a stack-sensitive value for the melee dmg
	 */
	protected float getMeleeDamage(ItemStack item) {
		return baseDamage + CustomToolHelper.getMeleeDamage(item, material.getAttackDamage()) * 0.25F;
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
		if (state.getBlock().isToolEffective(state.getBlock().getTranslationKey(), state)) {
			return this.getSwordDestroySpeed(stack, state);
		}
		return CustomToolHelper.getEfficiency(stack, super.getDestroySpeed(stack, state), efficiencyMod) * 0.05F;
	}

	public float getSwordDestroySpeed(ItemStack stack, IBlockState state) {
		float base = super.getDestroySpeed(stack, state);
		return base <= 1.0F ? base
				: CustomToolHelper.getEfficiency(stack, material.getEfficiency(), efficiencyMod);
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
		return CustomToolHelper.getHarvestLevel(stack, super.getHarvestLevel(stack, toolClass, player, blockState));
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
	// ====================================================== CUSTOM END
	// ==============================================================\\
}
