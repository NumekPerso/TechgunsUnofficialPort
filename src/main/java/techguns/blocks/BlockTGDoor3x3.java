package techguns.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import techguns.TGBlocks;
import techguns.TGSounds;
import techguns.tileentities.Door3x3TileEntity;

/**
 * 3x3 doors (tech door, hangar doors). The center block is the master (has the Door3x3TileEntity).
 * Changes from 1.12: the door type was stored in the tile entity (and read in getActualState), it is now a block state
 * property set on all 9 blocks. The door is placed by the ItemTGDoor3x3 items (one item per door type).
 * The configuration GUI (Door3x3Gui) is opened with sneak + wrench.
 */
public class BlockTGDoor3x3 extends Block implements EntityBlock, IGenericBlock {

	public static final BooleanProperty MASTER = BooleanProperty.create("master");
	public static final BooleanProperty ZPLANE = BooleanProperty.create("zplane");
	public static final EnumProperty<EnumDoorState> STATE = EnumProperty.<EnumDoorState>create("state", EnumDoorState.class);
	public static final EnumProperty<EnumDoorType> TYPE = EnumProperty.<EnumDoorType>create("type", EnumDoorType.class);

	protected static final double size = 6 / 16D;//0.3125D;
	protected static final double size2 = 4 / 16D;

	protected static final VoxelShape BB_X = Shapes.box(size, 0, 0, 1 - size, 1, 1);
	protected static final VoxelShape BB_Z = Shapes.box(0, 0, size, 1, 1, 1 - size);

	protected static final VoxelShape BB_X_openTop = Shapes.box(0, 1 - size2, size, 1, 1, 1 - size);
	protected static final VoxelShape BB_Z_openTop = Shapes.box(size, 1 - size2, 0, 1 - size, 1, 1);

	protected static final VoxelShape BB_X_openBottom = Shapes.box(0, 0, size, 1, size2, 1 - size);
	protected static final VoxelShape BB_Z_openBottom = Shapes.box(size, 0, 0, 1 - size, size2, 1);

	protected static final VoxelShape BB_Z_openLeft = Shapes.box(size, 0, 0, 1 - size, 1, size2);
	protected static final VoxelShape BB_Z_openRight = Shapes.box(size, 0, 1 - size2, 1 - size, 1, 1);
	protected static final VoxelShape BB_X_openLeft = Shapes.box(0, 0, size, size2, 1, 1 - size);
	protected static final VoxelShape BB_X_openRight = Shapes.box(1 - size2, 0, size, 1, 1, 1 - size);

	public static final int BLOCK_UPDATE_DELAY = 5;
	public static final int DOOR_OPEN_TIME = 1000;
	public static final int DOOR_AUTOCLOSE_DELAY = 5000;

	public static final int DOOR_OPEN_TICKS = DOOR_OPEN_TIME / 50;

	public static VoxelShape NO_COLLIDE = Shapes.empty();

	public BlockTGDoor3x3() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(6.0f).noOcclusion()
				.pushReaction(PushReaction.BLOCK).noLootTable());
		this.registerDefaultState(this.stateDefinition.any().setValue(MASTER, false).setValue(ZPLANE, false).setValue(STATE, EnumDoorState.CLOSED).setValue(TYPE, EnumDoorType.METAL));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(MASTER, ZPLANE, STATE, TYPE);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		if (state.getValue(MASTER)) {
			return new Door3x3TileEntity(pos, state);
		}
		return null;
	}

	protected static Vec3i[] pos_z = {
			new Vec3i(0, 1, 0),
			new Vec3i(0, -1, 0),
			new Vec3i(0, 0, -1),
			new Vec3i(0, 0, 1),
			new Vec3i(0, -1, -1),
			new Vec3i(0, -1, 1),
			new Vec3i(0, 1, -1),
			new Vec3i(0, 1, 1)
	};

	protected static Vec3i[] pos_x = {
			new Vec3i(0, 1, 0),
			new Vec3i(0, -1, 0),
			new Vec3i(-1, 0, 0),
			new Vec3i(1, 0, 0),
			new Vec3i(-1, -1, 0),
			new Vec3i(1, -1, 0),
			new Vec3i(-1, 1, 0),
			new Vec3i(1, 1, 0)
	};

	public BlockPos findMaster(BlockGetter w, BlockPos pos, BlockState state) {
		if (state.getValue(MASTER)) {
			return pos;
		}

		Vec3i[] offsets = pos_x;
		if (state.getValue(ZPLANE)) {
			offsets = pos_z;
		}

		for (int i = 0; i < offsets.length; i++) {
			BlockPos p = pos.offset(offsets[i]);
			BlockState s = w.getBlockState(p);
			if (s.getBlock() == this && s.getValue(MASTER)) {
				return p;
			}
		}
		return pos;
	}

	public boolean isStateOpen(BlockState state) {
		return state.getValue(STATE) == EnumDoorState.OPENED || state.getValue(STATE) == EnumDoorState.OPENING;
	}

	/**
	 * 1.12 getBoundingBox
	 */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter source, BlockPos pos, CollisionContext context) {
		if (!isStateOpen(state)) {
			return getBBforPlane(state);
		} else {
			BlockPos master = findMaster(source, pos, state);
			EnumDoorType type = state.getValue(TYPE);
			if (type == EnumDoorType.HANGAR_UP) {
				return getOpenBBforTypeHangarUp(state, pos, master);
			} else if (type == EnumDoorType.HANGAR_DOWN) {
				return getOpenBBforTypeHangarDown(state, pos, master);
			} else {
				return getOpenBBforTypeMetal(state, pos, master);
			}
		}
	}

	protected VoxelShape getOpenBBforTypeMetal(BlockState state, BlockPos pos, BlockPos master) {
		if (state.getValue(MASTER) || master.getY() >= pos.getY()) {
			if (!state.getValue(ZPLANE)) {
				if (pos.getX() < master.getX()) {
					return BB_X_openLeft;
				} else if (pos.getX() > master.getX()) {
					return BB_X_openRight;
				}
			} else {
				if (pos.getZ() < master.getZ()) {
					return BB_Z_openLeft;
				} else if (pos.getZ() > master.getZ()) {
					return BB_Z_openRight;
				}
			}
			return NO_COLLIDE;
		} else {
			if (!state.getValue(ZPLANE)) {
				return BB_X_openTop;
			} else {
				return BB_Z_openTop;
			}
		}
	}

	protected VoxelShape getOpenBBforTypeHangarUp(BlockState state, BlockPos pos, BlockPos master) {
		if (state.getValue(MASTER) || master.getY() >= pos.getY()) {
			return NO_COLLIDE;
		} else {
			if (!state.getValue(ZPLANE)) {
				return BB_X_openTop;
			} else {
				return BB_Z_openTop;
			}
		}
	}

	protected VoxelShape getOpenBBforTypeHangarDown(BlockState state, BlockPos pos, BlockPos master) {
		if (state.getValue(MASTER) || master.getY() <= pos.getY()) {
			return NO_COLLIDE;
		} else {
			if (!state.getValue(ZPLANE)) {
				return BB_X_openBottom;
			} else {
				return BB_Z_openBottom;
			}
		}
	}

	protected VoxelShape getBBforPlane(BlockState state) {
		if (state.getValue(ZPLANE)) {
			return BB_X;
		} else {
			return BB_Z;
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (isStateOpen(blockState)) {
			return Shapes.empty();
		} else {
			return getBBforPlane(blockState);
		}
	}

	protected SoundEvent getClosingSound() {
		return TGSounds.TECHDOOR_CLOSE;
	}

	protected SoundEvent getOpeningSound() {
		return TGSounds.TECHDOOR_OPEN;
	}

	protected SoundEvent getStateFinishedSound() {
		return TGSounds.TECHDOOR_STATE_FINISHED;
	}

	public EnumDoorState getNextOpenState(EnumDoorState current) {
		switch (current) {
		case CLOSED:
			return EnumDoorState.OPENING;
		case OPENED:
			return EnumDoorState.CLOSING;
		case OPENING:
			return EnumDoorState.OPENED;
		case CLOSING:
		default:
			return EnumDoorState.CLOSED;
		}
	}

	@Nullable
	public SoundEvent getSoundEvent(EnumDoorState nextState) {
		switch (nextState) {
		case CLOSING:
			return this.getClosingSound();
		case OPENING:
			return this.getOpeningSound();
		case CLOSED:
		case OPENED:
			return this.getStateFinishedSound();
		default:
			return null;
		}
	}

	public void toggleState(Level w, BlockPos masterPos) {
		toggleState(w, masterPos, true);
	}

	/**
	 * Only called on the server, the clients get the block updates and a PacketDoorStateChange for the animation timing
	 */
	public void toggleState(Level w, BlockPos masterPos, boolean checkNeighbours) {
		BlockState masterstate = w.getBlockState(masterPos);
		if (masterstate.getBlock() != this) return;
		EnumDoorState nextState = this.getNextOpenState(masterstate.getValue(STATE));

		Vec3i[] offsets = pos_x;
		if (masterstate.getValue(ZPLANE)) {
			offsets = pos_z;
		}

		for (int i = 0; i < offsets.length; i++) {
			BlockPos p = masterPos.offset(offsets[i]);
			this.setOpenedStateForBlock(w, p, nextState);
		}
		this.setOpenedStateForBlock(w, masterPos, nextState);

		SoundEvent sound = this.getSoundEvent(nextState);
		if (sound != null) {
			w.playSound(null, masterPos, sound, SoundSource.BLOCKS, (float) (0.9 + Math.random() * 0.2), (float) (0.9 + Math.random() * 0.2));
		}

		BlockEntity tile = w.getBlockEntity(masterPos);
		if (tile instanceof Door3x3TileEntity door) {
			if (!w.isClientSide) {
				door.changeStateServerSide();

				EnumDoorType type = masterstate.getValue(TYPE);
				if (checkNeighbours && (type == EnumDoorType.HANGAR_DOWN || type == EnumDoorType.HANGAR_UP)) {
					checkOpenNeighbours(w, masterPos, masterstate, door);
				}

				if (nextState == EnumDoorState.OPENING || nextState == EnumDoorState.CLOSING) {
					w.scheduleTick(masterPos, this, DOOR_OPEN_TICKS - 1);
				} else if (door.isAutoClose() && nextState == EnumDoorState.OPENED) {
					w.scheduleTick(masterPos, this, BLOCK_UPDATE_DELAY);
				}
				door.setLastStateChangeTime(System.currentTimeMillis());
			} else {
				door.setLastStateChangeTime(System.currentTimeMillis());
			}
		}
	}

	public void checkOpenNeighbours(Level w, BlockPos masterPos, BlockState masterstate, Door3x3TileEntity tile) {
		boolean zplane = masterstate.getValue(ZPLANE);
		//check sideways
		BlockPos pos = null;
		for (int i = 0; i < 2; i++) {
			for (int z = 3; z <= 9; z += 3) {
				pos = masterPos;
				if (zplane) {
					if (i == 0) {
						pos = pos.offset(0, 0, -z);
					} else {
						pos = pos.offset(0, 0, z);
					}
				} else {
					if (i == 0) {
						pos = pos.offset(-z, 0, 0);
					} else {
						pos = pos.offset(z, 0, 0);
					}
				}
				if (!checkBlockAndToggleState(w, pos, masterstate)) {
					break;
				}
			}
		}
		//check upper or lower
		int y;
		if (masterstate.getValue(TYPE) == EnumDoorType.HANGAR_UP) {
			y = -3;
		} else {
			y = 3;
		}
		BlockPos otherMaster = masterPos.offset(0, y, 0);
		if (checkBlockAndToggleState(w, otherMaster, masterstate)) {
			for (int i = 0; i < 2; i++) {
				for (int z = 3; z <= 9; z += 3) {
					pos = otherMaster;
					if (zplane) {
						if (i == 0) {
							pos = pos.offset(0, 0, -z);
						} else {
							pos = pos.offset(0, 0, z);
						}
					} else {
						if (i == 0) {
							pos = pos.offset(-z, 0, 0);
						} else {
							pos = pos.offset(z, 0, 0);
						}
					}
					if (!checkBlockAndToggleState(w, pos, masterstate)) {
						break;
					}
				}
			}
		}
	}

	protected boolean checkBlockAndToggleState(Level w, BlockPos pos, BlockState masterstate) {
		BlockState state = w.getBlockState(pos);
		if (isMatchingDoorType(masterstate, state)) {
			Block b = state.getBlock();
			if (b instanceof BlockTGDoor3x3 d) {
				d.toggleState(w, pos, false);
				return true;
			}
		}
		return false;
	}

	protected boolean isMatchingDoorType(BlockState master, BlockState other) {
		if (master == other) return true;
		EnumDoorType type = master.getValue(TYPE);
		if (type == EnumDoorType.HANGAR_DOWN || type == EnumDoorType.HANGAR_UP) {
			return master.setValue(TYPE, EnumDoorType.HANGAR_DOWN) == other || master.setValue(TYPE, EnumDoorType.HANGAR_UP) == other;
		} else {
			return false;
		}
	}

	public void setOpenedStateForBlock(Level w, BlockPos p, EnumDoorState doorstate) {
		BlockState state = w.getBlockState(p);
		if (state.getBlock() == this) {
			BlockState newstate = state.setValue(STATE, doorstate);
			w.setBlock(p, newstate, 3);
		}
	}

	public ItemStack getDoorItem(EnumDoorType type) {
		ItemTGDoor3x3 item = TGBlocks.DOOR3x3_ITEMS.get(type);
		return item != null ? new ItemStack(item) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		return getDoorItem(state.getValue(TYPE));
	}

	/**
	 * 1.12 onBlockHarvested, the door drops its item when broken by a player not in creative mode (no loot table)
	 */
	@Override
	public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
		if (!player.isCreative() && !worldIn.isClientSide) {
			ItemStack drop = getDoorItem(state.getValue(TYPE));
			if (!drop.isEmpty()) {
				ItemEntity ent = new ItemEntity(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, drop);
				ent.setDefaultPickUpDelay();
				worldIn.addFreshEntity(ent);
			}
		}
		super.playerWillDestroy(worldIn, pos, state, player);
	}

	public boolean canPlaceDoor(Level worldIn, BlockPos pos, net.minecraft.core.Direction lookdirection) {
		int xmin = 0;
		int xmax = 1;
		int zmin = 0;
		int zmax = 1;
		if (lookdirection == net.minecraft.core.Direction.NORTH || lookdirection == net.minecraft.core.Direction.SOUTH) {
			xmin = -1;
			xmax = 2;
		} else {
			zmin = -1;
			zmax = 2;
		}
		for (int x = xmin; x < xmax; x++) for (int y = 0; y < 3; y++) for (int z = zmin; z < zmax; z++) {
			BlockPos p = pos.offset(x, y, z);
			if (!worldIn.isInWorldBounds(p) || !worldIn.getBlockState(p).canBeReplaced()) {
				return false;
			}
		}
		return true;
	}

	protected void breakSlave(Level w, BlockPos p) {
		BlockState state = w.getBlockState(p);
		if (state.getBlock() == this) {
			w.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
		}
	}

	protected void checkBreakMaster(Level w, BlockPos p) {
		BlockState state = w.getBlockState(p);
		if (state.getBlock() == this && state.getValue(MASTER)) {
			w.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
		}
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return this.isStateOpen(state);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit) {
		if (worldIn.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		BlockPos master = this.findMaster(worldIn, pos, state);
		BlockEntity tile = worldIn.getBlockEntity(master);
		if (tile instanceof Door3x3TileEntity door) {
			if (door.isUseableByPlayer(playerIn)) {
				//sneaking with a wrench opens the door GUI
				if (playerIn.isShiftKeyDown() && playerIn.getMainHandItem().is(techguns.blocks.machines.BasicMachine.WRENCH_TAG)) {
					techguns.blocks.machines.BasicMachine.openGuiForPlayer(playerIn, door);
				} else if (door.isOpenWithRightClick()) {
					this.toggleState(worldIn, master);
				}
			}
		}
		return InteractionResult.CONSUME;
	}

	public boolean isPowered(Level w, BlockPos masterPos) {
		BlockState masterstate = w.getBlockState(masterPos);
		Vec3i[] offsets = pos_x;
		if (masterstate.getValue(ZPLANE)) {
			offsets = pos_z;
		}

		for (int i = 0; i < offsets.length; i++) {
			BlockPos p = masterPos.offset(offsets[i]);
			if (w.hasNeighborSignal(p)) {
				return true;
			}
		}
		return false;
	}

	public void updateRedstoneState(Level world, BlockPos masterPos) {
		BlockEntity tile = world.getBlockEntity(masterPos);
		if (tile instanceof Door3x3TileEntity door) {
			if (door.isRedstoneMode() && door.getRedstoneBehaviour() != 0) {
				BlockState masterstate = world.getBlockState(masterPos);
				boolean powered = this.isPowered(world, masterPos);
				if (door.getRedstoneBehaviour() == 1) {
					if ((powered && masterstate.getValue(STATE) == EnumDoorState.CLOSED) || (!powered && masterstate.getValue(STATE) == EnumDoorState.OPENED)) {
						this.toggleState(world, masterPos);
					}
				} else if (door.getRedstoneBehaviour() == 2) {
					if ((!powered && masterstate.getValue(STATE) == EnumDoorState.CLOSED) || (powered && masterstate.getValue(STATE) == EnumDoorState.OPENED)) {
						this.toggleState(world, masterPos);
					}
				}
			}
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!world.isClientSide && !state.getValue(MASTER)) {
			BlockPos masterpos = this.findMaster(world, pos, state);
			this.updateRedstoneState(world, masterpos);
		}
	}

	/**
	 * 1.12 breakBlock: breaking one part breaks the whole door
	 */
	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (state.getValue(MASTER)) {
				breakSlave(worldIn, pos.above());
				breakSlave(worldIn, pos.below());
				if (state.getValue(ZPLANE)) {
					breakSlave(worldIn, pos.north());
					breakSlave(worldIn, pos.north().above());
					breakSlave(worldIn, pos.north().below());
					breakSlave(worldIn, pos.south());
					breakSlave(worldIn, pos.south().above());
					breakSlave(worldIn, pos.south().below());
				} else {
					breakSlave(worldIn, pos.east());
					breakSlave(worldIn, pos.east().above());
					breakSlave(worldIn, pos.east().below());
					breakSlave(worldIn, pos.west());
					breakSlave(worldIn, pos.west().above());
					breakSlave(worldIn, pos.west().below());
				}
			} else {
				checkBreakMaster(worldIn, pos.above());
				checkBreakMaster(worldIn, pos.below());
				if (state.getValue(ZPLANE)) {
					checkBreakMaster(worldIn, pos.north());
					checkBreakMaster(worldIn, pos.north().above());
					checkBreakMaster(worldIn, pos.north().below());
					checkBreakMaster(worldIn, pos.south());
					checkBreakMaster(worldIn, pos.south().above());
					checkBreakMaster(worldIn, pos.south().below());
				} else {
					checkBreakMaster(worldIn, pos.east());
					checkBreakMaster(worldIn, pos.east().above());
					checkBreakMaster(worldIn, pos.east().below());
					checkBreakMaster(worldIn, pos.west());
					checkBreakMaster(worldIn, pos.west().above());
					checkBreakMaster(worldIn, pos.west().below());
				}
			}
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		if (rot == Rotation.COUNTERCLOCKWISE_90 || rot == Rotation.CLOCKWISE_90) {
			return state.setValue(ZPLANE, !state.getValue(ZPLANE));
		}
		return state;
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state;
	}

	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
		if ((state.getBlock() != this) || !state.getValue(MASTER)) return;
		BlockEntity tile = worldIn.getBlockEntity(pos);
		if (tile instanceof Door3x3TileEntity door) {
			boolean openstate = state.getValue(STATE) == EnumDoorState.OPENED;
			boolean closedstate = state.getValue(STATE) == EnumDoorState.CLOSED;

			if (!openstate && !closedstate) {
				worldIn.scheduleTick(pos, this, 1);
				if (System.currentTimeMillis() - door.getLastStateChangeTime() > DOOR_OPEN_TIME) {
					this.toggleState(worldIn, pos);
				}
			} else if (door.getDoormode() == 0) {
				if (door.isAutoClose() && openstate) {
					if (door.checkAutoCloseDelay()) {
						this.toggleState(worldIn, pos);
					}
					worldIn.scheduleTick(pos, this, BLOCK_UPDATE_DELAY);
				}
			} else if (door.getDoormode() == 2) {
				boolean open = checkOpenForNearPlayers(worldIn, door, pos);
				if (open && closedstate) {
					this.toggleState(worldIn, pos);
				} else if (!open && openstate && door.checkPlayerSensorAutoCloseDelay()) {
					this.toggleState(worldIn, pos);
				}
				worldIn.scheduleTick(pos, this, BLOCK_UPDATE_DELAY);
			}
		}
	}

	protected boolean checkOpenForNearPlayers(Level w, Door3x3TileEntity door, BlockPos masterPos) {
		AABB bb = new AABB(masterPos).inflate(2, 1, 2);
		List<Player> nearbyPlayers = w.getEntitiesOfClass(Player.class, bb);
		for (Player ply : nearbyPlayers) {
			if (door.isUseableByPlayer(ply)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Placed by the ItemTGDoor3x3 items
	 */
	@Override
	public BlockItem createItemBlock(String name) {
		return null;
	}
}
