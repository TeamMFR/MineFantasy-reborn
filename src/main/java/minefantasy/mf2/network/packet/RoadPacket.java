package minefantasy.mf2.network.packet;

import io.netty.buffer.ByteBuf;
import minefantasy.mf2.block.tileentity.TileEntityRoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.ByteBufUtils;

public class RoadPacket extends PacketMF
{
	public static final String packetName = "MF2_RoadPacket";
	private int[] coords = new int[3];
	private int[] surface;
	private boolean isLocked;

	public RoadPacket(TileEntityRoad tile)
	{
		this.coords = new int[]{tile.xCoord, tile.yCoord, tile.zCoord};
		this.surface = tile.surface;
		this.isLocked = tile.isLocked;
	}

	public RoadPacket() {
	}

	@Override
	public void process(ByteBuf packet, EntityPlayer player) 
	{
		this.coords = new int[]{packet.readInt(), packet.readInt(), packet.readInt()};
        TileEntity entity = player.worldObj.getTileEntity(coords[0], coords[1], coords[2]);
        int s0 = packet.readInt();
        int s1 = packet.readInt();
        this.isLocked = packet.readBoolean();
        
        if(entity != null && entity instanceof TileEntityRoad)
        {
	        TileEntityRoad tile = (TileEntityRoad)entity;
	        tile.surface = new int[]{s0, s1};
	        tile.isLocked = this.isLocked;
	        
	        tile.refreshSurface();
        }
	}

	@Override
	public String getChannel()
	{
		return packetName;
	}

	@Override
	public void write(ByteBuf packet) 
	{
		for(int a = 0; a < coords.length; a++)
		{
			packet.writeInt(coords[a]);
		}
		packet.writeInt(surface[0]);
		packet.writeInt(surface[1]);
		packet.writeBoolean(this.isLocked);
	}
}
