package techguns.client.render.entities.npcs;

import java.util.function.Function;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import techguns.client.models.npcs.ITGEntityModel;
import techguns.client.models.npcs.TGEntityModel;

/**
 * Renderer of the non humanoid NPCs with a 1.12 style model: 1.12 RenderAlienBug, RenderGhastling (RenderLiving)
 */
public class RenderTGEntityModel<T extends Mob> extends MobRenderer<T, TGEntityModel<T>> {

	protected final Function<T, ResourceLocation> texture;

	public RenderTGEntityModel(EntityRendererProvider.Context ctx, ITGEntityModel model, float shadowSize, Function<T, ResourceLocation> texture) {
		super(ctx, new TGEntityModel<>(model), shadowSize);
		this.texture = texture;
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return this.texture.apply(entity);
	}
}
