package minefantasy.mfr.block;

import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.item.ItemFoodMFR;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockCakeMFR extends BasicBlockMF {
	public static final PropertyInteger BITES = PropertyInteger.create("bites", 0, 8);
	protected static final AxisAlignedBB[] CAKE_AABB = new AxisAlignedBB[] {
			new AxisAlignedBB(1D / 16D, 0.0D, 1D / 16D, 15D / 16D, 0.5D, 15D / 16D),  //0
			new AxisAlignedBB(1D / 16D, 0.0D, 1D / 16D, 15D / 16D, 0.5D, 13.5D / 16D),//1
			new AxisAlignedBB(1D / 16D, 0.0D, 1D / 16D, 15D / 16D, 0.5D, 12D / 16D),  //2
			new AxisAlignedBB(1D / 16D, 0.0D, 1D / 16D, 15D / 16D, 0.5D, 10.5D / 16D),//3
			new AxisAlignedBB(1D / 16D, 0.0D, 1D / 16D, 15D / 16D, 0.5D, 9D / 16D),   //4
			new AxisAlignedBB(1D / 16D, 0.0D, 1D / 16D, 15D / 16D, 0.5D, 7.5D / 16D), //5
			new AxisAlignedBB(1D / 16D, 0.0D, 1D / 16D, 15D / 16D, 0.5D, 6D / 16D),   //6
			new AxisAlignedBB(1D / 16D, 0.0D, 1D / 16D, 15D / 16D, 0.5D, 4.5D / 16D), //7
			new AxisAlignedBB(1D / 16D, 0.0D, 1D / 16D, 15D / 16D, 0.5D, 3D / 16D)    //8
	};
	private Item cakeSlice;

	public BlockCakeMFR(String name, Item slice) {
		super(name, Material.CAKE);

		cakeSlice = slice;

		this.setTickRandomly(true);
		setCreativeTab(MineFantasyTabs.tabFood);
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BITES);
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(BITES, meta);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BITES);
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return CAKE_AABB[state.getValue(BITES)];
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube? This determines whether or
	 * not to render the shared face of two adjacent blocks and also whether the
	 * player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates.
	 * Args: world, x, y, z
	 */
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		if (world.getBlockState(pos.down()).getBlock() instanceof BlockCakeMFR) {
			return false;
		}
		return super.canPlaceBlockAt(world, pos) && this.canBlockStay(world, pos);
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which
	 * neighbor changed (coordinates passed are their own) Args: x, y, z, neighbor
	 * Block
	 */
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!this.canBlockStay(world, pos)) {
			ItemStack item = new ItemStack(this, 1, damageDropped(world.getBlockState(pos)));
			EntityItem drop = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, item);
			world.spawnEntity(drop);
			world.setBlockToAir(pos);
		}
	}

	/**
	 * Can this block stay at this position. Similar to canPlaceBlockAt except gets
	 * checked often with plants.
	 */

	public boolean canBlockStay(World world, BlockPos pos) {
		return world.getBlockState(pos.add(0, -1, 0)).getMaterial().isSolid();
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(BITES);
	}

	public Rarity getRarity() {
		if (cakeSlice instanceof ItemFoodMFR) {
			return ((ItemFoodMFR) cakeSlice).itemRarity;
		}
		return Rarity.COMMON;
	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		for (int i : BITES.getAllowedValues()) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(getRegistryName(), "bites=" + i));
		}
	}
}