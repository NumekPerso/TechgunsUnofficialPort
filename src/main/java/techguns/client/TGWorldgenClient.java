package techguns.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import techguns.TGMachines;
import techguns.gui.DungeonGeneratorGui;
import techguns.gui.DungeonScannerGui;
import techguns.tileentities.DungeonGeneratorTileEnt;
import techguns.tileentities.DungeonScannerTileEnt;
import techguns.world.dungeon.TemplateSegment;
import techguns.world.dungeon.TemplateSegment.SegmentType;

/**
 * Client setup of lot K (world generation): dungeon scanner/generator screens and their "ghost" outline renderers
 * (1.12 dungeon scanner/generator TESR)
 */
public class TGWorldgenClient {

	public static void registerModBusListeners(IEventBus modBus) {
		modBus.addListener(TGWorldgenClient::onClientSetup);
		modBus.addListener(TGWorldgenClient::registerRenderers);
	}

	private static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(TGMachines.DUNGEON_SCANNER_CONTAINER, DungeonScannerGui::new);
			MenuScreens.register(TGMachines.DUNGEON_GENERATOR_CONTAINER, DungeonGeneratorGui::new);
		});
	}

	private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(TGMachines.DUNGEON_SCANNER_TILE, ctx -> new ScannerRenderer());
		event.registerBlockEntityRenderer(TGMachines.DUNGEON_GENERATOR_TILE, ctx -> new GeneratorRenderer());
	}

	public static class ScannerRenderer implements BlockEntityRenderer<DungeonScannerTileEnt> {
		@Override
		public void render(DungeonScannerTileEnt tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
			if (!tile.showGhost) {
				return;
			}
			VertexConsumer lines = buffer.getBuffer(RenderType.lines());
			int s = DungeonScannerTileEnt.SPACING;
			for (SegmentType type : TemplateSegment.templateSegments.keySet()) {
				TemplateSegment seg = TemplateSegment.templateSegments.get(type);
				double x = s + seg.col * (tile.sizeXZ + s);
				double z = s + seg.row * (tile.sizeXZ + s);
				boolean on = tile.template_filter.getOrDefault(type, true);
				float g = on ? 1.0f : 0.3f;
				LevelRenderer.renderLineBox(poseStack, lines, x, 0, z, x + tile.sizeXZ, tile.sizeY * seg.sizeY, z + tile.sizeXZ, 0.2f, g, 0.2f, 1.0f);
			}
		}

		@Override
		public boolean shouldRenderOffScreen(DungeonScannerTileEnt tile) {
			return true;
		}

		@Override
		public int getViewDistance() {
			return 128;
		}
	}

	public static class GeneratorRenderer implements BlockEntityRenderer<DungeonGeneratorTileEnt> {
		@Override
		public void render(DungeonGeneratorTileEnt tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
			if (!tile.showGhost) {
				return;
			}
			VertexConsumer lines = buffer.getBuffer(RenderType.lines());
			LevelRenderer.renderLineBox(poseStack, lines, 1, 0, 1, tile.sizeX + 1, tile.sizeY, tile.sizeZ + 1, 1.0f, 0.5f, 0.0f, 1.0f);
		}

		@Override
		public boolean shouldRenderOffScreen(DungeonGeneratorTileEnt tile) {
			return true;
		}

		@Override
		public int getViewDistance() {
			return 128;
		}
	}
}
