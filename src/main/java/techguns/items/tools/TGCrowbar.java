package techguns.items.tools;

import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import techguns.Techguns;
import techguns.util.ItemUtil;

public class TGCrowbar extends TGPickaxe {

	/**
	 * 1.12 tool classes (only used for the tooltip now, digging is handled by the PickaxeItem / tier)
	 */
	protected LinkedHashMap<String, Integer> harvestLevels = new LinkedHashMap<String, Integer>();

	public TGCrowbar(Tier mat, String name) {
		super(mat, name);
		int level = mat.getLevel();
		harvestLevels.put("default", level);
		harvestLevels.put("pickaxe", level);
		harvestLevels.put("wrench", level);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);

		if (Techguns.proxy.isShiftKeyDown()) {
			ItemUtil.addToolClassesTooltip(harvestLevels, tooltip);
		} else {
			ItemUtil.addShiftExpandedTooltip(tooltip);
		}
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
		return true;
	}

}
