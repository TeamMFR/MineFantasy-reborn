package minefantasy.mfr.block;

import minefantasy.mfr.api.crafting.IIgnitable;
import minefantasy.mfr.api.heating.ForgeFuel;
import minefantasy.mfr.api.heating.Heatable;
import minefantasy.mfr.api.heating.TongsHelper;
import minefantasy.mfr.api.tool.ILighter;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.item.ItemApron;
import minefantasy.mfr.item.ItemLighter;
import minefantasy.mfr.item.ItemTongs;
import minefantasy.mfr.registry.ForgeFuelRegistry;
import minefantasy.mfr.tile.TileEntityForge;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockForge extends BlockTileEntity<TileEntityForge> implements IIgnitable {
	private static final PropertyBool BURNING = PropertyBool.create("burning");
	public static final PropertyBool UNDER = PropertyBool.create("under");
	private static final PropertyInteger FUEL_COUNT = PropertyInteger.create("fuel_count", 0, 3);

	public int tier;
	public String type;

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0F, 0F, 0F, 1F, 0.5F, 1F);

	public BlockForge(String type, int tier) {
		super(tier == 1 ? Material.IRON : Material.ROCK);
		this.tier = tier;
		this.type = type;

		setRegistryName("forge_" + type);
		setTranslationKey("forge_" + type);
		this.setSoundType(SoundType.STONE);
		this.setHardness(5F);
		this.setResistance(8F);
		this.setCreativeTab(MineFantasyTabs.tabUtil);
		setDefaultState(blockState.getBaseState().withProperty(BURNING,false).withProperty(FUEL_COUNT, 0).withProperty(UNDER, false));
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BURNING, FUEL_COUNT, UNDER);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityForge();
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntityForge tile = (TileEntityForge) getTile(world, pos);
		return state.withProperty(BURNING, tile.isLit()).withProperty(FUEL_COUNT, tile.getFuelCount()).withProperty(UNDER, tile.hasBlockAbove());
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;

		if (state.getValue(FUEL_COUNT) == 1) {
			i |= 1;
		}

		if (state.getValue(FUEL_COUNT) == 2) {
			i |= 2;
		}

		if (state.getValue(FUEL_COUNT) == 3) {
			i |= 3;
		}

		if (state.getValue(BURNING)) {
			i |= 4;
		}

		if (state.getValue(UNDER)) {
			i |= 8;
		}

		return i;
	}

	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FUEL_COUNT, (meta & 3)).withProperty(BURNING, (meta & 4) > 0).withProperty(UNDER, (meta & 8) > 0);
	}

	public static void setActiveState(boolean burning, int fuelCount, boolean under, World world, BlockPos pos) {
		world.setBlockState(pos, world.getBlockState(pos).withProperty(BURNING, burning).withProperty(FUEL_COUNT, fuelCount).withProperty(UNDER, under), 2);
	}

	@Nonnull
	@Override
	public IBlockState getStateForPlacement(final World world, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer, final EnumHand hand) {
		return getDefaultState().withProperty(BURNING, false).withProperty(FUEL_COUNT, 0).withProperty(UNDER, false);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntityForge tile = (TileEntityForge) getTile(world, pos);
		if (tile != null) {
			setActiveState(false, 0, tile.hasBlockAbove(), world, pos);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(BURNING) ? 15 : 0;
	}

	/**
	 * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
	 * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
	 * <p>
	 * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that
	 * does not fit the other descriptions and will generally cause other things not to connect to the face.
	 *
	 * @return an approximation of the form of the given face
	 */
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isBurning(IBlockAccess world, BlockPos pos) {
		TileEntityForge tile = (TileEntityForge) getTile(world, pos);
		return tile != null && tile.isLit();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack held = player.getHeldItemMainhand();
		TileEntityForge forge = (TileEntityForge) world.getTileEntity(pos);
		if (forge != null) {
			// Burn unprotected players
			if (forge.isLit() && !ItemApron.isUserProtected(player)) {
				player.setFire(5);
				player.attackEntityFrom(DamageSource.ON_FIRE, player.isWet() ? 3 : 1);
				if (!player.world.isRemote) {
					player.sendMessage(new TextComponentTranslation("info.noHeatProtection.message"));
				}
			}
			if (!held.isEmpty()) {
				// Tong use
				if (facing == EnumFacing.UP && held.getItem() instanceof ItemTongs && onUsedTongs(player, held, forge)) {
					return true;
				}
				// Ignition
				if (held.getItem() instanceof ItemFlintAndSteel || held.getItem() instanceof ILighter) {
					if (!forge.isLit() && forge.getFuel() > 0 && forge.getTier() != 1) {
						// 1 for ignition, -1 for a failed attempt, 0 for a null input or for an item that needs to bypass normal ignition
						int uses = ItemLighter.tryUse(held, player);
						if (uses != 0) {
							player.playSound(SoundEvents.ITEM_FLINTANDSTEEL_USE, 1.0F, 1.0F);
							if (uses == 1 && !world.isRemote) {
								held.damageItem(1, player);
								igniteBlock(world, pos, state);
							}
						}
						return true;
					}
				}
				// Adding heatable items
				if (Heatable.canHeatItem(held) && forge.tryAddHeatable(held)) {
					held.shrink(1);
					if (held.getCount() <= 0) {
						player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
					}
					return true;
				}

				// Adding fuel
				ForgeFuel stats = ForgeFuelRegistry.getFuelStats(held);

				if (stats != null && forge.addFuel(stats, true)) {
					if (player.capabilities.isCreativeMode) {
						return true;
					}
					ItemStack heldItem = player.getHeldItemMainhand();
					if (heldItem.getItem().getContainerItem() != null) {
						ItemStack cont = new ItemStack(heldItem.getItem().getContainerItem());
						if (player.getHeldItem(hand).getCount() == 1) {
							player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, cont);
							return true;
						} else {
							if (!player.inventory.addItemStackToInventory(cont)) {
								player.entityDropItem(cont, 0.0F);
							}
						}
					}
					if (heldItem.getCount() == 1) {
						player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
					} else {
						heldItem.shrink(1);
					}
					return true;
				}
			}
			// Open GUI
			if (!world.isRemote && !forge.hasBlockAbove()) {
				TileEntityForge tileEntity = (TileEntityForge) getTile(world, pos);
				if (tileEntity != null) {
					tileEntity.openGUI(world, player);
				}
			}
		}
		return true;
	}

	/**
	 * Standardized function to handle block ignition
	 * @param world World
	 * @param pos BlockPos
	 * @param state IBlockState
	 */
	@Override
	public void igniteBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityForge forge = (TileEntityForge) getTile(world, pos);
		if (forge != null) {
			if (world.isRainingAt(pos.add(0, 1, 0)) && forge.getFuel() > 0) {
				world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.AMBIENT, 0.5F, 1.0F);
				//world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D, 0F, 0.1F, 0F);
			} else if (!state.getValue(BURNING) && forge.getFuel() > 0) {
				IIgnitable.playIgnitionSound(world, pos);
				forge.setIsLit(true);
				setActiveState(true, forge.getFuelCount(), forge.hasBlockAbove(), world, pos);
			}
		}
	}

	private boolean onUsedTongs(EntityPlayer user, ItemStack held, TileEntityForge tile) {
		ItemStack contents = tile.getInventory().getStackInSlot(0);
		ItemStack grabbed = TongsHelper.getHeldItem(held);

		// GRAB
		if (grabbed.isEmpty()) {
			if (!contents.isEmpty() && contents.getItem() == MineFantasyItems.HOT_ITEM) {
				if (TongsHelper.trySetHeldItem(held, contents)) {
					tile.getInventory().setStackInSlot(0, ItemStack.EMPTY);
					return true;
				}
			}
		} else {
			if (contents.isEmpty()) {
				tile.getInventory().setStackInSlot(0, grabbed);
				TongsHelper.clearHeldItem(held, user);
				return true;
			}
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
		if (world.getTileEntity(pos) instanceof TileEntityForge && !(((TileEntityForge) world.getTileEntity(pos)).isBurning())) {
			return;
		}

		for (int i = 0; i < 3; ++i) {
			double x = pos.getX() + 0.25D + random.nextDouble() * 0.5D;
			double y = pos.getY() + random.nextDouble() * 0.5D;
			double z = pos.getZ() + 0.25D + random.nextDouble() * 0.5D;
			world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
			TileEntityForge tile = (TileEntityForge) world.getTileEntity(pos);
			if (tile != null && tile.isOutside()) {
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		TileEntityForge tile = (TileEntityForge) getTile(world, pos);
		if (tier == 1 && !world.isRemote) {
			if (tile.isLit() && !world.isBlockPowered(pos)) {
				setActiveState(false, tile.getFuelCount(), tile.hasBlockAbove(), world, pos);
				tile.setIsLit(false);
				world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.AMBIENT, 0.5F, 1.0F);
			} else if (!tile.isLit() && world.isBlockPowered(pos)) {
				igniteBlock(world, pos, state);
				world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.AMBIENT, 1.0F, 1.0F);
			}
		}
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		TileEntityForge tile = (TileEntityForge) getTile(world, pos);
		if (tier == 1 && !world.isRemote && tile.isLit() && !world.isBlockPowered(pos)) {
			setActiveState(tile.isLit(), tile.getFuelCount(), tile.hasBlockAbove(), world, pos);
		}
	}
}
