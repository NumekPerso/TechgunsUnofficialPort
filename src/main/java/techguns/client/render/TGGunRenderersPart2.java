package techguns.client.render;

import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.client.models.guns.*;
import techguns.client.models.items.*;
import techguns.client.models.projectiles.ModelRocket;
import techguns.client.render.fx.IScreenEffect;
import techguns.client.render.item.*;

/**
 * Item renderer registrations, part 2: remaining guns and grenades (port of 1.12 ClientProxy.registerItemRenderers). Registered by registry name.
 */
public class TGGunRenderersPart2 {

	public static void register() {
		ItemRenderHack.registerItemRenderer("revolver", new RenderGunBase90(new ModelRevolver(),1).setBaseTranslation(-0.35f, -0.2f, RenderItemBase.SCALE*0.5f-0.1f)
				.setBaseScale(0.75f).setGUIScale(0.75f).setMuzzleFx(IScreenEffect.muzzleFlash_gun, 0, 0.25f, -0.41f, 0.5f,0).setTransformTranslations(new float[][]{
					{0f,0.14f,0.01f}, //First Person
					{0f,0.0f,0.0f}, //Third Person
					{0.05f,0.01f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,0f} //frame
				}).setMuzzleFXPos3P(0.09f, -0.30f).setRecoilAnim(GunAnimation.genericRecoil, 0.1f, 10.0f));

		ItemRenderHack.registerItemRenderer("grimreaper", new RenderGunBase90(new ModelGrimReaper(),1).setBaseTranslation(0.3f, -0.2f, RenderItemBase.SCALE*0.5f)
				.setGUIScale(0.3f).setChargeTranslationAmount(0.025f).setMuzzleFx(IScreenEffect.muzzleFlash_gun, 0, 0.39f, -0.61f, 0.75f,0).setTransformTranslations(new float[][]{
					{0f,0.25f,0.18f}, //First Person
					{0,0.13f,0.1f}, //Third Person
					{-0.02f,0.01f,0.0f}, //GUI
					{0.0f,0.1f,0}, //Ground
					{0,0,0f} //frame
				}).setMuzzleFXPos3P(0.24f, -0.56f).setBaseScale(1.25f).setFirstPersonScale(0.4f).setGroundAndFrameScale(0.35f));

		ItemRenderHack.registerItemRenderer("pdw", new RenderGunBase90(new ModelPDW(),1).setBaseTranslation(0, -0.2f, RenderItemBase.SCALE*1.5f-0.1f)
				.setGUIScale(0.55f).setMuzzleFx(IScreenEffect.muzzleFlash_blue, 0, 0.13f, -0.58f, 0.55f ,0).setTransformTranslations(new float[][]{
					{0.0f, 0.09f, -0.04f}, //First Person
					{0f, 0.06f, -0.02f}, //Third Person
					{0f, 0f, 0f}, //GUI
					{0f, 0f, 0f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.11f, -0.41f).setMuzzleFlashJitter(0.01f, 0.01f, 5.0f, 0.1f).setRecoilAnim(GunAnimation.genericRecoil,  0.06f, 4.0f));

		ItemRenderHack.registerItemRenderer("as50", new RenderGunBase(new ModelAS50(),1).setBaseTranslation(RenderItemBase.SCALE*0.5f, -0.1f, 0)
				.setBaseScale(0.85f).setGUIScale(0.30f).setMuzzleFx(IScreenEffect.muzzleFlash_rifle, 0, 0.29f, -1.82f, 1.15f,0).setRecoilAnim(GunAnimation.genericRecoil, 0.25f, 4.0f).setTransformTranslations(new float[][]{
					{0f,0.06f,-0.1f}, //First Person
					{0f,0.0f,-0.05f}, //Third Person
					{0.13f,-0.09f,-0.05f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,-0.2f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.17f, -1.29f).setScope(IScreenEffect.sniperScope).setRecoilAnim(GunAnimation.genericRecoil, 0.25f, 5.0f).setScopeRecoilAnim(GunAnimation.scopeRecoil, 0.2f, 2.0f));

		ItemRenderHack.registerItemRenderer("m4_infiltrator", new RenderGunBase(new ModelM4Infiltrator(),1).setBaseTranslation(RenderItemBase.SCALE*0.5f, -0.1f, 0)
				.setGUIScale(0.35f)/*.setMuzzleFx(IScreenEffect.muzzleFlash_rifle, 0, 0.18f, -1.5f, 0.5f,0)*/.setRecoilAnim(GunAnimation.genericRecoil, 0.1f, 4.0f).setTransformTranslations(new float[][]{
					{0f,0f,-0.05f}, //First Person
					{0f,0.01f,-0.1f}, //Third Person
					{0f,0f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.14f, -1.15f).setScope(IScreenEffect.sniperScope).setScopeRecoilAnim(GunAnimation.scopeRecoil, 0.05f, 1.0f));

		ItemRenderHack.registerItemRenderer("goldenrevolver", new RenderGunBase90(new ModelGoldenRevolver(),1).setBaseTranslation(-0.35f, -0.2f, RenderItemBase.SCALE*0.5f-0.1f)
				.setBaseScale(0.75f).setGUIScale(0.75f).setMuzzleFx(IScreenEffect.muzzleFlash_gun, 0, 0.25f, -0.41f, 0.5f,0).setTransformTranslations(new float[][]{
					{0f,0.14f,0.01f}, //First Person
					{0f,0.0f,0.0f}, //Third Person
					{0.05f,0.01f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,0f} //frame
				}).setMuzzleFXPos3P(0.09f, -0.30f).setRecoilAnim(GunAnimation.genericRecoil, 0.1f, 15.0f));

		ItemRenderHack.registerItemRenderer("pulserifle", new RenderGunBase90(new ModelPulseRifle(),1).setBaseTranslation(0, -0.2f, RenderItemBase.SCALE*1.5f - 0.09f)
				.setGUIScale(0.45f).setMuzzleFx(IScreenEffect.muzzleFlash_blue, 0, 0.22f, -0.76f, 0.6f,0).setTransformTranslations(new float[][]{
					{0f,0.16f,0.01f}, //First Person
					{0f,0.05f,0.08f}, //Third Person
					{0.05f,0f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.1f, -0.50f).setScope(IScreenEffect.techScope,2.125f).setScopeRecoilAnim(GunAnimation.scopeRecoil, 0.10f, 1.5f).setRecoilAnim(GunAnimation.pulseRifleRecoil, 0.25f, 10.0f));

		ItemRenderHack.registerItemRenderer("teslagun", new RenderGunBase90(new ModelTeslaGun(),1).setBaseTranslation(0.25f, -0.2f, RenderItemBase.SCALE-0.09f)
				.setGUIScale(0.45f).setMuzzleFx(IScreenEffect.muzzleFlashLightning, 0, 0.26f, -0.67f, 0.5f,0).setTransformTranslations(new float[][]{
					{0f,0.15f,0.04f}, //First Person
					{0f,0.05f,-0.08f}, //Third Person
					{0.03f,0.01f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.12f, -0.65f).setRecoilAnim(GunAnimation.genericRecoil, 0.2f, 5.0f));

		ItemRenderHack.registerItemRenderer("netherblaster", new RenderGunBase(new ModelNetherBlaster(),1).setBaseTranslation(0f, -0.2f, RenderItemBase.SCALE-0.09f)
				.setGUIScale(0.60f).setMuzzleFx(IScreenEffect.muzzleFlashFireball_alpha, 0, 0.29f, -0.33f, 0.5f,0).setTransformTranslations(new float[][]{
					{0f,0.15f,0.04f}, //First Person
					{0f,-0.16f,-0.24f}, //Third Person
					{-0.10f,0.01f,0f}, //GUI
					{0f,0f,-0.11f}, //Ground
					{0.16f,-0.07f,-0.16f} //frame
				}).setMuzzleFXPos3P(-0.06f, -0.45f));

		ItemRenderHack.registerItemRenderer("lasergun", new RenderGunBase90(new ModelLasergun(),1).setBaseTranslation(0.25f, -0.2f, RenderItemBase.SCALE*0.5f-0.10f)
				.setBaseScale(1.1f).setGUIScale(0.40f).setMuzzleFx(IScreenEffect.muzzleFlashLaser, 0, 0.30f, -1.06f, 0.5f,0).setTransformTranslations(new float[][]{
					{0f,0.15f,0.04f}, //First Person
					{0f,0.02f,0.01f}, //Third Person
					{0.13f,0.01f,0f}, //GUI
					{0f,0f,0.15f}, //Ground
					{-0.18f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.11f, -0.83f).setRecoilAnim(GunAnimation.genericRecoil, 0.2f, 5.0f));

		ItemRenderHack.registerItemRenderer("alienblaster", new RenderGunBase(new ModelAlienBlaster(),1).setBaseTranslation(RenderItemBase.SCALE*0.5f, -0.3f, -0.2f)
				.setBaseScale(0.75f).setGUIScale(0.75f).setMuzzleFx(IScreenEffect.muzzleFlashAlienBlaster, 0f, 0.28f, -0.52f, 0.55f,0f).setTransformTranslations(new float[][]{
					{0,0.06f,-0.02f}, //First Person
					{0.0f,-0.07f,-0.01f}, //Third Person
					{0.02f,-0.08f,0}, //GUI
					{0.02f,-0.08f,0}, //Ground
					{-0.04f,-0.09f,0f} //frame
				}).setRecoilAnim(GunAnimation.genericRecoil, 0.025f, 10.0f).setMuzzleFXPos3P(0.07f, -0.36f).setReloadAnim(GunAnimation.breechReload, -0.15f, 55.0f).setReloadAnim3p(GunAnimation.breechReload, 0f, 55.0f));

		ItemRenderHack.registerItemRenderer("blasterrifle", new RenderGunBase(new ModelBlasterRifle(),1).setBaseTranslation(RenderItemBase.SCALE*0.5f, -0.1f, 0)
				.setBaseScale(0.9f).setGUIScale(0.45f).setMuzzleFx(IScreenEffect.muzzleFlashLaser, 0, 0.24f, -0.93f, 0.75f,0).setRecoilAnim(GunAnimation.genericRecoil, 0.1f, 4.0f).setTransformTranslations(new float[][]{
					{0f,0.11f,-0.20f}, //First Person
					{0f,0.0f,-0.04f}, //Third Person
					{0f,0f,0.03f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.09f, -0.57f).setScope(IScreenEffect.sniperScope).setScopeRecoilAnim(GunAnimation.scopeRecoil, 0.05f, 1.0f));

		ResourceLocation[] powerhammer_textures = {new ResourceLocation(Techguns.MODID, "textures/guns/powerhammer.png"),new ResourceLocation(Techguns.MODID, "textures/guns/powerhammer_obsidian.png"),new ResourceLocation(Techguns.MODID, "textures/guns/powerhammer_carbon.png")};
		ItemRenderHack.registerItemRenderer("powerhammer", new RenderMiningToolMultiTexture(new ModelPowerHammer(),2, powerhammer_textures).setBaseTranslation(0.15f, -0.2f, RenderItemBase.SCALE-0.09f)
				.setBaseScale(1.25f).setGUIScale(0.45f).setMuzzleFx(null, 0, 0.26f, -0.67f, 0.5f,0).setTransformTranslations(new float[][]{
					{0f,0.18f,0.09f}, //First Person
					{0f,0.04f,0.04f}, //Third Person
					{0.03f,0.01f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{-0.07f,-0.03f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.12f, -0.65f).setChargeTranslationAmount(0.125f));

		ItemRenderHack.registerItemRenderer("grenadelauncher", new RenderGunBaseObj(new ModelBaseBakedGrenadeLauncher(new ResourceLocation(Techguns.MODID, "models/item/grenadelauncher.obj"), new ResourceLocation(Techguns.MODID, "models/item/grenadelauncher_1.obj")),1,90.0f)
				.setBaseTranslation(0f, 0f, 0f)
				.setBaseScale(0.125f).setGUIScale(0.45f).setMuzzleFx(IScreenEffect.muzzleFlash_gun, 0, 0.22f, -0.63f, 0.5f,0).setTransformTranslations(new float[][]{
					{0f,0.19f,-0.09f}, //First Person
					{0f,0.06f,-0.20f}, //Third Person
					{-0.05f,0.08f,0f}, //GUI
					{0f,0.05f,-0.09f}, //Ground
					{0.11f,0.01f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.07f, -0.61f));

		ItemRenderHack.registerItemRenderer("aug", new RenderGunBase(new ModelAUG(),2).setBaseTranslation(RenderItemBase.SCALE*0.5f, -0.1f, 0.1f)
				.setGUIScale(0.35f).setMuzzleFx(IScreenEffect.muzzleFlash_rifle, 0, 0.19f, -1.45f, 0.75f,0).setRecoilAnim(GunAnimation.genericRecoil, 0.1f, 4.0f).setTransformTranslations(new float[][]{
					{0f,0.01f,-0.15f}, //First Person
					{0f,0.0f,-0.01f}, //Third Person
					{0f,0f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0.02f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.12f, -0.87f).setMuzzleFlashJitter(0.02f, 0.02f, 5.0f, 0.1f).setScope(IScreenEffect.sniperScope).setScopeRecoilAnim(GunAnimation.scopeRecoil, 0.075f, 1.0f));

		ItemRenderHack.registerItemRenderer("sonicshotgun", new RenderGunBase90(new ModelSonicShotgun(),1).setBaseTranslation(0, -0.2f, RenderItemBase.SCALE*0.5f-0.10f)
				.setBaseScale(1.0f).setGUIScale(0.35f).setMuzzleFx(IScreenEffect.muzzleFlashSonic, 0, 0.28f, -0.98f, 0.5f,0).setTransformTranslations(new float[][]{
					{0f,0.10f,0.04f}, //First Person
					{0f,-0.01f,-0.03f}, //Third Person
					{0.08f,0.0f,0f}, //GUI
					{0f,0.05f,0.15f}, //Ground
					{-0.18f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.11f, -0.81f));

		ItemRenderHack.registerItemRenderer("chainsaw", new RenderGunChainsaw(new ModelChainsaw(),2).setBaseTranslation(-0.4f, -0.2f, RenderItemBase.SCALE-0.09f)
				.setBaseScale(0.95f).setGUIScale(0.45f).setTransformTranslations(new float[][]{
					{0f,-0.08f,0.15f}, //First Person
					{0f,-0.5f,0.04f}, //Third Person
					{0.03f,0.01f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{-0.07f,-0.03f,-0.11f} //frame
				}));

		ItemRenderHack.registerItemRenderer("scatterbeamrifle", new RenderGunBase(new ModelLasergun2(),1).setBaseTranslation(RenderItemBase.SCALE*0.5f, -0.1f, 0.1f)
				.setGUIScale(0.35f).setMuzzleFx(IScreenEffect.muzzleFlashLaser, 0, 0.22f, -1.09f, 0.75f,0).setRecoilAnim(GunAnimation.genericRecoil, 0.1f, 4.0f).setTransformTranslations(new float[][]{
					{0f,0.04f,-0.05f}, //First Person
					{0f,0.01f,-0.1f}, //Third Person
					{0f,0f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.14f, -0.82f));

		ItemRenderHack.registerItemRenderer("nucleardeathray", new RenderGunBase90Translucent(new ModelNDR(),2,1).setBaseTranslation(1f, -0.2f, RenderItemBase.SCALE*1.5f-0.09f)
				.setBaseScale(1.2f).setGUIScale(0.40f).setMuzzleFx(IScreenEffect.muzzleFlashNukeBeam, 0, 0.19f, -0.91f, 0.65f,0).setTransformTranslations(new float[][]{
					{0f,0.02f,0.09f}, //First Person
					{-0.01f,0.04f,0.3f}, //Third Person
					{0.11f,-0.08f,0f}, //GUI
					{0f,0f,0.15f}, //Ground
					{-0.23f,-0.08f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.11f, -0.83f).setRecoilAnim(GunAnimation.swayRecoil, 0.025f, 0.75f));

		ItemRenderHack.registerItemRenderer("scar", new RenderGunBase(new ModelScar(),2).setBaseTranslation(RenderItemBase.SCALE*0.5f, -0.1f, 0.1f)
				.setGUIScale(0.35f).setMuzzleFx(IScreenEffect.muzzleFlash_rifle, 0, 0.23f, -1.48f, 0.78f,0).setRecoilAnim(GunAnimation.genericRecoil, 0.1f, 4.0f).setTransformTranslations(new float[][]{
					{0f,0.04f,-0.15f}, //First Person
					{0f,0.02f,-0.11f}, //Third Person
					{0.05f,0f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0.02f,-0.09f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.14f, -1.04f).setMuzzleFlashJitter(0.02f, 0.02f, 5.0f, 0.1f).setScope(IScreenEffect.sniperScope).setScopeRecoilAnim(GunAnimation.scopeRecoil, 0.05f, 2.0f));

		ItemRenderHack.registerItemRenderer("vector", new RenderGunBase(new ModelVector(),1).setBaseTranslation(RenderItemBase.SCALE*0.5f, -0.57f, -0.2f)
				.setBaseScale(1.1f).setGUIScale(0.45f).setMuzzleFx(IScreenEffect.muzzleFlash_rifle, 0, 0.10f, -0.72f, 0.60f,0).setRecoilAnim(GunAnimation.genericRecoil, 0.05f, 2.0f).setTransformTranslations(new float[][]{
					{0f,-0.17f,0.0f}, //First Person
					{0f,-0.2f,-0.04f}, //Third Person
					{-0.08f,-0.09f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0.05f,-0.17f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.0f, -0.53f).setMuzzleFlashJitter(0.02f, 0.02f, 5.0f, 0.1f));

		ItemRenderHack.registerItemRenderer("mac10", new RenderGunBase(new ModelMac10(),1).setBaseTranslation(RenderItemBase.SCALE*0.5f, -0.45f, -0.3f)
				.setBaseScale(1.2f).setGUIScale(0.55f).setMuzzleFx(IScreenEffect.muzzleFlash_rifle, 0, 0.23f, -0.46f, 0.5f,0).setRecoilAnim(GunAnimation.genericRecoil, 0.06f, 3.0f).setTransformTranslations(new float[][]{
					{0f,0f,-0.05f}, //First Person
					{0f,-0.10f,0.01f}, //Third Person
					{-0.02f,-0.02f,0f}, //GUI
					{0f,0.03f,0f}, //Ground
					{0f,-0.05f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.07f, -0.26f).setMuzzleFlashJitter(0.02f, 0.05f, 5.0f, 0.1f));

		ItemRenderHack.registerItemRenderer("mibgun", new RenderGunBase(new ModelMibGun(),1).setBaseTranslation(/*RenderItemBase.SCALE*0.5f*/0f, -0.56f, -0.02f)
				.setBaseScale(1.2f).setGUIScale(0.75f).setMuzzleFx(IScreenEffect.muzzleFlashMibGun, 0f, 0.26f, -0.42f, 0.55f,0f).setTransformTranslations(new float[][]{
					{0,0.10f,-0.02f}, //First Person
					{0.0f,-0.04f,-0.01f}, //Third Person
					{0.02f,-0.08f,0}, //GUI
					{0.02f,-0.08f,0}, //Ground
					{-0.04f,-0.09f,0f} //frame
				}).setRecoilAnim(GunAnimation.genericRecoil, 0.025f, 10.0f).setMuzzleFXPos3P(0.06f, -0.31f).setReloadAnim(GunAnimation.breechReload, -0.15f, 55.0f).setReloadAnim3p(GunAnimation.breechReload, 0f, 55.0f));

		ItemRenderHack.registerItemRenderer("gaussrifle", new RenderGunBaseObj(
				new ModelBaseBaked(new ResourceLocation(Techguns.MODID, "models/item/gaussrifle.obj")),1,-90f)
				.setBaseTranslation(0.6f, 0f, RenderItemBase.SCALE*0.5f-0.09f)
				.setBaseScale(0.9f).setGUIScale(0.25f).setMuzzleFx(IScreenEffect.muzzleFlashSonic, 0, 0.21f, -1.56f, 1.0f,0).setTransformTranslations(new float[][]{
					{0f,0.12f,-0.1f}, //First Person
					{0f,0.05f,-0.17f}, //Third Person
					{0.f,0.06f,0.06f}, //GUI
					{0f,0f,0.f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.09f, -1.26f).setRecoilAnim(GunAnimation.genericRecoil, 0.25f, 7.5f).setScope(IScreenEffect.techScope,2.125f).setScopeRecoilAnim(GunAnimation.scopeRecoil, 0.15f, 1.0f));

		ItemRenderHack.registerItemRenderer("guidedmissilelauncher", new RenderGunBase90(new ModelGuidedMissileLauncher(),1).setBaseTranslation(-0.4f, -0.2f, RenderItemBase.SCALE*0.5f)
				.setGUIScale(0.35f).setChargeTranslationAmount(0).setMuzzleFx(IScreenEffect.muzzleFlash_gun, 0, 0.39f, -0.6f, 0.87f, 0).setTransformTranslations(new float[][]{
					{-0.13f,0.3f,0.62f}, //First Person
					{0,0.09f,0.28f}, //Third Person
					{0.0f,0.03f,0.0f}, //GUI
					{0.0f,0.0f,0}, //Ground
					{0,0,-0.04f} //frame
				}).setMuzzleFXPos3P(0.09f, -0.26f));

		ResourceLocation[] drill_textures = {new ResourceLocation(Techguns.MODID, "textures/guns/miningdrill_obsidian.png"), new ResourceLocation(Techguns.MODID, "textures/guns/miningdrill_carbon.png")};
		ItemRenderHack.registerItemRenderer("miningdrill", new RenderMiningToolMultiTexture(new ModelMiningDrill(),2, drill_textures).setBaseTranslation(0, -0.2f, -RenderItemBase.SCALE*0.5f).setBaseScale(2.0f)
				.setGUIScale(0.35f).setTransformTranslations(new float[][]{
					{0f,-0.03f,0.0f}, //First Person
					{0f,-0.57f,0.08f}, //Third Person
					{0.01f,-0.01f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,-0.08f,-0.05f} //frame
				}).setRecoilAnim(GunAnimation.genericRecoil, 0.05f, 1.0f));

		ItemRenderHack.registerItemRenderer("tfg", new RenderGunBase90(new ModelTFG(),1).setBaseTranslation(-0.46f, -0.38f, RenderItemBase.SCALE-0.125f)
				.setBaseScale(1.20f).setGUIScale(0.30f).setMuzzleFx(IScreenEffect.muzzleFlashTFG, 0.0f, 0.18f, -0.87f, 0.9f,0).setTransformTranslations(new float[][]{
					{0f,-0.03f,0.16f}, //First Person
					{0f,-0.09f,-0.26f}, //Third Person
					{0.04f,-0.04f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{-0.07f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.09f, -1.14f).setChargeTranslationAmount(0.05f).setFirstPersonScale(0.45f));

		ItemRenderHack.registerItemRenderer("shishkebap", new RenderGunBase(new ModelShishkebap(),1).setBaseTranslation(0, 0,0).setBaseScale(1.0f)
				.setGUIScale(0.3f).setTransformTranslations(new float[][]{
					{0f,0.0f,0.0f}, //First Person
					{0f,0.0f,0.0f}, //Third Person
					{0.0f,-0.25f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0.0f,0.0f} //frame
				}).setAmbientParticleFX("ScreenTestFX").setReloadAnim(GunAnimation.breechReload, -0.15f, 55.0f).setReloadAnim3p(GunAnimation.breechReload, 0f, 55.0f));

		ItemRenderHack.registerItemRenderer("laserpistol", new RenderGunBase(new ModelLaserPistol(),1).setBaseTranslation(RenderItemBase.SCALE*0.5f, -0.3f, -0.4f)
				.setBaseScale(1.2f).setGUIScale(0.7f).setMuzzleFx(IScreenEffect.muzzleFlashLaser, 0.03f, 0.2f, -0.5f, 0.55f,-0.03f).setTransformTranslations(new float[][]{
					{0,0.09f,-0.02f}, //First Person
					{0.0f,-0.03f,0.0f}, //Third Person
					{0.02f,-0.08f,0}, //GUI
					{0.02f,-0.08f,0}, //Ground
					{0,0,0f} //frame
				}).setRecoilAnim(GunAnimation.genericRecoil, 0.0125f, 6.0f).setMuzzleFXPos3P(0.07f, -0.26f));

		ItemRenderHack.registerItemRenderer("stielgranate", new RenderGrenade(new ModelStielgranate(), new ResourceLocation(Techguns.MODID,"textures/guns/stielgranate.png")).setBaseScale(1f).setGUIScale(0.8f).setBaseTranslation(-RenderItemBase.SCALE*0.5f, 0.48f, -RenderItemBase.SCALE).setTransformTranslations(new float[][]{
			{0f,-0.06f,0f}, //First Person
			{0f,-0.15f,0.06f}, //Third Person
			{-0.05f,-0.49f,0f}, //GUI
			{0f,-0.15f,0f}, //Ground
			{0f,0f,-0.05f} //frame
		}));

		ItemRenderHack.registerItemRenderer("fraggrenade", new RenderGrenade(new ModelFragGrenade(true), new ResourceLocation(Techguns.MODID,"textures/guns/frag_grenade_texture.png"),90.0f).setBaseScale(1.25f).setGUIScale(1.35f).setBaseTranslation(-0.02f,0.65f,-RenderItemBase.SCALE+0.02f).setTransformTranslations(new float[][]{
			{0f,-0.06f,0f}, //First Person
			{0f,-0.11f,-0.01f}, //Third Person
			{-0.05f,-0.49f,0f}, //GUI
			{0f,0.f,0f}, //Ground
			{0f,0f,-0.05f} //frame
		}));
	}
}
