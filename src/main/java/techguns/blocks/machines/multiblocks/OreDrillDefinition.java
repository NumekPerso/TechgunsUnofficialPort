package techguns.blocks.machines.multiblocks;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGMachines;
import techguns.blocks.BlockOreCluster;
import techguns.blocks.machines.MultiBlockMachine;
import techguns.packets.PacketMultiBlockFormInvalidBlockMessage;
import techguns.tileentities.MultiBlockMachineTileEntMaster;
import techguns.tileentities.MultiBlockMachineTileEntSlave;
import techguns.tileentities.OreDrillTileEntMaster;

/**
 * Ore drill: controller, engines (radius x radius x n), rods up to an ore cluster, frame + scaffold around.
 * Types of slaves: 0 rod, 1 engine, 2 frame, 3 scaffold, 4 scaffold of the last row (becomes oredrill_scaffold_hidden).
 */
public class OreDrillDefinition extends MultiBlockMachineSchematic {

	protected static final Direction[][] DIRECTIONS_AROUND_AXIS = { /*X*/{ Direction.DOWN, Direction.NORTH, Direction.UP, Direction.SOUTH },
			/*Y*/{ Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST },
			/*Z*/{ Direction.DOWN, Direction.EAST, Direction.UP, Direction.WEST }
	};

	public static final int MAX_RADIUS = 3;
	public static final int MAX_SIZE = 16;

	public OreDrillDefinition() {
		super(OreDrillTileEntMaster.class);
	}

	@Override
	public boolean checkForm(Level w, Player player, BlockPos masterPos, Direction direction) {
		int dir = getDirection(w, player, masterPos);
		if (dir < 0) {
			sendErrorMSG(w, masterPos, player, PacketMultiBlockFormInvalidBlockMessage.MSG_TYPE_ORE_DRILL_CONTROLLER_ALONE);
			return false;
		}

		Direction drill_direction = Direction.from3DDataValue(dir);

		int[] length = this.checkLength(w, masterPos, drill_direction);

		int engines = length[0];
		int rods = length[1];

		if (engines >= 0 && rods > 0 && (rods + engines <= MAX_SIZE)) {

			if (engines == 0 && rods == 1) {
				return true;
			}

			//check distribution of engines and rods
			int rad = this.checkWidth(w, masterPos, drill_direction);

			//Have radius, check all blocks
			if (this.checkDrillSize(rods, engines, rad)) {
				//check engine block
				if (FabricatorDefinition.allBlocksMatchUnformed(w, player, getEngineBlocks(w, masterPos, drill_direction, rad, engines), TGMachines.OREDRILL_ENGINE, false)) {
					//engines OK, check outer frame
					if (FabricatorDefinition.allBlocksMatchUnformed(w, player, getFrameBlocks(w, masterPos, drill_direction, rad + 1, engines + rods, true, true), TGMachines.OREDRILL_FRAME, false)) {

						if (FabricatorDefinition.allBlocksMatchUnformed(w, player, getFrameBlocks(w, masterPos, drill_direction, rad + 1, engines + rods, false, true), TGMachines.OREDRILL_SCAFFOLD, false)) {

							if (allBlocksAir(w, player, getAirBlocks(w, masterPos.relative(drill_direction, engines), drill_direction, rad, rods - 1))) {
								return true;
							} else {
								//Air Block Error
								sendErrorMSG(w, masterPos, player, PacketMultiBlockFormInvalidBlockMessage.MSG_TYPE_ORE_DRILL_AIR_ERROR);
							}

						} else {
							//Scaffold Error
							sendErrorMSG(w, masterPos, player, PacketMultiBlockFormInvalidBlockMessage.MSG_TYPE_ORE_DRILL_SCAFFOLD_ERROR);
						}

					} else {
						//Frame Error
						sendErrorMSG(w, masterPos, player, PacketMultiBlockFormInvalidBlockMessage.MSG_TYPE_ORE_DRILL_FRAME_ERROR);
					}

				} else {
					//Engine Error
					sendErrorMSG(w, masterPos, player, PacketMultiBlockFormInvalidBlockMessage.MSG_TYPE_ORE_DRILL_ENGINE_ERROR);
				}
			} else {
				sendErrorMSG(w, masterPos, player, PacketMultiBlockFormInvalidBlockMessage.MSG_TYPE_ROD_SIZE);
			}
			return false;

		} else {

			if (engines < 0) {
				sendErrorPing(w, masterPos.relative(drill_direction, -engines + 1), player, 0, false);
			} else if (rods < 0) {
				sendErrorPing(w, masterPos.relative(drill_direction, (-rods) + Math.abs(engines) + 1), player, 0, false);
			}
			return false;
		}
	}

	protected static boolean allBlocksAir(Level w, Player player, ArrayList<BlockPos> positions) {
		boolean valid = true;
		for (BlockPos p : positions) {
			if (!w.getBlockState(p).isAir()) {
				sendErrorPing(w, p, player, 0, false);
				valid = false;
			}
		}
		return valid;
	}

	public boolean checkDrillSize(int rods, int engines, int radius) {
		return rods >= (radius * 2 + 1) && rods >= engines && engines > radius;
	}

	public static Direction getSide1(Direction facing) {
		switch (facing.getAxis()) {
		case X:
			return Direction.DOWN;
		case Y:
			return Direction.NORTH;
		case Z:
			return Direction.DOWN;
		default:
			return Direction.DOWN; //WOn't happen
		}
	}

	public static Direction getSide2(Direction facing) {
		switch (facing.getAxis()) {
		case X:
			return Direction.NORTH;
		case Y:
			return Direction.EAST;
		case Z:
			return Direction.EAST;
		default:
			return Direction.DOWN; //WOn't happen
		}
	}

	protected ArrayList<BlockPos> getEngineBlocks(Level w, BlockPos masterPos, Direction direction, int radius, int len) {
		ArrayList<BlockPos> positions = new ArrayList<BlockPos>((radius * 2 + 1) * (radius * 2 + 1) * len);

		for (int r1 = -radius; r1 <= radius; r1++) for (int r2 = -radius; r2 <= radius; r2++) for (int h = 0; h < len; h++) {
			BlockPos p = masterPos.relative(direction, h + 1).relative(getSide1(direction), r1).relative(getSide2(direction), r2);
			positions.add(p);
		}
		return positions;
	}

	protected ArrayList<BlockPos> getAirBlocks(Level w, BlockPos startPos, Direction direction, int radius, int len) {
		ArrayList<BlockPos> positions = new ArrayList<BlockPos>();

		for (int r1 = -radius; r1 <= radius; r1++) for (int r2 = -radius; r2 <= radius; r2++) for (int h = 0; h < len; h++) {

			if (!(r1 == 0 && r2 == 0)) {
				BlockPos p = startPos.relative(direction, h + 1).relative(getSide1(direction), r1).relative(getSide2(direction), r2);
				positions.add(p);
			}
		}
		return positions;
	}

	protected ArrayList<BlockPos> getFrameBlocks(Level w, BlockPos masterPos, Direction direction, int radius, int len, boolean outer, boolean lastRow) {
		ArrayList<BlockPos> positions = new ArrayList<BlockPos>();

		for (int r1 = -radius; r1 <= radius; r1++) for (int r2 = -radius; r2 <= radius; r2++) for (int h = 0; h < len; h++) {

			if (outer && isFramePos(r1, r2, h, radius, len)) {
				BlockPos p = masterPos.relative(direction, h + 1).relative(getSide1(direction), r1).relative(getSide2(direction), r2);
				positions.add(p);
			} else if (!outer && isScaffoldPos(r1, r2, h, radius, len)) {
				if (!lastRow || h != len - 1) {
					BlockPos p = masterPos.relative(direction, h + 1).relative(getSide1(direction), r1).relative(getSide2(direction), r2);
					positions.add(p);
				}
			}
		}
		return positions;
	}

	protected ArrayList<BlockPos> getFinalScaffoldRow(Level w, BlockPos masterPos, Direction direction, int radius, int len) {
		ArrayList<BlockPos> positions = new ArrayList<BlockPos>((radius * 2 + 1) * (radius * 2 + 1) - 1);
		for (int r1 = -radius; r1 <= radius; r1++) for (int r2 = -radius; r2 <= radius; r2++) {
			if (!(r1 == 0 && r2 == 0)) {
				BlockPos p = masterPos.relative(direction, len).relative(getSide1(direction), r1).relative(getSide2(direction), r2);
				positions.add(p);
			}
		}
		return positions;
	}

	protected boolean isFramePos(int r1, int r2, int h, int radius, int len) {
		int minc = 0;
		if (Math.abs(r1) == radius) minc++;
		if (Math.abs(r2) == radius) minc++;
		if (h == 0 || h == len - 1) minc++;
		return minc >= 2;
	}

	protected boolean isScaffoldPos(int r1, int r2, int h, int radius, int len) {
		if (h == 0) return false;
		int minc = 0;
		if (Math.abs(r1) == radius) minc++;
		if (Math.abs(r2) == radius) minc++;
		if (h == len - 1) {
			minc++;
			return minc == 1 && !(r1 == 0 && r2 == 0);
		}
		return minc == 1;
	}

	@Override
	public boolean form(Level w, Player player, BlockPos masterPos, Direction direction) {
		BlockEntity tile = w.getBlockEntity(masterPos);
		if (tile instanceof OreDrillTileEntMaster) {

			OreDrillTileEntMaster master = (OreDrillTileEntMaster) tile;

			int dir = getDirection(w, player, masterPos);
			if (dir < 0) return false;

			Direction drill_direction = Direction.from3DDataValue(dir);

			int[] length = this.checkLength(w, masterPos, drill_direction);

			int engines = length[0];
			int rods = length[1];

			if (engines >= 0 && rods > 0) {

				if (engines == 0 && rods == 1) {
					master.formOreDrill(direction, engines, rods, 0, drill_direction);
					linkSlave(w, player, masterPos.relative(drill_direction, 1), 0, masterPos);
					return true;
				}

				//check radius
				int rad = this.checkWidth(w, masterPos, drill_direction);

				master.formOreDrill(direction, engines, rods, rad, drill_direction);

				for (int i = engines; i < engines + rods + 1; i++) {
					linkSlave(w, player, masterPos.relative(drill_direction, i), 0, masterPos);
				}

				this.getEngineBlocks(w, masterPos, drill_direction, rad, engines).forEach(p -> linkSlave(w, player, p, 1, masterPos));

				this.getFrameBlocks(w, masterPos, drill_direction, rad + 1, engines + rods, true, true).forEach(p -> linkSlave(w, player, p, 2, masterPos));
				this.getFrameBlocks(w, masterPos, drill_direction, rad + 1, engines + rods, false, false).forEach(p -> linkSlave(w, player, p, 3, masterPos));
				this.getFinalScaffoldRow(w, masterPos, drill_direction, rad, engines + rods).forEach(p -> linkSlave(w, player, p, 4, masterPos));

			}
		}
		return false;
	}

	/**
	 * Type 4: the scaffold block is replaced by the hidden scaffold block (new block entity) before it is linked.
	 * 1.12 changed the MACHINE_TYPE property and kept the tile entity.
	 */
	@Override
	protected void linkSlave(Level w, Player p, BlockPos pos, int type, BlockPos masterPos) {
		if (type == 4 && !w.isClientSide) {
			BlockState bs = w.getBlockState(pos);
			if (bs.is(TGMachines.OREDRILL_SCAFFOLD)) {
				w.setBlock(pos, TGMachines.OREDRILL_SCAFFOLD_HIDDEN.defaultBlockState().setValue(MultiBlockMachine.FORMED, true), 3);
				BlockEntity tile = w.getBlockEntity(pos);
				if (tile instanceof MultiBlockMachineTileEntSlave slave) {
					slave.form(masterPos, (byte) type);
					slave.needUpdate();
				}
				return;
			}
		}
		super.linkSlave(w, p, pos, type, masterPos);
	}

	@Override
	protected void updateBlockStateUnform(Level w, BlockPos pos, BlockState bs, int type) {
		if (bs.is(TGMachines.OREDRILL_SCAFFOLD_HIDDEN)) {
			w.setBlock(pos, TGMachines.OREDRILL_SCAFFOLD.defaultBlockState().setValue(MultiBlockMachine.FORMED, false), 3);
		} else {
			super.updateBlockStateUnform(w, pos, bs, type);
		}
	}

	@Override
	public void unform(Level w, MultiBlockMachineTileEntMaster master) {
		if (master instanceof OreDrillTileEntMaster) {
			OreDrillTileEntMaster masterTile = (OreDrillTileEntMaster) master;

			Direction drill_direction = masterTile.getDrill_direction();

			int engines = masterTile.getEngines();
			int rods = masterTile.getRods();

			BlockPos masterPos = masterTile.getBlockPos();

			if (drill_direction == null) {
				master.unform();
				return;
			}

			if (engines == 0 && rods == 1) {
				unlinkSlave(w, masterPos.relative(drill_direction, 1));
				master.unform();
				return;
			}

			//check radius
			int rad = masterTile.getRadius();

			for (int i = engines; i < engines + rods + 1; i++) {
				unlinkSlave(w, masterPos.relative(drill_direction, i));
			}

			this.getEngineBlocks(w, masterPos, drill_direction, rad, engines).forEach(p -> unlinkSlave(w, p));

			this.getFrameBlocks(w, masterPos, drill_direction, rad + 1, engines + rods, true, true).forEach(p -> unlinkSlave(w, p));
			this.getFrameBlocks(w, masterPos, drill_direction, rad + 1, engines + rods, false, false).forEach(p -> unlinkSlave(w, p));
			this.getFinalScaffoldRow(w, masterPos, drill_direction, rad, engines + rods).forEach(p -> unlinkSlave(w, p));

			master.unform();
		}
	}

	@Override
	public boolean canFormFromSide(Direction side) {
		return true;
	}

	protected int getDirection(Level w, Player ply, BlockPos startPos) {
		int dir = -1;
		for (Direction f : Direction.values()) {
			if (checkBlock(w, startPos.relative(f))) {
				if (dir == -1) {
					dir = f.get3DDataValue();
				} else {
					sendErrorPing(w, startPos.relative(f, 1), ply, 0, false);
					return -1;
				}
			}
		}
		return dir;
	}

	protected int checkWidth(Level w, BlockPos p, Direction dir) {
		BlockPos enginePos = p.relative(dir, 1);
		int rad = 0;
		for (Direction facing : DIRECTIONS_AROUND_AXIS[dir.getAxis().ordinal()]) {
			int result = countEngines(w, enginePos, facing);
			if (result > rad) {
				rad = result;
			}
		}
		return rad;
	}

	protected int countEngines(Level w, BlockPos p, Direction dir) {
		int count = 0;
		while (isDrillType(w, p.relative(dir, count + 1), TGMachines.OREDRILL_ENGINE) && count < MAX_RADIUS) {
			count++;
		}
		return count;
	}

	/**
	 * Return the length of the drills and rods as int[] with 2 entries, negative values = errors
	 * [engines,rods]
	 * negative rods -> failure after |rods| rod blocks
	 */
	protected int[] checkLength(Level w, BlockPos p, Direction dir) {
		int engines = 0;
		int rods = 0;
		if (isDrillType(w, p.relative(dir, 1), TGMachines.OREDRILL_ROD)) {
			//Check for mini drill with Controller + rod
			rods++;
			if (isOreCluster(w, p.relative(dir, 2))) {
				return new int[] { 0, 1 };
			} else {
				return new int[] { -1, -1 };
			}

		} else if (isDrillType(w, p.relative(dir, 1), TGMachines.OREDRILL_ENGINE)) {
			engines++;
			while (isDrillType(w, p.relative(dir, engines + 1), TGMachines.OREDRILL_ENGINE) && engines < MAX_SIZE) {
				engines++;
			}
			if (isDrillType(w, p.relative(dir, engines + rods + 1), TGMachines.OREDRILL_ROD)) {
				rods++;
				while (isDrillType(w, p.relative(dir, engines + rods + 1), TGMachines.OREDRILL_ROD) && rods < MAX_SIZE) {
					rods++;
				}
				if (isOreCluster(w, p.relative(dir, engines + rods + 1))) {
					return new int[] { engines, rods };
				} else {
					return new int[] { engines, -rods };
				}

			} else {
				return new int[] { -engines, -1 };
			}
		} else {
			return new int[] { -1, -1 };
		}

	}

	public static boolean isOreCluster(Level w, BlockPos p) {
		BlockState bs = w.getBlockState(p);
		return bs.getBlock() instanceof BlockOreCluster;
	}

	protected boolean isDrillType(Level w, BlockPos p, Block type) {
		BlockState bs = w.getBlockState(p);
		return bs.is(type) && !bs.getValue(MultiBlockMachine.FORMED);
	}

	protected boolean checkBlock(Level w, BlockPos p) {
		return isDrillType(w, p, TGMachines.OREDRILL_ROD) || isDrillType(w, p, TGMachines.OREDRILL_ENGINE);
	}
}
