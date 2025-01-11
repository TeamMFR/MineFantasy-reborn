package minefantasy.mfr.item;

import com.google.common.collect.Lists;
import minefantasy.mfr.api.archery.IAmmo;
import minefantasy.mfr.api.crafting.ISpecialSalvage;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.entity.EntityBomb;
import minefantasy.mfr.init.MineFantasyTabs;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBomb extends ItemBaseMFR implements ISpecialSalvage, IAmmo {
	private static final String powderNBT = "powder_type";
	private static final String fuseNBT = "fuse_type";
	private static final String fillingNBT = "filling_type";
	private static final String casingNBT = "casing_type";

	public ItemBomb(String name) {
		super(name);
		this.maxStackSize = 16;

		this.setCreativeTab(MineFantasyTabs.tabGadget);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new BehaviorDefaultDispenseItem() {
			@Override
			protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
				IPosition iposition = BlockDispenser.getDispensePosition(source);
				ItemStack itemstack = stack.splitStack(1);
				ItemBomb.doDispense(source.getWorld(), itemstack, 6, facing, iposition);
				return stack;
			}
		});

		this.addPropertyOverride(new ResourceLocation("casing"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (getCasing(stack).equals("ceramic")) {
					return 1;
				}
				if (getCasing(stack).equals("iron")) {
					return 2;
				}
				if (getCasing(stack).equals("obsidian")) {
					return 3;
				}
				if (getCasing(stack).equals("crystal")) {
					return 4;
				}
				return 0;
			}
		});
		this.addPropertyOverride(new ResourceLocation("filling"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (getFilling(stack).equals("basic")) {
					return 1;
				}
				if (getFilling(stack).equals("shrapnel")) {
					return 2;
				}
				if (getFilling(stack).equals("fire")) {
					return 3;
				}
				return 0;
			}
		});
	}

	public static void doDispense(World world, ItemStack stack, int speed, EnumFacing facing, IPosition position) {
		double posX = position.getX() + (facing.getXOffset() / 2F);
		double posY = position.getY() + (facing.getYOffset() / 2F);
		double posZ = position.getZ() + (facing.getZOffset() / 2F);

		double xVelocity = facing.getXOffset();
		double yVelocity = facing.getYOffset();
		double zVelocity = facing.getZOffset();

		EntityBomb bomb = new EntityBomb(world).setType(ItemBomb.getFilling(stack), ItemBomb.getCasing(stack),
				ItemBomb.getFuse(stack), ItemBomb.getPowder(stack));
		bomb.setPosition(posX, posY, posZ);
		bomb.setThrowableHeading(xVelocity, yVelocity, zVelocity, 1.0F, (float) speed);
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("stickyBomb")) {
			bomb.getEntityData().setBoolean("stickyBomb", true);
		}
		world.spawnEntity(bomb);
	}

	public static void setSticky(ItemStack item) {
		NBTTagCompound nbt = getNBT(item);
		nbt.setBoolean("stickyBomb", true);
	}

	public static void setFuse(ItemStack item, String fuse) {
		NBTTagCompound nbt = getNBT(item);
		nbt.setString(fuseNBT, fuse);
	}

	public static String getFuse(ItemStack item) {
		NBTTagCompound nbt = getNBT(item);
		if (item.getItem() instanceof ItemBomb) {
			return ((ItemBomb) item.getItem()).getItemFuse(nbt.getString(fuseNBT));
		}
		return nbt.getString(fuseNBT);
	}

	public static void setPowder(ItemStack item, String powder) {
		NBTTagCompound nbt = getNBT(item);
		nbt.setString(powderNBT, powder);
	}

	public static String getPowder(ItemStack item) {
		NBTTagCompound nbt = getNBT(item);
		if (item.getItem() instanceof ItemBomb) {
			return ((ItemBomb) item.getItem()).getItemPowder(nbt.getString(powderNBT));
		}
		return nbt.getString(powderNBT);
	}

	/**
	 * 0 = Basic 1 = Shrapnel 2 = Fire
	 */
	public static void setFilling(ItemStack item, String filling) {
		NBTTagCompound nbt = getNBT(item);
		nbt.setString(fillingNBT, filling);
	}

	public static String getFilling(ItemStack item) {
		NBTTagCompound nbt = getNBT(item);
		if (item.getItem() instanceof ItemBomb) {
			return ((ItemBomb) item.getItem()).getItemFilling(nbt.getString(fillingNBT));
		}
		return nbt.getString(fillingNBT);
	}

	/**
	 * 0 = Ceramic 1 = Iron
	 */
	public static void setCasing(ItemStack item, String casing) {
		NBTTagCompound nbt = getNBT(item);
		nbt.setString(casingNBT, casing);
	}

	public static String getCasing(ItemStack item) {
		NBTTagCompound nbt = getNBT(item);
		if (item.getItem() instanceof ItemBomb) {
			return ((ItemBomb) item.getItem()).getItemCasing(nbt.getString(casingNBT));
		}
		return nbt.getString(casingNBT);
	}

	public static NBTTagCompound getNBT(ItemStack item) {
		if (!item.hasTagCompound())
			item.setTagCompound(new NBTTagCompound());
		return item.getTagCompound();
	}

	public static ItemStack createExplosive(Item item, String casing, String filling, String fuse, String powder, int stackSize, boolean sticky) {
		ItemStack bomb = new ItemStack(item, stackSize);
		setFilling(bomb, filling);
		setCasing(bomb, casing);
		setFuse(bomb, fuse);
		setPowder(bomb, powder);
		if (sticky) {
			setSticky(bomb);
		}
		return bomb;
	}

	public static ItemStack createExplosive(Item item, String casing, String filling, String fuse, String powder, int stackSize) {
		return createExplosive(item, casing, filling, fuse, powder, stackSize, false);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack item) {
		return EnumAction.valueOf("mfr_block");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer user, EnumHand hand) {
		ItemStack item = user.getHeldItem(hand);
		if (!user.isSwingInProgress) {
			world.playSound(user, user.getPosition(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.AMBIENT, 1.0F, 1.0F);
			world.playSound(user, user.getPosition(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.AMBIENT, 1.0F, 1.0F);
			user.setActiveHand(hand);
		}
		return ActionResult.newResult(EnumActionResult.PASS, item);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack item) {
		return 15;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack item, World world, EntityLivingBase entity) {
		entity.swingArm(entity.getActiveHand());

		if (!(entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode)) {
			item.shrink(1);
		}

		if (!world.isRemote) {

			EntityBomb bomb = new EntityBomb(world, entity, item)
					.setType(getFilling(item), getCasing(item), getFuse(item), getPowder(item));
			world.spawnEntity(bomb);
			if (item.hasTagCompound() && item.getTagCompound().hasKey("stickyBomb")) {
				bomb.getEntityData().setBoolean("stickyBomb", true);
			}
		}
		return item;
	}

	@Override
	public void addInformation(ItemStack item, World world, List<String> list, ITooltipFlag flag) {
		super.addInformation(item, world, list, flag);

		if (item.hasTagCompound() && item.getTagCompound().hasKey("stickyBomb")) {
			list.add(TextFormatting.GREEN + I18n.format("bomb.case.sticky")
					+ TextFormatting.GRAY);
		}
		EnumFillingType fill = EnumFillingType.getType(getFilling(item));
		EnumCasingType casing = EnumCasingType.getType(getCasing(item));
		EnumFuseType fuse = EnumFuseType.getType(getFuse(item));
		EnumPowderType powder = EnumPowderType.getType(getPowder(item));

		int damage = (int) (fill.damage * casing.damageModifier * powder.damageModifier);
		float range = fill.range * casing.rangeModifier * powder.rangeModifier;
		float fusetime = fuse.time / 20F;

		list.add(I18n.format("bomb.case." + casing.name + ".name"));
		list.add(I18n.format("bomb.powder." + powder.name + ".name"));
		list.add(I18n.format("bomb.fuse." + fuse.name + ".name"));
		list.add("");
		list.add(I18n.format("bomb.fusetime.name", fusetime));
		list.add(I18n.format("bomb.damage.name") + ": " + damage);
		list.add(I18n.format("bomb.range.metric.name", range));
	}

	@Override
	public String getTranslationKey(ItemStack item) {
		EnumFillingType type = EnumFillingType.getType(getFilling(item));
		return "item.bomb_" + type.name;
	}

	public ItemStack createBomb(String casing, String filling, String fuse, String powder, int stackSize) {
		return ItemBomb.createExplosive(this, casing, filling, fuse, powder, stackSize);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab)) {
			return;
		}
		items.add(createBomb("ceramic", "basic", "basic", "black_powder", 1));
		items.add(createBomb("ceramic", "shrapnel", "basic", "black_powder", 1));
		items.add(createBomb("ceramic", "fire", "basic", "black_powder", 1));

		items.add(createBomb("iron", "basic", "basic", "black_powder", 1));
		items.add(createBomb("iron", "shrapnel", "basic", "black_powder", 1));
		items.add(createBomb("iron", "fire", "basic", "black_powder", 1));

		items.add(createBomb("obsidian", "basic", "basic", "black_powder", 1));
		items.add(createBomb("obsidian", "shrapnel", "basic", "black_powder", 1));
		items.add(createBomb("obsidian", "fire", "basic", "black_powder", 1));

		items.add(createBomb("crystal", "basic", "basic", "black_powder", 1));
		items.add(createBomb("crystal", "shrapnel", "basic", "black_powder", 1));
		items.add(createBomb("crystal", "fire", "basic", "black_powder", 1));
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		if (getFilling(item).equals("fire") || getCasing(item).equals("obsidian") || getCasing(item).equals("crystal")) {
			return Rarity.UNCOMMON;
		}
		return Rarity.COMMON;
	}

	@Override
	public List<ItemStack> getSalvage(ItemStack item) {
		return Lists.newArrayList(
				ItemBombComponent.getBombComponent("bombcase", getCasing(item)),
				ItemBombComponent.getBombComponent("fuse", getFuse(item)),
				ItemBombComponent.getBombComponent("powder", getPowder(item)),
				ItemBombComponent.getBombComponent("filling", getFilling(item))
		);
	}

	public String getItemFuse(String fuse) {
		return fuse;
	}

	public String getItemFilling(String filling) {
		return filling;
	}

	public String getItemCasing(String casing) {
		return casing;
	}

	public String getItemPowder(String powder) {
		return powder;
	}

	@Override
	public String getAmmoType(ItemStack ammo) {
		return "mine";
	}
}