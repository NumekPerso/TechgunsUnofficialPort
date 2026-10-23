package techguns.packets;

import java.util.BitSet;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.capabilities.TGExtendedPlayer;
import techguns.gui.player.TGPlayerInventory;
import techguns.util.DataUtil;

/**
 * Syncs the TGExtendedPlayer to a client. allSlots=true (sent to the player itself): flags, food, radiation and the whole
 * Techguns inventory; allSlots=false (sent to other players): flags and the visible slots (face, back, hand).
 */
public class PacketTGExtendedPlayerSync extends TGPacket {
	public int entityId;
	public NonNullList<ItemStack> inv = NonNullList.<ItemStack>withSize(TGPlayerInventory.NUMSLOTS, ItemStack.EMPTY);
	public int radlevel;
	public short foodLeft = 0;
	public boolean nightVisionEnabled = false;
	public boolean safemodeEnabled = true;
	public float lastSaturation = 0f;
	public boolean hovermodeEnabled = false;
	public boolean stepAssist;
	public boolean showHUD;
	public boolean enableJetpack;
	private boolean allSlots = false;

	public PacketTGExtendedPlayerSync(Player ply, TGExtendedPlayer props) {
		this(ply, props, true);
	}

	public PacketTGExtendedPlayerSync(Player ply, TGExtendedPlayer props, boolean allSlots) {
		this.entityId = ply.getId();
		this.nightVisionEnabled = props.enableNightVision;
		this.safemodeEnabled = props.enableSafemode;
		this.hovermodeEnabled = props.enableHovermode;
		this.stepAssist = props.enableStepAssist;
		this.showHUD = props.showTGHudElements;
		this.enableJetpack = props.enableJetpack;
		for (int i = 0; i < TGPlayerInventory.NUMSLOTS; i++) {
			//copy: the packet may be encoded later on the network thread
			this.inv.set(i, props.tg_inventory.inventory.get(i).copy());
		}
		if (allSlots) {
			this.radlevel = props.radlevel;
			this.foodLeft = props.foodleft;
			this.lastSaturation = props.lastSaturation;
		}
		this.allSlots = allSlots;
	}

	public PacketTGExtendedPlayerSync(FriendlyByteBuf buf) {
		this.entityId = buf.readInt();
		BitSet states = DataUtil.uncompress(buf.readByte());
		this.enableJetpack = states.get(0);
		this.nightVisionEnabled = states.get(1);
		this.safemodeEnabled = states.get(2);
		this.stepAssist = states.get(3);
		this.showHUD = states.get(4);
		this.hovermodeEnabled = states.get(5);

		this.allSlots = buf.readBoolean();
		if (allSlots) {
			this.radlevel = buf.readInt();
			this.foodLeft = buf.readShort();
			this.lastSaturation = buf.readFloat();
			for (int i = 0; i < TGPlayerInventory.NUMSLOTS; i++) {
				this.inv.set(i, buf.readItem());
			}
		} else {
			this.inv.set(TGPlayerInventory.SLOT_FACE, buf.readItem());
			this.inv.set(TGPlayerInventory.SLOT_BACK, buf.readItem());
			this.inv.set(TGPlayerInventory.SLOT_HAND, buf.readItem());
		}
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeByte(DataUtil.compress(this.enableJetpack, this.nightVisionEnabled, this.safemodeEnabled, this.stepAssist, this.showHUD, this.hovermodeEnabled));
		buf.writeBoolean(this.allSlots);
		if (allSlots) {
			buf.writeInt(radlevel);
			buf.writeShort(foodLeft);
			buf.writeFloat(lastSaturation);
			for (int i = 0; i < TGPlayerInventory.NUMSLOTS; i++) {
				buf.writeItem(inv.get(i));
			}
		} else {
			buf.writeItem(inv.get(TGPlayerInventory.SLOT_FACE));
			buf.writeItem(inv.get(TGPlayerInventory.SLOT_BACK));
			buf.writeItem(inv.get(TGPlayerInventory.SLOT_HAND));
		}
	}

	@Override
	protected void handle(Player local_ply) {
		Entity ent = local_ply.level().getEntity(this.entityId);
		if (ent instanceof Player ply) {
			TGExtendedPlayer props = TGExtendedPlayer.get(ply);
			if (props != null) {
				if (this.allSlots) {
					props.radlevel = this.radlevel;
					props.lastSaturation = this.lastSaturation;
					props.foodleft = this.foodLeft;
				}
				props.enableNightVision = this.nightVisionEnabled;
				props.enableSafemode = this.safemodeEnabled;
				props.enableHovermode = this.hovermodeEnabled;
				props.enableStepAssist = this.stepAssist;
				props.showTGHudElements = this.showHUD;
				props.enableJetpack = this.enableJetpack;

				int max = this.allSlots ? this.inv.size() : 3;
				for (int i = 0; i < max; i++) {
					props.tg_inventory.inventory.set(i, this.inv.get(i));
				}
			}
		}
	}
}
