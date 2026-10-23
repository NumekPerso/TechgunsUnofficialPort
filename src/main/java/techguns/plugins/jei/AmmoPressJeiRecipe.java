package techguns.plugins.jei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import techguns.TGItems;
import techguns.Techguns;
import techguns.tileentities.AmmoPressTileEnt;
import techguns.tileentities.operation.AmmoPressBuildPlans;
import techguns.tileentities.operation.IMachineRecipe;
import techguns.util.TextUtil;

public class AmmoPressJeiRecipe extends BasicRecipeWrapper {

	int type;

	public AmmoPressJeiRecipe(IMachineRecipe recipe, int type) {
		super(recipe);
		this.type = type;
	}

	@Override
	public void drawInfo(GuiGraphics g, int mouseX, int mouseY) {
		int x = 0 + BasicRecipeCategory.JEI_OFFSET_X;
		int y = 0 + BasicRecipeCategory.JEI_OFFSET_Y;
		int color = 4210752;

		Item item = null;
		switch (type) {
		case 0:
			item = TGItems.PISTOL_ROUNDS;
			break;
		case 1:
			item = TGItems.SHOTGUN_ROUNDS;
			break;
		case 2:
			item = TGItems.RIFLE_ROUNDS;
			break;
		case 3:
			item = TGItems.SNIPER_ROUNDS;
			break;
		}
		String s2 = item != null ? TextUtil.trans(item.getDescriptionId()) : "";

		drawString(g, TextUtil.trans(Techguns.MODID + ".gui.ammopress.build") + ":", x + 20, y + 30, color);
		drawString(g, s2, x + 20, y + 40, color);
	}

	public static List<AmmoPressJeiRecipe> getRecipes() {
		List<AmmoPressJeiRecipe> recipes = new ArrayList<AmmoPressJeiRecipe>();
		for (int i = 0; i < 4; i++) {
			recipes.add(new AmmoPressJeiRecipe(AmmoPressBuildPlans.getRecipeForType(i), i));
		}
		return recipes;
	}

	@Override
	protected int getRFperTick() {
		return AmmoPressTileEnt.POWER_PER_TICK;
	}

}
