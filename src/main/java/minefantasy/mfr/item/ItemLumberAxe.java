package minefantasy.mfr.item;

import minefantasy.mfr.api.weapon.IRackItem;
import minefantasy.mfr.client.render.item.RenderBigTool;
import minefantasy.mfr.config.ConfigTools;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.mechanics.StaminaMechanics;
import minefantasy.mfr.tile.TileEntityRack;
import minefantasy.mfr.util.BlockUtils;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.ModelLoaderHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import static minefantasy.mfr.registry.CustomMaterialRegistry.DECIMAL_FORMAT;

public class ItemLumberAxe extends ItemAxeMFR implements IRackItem {
	private final Random rand = new Random();

	public ItemLumberAxe(String name, Item.ToolMaterial material, Rarity rarity) {
		super(name, material, rarity);
		this.setMaxDamage(getMaxDamage() * 5);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack item, World world, IBlockState state, BlockPos pos, EntityLivingBase user) {
		if (!user.world.isRemote && user instanceof EntityPlayer && StaminaMechanics.canAcceptCost(user)) {
			breakTree(world, pos, item, user, ConfigTools.lumberAxeMaxLogs);
		}
		return super.onBlockDestroyed(item, world, state, pos, user);
	}

	private void breakTree(World world, BlockPos startPos, ItemStack item, EntityLivingBase user, int maxLogs) {
		Queue<BlockPos> queue = new LinkedList<>();
		Set<BlockPos> logsToBreak = new HashSet<>();
		Set<BlockPos> checkedBlocks = new HashSet<>();

		queue.add(startPos);
		//Continue searching till the queue is empty, or the maxLogs to break has been reached
		while (!queue.isEmpty() && logsToBreak.size() < maxLogs) {
			//Remove the current pos from the queue, and use it for checking
			BlockPos currentPos = queue.remove();
			//Used for remembering removed positions that have been checked already while removing them from the queue
			checkedBlocks.add(currentPos);

			//The current block must be a log, and must be within 4 blocks in the horizontal directions of the startPos
			if (isLog(world.getBlockState(currentPos)) && BlockUtils.isBlockWithinHorizontalRange(startPos, currentPos, 4)) {
				logsToBreak.add(currentPos);

				//check adjacent blocks in a one block radius
				for (int y = -1; y <= 1; y++) {
					for (int x = -1; x <= 1; x++) {
						for (int z = -1; z <= 1; z++) {
							BlockPos adjacentPos = currentPos.add(x, y, z);
							//Adjacent block must be a log, must not already be checked, and must not already be in the queue
							if (isLog(world.getBlockState(adjacentPos))
									&& !checkedBlocks.contains(adjacentPos)
									&& !queue.contains(adjacentPos)) {
								queue.add(adjacentPos);
							}
						}
					}
				}
			}
		}

		//Loop through the set of logs to break
		logsToBreak.forEach(posToBreak -> {
			IBlockState brokenState = world.getBlockState(posToBreak);
			//Handle item drop
			if (rand.nextFloat() * 100F < (100F - ConfigTools.heavy_tool_drop_chance)) {
				brokenState.getBlock().dropBlockAsItem(world, posToBreak, brokenState, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, item));
			}

			//Handle block breaking and item damage
			world.setBlockToAir(posToBreak);
			item.damageItem(1, user);

			//Handle Stamina Mechanics
			if (user instanceof EntityPlayer) {
				StaminaMechanics.tirePlayer((EntityPlayer) user, 0.5F);
			}
		});
	}

	private boolean isLog(IBlockState state) {
		Block block = state.getBlock();
		return block == Blocks.LOG
				|| block == Blocks.LOG2
				|| OreDictionary.getOres("logWood").contains(new ItemStack(block))
				|| state.getMaterial() == Material.WOOD;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		CustomMaterial material = CustomToolHelper.getCustomPrimaryMaterial(stack);
		float efficiency = material.getHardness() > 0 ? material.getHardness() : this.efficiency;
		return !state.getBlock().isToolEffective("axe", state)
				? super.getDestroySpeed(stack, state)
				: CustomToolHelper.getEfficiency(stack, efficiency, efficiencyMod / 8F);
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
	public void addInformation(ItemStack item, World world, List<String> list, ITooltipFlag flag) {
		if (isCustom) {
			CustomToolHelper.addInformation(item, list);
		}

		CustomMaterial material = CustomToolHelper.getCustomPrimaryMaterial(item);
		float efficiency = material.getHardness() > 0 ? material.getHardness() : this.efficiency;
		list.add(TextFormatting.GREEN + I18n.format("attribute.tool.digEfficiency.name",
				DECIMAL_FORMAT.format(CustomToolHelper.getEfficiency(item, efficiency, efficiencyMod / 8F))));
	}

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
		return 1.8F;
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
		return true;
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
