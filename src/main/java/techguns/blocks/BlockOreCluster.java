package techguns.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * 1.12 "orecluster", flattened: one block per cluster type (orecluster_coal, ...). Unbreakable, drops nothing
 * (no loot table), mined by the ore drill (OreDrillTileEntMaster, data in TGOreClusters). Render type cutout_mipped is set in the models.
 */
public class BlockOreCluster<T extends Enum<T> & IEnumOreClusterType> extends GenericBlock {

	protected final T clusterType;

	public BlockOreCluster(T type) {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE)
				.strength(-1.0f, 6000000.0F).noLootTable().pushReaction(PushReaction.BLOCK));
		this.clusterType = type;
	}

	public T getClusterType() {
		return clusterType;
	}

	/**
	 * Mining level / power and amount multipliers of the cluster (TGOreClusters, config values)
	 */
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		techguns.TGOreClusters.OreCluster ore = techguns.TGOreClusters.orecluster.getClusterForType(clusterType);
		if (ore != null) {
			tooltip.add(Component.translatable("techguns.orecluster.mininglevel").append(": " + ore.getMininglevel()));
			tooltip.add(Component.translatable("techguns.orecluster.powermult").append(": x" + String.format("%.1f", ore.getMultiplier_power())));
			tooltip.add(Component.translatable("techguns.orecluster.amountmult").append(": x" + String.format("%.1f", ore.getMultiplier_amount())));
		}
	}
}
