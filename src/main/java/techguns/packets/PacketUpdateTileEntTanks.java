package techguns.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import techguns.tileentities.operation.ITileEntityFluidTanks;

/**
 * Syncs the fluid tanks of a machine (ITileEntityFluidTanks) to the client (1.12 PacketUpdateTileEntTanks)
 */
public class PacketUpdateTileEntTanks extends TGPacket {

	protected BlockPos pos;
	protected CompoundTag tankTags;

	public PacketUpdateTileEntTanks(ITileEntityFluidTanks tile, BlockPos pos) {
		this.tankTags = new CompoundTag();
		tile.saveTanksToNBT(tankTags);
		this.pos = pos;
	}

	public PacketUpdateTileEntTanks(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.tankTags = buf.readNbt();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeNbt(tankTags);
	}

	@Override
	protected void handle(Player ply) {
		if (this.tankTags != null) {
			BlockEntity tile = ply.level().getBlockEntity(this.pos);
			if (tile instanceof ITileEntityFluidTanks tanks) {
				tanks.loadTanksFromNBT(this.tankTags);
			}
		}
	}
}
