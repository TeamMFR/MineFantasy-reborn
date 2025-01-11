package minefantasy.mfr.item;

import com.google.common.collect.Multimap;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.mining.RandomDigs;
import minefantasy.mfr.api.tier.IToolMaterial;
import minefantasy.mfr.constants.Rarity;
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
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
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
public class ItemTrow extends ItemSpade implements IToolMaterial, IClientRegister {
	protected Rarity itemRarity;
	private float baseDamage = 1F;
	private final Random rand = new Random();
	// ===================================================== CUSTOM START
	// =============================================================\\
	private boolean isCustom = false;
	private float efficiencyMod = 1.0F;

	public ItemTrow(String name, ToolMaterial material, Rarity rarity) {
		super(material);
		itemRarity = rarity;
		setCreativeTab(MineFantasyTabs.tabOldTools);
		setRegistryName(name);
		setTranslationKey(name);

		setMaxDamage(material.getMaxUses());

		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack item, World world, IBlockState state, BlockPos pos, EntityLivingBase user) {
		alwaysDropFlint(state.getBlock(), item, world, user, pos);

		if (!world.isRemote) {
			int harvestlvl = this.getMaterial().getHarvestLevel();
			int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, item);
			boolean silk = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, item) == 1;

			ArrayList<ItemStack> specialDrops = RandomDigs.getDroppedItems(state.getBlock(), state.getBlock().getMetaFromState(state), harvestlvl, fortune, silk, pos.getY());

			if (specialDrops != null && !specialDrops.isEmpty()) {

				for (ItemStack newdrop : specialDrops) {
					if (!newdrop.isEmpty()) {
						if (newdrop.getCount() > 1) {
							if (CustomToolHelper.getCustomPrimaryMaterial(item).getTier() > 0) {
								newdrop.setCount(itemRand.nextInt(CustomToolHelper.getCustomPrimaryMaterial(item).getTier()));
							} else {
								newdrop.setCount(1);
							}
						}
						if (newdrop.getCount() < 1){
							newdrop.setCount(1);
						}
						dropItem(world, pos, newdrop);
					}
				}
			}
		}
		return super.onBlockDestroyed(item, world, state, pos, user);
	}

	private void alwaysDropFlint(Block block, ItemStack item, World world, EntityLivingBase user, BlockPos pos) {
		if (block == Blocks.GRAVEL) {
			world.setBlockToAir(pos);
			int loot = 0;
			int enc = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, item);
			if (enc > 0) {
				loot = rand.nextInt(enc);
			}
			dropItem(world, pos, new ItemStack(Items.FLINT, 1 + loot));
		}
	}

	private void dropItem(World world, BlockPos pos, ItemStack drop) {
		if (!world.isRemote) {
			EntityItem dropItem = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, drop);
			dropItem.setPickupDelay(10);
			world.spawnEntity(dropItem);
		}
	}

	@Override
	public ToolMaterial getMaterial() {
		return toolMaterial;
	}

	public ItemTrow setCustom(String s) {
		canRepair = false;
		isCustom = true;
		return this;
	}

	public ItemTrow setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
		return this;
	}

	public ItemTrow setEfficiencyMod(float efficiencyMod) {
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
		return CustomToolHelper.getMaxDamage(stack, super.getMaxDamage(stack)) / 2;
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

		return !state.getBlock().isToolEffective("shovel", state)
				? super.getDestroySpeed(stack, state)
				: CustomToolHelper.getEfficiency(stack, efficiency, efficiencyMod / 4F);
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
				DECIMAL_FORMAT.format(CustomToolHelper.getEfficiency(item, efficiency, efficiencyMod / 4F))));

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
