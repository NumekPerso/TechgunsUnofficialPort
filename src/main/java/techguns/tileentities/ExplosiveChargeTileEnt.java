package techguns.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import techguns.TGConfig;
import techguns.TGMachines;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.blocks.machines.BlockExplosiveCharge;
import techguns.gui.containers.ExplosiveChargeContainer;
import techguns.packets.PacketSpawnParticle;

/**
 * Mining charge: removes a cuboid of blocks (radius x radius x length) in the direction it faces when the fuse ends.
 * Blocks are broken as the owner (BreakEvent), so protected areas stay protected.
 */
public class ExplosiveChargeTileEnt extends BasicOwnedTileEnt {

	public ExplosiveChargeTileEnt(BlockPos pos, BlockState state) {
		this(TGMachines.EXPLOSIVE_CHARGE_TILE, pos, state);
	}

	protected ExplosiveChargeTileEnt(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, 0, false);
	}

	protected short maxBlastradius = 3;
	protected short minBlastradius = 3;
	protected short minBlastLength = 2;
	protected short maxBlastLength = 5;
	protected short minFuseTime = 60;
	protected short maxFuseTime = 200;

	protected short blastradius = 3;
	protected short blastlength = 3;

	protected short fusetime = 100;
	protected boolean armed = false;

	public SoundEvent getSoundPlant() {
		return TGSounds.TNT_PLANT;
	}

	public SoundEvent getTickSound() {
		return TGSounds.TNT_TICK;
	}

	public SoundEvent getInitSound() {
		return TGSounds.TNT_INIT;
	}

	protected int getBlastRadiusForCuboid() {
		return this.blastradius / 2;
	}

	protected Direction getOrientation() {
		BlockState state = this.level.getBlockState(worldPosition);
		if (state.hasProperty(BlockStateProperties.FACING)) {
			return state.getValue(BlockStateProperties.FACING);
		}
		return Direction.DOWN;
	}

	@Override
	public boolean canBeWrenchRotated() {
		return !this.armed;
	}

	@Override
	public boolean canBeWrenchDismantled() {
		return !this.armed;
	}

	@Override
	public void update() {
		if (this.armed) {
			this.fusetime--;

			if (!this.level.isClientSide && this.fusetime > 0 && (this.fusetime % 20 == 0)) {
				this.level.playSound((Player) null, this.worldPosition, getTickSound(), SoundSource.BLOCKS, 4.0F, 1.0f);
			}

			if (this.fusetime <= 0) {

				this.explode();

			}

		}
	}

	protected void explode() {

		BlockPos pos = this.getBlockPos();
		if (!this.level.isClientSide) {
			this.level.playSound((Player) null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F);

			//get the first slice of the explosion volume

			Direction facing = this.getOrientation();

			Direction side1;
			Direction side2;

			if (facing.getAxis() == Direction.Axis.X) {
				side1 = Direction.NORTH;
				side2 = Direction.UP;
			} else if (facing.getAxis() == Direction.Axis.Z) {
				side1 = Direction.EAST;
				side2 = Direction.UP;
			} else {
				side1 = Direction.NORTH;
				side2 = Direction.EAST;
			}

			net.minecraft.world.level.Level w = this.level;

			//remove self
			w.removeBlock(pos, false);

			int rad = this.getBlastRadiusForCuboid();
			for (BlockPos p : BlockPos.betweenClosed(pos.relative(side1, rad).relative(side2, rad), pos.relative(side1.getOpposite(), rad).relative(side2.getOpposite(), rad))) {
				explodeLine(w, p.immutable(), facing);
			}

		}

	}

	protected void explodeLine(net.minecraft.world.level.Level w, BlockPos p, Direction direction) {
		BlockPos.MutableBlockPos pos = p.mutable();
		int i = 0;
		boolean blocked = false;

		while (!blocked && i < this.blastlength) {
			pos.move(direction);
			blocked = explodeBlock(w, pos.immutable());
			i++;
		}
	}

	/**
	 * Returns true when explosion is blocked
	 */
	protected boolean explodeBlock(net.minecraft.world.level.Level w, BlockPos coords) {
		BlockState bs = w.getBlockState(coords);

		if (this.canBreakBlockHardness(bs.getDestroySpeed(w, coords))) {

			//post blockBreakEvent
			Player ply = this.getOwner() != null ? w.getPlayerByUUID(getOwner()) : null;
			if (ply != null) {
				final BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(w, coords, bs, ply);

				MinecraftForge.EVENT_BUS.post(breakEvent);

				if (!breakEvent.isCanceled()) {

					if (!bs.isAir()) {
						BlockEntity be = bs.hasBlockEntity() ? w.getBlockEntity(coords) : null;
						Block.dropResources(bs, w, coords, be);
						w.removeBlock(coords, false);

						if (w.random.nextFloat() > 0.5f) {
							TGPackets.network.sendToAllAround(new PacketSpawnParticle("MiningChargeBlockExplosion", coords.getX() + 0.5d, coords.getY() + 0.5d, coords.getZ() + 0.5d), TGPackets.targetPointAroundBlockPos(w.dimension(), coords, 50));
						}
					}

					return false;
				}
			}
		}
		return true;
	}

	protected boolean canBreakBlockHardness(float hardness) {
		return hardness >= 0 && hardness < TGConfig.explosiveChargeMaxBlockHardness;
	}

	@Override
	public void readClientDataFromNBT(CompoundTag tags) {
		super.readClientDataFromNBT(tags);
		blastradius = tags.getShort("blastradius");
		blastlength = tags.getShort("blastlength");
		fusetime = tags.getShort("fusetime");
		armed = tags.getBoolean("armed");
	}

	@Override
	public void writeClientDataToNBT(CompoundTag tags) {
		super.writeClientDataToNBT(tags);
		tags.putShort("blastradius", blastradius);
		tags.putShort("blastlength", blastlength);
		tags.putShort("fusetime", fusetime);
		tags.putBoolean("armed", armed);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("techguns.inventory.explosivecharge");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		if (this.armed) {
			return null;
		}
		return new ExplosiveChargeContainer(id, inv, this);
	}

	public short getBlastradius() {
		return blastradius;
	}

	public short getBlastlength() {
		return blastlength;
	}

	public short getFusetime() {
		return fusetime;
	}

	public boolean isArmed() {
		return armed;
	}

	public void setBlastradius(short blastradius) {
		this.blastradius = blastradius;
	}

	public void setBlastlength(short blastlength) {
		this.blastlength = blastlength;
	}

	public void setFusetime(short fusetime) {
		this.fusetime = fusetime;
	}

	@Override
	public void buttonClicked(int id, Player ply, String data) {
		if (!this.armed) {
			if ((id == 0) && (this.isUseableByPlayer(ply))) {
				this.armed = true;
				if (!this.level.isClientSide) {
					this.level.playSound(null, this.getBlockPos(), this.getInitSound(), SoundSource.BLOCKS, 4.0F, 1.0f);
					BlockState state = level.getBlockState(getBlockPos());
					if (state.hasProperty(BlockExplosiveCharge.ARMED)) {
						this.level.setBlock(getBlockPos(), state.setValue(BlockExplosiveCharge.ARMED, true), 3);
					}
					this.needUpdate();
				}
			} else if (id == 1) {
				if (this.blastradius < this.maxBlastradius) {
					this.blastradius++;
					this.needUpdate();
				}
			} else if (id == 2) {
				if (this.blastradius > this.minBlastradius) {
					this.blastradius--;
					this.needUpdate();
				}
			}

			else if (id == 3) {
				if (this.blastlength < this.maxBlastLength) {
					this.blastlength++;
					this.needUpdate();
				}
			}

			else if (id == 4) {
				if (this.blastlength > this.minBlastLength) {
					this.blastlength--;
					this.needUpdate();
				}
			}

			else if (id == 5) {
				if (this.fusetime < this.maxFuseTime) {
					this.fusetime += 20;
					this.needUpdate();
				}
			} else if (id == 6) {
				if (this.fusetime > this.minFuseTime) {
					this.fusetime -= 20;
					this.needUpdate();
				}
			}
		}
	}

}
