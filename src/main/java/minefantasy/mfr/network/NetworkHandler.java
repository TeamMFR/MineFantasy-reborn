package minefantasy.mfr.network;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.client.gui.GuiAnvilMF;
import minefantasy.mfr.client.gui.GuiBigFurnace;
import minefantasy.mfr.client.gui.GuiBlastChamber;
import minefantasy.mfr.client.gui.GuiBlastHeater;
import minefantasy.mfr.client.gui.GuiBloomery;
import minefantasy.mfr.client.gui.GuiBombBench;
import minefantasy.mfr.client.gui.GuiCarpenterMF;
import minefantasy.mfr.client.gui.GuiCrossbowBench;
import minefantasy.mfr.client.gui.GuiCrucible;
import minefantasy.mfr.client.gui.GuiForge;
import minefantasy.mfr.client.gui.GuiKitchenBench;
import minefantasy.mfr.client.gui.GuiKnowledgeEntry;
import minefantasy.mfr.client.gui.GuiKnowledgeMenu;
import minefantasy.mfr.client.gui.GuiQuern;
import minefantasy.mfr.client.gui.GuiReload;
import minefantasy.mfr.client.gui.GuiResearchBench;
import minefantasy.mfr.container.ContainerReload;
import minefantasy.mfr.mechanics.knowledge.InformationList;
import minefantasy.mfr.tile.TileEntityAnvil;
import minefantasy.mfr.tile.TileEntityBigFurnace;
import minefantasy.mfr.tile.TileEntityBloomery;
import minefantasy.mfr.tile.TileEntityBombBench;
import minefantasy.mfr.tile.TileEntityCarpenter;
import minefantasy.mfr.tile.TileEntityCrossbowBench;
import minefantasy.mfr.tile.TileEntityCrucible;
import minefantasy.mfr.tile.TileEntityForge;
import minefantasy.mfr.tile.TileEntityKitchenBench;
import minefantasy.mfr.tile.TileEntityQuern;
import minefantasy.mfr.tile.TileEntityResearchBench;
import minefantasy.mfr.tile.blastfurnace.TileEntityBlastChamber;
import minefantasy.mfr.tile.blastfurnace.TileEntityBlastHeater;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

public class NetworkHandler implements IGuiHandler {

	public static final NetworkHandler INSTANCE = new NetworkHandler();
	public static final String CHANNEL_NAME = "MFR";

	//unused: 16, 18, 20, 25
	private static final int PLAYER_SYNC_PACKET = 1;
	private static final int STAMINA_PACKET = 2;
	private static final int PARRY_PACKET = 3;
	private static final int HIT_SOUND_PACKET = 4;
	private static final int EXTENDED_REACH_PACKET = 5;
	private static final int ARTEFACT_PACKET = 6;
	private static final int KNOWLEDGE_PACKET = 7;
	private static final int RESEARCH_REQUEST_PACKET = 8;
	private static final int REMOVE_OFFHAND_PACKET = 9;
	private static final int OPEN_RELOAD_GUI_PACKET = 10;
	private static final int LEVEL_UP_PACKET = 11;
	private static final int FORGE_PACKET = 12;
	private static final int RESEARCH_TABLE_PACKET = 13;
	private static final int TROUGH_PACKET = 14;
	private static final int BOMB_BENCH_PACKET = 15;
	private static final int BELLOW_PACKET = 16;
	private static final int ROAD_PACKET = 17;
	private static final int CROSSBOW_BENCH_PACKET = 19;
	private static final int BIG_FURNACE_PACKET = 21;
	private static final int DODGE_COMMAND_PACKET = 22;
	private static final int RACK_COMMAND_PACKET = 23;
	private static final int AMMO_BOX_COMMAND_PACKET = 24;
	private static final int COGWORK_CONTROL_PACKET = 26;
	private static final int SPARK_PARTICLE_PACKET = 27;

	//unused:
	public static final int GUI_CRUCIBLE = 1;
	public static final int GUI_BLAST_CHAMBER = 2;
	public static final int GUI_BLAST_HEATER = 3;
	public static final int GUI_QUERN = 4;
	public static final int GUI_BLOOMERY = 5;
	public static final int GUI_FORGE = 6;
	public static final int GUI_BIG_FURNANCE = 7;
	public static final int GUI_CARPENTER_BENCH = 8;
	public static final int GUI_BOMB_BENCH = 9;
	public static final int GUI_RESEARCH_BENCH = 10;
	public static final int GUI_CROSSBOW_BENCH = 11;
	public static final int GUI_ANVIL = 12;
	public static final int GUI_RESEARCH_BOOK = 13;
	public static final int GUI_RELOAD = 14;
	public static final int GUI_KITCHEN_BENCH = 15;

	private FMLEventChannel channel;

	public final void registerNetwork() {
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNEL_NAME);

		channel.register(new PacketHandlerMF());

		PacketMF.registerPacket(PLAYER_SYNC_PACKET, PlayerDataPacket.class, PlayerDataPacket::new);
		PacketMF.registerPacket(STAMINA_PACKET, StaminaPacket.class, StaminaPacket::new);
		PacketMF.registerPacket(PARRY_PACKET, ParryPacket.class, ParryPacket::new);
		PacketMF.registerPacket(HIT_SOUND_PACKET, HitSoundPacket.class, HitSoundPacket::new);
		PacketMF.registerPacket(EXTENDED_REACH_PACKET, ExtendedReachPacket.class, ExtendedReachPacket::new);
		PacketMF.registerPacket(ARTEFACT_PACKET, ArtefactPacket.class, ArtefactPacket::new);
		PacketMF.registerPacket(KNOWLEDGE_PACKET, KnowledgePacket.class, KnowledgePacket::new);
		PacketMF.registerPacket(RESEARCH_REQUEST_PACKET, ResearchRequestPacket.class, ResearchRequestPacket::new);
		PacketMF.registerPacket(REMOVE_OFFHAND_PACKET, RemoveOffhandPacket.class, RemoveOffhandPacket::new);
		PacketMF.registerPacket(OPEN_RELOAD_GUI_PACKET, OpenReloadGUIPacket.class, OpenReloadGUIPacket::new);
		PacketMF.registerPacket(LEVEL_UP_PACKET, LevelUpPacket.class, LevelUpPacket::new);
		PacketMF.registerPacket(RESEARCH_TABLE_PACKET, ResearchTablePacket.class, ResearchTablePacket::new);
		PacketMF.registerPacket(TROUGH_PACKET, TroughPacket.class, TroughPacket::new);
		PacketMF.registerPacket(FORGE_PACKET, ForgePacket.class, ForgePacket::new);
		PacketMF.registerPacket(BOMB_BENCH_PACKET, BombBenchPacket.class, BombBenchPacket::new);
		PacketMF.registerPacket(BELLOW_PACKET, BellowsPacket.class, BellowsPacket::new);
		PacketMF.registerPacket(ROAD_PACKET, RoadPacket.class, RoadPacket::new);
		PacketMF.registerPacket(CROSSBOW_BENCH_PACKET, CrossbowBenchPacket.class, CrossbowBenchPacket::new);
		PacketMF.registerPacket(BIG_FURNACE_PACKET, BigFurnacePacket.class, BigFurnacePacket::new);
		PacketMF.registerPacket(DODGE_COMMAND_PACKET, DodgeCommandPacket.class, DodgeCommandPacket::new);
		PacketMF.registerPacket(RACK_COMMAND_PACKET, RackCommandPacket.class, RackCommandPacket::new);
		PacketMF.registerPacket(AMMO_BOX_COMMAND_PACKET, AmmoBoxCommandPacket.class, AmmoBoxCommandPacket::new);
		PacketMF.registerPacket(COGWORK_CONTROL_PACKET, CogworkControlPacket.class, CogworkControlPacket::new);
		PacketMF.registerPacket(SPARK_PARTICLE_PACKET, SparkParticlePacket.class, SparkParticlePacket::new);

		NetworkRegistry.INSTANCE.registerGuiHandler(MineFantasyReforged.INSTANCE, this);

	}

	public static void sendToServer(PacketMF pkt) {
		INSTANCE.channel.sendToServer(pkt.getFMLPacket());
	}

	public static void sendToPlayer(EntityPlayerMP player, PacketMF pkt) {
		INSTANCE.channel.sendTo(pkt.getFMLPacket(), player);
	}

	public static void sendToAllPlayers(PacketMF pkt) {
		//noinspection Co9öu4nstantConditions
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList() != null) {
			INSTANCE.channel.sendToAll(pkt.getFMLPacket());
		}
	}

	public static void sendToAllTracking(Entity e, PacketMF pkt) {
		WorldServer server = (WorldServer) e.world;
		server.getEntityTracker().sendToTracking(e, pkt.getFMLPacket());
	}

	public static void sendToAllTrackingBlock(TargetPoint targetPoint, PacketMF pkt) {
		INSTANCE.channel.sendToAllTracking(pkt.getFMLPacket(), targetPoint);
	}

	public static void sendToAllTrackingChunk(World world, int cx, int cz, PacketMF packet) {
		WorldServer server = (WorldServer) world;
		for (EntityPlayer p : server.playerEntities) {
			if (server.getPlayerChunkMap().isPlayerWatchingChunk((EntityPlayerMP) p, cx, cz)) {
				sendToPlayer((EntityPlayerMP) p, packet);
			}
		}
	}

	@Override
	@Nullable
	public Object getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		final TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

		if (tileEntity != null) {
			switch (ID) {
				case GUI_CRUCIBLE:
					return ((TileEntityCrucible) tileEntity).createContainer(player);
				case GUI_BLAST_CHAMBER:
					return ((TileEntityBlastChamber) tileEntity).createContainer(player);
				case GUI_BLAST_HEATER:
					return ((TileEntityBlastHeater) tileEntity).createContainer(player);
				case GUI_BLOOMERY:
					return ((TileEntityBloomery) tileEntity).createContainer(player);
				case GUI_QUERN:
					return ((TileEntityQuern) tileEntity).createContainer(player);
				case GUI_FORGE:
					return ((TileEntityForge) tileEntity).createContainer(player);
				case GUI_BIG_FURNANCE:
					return ((TileEntityBigFurnace) tileEntity).createContainer(player);
				case GUI_CARPENTER_BENCH:
					return ((TileEntityCarpenter) tileEntity).createContainer(player);
				case GUI_KITCHEN_BENCH:
					return ((TileEntityKitchenBench) tileEntity).createContainer(player);
				case GUI_BOMB_BENCH:
					return ((TileEntityBombBench) tileEntity).createContainer(player);
				case GUI_RESEARCH_BENCH:
					return ((TileEntityResearchBench) tileEntity).createContainer(player);
				case GUI_CROSSBOW_BENCH:
					return ((TileEntityCrossbowBench) tileEntity).createContainer(player);
				case GUI_ANVIL:
					return ((TileEntityAnvil) tileEntity).createContainer(player);
			}
		}
		if (ID == GUI_RELOAD && x == 1 && !player.getHeldItemMainhand().isEmpty()) {
			return new ContainerReload(player.inventory, player.getHeldItemMainhand());
		}

		return null;
	}

	@Override
	@Nullable
	public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		Minecraft mc = Minecraft.getMinecraft();
		final TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

		if (tileEntity != null) {
			switch (ID) {
				case GUI_CRUCIBLE:
					return new GuiCrucible(((TileEntityCrucible) tileEntity).createContainer(player), (TileEntityCrucible) tileEntity);
				case GUI_BLAST_CHAMBER:
					return new GuiBlastChamber(((TileEntityBlastChamber) tileEntity).createContainer(player), (TileEntityBlastChamber) tileEntity);
				case GUI_BLAST_HEATER:
					return new GuiBlastHeater(((TileEntityBlastHeater) tileEntity).createContainer(player), (TileEntityBlastHeater) tileEntity);
				case GUI_BLOOMERY:
					return new GuiBloomery(((TileEntityBloomery) tileEntity).createContainer(player), (TileEntityBloomery) tileEntity);
				case GUI_QUERN:
					return new GuiQuern(((TileEntityQuern) tileEntity).createContainer(player), (TileEntityQuern) tileEntity);
				case GUI_FORGE:
					return new GuiForge(((TileEntityForge) tileEntity).createContainer(player), (TileEntityForge) tileEntity);
				case GUI_BIG_FURNANCE:
					return new GuiBigFurnace(((TileEntityBigFurnace) tileEntity).createContainer(player), (TileEntityBigFurnace) tileEntity);
				case GUI_CARPENTER_BENCH:
					return new GuiCarpenterMF(((TileEntityCarpenter) tileEntity).createContainer(player), (TileEntityCarpenter) tileEntity);
				case GUI_KITCHEN_BENCH:
					return new GuiKitchenBench(((TileEntityKitchenBench) tileEntity).createContainer(player), (TileEntityKitchenBench) tileEntity);
				case GUI_BOMB_BENCH:
					return new GuiBombBench(((TileEntityBombBench) tileEntity).createContainer(player), (TileEntityBombBench) tileEntity);
				case GUI_RESEARCH_BENCH:
					return new GuiResearchBench(((TileEntityResearchBench) tileEntity).createContainer(player), (TileEntityResearchBench) tileEntity);
				case GUI_CROSSBOW_BENCH:
					return new GuiCrossbowBench(((TileEntityCrossbowBench) tileEntity).createContainer(player), (TileEntityCrossbowBench) tileEntity);
				case GUI_ANVIL:
					return new GuiAnvilMF(((TileEntityAnvil) tileEntity).createContainer(player), (TileEntityAnvil) tileEntity);
			}
		}
		switch (ID) {
			case GUI_RESEARCH_BOOK:
				if (x == 0) {
					if (y >= 0) {
						return new GuiKnowledgeEntry(mc.currentScreen, InformationList.knowledgeList.get(y));
					}
					return new GuiKnowledgeMenu(player);
				}
			case GUI_RELOAD:
				if (x == 1 && !player.getHeldItemMainhand().isEmpty()) {
					return new GuiReload(player.inventory, player.getHeldItemMainhand());
				}
		}
		return null;
	}
}
