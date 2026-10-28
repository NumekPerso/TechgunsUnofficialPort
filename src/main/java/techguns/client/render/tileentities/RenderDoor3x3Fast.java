package techguns.client.render.tileentities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.data.ModelData;
import techguns.Techguns;
import techguns.blocks.BlockTGDoor3x3;
import techguns.blocks.EnumDoorState;
import techguns.tileentities.Door3x3TileEntity;

/**
 * Renders the moving parts of the 3x3 doors while they open / close (the frame is the block model of the master).
 * 1.12 baked the models with a 90 degree rotation for doors in the z plane, done with the pose stack now.
 */
public class RenderDoor3x3Fast implements BlockEntityRenderer<Door3x3TileEntity> {

	protected static final ResourceLocation door_left_loc = new ResourceLocation(Techguns.MODID, "block/techdoor3x3_left");
	protected static final ResourceLocation door_right_loc = new ResourceLocation(Techguns.MODID, "block/techdoor3x3_right");
	protected static final ResourceLocation door_left_loc_nether = new ResourceLocation(Techguns.MODID, "block/techdoor3x3_left_nether");
	protected static final ResourceLocation door_right_loc_nether = new ResourceLocation(Techguns.MODID, "block/techdoor3x3_right_nether");

	protected static final ResourceLocation hangar_up_mid_loc = new ResourceLocation(Techguns.MODID, "block/hangar_door_mid1j");
	protected static final ResourceLocation hangar_up_mid2_loc = new ResourceLocation(Techguns.MODID, "block/hangar_door_mid2j");
	protected static final ResourceLocation hangar_up_mid3_loc = new ResourceLocation(Techguns.MODID, "block/hangar_door_mid3j");
	protected static final ResourceLocation hangar_up_lower_loc = new ResourceLocation(Techguns.MODID, "block/hangar_door_lower");
	protected static final ResourceLocation hangar_down_upper_loc = new ResourceLocation(Techguns.MODID, "block/hangar_door_down_upper");

	public static void registerModels(ModelEvent.RegisterAdditional event) {
		event.register(door_left_loc);
		event.register(door_right_loc);
		event.register(door_left_loc_nether);
		event.register(door_right_loc_nether);
		event.register(hangar_up_mid_loc);
		event.register(hangar_up_mid2_loc);
		event.register(hangar_up_mid3_loc);
		event.register(hangar_up_lower_loc);
		event.register(hangar_down_upper_loc);
	}

	public RenderDoor3x3Fast(BlockEntityRendererProvider.Context context) {
	}

	protected static BakedModel model(ResourceLocation loc) {
		return Minecraft.getInstance().getModelManager().getModel(loc);
	}

	/**
	 * Helper keeping the 1.12 "buffer.setTranslation + renderModel" structure
	 */
	protected static class Ctx {
		PoseStack poseStack;
		VertexConsumer buffer;
		BlockState state;
		int light;
		int overlay;
		boolean zplane;

		void render(BakedModel model, double tx, double ty, double tz) {
			poseStack.pushPose();
			poseStack.translate(tx, ty, tz);
			if (zplane) {
				poseStack.translate(0, 0, 1);
				poseStack.mulPose(Axis.YP.rotationDegrees(90));
			}
			//ModelBlockRenderer.renderModel draws without the directional face shading of the chunk renderer: the moving
			//parts were brighter than the static door, apply the shade of each face (rotated with the door)
			ClientLevel level = Minecraft.getInstance().level;
			PoseStack.Pose pose = poseStack.last();
			RandomSource rand = RandomSource.create();
			for (Direction side : SIDES) {
				rand.setSeed(42L);
				for (BakedQuad quad : model.getQuads(state, side, rand, ModelData.EMPTY, RenderType.cutout())) {
					float shade = 1f;
					if (quad.isShade() && level != null) {
						Direction dir = quad.getDirection();
						if (zplane && dir.getAxis().isHorizontal()) {
							dir = dir.getCounterClockWise();
						}
						shade = level.getShade(dir, true);
					}
					buffer.putBulkData(pose, quad, shade, shade, shade, light, overlay);
				}
			}
			poseStack.popPose();
		}

		private static final Direction[] SIDES = { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null };
	}

	@Override
	public void render(Door3x3TileEntity te, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockState state = te.getBlockState();
		if (!(state.getBlock() instanceof BlockTGDoor3x3)) return;
		if (state.getValue(BlockTGDoor3x3.STATE) == EnumDoorState.OPENED || state.getValue(BlockTGDoor3x3.STATE) == EnumDoorState.CLOSED) {
			return;
		}

		long timestamp = te.getLastStateChangeTime();
		long diff = System.currentTimeMillis() - timestamp;
		float prog = 0;
		if (diff < 1000) {
			prog = diff / 1000.0f;
		}

		Ctx ctx = new Ctx();
		ctx.poseStack = poseStack;
		ctx.buffer = bufferSource.getBuffer(RenderType.cutout());
		ctx.state = state;
		ctx.light = packedLight;
		ctx.overlay = packedOverlay;
		ctx.zplane = state.getValue(BlockTGDoor3x3.ZPLANE);

		switch (te.getDoorType()) {
		case 0:
			this.renderDoor0(ctx, prog, model(door_left_loc), model(door_right_loc));
			break;
		case 1:
			this.renderDoor1(ctx, prog);
			break;
		case 2:
			this.renderDoor2(ctx, prog);
			break;
		case 3:
			this.renderDoor0(ctx, prog, model(door_left_loc_nether), model(door_right_loc_nether));
			break;
		}
	}

	protected void renderDoor0(Ctx ctx, float prog, BakedModel L, BakedModel R) {
		int dx = 1;
		int dz = 0;
		if (ctx.zplane) {
			dx = 0;
			dz = 1;
		}
		double distance = 1.125d; //18 pixel
		if (prog == 0.0f) {
			if (ctx.state.getValue(BlockTGDoor3x3.STATE) == EnumDoorState.OPENING) {
				ctx.render(L, -((double) dx * distance), 0, ((double) dz * distance));
				ctx.render(R, ((double) dx * distance), 0, -((double) dz * distance));
			} else {
				ctx.render(L, 0, 0, 0);
				ctx.render(R, 0, 0, 0);
			}
		} else {
			if (ctx.state.getValue(BlockTGDoor3x3.STATE) == EnumDoorState.CLOSING) {
				prog = 1.0f - prog;
			}
			ctx.render(L, -((double) dx * distance * prog), 0, ((double) dz * distance * prog));
			ctx.render(R, ((double) dx * distance * prog), 0, -((double) dz * distance * prog));
		}
	}

	/**
	 * HangarDoor up
	 */
	protected void renderDoor1(Ctx ctx, float prog) {
		BakedModel model_segment = model(hangar_up_mid_loc);
		BakedModel model_segment2 = model(hangar_up_mid2_loc);
		BakedModel model_segment3 = model(hangar_up_mid3_loc);
		BakedModel model_lower = model(hangar_up_lower_loc);

		if (prog == 0.0f) {
			if (ctx.state.getValue(BlockTGDoor3x3.STATE) == EnumDoorState.OPENING) {
				//Render opened door
				ctx.render(model_lower, 0, 3d, 0);
			} else {
				//render closed door
				ctx.render(model_lower, 0, 0, 0);
				ctx.render(model_segment, 0, 0, 0);
				ctx.render(model_segment2, 0, 0, 0);
				ctx.render(model_segment3, 0, 0, 0);
			}
		} else {
			if (ctx.state.getValue(BlockTGDoor3x3.STATE) == EnumDoorState.CLOSING) {
				prog = 1.0f - prog;
			}
			double dist = prog * 3d;
			ctx.render(model_lower, 0, dist, 0);
			ctx.render(model_segment, 0, dist, 0);
			if (dist <= 2d) {
				ctx.render(model_segment2, 0, dist, 0);
			}
			if (dist <= 1d) {
				ctx.render(model_segment3, 0, dist, 0);
			}
		}
	}

	/**
	 * HangarDoor down
	 */
	protected void renderDoor2(Ctx ctx, float prog) {
		BakedModel model_segment = model(hangar_up_mid_loc);
		BakedModel model_segment2 = model(hangar_up_mid2_loc);
		BakedModel model_segment3 = model(hangar_up_mid3_loc);
		BakedModel model_upper = model(hangar_down_upper_loc);

		if (prog == 0.0f) {
			if (ctx.state.getValue(BlockTGDoor3x3.STATE) == EnumDoorState.OPENING) {
				//Render opened door
				ctx.render(model_upper, 0, -3d, 0);
			} else {
				//render closed door
				ctx.render(model_upper, 0, 0, 0);
				ctx.render(model_segment, 0, 0, 0);
				ctx.render(model_segment2, 0, 0, 0);
				ctx.render(model_segment3, 0, 0, 0);
			}
		} else {
			if (ctx.state.getValue(BlockTGDoor3x3.STATE) == EnumDoorState.CLOSING) {
				prog = 1.0f - prog;
			}
			double dist = prog * 3d;
			ctx.render(model_upper, 0, -dist, 0);
			ctx.render(model_segment3, 0, -dist, 0);
			if (dist <= 2d) {
				ctx.render(model_segment2, 0, -dist, 0);
			}
			if (dist <= 1d) {
				ctx.render(model_segment, 0, -dist, 0);
			}
		}
	}

	@Override
	public boolean shouldRenderOffScreen(Door3x3TileEntity te) {
		return true;
	}
}
