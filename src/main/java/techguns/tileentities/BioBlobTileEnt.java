package techguns.tileentities;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGBlocks;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.blocks.BlockBioblob;
import techguns.damagesystem.TGDamageSource;
import techguns.damagesystem.TGExplosion;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.packets.PacketSpawnParticle;

/**
 * Tile entity of the bio blob: shrinks over time, grows (and finally explodes) when hit by bio gun projectiles.
 * The size is mirrored in the BlockBioblob.SIZE block state property (1.12 read it in getActualState).
 * 1.12 implemented ITGTileEntSecurity (owner only, security always 0), only the owner is kept.
 */
public class BioBlobTileEnt extends BlockEntity {

	protected UUID owner;

	protected static final int NUMTICKS = 60;

	protected int ticks = NUMTICKS;

	public byte size = 1;

	public BioBlobTileEnt(BlockPos pos, BlockState state) {
		super(TGBlocks.BIOBLOB_TILEENT, pos, state);
	}

	public void setOwner(Player ply) {
		UUID id = ply.getGameProfile().getId();
		if (id != null) {
			this.owner = id;
		}
	}

	public boolean isOwnedByPlayer(Player ply) {
		if (this.owner == null) {
			return false;
		}
		return this.owner.equals(ply.getGameProfile().getId());
	}

	@Nullable
	public UUID getOwner() {
		return owner;
	}

	public byte getSecurity() {
		return 0;
	}

	/**
	 * Return the bioblob size [0-2]
	 */
	public int getBlobSize() {
		return size - 1;
	}

	/**
	 * Server tick
	 */
	public void update() {
		if (!this.level.isClientSide) {
			this.ticks--;
			if (ticks <= 0) {
				if (this.size > 1) {
					size--;
					this.ticks = NUMTICKS;
					this.needUpdate();
				} else {
					//remove blob
					this.level.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());
				}
			}
		}
	}

	/**
	 * Updates the size block state (synced to the clients by the block update)
	 */
	public void needUpdate() {
		if (!this.level.isClientSide) {
			BlockState state = this.getBlockState();
			if (state.getBlock() instanceof BlockBioblob) {
				int s = Math.max(0, Math.min(2, this.getBlobSize()));
				if (state.getValue(BlockBioblob.SIZE) != s) {
					this.level.setBlock(this.worldPosition, state.setValue(BlockBioblob.SIZE, s), 3);
				}
			}
			this.setChanged();
		}
	}

	@Override
	public void load(CompoundTag tags) {
		super.load(tags);
		ticks = tags.getInt("BlobTicks");
		size = tags.getByte("size");
		if (tags.hasUUID("owner")) {
			this.owner = tags.getUUID("owner");
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tags) {
		super.saveAdditional(tags);
		tags.putInt("BlobTicks", ticks);
		tags.putByte("size", size);
		if (this.owner != null) {
			tags.putUUID("owner", this.owner);
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public void hitBlob(int power, LivingEntity attacker) {
		if (!this.level.isClientSide) {
			this.size += power;
			if (size > 3) {
				size = 3;
				//kaboom
				float radius = 3.0f;

				TGDamageSource dmgSrc = TGDamageSource.causePoisonDamage(null, attacker, DeathType.BIO);
				dmgSrc.goreChance = 1.0f;
				dmgSrc.armorPenetration = 0.35f;

				TGExplosion explosion = new TGExplosion(level, attacker, null, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, 30, 15, radius, radius * 1.5f, 0.0f);
				explosion.setDmgSrc(dmgSrc);

				BlockPos pos = this.worldPosition;
				this.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

				explosion.doExplosion(false);

				this.level.playSound((Player) null, pos, TGSounds.DEATH_BIO, SoundSource.BLOCKS, 4.0F, 1.0F);

				TGPackets.network.sendToAllAround(new PacketSpawnParticle("bioblobExplosion", pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
						TGPackets.targetPointAroundBlockPos(this.level.dimension(), pos, 50.0D));
				return;
			}
			this.ticks = NUMTICKS;
			this.needUpdate();
		}
	}
}
