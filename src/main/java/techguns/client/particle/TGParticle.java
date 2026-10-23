package techguns.client.particle;

import java.awt.Color;
import java.util.Random;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import techguns.client.particle.TGParticleSystemType.AlphaEntry;
import techguns.client.particle.TGParticleSystemType.ColorEntry;
import techguns.client.render.FirstPersonFXCorrection;
import techguns.client.render.TGRenderHelper;
import techguns.client.render.TGRenderTypes;
import techguns.util.MathUtil;


/**
 * An actual spawned particle
 *
 * 1.20: still extends the vanilla Particle (fields: x/y/z = posX.., xo = prevPosX.., xd = motionX.., age = particleAge,
 * lifetime = particleMaxAge, rCol/gCol/bCol/alpha = particleRed..), but is ticked and rendered by the TGParticleManager,
 * never by the vanilla particle engine. Rendering writes into a VertexConsumer of TGRenderTypes instead of the Tessellator.
 */
public class TGParticle extends Particle implements ITGParticle {

	protected static final Random rand = new Random();

	/**
	 * 1.12 lightmap(0, 240) of the vertices (only used by the ALPHA_SHADED entity format)
	 */
	protected static final int LIGHTMAP = LightTexture.pack(0, 15);

	protected static final ColorEntry WHITE = new ColorEntry(1.0f, 1.0f, 1.0f, 0);

	float angle;
	float angleRate;
	float angleRateDamping;

	float size;
	float sizePrev;
	float sizeRate;
	float sizeRateDamping;

	float animationSpeed;

	double velX;
	double velY;
	double velZ;
	float velocityDamping;
	float velocityDampingOnGround;

	float systemVelocityFactor;

	protected float particleScale;

	TGParticleSystem particleSystem;
	TGParticleSystemType type;

	int variationFrame;

	protected double depth;

	protected boolean itemAttached=false;

	public TGParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
			double ySpeedIn, double zSpeedIn, TGParticleSystem particleSystem) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn);
		this.xd = xSpeedIn;
		this.yd = ySpeedIn;
		this.zd = zSpeedIn;

		this.particleSystem = particleSystem;
		this.type = particleSystem.type;
		this.init();
	}

	private void init() {
		this.lifetime = MathUtil.randomInt(rand, type.lifetimeMin, type.lifetimeMax);
		this.size = MathUtil.randomFloat(rand, type.sizeMin, type.sizeMax) * this.particleSystem.scale;
		this.size+= (this.particleSystem.startSize);
		this.sizePrev = this.size;
		this.sizeRate = MathUtil.randomFloat(rand, type.sizeRateMin, type.sizeRateMax)  * this.particleSystem.scale;
		this.sizeRateDamping = MathUtil.randomFloat(rand, type.sizeRateDampingMin, type.sizeRateDampingMax);
		this.animationSpeed = MathUtil.randomFloat(rand, type.animationSpeedMin, type.animationSpeedMax);
		this.velocityDamping = MathUtil.randomFloat(rand, type.velocityDampingMin, type.velocityDampingMax);
		this.systemVelocityFactor = MathUtil.randomFloat(rand, type.systemVelocityFactorMin, type.systemVelocityFactorMax);
		this.velocityDampingOnGround = MathUtil.randomFloat(rand, type.velocityDampingOnGroundMin, type.velocityDampingOnGroundMax);

		this.angle = MathUtil.randomFloat(rand, type.angleMin, type.angleMax);
		this.angleRate = MathUtil.randomFloat(rand, type.angleRateMin, type.angleRateMax);
		this.angleRateDamping = MathUtil.randomFloat(rand, type.angleRateDampingMin, type.angleRateDampingMax);

		this.xd+=(systemVelocityFactor*particleSystem.motionX());
		this.yd+=(systemVelocityFactor*particleSystem.motionY());
		this.zd+=(systemVelocityFactor*particleSystem.motionZ());

		this.velX = this.xd;
		this.velY = this.yd;
		this.velZ = this.zd;

		this.variationFrame = rand.nextInt(Math.max(1, type.frames));
	}


	public void onUpdate()
	{

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.sizePrev = this.size;

		if (this.age++ >= this.lifetime) {
			this.remove();
			return;
		}

		/*---
		 * Move with System
		 */
		if (this.type.particlesStickToSystem) {

			if (this.particleSystem.entity != null) {

				if (!this.particleSystem.entity.isAlive()) {
					this.remove();
					return;
				}

				if (this.type.particlesMoveWithSystem && this.particleSystem.attachToHead && this.particleSystem.entity instanceof LivingEntity) {
					LivingEntity ent = (LivingEntity)this.particleSystem.entity;

					double p = ent.getXRot()*MathUtil.D2R;
					double y = ent.yHeadRot*MathUtil.D2R;

					double prevP = ent.xRotO * MathUtil.D2R;
					double prevY =ent.yHeadRotO * MathUtil.D2R;

					Vec3 offsetBase = this.particleSystem.entityOffset != null ? this.particleSystem.entityOffset.add(this.particleSystem.type.offset) : this.particleSystem.type.offset;

					Vec3 offset = offsetBase.xRot((float)-p);
					offset = offset.yRot((float)-y);

					Vec3 offsetP = offsetBase.xRot((float)-prevP);
					offsetP = offsetP.yRot((float)-prevY);

					this.xo = ent.xo + offsetP.x;
					this.yo = ent.yo + ent.getEyeHeight() + offsetP.y;
					this.zo = ent.zo + offsetP.z;
					this.x = ent.getX() + offset.x;
					this.y = ent.getY() + ent.getEyeHeight() + offset.y;
					this.z = ent.getZ() + offset.z;
				}else {
					Entity ent = this.particleSystem.entity;
					this.xo = ent.xo;
					this.yo = ent.yo;
					this.zo = ent.zo;
					this.x = ent.getX();
					this.y = ent.getY();
					this.z = ent.getZ();
				}
			}else {

				if (!this.particleSystem.isAlive()) {
					this.remove();
					return;
				}

				this.x = this.particleSystem.posX();
				this.y = this.particleSystem.posY();
				this.z = this.particleSystem.posZ();
			}

		}else if (this.type.particlesMoveWithSystem) {
			double dP = (this.particleSystem.rotationPitch - this.particleSystem.prevRotationPitch)*MathUtil.D2R;
			double dY = (this.particleSystem.rotationYaw - this.particleSystem.prevRotationYaw)*MathUtil.D2R;

			Vec3 pos = new Vec3(this.x,  this.y, this.z);
			Vec3 sysPos = new Vec3(this.particleSystem.posX(), this.particleSystem.posY(), this.particleSystem.posZ());

			Vec3 offset = sysPos.subtract(pos);
			offset = offset.yRot((float)-dY);
			offset = offset.xRot((float)-dP);

			Vec3 motion = new Vec3 (this.xd, this.yd, this.zd);
			motion = motion.yRot((float)-dY);
			motion = motion.xRot((float)-dP);

			this.x = sysPos.x+offset.x;
			this.y = sysPos.y+offset.y;
			this.z = sysPos.z+offset.z;

			this.xd = motion.x;
			this.yd = motion.y;
			this.zd = motion.z;

		}


		/* -------------
		 * MOTION
		 */

		this.xd = velX;
		this.yd = velY;
		this.zd = velZ;
		this.yd -= type.gravity;
		this.setPos(this.x+this.xd, this.y+this.yd, this.z+this.zd);


		this.velX *= velocityDamping;
		this.velY *= velocityDamping;
		this.velZ *= velocityDamping;

		if (this.onGround) {
			this.velX *= velocityDampingOnGround;
			this.velY *= velocityDampingOnGround; // ?
			this.velZ *= velocityDampingOnGround;
			if (type.removeOnGround)
				this.remove();
		}

		/* ------------
		 * SIZE
		 */
		size = Math.max(0.0f, size+sizeRate);
		sizeRate *= sizeRateDamping;

		/*
		 * ANGLE
		 */
		angle = (angle + angleRate) % 360.0f;
		angleRate *= angleRateDamping;
	}

	@Override
	public void tick() {
		this.onUpdate();
	}

	/**
	 * Current animation frame
	 */
	protected int getCurrentFrame(float progress) {
		if (type.hasVariations) {
			return variationFrame;
		}else {
			return ((int)((float)type.frames*(progress * this.animationSpeed))) % type.frames;
		}
	}

	/**
	 * Renders the particle
	 */
	public void renderParticle(PoseStack poseStack, MultiBufferSource buffers, Entity entityIn, float partialTickTime, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ)
	{
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

		VertexConsumer buffer = buffers.getBuffer(getTGRenderType());
		Matrix4f mat = poseStack.last().pose();
		boolean fx = isFXFormat();

		addVertex(buffer, mat, fx, q[0] + fPosX, q[1] + fPosY, q[2] + fPosZ, ua, va, this.rCol, this.gCol, this.bCol, this.alpha);
		addVertex(buffer, mat, fx, q[3] + fPosX, q[4] + fPosY, q[5] + fPosZ, ub, vb, this.rCol, this.gCol, this.bCol, this.alpha);
		addVertex(buffer, mat, fx, q[6] + fPosX, q[7] + fPosY, q[8] + fPosZ, uc, vc, this.rCol, this.gCol, this.bCol, this.alpha);
		addVertex(buffer, mat, fx, q[9] + fPosX, q[10] + fPosY, q[11] + fPosZ, ud, vd, this.rCol, this.gCol, this.bCol, this.alpha);
	}

	/**
	 * Quad corners of the last computeQuad call (render thread only, avoids allocating Vec3s per particle and frame)
	 */
	protected static final double[] QUAD = new double[12];

	/**
	 * Billboard (or ground aligned) quad corners relative to the particle center, with the angle rotation applied.
	 * Returns the shared QUAD array: p1 = [0..2], p2 = [3..5], p3 = [6..8], p4 = [9..11]
	 */
	protected double[] computeQuad(float fscale, double a, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ) {
		double[] q = QUAD;
		if (this.type.groundAligned) {
			float s = fscale;
			setQuad(q, -s, 0, -s, s, 0, -s, s, 0, s, -s, 0, s);
			if (a > 0.0001f) {
				//Vec3.yRot
				float f = Mth.cos((float) a);
				float f1 = Mth.sin((float) a);
				for (int i = 0; i < 12; i += 3) {
					double x = q[i];
					double z = q[i + 2];
					q[i] = x * (double) f + z * (double) f1;
					q[i + 2] = z * (double) f - x * (double) f1;
				}
			}
		} else {
			setQuad(q, (double)(- rotX * fscale - rotXY * fscale), (double)(- rotZ * fscale), (double)(- rotYZ * fscale - rotXZ * fscale),
					(double)(- rotX * fscale + rotXY * fscale), (double)( + rotZ * fscale), (double)( - rotYZ * fscale + rotXZ * fscale),
					(double)( rotX * fscale + rotXY * fscale), (double)( + rotZ * fscale), (double)( + rotYZ * fscale + rotXZ * fscale),
					(double)( rotX * fscale - rotXY * fscale), (double)( - rotZ * fscale), (double)( + rotYZ * fscale - rotXZ * fscale));

			//AngleZ Rotation
			if (a > 0.0001f) {
				//axis = p1.normalize().cross(p2.normalize())
				double l1 = Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2]);
				double l2 = Math.sqrt(q[3] * q[3] + q[4] * q[4] + q[5] * q[5]);
				double n1x = 0, n1y = 0, n1z = 0, n2x = 0, n2y = 0, n2z = 0;
				if (l1 >= 1.0E-4D) {
					n1x = q[0] / l1; n1y = q[1] / l1; n1z = q[2] / l1;
				}
				if (l2 >= 1.0E-4D) {
					n2x = q[3] / l2; n2y = q[4] / l2; n2z = q[5] / l2;
				}
				double ax = n1y * n2z - n1z * n2y;
				double ay = n1z * n2x - n1x * n2z;
				double az = n1x * n2y - n1y * n2x;

				double cosa = Math.cos(a);
				double sina = Math.sin(a);

				for (int i = 0; i < 12; i += 3) {
					//rotAxis: p*cos + (axis x p)*sin + axis*(axis.p)*(1-cos)
					double px = q[i], py = q[i + 1], pz = q[i + 2];
					double cx = ay * pz - az * py;
					double cy = az * px - ax * pz;
					double cz = ax * py - ay * px;
					double d = (ax * px + ay * py + az * pz) * (1.0 - cosa);
					q[i] = px * cosa + cx * sina + ax * d;
					q[i + 1] = py * cosa + cy * sina + ay * d;
					q[i + 2] = pz * cosa + cz * sina + az * d;
				}
			}
		}
		return q;
	}

	private static void setQuad(double[] q, double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4) {
		q[0] = x1; q[1] = y1; q[2] = z1;
		q[3] = x2; q[4] = y2; q[5] = z2;
		q[6] = x3; q[7] = y3; q[8] = z3;
		q[9] = x4; q[10] = y4; q[11] = z4;
	}

	/**
	 * Frustum test of the world particle (dx, dy, dz: position relative to the camera). Particles close to the camera
	 * are always rendered (the first person FOV correction can move them into view).
	 */
	public boolean isVisible(Frustum frustum, double dx, double dy, double dz) {
		if (dx * dx + dy * dy + dz * dz < VISIBLE_DIST_SQ) {
			return true;
		}
		double r = 0.15 * Math.max(this.size, this.sizePrev) + 0.1;
		return frustum.isVisible(new AABB(Math.min(this.xo, this.x) - r, Math.min(this.yo, this.y) - r, Math.min(this.zo, this.z) - r,
				Math.max(this.xo, this.x) + r, Math.max(this.yo, this.y) + r, Math.max(this.zo, this.z) + r));
	}

	/**
	 * Squared distance under which particles are never frustum culled
	 */
	protected static final double VISIBLE_DIST_SQ = 6.0 * 6.0;

	/**
	 * Render type of the particle texture / blend mode (1.12: bindTexture + enableBlendMode)
	 */
	protected RenderType getTGRenderType() {
		return TGRenderHelper.getRenderType(type.renderType, type.texture);
	}

	protected boolean isFXFormat() {
		return TGRenderHelper.isFXFormat(type.renderType);
	}

	/**
	 * Writes one vertex: fx render types (TGRenderTypes.fxVertex) or the entity format (ALPHA_SHADED), both in the
	 * NEW_ENTITY element order. While world particles are rendered, the first person FOV correction
	 * (FirstPersonFXCorrection.particleK) is applied per vertex here, without wrapping the buffer.
	 */
	protected static void addVertex(VertexConsumer buffer, Matrix4f mat, boolean fxFormat, double x, double y, double z, float u, float v, float r, float g, float b, float a) {
		float k = FirstPersonFXCorrection.particleK;
		VertexConsumer vc;
		if (k != 1.0f) {
			vc = FirstPersonFXCorrection.vertex(buffer, mat, (float) x, (float) y, (float) z, k);
		} else {
			vc = buffer.vertex(mat, (float) x, (float) y, (float) z);
		}
		vc.color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fxFormat ? TGRenderTypes.FX_LIGHT : LIGHTMAP).normal(0.0f, 1.0f, 0.0f).endVertex();
	}

	/**
	 * interpolate colors and alpha values
	 */
	protected void preRenderStep(float progress) {

		/* ------------------------
		 * INTERPOLATE COLOR VALUES
		 */

		ColorEntry c1 = null;
		ColorEntry c2 = null;
		if (type.colorEntries.size()==0) {
			c1 = WHITE;
			c2 = c1;
		}else if (type.colorEntries.size() == 1) {
			c1 = type.colorEntries.get(0);
			c2 = c1;
		}else {
			c1 = type.colorEntries.get(0);
			for (int i = 1; i < type.colorEntries.size(); i++) {
				c2 = type.colorEntries.get(i);
				if (progress < c2.time) {
					break;
				}else {
					c1 = c2;
				}
			}
		}
		float p = (progress-c1.time) / (c2.time-c1.time);
		if (c1 != c2) {

			//RGB to HSB
			float[] hsb1 = c1.getHSB();
			float[] hsb2 = c2.getHSB();
			//HSB to RGB;
			int rgb = Color.HSBtoRGB(hsb1[0]*(1f-p) + hsb2[0]*p, hsb1[1]*(1f-p) + hsb2[1]*p, hsb1[2]*(1f-p) + hsb2[2]*p);
			this.rCol = (float)((rgb >> 16) & 0xFF) / 255.0f;
			this.gCol = (float)((rgb >> 8) & 0xFF) / 255.0f;
			this.bCol = (float)(rgb & 0xFF) / 255.0f;
		}else {
			this.rCol = (float)c1.r;
			this.gCol = (float)c1.g;
			this.bCol = (float)c1.b;
		}

		/*-------------------------
		 * INTERPOLATE ALPHA VALUES
		 */
		AlphaEntry a1 = null;
		AlphaEntry a2 = null;
		if (type.alphaEntries.size() == 0) {
			this.alpha = 1.0f;
		}else if (type.alphaEntries.size() == 1) {
			a1 = type.alphaEntries.get(0);
			this.alpha = a1.alpha;
		}else {
			a1 = type.alphaEntries.get(0);
			for (int i = 1; i < type.alphaEntries.size(); i++) {
				a2 = type.alphaEntries.get(i);
				if (progress < a2.time) {
					break;
				}else {
					a1 = a2;
				}
			}
			if (a1.time != a2.time) {
				p = (progress-a1.time) / (a2.time-a1.time);
				//interpolate
				this.alpha = a1.alpha*(1f-p) + a2.alpha * p;
			}else {
				this.alpha = a1.alpha;
			}
		}
	}

	@Override
	protected int getLightColor(float p_189214_1_)
	{
		return 15728880;
	}

	/**
	 * Not used, TGParticles are not added to the vanilla particle engine
	 */
	@Override
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.CUSTOM;
	}

	public double posX() {
		return x;
	}

	public double posY() {
		return y;
	}

	public double posZ() {
		return z;
	}


	protected Vec3 rotAxis(Vec3 p1, Vec3 axis, double sina, double cosa) {
		Vec3 v1 = axis.cross(p1);
		double d1 = axis.dot(p1);
		return p1.scale(cosa).add(v1.scale(sina)).add(axis.scale(d1*(1.0 - cosa)));
	}

	@Override
	public Vec3 getPos() {
		return new Vec3(this.x, this.y, this.z);
	}

	@Override
	public boolean shouldRemove() {
		return !this.isAlive();
	}

	@Override
	public void updateTick() {
		this.onUpdate();
	}

	@Override
	public void doRender(PoseStack poseStack, MultiBufferSource buffers, Entity entityIn, float partialTickTime, float rotX, float rotZ,
			float rotYZ, float rotXY, float rotXZ) {
		this.renderParticle(poseStack, buffers, entityIn, partialTickTime, rotX, rotZ, rotYZ, rotXY, rotXZ);
	}

	@Override
	public AABB getRenderBoundingBox(float partialTickTime, Entity viewEnt) {
		double fPosX = (this.x-viewEnt.getX());
		double fPosY = (this.y-viewEnt.getY());
		double fPosZ = (this.z-viewEnt.getZ());

		double s = size*0.5;
		return new AABB(fPosX-s, fPosY-s, fPosZ-s, fPosX+s, fPosY+s, fPosZ+s);
	}

	@Override
	public double getDepth() {
		return this.depth;
	}

	@Override
	public void setDepth(double depth) {
		this.depth=depth;
	}

	@Override
	public void setItemAttached() {
		this.itemAttached=true;
	}

	@Override
	public void remove() {
		super.remove();
		this.particleSystem=null;
	}

}
