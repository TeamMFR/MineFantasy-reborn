package minefantasy.mfr.util;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.api.archery.IAmmo;
import minefantasy.mfr.api.archery.IFirearm;
import minefantasy.mfr.item.ItemArrowMFR;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Utils {

	public Utils() {
		throw new IllegalStateException("Util class cannot be instantiated");
	}

	public static <T> T nullValue() {
		return null;
	}

	public static boolean doesMatch(ItemStack item1, ItemStack item2) {
		return item2.getItem() == item1.getItem() && (item2.getItemDamage() == OreDictionary.WILDCARD_VALUE
				|| item2.getItemDamage() == item1.getItemDamage());
	}

	public static String convertSnakeCaseToSplitCapitalized(String string) {
		return WordUtils.capitalize(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, string).replaceAll("_", " "));
	}

	public static String convertSplitCapitalizedToSnakeCase(String string) {
		return string.toLowerCase().replaceAll(" ", "_");
	}

	public static String serializeList(Set<String> list) {
		return list.toString().replaceAll("[\\[|\\]]", "");
	}

	public static Set<String> deserializeList(String string) {
		List<String> list = Arrays.asList(StringUtils.splitByWholeSeparator(string, ","));
		list.replaceAll(String::trim);
		return Sets.newHashSet(list);
	}

	public static boolean canAcceptArrow(ItemStack ammo, ItemStack weapon) {
		String ammoType = "null";
		if (!ammo.isEmpty() && ammo.getItem() instanceof IAmmo) {
			ammoType = ((IAmmo) ammo.getItem()).getAmmoType(ammo);
		}

		if (isVanillaArrow(ammo)) {
			ammoType = "arrow";
		}

		if (!weapon.isEmpty() && weapon.getItem() instanceof IFirearm) {
			return ((IFirearm) weapon.getItem()).canAcceptAmmo(weapon, ammoType);
		}

		return ammoType.equalsIgnoreCase("arrow");
	}

	public static boolean isVanillaArrow(ItemStack ammo) {
		return ammo.getItem() instanceof ItemArrow && !(ammo.getItem() instanceof ItemArrowMFR);
	}

	public static NBTTagCompound getOrApplyNBT(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound();
	}

	public static TileEntityChest getOtherDoubleChest(TileEntity inv) {
		if (inv instanceof TileEntityChest) {
			TileEntityChest chest = (TileEntityChest) inv;

			TileEntityChest adjacent = null;

			if (chest.adjacentChestXNeg != null) {
				adjacent = chest.adjacentChestXNeg;
			}

			if (chest.adjacentChestXPos != null) {
				adjacent = chest.adjacentChestXPos;
			}

			if (chest.adjacentChestZNeg != null) {
				adjacent = chest.adjacentChestZNeg;
			}

			if (chest.adjacentChestZPos != null) {
				adjacent = chest.adjacentChestZPos;
			}

			return adjacent;
		}
		return null;
	}

	public interface IItemPropertyGetterFix extends IItemPropertyGetter {
		float applyPropertyGetter(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn);

		static IItemPropertyGetterFix create(final IItemPropertyGetterFix lambda) {
			return lambda;
		}

		@Override
		@SideOnly(Side.CLIENT)
		default float apply(final ItemStack stack, @Nullable final World worldIn, @Nullable final EntityLivingBase entityIn) {
			return applyPropertyGetter(stack, worldIn, entityIn);
		}
	}

	/**
	 * Stores the given NBT tag inside the given NBT tag compound using the given key. Under normal circumstances, this
	 * is equivalent to {@link NBTTagCompound#setTag(String, NBTBase)}, but this method performs safety checks to
	 * prevent circular references. If storing the given tag would cause a circular reference, the tag is not stored
	 * and an error is printed to the console.
	 * @param compound The {@link NBTTagCompound} in which to store the tag.
	 * @param key The key to store the tag under.
	 * @param tag The tag to store.
	 */
	// This is a catch-all fix for issue #299.
	public static void storeTagSafely(NBTTagCompound compound, String key, NBTBase tag){

		if(compound == tag || deepContains(tag, compound)){
			MineFantasyReforged.LOG.error("Cannot store tag of type {} under key '{}' as it would result in a circular reference! Please report this (including your full log) to MFR's issue tracker.",
					NBTBase.getTypeName(tag.getId()), key);
		}else{
			compound.setTag(key, tag);
			//MineFantasyReforged.LOG.warn("writing: " + key + ": " + tag);
		}
	}

	/**
	 * Recursively searches within the first NBT tag for the second NBT tag. This handles both compound and list tags.
	 * @param toSearch The NBT tag to search inside. If this is not a compound or list tag, this method will always
	 *                 return false.
	 * @param searchFor The NBT tag to search for.
	 * @return True if the second tag appears anywhere within the NBT tree contained within the first tag, false if not.
	 */
	public static boolean deepContains(NBTBase toSearch, NBTBase searchFor){

		if(toSearch instanceof NBTTagCompound){

			for(String subKey : ((NBTTagCompound)toSearch).getKeySet()){
				NBTBase subTag = ((NBTTagCompound)toSearch).getTag(subKey);
				if(subTag == searchFor || deepContains(subTag, searchFor)) return true;
			}

		}else if(toSearch instanceof NBTTagList){
			for(NBTBase subTag : (NBTTagList)toSearch){
				if(subTag == searchFor || deepContains(subTag, searchFor)) return true;
			}
		}

		return false;
	}

	/**
	 * Provides the percent difference between two integers, provided in percentage form.
	 * @param a the first input integer
	 * @param b the second input integer
	 * @return the percent difference
	 */
	public static float percentDifferenceCalculator (int a, int b){
		float absoluteDifference = Math.abs(a - b);
		float average = (a + b) / 2F;
		return 100 * (absoluteDifference/average);
	}

	public static Long gcd(List<Long> input) {
		long result = input.get(0);
		for (int i = 1; i < input.size(); i++)
			result = gcd(result, input.get(i));
		return result;
	}

	private static long gcd(Long a, Long b) {
		while (b > 0) {
			long temp = b;
			b = a % b; // % is remainder
			a = temp;
		}
		return a;
	}

	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}
}
