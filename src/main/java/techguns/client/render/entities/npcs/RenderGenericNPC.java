package techguns.client.render.entities.npcs;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import techguns.Techguns;
import techguns.client.models.armor.TGModelBiped;
import techguns.client.models.npcs.ModelGenericNPC;
import techguns.client.models.npcs.NPCHumanoidModel;

/**
 * Renderer of the humanoid Techguns NPCs (1.12 RenderGenericNPC = RenderBiped and the NPC specific Render* subclasses, which only
 * differ by texture, model and layers): custom head, elytra, held item (optionally translated, 1.12 LayerHeldItemTranslateGun),
 * armor (TG armor uses its 3D models through the armor item client extensions).
 */
public class RenderGenericNPC<T extends Mob> extends HumanoidMobRenderer<T, NPCHumanoidModel<T>> {

	protected final Function<T, ResourceLocation> texture;

	/**
	 * @param armorModels armor model factories (inner = legs, outer = other slots), null = no armor layer (1.12 cyber demon)
	 */
	public RenderGenericNPC(EntityRendererProvider.Context ctx, TGModelBiped model, float shadowSize, Function<T, ResourceLocation> texture, boolean translateGun,
			@Nullable Function<Float, TGModelBiped> armorModels) {
		super(ctx, new NPCHumanoidModel<>(model), shadowSize);
		this.texture = texture;
		if (translateGun) {
			this.layers.removeIf(l -> l instanceof ItemInHandLayer);
			this.addLayer(new LayerHeldItemTranslateGun<>(this, ctx.getItemInHandRenderer()));
		}
		if (armorModels != null) {
			this.addLayer(new HumanoidArmorLayer<>(this, new NPCHumanoidModel<T>(armorModels.apply(0.5f)), new NPCHumanoidModel<T>(armorModels.apply(1.0f)), ctx.getModelManager()));
		}
	}

	/**
	 * Standard Techguns NPC: ModelGenericNPC with armor layer (1.12 RenderArmySoldier, RenderBandit, ...)
	 */
	public static <T extends Mob> EntityRendererProvider<T> generic(String texture) {
		ResourceLocation tex = new ResourceLocation(Techguns.MODID, "textures/entity/" + texture + ".png");
		return ctx -> new RenderGenericNPC<T>(ctx, new ModelGenericNPC(), 0.5f, e -> tex, false, s -> new ModelGenericNPC(s, true));
	}

	public static <T extends Mob> EntityRendererProvider<T> of(Supplier<TGModelBiped> model, ResourceLocation tex, boolean translateGun, @Nullable Function<Float, TGModelBiped> armorModels) {
		return ctx -> new RenderGenericNPC<T>(ctx, model.get(), 0.5f, e -> tex, translateGun, armorModels);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return this.texture.apply(entity);
	}
}
