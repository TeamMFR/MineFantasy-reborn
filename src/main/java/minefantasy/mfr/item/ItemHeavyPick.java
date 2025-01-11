package minefantasy.mfr.item;

import com.google.common.collect.Multimap;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.tier.IToolMaterial;
import minefantasy.mfr.config.ConfigTools;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyMaterials;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.mechanics.StaminaMechanics;
import minefantasy.mfr.proxy.IClientRegister;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.ModelLoaderHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static minefantasy.mfr.registry.CustomMaterialRegistry.DECIMAL_FORMAT;

/**
 * @author Anonymous Productions
 */
public class ItemHeavyPick extends ItemPickaxe implements IToolMaterial, IClientRegister {
	private final Random rand = new Random();
	protected Rarity itemRarity;
	private float baseDamage = 2F;
	// ===================================================== CUSTOM START
	// =============================================================\\
	private boolean isCustom = false;
	private float efficiencyMod = 1.0F;

	public ItemHeavyPick(String name, ToolMaterial material, Rarity rarity) {
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
		if (!world.isRemote && isToolEffective(state, world, pos, stack) && StaminaMechanics.canAcceptCost(user)) {
			for (int x1 = -1; x1 <= 1; x1++) {
				for (int y1 = -1; y1 <= 1; y1++) {
					for (int z1 = -1; z1 <= 1; z1++) {
						EnumFacing enumFacing = EnumFacing.getDirectionFromEntityLiving(pos, user);
						if (enumFacing != EnumFacing.UP && enumFacing != EnumFacing.DOWN) {
							enumFacing = user.getHorizontalFacing();
						}

						int blockX = pos.getX() + x1 + enumFacing.getXOffset();
						int blockY = pos.getY() + y1 + enumFacing.getYOffset();
						int blockZ = pos.getZ() + z1 + enumFacing.getZOffset();
						BlockPos blockPos = new BlockPos(blockX, blockY, blockZ);

						if (!(x1 + enumFacing.getXOffset() == 0 && y1 + enumFacing.getYOffset() == 0 && z1 + enumFacing.getZOffset() == 0)) {
							IBlockState newState = world.getBlockState(blockPos);
							if (newState != null && user instanceof EntityPlayer && isToolEffective(newState, world, blockPos, stack)) {

								if (rand.nextFloat() * 100F < (100F - ConfigTools.heavy_tool_drop_chance)) {
									newState.getBlock().harvestBlock(world, (EntityPlayer) user, pos, newState, null, stack);
								}
								world.setBlockToAir(blockPos);
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

	public boolean isToolEffective(IBlockState state, World world, BlockPos pos, ItemStack stack){
		if (state.getBlock() == Blocks.END_STONE) { //Potentially make this data driven list check for overrides
			return true;
		}
		return ForgeHooks.canToolHarvestBlock(world, pos, stack);
	}

	@Override
	public ToolMaterial getMaterial() {
		return toolMaterial;
	}

	public ItemHeavyPick setCustom(String s) {
		setCreativeTab(MineFantasyTabs.tabOldTools);
		canRepair = false;
		isCustom = true;
		return this;
	}

	public ItemHeavyPick setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
		return this;
	}

	public ItemHeavyPick setEfficiencyMod(float efficiencyMod) {
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
		return CustomToolHelper.getMaxDamage(stack, super.getMaxDamage(stack)) * 2;
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
		CustomMaterial material = CustomToolHelper.getCustomPrimaryMaterial(stack);
		float efficiency = material.getHardness() > 0 ? material.getHardness() : this.efficiency;
		return !state.getBlock().isToolEffective("pickaxe", state)
				? super.getDestroySpeed(stack, state)
				: CustomToolHelper.getEfficiency(stack, efficiency, efficiencyMod / 8F);
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

		CustomMaterial material = CustomToolHelper.getCustomPrimaryMaterial(item);
		float efficiency = material.getHardness() > 0 ? material.getHardness() : this.efficiency;
		list.add(TextFormatting.GREEN + I18n.format("attribute.tool.digEfficiency.name",
				DECIMAL_FORMAT.format(CustomToolHelper.getEfficiency(item, efficiency, efficiencyMod / 8F))));

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
