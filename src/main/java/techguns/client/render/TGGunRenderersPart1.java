package techguns.client.render;

import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.client.models.guns.*;
import techguns.client.models.items.*;
import techguns.client.models.projectiles.ModelRocket;
import techguns.client.render.fx.IScreenEffect;
import techguns.client.render.item.*;

/**
 * Item renderer registrations, part 1: ammo items and the first guns (port of 1.12 ClientProxy.registerItemRenderers). Registered by registry name.
 */
public class TGGunRenderersPart1 {

	public static void register() {
		float[][] m4magTranslations = {
				{0,0.25f,0}, //First Person
				{0f,0.25f,0.05f}, //Third Person
				{0.1f,0.25f,0}, //GUI
				{0,-0.1f,0}, //Ground
				{0,0,-0.05f} //frame
		};

		float[][] as50magTranslations = {
				{0,0f,0}, //First Person
				{0f,0f,0f}, //Third Person
				{0f,0.05f,0}, //GUI
				{0,-0.1f,0}, //Ground
				{0,0,0f} //frame
		};

		float[][] lmgmagTranslations = {
				{0,0.35f,0}, //First Person
				{0f,0.15f,0.0f}, //Third Person
				{0.1f,0.25f,0}, //GUI
				{0,0f,0}, //Ground
				{0,0.25f,-0.05f} //frame
		};

		float[][] rocketTranslations = {
				{0,0f,0f}, //First Person
				{0f,-0.1f,0.02f}, //Third Person
				{0.0f,0.0f,0}, //GUI
				{0.0f,0.0f,0}, //Ground
				{0,0,0f} //frame
		};
		/*
		  DEFAULT
		 
		.setTransformTranslations(new float[][]{
			{0f,0f,0f}, //First Person
			{0f,0f,0f}, //Third Person
			{0f,0f,0f}, //GUI
			{0f,0f,0f}, //Ground
			{0f,0f,-0.05f} //frame
		})
		
		*/

		ItemRenderHack.registerItemRenderer("assaultriflemagazine", new RenderItemBase(new ModelARMagazine(false), new ResourceLocation(Techguns.MODID,"textures/guns/ar_mag.png")).setBaseScale(1.25f).setGUIScale(0.85f).setBaseTranslation(0, -0.2f, 0).setTransformTranslations(m4magTranslations));

		ItemRenderHack.registerItemRenderer("assaultriflemagazineempty", new RenderItemBase(new ModelARMagazine(true), new ResourceLocation(Techguns.MODID,"textures/guns/ar_mag.png")).setBaseScale(1.25f).setGUIScale(0.85f).setBaseTranslation(0, -0.2f, 0).setTransformTranslations(m4magTranslations));

		ItemRenderHack.registerItemRenderer("assaultriflemagazine_incendiary", new RenderItemBase(new ModelARMagazine(false), new ResourceLocation(Techguns.MODID,"textures/guns/ar_mag_inc.png")).setBaseScale(1.25f).setGUIScale(0.85f).setBaseTranslation(0, -0.2f, 0).setTransformTranslations(m4magTranslations));

		ItemRenderHack.registerItemRenderer("lmgmagazine", new RenderItemLMGMag(new ModelLmgMag(false), new ResourceLocation(Techguns.MODID,"textures/guns/lmg_mag.png")).setBaseScale(1.25f).setGUIScale(0.75f).setBaseTranslation(0, 0f, 0.2f).setTransformTranslations(lmgmagTranslations));

		ItemRenderHack.registerItemRenderer("lmgmagazineempty", new RenderItemLMGMag(new ModelLmgMag(true), new ResourceLocation(Techguns.MODID,"textures/guns/lmg_mag.png")).setBaseScale(1.25f).setGUIScale(0.75f).setBaseTranslation(0, 0f, 0.2f).setTransformTranslations(lmgmagTranslations));

		ItemRenderHack.registerItemRenderer("lmgmagazine_incendiary", new RenderItemLMGMag(new ModelLmgMag(false), new ResourceLocation(Techguns.MODID,"textures/guns/lmg_mag_inc.png")).setBaseScale(1.25f).setGUIScale(0.75f).setBaseTranslation(0, 0f, 0.2f).setTransformTranslations(lmgmagTranslations));

		ItemRenderHack.registerItemRenderer("as50magazine", new RenderItemBase(new ModelAS50Mag(false), new ResourceLocation(Techguns.MODID,"textures/guns/as50_mag.png")).setBaseScale(1.5f).setGUIScale(0.75f).setBaseTranslation(0.0325f, -0.2f, 0.33f).setTransformTranslations(as50magTranslations));

		ItemRenderHack.registerItemRenderer("as50magazineempty", new RenderItemBase(new ModelAS50Mag(true), new ResourceLocation(Techguns.MODID,"textures/guns/as50_mag.png")).setBaseScale(1.5f).setGUIScale(0.75f).setBaseTranslation(0.0325f, -0.2f, 0.33f).setTransformTranslations(as50magTranslations));

		ItemRenderHack.registerItemRenderer("as50magazine_incendiary", new RenderItemBase(new ModelAS50Mag(false), new ResourceLocation(Techguns.MODID,"textures/guns/as50_mag_inc.png")).setBaseScale(1.5f).setGUIScale(0.75f).setBaseTranslation(0.0325f, -0.2f, 0.33f).setTransformTranslations(as50magTranslations));

		ItemRenderHack.registerItemRenderer("as50magazine_explosive", new RenderItemBase(new ModelAS50Mag(false), new ResourceLocation(Techguns.MODID,"textures/guns/as50_mag_exp.png")).setBaseScale(1.5f).setGUIScale(0.75f).setBaseTranslation(0.0325f, -0.2f, 0.33f).setTransformTranslations(as50magTranslations));

		ItemRenderHack.registerItemRenderer("rocket", new RenderItemBaseRocketItem(new ModelRocket(), new ResourceLocation(Techguns.MODID,"textures/guns/rocket.png")).setBaseScale(1.5f).setGUIScale(0.5f).setBaseTranslation(0, 0, 0.1f).setTransformTranslations(rocketTranslations).setFirstPersonScale(0.35f));

		ItemRenderHack.registerItemRenderer("rocket_nuke", new RenderItemBaseRocketItem(new ModelRocket(), new ResourceLocation(Techguns.MODID,"textures/guns/rocket_nuke.png")).setBaseScale(1.5f).setGUIScale(0.5f).setBaseTranslation(0, 0, 0.1f).setTransformTranslations(rocketTranslations).setFirstPersonScale(0.35f));

		ItemRenderHack.registerItemRenderer("rocket_high_velocity", new RenderItemBaseRocketItem(new ModelRocket(), new ResourceLocation(Techguns.MODID,"textures/guns/rocket_hv.png")).setBaseScale(1.5f).setGUIScale(0.5f).setBaseTranslation(0, 0, 0.1f).setTransformTranslations(rocketTranslations).setFirstPersonScale(0.35f));

		ItemRenderHack.registerItemRenderer("m4", new RenderGunBase(new ModelM4(),1).setBaseTranslation(RenderItemBase.SCALE*0.5f, -0.1f, 0)
				.setGUIScale(0.35f).setMuzzleFx(IScreenEffect.muzzleFlash_rifle, 0, 0.18f, -1.29f, 0.75f,0).setRecoilAnim(GunAnimation.genericRecoil, 0.1f, 4.0f).setTransformTranslations(new float[][]{
					{0f,0f,-0.05f}, //First Person
					{0f,0.01f,-0.1f}, //Third Person
					{0f,0f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.13f, -1f).setMuzzleFlashJitter(0.02f, 0.02f, 5.0f, 0.1f));

		ItemRenderHack.registerItemRenderer("ak47", new RenderGunBase(new ModelAK(),1).setBaseTranslation(RenderItemBase.SCALE*0.5f, -0.1f, 0).setBaseScale(0.75f)
				.setGUIScale(0.35f).setMuzzleFx(IScreenEffect.muzzleFlash_rifle, 0, 0.18f, -1.36f, 0.8f,0).setRecoilAnim(GunAnimation.genericRecoil, 0.1f, 4.0f).setTransformTranslations(new float[][]{
					{0f,0.06f,-0.02f}, //First Person
					{0f,0f,-0.08f}, //Third Person
					{0.06f,-0.01f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.08f, -1.02f).setMuzzleFlashJitter(0.02f, 0.02f, 5.0f, 0.1f));

		ItemRenderHack.registerItemRenderer("lmg", new RenderGunBase(new ModelLMG(),1).setBaseTranslation(RenderItemBase.SCALE*0.5f, -0.1f, 0)
				.setGUIScale(0.35f).setMuzzleFx(IScreenEffect.muzzleFlash_rifle, 0, 0.21f, -1.5f, 0.78f,0).setMuzzleFXPos3P(0.13f, -0.98f).setRecoilAnim(GunAnimation.genericRecoil, 0.01f, 2.0f).setTransformTranslations(new float[][]{
					{0f,0.02f,-0.09f}, //First Person
					{0f,0f,-0.06f}, //Third Person
					{0.05f,-0.03f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.14f, -1.02f).setMuzzleFlashJitter(0.03f, 0.03f, 5.0f, 0.1f));

		ItemRenderHack.registerItemRenderer("handcannon", new RenderGunBase90(new ModelHandgun(),1).setBaseTranslation(0, -0.2f, RenderItemBase.SCALE-0.1f)
				.setGUIScale(0.45f).setMuzzleFx(IScreenEffect.muzzleFlash_gun, 0, 0.16f, -0.75f, 0.9f,0).setReloadAnim(GunAnimation.breechReload, -0.15f, 55.0f).setReloadAnim3p(GunAnimation.breechReload, 0f, 55.0f).setTransformTranslations(new float[][]{
					{0,0.03f,-0.12f}, //First Person
					{0.0f,-0.05f,-0.09f}, //Third Person
					{0.0f,0.0f,0}, //GUI
					{0.0f,0.0f,0}, //Ground
					{0,0,0f} //frame
				}).setMuzzleFXPos3P(0.03f, -0.59f).setRecoilAnim(GunAnimation.genericRecoil, 0.2f, 25.0f));

		ItemRenderHack.registerItemRenderer("sawedoff", new RenderGunBase90(new ModelSawedOff(),1).setBaseTranslation(0, -0.2f, RenderItemBase.SCALE-0.1f)
				.setGUIScale(0.45f).setMuzzleFx(IScreenEffect.muzzleFlash_gun, 0, 0.16f, -0.75f, 1.05f,0).setReloadAnim(GunAnimation.breechReload, -0.15f, 55.0f).setReloadAnim3p(GunAnimation.breechReload, 0f, 55.0f).setTransformTranslations(new float[][]{
					{0f,0f,0f}, //First Person
					{0f,-0.04f,0f}, //Third Person
					{0f,0f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.06f, -0.49f).setRecoilAnim(GunAnimation.genericRecoil, 0.2f, 20.0f));

		ItemRenderHack.registerItemRenderer("thompson", new RenderGunBase90(new ModelThompson(),1).setBaseTranslation(0, -0.2f, RenderItemBase.SCALE*0.5f-0.1f)
				.setGUIScale(0.45f).setMuzzleFx(IScreenEffect.muzzleFlash_rifle, 0, 0.14f, -0.75f, 0.55f,0).setMuzzleFXPos3P(0.1f, -0.59f).setMuzzleFlashJitter(0.02f, 0.02f, 5.0f, 0.1f));

		ItemRenderHack.registerItemRenderer("boltaction", new RenderGunBase90(new ModelBoltaction(),1).setBaseTranslation(0, -0.2f, RenderItemBase.SCALE-0.1f).setBaseScale(1.35f)
				.setGUIScale(0.35f).setMuzzleFx(IScreenEffect.muzzleFlash_gun, 0, 0.21f, -1.48f, 0.9f,0).setScope(IScreenEffect.sniperScope).setTransformTranslations(new float[][]{
					{0f,-0.02f,-0.09f}, //First Person
					{0f,-0.04f,-0.11f}, //Third Person
					{0.1f,-0.08f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,-0.025f} //frame
				}).setMuzzleFXPos3P(0.12f, -1.13f).setScopeRecoilAnim(GunAnimation.scopeRecoil, 0.20f, 2.0f));

		ItemRenderHack.registerItemRenderer("biogun", new RenderGunBase90(new ModelBiogun(),1).setBaseTranslation(0.35f, -0.2f, RenderItemBase.SCALE-0.1f)
				.setGUIScale(0.45f).setMuzzleFx(IScreenEffect.muzzleGreenFlare, 0, 0.23f, -0.51f, 0.55f,0).setTransformTranslations(new float[][]{
					{0f,0.16f,0.05f}, //First Person
					{0f,0.07f,-0.05f}, //Third Person
					{0f,0f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.1f, -0.51f).setChargeTranslationAmount(0.05f));

		ItemRenderHack.registerItemRenderer("flamethrower", new RenderGunFlamethrower(new ModelFlamethrower(),1).setBaseTranslation(0, -0.2f, RenderItemBase.SCALE-0.1f)
				.setGUIScale(0.45f).setMuzzleFx(IScreenEffect.FlamethrowerMuzzleFlash, 0, 0.16f, -0.9f, 0.45f,0).setTransformTranslations(new float[][]{
					{0f,0f,0f}, //First Person
					{0f,0f,-0.08f}, //Third Person
					{0.05f,0f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.1f, -0.63f).setRecoilAnim(GunAnimation.swayRecoil, 0.025f, 2.5f));

		ItemRenderHack.registerItemRenderer("pistol", new RenderGunBase(new ModelPistol(),2).setBaseTranslation(RenderItemBase.SCALE*0.5f, -0.3f, -0.4f)
				.setBaseScale(1.2f).setGUIScale(0.9f).setMuzzleFx(IScreenEffect.muzzleFlash_gun, 0.03f, 0.2f, -0.5f, 0.55f,-0.03f).setTransformTranslations(new float[][]{
					{0,0.09f,-0.02f}, //First Person
					{0.0f,-0.03f,0.0f}, //Third Person
					{0.02f,-0.08f,0}, //GUI
					{0.02f,-0.08f,0}, //Ground
					{0,0,0f} //frame
				}).setRecoilAnim(GunAnimation.genericRecoil, 0.025f, 12.0f).setMuzzleFlashJitter(0.01f, 0.01f, 5.0f, 0.05f).setMuzzleFXPos3P(0.07f, -0.26f));

		ItemRenderHack.registerItemRenderer("rocketlauncher", new RenderRocketLauncher(new ModelRocketLauncher(),2).setBaseTranslation(-0.4f, -0.2f, -RenderItemBase.SCALE*0.5f)
				.setGUIScale(0.45f).setMuzzleFx(IScreenEffect.muzzleFlash_gun, 0, 0.39f, -0.6f, 0.87f, 0).setTransformTranslations(new float[][]{
					{-0.13f,0.3f,0.32f}, //First Person
					{0,0.02f,0.07f}, //Third Person
					{-0.06f,0.0f,0.0f}, //GUI
					{0.0f,0.0f,0}, //Ground
					{0,0,0f} //frame
				}).setMuzzleFXPos3P(0.09f, -0.26f));

		ItemRenderHack.registerItemRenderer("minigun", new RenderGunBase90(new ModelMinigun(),2).setBaseTranslation(0, -0.2f, RenderItemBase.SCALE*0.5f).setBaseScale(1.2f)
				.setGUIScale(0.3f).setMuzzleFx(IScreenEffect.muzzleFlash_minigun, -0.04f, 0.05f, -1.25f, 0.8f,0.04f).setTransformTranslations(new float[][]{
					{0f,-0.16f,0.1f}, //First Person
					{0f,-0.53f,0.2f}, //Third Person
					{0.12f,-0.02f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(-0.38f, -0.78f).setRecoilAnim(GunAnimation.genericRecoil, 0.05f, 3.0f));

		ItemRenderHack.registerItemRenderer("combatshotgun", new RenderGunBase90(new ModelCombatShotgun(),2).setBaseTranslation(0, -0.2f, RenderItemBase.SCALE-0.1f)
				.setGUIScale(0.45f).setMuzzleFx(IScreenEffect.muzzleFlash_gun, 0, 0.21f, -0.91f, 0.75f,0).setTransformTranslations(new float[][]{
					{0f,0.03f,0f}, //First Person
					{0f,-0.01f,-0.10f}, //Third Person
					{0.05f,0f,0f}, //GUI
					{0f,0f,0f}, //Ground
					{0f,0f,-0.05f} //frame
				}).setMuzzleFXPos3P(0.12f, -0.81f).setRecoilAnim(GunAnimation.genericRecoil, 0.3f, 15.0f));
	}
}
