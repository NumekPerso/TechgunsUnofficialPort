package techguns.client.particle;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class TGParticleStreak extends TGParticle{

	protected TGParticleStreak prev;
	protected TGParticleStreak next;

	protected Vec3 pos1; //This streak segment's vertices
	protected Vec3 pos2;

	public TGParticleStreak(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
			double ySpeedIn, double zSpeedIn, TGParticleSystem particleSystem) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, particleSystem);
	}



	/**
	 * Renders the particle
	 *
	 * 1.12 used the look vector and position of the client player, 1.20 uses the camera (same in first person)
	 */
	@Override
	public void renderParticle(PoseStack poseStack, MultiBufferSource buffers, Entity entityIn, float partialTickTime, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ)
	{
		float progress = ((float)this.age+partialTickTime) / (float)this.lifetime;

		preRenderStep(progress);

		if (this.next == null) {
			this.alpha = 0.0f;
		}

		/*-------------------
		 * ANIMATION
		 */
		int currentFrame = getCurrentFrame(progress);

		/* -------------
		 * RENDER PARTICLE
		 */
		this.particleScale = sizePrev + (size-sizePrev)*partialTickTime;

		float fscale = 0.1F * this.particleScale;

		float fPosX = (float)(this.xo + (this.x - this.xo) * (double)partialTickTime - TGParticleManager.interpPosX);
		float fPosY = (float)(this.yo + (this.y - this.yo) * (double)partialTickTime - TGParticleManager.interpPosY);
		float fPosZ = (float)(this.zo + (this.z - this.zo) * (double)partialTickTime - TGParticleManager.interpPosZ);

		int col = currentFrame % type.columns;
		int row = (currentFrame / type.columns);

		float u = 1.f/type.columns;
		float v = 1.f/type.columns;

		float U1 = col*u;
		float V1 = row*v;
		float U2 = (col+1)*u;
		float V2 = (row+1)*v;

		float ua, va, ub, vb, uc, vc, ud, vd;
		ua=U2; va=V2; ub = U2; vb= V1; uc = U1; vc = V1; ud=U1; vd = V2;

		Vec3 p1, p2, p3, p4;

		if (prev == null) {
			this.pos1 = null;
			this.pos2 = null;
		}else {
			Vec3 v_view = TGParticleManager.viewVector;

			Vec3 v_prev = new Vec3(prev.x - TGParticleManager.interpPosX, prev.y - TGParticleManager.interpPosY, prev.z - TGParticleManager.interpPosZ);

			Vec3 v_dir = v_prev.subtract(fPosX, fPosY, fPosZ).normalize();

			Vec3 v_cross = v_view.cross(v_dir).normalize();

			p1 = new Vec3(v_cross.x*fscale + fPosX, v_cross.y*fscale  + fPosY, v_cross.z*fscale  + fPosZ);
			p2 = new Vec3(v_cross.x* -fscale + fPosX, v_cross.y* -fscale  + fPosY, v_cross.z* -fscale  + fPosZ);

			this.pos1 = p1;
			this.pos2 = p2;


			float fscaleP = prev.particleScale *0.1f;


			if (prev.pos1 != null && prev.pos2 != null) {
				p3 = prev.pos2;
				p4 = prev.pos1;
			}else {
				p4 = new Vec3(v_cross.x*fscaleP + v_prev.x, v_cross.y*fscaleP  + v_prev.y, v_cross.z*fscaleP  + v_prev.z);
				p3 = new Vec3(v_cross.x* -fscaleP + v_prev.x, v_cross.y* -fscaleP  + v_prev.y, v_cross.z* -fscaleP  + v_prev.z);

				prev.pos1 = p4;
				prev.pos2 = p3;
			}

			VertexConsumer buffer = buffers.getBuffer(getTGRenderType());
			Matrix4f mat = poseStack.last().pose();
			boolean fx = isFXFormat();

			addVertex(buffer, mat, fx, p1.x, p1.y, p1.z, ua, va, this.rCol, this.gCol, this.bCol, this.alpha);
			addVertex(buffer, mat, fx, p2.x, p2.y, p2.z, ub, vb, this.rCol, this.gCol, this.bCol, this.alpha);
			addVertex(buffer, mat, fx, p3.x, p3.y, p3.z, uc, vc, prev.rCol, prev.gCol, prev.bCol, prev.alpha);
			addVertex(buffer, mat, fx, p4.x, p4.y, p4.z, ud, vd, prev.rCol, prev.gCol, prev.bCol, prev.alpha);
		}
	}

	public TGParticleStreak getPrev() {
		return prev;
	}

	public void setPrev(TGParticleStreak prev) {
		this.prev = prev;
	}

	public TGParticleStreak getNext() {
		return next;
	}

	public void setNext(TGParticleStreak next) {
		this.next = next;
	}

	@Override
	public boolean doNotSort() {
		return true;
	}

	/**
	 * Streak segments reach to the previous particle and set its vertices, never culled
	 */
	@Override
	public boolean isVisible(net.minecraft.client.renderer.culling.Frustum frustum, double dx, double dy, double dz) {
		return true;
	}

}
