package minefantasy.mfr.network;

import io.netty.buffer.ByteBuf;
import minefantasy.mfr.tile.TileEntityBellows;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class BellowsPacket extends PacketMF {
	private BlockPos pos;
	private int press;
	private float powerLevel;

	public BellowsPacket(BlockPos pos, int press, float powerLevel) {
		this.pos = pos;
		this.press = press;
		this.powerLevel = powerLevel;
	}

	public BellowsPacket() {
	}

	@Override
	public void readFromStream(ByteBuf packet) {
		pos = BlockPos.fromLong(packet.readLong());
		press = packet.readInt();
		powerLevel = packet.readFloat();
	}

	@Override
	public void writeToStream(ByteBuf packet) {
		packet.writeLong(pos.toLong());
		packet.writeInt(press);
		packet.writeFloat(powerLevel);
	}

	@Override
	protected void execute(EntityPlayer player) {
		TileEntity tile = player.world.getTileEntity(pos);

		if (tile instanceof TileEntityBellows) {
			TileEntityBellows bellows = (TileEntityBellows) tile;
			bellows.setPress(press);
			bellows.activateForge(powerLevel);
		}
	}
}
