package minefantasy.mfr.item;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.heating.Heatable;
import minefantasy.mfr.api.heating.IHotItem;
import minefantasy.mfr.api.heating.TongsHelper;
import minefantasy.mfr.client.render.item.RenderHotItem;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.entity.EntityItemHeated;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.GuiHelper;
import minefantasy.mfr.util.MFRLogUtil;
import minefantasy.mfr.util.ModelLoaderHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class ItemHeated extends ItemBaseMFR implements IHotItem {
	public static boolean renderDynamicHotIngotRendering = true;
	private int ticksExisted;

	public ItemHeated() {
		super("hot_item");
		this.setMaxStackSize(64);
		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	public static int getTemp(ItemStack item) {
		return Heatable.getTemp(item);
	}

	public static ItemStack getStack(ItemStack item) {
		return Heatable.getItemStack(item);
	}

	public static void setTemp(ItemStack item, int heat) {
		NBTTagCompound nbt = getNBT(item);

		nbt.setInteger(Heatable.NBT_CurrentTemp, heat);
	}

	public static void setWorkTemp(ItemStack item, int heat) {
		NBTTagCompound nbt = getNBT(item);
		MFRLogUtil.logDebug("Set Workable Temp: " + heat);
		nbt.setInteger(Heatable.NBT_WorkableTemp, heat);
	}

	public static void setUnstableTemp(ItemStack item, int heat) {
		NBTTagCompound nbt = getNBT(item);
		MFRLogUtil.logDebug("Set Unstable Temp: " + heat);
		nbt.setInteger(Heatable.NBT_UnstableTemp, heat);
	}

	public static ItemStack createHotItem(ItemStack item) {
		return createHotItem(item, false);
	}

	public static ItemStack createHotItem(ItemStack item, int temp) {
		ItemStack hot = createHotItem(item, true);
		setTemp(hot, temp);

		return hot;
	}

	public static ItemStack createHotItem(ItemStack item, boolean ignoreStats) {
		Heatable stats = Heatable.loadStats(item);
		if (stats != null) {
			ItemStack out = new ItemStack(MineFantasyItems.HOT_ITEM, item.getCount());
			NBTTagCompound nbt = getNBT(out);
			NBTTagCompound save = new NBTTagCompound();
			item.writeToNBT(save);
			nbt.setTag(Heatable.NBT_Item, save);

			nbt.setBoolean(Heatable.NBT_ShouldDisplay, true);
			setWorkTemp(out, stats.getWorkableStat(item));
			setUnstableTemp(out, stats.getUnstableStat(item));

			return out;
		} else if (ignoreStats) {
			ItemStack out = new ItemStack(MineFantasyItems.HOT_ITEM, item.getCount());
			NBTTagCompound nbt = getNBT(out);
			NBTTagCompound save = new NBTTagCompound();
			item.writeToNBT(save);
			nbt.setTag(Heatable.NBT_Item, save);

			nbt.setBoolean(Heatable.NBT_ShouldDisplay, false);
			setWorkTemp(out, 0);
			setUnstableTemp(out, 0);

			return out;
		}
		return item;
	}

	private static NBTTagCompound getNBT(ItemStack item) {
		if (!item.hasTagCompound())
			item.setTagCompound(new NBTTagCompound());
		return item.getTagCompound();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		String name = "";

		ItemStack item = getStack(stack);
		if (!item.isEmpty())
			name = item.getItem().getItemStackDisplayName(item);
		return I18n.format("prefix.hotitem.name", name);
	}

	@Override
	public IRarity getForgeRarity(ItemStack stack) {
		ItemStack item = getStack(stack);
		if (!item.isEmpty())
			return item.getItem().getRarity(item);

		return Rarity.COMMON;
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag b) {
		ItemStack item = getStack(stack);

		if (!item.isEmpty()) {
			item.getItem().addInformation(item, world, list, b);
		} else
			super.addInformation(stack, world, list, b);

		NBTTagCompound nbt = getNBT(stack);
		if (nbt.hasKey(Heatable.NBT_ShouldDisplay)) {
			if (nbt.getBoolean(Heatable.NBT_ShouldDisplay)) {
				list.add(getHeatString(stack));
				if (!getWorkString(stack).equals(""))
					list.add(getWorkString(stack));
			}
		}
	}

	private String getHeatString(ItemStack item) {
		int heat = getTemp(item);
		String unit = "*C";
		return heat + unit;
	}

	private String getWorkString(ItemStack item) {
		byte stage = Heatable.getHeatableStage(item);
		switch (stage) {
			case 1:
				return TextFormatting.YELLOW + I18n.format("state.workable");
			case 2:
				return TextFormatting.RED + I18n.format("state.unstable");
		}
		return "";
	}

	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		RayTraceResult rayTrace = this.rayTrace(world, player, true);
		ItemStack item = player.getHeldItem(hand);
		if (rayTrace == null) {
			return ActionResult.newResult(EnumActionResult.PASS, item);
		} else {
			if (rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos rayTracePos = rayTrace.getBlockPos();

				if (!world.canMineBlockBody(player, rayTracePos)) {
					return ActionResult.newResult(EnumActionResult.PASS, item);
				}

				if (!player.canPlayerEdit(rayTracePos, rayTrace.sideHit, item)) {
					return ActionResult.newResult(EnumActionResult.PASS, item);
				}
				float water = TongsHelper.getWaterSource(world, rayTracePos);

				if (water >= 0) {
					player.playSound(SoundEvents.ENTITY_GENERIC_SPLASH, 1F, 1F);
					player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 2F, 0.5F);

					for (int a = 0; a < 5; a++) {
						world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, rayTracePos.getX() + 0.5F, rayTracePos.getY() + 1, rayTracePos.getZ() + 0.5F, 0, 0.065F, 0);
					}

					ItemStack drop = getStack(item).copy();

					if (Heatable.HCCquenchRuin) {
						float damageDone = 50F + (Math.max(water, 0F));
						if (damageDone > 99F)
							damageDone = 99F;

						if (drop.isItemStackDamageable()) {
							drop.setItemDamage((int) (drop.getMaxDamage() * damageDone / 100F));
						}
					}
					drop.setCount(item.getCount());
					if (!drop.isEmpty()) {
						item.setCount(0);

						if (item.getCount() <= 0) {
							return ActionResult.newResult(EnumActionResult.PASS, drop.copy());
						}
					}
				}
			}

			return ActionResult.newResult(EnumActionResult.FAIL, item);
		}
	}

	public static int getColorIntFromItemStack(ItemStack stack) {
		if (!renderDynamicHotIngotRendering) {
			return Color.WHITE.getRGB();
		}
		int heat = getTemp(stack);
		int maxHeat = Heatable.FORGE_MAXIMUM_METAL_HEAT;
		double heatPer = (double) heat / (double) maxHeat * 100D;

		int red = getRedOnHeat();
		int green = getGreenOnHeat(heatPer);
		int blue = getBlueOnHeat(heatPer);
		if (heat > 0 && blue <= 80) {
			blue = 80;
		}

		float curr_red;
		float curr_green;
		float curr_blue;

		ItemStack held = getStack(stack);
		if (!held.isEmpty()) {
			int colour = -1;
			CustomMaterial material = CustomMaterialRegistry.getMaterialFor(held, CustomToolHelper.slot_main);
			if (material != CustomMaterialRegistry.NONE) {
				colour = material.getColourInt();
			}

			curr_red = (colour >> 16 & 255) / 255.0F;
			curr_green = (colour >> 8 & 255) / 255.0F;
			curr_blue = (colour & 255) / 255.0F;

			red = (int) (red * curr_red);
			green = (int) (green * curr_green);
			blue = (int) (blue * curr_blue);
		}

		return GuiHelper.getColourForRGB(red, green, blue);
	}

	public static int getRedOnHeat() {
		return 255;
	}

	public static int getGreenOnHeat(double percent) {
		if (percent <= 0)
			return 255;
		if (percent > 100)
			percent = 100;

		if (percent <= 55) {
			return (int) (255 - ((255 / 55) * percent));
		} else {
			return (int) ((255 / 55) * (percent - 55));
		}
	}

	public static int getBlueOnHeat(double percent) {
		if (percent <= 0)
			return 255;

		if (percent > 100)
			percent = 100;

		if (percent <= 55) {
			return (int) (255 - ((255 / 55) * percent));
		}
		return 0;
	}


	/**
	 * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
	 * update it's contents.
	 */
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		ticksExisted++;
		if (ticksExisted % 80 == 0){
			if (stack.getItem() instanceof IHotItem){
				setTemp(stack, getTemp(stack) - 1);

				if (getTemp(stack) <= 0){
					ItemStack cooledStack = getStack(stack);
					cooledStack.setCount(stack.getCount());
					entity.replaceItemInInventory(itemSlot, cooledStack);
				}
			}
		}
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Nullable
	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityItemHeated(world, (EntityItem) location);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		ItemStack stack = entityItem.getItem();
		if (entityItem.getItem().getItem() instanceof ItemHeated && !stack.getTagCompound().getBoolean("bloomery")){
			setTemp(stack, getTemp(stack) - 1);

			ItemStack cooledStack = getStack(stack);
			cooledStack.setCount(stack.getCount());
			if (getTemp(stack) <= 0){
				entityItem.setItem(cooledStack);
			}
			if (entityItem.isInWater()){
				entityItem.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 1F, 1F);
				entityItem.setItem(cooledStack);
			}
		}
		return false;
	}

	@Override
	public boolean isHot(ItemStack item) {
		return true;
	}

	@Override
	public boolean isCoolable(ItemStack item) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
		ModelLoaderHelper.registerWrappedItemModel(this, new RenderHotItem(() -> modelLocation), modelLocation);
	}
}
