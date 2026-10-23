package techguns.client.render.entities.projectiles;

import java.util.Random;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import techguns.client.render.TGRenderHelper;
import techguns.client.render.TGRenderHelper.RenderType;
import techguns.entities.projectiles.NDRProjectile;
import techguns.util.MathUtil;

public class RenderNDRProjectile extends EntityRenderer<NDRProjectile>{

	private static final ResourceLocation LaserTextures = new ResourceLocation("techguns:textures/fx/nukebeam.png");

	private double laserWidth = 3.0D;

	public RenderNDRProjectile(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	/**
	 * Beams can be long and are drawn from the shooter, always render them
	 */
	@Override
	public boolean shouldRender(NDRProjectile entity, Frustum frustum, double camX, double camY, double camZ) {
		return true;
	}

	@Override
	public void render(NDRProjectile entity, float entityYaw, float ptt, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
		 Random rand = new Random(entity.getId()); //laser.seed);
		 double distance = entity.getDistance();//source.distanceTo(target);
		 float prog = ((float)entity.tickCount+ptt)/((float)entity.getMaxTicks());
		 double width = laserWidth * (Math.sin(Math.sqrt(prog)*Math.PI))*2;

		 Minecraft mc = Minecraft.getInstance();
		 Entity e = entity.level().getEntity(entity.shooterID);
		 LivingEntity shooter = null;
		 if (e instanceof LivingEntity)
			shooter = (LivingEntity) e;

	     if (shooter == null) return;

	     /*
	      * 1.12 computed the beam origin relative to the viewer (shooter.pos + eye height - player.pos), the pose stack is at the
	      * interpolated position of the projectile here, so the origin is shooter (interpolated) + eye height - projectile (interpolated)
	      */
	     Vec3 shooterPos = shooter.getPosition(ptt);
	     double x = shooterPos.x - Mth.lerp(ptt, entity.xo, entity.getX());
         double y = shooterPos.y + shooter.getEyeHeight() - Mth.lerp(ptt, entity.yo, entity.getY());
         double z = shooterPos.z - Mth.lerp(ptt, entity.zo, entity.getZ());

         float pitch = MathUtil.interpolateRotation(shooter.xRotO, shooter.getXRot(), ptt);
         float yaw = MathUtil.interpolateRotation(shooter.yHeadRotO, shooter.getYHeadRot(), ptt);

         boolean firstPerson = shooter == mc.player && mc.options.getCameraType().isFirstPerson();

         if (!firstPerson) {
	         x -= (double)(Mth.cos(shooter.getYRot() / 180.0F * (float)Math.PI) * 0.16F);
	         y -=-0.10000000149011612D - entity.get3PYOffset();
	         z -= (double)(Mth.sin(shooter.getYRot() / 180.0F * (float)Math.PI) * 0.16F);
	    }

        poseStack.pushPose();
        poseStack.translate(x, y, z);

        TGRenderHelper.rotate(poseStack, -(yaw-90F), 0.0F, 1.0F, 0.0F);
        TGRenderHelper.rotate(poseStack, pitch, 0.0F, 0.0F, 1.0F);

        poseStack.translate(0, -0.08, -0.12);

         if (firstPerson) {
        	 if (mc.options.bobView().get())
             {
                 setupViewBobbing(poseStack, ptt);
             }
         }

         //Move forward
         poseStack.translate(-0.25, 0, 0);
         distance+= 0.25;

         float f10 = 0.0125F;

         distance*=80.0D;

         float rot_x = 45f+(prog*360f);

         TGRenderHelper.rotate(poseStack, rot_x+90f , 1.0F, 0.0F, 0.0F);

         poseStack.scale(f10, f10, f10);

         float brightness = (float) Math.sin(Math.sqrt(prog)*Math.PI);

         //---
         // RENDER BEAM
         double UVscale = 2.0D;
         int numFrames = 17;
         float frametime = 0.5f; //ticks per frame
         double u = distance / (laserWidth*8.0 * UVscale);

         int frame = (int) ((((float)entity.tickCount+ptt) * frametime) % numFrames);
         double v1 = (1.0D / (double)numFrames) * (double)frame;
         double v2 = (1.0D / (double)numFrames) * (double)(frame+1);

         // 1.12: additive blending, no culling (TGRenderTypes.fxAdditive)
         VertexConsumer vc = buffers.getBuffer(TGRenderHelper.getRenderType(RenderType.ADDITIVE, this.getTextureLocation(entity)));

         for (int i = 0; i < 2; ++i)
         {
        	 TGRenderHelper.rotate(poseStack, 90.0F, 1.0F, 0.0F, 0.0F);
        	 Matrix4f m = poseStack.last().pose();

             RenderGenericProjectile.vertex(vc, m, -distance, -width, 0.0D, 1.0f, 1.0f, 1.0f, brightness, 0+i, v1);
             RenderGenericProjectile.vertex(vc, m, 0, -width, 0.0D, 1.0f, 1.0f, 1.0f, brightness, u+i, v1);
             RenderGenericProjectile.vertex(vc, m, 0, width, 0.0D, 1.0f, 1.0f, 1.0f, brightness, u+i, v2);
             RenderGenericProjectile.vertex(vc, m, -distance, width, 0.0D, 1.0f, 1.0f, 1.0f, brightness, 0+i, v2);
         }
         //-------

         //Render Spiral
         //---
         distance = Math.min(distance, 400.0); //SHORTEN LENGTH FOR SPIRAL
         int segments = (int) (distance/8.0);
         double w = 4.0; //width/2
         double radius = 0.0;
         double angle = Math.PI/8.0;
         double prevX = 0.0;
         double prevY = 0.0;
         double prevZ = 0.0;
         double prevu = 0.0;
         double cos2 = Math.cos((Math.PI-angle)*0.5)*2.0;
         double angleOffset = (rand.nextDouble()*Math.PI*2.0);
         UVscale = 8.0;
         //-
         double d = distance/segments;
         Matrix4f m = poseStack.last().pose();
         for (int i = 0; i < segments; i++) {
        	 x = -d*i;

        	 float iprog =  (float)i / (float)segments;
        	 radius = 0.5 * (1-Math.cos(2.0*Math.sqrt(iprog)*Math.PI)) * 8.0;

        	 Vec2 yz = MathUtil.polarOffsetXZ(0, 0, (float)radius, (float)(angleOffset-angle*i));
        	 y = yz.x;
        	 z = yz.y;
        	 u = prevu+(cos2*radius/UVscale);

        	 RenderGenericProjectile.vertex(vc, m, prevX-w, prevY, prevZ, 1.0f, 1.0f, 1.0f, brightness, prevu, v2);
        	 RenderGenericProjectile.vertex(vc, m, x-w, y, z, 1.0f, 1.0f, 1.0f, brightness, u, v2);
        	 RenderGenericProjectile.vertex(vc, m, x+w, y, z, 1.0f, 1.0f, 1.0f, brightness, u, v1);
        	 RenderGenericProjectile.vertex(vc, m, prevX+w, prevY, prevZ, 1.0f, 1.0f, 1.0f, brightness, prevu, v1);

             prevX = x;
             prevY = y;
             prevZ = z;
             prevu = u;
         }

         poseStack.popPose();
	}

	/**
	 * 1.12 setupViewBobbing with inverted translation (F1 = F2 = -1). 1.20 has no camera pitch bobbing (the last rotation of 1.12).
	 */
	private void setupViewBobbing(PoseStack poseStack, float p_78475_1_)
    {
        if (Minecraft.getInstance().getCameraEntity() instanceof Player)
        {
            Player entityplayer = (Player)Minecraft.getInstance().getCameraEntity();
            float f1 = entityplayer.walkDist - entityplayer.walkDistO;
            float f2 = -(entityplayer.walkDist + f1 * p_78475_1_);
            float f3 = Mth.lerp(p_78475_1_, entityplayer.oBob, entityplayer.bob);

            float F1 = -1.0f; //(float) Keybinds.X;
            float F2 = -1.0f; //(float) Keybinds.Y;

            poseStack.translate(Mth.sin(f2 * (float)Math.PI) * f3 * 0.5F * F1, -Math.abs(Mth.cos(f2 * (float)Math.PI) * f3) * F2, 0.0F);
            TGRenderHelper.rotate(poseStack, Mth.sin(f2 * (float)Math.PI) * f3 * 3.0F, 0.0F, 0.0F, 1.0F);
            TGRenderHelper.rotate(poseStack, Math.abs(Mth.cos(f2 * (float)Math.PI - 0.2F) * f3) * 5.0F, 1.0F, 0.0F, 0.0F);
        }
    }

	@Override
	public ResourceLocation getTextureLocation(NDRProjectile entity) {
		return LaserTextures;
	}

}
