package techguns.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import techguns.capabilities.TGExtendedPlayer;

/**
 * Used to sync a single variable of playerproperties to a player
 */
public class PacketTGPlayerFieldSync extends TGPacket {
	public int entityID;
	public short id;
	public boolean value;

	public static final short FIELD_JUMPBUTTONPRESSED = 0;
	public static final short FIELD_ENABLEJETPACK = 1;

	/**
	 * @param ent - the Entity to which the value belongs
	 * @param id - the field ID, mappings: 0->JumpButtonPressed (Jetpack) - boolean
	 *                                     1->enableJetpack - boolean
	 *             use the constants in this class for field id
	 * @param value - the value the field should be set (both fields are booleans)
	 */
	public PacketTGPlayerFieldSync(LivingEntity ent, short id, boolean value) {
		this.entityID = ent.getId();
		this.id = id;
		this.value = value;
	}

	public PacketTGPlayerFieldSync(FriendlyByteBuf buf) {
		this.entityID = buf.readInt();
		this.id = buf.readShort();
		this.value = buf.readBoolean();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeShort(id);
		buf.writeBoolean(value);
	}

	@Override
	protected void handle(Player localPlayer) {
		Entity entity = localPlayer.level().getEntity(this.entityID);
		if (entity instanceof Player ply) {
			TGExtendedPlayer props = TGExtendedPlayer.get(ply);
			if (props == null) {
				return;
			}
			if (this.id == FIELD_JUMPBUTTONPRESSED) {
				if (ply != localPlayer) {
					props.setJumpkeyPressed(this.value);
				}
			} else if (this.id == FIELD_ENABLEJETPACK) {
				props.enableJetpack = this.value;
			}
		}
	}
}
