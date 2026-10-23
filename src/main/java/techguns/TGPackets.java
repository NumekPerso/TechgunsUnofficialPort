package techguns;

import java.util.Optional;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import net.minecraftforge.network.simple.SimpleChannel;
import techguns.packets.GunFiredMessage;
import techguns.packets.PacketEntityDeathType;
import techguns.packets.PacketGunImpactFX;
import techguns.packets.PacketNotifyAmbientEffectChange;
import techguns.packets.PacketPlaySound;
import techguns.packets.PacketShootGun;
import techguns.packets.PacketShootGunTarget;
import techguns.packets.PacketSpawnParticle;
import techguns.packets.PacketSpawnParticleOnEntity;
import techguns.packets.PacketSwapWeapon;
import techguns.packets.PacketTGExtendedPlayerSync;
import techguns.packets.PacketTGKeybindPress;
import techguns.packets.ReloadStartedMessage;
import techguns.packets.TGPacket;

/**
 * Class for dealing with packets
 */
public class TGPackets {

	private static final String PROTOCOL_VERSION = "1";

	public static final TGNetworkWrapper network = new TGNetworkWrapper(NetworkRegistry.newSimpleChannel(new ResourceLocation(Techguns.MODID, "main"),
			() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals));

	private static int packetid = 0;

	public static TargetPoint targetPointAroundBlockPos(ResourceKey<Level> dimension, BlockPos pos, double distance) {
		return new TargetPoint(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, distance, dimension);
	}

	public static TargetPoint targetPointAroundEnt(Entity ent, double distance) {
		return new TargetPoint(ent.getX(), ent.getY(), ent.getZ(), distance, ent.level().dimension());
	}

	public static TargetPoint targetPoint(Level level, double x, double y, double z, double distance) {
		return new TargetPoint(x, y, z, distance, level.dimension());
	}

	public static void init() {
		registerServerbound(PacketShootGun.class, PacketShootGun::new);
		registerServerbound(PacketShootGunTarget.class, PacketShootGunTarget::new);
		registerClientbound(GunFiredMessage.class, GunFiredMessage::new);
		registerClientbound(ReloadStartedMessage.class, ReloadStartedMessage::new);
		registerClientbound(PacketPlaySound.class, PacketPlaySound::new);
		registerClientbound(PacketSpawnParticle.class, PacketSpawnParticle::new);
		registerClientbound(PacketSpawnParticleOnEntity.class, PacketSpawnParticleOnEntity::new);
		registerClientbound(PacketSwapWeapon.class, PacketSwapWeapon::new);
		registerClientbound(PacketEntityDeathType.class, PacketEntityDeathType::new);
		registerServerbound(PacketTGKeybindPress.class, PacketTGKeybindPress::new);
		registerClientbound(PacketTGExtendedPlayerSync.class, PacketTGExtendedPlayerSync::new);
		registerClientbound(PacketNotifyAmbientEffectChange.class, PacketNotifyAmbientEffectChange::new);
		registerClientbound(PacketGunImpactFX.class, PacketGunImpactFX::new);
		// machines (lot C)
		// lot E (simple machines)
		// lot F (fluids, chem lab, reaction chamber)
		registerClientbound(techguns.packets.PacketUpdateTileEntTanks.class, techguns.packets.PacketUpdateTileEntTanks::new);
		// lot G (multiblocks, explosives, turret)
		registerServerbound(techguns.packets.PacketGuiButtonClick.class, techguns.packets.PacketGuiButtonClick::new);
		registerClientbound(techguns.packets.PacketMultiBlockFormInvalidBlockMessage.class, techguns.packets.PacketMultiBlockFormInvalidBlockMessage::new);

		// additional slots / Techguns inventory (lot D)
		registerServerbound(techguns.packets.PacketOpenPlayerGUI.class, techguns.packets.PacketOpenPlayerGUI::new);
		registerClientbound(techguns.packets.PacketTGPlayerFieldSync.class, techguns.packets.PacketTGPlayerFieldSync::new);

		// blocks (lot B)

		// lot I (radiation)
		registerClientbound(techguns.radiation.PacketRadiationSync.class, techguns.radiation.PacketRadiationSync::new);

		// lot J (NPCs)

		// lot K (world: structures, dungeons)

		// lot M (JEI, manual)
		registerClientbound(techguns.packets.PacketDoorStateChange.class, techguns.packets.PacketDoorStateChange::new);

	}

	public static <T extends TGPacket> void registerClientbound(Class<T> clazz, Function<FriendlyByteBuf, T> decoder) {
		network.channel.registerMessage(packetid++, clazz, TGPacket::encode, decoder, TGPacket::handleMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

	public static <T extends TGPacket> void registerServerbound(Class<T> clazz, Function<FriendlyByteBuf, T> decoder) {
		network.channel.registerMessage(packetid++, clazz, TGPacket::encode, decoder, TGPacket::handleMessage, Optional.of(NetworkDirection.PLAY_TO_SERVER));
	}

	/**
	 * Mimics the 1.12 SimpleNetworkWrapper API so the ported code stays close to the original.
	 */
	public static class TGNetworkWrapper {
		public final SimpleChannel channel;

		public TGNetworkWrapper(SimpleChannel channel) {
			this.channel = channel;
		}

		public void sendToAllAround(TGPacket msg, TargetPoint point) {
			channel.send(PacketDistributor.NEAR.with(() -> point), msg);
		}

		public void sendToServer(TGPacket msg) {
			channel.sendToServer(msg);
		}

		public void sendTo(TGPacket msg, ServerPlayer player) {
			channel.send(PacketDistributor.PLAYER.with(() -> player), msg);
		}

		public void sendToAll(TGPacket msg) {
			channel.send(PacketDistributor.ALL.noArg(), msg);
		}

		public void sendToDimension(TGPacket msg, ResourceKey<Level> dimension) {
			channel.send(PacketDistributor.DIMENSION.with(() -> dimension), msg);
		}

		public void sendToAllTrackingAndSelf(TGPacket msg, Entity ent) {
			channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> ent), msg);
		}
	}
}
