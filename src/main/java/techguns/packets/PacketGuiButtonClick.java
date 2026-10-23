package techguns.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import techguns.tileentities.BasicInventoryTileEnt;

/**
 * Client -> server: a button of a machine GUI was clicked
 */
public class PacketGuiButtonClick extends TGPacket {
	private BlockPos pos;
	private int buttonId;

	private String data;

	public PacketGuiButtonClick(BasicInventoryTileEnt tileEnt, int buttonId) {
		this(tileEnt, buttonId, null);
	}

	public PacketGuiButtonClick(BasicInventoryTileEnt tileEnt, int buttonId, String data) {
		this.pos = tileEnt.getBlockPos();
		this.buttonId = buttonId;
		this.data = data;
	}

	public PacketGuiButtonClick(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.buttonId = buf.readInt();
		if (buf.readBoolean()) {
			this.data = buf.readUtf(32767);
		} else {
			this.data = null;
		}
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(buttonId);
		buf.writeBoolean(data != null);
		if (data != null) {
			buf.writeUtf(data, 32767);
		}
	}

	@Override
	protected void handle(Player ply) {
		if (!ply.level().isLoaded(this.pos)) {
			return;
		}
		BlockEntity tile = ply.level().getBlockEntity(this.pos);

		if (tile instanceof BasicInventoryTileEnt tileent) {
			if (tileent.isUseableByPlayer(ply)) {
				tileent.buttonClicked(this.buttonId, ply, this.data);
			}
		}
	}
}
