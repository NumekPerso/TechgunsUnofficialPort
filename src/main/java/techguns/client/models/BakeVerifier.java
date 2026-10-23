package techguns.client.models;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;
import techguns.Techguns;
import techguns.client.armor.TGArmorModel;
import techguns.client.armor.TGArmorModels;
import techguns.client.models.additionalslots.ModelAdditionalSlotBase;
import techguns.client.models.additionalslots.ModelAntiGravPack;
import techguns.client.models.additionalslots.ModelFaceMask;
import techguns.client.models.additionalslots.ModelGasMask;
import techguns.client.models.additionalslots.ModelGlider;
import techguns.client.models.additionalslots.ModelGloves;
import techguns.client.models.additionalslots.ModelJetPack;
import techguns.client.models.additionalslots.ModelNightVisionGoggles;
import techguns.client.models.additionalslots.ModelOxygenTanks;
import techguns.client.models.armor.IShieldModel;
import techguns.client.models.armor.ModelAdvancedShield;
import techguns.client.models.armor.ModelBallisticShield;
import techguns.client.models.armor.ModelRiotShield;
import techguns.client.models.armor.TGModelBiped;
import techguns.client.models.machines.ModelAmmoPress;
import techguns.client.models.machines.ModelChemLab;
import techguns.client.models.machines.ModelFabricator;
import techguns.client.models.machines.ModelMachine;
import techguns.client.models.machines.ModelMetalPress;
import techguns.client.models.machines.ModelTurretBase;
import techguns.client.render.ItemRenderHack;
import techguns.client.render.item.IItemRenderer;
import techguns.client.render.item.RenderItemBase;

/**
 * Dev only check of the pre-baked model rendering (TGModelRenderer / TGObjModel fast path): every item renderer, gun model
 * animation state, armor / additional slot / shield / machine model is rendered through the original (legacy) path and through
 * the new path (several times, so the dynamic, baking and baked states are all compared) into capturing vertex consumers, and
 * the vertices (position, uv, normal, color, light, overlay) are compared. Zero area quads are removed from the legacy output
 * (the new path skips them, they produce no pixel).
 *
 * Enabled with -Dtechguns.verifyBake=true (runs once when the title screen is shown, the result is logged),
 * -Dtechguns.verifyBake.exit=true closes the game afterwards.
 */
public class BakeVerifier {

	public static final String PROPERTY = "techguns.verifyBake";

	private static boolean done = false;

	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (done || event.phase != TickEvent.Phase.END) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		if (mc.getOverlay() != null || mc.screen == null) {
			return;
		}
		done = true;
		boolean ok;
		try {
			ok = new BakeVerifier().run();
		} catch (Throwable t) {
			Techguns.LOGGER.error("[BakeVerifier] crashed", t);
			ok = false;
		}
		if (Boolean.getBoolean(PROPERTY + ".exit")) {
			mc.stop();
		}
		if (!ok) {
			throw new IllegalStateException("[BakeVerifier] BAKE VERIFICATION FAILED, see the log");
		}
	}

	// ---------------------------------------------------------------------------------------------------------------

	static final class Vtx {
		RenderType rt;
		float x, y, z, u, v, nx, ny, nz;
		int r = 255, g = 255, b = 255, a = 255;
		int overlay, light;
	}

	/**
	 * Records all vertices of all render types, in order
	 */
	static final class Capture implements MultiBufferSource {
		final List<Vtx> vertices = new ArrayList<>();

		@Override
		public VertexConsumer getBuffer(RenderType rt) {
			return new CaptureConsumer(this, rt);
		}
	}

	static final class CaptureConsumer implements VertexConsumer {
		final Capture capture;
		final RenderType rt;
		Vtx cur;

		CaptureConsumer(Capture capture, RenderType rt) {
			this.capture = capture;
			this.rt = rt;
		}

		@Override
		public VertexConsumer vertex(double x, double y, double z) {
			cur = new Vtx();
			cur.rt = rt;
			cur.x = (float) x;
			cur.y = (float) y;
			cur.z = (float) z;
			return this;
		}

		@Override
		public VertexConsumer color(int red, int green, int blue, int alpha) {
			cur.r = red;
			cur.g = green;
			cur.b = blue;
			cur.a = alpha;
			return this;
		}

		@Override
		public VertexConsumer uv(float u, float v) {
			cur.u = u;
			cur.v = v;
			return this;
		}

		@Override
		public VertexConsumer overlayCoords(int u, int v) {
			cur.overlay = (u & 0xFFFF) | (v << 16);
			return this;
		}

		@Override
		public VertexConsumer uv2(int u, int v) {
			cur.light = (u & 0xFFFF) | (v << 16);
			return this;
		}

		@Override
		public VertexConsumer normal(float x, float y, float z) {
			cur.nx = x;
			cur.ny = y;
			cur.nz = z;
			return this;
		}

		@Override
		public void endVertex() {
			capture.vertices.add(cur);
			cur = null;
		}

		@Override
		public void defaultColor(int red, int green, int blue, int alpha) {
		}

		@Override
		public void unsetDefaultColor() {
		}
	}

	/**
	 * Minimal consumer for the timing (keeps the vertex data alive so nothing is optimized away)
	 */
	static final class SinkConsumer implements VertexConsumer, MultiBufferSource {
		double sum;
		int count;

		@Override
		public VertexConsumer getBuffer(RenderType rt) {
			return this;
		}

		@Override
		public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ) {
			sum += x + y + z + texU + texV + normalX;
			count++;
		}

		@Override
		public VertexConsumer vertex(double x, double y, double z) {
			sum += x + y + z;
			count++;
			return this;
		}

		@Override
		public VertexConsumer color(int red, int green, int blue, int alpha) {
			return this;
		}

		@Override
		public VertexConsumer uv(float u, float v) {
			sum += u;
			return this;
		}

		@Override
		public VertexConsumer overlayCoords(int u, int v) {
			return this;
		}

		@Override
		public VertexConsumer uv2(int u, int v) {
			return this;
		}

		@Override
		public VertexConsumer normal(float x, float y, float z) {
			sum += x;
			return this;
		}

		@Override
		public void endVertex() {
		}

		@Override
		public void defaultColor(int red, int green, int blue, int alpha) {
		}

		@Override
		public void unsetDefaultColor() {
		}
	}

	// ---------------------------------------------------------------------------------------------------------------

	private int cases = 0;
	private int comparedVertices = 0;
	private int removedDegenerate = 0;
	private int legacyVertices = 0;
	private int mismatches = 0;
	private int skipped = 0;
	private final List<String> failedNames = new ArrayList<>();

	private static final int LIGHT = 0xF000F0;
	private static final int OVERLAY = OverlayTexture.NO_OVERLAY;

	/**
	 * One render, done with the given pose stack and buffers
	 */
	interface Case extends BiConsumer<PoseStack, MultiBufferSource> {
	}

	public boolean run() {
		long t0 = System.nanoTime();
		Techguns.LOGGER.info("[BakeVerifier] start");
		boolean savedLegacy = TGModelRenderer.legacyRendering;
		boolean savedRaw = FastVertexWriter.enabled;
		try {
			this.selfTest();
			this.verifyItemRenderers();
			this.verifyGunAnimations();
			this.verifyArmorModels();
			this.verifyOtherModels();
			this.verifyRawWriter();
		} finally {
			FastVertexWriter.enabled = savedRaw;
			TGModelRenderer.legacyRendering = savedLegacy;
		}
		Techguns.LOGGER.info("[BakeVerifier] {} cases, {} vertices compared, {} legacy vertices, {} zero area quad vertices skipped by the new path ({}%), {} cases skipped (exception in both paths)",
				cases, comparedVertices, legacyVertices, removedDegenerate, legacyVertices > 0 ? String.format("%.1f", 100.0 * removedDegenerate / legacyVertices) : "0", skipped);
		this.timing();
		Techguns.LOGGER.info("[BakeVerifier] done in {} ms", (System.nanoTime() - t0) / 1000000L);
		if (mismatches > 0) {
			Techguns.LOGGER.error("[BakeVerifier] BAKE VERIFICATION FAILED: {} mismatching cases: {}", mismatches, failedNames);
			return false;
		}
		Techguns.LOGGER.info("[BakeVerifier] BAKE VERIFICATION PASSED");
		return true;
	}

	private static String itemName(Item item) {
		return String.valueOf(ForgeRegistries.ITEMS.getKey(item));
	}

	private static void basePose(PoseStack ps, int variant) {
		if (variant == 1) {
			//like an inventory slot (GuiGraphics.renderItem)
			ps.translate(120f + 8f, 80f + 8f, 150f);
			ps.mulPoseMatrix(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
			ps.scale(16.0F, 16.0F, 16.0F);
		} else if (variant == 2) {
			//arbitrary world like pose
			ps.translate(-3.25f, 1.5f, 7.75f);
			ps.mulPose(com.mojang.math.Axis.YP.rotationDegrees(33f));
			ps.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-21f));
			ps.scale(0.9f, 0.9f, 0.9f);
		}
	}

	/**
	 * Renders a case with the legacy path, then 3 times with the new path (dynamic, baking, baked) and compares
	 */
	private void check(String name, Case c) {
		for (int variant = 0; variant < 3; variant++) {
			cases++;
			String caseName = name + " pose" + variant;
			Capture legacy = new Capture();
			Throwable legacyEx = null;
			TGModelRenderer.legacyRendering = true;
			try {
				PoseStack ps = new PoseStack();
				basePose(ps, variant);
				c.accept(ps, legacy);
			} catch (Throwable t) {
				legacyEx = t;
			}
			for (int run = 0; run < 3; run++) {
				Capture fast = new Capture();
				Throwable fastEx = null;
				TGModelRenderer.legacyRendering = false;
				try {
					PoseStack ps = new PoseStack();
					basePose(ps, variant);
					c.accept(ps, fast);
				} catch (Throwable t) {
					fastEx = t;
				}
				if (legacyEx != null || fastEx != null) {
					if (legacyEx != null && fastEx != null) {
						if (run == 0 && variant == 0) {
							skipped++;
							Techguns.LOGGER.warn("[BakeVerifier] {}: exception in both paths (not a bake problem): {}", caseName, legacyEx.toString());
						}
					} else {
						fail(caseName + " run" + run, "exception legacy=" + legacyEx + " new=" + fastEx);
						Techguns.LOGGER.error("[BakeVerifier] exception", legacyEx != null ? legacyEx : fastEx);
						return;
					}
					continue;
				}
				if (!compare(caseName + " run" + run, legacy.vertices, fast.vertices, run == 0)) {
					return;
				}
			}
		}
	}

	private void fail(String name, String msg) {
		mismatches++;
		failedNames.add(name);
		Techguns.LOGGER.error("[BakeVerifier] MISMATCH {}: {}", name, msg);
	}

	private static boolean samePos(Vtx a, Vtx b) {
		return a.x == b.x && a.y == b.y && a.z == b.z;
	}

	private static List<Vtx> removeDegenerateQuads(List<Vtx> in) {
		List<Vtx> out = new ArrayList<>(in.size());
		int i = 0;
		while (i < in.size()) {
			Vtx v0 = in.get(i);
			if (v0.rt.mode() == VertexFormat.Mode.QUADS && i + 3 < in.size() && in.get(i + 1).rt == v0.rt && in.get(i + 2).rt == v0.rt && in.get(i + 3).rt == v0.rt) {
				Vtx[] q = { v0, in.get(i + 1), in.get(i + 2), in.get(i + 3) };
				int distinct = 1;
				for (int k = 1; k < 4; k++) {
					boolean seen = false;
					for (int j = 0; j < k; j++) {
						if (samePos(q[k], q[j])) {
							seen = true;
							break;
						}
					}
					if (!seen) {
						distinct++;
					}
				}
				if (distinct > 2) {
					for (Vtx v : q) {
						out.add(v);
					}
				}
				i += 4;
			} else {
				out.add(v0);
				i++;
			}
		}
		return out;
	}

	private static boolean close(float a, float b, float tol) {
		return Math.abs(a - b) <= tol * Math.max(1.0f, Math.abs(a));
	}

	private boolean compare(String name, List<Vtx> legacyRaw, List<Vtx> fast, boolean count) {
		List<Vtx> legacy = removeDegenerateQuads(legacyRaw);
		if (count) {
			legacyVertices += legacyRaw.size();
			removedDegenerate += legacyRaw.size() - legacy.size();
			comparedVertices += legacy.size();
		}
		if (legacy.size() != fast.size()) {
			fail(name, "vertex count legacy=" + legacy.size() + " (raw " + legacyRaw.size() + ") new=" + fast.size());
			return false;
		}
		final float tol = 1e-4f;
		for (int i = 0; i < legacy.size(); i++) {
			Vtx a = legacy.get(i);
			Vtx b = fast.get(i);
			String err = null;
			if (a.rt != b.rt) {
				err = "render type " + a.rt + " / " + b.rt;
			} else if (!close(a.x, b.x, tol) || !close(a.y, b.y, tol) || !close(a.z, b.z, tol)) {
				err = "position (" + a.x + "," + a.y + "," + a.z + ") / (" + b.x + "," + b.y + "," + b.z + ")";
			} else if (!close(a.u, b.u, tol) || !close(a.v, b.v, tol)) {
				err = "uv (" + a.u + "," + a.v + ") / (" + b.u + "," + b.v + ")";
			} else if (!close(a.nx, b.nx, tol) || !close(a.ny, b.ny, tol) || !close(a.nz, b.nz, tol)) {
				err = "normal (" + a.nx + "," + a.ny + "," + a.nz + ") / (" + b.nx + "," + b.ny + "," + b.nz + ")";
			} else if (a.r != b.r || a.g != b.g || a.b != b.b || a.a != b.a) {
				err = "color (" + a.r + "," + a.g + "," + a.b + "," + a.a + ") / (" + b.r + "," + b.g + "," + b.b + "," + b.a + ")";
			} else if (a.light != b.light || a.overlay != b.overlay) {
				err = "light/overlay " + a.light + "/" + a.overlay + " / " + b.light + "/" + b.overlay;
			}
			if (err != null) {
				fail(name, "vertex " + i + ": " + err);
				return false;
			}
		}
		return true;
	}

	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * The comparison must detect a small error (otherwise the verification proves nothing)
	 */
	private void selfTest() {
		Capture a = new Capture();
		Capture b = new Capture();
		TGModelRenderer.legacyRendering = true;
		for (Capture c : new Capture[] { a, b }) {
			PoseStack ps = new PoseStack();
			basePose(ps, 2);
			new ModelAmmoPress().render(ps, c.getBuffer(RenderType.entityCutoutNoCull(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS)), LIGHT, OVERLAY, 0.0625f, 0.5f);
		}
		Vtx v = b.vertices.get(b.vertices.size() / 2);
		v.y += 0.002f;
		int savedMismatches = mismatches;
		boolean identicalOk = compare("selftest identical", a.vertices, a.vertices, false);
		boolean detected = !compare("selftest (expected mismatch)", a.vertices, b.vertices, false);
		mismatches = savedMismatches;
		failedNames.clear();
		if (!identicalOk || !detected || a.vertices.isEmpty()) {
			throw new IllegalStateException("[BakeVerifier] self test failed: identical=" + identicalOk + " detected=" + detected);
		}
		Techguns.LOGGER.info("[BakeVerifier] self test OK ({} vertices, a 0.002 offset is detected)", a.vertices.size());
	}

	private void verifyItemRenderers() {
		int n = 0;
		int before = mismatches;
		for (Map.Entry<Item, IItemRenderer> e : ItemRenderHack.getRegisteredRenderers().entrySet()) {
			Item item = e.getKey();
			IItemRenderer r = e.getValue();
			int itemBefore = mismatches;
			for (ItemDisplayContext ctx : ItemDisplayContext.values()) {
				boolean left = ctx == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || ctx == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
				check("item " + itemName(item) + " " + ctx, (ps, buf) -> r.renderItem(ctx, new ItemStack(item), null, left, ps, buf, LIGHT, OVERLAY));
			}
			n++;
			Techguns.LOGGER.info("[BakeVerifier] item {}: {}", itemName(item), mismatches == itemBefore ? "OK" : "MISMATCH");
		}
		Techguns.LOGGER.info("[BakeVerifier] {} item renderers x {} display contexts, {} mismatches", n, ItemDisplayContext.values().length, mismatches - before);
	}

	private void verifyGunAnimations() {
		ItemDisplayContext[] ctxs = { ItemDisplayContext.GUI, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, ItemDisplayContext.GROUND };
		float[][] states = { { 0f, 0f, 0f }, { 0.3f, 0f, 0f }, { 0f, 0.5f, 0f }, { 0f, 0f, 0.6f }, { 0.8f, 0f, 0.4f }, { 0.05f, 0.95f, 1f } };
		int[] ammos = { 0, 1, 100 };
		int n = 0;
		int before = mismatches;
		for (int pass = 0; pass < 2; pass++) {
			for (Map.Entry<Item, IItemRenderer> e : ItemRenderHack.getRegisteredRenderers().entrySet()) {
				if (!(e.getValue() instanceof RenderItemBase rib) || rib.getModel() == null) {
					continue;
				}
				ModelMultipart model = rib.getModel();
				int parts = rib.getParts();
				String name = itemName(e.getKey());
				int itemBefore = mismatches;
				for (ItemDisplayContext ctx : ctxs) {
					for (int ammo : ammos) {
						for (float[] st : states) {
							check("anim " + name + " " + ctx + " ammo" + ammo + " fire" + st[0] + " reload" + st[1] + " charge" + st[2] + " pass" + pass, (ps, buf) -> {
								for (int p = 0; p < parts; p++) {
									model.render(ps, buf.getBuffer(RenderType.entityCutoutNoCull(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS)), LIGHT, OVERLAY, null, 0, 0, 0, 0, 0,
											RenderItemBase.SCALE, ammo, st[1], ctx, p, st[0], st[2]);
								}
							});
						}
					}
				}
				if (pass == 0) {
					n++;
				}
				Techguns.LOGGER.info("[BakeVerifier] gun model animations {} (pass {}): {}", name, pass, mismatches == itemBefore ? "OK" : "MISMATCH");
			}
		}
		Techguns.LOGGER.info("[BakeVerifier] {} item models animation states, {} mismatches", n, mismatches - before);
	}

	private static final EquipmentSlot[] SLOTS = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

	private static void randomPose(TGModelBiped m, Random rand) {
		TGModelRenderer[] roots = { m.bipedHead, m.bipedHeadwear, m.bipedBody, m.bipedRightArm, m.bipedLeftArm, m.bipedRightLeg, m.bipedLeftLeg };
		for (TGModelRenderer r : roots) {
			r.rotateAngleX = (rand.nextFloat() - 0.5f) * 2f;
			r.rotateAngleY = rand.nextInt(3) == 0 ? 0f : (rand.nextFloat() - 0.5f) * 1.5f;
			r.rotateAngleZ = rand.nextInt(2) == 0 ? 0f : (rand.nextFloat() - 0.5f) * 0.5f;
			r.rotationPointX += (rand.nextFloat() - 0.5f) * 0.5f;
			r.rotationPointY += (rand.nextFloat() - 0.5f) * 3f;
		}
	}

	private void verifyArmorModels() {
		int before = mismatches;
		int n = 0;
		for (Map.Entry<String, TGArmorModel> e : TGArmorModels.getAllArmorModels().entrySet()) {
			this.verifyBiped("armor " + e.getKey(), e.getValue().getBiped());
			n++;
		}
		Techguns.LOGGER.info("[BakeVerifier] {} worn armor models, {} mismatches", n, mismatches - before);
	}

	/**
	 * Worn armor: random part poses, one render per slot (like the armor layer: same model, visibility changed per slot), poses
	 * revisited so outdated bakes would show up
	 */
	private void verifyBiped(String name, TGModelBiped model) {
		int itemBefore = mismatches;
		long[] seeds = { 1L, 2L, 3L, 1L, 1L, 4L, 2L };
		for (int i = 0; i < seeds.length; i++) {
			long seed = seeds[i];
			for (boolean child : new boolean[] { false, true }) {
				check(name + " pose" + i + " child=" + child, (ps, buf) -> {
					model.setDefaultPose();
					randomPose(model, new Random(seed));
					VertexConsumer vc = buf.getBuffer(RenderType.armorCutoutNoCull(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS));
					for (EquipmentSlot slot : SLOTS) {
						model.setVisibleForSlot(slot, true);
						model.render(ps, vc, LIGHT, OVERLAY, 1f, 1f, 1f, 1f, child, 0.0625f);
					}
				});
			}
		}
		//1.12 tint (leather like color) and a non default scale
		check(name + " tinted scale", (ps, buf) -> {
			model.setDefaultPose();
			model.setVisibleForSlot(EquipmentSlot.CHEST, true);
			model.render(ps, buf.getBuffer(RenderType.armorCutoutNoCull(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS)), LIGHT, OVERLAY, 0.5f, 0.75f, 0.25f, 1f, false, 0.07f);
		});
		Techguns.LOGGER.info("[BakeVerifier] {}: {}", name, mismatches == itemBefore ? "OK" : "MISMATCH");
	}

	private void verifyOtherModels() {
		int before = mismatches;
		RenderType rt = RenderType.entityCutoutNoCull(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS);
		ModelMachine[] machines = { new ModelAmmoPress(), new ModelChemLab(), new ModelFabricator(), new ModelMetalPress() };
		for (ModelMachine m : machines) {
			int itemBefore = mismatches;
			for (float progress : new float[] { 0f, 0.25f, 0.6f, 0f, 0.99f }) {
				check("machine " + m.getClass().getSimpleName() + " progress" + progress, (ps, buf) -> m.render(ps, buf.getBuffer(rt), LIGHT, OVERLAY, 0.0625f, progress));
			}
			if (m instanceof ModelChemLab cl) {
				check("machine ModelChemLab glass", (ps, buf) -> cl.renderGlass(ps, buf.getBuffer(rt), LIGHT, OVERLAY, 0.0625f));
			}
			if (m instanceof ModelFabricator fab) {
				check("machine ModelFabricator glass", (ps, buf) -> fab.renderGlass(ps, buf.getBuffer(rt), LIGHT, OVERLAY, 0.0625f));
			}
			Techguns.LOGGER.info("[BakeVerifier] machine {}: {}", m.getClass().getSimpleName(), mismatches == itemBefore ? "OK" : "MISMATCH");
		}
		ModelTurretBase turret = new ModelTurretBase();
		for (int p = 0; p < 3; p++) {
			int part = p;
			check("turret base part" + p, (ps, buf) -> turret.render(ps, buf.getBuffer(rt), LIGHT, OVERLAY, null, 0, 0, 0, 0, 0, 0.0625f, 0, 0f, ItemDisplayContext.NONE, part, 0f, 0f));
		}

		IShieldModel[] shields = { new ModelAdvancedShield(), new ModelBallisticShield(), new ModelRiotShield() };
		for (IShieldModel s : shields) {
			check("shield " + s.getClass().getSimpleName(), (ps, buf) -> s.render(ps, buf.getBuffer(rt), LIGHT, OVERLAY, 0.0625f));
		}

		ModelAdditionalSlotBase[] slots = { new ModelGasMask(), new ModelGlider(), new ModelJetPack(0), new ModelJetPack(1), new ModelOxygenTanks(), new ModelNightVisionGoggles(),
				new ModelAntiGravPack(), new ModelFaceMask(true), new ModelFaceMask(false), new ModelGloves(0.45f, false), new ModelGloves(0.45f, true) };
		for (ModelAdditionalSlotBase s : slots) {
			int itemBefore = mismatches;
			for (long seed : new long[] { 5L, 6L, 5L }) {
				check("slot " + s.getClass().getSimpleName() + " seed" + seed, (ps, buf) -> {
					s.setDefaultPose();
					randomPose(s, new Random(seed));
					s.copyRotateAngles();
					s.render(ps, buf.getBuffer(rt), LIGHT, OVERLAY, 0.0625f, null);
				});
			}
			Techguns.LOGGER.info("[BakeVerifier] slot model {}: {}", s.getClass().getSimpleName(), mismatches == itemBefore ? "OK" : "MISMATCH");
		}
		Techguns.LOGGER.info("[BakeVerifier] machine / turret / shield / additional slot models, {} mismatches", mismatches - before);
	}

	// ---------------------------------------------------------------------------------------------------------------

	private final com.mojang.blaze3d.vertex.BufferBuilder bbCheck = new com.mojang.blaze3d.vertex.BufferBuilder(1 << 16);
	private int rawCases = 0;
	private int rawVertices = 0;
	private int rawDirect = 0;

	private byte[] renderToBufferBuilder(Case c, boolean raw) {
		FastVertexWriter.enabled = raw;
		bbCheck.begin(VertexFormat.Mode.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.NEW_ENTITY);
		PoseStack ps = new PoseStack();
		basePose(ps, 1);
		try {
			c.accept(ps, rt -> bbCheck);
		} catch (Throwable t) {
			//compared by the main verification
		}
		com.mojang.blaze3d.vertex.BufferBuilder.RenderedBuffer rb = bbCheck.end();
		ByteBuffer vb = rb.vertexBuffer();
		byte[] out = new byte[vb.remaining()];
		vb.get(vb.position(), out);
		rb.release();
		return out;
	}

	/**
	 * FastVertexWriter must write exactly the bytes of BufferBuilder.vertex (the padding byte of the normal excepted)
	 */
	private void checkRaw(String name, Case c) {
		rawCases++;
		TGModelRenderer.legacyRendering = false;
		//reach the baked state first
		this.renderToBufferBuilder(c, false);
		this.renderToBufferBuilder(c, false);
		byte[] normal = this.renderToBufferBuilder(c, false);
		byte[] direct = this.renderToBufferBuilder(c, true);
		if (normal.length != direct.length) {
			fail(name + " raw", "byte count " + normal.length + " / " + direct.length);
			return;
		}
		rawVertices += normal.length / FastVertexWriter.VERTEX_SIZE;
		for (int i = 0; i < normal.length; i++) {
			if (i % FastVertexWriter.VERTEX_SIZE == 35) {
				continue;
			}
			if (normal[i] != direct[i]) {
				fail(name + " raw", "byte " + i + " (vertex " + i / FastVertexWriter.VERTEX_SIZE + " offset " + i % FastVertexWriter.VERTEX_SIZE + "): " + normal[i] + " / " + direct[i]);
				return;
			}
		}
	}

	private void verifyRawWriter() {
		int before = mismatches;
		//count how much really goes through the direct writer
		SinkConsumer dummy = new SinkConsumer();
		if (FastVertexWriter.target(dummy) != null) {
			throw new IllegalStateException("FastVertexWriter accepts a non BufferBuilder consumer");
		}
		for (Map.Entry<Item, IItemRenderer> e : ItemRenderHack.getRegisteredRenderers().entrySet()) {
			Item item = e.getKey();
			IItemRenderer r = e.getValue();
			for (ItemDisplayContext ctx : ItemDisplayContext.values()) {
				boolean left = ctx == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || ctx == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
				checkRaw("item " + itemName(item) + " " + ctx, (ps, buf) -> r.renderItem(ctx, new ItemStack(item), null, left, ps, buf, LIGHT, OVERLAY));
			}
		}
		for (Map.Entry<String, TGArmorModel> e : TGArmorModels.getAllArmorModels().entrySet()) {
			TGModelBiped model = e.getValue().getBiped();
			checkRaw("armor " + e.getKey(), (ps, buf) -> {
				model.setDefaultPose();
				randomPose(model, new Random(9L));
				for (EquipmentSlot slot : SLOTS) {
					model.setVisibleForSlot(slot, true);
					model.render(ps, buf.getBuffer(RenderType.armorCutoutNoCull(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS)), 0x00A00050, OVERLAY, 0.8f, 0.6f, 0.3f, 1f, false,
							0.0625f);
				}
			});
		}
		//how many vertices take the direct path (GUI like BufferBuilder)
		FastVertexWriter.enabled = true;
		Techguns.LOGGER.info("[BakeVerifier] direct BufferBuilder writes: {} cases, {} vertices per pass, {} vertices written directly in total, byte identical: {}", rawCases, rawVertices,
				FastVertexWriter.writtenVertices, mismatches == before ? "yes" : "NO");
		if (FastVertexWriter.writtenVertices == 0L) {
			fail("raw writer", "the direct writer was never used");
		}
	}

	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Rough cost of the model rendering in the inventory: all item renderers in the GUI context, legacy vs new path
	 */
	private void timing() {
		List<Map.Entry<Item, IItemRenderer>> list = new ArrayList<>(ItemRenderHack.getRegisteredRenderers().entrySet());
		SinkConsumer sink = new SinkConsumer();
		com.mojang.blaze3d.vertex.BufferBuilder bb = new com.mojang.blaze3d.vertex.BufferBuilder(1 << 21);
		MultiBufferSource bbSource = rt -> bb;
		int rounds = 200;
		long[] result = new long[3];
		int[] vertices = new int[3];
		for (int target = 0; target < 2; target++) {
			//target 0: model work only (minimal consumer), target 1: written into a real BufferBuilder like in game
			for (int rep = 0; rep < 5; rep++) {
				for (int mode = 0; mode < 3; mode++) {
					//mode 0: legacy, 1: baked, 2: baked + direct BufferBuilder writes
					TGModelRenderer.legacyRendering = mode == 0;
					FastVertexWriter.enabled = mode == 2;
					sink.count = 0;
					long t = System.nanoTime();
					for (int i = 0; i < rounds; i++) {
						if (target == 1) {
							bb.begin(VertexFormat.Mode.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.NEW_ENTITY);
						}
						for (Map.Entry<Item, IItemRenderer> e : list) {
							PoseStack ps = new PoseStack();
							basePose(ps, 1);
							try {
								e.getValue().renderItem(ItemDisplayContext.GUI, new ItemStack(e.getKey()), null, false, ps, target == 0 ? sink : bbSource, LIGHT, OVERLAY);
							} catch (Throwable ex) {
								//reported by the verification
							}
						}
						if (target == 1) {
							bb.end().release();
						}
					}
					result[mode] = System.nanoTime() - t;
					vertices[mode] = sink.count;
				}
				Techguns.LOGGER.info("[BakeVerifier] timing {} (run {}): {} item renderers x {} GUI renders: legacy {} ms, baked {} ms ({}x), baked + direct writes {} ms ({}x)",
						target == 0 ? "model only" : "BufferBuilder", rep, list.size(), rounds, result[0] / 1000000L, result[1] / 1000000L,
						String.format("%.2f", (double) result[0] / Math.max(1L, result[1])), result[2] / 1000000L, String.format("%.2f", (double) result[0] / Math.max(1L, result[2])));
			}
		}
		FastVertexWriter.enabled = true;
		Techguns.LOGGER.info("[BakeVerifier] (sum {})", sink.sum);
	}
}
