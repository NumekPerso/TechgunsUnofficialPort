package techguns.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Server -> client: status message when a multiblock can't be formed
 */
public class PacketMultiBlockFormInvalidBlockMessage extends TGPacket {

	public static final int MSG_TYPE_ORE_DRILL_FRAME_ERROR = 2;
	public static final int MSG_TYPE_ORE_DRILL_SCAFFOLD_ERROR = 3;
	public static final int MSG_TYPE_ORE_DRILL_ENGINE_ERROR = 1;
	public static final int MSG_TYPE_ORE_DRILL_AIR_ERROR = 4;
	public static final int MSG_TYPE_ORE_DRILL_ROD_PLACEMENT_ERROR = 5;
	public static final int MSG_TYPE_ORE_DRILL_CONTROLLER_PLACEMENT_ERROR = 6;
	public static final int MSG_TYPE_ORE_DRILL_CONTROLLER_ALONE = 7;
	public static final int MSG_TYPE_MULTIBLOCK_ERROR = 8;
	public static final int MSG_TYPE_ROD_SIZE = 9;

	protected BlockPos pos;
	protected short type;

	public PacketMultiBlockFormInvalidBlockMessage(BlockPos pos, int type) {
		super();
		this.pos = pos;
		this.type = (short) type;
	}

	public PacketMultiBlockFormInvalidBlockMessage(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.type = buf.readShort();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeShort(type);
	}

	@Override
	protected void handle(Player player) {
		BlockPos p = this.pos;
		switch (this.type) {
		case MSG_TYPE_ORE_DRILL_ENGINE_ERROR:
			player.displayClientMessage(Component.translatable("techguns.multiblock.oredrill.invalid.engines"), true);
			break;
		case MSG_TYPE_ORE_DRILL_FRAME_ERROR:
			player.displayClientMessage(Component.translatable("techguns.multiblock.oredrill.invalid.frame"), true);
			break;
		case MSG_TYPE_ORE_DRILL_SCAFFOLD_ERROR:
			player.displayClientMessage(Component.translatable("techguns.multiblock.oredrill.invalid.scaffold"), true);
			break;
		case MSG_TYPE_ORE_DRILL_AIR_ERROR:
			player.displayClientMessage(Component.translatable("techguns.multiblock.oredrill.invalid.air"), true);
			break;
		case MSG_TYPE_ORE_DRILL_ROD_PLACEMENT_ERROR:
			player.displayClientMessage(Component.translatable("techguns.msg.error.alreadyrod"), true);
			break;
		case MSG_TYPE_ORE_DRILL_CONTROLLER_PLACEMENT_ERROR:
			player.displayClientMessage(Component.translatable("techguns.msg.error.oredrill.controllernext"), true);
			break;
		case MSG_TYPE_ORE_DRILL_CONTROLLER_ALONE:
			player.displayClientMessage(Component.translatable("techguns.msg.error.oredrill.controlleralone"), true);
			break;
		case MSG_TYPE_MULTIBLOCK_ERROR:
			player.displayClientMessage(Component.translatable("techguns.msg.error.multiblockinvalid"), true);
			break;
		case MSG_TYPE_ROD_SIZE:
			player.displayClientMessage(Component.translatable("techguns.msg.error.oredrill.rodsize"), true);
			break;
		default:
			player.displayClientMessage(Component.translatable("techguns.multiblock.invalidblock").append(": " + p.getX() + ", " + p.getY() + ", " + p.getZ()), false);
		}
	}

}
