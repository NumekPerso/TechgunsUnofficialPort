package techguns.items;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Debug tool (1.12 BuildingScanTool): click 2 corners, the blocks in between are written to ./structure_scan.txt
 * (palette as MBlock lines + the x,y,z,index list of the structure files in assets/techguns/structures).
 */
public class BuildingScanTool extends GenericItem {

	public BuildingScanTool(String name) {
		super(name, true, new Properties().stacksTo(1));
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Player player = ctx.getPlayer();
		Level world = ctx.getLevel();
		ItemStack item = ctx.getItemInHand();
		BlockPos pos = ctx.getClickedPos();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		CompoundTag tag = item.getOrCreateTag();
		if (tag.contains("x1") && tag.contains("y1") && tag.contains("z1")) {
			int x1 = tag.getInt("x1");
			int y1 = tag.getInt("y1");
			int z1 = tag.getInt("z1");
			int sizeX = Math.abs(x1 - x) + 1;
			int sizeY = Math.abs(y1 - y) + 1;
			int sizeZ = Math.abs(z1 - z) + 1;

			if (world.isClientSide && player != null) {
				player.sendSystemMessage(Component.literal("Position2 set (" + x + "/" + y + "/" + z + ")."));
				player.sendSystemMessage(Component.literal("Size: (" + sizeX + "/" + sizeY + "/" + sizeZ + ")."));
			}
			if (!world.isClientSide) {
				scanStructure(world, Math.min(x, x1), Math.min(y, y1), Math.min(z, z1), sizeX, sizeY, sizeZ);
			}
			tag.remove("x1");
			tag.remove("y1");
			tag.remove("z1");
		} else {
			tag.putInt("x1", x);
			tag.putInt("y1", y);
			tag.putInt("z1", z);
			if (world.isClientSide && player != null) {
				player.sendSystemMessage(Component.literal("Position1 set (" + x + "/" + y + "/" + z + ")."));
			}
		}
		return InteractionResult.SUCCESS;
	}

	private void scanStructure(Level world, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
		StringBuilder sbDefBlocks = new StringBuilder();
		ArrayList<BlockState> blockList = new ArrayList<>();
		ArrayList<int[]> blockPosList = new ArrayList<>();
		MutableBlockPos p = new MutableBlockPos();

		for (int ix = 0; ix < sizeX; ix++) {
			for (int iy = 0; iy < sizeY; iy++) {
				for (int iz = 0; iz < sizeZ; iz++) {
					BlockState bs = world.getBlockState(p.set(x + ix, y + iy, z + iz));
					if (!bs.isAir() && !bs.is(Blocks.DIRT) && !bs.is(Blocks.GRASS_BLOCK)) {
						if (!blockList.contains(bs)) {
							blockList.add(bs);
							sbDefBlocks.append("blockList.add(MBlock.of(\"" + BlockStateParser.serialize(bs).replace("\"", "\\\"") + "\"));\n");
						}
						blockPosList.add(new int[] { ix, iy, iz, blockList.indexOf(bs) });
					}
				}
			}
		}

		try (PrintWriter pw = new PrintWriter(new FileWriter(new File("structure_scan.txt")))) {
			pw.println(sbDefBlocks.toString());
			pw.println("---");
			pw.println();
			pw.println(blockPosList.size());
			for (int[] bp : blockPosList) {
				pw.println(bp[0] + "," + bp[1] + "," + bp[2] + "," + bp[3]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
