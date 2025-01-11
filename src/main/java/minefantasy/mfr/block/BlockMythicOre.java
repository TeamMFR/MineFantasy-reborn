package minefantasy.mfr.block;

import minefantasy.mfr.config.ConfigSpecials;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasySounds;
import minefantasy.mfr.util.EntityUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockMythicOre extends BlockOreMF {
	private boolean isPure;

	public BlockMythicOre(String name, boolean pure) {
		super(name, 4, pure ? Rarity.EPIC : Rarity.RARE);
		isPure = pure;
		setTickRandomly(true);
	}

	@Override
	public int tickRate(World worldIn) {
		return worldIn.rand.nextInt(120) + 80;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (ConfigSpecials.mythicOreSoundTooltip && !world.isRemote) {
			List<EntityPlayer> players = EntityUtils.getEntitiesWithinRadius(10, pos.getX(), pos.getY(), pos.getZ(), world, EntityPlayer.class);
			if (!players.isEmpty())
				for (EntityPlayer player : players) {
					EnumFacing direction; // Doesn't matter what this is

					direction = EnumFacing.getFacingFromVector((float) (pos.getX() + 0.5 - player.posX),
							(float) (pos.getY() + 0.5 - (player.posY + player.getEyeHeight())),
							(float) (pos.getZ() + 0.5 - player.posZ));

					player.sendStatusMessage(new TextComponentTranslation("message.mythic_ore_sound",
							(direction.ordinal() == 0 ? new TextComponentTranslation("message.below").getUnformattedText() :
									direction.ordinal() == 1 ? new TextComponentTranslation("message.above").getUnformattedText() :
											new TextComponentTranslation("message.definite_article").getUnformattedText() + " " + direction.name())), true);
				}
		}
		world.playSound(null, pos, (isPure ? MineFantasySounds.MYTHIC_ORE : SoundEvents.ENTITY_PLAYER_LEVELUP), SoundCategory.NEUTRAL, 0.25F, rand.nextFloat() * 0.4F + 1.1F);
		int distance = -1;
		EntityPlayer closestPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 8, false);
		if (closestPlayer != null) {
			distance = (int) (Math.sqrt(pos.distanceSq(closestPlayer.posX, closestPlayer.posY, closestPlayer.posZ)));
		}
		world.scheduleUpdate(pos, this, distance != -1 ? (distance + 1) * (rand.nextInt(15) + 15) : this.tickRate(world));
	}

	/**
	 * Called after the block is set in the Chunk data, but before the Tile Entity is set
	 * Plays the sound when the block is placed
	 */
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
	}
}
