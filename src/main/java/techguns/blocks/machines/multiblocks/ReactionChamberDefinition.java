package techguns.blocks.machines.multiblocks;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import techguns.TGMachines;
import techguns.blocks.machines.MultiBlockMachine;
import techguns.packets.PacketMultiBlockFormInvalidBlockMessage;
import techguns.tileentities.MultiBlockMachineTileEntMaster;
import techguns.tileentities.ReactionChamberTileEntMaster;

/**
 * Reaction chamber: 3x3 base of housings with the controller in the middle of the front row, 2 layers of 3x3 glass,
 * 3x3 housing on top (1.12 ReactionChamberDefinition)
 */
public class ReactionChamberDefinition extends MultiBlockMachineSchematic {

	public static final float BOTTOM_DIFF = 0.25f;
	public static final float MIDDLE_DIFF = 0.45f;
	public static final float HEIGHT_DIFF = 0.35f;

	//assumes Multiblock FACING SOUTH
	public static HashMap<SlavePos, AABB> boundingBoxes = new HashMap<>();
	static {
		//SOUTH = +Z;
		//NORTH = -Z;
		//WEST= -X;
		//EAST= +X

		//add 4 corners in bottom row
		boundingBoxes.put(new SlavePos(-1, 0, -1), new AABB(BOTTOM_DIFF, 0, BOTTOM_DIFF, 1, 1, 1));
		boundingBoxes.put(new SlavePos(1, 0, -1), new AABB(0, 0, BOTTOM_DIFF, 1 - BOTTOM_DIFF, 1, 1));
		boundingBoxes.put(new SlavePos(-1, 0, 1), new AABB(BOTTOM_DIFF, 0, 0, 1, 1, 1 - BOTTOM_DIFF));
		boundingBoxes.put(new SlavePos(1, 0, 1), new AABB(0, 0, 0, 1 - BOTTOM_DIFF, 1, 1 - BOTTOM_DIFF));

		//glass Row 1 corners
		boundingBoxes.put(new SlavePos(-1, 1, -1), new AABB(MIDDLE_DIFF, 0, MIDDLE_DIFF, 1, 1, 1));
		boundingBoxes.put(new SlavePos(1, 1, -1), new AABB(0, 0, MIDDLE_DIFF, 1 - MIDDLE_DIFF, 1, 1));
		boundingBoxes.put(new SlavePos(-1, 1, 1), new AABB(MIDDLE_DIFF, 0, 0, 1, 1, 1 - MIDDLE_DIFF));
		boundingBoxes.put(new SlavePos(1, 1, 1), new AABB(0, 0, 0, 1 - MIDDLE_DIFF, 1, 1 - MIDDLE_DIFF));

		//glass row 1 sides
		boundingBoxes.put(new SlavePos(-1, 1, 0), new AABB(MIDDLE_DIFF, 0, 0, 1, 1, 1));
		boundingBoxes.put(new SlavePos(1, 1, 0), new AABB(0, 0, 0, 1 - MIDDLE_DIFF, 1, 1));
		boundingBoxes.put(new SlavePos(0, 1, -1), new AABB(0, 0, MIDDLE_DIFF, 1, 1, 1));
		boundingBoxes.put(new SlavePos(0, 1, 1), new AABB(0, 0, 0, 1, 1, 1 - MIDDLE_DIFF));

		//glass Row 2 corners
		boundingBoxes.put(new SlavePos(-1, 2, -1), new AABB(MIDDLE_DIFF, 0, MIDDLE_DIFF, 1, 1, 1));
		boundingBoxes.put(new SlavePos(1, 2, -1), new AABB(0, 0, MIDDLE_DIFF, 1 - MIDDLE_DIFF, 1, 1));
		boundingBoxes.put(new SlavePos(-1, 2, 1), new AABB(MIDDLE_DIFF, 0, 0, 1, 1, 1 - MIDDLE_DIFF));
		boundingBoxes.put(new SlavePos(1, 2, 1), new AABB(0, 0, 0, 1 - MIDDLE_DIFF, 1, 1 - MIDDLE_DIFF));

		//glass row 2 sides
		boundingBoxes.put(new SlavePos(-1, 2, 0), new AABB(MIDDLE_DIFF, 0, 0, 1, 1, 1));
		boundingBoxes.put(new SlavePos(1, 2, 0), new AABB(0, 0, 0, 1 - MIDDLE_DIFF, 1, 1));
		boundingBoxes.put(new SlavePos(0, 2, -1), new AABB(0, 0, MIDDLE_DIFF, 1, 1, 1));
		boundingBoxes.put(new SlavePos(0, 2, 1), new AABB(0, 0, 0, 1, 1, 1 - MIDDLE_DIFF));

		//top row corners
		boundingBoxes.put(new SlavePos(-1, 3, -1), new AABB(MIDDLE_DIFF, 0, MIDDLE_DIFF, 1, 1 - HEIGHT_DIFF, 1));
		boundingBoxes.put(new SlavePos(1, 3, -1), new AABB(0, 0, MIDDLE_DIFF, 1 - MIDDLE_DIFF, 1 - HEIGHT_DIFF, 1));
		boundingBoxes.put(new SlavePos(-1, 3, 1), new AABB(MIDDLE_DIFF, 0, 0, 1, 1 - HEIGHT_DIFF, 1 - MIDDLE_DIFF));
		boundingBoxes.put(new SlavePos(1, 3, 1), new AABB(0, 0, 0, 1 - MIDDLE_DIFF, 1 - HEIGHT_DIFF, 1 - MIDDLE_DIFF));

		//top row sides
		boundingBoxes.put(new SlavePos(-1, 3, 0), new AABB(MIDDLE_DIFF, 0, 0, 1, 1 - HEIGHT_DIFF, 1));
		boundingBoxes.put(new SlavePos(1, 3, 0), new AABB(0, 0, 0, 1 - MIDDLE_DIFF, 1 - HEIGHT_DIFF, 1));
		boundingBoxes.put(new SlavePos(0, 3, -1), new AABB(0, 0, MIDDLE_DIFF, 1, 1 - HEIGHT_DIFF, 1));
		boundingBoxes.put(new SlavePos(0, 3, 1), new AABB(0, 0, 0, 1, 1 - HEIGHT_DIFF, 1 - MIDDLE_DIFF));

	}

	public ReactionChamberDefinition() {
		super(ReactionChamberTileEntMaster.class);
	}

	protected ArrayList<BlockPos> getGlassBlocks(BlockPos masterPos, Direction direction) {
		ArrayList<BlockPos> positions = new ArrayList<>();
		BlockPos pos = masterPos.immutable();

		Direction left = direction.getClockWise();
		BlockPos pos1 = pos.relative(left).above();

		BlockPos pos2 = pos.relative(left.getOpposite()).relative(direction, 2).above(2);

		BlockPos.betweenClosed(pos1, pos2).forEach(p -> positions.add(p.immutable()));
		return positions;
	}

	protected ArrayList<BlockPos> getEnergyConnector(BlockPos masterPos, Direction direction) {
		ArrayList<BlockPos> positions = new ArrayList<>();
		BlockPos pos = masterPos.immutable();
		positions.add(pos.relative(direction).above(3));
		return positions;
	}

	protected ArrayList<BlockPos> getSideConnectors(BlockPos masterPos, Direction direction) {
		ArrayList<BlockPos> positions = new ArrayList<>();
		BlockPos center = masterPos.immutable().relative(direction);
		Direction left = direction.getClockWise();

		positions.add(center.relative(direction));
		positions.add(center.relative(left));
		positions.add(center.relative(left.getOpposite()));
		return positions;
	}

	protected ArrayList<BlockPos> getNoConnectorHousings(BlockPos masterPos, Direction direction) {
		ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
		BlockPos pos = masterPos.immutable();

		Direction left = direction.getClockWise();
		BlockPos leftp = pos.relative(left);
		BlockPos rightp = pos.relative(left.getOpposite());
		BlockPos centerp = pos.relative(direction);

		positions.add(leftp);
		positions.add(rightp);
		positions.add(leftp.relative(direction, 2));
		positions.add(rightp.relative(direction, 2));
		positions.add(centerp);

		positions.add(leftp.above(3));
		positions.add(rightp.above(3));
		positions.add(leftp.relative(direction, 2).above(3));
		positions.add(rightp.relative(direction, 2).above(3));

		BlockPos centeru = centerp.above(3);
		positions.add(centeru.relative(direction));
		positions.add(centeru.relative(direction.getOpposite()));
		positions.add(centeru.relative(left));
		positions.add(centeru.relative(left.getOpposite()));

		return positions;
	}

	protected ArrayList<BlockPos> getHousingBlocks(BlockPos masterPos, Direction direction) {
		ArrayList<BlockPos> positions = this.getNoConnectorHousings(masterPos, direction);
		positions.addAll(this.getEnergyConnector(masterPos, direction));
		positions.addAll(this.getSideConnectors(masterPos, direction));
		return positions;
	}

	@Override
	public boolean checkForm(Level w, Player player, BlockPos masterPos, Direction direction) {
		Direction dir = direction.getOpposite();

		BlockState unformedHousing = TGMachines.REACTIONCHAMBER_HOUSING.defaultBlockState().setValue(MultiBlockMachine.FORMED, false);
		BlockState unformedGlass = TGMachines.REACTIONCHAMBER_GLASS.defaultBlockState().setValue(MultiBlockMachine.FORMED, false);

		if (this.canFormFromSide(dir) &&
				allBlocksMatch(w, player, this.getGlassBlocks(masterPos, dir), unformedGlass) &&
				allBlocksMatch(w, player, this.getHousingBlocks(masterPos, dir), unformedHousing)) {
			return true;
		} else {
			sendErrorMSG(w, masterPos, player, PacketMultiBlockFormInvalidBlockMessage.MSG_TYPE_MULTIBLOCK_ERROR);
			return false;
		}
	}

	@Override
	public boolean form(Level w, Player player, BlockPos masterPos, Direction direction) {
		Direction dir = direction.getOpposite();
		BlockEntity tile = w.getBlockEntity(masterPos);
		if (tile != null && tile instanceof MultiBlockMachineTileEntMaster) {

			MultiBlockMachineTileEntMaster master = (MultiBlockMachineTileEntMaster) tile;
			master.form(dir);

			ArrayList<BlockPos> housings_EnergyConnector = this.getEnergyConnector(masterPos, dir);
			ArrayList<BlockPos> housings_SideConnectors = this.getSideConnectors(masterPos, dir);
			ArrayList<BlockPos> housings_noConnection = this.getNoConnectorHousings(masterPos, dir);

			ArrayList<BlockPos> glassBlocks = this.getGlassBlocks(masterPos, dir);

			glassBlocks.forEach(b -> linkSlave(w, player, b, 1, masterPos));
			housings_noConnection.forEach(b -> linkSlave(w, player, b, 1, masterPos));

			housings_SideConnectors.forEach(b -> linkSlave(w, player, b, 2, masterPos));
			housings_EnergyConnector.forEach(b -> linkSlave(w, player, b, 3, masterPos));

		}
		return false;
	}

	@Override
	public void unform(Level w, MultiBlockMachineTileEntMaster master) {
		BlockPos masterPos = master.getBlockPos();
		Direction direction = master.getMultiblockDirection();

		master.unform();

		if (direction == null) {
			return;
		}

		ArrayList<BlockPos> glassBlocks = this.getGlassBlocks(masterPos, direction);
		ArrayList<BlockPos> housingBlocks = this.getHousingBlocks(masterPos, direction);

		glassBlocks.forEach(b -> unlinkSlave(w, b));
		housingBlocks.forEach(b -> unlinkSlave(w, b));
	}

	@Override
	public boolean canFormFromSide(Direction side) {
		return side != null && side != Direction.UP && side != Direction.DOWN;
	}

}
