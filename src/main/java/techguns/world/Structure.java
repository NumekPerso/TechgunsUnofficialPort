package techguns.world;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import techguns.util.MBlock;
import techguns.world.dungeon.presets.specialblocks.SpecialBlockHandler;

/**
 * Scanned block structure (dungeon template segments). 1.20: saved as text instead of java serialization (see
 * DungeonTemplate): palette lines are either 1.12 "registryname meta" (converted 1.12 templates) or "=blockstate"
 * (templates scanned in 1.20), then "layer L n" followed by n "x,y,z,paletteIndex" lines.
 */
public class Structure {

	int sizeX;
	int sizeY;
	int sizeZ;
	List<MBlock> blocks = new ArrayList<MBlock>();
	HashMap<Integer, List<BlockEntry>> blockEntries = new HashMap<>();

	public void applySpecialMBlocks(ResourceLocation loottable) {
		SpecialBlockHandler.applySpecialMBlocks(blocks, loottable);
	}

	public Structure(int sizeX, int sizeY, int sizeZ) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
	}

	public static Structure scanBlocks(LevelAccessor world, BlockPos xyz, int sizeX, int sizeY, int sizeZ) {
		return scanBlocks(world, xyz.getX(), xyz.getY(), xyz.getZ(), sizeX, sizeY, sizeZ);
	}

	public static Structure scanBlocks(LevelAccessor world, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
		Structure structure = new Structure(sizeX, sizeY, sizeZ);

		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				for (int k = 0; k < sizeZ; k++) {
					BlockPos bpos = new BlockPos(x + i, y + j, z + k);
					BlockState state = world.getBlockState(bpos);
					int layer = BlockData.getBlockLayer(state);
					MBlock mblock = new MBlock(state);
					int index = structure.blocks.indexOf(mblock);
					if (index < 0) {
						structure.blocks.add(mblock);
						index = structure.blocks.size() - 1;
					}
					structure.blockEntries.computeIfAbsent(layer, l -> new ArrayList<BlockEntry>()).add(new BlockEntry(i, j, k, index));
				}
			}
		}

		return structure;
	}

	public void placeBlocks(LevelAccessor world, int x, int y, int z, int rotation) {
		Vec3 axis = new Vec3(x + sizeX * 0.5, y, z + sizeZ * 0.5);

		MutableBlockPos mpos = new MutableBlockPos();
		for (int i = BlockData.MIN_LAYER; i <= BlockData.MAX_LAYER; i++) {

			List<BlockEntry> blockList = blockEntries.get(i);
			if (blockList != null) {
				for (BlockEntry b : blockList) {
					MBlock mb = blocks.get(b.blockIndex);
					mpos.set(x + b.x, y + b.y, z + b.z);

					if (mpos.getY() > world.getMinBuildHeight()) {
						StructureBlockUtils.rotateAroundY(mpos, axis, rotation);

						BlockState state = BlockRotator.getRotatedHorizontal(mb.getState(), rotation);

						MBlock.placeState(world, mpos, state, 2);

						if (mb.hasTileEntity()) {
							mb.tileEntityPostPlacementAction(world, state, mpos, rotation);
						}
					}
				}
			}
		}
	}

	public void write(PrintWriter pw, String type) {
		pw.print("segment " + type + " " + sizeX + " " + sizeY + " " + sizeZ + " " + blocks.size() + "\n");
		for (MBlock b : blocks) {
			pw.print("=" + BlockStateParser.serialize(b.getState()) + "\n");
		}
		for (Integer l : new java.util.TreeSet<>(blockEntries.keySet())) {
			List<BlockEntry> es = blockEntries.get(l);
			pw.print("layer " + l + " " + es.size() + "\n");
			for (BlockEntry e : es) {
				pw.print(e.x + "," + e.y + "," + e.z + "," + e.blockIndex + "\n");
			}
		}
	}

	/**
	 * Reads the segment after its "segment TYPE sx sy sz npalette" line, returns the next line (or null)
	 */
	public static String read(BufferedReader br, Structure s, int paletteSize) throws IOException {
		for (int i = 0; i < paletteSize; i++) {
			String line = br.readLine().trim();
			BlockState state;
			if (line.startsWith("=")) {
				try {
					state = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), line.substring(1), false).blockState();
				} catch (Exception e) {
					state = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
				}
			} else {
				String[] p = line.split(" ");
				state = LegacyBlocks.get(p[0], Integer.parseInt(p[1]));
			}
			s.blocks.add(new MBlock(state));
		}
		String line;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("layer ")) {
				String[] p = line.split(" ");
				int layer = Integer.parseInt(p[1]);
				int n = Integer.parseInt(p[2]);
				List<BlockEntry> list = s.blockEntries.computeIfAbsent(layer, l -> new ArrayList<BlockEntry>());
				for (int i = 0; i < n; i++) {
					String[] c = br.readLine().trim().split(",");
					list.add(new BlockEntry(Integer.parseInt(c[0]), Integer.parseInt(c[1]), Integer.parseInt(c[2]), Integer.parseInt(c[3])));
				}
			} else if (!line.isEmpty()) {
				return line;
			}
		}
		return null;
	}

	public static class BlockEntry {
		int x;
		int y;
		int z;
		int blockIndex;

		public BlockEntry(int x, int y, int z, int blockIndex) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.blockIndex = blockIndex;
		}
	}
}
