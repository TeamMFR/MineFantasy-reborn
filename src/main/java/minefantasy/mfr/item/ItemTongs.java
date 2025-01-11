package minefantasy.mfr.item;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.heating.Heatable;
import minefantasy.mfr.api.heating.TongsHelper;
import minefantasy.mfr.api.tier.IToolMaterial;
import minefantasy.mfr.api.tool.ISmithTongs;
import minefantasy.mfr.api.weapon.IRackItem;
import minefantasy.mfr.client.render.item.RenderTong;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.material.CustomMaterial;
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anonymous Productions
 */
public class ItemTongs extends ItemTool implements IRackItem, IToolMaterial, ISmithTongs, IClientRegister {
	protected Rarity itemRarity;
	private float baseDamage;
	// ===================================================== CUSTOM START
	// =============================================================\\
	private boolean isCustom = false;
	private float efficiencyMod = 1.0F;

	public ItemTongs(String name, ToolMaterial material, Rarity rarity) {
		super(0F, 1.0F, material, Sets.newHashSet(new Block[] {}));
		itemRarity = rarity;
		setCreativeTab(MineFantasyTabs.tabOldTools);
		this.setMaxDamage(getMaxDamage() / 5);
		setRegistryName(name);
		setTranslationKey(name);

		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	@Override
	public ToolMaterial getMaterial() {
		return toolMaterial;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		RayTraceResult rayTraceResult = this.rayTrace(world, player, true);

		if (rayTraceResult == null) {
			return ActionResult.newResult(EnumActionResult.PASS, item);
		} else {
			if (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos pos = rayTraceResult.getBlockPos();

				if (!world.canMineBlockBody(player, pos)) {
					return ActionResult.newResult(EnumActionResult.PASS, item);
				}

				if (!player.canPlayerEdit(pos, rayTraceResult.sideHit, item)) {
					return ActionResult.newResult(EnumActionResult.PASS, item);
				}

				float water = TongsHelper.getWaterSource(world, pos);

				if (!TongsHelper.getHeldItem(item).isEmpty() && water >= 0) {
					ItemStack drop = TongsHelper.getHeldItem(item).copy(), cooled = drop;

					if (TongsHelper.isCoolableItem(drop)) {
						cooled = Heatable.getQuenchedItem(drop, water);
						cooled.setCount(drop.getCount());

						player.playSound(SoundEvents.ENTITY_GENERIC_SPLASH, 1F, 1F);
						player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 2F, 0.5F);

						for (int a = 0; a < 5; a++) {
							world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F, 0, 0.065F, 0);
						}
					}
					if (!cooled.isEmpty() && !world.isRemote) {
						if (world.isAirBlock(pos.add(0, 1, 0))) {
							EntityItem entity = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, cooled);
							entity.setPickupDelay(20);
							entity.motionX = entity.motionY = entity.motionZ = 0F;
							world.spawnEntity(entity);
						} else {
							player.entityDropItem(cooled, 0);
						}
					}

					return ActionResult.newResult(EnumActionResult.PASS, TongsHelper.clearHeldItem(item, player));
				}
			}

			return ActionResult.newResult(EnumActionResult.FAIL, item);
		}
	}

	public ItemTongs setCustom(String s) {
		canRepair = false;
		isCustom = true;
		return this;
	}

	public ItemTongs setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
		return this;
	}

	public ItemTongs setEfficiencyMod(float efficiencyMod) {
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

	public ItemStack construct(String main) {
		return CustomToolHelper.construct(this, main, null);
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
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
					items.add(this.construct(customMat.getName()));
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
		} else {

			ItemStack held = TongsHelper.getHeldItem(item);
			if (!held.isEmpty()) {
				list.add("");
				list.add(held.getItem().getItemStackDisplayName(held));
				held.getItem().addInformation(held, world, list, flag);
			}
		}

		super.addInformation(item, world, list, flag);
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		String unlocalName = this.getUnlocalizedNameInefficiently(item) + ".name";
		return CustomToolHelper.getLocalisedName(item, unlocalName);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		// This method does some VERY strange things! Despite its name, it also seems to affect the updating of NBT...

		if(!oldStack.isEmpty() || !newStack.isEmpty()){
			// We only care about the situation where we specifically want the animation NOT to play.
			if(oldStack.getItem() == newStack.getItem() && !slotChanged)
				return false;
		}

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);	}

	@Override
	public boolean hitEntity(ItemStack item, EntityLivingBase target, EntityLivingBase user) {
		return true;
	}
	// ====================================================== CUSTOM END
	// ==============================================================\\

	@Override
	public float getScale(ItemStack itemstack) {
		return 1F;
	}

	@Override
	public float getOffsetX(ItemStack itemstack) {
		return 0.4F;
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
		ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
		ModelLoaderHelper.registerWrappedItemModel(this, new RenderTong(() -> modelLocation), modelLocation);
	}
}
