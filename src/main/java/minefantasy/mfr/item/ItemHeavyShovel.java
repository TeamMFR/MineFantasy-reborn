package minefantasy.mfr.item;

import com.google.common.collect.Multimap;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.tier.IToolMaterial;
import minefantasy.mfr.config.ConfigTools;
import minefantasy.mfr.init.MineFantasyMaterials;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.mechanics.StaminaMechanics;
import minefantasy.mfr.proxy.IClientRegister;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.ModelLoaderHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static minefantasy.mfr.material.CustomMaterial.decimal_format;

/**
 * @author Anonymous Productions
 */
public class ItemHeavyShovel extends ItemSpade implements IToolMaterial, IClientRegister {
	protected int itemRarity;
	private float baseDamage = 2F;
	private Random rand = new Random();
	// ===================================================== CUSTOM START
	// =============================================================\\
	private boolean isCustom = false;
	private float efficiencyMod = 1.0F;

	public ItemHeavyShovel(String name, ToolMaterial material, int rarity) {
		super(material);
		itemRarity = rarity;
		setCreativeTab(MineFantasyTabs.tabOldTools);
		setRegistryName(name);
		setTranslationKey(name);

		setMaxDamage(material.getMaxUses());

		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase user) {
		if (!world.isRemote && ForgeHooks.canToolHarvestBlock(world, pos, stack) && StaminaMechanics.canAcceptCost(user)) {
			int range = 2;
			for (int x1 = -range; x1 <= range; x1++) {
				for (int z1 = -range; z1 <= range; z1++) {
					if (getDistance(pos.getX() + x1, pos.getY(), pos.getZ() + z1, pos) <= range + 0.5D) {
						EnumFacing facing = EnumFacing.getDirectionFromEntityLiving(pos, user);
						int blockX = pos.getX() + x1 + facing.getXOffset();
						int blockY = pos.getY() + facing.getYOffset();
						int blockZ = pos.getZ() + z1 + facing.getZOffset();

						if (!(x1 + facing.getXOffset() == 0 && facing.getYOffset() == 0 && z1 + facing.getZOffset() == 0)) {
							BlockPos newBlockPos = new BlockPos(blockX, blockY, blockZ);
							IBlockState newState = world.getBlockState(newBlockPos);
							IBlockState above = world.getBlockState(newBlockPos.add(0, 1, 0));

							if ((above == Blocks.AIR.getDefaultState() || !above.getMaterial().isSolid()) && newState != null
									&& user instanceof EntityPlayer
									&& ForgeHooks.canHarvestBlock(newState.getBlock(), (EntityPlayer) user, world, newBlockPos)
									&& ForgeHooks.canToolHarvestBlock(world, newBlockPos, stack)) {

								if (rand.nextFloat() * 100F < (100F - ConfigTools.heavy_tool_drop_chance)) {
									newState.getBlock().dropBlockAsItemWithChance(world, newBlockPos, newState, ConfigTools.heavy_tool_drop_chance, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
								}
								world.setBlockToAir(newBlockPos);
								stack.damageItem(1, user);
								StaminaMechanics.tirePlayer((EntityPlayer) user, 1F);
							}
						}
					}
				}
			}
		}
		return super.onBlockDestroyed(stack, world, state, pos, user);
	}

	public double getDistance(double x, double y, double z, BlockPos pos) {
		double var7 = pos.getX() - x;
		double var9 = pos.getY() - y;
		double var11 = pos.getZ() - z;
		return MathHelper.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
	}

	@Override
	public ToolMaterial getMaterial() {
		return toolMaterial;
	}

	public ItemHeavyShovel setCustom(String s) {
		canRepair = false;
		isCustom = true;
		return this;
	}

	public ItemHeavyShovel setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
		return this;
	}

	public ItemHeavyShovel setEfficiencyMod(float efficiencyMod) {
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

	//    @Override
	//    @SideOnly(Side.CLIENT)
	//    public int getColorFromItemStack(ItemStack item, int layer) {
	//        return CustomToolHelper.getColourFromItemStack(item, layer, super.getColorFromItemStack(item, layer));
	//    }

	@Override
	public int getMaxDamage(ItemStack stack) {
		return CustomToolHelper.getMaxDamage(stack, super.getMaxDamage(stack)) * 2;
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
		return CustomToolHelper.getEfficiency(stack, super.getDestroySpeed(stack, state), efficiencyMod / 8F);
	}

	public float getSpadeDestroySpeed(ItemStack stack, IBlockState block) {
		float base = super.getDestroySpeed(stack, block);
		return base <= 1.0F ? base : CustomToolHelper.getEfficiency(stack, this.efficiency, efficiencyMod );
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
				decimal_format.format(CustomToolHelper.getEfficiency(item, efficiency, efficiencyMod / 8F))));

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
