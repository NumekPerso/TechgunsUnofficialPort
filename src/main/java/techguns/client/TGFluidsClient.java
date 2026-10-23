package techguns.client;

import java.util.function.Consumer;

import org.joml.Vector3f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import techguns.TGFluids;
import techguns.Techguns;

/**
 * Client side of the Techguns fluids (1.12 TGFluids.initModels / registerFluidModelsForFluidBlock):
 * fluid textures (IClientFluidTypeExtensions), render layers, helpers for the fluid gauges of the machine GUIs.
 */
public class TGFluidsClient {

	public static final ResourceLocation ACID_STILL = new ResourceLocation(Techguns.MODID, "blocks/acid_still");
	public static final ResourceLocation ACID_FLOW = new ResourceLocation(Techguns.MODID, "blocks/acid_flow");

	/**
	 * Called by the acid FluidType constructor on the client
	 */
	public static void initializeAcidClient(Consumer<IClientFluidTypeExtensions> consumer) {
		consumer.accept(new IClientFluidTypeExtensions() {
			@Override
			public ResourceLocation getStillTexture() {
				return ACID_STILL;
			}

			@Override
			public ResourceLocation getFlowingTexture() {
				return ACID_FLOW;
			}

			@Override
			public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
				return new Vector3f(0.35f, 0.75f, 0.1f);
			}
		});
	}

	/**
	 * Called from TGMachinesClient (client setup, enqueued work)
	 */
	public static void onClientSetup() {
		ItemBlockRenderTypes.setRenderLayer(TGFluids.ACID, RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(TGFluids.ACID_FLOWING, RenderType.translucent());
	}

	/**
	 * Still texture of a fluid stack in the block atlas (1.12 fluid.getStill())
	 */
	public static TextureAtlasSprite getStillSprite(FluidStack stack) {
		if (stack == null || stack.isEmpty()) {
			return null;
		}
		ResourceLocation tex = IClientFluidTypeExtensions.of(stack.getFluid()).getStillTexture(stack);
		if (tex == null) {
			return null;
		}
		return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(tex);
	}

	/**
	 * Flowing texture of a fluid stack in the block atlas (1.12 fluid.getFlowing())
	 */
	public static TextureAtlasSprite getFlowingSprite(FluidStack stack) {
		if (stack == null || stack.isEmpty()) {
			return null;
		}
		ResourceLocation tex = IClientFluidTypeExtensions.of(stack.getFluid()).getFlowingTexture(stack);
		if (tex == null) {
			return null;
		}
		return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(tex);
	}

	/**
	 * ARGB tint of a fluid stack (1.12 fluid.getColor(stack)), water is blue
	 */
	public static int getTintColor(FluidStack stack) {
		if (stack == null || stack.isEmpty()) {
			return 0xFFFFFFFF;
		}
		return IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor(stack);
	}
}
