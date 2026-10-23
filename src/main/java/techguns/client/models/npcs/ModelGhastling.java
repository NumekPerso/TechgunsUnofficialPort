package techguns.client.models.npcs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import techguns.client.models.TGModelBase;
import techguns.client.models.TGModelRenderer;

public class ModelGhastling extends TGModelBase implements ITGEntityModel {

	TGModelRenderer body;
	TGModelRenderer[] tentacles = new TGModelRenderer[9];

	public ModelGhastling() {
		this.body = new TGModelRenderer(this, 0, 0);
		this.body.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
		this.body.rotationPointY += 8.0F;
		Random random = new Random(1660L);

		for (int j = 0; j < this.tentacles.length; ++j) {
			this.tentacles[j] = new TGModelRenderer(this, 0, 0);
			float f = (((float) (j % 3) - (float) (j / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
			float f1 = ((float) (j / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
			int k = random.nextInt(7) + 8;
			this.tentacles[j].addBox(-1.0F, 0.0F, -1.0F, 2, k, 2);
			this.tentacles[j].rotationPointX = f;
			this.tentacles[j].rotationPointZ = f1;
			this.tentacles[j].rotationPointY = 15.0F;
		}
	}

	@Override
	public List<TGModelRenderer> getRenderedParts() {
		List<TGModelRenderer> parts = new ArrayList<>();
		parts.add(this.body);
		for (TGModelRenderer t : this.tentacles) {
			parts.add(t);
		}
		return parts;
	}

	/**
	 * 1.12 render: translate(0, -0.6, 0)
	 */
	@Override
	public float getRenderOffsetY() {
		return -0.6f;
	}

	/**
	 * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
	 * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
	 * "far" arms and legs can swing at most.
	 */
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		for (int i = 0; i < this.tentacles.length; ++i) {
			this.tentacles[i].rotateAngleX = 0.2F * Mth.sin(ageInTicks * 0.3F + (float) i) + 0.4F;
		}
	}
}
