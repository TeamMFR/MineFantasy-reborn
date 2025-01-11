package minefantasy.mfr.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.farming.FarmingHelper;
import minefantasy.mfr.api.tier.IToolMaterial;
import minefantasy.mfr.api.weapon.IDamageType;
import minefantasy.mfr.api.weapon.IRackItem;
import minefantasy.mfr.client.render.item.RenderBigTool;
import minefantasy.mfr.config.ConfigTools;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyMaterials;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.mechanics.StaminaMechanics;
import minefantasy.mfr.proxy.IClientRegister;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.tile.TileEntityRack;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.ModelLoaderHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anonymous Productions
 */
public class ItemScythe extends Item implements IToolMaterial, IDamageType, IRackItem, IClientRegister {
	protected Rarity itemRarity;
	private final ToolMaterial toolMaterial;
	private float baseDamage = 3.0F;
	// ===================================================== CUSTOM START
	// =============================================================\\
	private boolean isCustom = false;
	private float efficiencyMod = 1.0F;

	/**
	 *
	 */
	public ItemScythe(String name, ToolMaterial material, Rarity rarity) {
		this.toolMaterial = material;
		this.setFull3D();
		itemRarity = rarity;
		setRegistryName(name);
		setTranslationKey(name);

		setCreativeTab(MineFantasyTabs.tabOldTools);
		this.maxStackSize = 1;
		this.setMaxDamage(material.getMaxUses());

		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	@Override
	public ToolMaterial getMaterial() {
		return toolMaterial;
	}

	private boolean cutGrass(World world, BlockPos pos, int r, EntityPlayer player, boolean leaf) {
		boolean flag = false;
		ItemStack stack = player.getHeldItemMainhand();
		if (stack.isEmpty())
			return false;

		for (int x2 = -r; x2 <= r; x2++) {
			for (int y2 = -r; y2 <= r; y2++) {
				for (int z2 = -r; z2 <= r; z2++) {
					BlockPos newPos = pos.add(x2, y2, z2);
					IBlockState state = world.getBlockState(newPos);
					if (state != null) {
						Material m = state.getMaterial();
						if (canCutMaterial(m, state.getBlockHardness(world, newPos), leaf)) {

							if (pos.getDistance(pos.getX() + x2, pos.getY() + y2, pos.getZ() + z2) < r) {
								flag = true;

								world.setBlockToAir(newPos);
								world.playSound(player, newPos, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.AMBIENT, 1.0F, 1.0F);
								tryBreakFarmland(world, newPos.add(0, -1, 0));
								if (!player.capabilities.isCreativeMode) {
									StaminaMechanics.tirePlayer(player, 1F);
									state.getBlock().dropBlockAsItemWithChance(world, newPos, state, ConfigTools.heavy_tool_drop_chance, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
								}
								stack.damageItem(1, player);
							}
						}
					}
				}
			}
		}
		return flag;
	}

	public double getDistance(double x, double y, double z, BlockPos pos) {
		double var7 = pos.getX() - x;
		double var9 = pos.getY() - y;
		double var11 = pos.getZ() - z;
		return MathHelper.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
	}

	private void tryBreakFarmland(World world, BlockPos pos) {
		IBlockState base = world.getBlockState(pos);

		if (base != null && base == Blocks.FARMLAND.getDefaultState() && FarmingHelper.didHarvestRuinBlock(world, true)) {
			world.setBlockState(pos, Blocks.DIRT.getDefaultState());
		}
	}

	protected void dropBlockAsItem_do(World world, BlockPos pos, ItemStack drop) {
		if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops")) {
			float var6 = 0.7F;
			double var7 = world.rand.nextFloat() * var6 + (1.0F - var6) * 0.5D;
			double var9 = world.rand.nextFloat() * var6 + (1.0F - var6) * 0.5D;
			double var11 = world.rand.nextFloat() * var6 + (1.0F - var6) * 0.5D;
			EntityItem var13 = new EntityItem(world, pos.getX() + var7, pos.getY() + var9, pos.getZ() + var11, drop);
			var13.setPickupDelay(10);
			world.spawnEntity(var13);
		}
	}

	private boolean canCutMaterial(Material material, float str, boolean leaf) {
		if (!leaf) {
			if (str <= 0.0F) {
				return material == Material.VINE || material == Material.PLANTS || material == Material.GRASS;
			} else
				return false;
		}
		return material == Material.LEAVES || material == Material.VINE;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItemMainhand();
		if (!player.canPlayerEdit(pos, facing, stack) || !StaminaMechanics.canAcceptCost(player)) {
			return EnumActionResult.FAIL;
		} else {
			IBlockState state = world.getBlockState(pos);

			if (state != null) {
				Material m = state.getMaterial();
				float hard = state.getBlockHardness(world, pos);
				if (this.canCutMaterial(m, hard, false)) {
					if (cutGrass(world, pos, 5, player, false)) {
						player.swingArm(hand);
					}
				} else if (this.canCutMaterial(m, hard, true)) {
					if (cutGrass(world, pos, 3, player, true)) {
						player.swingArm(hand);
					}
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public float[] getDamageRatio(Object... implement) {
		return new float[] {1, 0, 2};
	}

	@Override
	public float getPenetrationLevel(Object implement) {
		return 0F;
	}

	public ItemScythe setCustom(String s) {
		canRepair = false;
		isCustom = true;
		return this;
	}

	public ItemScythe setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
		return this;
	}

	public ItemScythe setEfficiencyMod(float efficiencyMod) {
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
		map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -3F, 0));
		return map;
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

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		stack.damageItem(1, attacker);
		return true;
	}

	public ItemStack construct(String main, String haft) {
		return CustomToolHelper.construct(this, main, haft);
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		return CustomToolHelper.getRarity(item, itemRarity);
	}

	public float getDigSpeed(ItemStack stack, Block block, World world, BlockPos pos, EntityPlayer player) {
		if (!ForgeHooks.isToolEffective(world, pos, stack)) {
			return this.getDestroySpeed(stack, block.getDefaultState());
		}
		float digSpeed = player.getDigSpeed(block.getDefaultState(), pos);
		return CustomToolHelper.getEfficiency(stack, digSpeed, efficiencyMod / 10);
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
	public String getItemStackDisplayName(ItemStack item) {
		String unlocalName = this.getUnlocalizedNameInefficiently(item) + ".name";
		return CustomToolHelper.getLocalisedName(item, unlocalName);
	}
	// ====================================================== CUSTOM END
	// ==============================================================\\

	@Override
	public float getScale(ItemStack itemstack) {
		return 2.0F;
	}

	@Override
	public float getOffsetX(ItemStack itemstack) {
		return 1.5F;
	}

	@Override
	public float getOffsetY(ItemStack itemstack) {
		return 1.7F;
	}

	@Override
	public float getOffsetZ(ItemStack itemstack) {
		return 0.05F;
	}

	@Override
	public float getRotationOffset(ItemStack itemstack) {
		return 90F;
	}

	@Override
	public boolean canHang(TileEntityRack rack, ItemStack item, int slot) {
		return rack.hasRackBelow(slot);
	}

	@Override
	public boolean flip(ItemStack itemStack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
		ModelLoaderHelper.registerWrappedItemModel(this, new RenderBigTool(() -> modelLocation, 2F, -0.5F, -15, 0.26f), modelLocation);
	}
}
