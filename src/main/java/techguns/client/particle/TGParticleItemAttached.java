package techguns.client.particle;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import techguns.util.MathUtil;


/**
 * Particle attached to the item held by an entity, rendered by the item renderer in item space
 */
public class TGParticleItemAttached extends TGParticle {

	protected Entity parentEntity;

	public TGParticleItemAttached(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
			double ySpeedIn, double zSpeedIn, TGParticleSystem particleSystem, Entity parentEntity) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, particleSystem);
		this.parentEntity=parentEntity;
	}

	@Override
	public void doRender(PoseStack poseStack, MultiBufferSource buffers, Entity renderViewEntity, float partialTickTime, float rotX, float rotZ,
			float rotYZ, float rotXY, float rotXZ) {
		boolean thirdp = true;
		if(parentEntity!=null && parentEntity == renderViewEntity) {
			if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
				thirdp=false;
			}
		}
		this.renderParticle(poseStack, buffers, renderViewEntity, partialTickTime, rotX, rotZ, rotYZ, rotXY, rotXZ, thirdp);
	}

	@Override
	public void renderParticle(PoseStack poseStack, MultiBufferSource buffers, Entity entityIn, float partialTickTime, float rotX, float rotZ,
			float rotYZ, float rotXY, float rotXZ) {
		renderParticle(poseStack, buffers, entityIn, partialTickTime, rotX, rotZ, rotYZ, rotXY, rotXZ, true);
	}

	public void renderParticle(PoseStack poseStack, MultiBufferSource buffers, Entity entityIn, float partialTickTime, float rotX, float rotZ,
			float rotYZ, float rotXY, float rotXZ, boolean thirdp) {
		float progress = ((float)this.age+partialTickTime) / (float)this.lifetime;

		preRenderStep(progress);

		/*-------------------
		 * ANIMATION
		 */
		int currentFrame = getCurrentFrame(progress);

		/* -------------
		 * RENDER PARTICLE
		 */
		this.particleScale = sizePrev + (size-sizePrev)*partialTickTime;

		float fscale = 0.1F * this.particleScale;

		float fPosX = (float)(this.xo + (this.x - this.xo) * (double)partialTickTime - (!this.itemAttached ? TGParticleManager.interpPosX :0));
		float fPosY = (float)(this.yo + (this.y - this.yo) * (double)partialTickTime - (!this.itemAttached ? TGParticleManager.interpPosY :0));
		float fPosZ = (float)(this.zo + (this.z - this.zo) * (double)partialTickTime - (!this.itemAttached ? TGParticleManager.interpPosZ :0));

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

		double a = (angle + (partialTickTime * angleRate)) * MathUtil.D2R;
		double[] q = computeQuad(fscale, a, rotX, rotZ, rotYZ, rotXY, rotXZ);
		double p1x = q[0], p1y = q[1], p1z = q[2];
		double p2x = q[3], p2y = q[4], p2z = q[5];
		double p3x = q[6], p3y = q[7], p3z = q[8];
		double p4x = q[9], p4y = q[10], p4z = q[11];

		VertexConsumer buffer = buffers.getBuffer(getTGRenderType());
		Matrix4f mat = poseStack.last().pose();
		boolean fx = isFXFormat();
		float r = this.rCol, g = this.gCol, b = this.bCol, al = this.alpha;

		addVertex(buffer, mat, fx, p1x + fPosX, p1y + fPosY, p1z + fPosZ, ua, va, r, g, b, al);
		addVertex(buffer, mat, fx, p2x + fPosX, p2y + fPosY, p2z + fPosZ, ub, vb, r, g, b, al);
		addVertex(buffer, mat, fx, p3x + fPosX, p3y + fPosY, p3z + fPosZ, uc, vc, r, g, b, al);
		addVertex(buffer, mat, fx, p4x + fPosX, p4y + fPosY, p4z + fPosZ, ud, vd, r, g, b, al);

		if(thirdp) {
			addVertex(buffer, mat, fx, p1z + fPosZ, p1y + fPosY, p1x + fPosX, ua, va, r, g, b, al);
			addVertex(buffer, mat, fx, p2z + fPosZ, p2y + fPosY, p2x + fPosX, ub, vb, r, g, b, al);
			addVertex(buffer, mat, fx, p3z + fPosZ, p3y + fPosY, p3x + fPosX, uc, vc, r, g, b, al);
			addVertex(buffer, mat, fx, p4z + fPosZ, p4y + fPosY, p4x + fPosX, ud, vd, r, g, b, al);

			double h = (p1y+p3y)*0.5d;

			addVertex(buffer, mat, fx, p1x + fPosX, h + fPosY, -p2x + fPosX, ua, va, r, g, b, al);
			addVertex(buffer, mat, fx, p2x + fPosX, h + fPosY, -p1x + fPosX, ub, vb, r, g, b, al);
			addVertex(buffer, mat, fx, p3x + fPosX, h + fPosY, -p4x + fPosX, uc, vc, r, g, b, al);
			addVertex(buffer, mat, fx, p4x + fPosX, h + fPosY, -p3x + fPosX, ud, vd, r, g, b, al);
		}
	}


}
