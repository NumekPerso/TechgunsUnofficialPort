package techguns.client.particle;

import java.util.Iterator;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import techguns.client.particle.list.ParticleList;
import techguns.client.particle.list.ParticleList.ParticleListIterator;

public class TGParticleManager {

	/**
	 * Camera position of the current frame, world particles are rendered relative to it
	 */
	public static double interpPosX;
	public static double interpPosY;
	public static double interpPosZ;

	/**
	 * Camera look vector of the current frame (used by streaks)
	 */
	public static Vec3 viewVector = new Vec3(0, 0, 1);

	protected ParticleList<TGParticleSystem> list_systems = new ParticleList<>();
	/**
	 * 1.12 kept this list depth sorted with a partial bubble sort (10 passes per tick). Particles are now batched per
	 * render type and depth sorted every frame on upload (TGParticleBufferSource), this list is no longer sorted.
	 */
	protected ParticleList<ITGParticle> list = new ParticleList<>();
	protected ParticleList<ITGParticle> list_nosort = new ParticleList<>();

	public void addEffect(ITGParticle effect)
	{
		if (effect == null) return;
		if(effect instanceof TGParticleSystem) {
			list_systems.add((TGParticleSystem) effect);
		} else {
			if (effect.doNotSort()) {
				list_nosort.add(effect);
			} else {
				list.add(effect);
			}
		}
	}

	public void tickParticles() {
		if(Minecraft.getInstance().isPaused()) return;

		Iterator<TGParticleSystem> sysit = list_systems.iterator();
		while(sysit.hasNext()) {
			TGParticleSystem p = sysit.next();

			p.updateTick();
			if(p.shouldRemove()) {
				sysit.remove();
			}
		}

		ParticleListIterator<ITGParticle> it = list.iterator();
		while(it.hasNext()) {
			ITGParticle p = it.next();

			p.updateTick();
			if(p.shouldRemove()) {
				it.remove();
			}
		}

		Iterator<ITGParticle> it2 = list_nosort.iterator();
		while(it2.hasNext()) {
			ITGParticle p = it2.next();

			p.updateTick();
			if(p.shouldRemove()) {
				it2.remove();
			}
		}
	}

	/**
	 * Removes all particles (level change)
	 */
	public void clear() {
		this.list_systems = new ParticleList<>();
		this.list = new ParticleList<>();
		this.list_nosort = new ParticleList<>();
	}

	public int getParticleCount() {
		return list.getSize() + list_nosort.getSize();
	}

	/**
	 * 1.12: renderParticles(renderViewEntity, partialTicks) in RenderWorldLastEvent.
	 * The pose stack contains the camera rotation, vertices are written relative to the camera position.
	 * Particles outside of the frustum (if not null) are skipped.
	 */
	public void renderParticles(Camera camera, PoseStack poseStack, TGParticleBufferSource buffers, Frustum frustum, float partialTicks)
	{
		this.renderParticles(camera, poseStack, buffers, frustum, partialTicks, RenderPass.ALL);
	}

	/**
	 * Without the fabulous transparency, the particles are rendered after the translucent terrain: water hides the
	 * particles behind its surface (depth test against the water). The particles on the other side of a water surface
	 * than the camera are rendered before the translucent terrain (BEHIND_WATER), the water is then blended over them.
	 */
	public enum RenderPass {
		ALL, BEHIND_WATER, NORMAL
	}

	protected boolean cameraInWater = false;
	protected final Long2BooleanOpenHashMap waterCache = new Long2BooleanOpenHashMap();

	protected boolean isOtherSideOfWater(double x, double y, double z) {
		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return false;
		}
		long key = BlockPos.asLong(Mth.floor(x), Mth.floor(y), Mth.floor(z));
		boolean inWater;
		if (waterCache.containsKey(key)) {
			inWater = waterCache.get(key);
		} else {
			inWater = level.getFluidState(BlockPos.of(key)).is(FluidTags.WATER);
			waterCache.put(key, inWater);
		}
		return inWater != cameraInWater;
	}

	public void renderParticles(Camera camera, PoseStack poseStack, TGParticleBufferSource buffers, Frustum frustum, float partialTicks, RenderPass pass)
	{
		Entity entityIn = camera.getEntity();
		this.cameraInWater = camera.getFluidInCamera() == FogType.WATER;
		this.waterCache.clear();

		float f1 = Mth.cos(camera.getYRot() * 0.017453292F);
		float f2 = Mth.sin(camera.getYRot() * 0.017453292F);
		float f3 = -f2 * Mth.sin(camera.getXRot() * 0.017453292F);
		float f4 = f1 * Mth.sin(camera.getXRot() * 0.017453292F);
		float f5 = Mth.cos(camera.getXRot() * 0.017453292F);

		Vec3 campos = camera.getPosition();
		interpPosX = campos.x;
		interpPosY = campos.y;
		interpPosZ = campos.z;
		Vector3f look = camera.getLookVector();
		viewVector = new Vec3(look);

		renderList(this.list, poseStack, buffers, frustum, entityIn, partialTicks, look, f1, f5, f2, f3, f4, pass);
		renderList(this.list_nosort, poseStack, buffers, frustum, entityIn, partialTicks, look, f1, f5, f2, f3, f4, pass);
	}

	protected void renderList(ParticleList<ITGParticle> particles, PoseStack poseStack, TGParticleBufferSource buffers, Frustum frustum, Entity entityIn, float partialTicks, Vector3f look,
			float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ, RenderPass pass) {
		for (ITGParticle p : particles) {
			if (p instanceof TGParticle) {
				TGParticle tp = (TGParticle) p;
				if (pass != RenderPass.ALL && (pass == RenderPass.BEHIND_WATER) != this.isOtherSideOfWater(tp.posX(), tp.posY(), tp.posZ())) {
					continue;
				}
				double dx = tp.posX() - interpPosX;
				double dy = tp.posY() - interpPosY;
				double dz = tp.posZ() - interpPosZ;
				if (frustum != null && !tp.isVisible(frustum, dx, dy, dz)) {
					continue;
				}
				//depth along the view direction, orders the alpha batches
				buffers.setCurrentDepth(dx * look.x() + dy * look.y() + dz * look.z());
			} else if (pass == RenderPass.BEHIND_WATER) {
				continue;
			}
			p.doRender(poseStack, buffers, entityIn, partialTicks, rotX, rotZ, rotYZ, rotXY, rotXZ);
		}
	}
}
