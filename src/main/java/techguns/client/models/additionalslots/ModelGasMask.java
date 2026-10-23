package techguns.client.models.additionalslots;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.entity.player.Player;
import techguns.client.models.TGModelRenderer;

public class ModelGasMask extends ModelAdditionalSlotBase {
	// fields
	TGModelRenderer Head;
	TGModelRenderer Front;
	TGModelRenderer FilterMount;
	TGModelRenderer Filter;

	public ModelGasMask() {
		textureWidth = 32;
		textureHeight = 32;

		this.bipedHead = new TGModelRenderer(this, textureWidth, textureHeight);
		this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);

		Head = new TGModelRenderer(this, 0, 0);
		Head.addBox(-4F, -8F, -4F, 8, 8, 8);
		Head.setRotationPoint(0F, 0F, 0F);
		Head.setTextureSize(32, 32);
		Head.mirror = true;
		setRotation(Head, 0F, 0F, 0F);
		this.bipedHead.addChild(Head);

		Front = new TGModelRenderer(this, 9, 17);
		Front.addBox(0F, 0F, 0F, 4, 3, 1);
		Front.setRotationPoint(-2F, -3F, -5F);
		Front.setTextureSize(32, 32);
		Front.mirror = true;
		setRotation(Front, 0F, 0F, 0F);
		this.bipedHead.addChild(Front);

		FilterMount = new TGModelRenderer(this, 0, 17);
		FilterMount.addBox(0F, 0F, 0F, 3, 3, 1);
		FilterMount.setRotationPoint(-1.5F, -2.2F, -8F);
		FilterMount.setTextureSize(32, 32);
		FilterMount.mirror = true;
		setRotation(FilterMount, 0.3346075F, 0F, 0F);
		this.bipedHead.addChild(FilterMount);

		Filter = new TGModelRenderer(this, 20, 17);
		Filter.addBox(-1F, -1F, 0F, 2, 2, 2);
		Filter.setRotationPoint(0F, -0.9F, -7F);
		Filter.setTextureSize(32, 32);
		Filter.mirror = true;
		setRotation(Filter, 0.3490659F, -0.2268928F, 0.7853982F);
		this.bipedHead.addChild(Filter);
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale, Player entityIn) {
		this.bipedHead.render(poseStack, buffer, packedLight, packedOverlay, scale);
	}

	@Override
	public void copyRotateAngles() {
	}
}
