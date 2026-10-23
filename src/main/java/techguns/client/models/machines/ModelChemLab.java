package techguns.client.models.machines;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelRenderer;

/**
 * 1.12 ModelChemLab. The glass parts (G1-G11) were rendered with alpha blending: they are rendered separately by
 * renderGlass with a translucent render type (see RenderChemLab).
 */
public class ModelChemLab extends ModelMachine{

	//fields
    TGModelRenderer Shape1;
    TGModelRenderer Shape2;
    TGModelRenderer Shape3;
    TGModelRenderer G10;
    TGModelRenderer Shape4;
    TGModelRenderer Shape5;
    TGModelRenderer Shape6;
    TGModelRenderer Shape7;
    TGModelRenderer L8;
    TGModelRenderer Shape8;
    TGModelRenderer Shape9;
    TGModelRenderer Shape10;
    TGModelRenderer Shape11;
    TGModelRenderer Shape12;
    TGModelRenderer Shape13;
    TGModelRenderer Shape14;
    TGModelRenderer Shape15;
    TGModelRenderer Shape16;
    TGModelRenderer Shape17;
    TGModelRenderer Shape18;
    TGModelRenderer Shape19;
    TGModelRenderer Shape20;
    TGModelRenderer Shape21;
    TGModelRenderer Shape22;
    TGModelRenderer Shape23;
    TGModelRenderer G3;
    TGModelRenderer G9;
    TGModelRenderer L5;
    TGModelRenderer L4;
    TGModelRenderer Shape24;
    TGModelRenderer Shape25;
    TGModelRenderer Shape26;
    TGModelRenderer G8;
    TGModelRenderer Shape27;
    TGModelRenderer Shape28;
    TGModelRenderer Shape29;
    TGModelRenderer Shape30;
    TGModelRenderer Shape31;
    TGModelRenderer Shape32;
    TGModelRenderer Shape33;
    TGModelRenderer G5;
    TGModelRenderer G4;
    TGModelRenderer L3;
    TGModelRenderer G2;
    TGModelRenderer Shape34;
    TGModelRenderer Shape35;
    TGModelRenderer Shape36;
    TGModelRenderer Shape37;
    TGModelRenderer Shape38;
    TGModelRenderer Shape39;
    TGModelRenderer Shape40;
    TGModelRenderer Shape41;
    TGModelRenderer Shape42;
    TGModelRenderer Shape43;
    TGModelRenderer Shape44;
    TGModelRenderer Shape45;
    TGModelRenderer G1;
    TGModelRenderer L1;
    TGModelRenderer L2;
    TGModelRenderer G7;
    TGModelRenderer G6;
    TGModelRenderer G11;
    TGModelRenderer L7;
    TGModelRenderer Shape46;
  
  public ModelChemLab()
  {
    textureWidth = 128;
    textureHeight = 64;
    
      Shape1 = new TGModelRenderer(this, 0, 0);
      Shape1.addBox(0F, 0F, 0F, 16, 2, 16);
      Shape1.setRotationPoint(-8F, 22F, -8F);
      Shape1.setTextureSize(128, 64);
      Shape1.mirror = true;
      setRotation(Shape1, 0F, 0F, 0F);
      Shape2 = new TGModelRenderer(this, 65, 5);
      Shape2.addBox(0F, 0F, 0F, 6, 6, 1);
      Shape2.setRotationPoint(-3F, 13F, -8F);
      Shape2.setTextureSize(128, 64);
      Shape2.mirror = true;
      setRotation(Shape2, 0F, 0F, 0F);
      Shape3 = new TGModelRenderer(this, 0, 18);
      Shape3.addBox(-2F, -2F, -2F, 5, 5, 5);
      Shape3.setRotationPoint(-5.5F, 15.5F, -0.5F);
      Shape3.setTextureSize(128, 64);
      Shape3.mirror = true;
      setRotation(Shape3, 0F, 0F, 0F);
      G10 = new TGModelRenderer(this, 41, 22);
      G10.addBox(0F, 0F, 0F, 4, 9, 4);
      G10.setRotationPoint(-7F, 12F, -7F);
      G10.setTextureSize(128, 64);
      G10.mirror = true;
      setRotation(G10, 0F, 0F, 0F);
      Shape4 = new TGModelRenderer(this, 0, 29);
      Shape4.addBox(0F, 0F, 0F, 6, 1, 6);
      Shape4.setRotationPoint(-3F, 21F, -2.5F);
      Shape4.setTextureSize(128, 64);
      Shape4.mirror = true;
      setRotation(Shape4, 0F, 0F, 0F);
      Shape5 = new TGModelRenderer(this, 57, 29);
      Shape5.addBox(0F, 0F, 0F, 5, 1, 5);
      Shape5.setRotationPoint(-7.5F, 21F, -7.5F);
      Shape5.setTextureSize(128, 64);
      Shape5.mirror = true;
      setRotation(Shape5, 0F, 0F, 0F);
      Shape6 = new TGModelRenderer(this, 79, 23);
      Shape6.addBox(0F, 0F, 0F, 2, 3, 2);
      Shape6.setRotationPoint(-1F, 10.5F, -6F);
      Shape6.setTextureSize(128, 64);
      Shape6.mirror = true;
      setRotation(Shape6, 0F, 0F, 0F);
      Shape7 = new TGModelRenderer(this, 50, 0);
      Shape7.addBox(-2F, -2F, -2F, 1, 6, 6);
      Shape7.setRotationPoint(-6F, 15F, -1F);
      Shape7.setTextureSize(128, 64);
      Shape7.mirror = true;
      setRotation(Shape7, 0F, 0F, 0F);
      L8 = new TGModelRenderer(this, 114, 27);
      L8.addBox(0F, 0F, 0F, 3, 1, 3);
      L8.setRotationPoint(-6.5F, 19.5F, -6.5F);
      L8.setTextureSize(128, 64);
      L8.mirror = true;
      setRotation(L8, 0F, 0F, 0F);
      Shape8 = new TGModelRenderer(this, 41, 18);
      Shape8.addBox(0F, 0F, 0F, 3, 1, 3);
      Shape8.setRotationPoint(-1.5F, 13F, -6.5F);
      Shape8.setTextureSize(128, 64);
      Shape8.mirror = true;
      setRotation(Shape8, 0F, 0F, 0F);
      Shape9 = new TGModelRenderer(this, 21, 33);
      Shape9.addBox(0F, 0F, 0F, 5, 9, 5);
      Shape9.setRotationPoint(-2.5F, 13.5F, -7F);
      Shape9.setTextureSize(128, 64);
      Shape9.mirror = true;
      setRotation(Shape9, 0F, 0F, 0F);
      Shape10 = new TGModelRenderer(this, 0, 38);
      Shape10.addBox(0F, 0F, 0F, 1, 1, 1);
      Shape10.setRotationPoint(2.5F, 21F, 3F);
      Shape10.setTextureSize(128, 64);
      Shape10.mirror = true;
      setRotation(Shape10, 0F, 0F, 0F);
      Shape11 = new TGModelRenderer(this, 57, 29);
      Shape11.addBox(0F, 0F, 0F, 5, 1, 5);
      Shape11.setRotationPoint(2.5F, 21F, -7.5F);
      Shape11.setTextureSize(128, 64);
      Shape11.mirror = true;
      setRotation(Shape11, 0F, 0F, 0F);
      Shape12 = new TGModelRenderer(this, 50, 0);
      Shape12.addBox(-2F, -2F, -2F, 1, 6, 6);
      Shape12.setRotationPoint(9F, 15F, -1F);
      Shape12.setTextureSize(128, 64);
      setRotation(Shape12, 0F, 0F, 0F);
      Shape12.mirror = false;
      Shape13 = new TGModelRenderer(this, 0, 18);
      Shape13.addBox(-2F, -2F, -2F, 5, 5, 5);
      Shape13.setRotationPoint(4.5F, 15.5F, -0.5F);
      Shape13.setTextureSize(128, 64);
      Shape13.mirror = true;
      setRotation(Shape13, 0F, 0F, 0F);
      Shape14 = new TGModelRenderer(this, 51, 18);
      Shape14.addBox(0F, 0F, 0F, 1, 2, 2);
      Shape14.setRotationPoint(-3F, 10.5F, -1F);
      Shape14.setTextureSize(128, 64);
      Shape14.mirror = true;
      setRotation(Shape14, 0F, 0F, 0F);
      Shape15 = new TGModelRenderer(this, 79, 18);
      Shape15.addBox(0F, 0F, 0F, 12, 2, 2);
      Shape15.setRotationPoint(-6F, 8.5F, -6F);
      Shape15.setTextureSize(128, 64);
      Shape15.mirror = true;
      setRotation(Shape15, 0F, 0F, 0F);
      Shape16 = new TGModelRenderer(this, 88, 23);
      Shape16.addBox(0F, 0F, 0F, 2, 1, 2);
      Shape16.setRotationPoint(-6F, 10.5F, -6F);
      Shape16.setTextureSize(128, 64);
      Shape16.mirror = true;
      setRotation(Shape16, 0F, 0F, 0F);
      Shape17 = new TGModelRenderer(this, 41, 18);
      Shape17.addBox(0F, 0F, 0F, 3, 1, 3);
      Shape17.setRotationPoint(3.5F, 11F, -6.5F);
      Shape17.setTextureSize(128, 64);
      Shape17.mirror = true;
      setRotation(Shape17, 0F, 0F, 0F);
      Shape18 = new TGModelRenderer(this, 88, 23);
      Shape18.addBox(0F, 0F, 0F, 2, 1, 2);
      Shape18.setRotationPoint(4F, 10.5F, -6F);
      Shape18.setTextureSize(128, 64);
      Shape18.mirror = true;
      setRotation(Shape18, 0F, 0F, 0F);
      Shape19 = new TGModelRenderer(this, 1, 30);
      Shape19.addBox(0F, 0F, 0F, 1, 3, 1);
      Shape19.setRotationPoint(-2.5F, 18.5F, 1.5F);
      Shape19.setTextureSize(128, 64);
      Shape19.mirror = true;
      setRotation(Shape19, 0F, 0F, 0F);
      Shape20 = new TGModelRenderer(this, 80, 5);
      Shape20.addBox(0F, 0F, 0F, 6, 1, 6);
      Shape20.setRotationPoint(-3F, 8F, -3F);
      Shape20.setTextureSize(128, 64);
      Shape20.mirror = true;
      setRotation(Shape20, 0F, 0F, 0F);
      Shape21 = new TGModelRenderer(this, 21, 18);
      Shape21.addBox(0F, 0F, 0F, 5, 10, 5);
      Shape21.setRotationPoint(-2.5F, 8.5F, -2.5F);
      Shape21.setTextureSize(128, 64);
      Shape21.mirror = true;
      setRotation(Shape21, 0F, 0F, 0F);
      Shape22 = new TGModelRenderer(this, 106, 0);
      Shape22.addBox(0F, 0F, 0F, 3, 1, 1);
      Shape22.setRotationPoint(-5.5F, 11F, -0.5F);
      Shape22.setTextureSize(128, 64);
      Shape22.mirror = true;
      setRotation(Shape22, 0F, 0F, 0F);
      Shape23 = new TGModelRenderer(this, 0, 37);
      Shape23.addBox(0F, 0F, 0F, 5, 1, 5);
      Shape23.setRotationPoint(2.5F, 20F, 3F);
      Shape23.setTextureSize(128, 64);
      Shape23.mirror = true;
      setRotation(Shape23, 0F, 0F, 0F);
      G3 = new TGModelRenderer(this, 87, 41);
      G3.addBox(-1F, 0F, -1F, 2, 9, 2);
      G3.setRotationPoint(-6F, 12F, 5.5F);
      G3.setTextureSize(128, 64);
      G3.mirror = true;
      setRotation(G3, 0F, 0.7853982F, 0F);
      G9 = new TGModelRenderer(this, 12, 55);
      G9.addBox(0F, 0F, 0F, 3, 1, 3);
      G9.setRotationPoint(3.5F, 19F, 4F);
      G9.setTextureSize(128, 64);
      G9.mirror = true;
      setRotation(G9, 0F, 0F, 0F);
      L5 = new TGModelRenderer(this, 107, 43);
      L5.addBox(-1F, 0F, -1F, 2, 1, 2);
      L5.setRotationPoint(5F, 16F, 5.5F);
      L5.setTextureSize(128, 64);
      L5.mirror = true;
      setRotation(L5, 0F, 0F, 0F);
      L4 = new TGModelRenderer(this, 103, 46);
      L4.addBox(0F, 0F, 0F, 3, 2, 3);
      L4.setRotationPoint(3.5F, 17F, 4F);
      L4.setTextureSize(128, 64);
      L4.mirror = true;
      setRotation(L4, 0F, 0F, 0F);
      Shape24 = new TGModelRenderer(this, 81, 38);
      Shape24.addBox(-1F, 0F, -1F, 2, 1, 2);
      Shape24.setRotationPoint(-6F, 11F, 5.5F);
      Shape24.setTextureSize(128, 64);
      Shape24.mirror = true;
      setRotation(Shape24, 0F, 0F, 0F);
      Shape25 = new TGModelRenderer(this, 106, 0);
      Shape25.addBox(0F, 0F, 0F, 1, 2, 1);
      Shape25.setRotationPoint(4.5F, 10F, 5F);
      Shape25.setTextureSize(128, 64);
      Shape25.mirror = true;
      setRotation(Shape25, 0F, 0F, 0F);
      Shape26 = new TGModelRenderer(this, 1, 30);
      Shape26.addBox(0F, 0F, 0F, 1, 3, 1);
      Shape26.setRotationPoint(1.5F, 18.5F, 1.5F);
      Shape26.setTextureSize(128, 64);
      Shape26.mirror = true;
      setRotation(Shape26, 0F, 0F, 0F);
      G8 = new TGModelRenderer(this, 0, 55);
      G8.addBox(0F, 0F, 0F, 4, 2, 4);
      G8.setRotationPoint(3F, 17F, 3.5F);
      G8.setTextureSize(128, 64);
      G8.mirror = true;
      setRotation(G8, 0F, 0F, 0F);
      Shape27 = new TGModelRenderer(this, 50, 41);
      Shape27.addBox(0F, 0F, 0F, 10, 10, 0);
      Shape27.setRotationPoint(-8F, 12F, 7F);
      Shape27.setTextureSize(128, 64);
      Shape27.mirror = true;
      setRotation(Shape27, 0F, 0F, 0F);
      Shape28 = new TGModelRenderer(this, 0, 38);
      Shape28.addBox(0F, 0F, 0F, 1, 1, 1);
      Shape28.setRotationPoint(6.5F, 21F, 3F);
      Shape28.setTextureSize(128, 64);
      Shape28.mirror = true;
      setRotation(Shape28, 0F, 0F, 0F);
      Shape29 = new TGModelRenderer(this, 0, 38);
      Shape29.addBox(0F, 0F, 0F, 1, 1, 1);
      Shape29.setRotationPoint(6.5F, 21F, 7F);
      Shape29.setTextureSize(128, 64);
      Shape29.mirror = true;
      setRotation(Shape29, 0F, 0F, 0F);
      Shape30 = new TGModelRenderer(this, 0, 38);
      Shape30.addBox(0F, 0F, 0F, 1, 1, 1);
      Shape30.setRotationPoint(2.5F, 21F, 7F);
      Shape30.setTextureSize(128, 64);
      Shape30.mirror = true;
      setRotation(Shape30, 0F, 0F, 0F);
      Shape31 = new TGModelRenderer(this, 50, 37);
      Shape31.addBox(0F, 0F, 0F, 10, 0, 3);
      Shape31.setRotationPoint(-8F, 12F, 4F);
      Shape31.setTextureSize(128, 64);
      Shape31.mirror = true;
      setRotation(Shape31, 0F, 0F, 0F);
      Shape32 = new TGModelRenderer(this, 43, 37);
      Shape32.addBox(0F, 0F, 0F, 0, 10, 3);
      Shape32.setRotationPoint(2F, 12F, 4F);
      Shape32.setTextureSize(128, 64);
      Shape32.mirror = true;
      setRotation(Shape32, 0F, 0F, 0F);
      Shape33 = new TGModelRenderer(this, 43, 37);
      Shape33.addBox(0F, 0F, 0F, 0, 10, 3);
      Shape33.setRotationPoint(-8F, 12F, 4F);
      Shape33.setTextureSize(128, 64);
      Shape33.mirror = true;
      setRotation(Shape33, 0F, 0F, 0F);
      G5 = new TGModelRenderer(this, 8, 45);
      G5.addBox(-0.5F, 0F, -0.5F, 1, 1, 1);
      G5.setRotationPoint(5F, 13F, 5.5F);
      G5.setTextureSize(128, 64);
      G5.mirror = true;
      setRotation(G5, 0F, 0.7853982F, 0F);
      G4 = new TGModelRenderer(this, 0, 44);
      G4.addBox(-1F, 0F, -1F, 2, 1, 2);
      G4.setRotationPoint(5F, 12F, 5.5F);
      G4.setTextureSize(128, 64);
      G4.mirror = true;
      setRotation(G4, 0F, 0F, 0F);
      L3 = new TGModelRenderer(this, 123, 47);
      L3.addBox(-0.5F, 0F, -0.5F, 1, 1, 1);
      L3.setRotationPoint(-6F, 19.5F, 5.5F);
      L3.setTextureSize(128, 64);
      L3.mirror = true;
      setRotation(L3, 0F, 0.7853982F, 0F);
      G2 = new TGModelRenderer(this, 79, 41);
      G2.addBox(-1F, 0F, -1F, 2, 9, 2);
      G2.setRotationPoint(-3F, 12F, 5.5F);
      G2.setTextureSize(128, 64);
      G2.mirror = true;
      setRotation(G2, 0F, 0.7853982F, 0F);
      Shape34 = new TGModelRenderer(this, 81, 32);
      Shape34.addBox(-1F, 0F, -1F, 2, 1, 2);
      Shape34.setRotationPoint(0F, 11F, 5.5F);
      Shape34.setTextureSize(128, 64);
      Shape34.mirror = true;
      setRotation(Shape34, 0F, 0F, 0F);
      Shape35 = new TGModelRenderer(this, 81, 35);
      Shape35.addBox(-1F, 0F, -1F, 2, 1, 2);
      Shape35.setRotationPoint(-3F, 11F, 5.5F);
      Shape35.setTextureSize(128, 64);
      Shape35.mirror = true;
      setRotation(Shape35, 0F, 0F, 0F);
      Shape36 = new TGModelRenderer(this, 41, 18);
      Shape36.addBox(0F, 0F, 0F, 3, 1, 3);
      Shape36.setRotationPoint(-6.5F, 11F, -6.5F);
      Shape36.setTextureSize(128, 64);
      Shape36.mirror = true;
      setRotation(Shape36, 0F, 0F, 0F);
      Shape37 = new TGModelRenderer(this, 106, 0);
      Shape37.addBox(0F, 0F, 0F, 1, 1, 10);
      Shape37.setRotationPoint(4.5F, 9F, -4F);
      Shape37.setTextureSize(128, 64);
      Shape37.mirror = true;
      setRotation(Shape37, 0F, 0F, 0F);
      Shape38 = new TGModelRenderer(this, 106, 0);
      Shape38.addBox(0F, 0F, 0F, 1, 1, 1);
      Shape38.setRotationPoint(-0.5F, 10F, 5F);
      Shape38.setTextureSize(128, 64);
      Shape38.mirror = true;
      setRotation(Shape38, 0F, 0F, 0F);
      Shape39 = new TGModelRenderer(this, 106, 0);
      Shape39.addBox(0F, 0F, 0F, 1, 1, 1);
      Shape39.setRotationPoint(-5.5F, 10F, -0.5F);
      Shape39.setTextureSize(128, 64);
      Shape39.mirror = true;
      setRotation(Shape39, 0F, 0F, 0F);
      Shape40 = new TGModelRenderer(this, 106, 0);
      Shape40.addBox(0F, 0F, 0F, 1, 1, 5);
      Shape40.setRotationPoint(-5.5F, 9F, -4.5F);
      Shape40.setTextureSize(128, 64);
      Shape40.mirror = true;
      setRotation(Shape40, 0F, 0F, 0F);
      Shape41 = new TGModelRenderer(this, 105, 3);
      Shape41.addBox(0F, 0F, 0F, 2, 1, 1);
      Shape41.setRotationPoint(-7.5F, 9F, -0.5F);
      Shape41.setTextureSize(128, 64);
      Shape41.mirror = true;
      setRotation(Shape41, 0F, 0F, 0F);
      Shape42 = new TGModelRenderer(this, 106, 0);
      Shape42.addBox(0F, 0F, 0F, 1, 1, 6);
      Shape42.setRotationPoint(-7.5F, 9F, 0F);
      Shape42.setTextureSize(128, 64);
      Shape42.mirror = true;
      setRotation(Shape42, 0F, 0F, 0F);
      Shape43 = new TGModelRenderer(this, 106, 0);
      Shape43.addBox(0F, 0F, 0F, 7, 1, 1);
      Shape43.setRotationPoint(-6.5F, 9F, 5F);
      Shape43.setTextureSize(128, 64);
      Shape43.mirror = true;
      setRotation(Shape43, 0F, 0F, 0F);
      Shape44 = new TGModelRenderer(this, 106, 0);
      Shape44.addBox(0F, 0F, 0F, 1, 1, 1);
      Shape44.setRotationPoint(-6.5F, 10F, 5F);
      Shape44.setTextureSize(128, 64);
      Shape44.mirror = true;
      setRotation(Shape44, 0F, 0F, 0F);
      Shape45 = new TGModelRenderer(this, 106, 0);
      Shape45.addBox(0F, 0F, 0F, 1, 1, 1);
      Shape45.setRotationPoint(-3.5F, 10F, 5F);
      Shape45.setTextureSize(128, 64);
      Shape45.mirror = true;
      setRotation(Shape45, 0F, 0F, 0F);
      G1 = new TGModelRenderer(this, 71, 41);
      G1.addBox(-1F, 0F, -1F, 2, 9, 2);
      G1.setRotationPoint(0F, 12F, 5.5F);
      G1.setTextureSize(128, 64);
      G1.mirror = true;
      setRotation(G1, 0F, 0.7853982F, 0F);
      L1 = new TGModelRenderer(this, 123, 41);
      L1.addBox(-0.5F, 0F, -0.5F, 1, 1, 1);
      L1.setRotationPoint(0F, 19.5F, 5.5F);
      L1.setTextureSize(128, 64);
      L1.mirror = true;
      setRotation(L1, 0F, 0.7853982F, 0F);
      L2 = new TGModelRenderer(this, 123, 44);
      L2.addBox(-0.5F, 0F, -0.5F, 1, 1, 1);
      L2.setRotationPoint(-3F, 19.5F, 5.5F);
      L2.setTextureSize(128, 64);
      L2.mirror = true;
      setRotation(L2, 0F, 0.7853982F, 0F);
      G7 = new TGModelRenderer(this, 0, 50);
      G7.addBox(0F, 0F, 0F, 3, 2, 3);
      G7.setRotationPoint(3.5F, 15F, 4F);
      G7.setTextureSize(128, 64);
      G7.mirror = true;
      setRotation(G7, 0F, 0F, 0F);
      G6 = new TGModelRenderer(this, 0, 47);
      G6.addBox(-1F, 0F, -1F, 2, 1, 2);
      G6.setRotationPoint(5F, 14F, 5.5F);
      G6.setTextureSize(128, 64);
      G6.mirror = true;
      setRotation(G6, 0F, 0F, 0F);
      G11 = new TGModelRenderer(this, 41, 22);
      G11.addBox(0F, 0F, 0F, 4, 9, 4);
      G11.setRotationPoint(3F, 12F, -7F);
      G11.setTextureSize(128, 64);
      G11.mirror = true;
      setRotation(G11, 0F, 0F, 0F);
      L7 = new TGModelRenderer(this, 114, 22);
      L7.addBox(0F, 0F, 0F, 3, 1, 3);
      L7.setRotationPoint(3.5F, 19.5F, -6.5F);
      L7.setTextureSize(128, 64);
      L7.mirror = true;
      setRotation(L7, 0F, 0F, 0F);
      Shape46 = new TGModelRenderer(this, 50, 41);
      Shape46.addBox(0F, 0F, 0F, 10, 10, 0);
      Shape46.setRotationPoint(-8F, 12F, 4F);
      Shape46.setTextureSize(128, 64);
      Shape46.mirror = true;
      setRotation(Shape46, 0F, 0F, 0F);
  }
	

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float f5, float progress) {
		TGModelRenderer[] shapes = { Shape1, Shape2, Shape3, Shape4, Shape5, Shape6, Shape7, Shape8, Shape9, Shape10, Shape11, Shape12, Shape13, Shape14,
				Shape15, Shape16, Shape17, Shape18, Shape19, Shape20, Shape21, Shape22, Shape23, Shape24, Shape25, Shape26, Shape27, Shape28, Shape29,
				Shape30, Shape31, Shape32, Shape33, Shape34, Shape35, Shape36, Shape37, Shape38, Shape39, Shape40, Shape41, Shape42, Shape43, Shape44,
				Shape45, Shape46 };
		for (TGModelRenderer s : shapes) {
			s.render(poseStack, buffer, packedLight, packedOverlay, f5);
		}

		if (progress > 0) {
			//Liquid in bottle
			L4.render(poseStack, buffer, packedLight, packedOverlay, f5);
			L5.render(poseStack, buffer, packedLight, packedOverlay, f5);

			//3 glasses
			if (progress > 0.20f) { // < 0.4f;
				renderGlassLiquid(L1, poseStack, buffer, packedLight, packedOverlay, f5, progress, 0.2f);
			}
			if (progress > 0.40f) { // < 0.6f;
				renderGlassLiquid(L2, poseStack, buffer, packedLight, packedOverlay, f5, progress, 0.4f);
			}
			if (progress > 0.60f) { // < 0.8f;
				renderGlassLiquid(L3, poseStack, buffer, packedLight, packedOverlay, f5, progress, 0.6f);
			}

			//tanks
			float prog1 = 1f;
			float prog2 = 0f;

			if (progress < (0.333f)) {
				prog1 = 1.0f - Math.min(1.0f, (progress) * 3f);
			} else if (progress < 0.666f) {
				prog1 = 0f;
			} else {
				prog1 = 0f;
				prog2 = Math.min(1.0f, (progress - 0.666f) * 3f);
			}

			int count1 = Math.round(prog1 * 16f); //16f = height of glass
			int count2 = Math.round(prog2 * 16f);

			poseStack.pushPose();
			for (int i = 0; i < 16; i++) {
				if (count1 > i) L7.render(poseStack, buffer, packedLight, packedOverlay, f5);
				if (count2 > i) L8.render(poseStack, buffer, packedLight, packedOverlay, f5);
				poseStack.translate(0, -0.03125f, 0);
			}
			poseStack.popPose();
		}
	}

	protected void renderGlassLiquid(TGModelRenderer part, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float f5, float progress, float start) {
		float prog;
		if (progress > 0.8f) {
			prog = 1.0f - ((progress - 0.8f) * 5f);
		} else {
			prog = Math.min(1.0f, (progress - start) * 5f);
			prog = (float) Math.sqrt(prog);
		}

		int count = Math.round(prog * 16f); //16f = height of glass

		poseStack.pushPose();
		for (int i = 0; i < count; i++) {
			part.render(poseStack, buffer, packedLight, packedOverlay, f5);
			poseStack.translate(0, -0.03125f, 0);
		}
		poseStack.popPose();
	}

	/**
	 * Glass parts (1.12: rendered last with alpha blending)
	 */
	public void renderGlass(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float f5) {
		TGModelRenderer[] glass = { G1, G2, G3, G4, G5, G6, G7, G8, G9, G10, G11 };
		for (TGModelRenderer g : glass) {
			g.render(poseStack, buffer, packedLight, packedOverlay, f5);
		}
	}

}
