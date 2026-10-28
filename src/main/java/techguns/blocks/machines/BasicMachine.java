package techguns.blocks.machines;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.network.NetworkHooks;
import techguns.Techguns;
import techguns.api.machines.IMachineType;
import techguns.blocks.machines.multiblocks.MultiBlockRegister;
import techguns.tileentities.BasicInventoryTileEnt;
import techguns.tileentities.BasicOwnedTileEnt;
import techguns.tileentities.BasicRedstoneTileEnt;
import techguns.tileentities.BasicTGTileEntity;
import techguns.tileentities.MultiBlockMachineTileEntMaster;
import techguns.tileentities.MultiBlockMachineTileEntSlave;

/**
 * A machine block (1.12 BasicMachine: 16 machine types per block, rotation saved in the tile entity, TESR or json model).
 * In 1.20 each machine type is its own block (registry name = lowercase machine type name), created by TGMachines with the
 * BlockEntityType of its tile entity.
 *
 * @param <T> Enum of all Machine Types
 */
public class BasicMachine<T extends Enum<T> & IMachineType> extends BaseEntityBlock {

	/**
	 * Items in this tag act as wrench (1.12: tool class "wrench"): sneak + use dismantles the machine (keeps its content), use rotates it
	 */
	public static final TagKey<Item> WRENCH_TAG = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "tools/wrench"));

	/**
	 * Side of the block that was clicked when placing (1.12 onBlockPlacedByExtended sideHit), set by MachineItemBlock
	 */
	protected static final ThreadLocal<Direction> PLACING_SIDE = new ThreadLocal<>();

	protected final T machineType;
	protected final Supplier<? extends BlockEntityType<?>> tileType;
	protected final ResourceLocation registryName;

	public BasicMachine(T machineType, Supplier<? extends BlockEntityType<?>> tileType) {
		this(machineType, tileType, defaultProperties(machineType));
	}

	public BasicMachine(T machineType, Supplier<? extends BlockEntityType<?>> tileType, BlockBehaviour.Properties properties) {
		super(properties);
		this.machineType = machineType;
		this.tileType = tileType;
		this.registryName = new ResourceLocation(Techguns.MODID, machineType.getName());
	}

	/**
	 * 1.12: Material.IRON, hardness 4, metal sound (sound from machine type), not opaque if not a full cube
	 */
	public static BlockBehaviour.Properties defaultProperties(IMachineType type) {
		BlockBehaviour.Properties p = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0f).requiresCorrectToolForDrops().sound(type.getSoundType());
		if (!type.isFullCube()) {
			p = p.noOcclusion();
		}
		return p;
	}

	/**
	 * 1.12 createItemBlock: creates the item of this machine block (added to TGItems.ITEMLIST), null for blocks without item
	 */
	@Nullable
	public net.minecraft.world.item.BlockItem createItemBlock() {
		return new MachineItemBlock(this);
	}

	public ResourceLocation getTGRegistryName() {
		return registryName;
	}

	public T getMachineType() {
		return machineType;
	}

	public BlockEntityType<?> getTileType() {
		return tileType.get();
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return this.tileType.get().create(pos, state);
	}

	@Override
	@Nullable
	public <E extends BlockEntity> BlockEntityTicker<E> getTicker(Level level, BlockState state, BlockEntityType<E> type) {
		if (type != this.tileType.get()) {
			return null;
		}
		return (lvl, pos, st, be) -> ((BasicTGTileEntity) be).update();
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return this.machineType.getRenderType();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return this.machineType.getBoundingBox();
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity tile = worldIn.getBlockEntity(pos);
			if (tile instanceof BasicInventoryTileEnt) {
				((BasicInventoryTileEnt) tile).onBlockBreak();
			} else if (tile instanceof MultiBlockMachineTileEntSlave) {
				((MultiBlockMachineTileEntSlave) tile).onBlockBreak();
			}
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockEntity tile = worldIn.getBlockEntity(pos);
		if (tile instanceof BasicRedstoneTileEnt) {
			((BasicRedstoneTileEnt) tile).onNeighborBlockChange();
		}
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		if (!oldState.is(state.getBlock())) {
			BlockEntity tile = level.getBlockEntity(pos);
			if (tile instanceof BasicRedstoneTileEnt) {
				((BasicRedstoneTileEnt) tile).onNeighborBlockChange();
			}
		}
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, net.minecraft.world.level.SignalGetter level, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		Direction facing = hit.getDirection();
		BlockEntity tile = world.getBlockEntity(pos);
		if (!world.isClientSide && tile instanceof BasicInventoryTileEnt) {
			BasicInventoryTileEnt tileent = (BasicInventoryTileEnt) tile;

			if (tileent.isUseableByPlayer(player)) {

				if (tile instanceof MultiBlockMachineTileEntMaster) {
					MultiBlockMachineTileEntMaster master = (MultiBlockMachineTileEntMaster) tile;
					if (!master.isFormed() && !player.isShiftKeyDown()) {
						if (MultiBlockRegister.canFormFromSide(tileent, facing)) {
							if (MultiBlockRegister.canForm(master, player, facing)) {
								if (MultiBlockRegister.form(tileent, player, facing)) {
									master.needUpdate();
								}
							}
							return InteractionResult.SUCCESS;
						}
					}
				}

				ItemStack helditem = player.getItemInHand(hand);
				if (!helditem.isEmpty() && helditem.is(WRENCH_TAG)) {

					if (player.isShiftKeyDown() && tileent.canBeWrenchDismantled()) {

						CompoundTag tileEntTags = new CompoundTag();
						tileent.writeNBTforDismantling(tileEntTags);
						ItemStack item = new ItemStack(this, 1);
						item.getOrCreateTag().put("TileEntityData", tileEntTags);
						tileent.emptyContent();
						world.removeBlock(pos, false);
						world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, item));

					} else if (tileent.canBeWrenchRotated()) {
						if (tileent.hasRotation()) {
							tileent.rotateTile(facing);
						} else {
							this.rotateBlock(world, pos, Direction.UP);
						}
					}

				} else if (!helditem.isEmpty() && hasBucketInteraction(state) && helditem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {

					IFluidHandlerItem fluidhandler = helditem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElseThrow(IllegalStateException::new);

					boolean interacted = tileent.onFluidContainerInteract(player, hand, fluidhandler, helditem);

					if (interacted) {
						world.playSound(null, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
						return InteractionResult.SUCCESS;
					} else {
						openGuiForPlayer(player, tileent);
					}

				} else {
					openGuiForPlayer(player, tileent);
				}

			} else {
				player.displayClientMessage(Component.translatable("techguns.container.security.denied"), true);
			}

		} else if (tile instanceof MultiBlockMachineTileEntSlave) {

			MultiBlockMachineTileEntSlave slave = (MultiBlockMachineTileEntSlave) tile;
			if (slave.hasMaster()) {
				if (!world.isClientSide) {
					BlockEntity master = world.getBlockEntity(slave.getMasterPos());
					if (master instanceof MultiBlockMachineTileEntMaster) {
						MultiBlockMachineTileEntMaster mastertile = (MultiBlockMachineTileEntMaster) master;
						if (mastertile.isUseableByPlayer(player)) {
							openGuiForPlayer(player, mastertile);
						} else {
							player.displayClientMessage(Component.translatable("techguns.container.security.denied"), true);
						}
					}
				}

			} else {
				return InteractionResult.PASS;
			}
		}
		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	/**
	 * Spectators open the menu provider of the block with the vanilla openMenu (no block position for the client menu,
	 * the client crashed), the Techguns menus are only opened by use().
	 */
	@Override
	@Nullable
	public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return null;
	}

	/**
	 * 1.12 TechgunsGuiHandler.openGuiForPlayer: opens the menu of the tile (BasicInventoryTileEnt.createMenu), the client
	 * menu gets the block position. Nothing opens if createMenu returns null.
	 */
	public static void openGuiForPlayer(Player player, BasicInventoryTileEnt tile) {
		if (player instanceof ServerPlayer sply) {
			NetworkHooks.openScreen(sply, tile, tile.getBlockPos());
		}
	}

	protected boolean hasBucketInteraction(BlockState state) {
		//1.20: also the formed reaction chamber controller (1.12 could only be filled with pipes from other mods)
		return this.machineType == EnumMachineType.CHEM_LAB || this.machineType == EnumMultiBlockMachineType.REACTIONCHAMBER_CONTROLLER;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, pos, state, placer, stack);
		Direction side = PLACING_SIDE.get();
		this.onBlockPlacedByExtended(world, pos, state, placer, stack, side != null ? side : Direction.UP);
	}

	public void onBlockPlacedByExtended(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, Direction sideHit) {

		BlockEntity tile = world.getBlockEntity(pos);

		if (placer instanceof Player && tile instanceof BasicOwnedTileEnt) {
			((BasicOwnedTileEnt) tile).setOwner((Player) placer);
		}

		if (tile instanceof BasicInventoryTileEnt) {
			BasicInventoryTileEnt invtile = (BasicInventoryTileEnt) tile;
			if (invtile.hasRotation() && placer != null) {

				int dir = Mth.floor((double) (placer.getYRot() * 4.0F / 360.0F) + 0.5D) & 3;
				invtile.rotation = (byte) (dir % 4);

			}

			if (stack.getTag() != null && stack.getTag().contains("TileEntityData")) {
				invtile.readNBTfromStackTag(stack.getTag().getCompound("TileEntityData"));
			}
			if (!world.isClientSide) {
				invtile.needUpdate();
			}
		}
		if (tile instanceof techguns.tileentities.TurretTileEnt) {
			techguns.tileentities.TurretTileEnt turret = (techguns.tileentities.TurretTileEnt) tile;
			if (sideHit == Direction.DOWN) {
				turret.setFacing(Direction.DOWN);
			} else {
				turret.setFacing(Direction.UP);
			}

			if (!turret.turretDeath) {
				turret.spawnTurret(world, pos);
			}
		}
	}

	/**
	 * Wrench rotation (1.12 Block.rotateBlock), only around the vertical axis
	 */
	public boolean rotateBlock(Level world, BlockPos pos, Direction axis) {

		if (axis == Direction.DOWN || axis == Direction.UP) {
			BlockEntity tile = world.getBlockEntity(pos);
			if (tile instanceof BasicInventoryTileEnt) {
				BasicInventoryTileEnt invtile = (BasicInventoryTileEnt) tile;
				if (invtile.hasRotation()) {
					invtile.rotateTile();
					return true;
				}
			}

		}
		return false;
	}

	public static void setPlacingSide(@Nullable Direction side) {
		if (side == null) {
			PLACING_SIDE.remove();
		} else {
			PLACING_SIDE.set(side);
		}
	}
}
