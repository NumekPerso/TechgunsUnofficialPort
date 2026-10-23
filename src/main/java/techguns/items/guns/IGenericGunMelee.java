package techguns.items.guns;

import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;

/**
 * Melee / mining tools. 1.12 used Forge tool classes + harvest levels, the port maps them to the 1.20 block tags and tiers:
 * <ul>
 * <li>tool class "pickaxe"/"axe"/"shovel"/"hoe" -&gt; BlockTags.MINEABLE_WITH_*</li>
 * <li>tool class "sword" -&gt; cobweb (the blocks swords are effective on)</li>
 * <li>harvest level 0=wood, 1=stone, 2=iron, 3=diamond, 4+=netherite tier (TierSortingRegistry)</li>
 * </ul>
 * Blocks without a tool class (not in any of the tags) are effective for every tool, like blocks without harvest tool in 1.12.
 */
public interface IGenericGunMelee<T extends GenericGun> {

	public static final String[] TOOL_CLASSES = { "pickaxe", "axe", "shovel", "hoe", "sword" };

	@SuppressWarnings("unchecked")
	public default T setMeleeDmg(float dmgPowered, float dmgUnpowered) {
		GenericGun g = (GenericGun) this;
		g.meleeDamagePwr = dmgPowered;
		g.meleeDamageEmpty = dmgUnpowered;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public default T setTool(String toolclass, int harvestLevel) {
		if (!this.getMiningLevels().containsKey("default")) {
			this.getMiningLevels().put("default", harvestLevel);
		}

		this.getMiningLevels().put(toolclass, harvestLevel);
		return (T) this;
	}

	/**
	 * Is the block in the tag of the 1.12 tool class
	 */
	public static boolean isToolClassForBlock(String toolclass, BlockState state) {
		switch (toolclass) {
		case "pickaxe":
			return state.is(BlockTags.MINEABLE_WITH_PICKAXE);
		case "axe":
			return state.is(BlockTags.MINEABLE_WITH_AXE);
		case "shovel":
			return state.is(BlockTags.MINEABLE_WITH_SHOVEL);
		case "hoe":
			return state.is(BlockTags.MINEABLE_WITH_HOE);
		case "sword":
			return state.is(Blocks.COBWEB);
		default:
			return false;
		}
	}

	/**
	 * 1.12 Block.getHarvestTool, null if the block has no tool class
	 */
	@Nullable
	public static String getHarvestTool(BlockState state) {
		for (String c : TOOL_CLASSES) {
			if (isToolClassForBlock(c, state)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * 1.12 harvest level to 1.20 tier
	 */
	public static Tier getTierForHarvestLevel(int level) {
		if (level <= 0) {
			return Tiers.WOOD;
		} else if (level == 1) {
			return Tiers.STONE;
		} else if (level == 2) {
			return Tiers.IRON;
		} else if (level == 3) {
			return Tiers.DIAMOND;
		}
		return Tiers.NETHERITE;
	}

	/**
	 * Call this in getDestroySpeed()
	 */
	public default float getDigSpeed(ItemStack itemstack, BlockState state) {
		if (this.isEffectiveToolForState(itemstack, state)) {
			return this.getEffectiveDigSpeed(itemstack);
		} else {
			return 1.0f;
		}
	}

	public default boolean isEffectiveToolForState(ItemStack stack, BlockState state) {
		GenericGun g = (GenericGun) this;
		if (g.getCurrentAmmo(stack) >= g.getMiningAmmoConsumption()) {
			boolean hasToolClass = false;
			for (String c : TOOL_CLASSES) {
				if (isToolClassForBlock(c, state)) {
					hasToolClass = true;
					if (this.getMiningLevels().containsKey(c)) {
						return true;
					}
				}
			}
			return !hasToolClass;
		}
		return false;
	}

	/**
	 * Effective tool and harvest level high enough for the block (1.20 isCorrectToolForDrops)
	 */
	public default boolean canHarvestWithTool(ItemStack stack, BlockState state) {
		GenericGun g = (GenericGun) this;
		if (g.getCurrentAmmo(stack) >= g.getMiningAmmoConsumption()) {
			boolean hasToolClass = false;
			for (String c : TOOL_CLASSES) {
				if (isToolClassForBlock(c, state)) {
					hasToolClass = true;
					int level = this.getToolHarvestLevel(stack, c, null, state);
					if (level >= 0 && TierSortingRegistry.isCorrectTierForDrops(getTierForHarvestLevel(level), state)) {
						return true;
					}
				}
			}
			return !hasToolClass;
		}
		return false;
	}

	/**
	 * Harvest level of a tool class, -1 if the tool does not have this tool class
	 */
	public default int getToolHarvestLevel(ItemStack stack, String toolClass, @Nullable Player player, BlockState blockState) {
		if (this.getMiningLevels() == null) {
			return -1;
		}
		if (toolClass == null) {
			return this.getMiningLevels().get("default") + getExtraMiningLevel(stack, toolClass, player);
		} else if (this.getMiningLevels().containsKey(toolClass)) {
			return this.getMiningLevels().get(toolClass) + getExtraMiningLevel(stack, toolClass, player);
		} else {
			return -1;
		}
	}

	public default int getExtraMiningLevel(ItemStack stack, String toolClass, @Nullable Player player) {
		return 0;
	}

	public T setDigSpeed(float speed);

	public float getEffectiveDigSpeed(ItemStack itemstack);

	public HashMap<String, Integer> getMiningLevels();
}
