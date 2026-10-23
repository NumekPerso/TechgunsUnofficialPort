package techguns.client.render;

import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import techguns.Techguns;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import techguns.api.guns.GunManager;
import techguns.client.render.item.IItemRenderer;

/**
 * Registry of the Techguns item renderers. In 1.12 an ASM hook replaced the vanilla item rendering, in 1.20 the items use
 * a builtin/entity model and are rendered through TGItemRenderer (BlockEntityWithoutLevelRenderer).
 */
public class ItemRenderHack {

	protected static HashMap<Item, IItemRenderer> renderRegistry = new HashMap<>();

	/**
	 * The living entity currently rendered by a LivingEntityRenderer (third person items), set by client events
	 */
	protected static LivingEntity currentRenderedEntity = null;

	public static void registerItemRenderer(Item item, IItemRenderer renderer) {
		renderRegistry.put(item, renderer);
	}

	/**
	 * Register by registry name (techguns:&lt;name&gt;), so renderers don't depend on the static item fields
	 */
	public static void registerItemRenderer(String name, IItemRenderer renderer) {
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Techguns.MODID, name));
		if (item == null || item == Items.AIR) {
			Techguns.LOGGER.warn("Can't register item renderer, no item techguns:" + name);
			return;
		}
		renderRegistry.put(item, renderer);
	}

	/**
	 * All registered renderers (read only, used by the bake verification)
	 */
	public static java.util.Map<Item, IItemRenderer> getRegisteredRenderers() {
		return java.util.Collections.unmodifiableMap(renderRegistry);
	}

	public static IItemRenderer getRendererForItem(Item item) {
		return renderRegistry.get(item);
	}

	public static void setCurrentRenderedEntity(@Nullable LivingEntity ent) {
		currentRenderedEntity = ent;
	}

	/**
	 * Entity holding the rendered item: the local player for first person, the rendered living entity for third person, null otherwise
	 */
	@Nullable
	public static LivingEntity getEntityForContext(ItemDisplayContext ctx) {
		switch (ctx) {
		case FIRST_PERSON_LEFT_HAND:
		case FIRST_PERSON_RIGHT_HAND:
			return Minecraft.getInstance().player;
		case THIRD_PERSON_LEFT_HAND:
		case THIRD_PERSON_RIGHT_HAND:
		case HEAD:
			return currentRenderedEntity;
		default:
			return null;
		}
	}

	public static boolean shouldRenderItem(ItemStack stack, @Nullable LivingEntity elb, ItemDisplayContext transform) {
		if (!(transform == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transform == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
				transform == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transform == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)) {
			return true;
		}
		if (elb == null) {
			return true;
		}

		boolean mainhand = transform == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transform == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
		if (elb.getMainArm() == HumanoidArm.LEFT) {
			mainhand = !mainhand;
		}
		if (mainhand) {
			return true;
		} else {
			return GunManager.canUseOffhand(elb.getMainHandItem(), stack, elb);
		}
	}
}
