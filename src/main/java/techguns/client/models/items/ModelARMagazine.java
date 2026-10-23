package techguns.client.models.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import techguns.client.models.ModelMultipart;

public class ModelARMagazine extends ModelMultipart {

	// fields
	TGModelRenderer Magazine02;
	TGModelRenderer Magazine01;
	TGModelRenderer Magazine03;
	TGModelRenderer Magazine04;

	protected boolean empty=false;
	
	public ModelARMagazine(boolean empty) {
		textureWidth = 32;
		textureHeight = 32;

		this.empty=empty;
		
		Magazine02 = new TGModelRenderer(this, 0, 0);
		Magazine02.addBox(0F, 0F, 0F, 2, 4, 6);
		Magazine02.setRotationPoint(-1.5F, 9F, -4.4F);
		Magazine02.setTextureSize(128, 64);
		Magazine02.mirror = true;
		setRotation(Magazine02, -0.1570796F, 0F, 0F);
		Magazine01 = new TGModelRenderer(this, 0, 11);
		Magazine01.addBox(0F, 0F, 0F, 2, 5, 6);
		Magazine01.setRotationPoint(-1.5F, 5F, -4.5F);
		Magazine01.setTextureSize(128, 64);
		Magazine01.mirror = true;
		setRotation(Magazine01, 0F, 0F, 0F);
		Magazine03 = new TGModelRenderer(this, 0, 11);
		Magazine03.addBox(0F, 0F, 0F, 2, 2, 6);
		Magazine03.setRotationPoint(-1.5F, 3F, -4.5F);
		Magazine03.setTextureSize(128, 64);
		Magazine03.mirror = true;
		setRotation(Magazine03, 0F, 0F, 0F);
		
		if (!empty){
			Magazine04 = new TGModelRenderer(this, 0, 22);
			Magazine04.addBox(0F, 0F, 0F, 1, 1, 5);
			Magazine04.setRotationPoint(-1.25F, 3F, -4F);
			Magazine04.setTextureSize(128, 64);
			Magazine04.mirror = true;
			setRotation(Magazine04, 0F, 0F, -0.7853982F);
		}
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, int ammoLeft,
			float reloadProgress, ItemDisplayContext transformType, int part, float fireProgress, float chargeProgress) {
		Magazine02.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Magazine01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Magazine03.render(poseStack, buffer, packedLight, packedOverlay, scale);
		if (!empty){
			Magazine04.render(poseStack, buffer, packedLight, packedOverlay, scale);
		}
	}

}
