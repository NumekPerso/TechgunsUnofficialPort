package techguns.world.dungeon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import techguns.world.Structure;
import techguns.world.dungeon.TemplateSegment.SegmentType;

/**
 * Dungeon template: one scanned Structure per segment type.
 * 1.20: the 1.12 java serialized .ser files were converted to text files (assets/techguns/dungeons/NAME.txt, see
 * Structure for the format); templates scanned by the dungeon scanner are saved in ./templates/NAME.txt and loaded
 * from there too (debug, like 1.12 loadTemplates_debug).
 */
public class DungeonTemplate {
	private static final Logger LOGGER = LogUtils.getLogger();

	//local only for testing
	public static final String SCAN_DIR = "./templates/";

	private static final String DUNGEON_TEMPLATE_DIR = "/assets/techguns/dungeons/";
	private static final String[] TEMPLATE_LIST = new String[] { "ncdung1", "nclower1", "ncmid1", "ncupper1", "nctop1", "ncroof1", //castle
			"nether0", "nether1", "nether_b0", "nether_b1", //nether castle
			"tfmain", "tfupper" }; //tech fortress

	protected ResourceLocation LOOTTABLE = null;

	public static Map<String, DungeonTemplate> dungeonTemplates = new HashMap<>();

	private static boolean loaded = false;

	public int sizeXZ;
	public int sizeY;

	public HashMap<SegmentType, DungeonSegment> segments = new HashMap<>();

	public String name;

	public static synchronized void init() {
		if (!loaded) {
			loaded = true;
			loadTemplates();
			loadTemplates_debug();
		}
	}

	/**
	 * Names of the available templates without loading them (GUIs on the client)
	 */
	public static java.util.List<String> getTemplateNames() {
		java.util.TreeSet<String> names = new java.util.TreeSet<>(java.util.Arrays.asList(TEMPLATE_LIST));
		names.addAll(dungeonTemplates.keySet());
		File[] files = new File(SCAN_DIR).listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.getName().endsWith(".txt")) {
					names.add(f.getName().substring(0, f.getName().length() - 4));
				}
			}
		}
		return new java.util.ArrayList<>(names);
	}

	public static DungeonTemplate get(String name) {
		init();
		return dungeonTemplates.get(name);
	}

	public void applySpecialMBlocks() {
		this.segments.values().forEach(s -> s.structure.applySpecialMBlocks(LOOTTABLE));
	}

	public DungeonTemplate setLoottable(ResourceLocation loottable) {
		LOOTTABLE = loottable;
		this.applySpecialMBlocks();
		return this;
	}

	public DungeonTemplate(int sizeXZ, int sizeY) {
		super();
		this.sizeXZ = sizeXZ;
		this.sizeY = sizeY;
	}

	public void placeTemplate(LevelAccessor world, int posX, int posY, int posZ) {
		for (DungeonSegment segment : segments.values()) {
			if (segment.template == null)
				segment.template = this;
			segment.placeTemplateSegment(world, posX, posY, posZ, 0);
		}
	}

	public static DungeonTemplate scanTemplate(LevelAccessor world, int posX, int posY, int posZ, int sizeXZ, int sizeY, String name) {
		init();
		DungeonTemplate template = new DungeonTemplate(sizeXZ, sizeY);
		template.name = name;

		for (SegmentType type : TemplateSegment.templateSegments.keySet()) {
			DungeonSegment segment = new DungeonSegment(template, type);
			segment.scanBlocks(world, posX, posY, posZ);
			template.segments.put(type, segment);
		}

		dungeonTemplates.put(name, template);

		return template;
	}

	public void save(File file) throws IOException {
		file.getParentFile().mkdirs();
		try (PrintWriter pw = new PrintWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
			pw.print("template " + sizeXZ + " " + sizeY + "\n");
			for (Map.Entry<SegmentType, DungeonSegment> e : new TreeMap<>(segments).entrySet()) {
				e.getValue().structure.write(pw, e.getKey().name());
			}
		}
	}

	public static DungeonTemplate load(InputStream is, String name) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		String[] header = br.readLine().trim().split(" ");
		DungeonTemplate template = new DungeonTemplate(Integer.parseInt(header[1]), Integer.parseInt(header[2]));
		template.name = name;
		String line = br.readLine();
		while (line != null) {
			line = line.trim();
			if (line.startsWith("segment ")) {
				String[] p = line.split(" ");
				SegmentType type = SegmentType.valueOf(p[1]);
				Structure s = new Structure(Integer.parseInt(p[2]), Integer.parseInt(p[3]), Integer.parseInt(p[4]));
				line = Structure.read(br, s, Integer.parseInt(p[5]));
				DungeonSegment seg = new DungeonSegment(template, type);
				seg.structure = s;
				template.segments.put(type, seg);
			} else {
				line = br.readLine();
			}
		}
		return template;
	}

	public static void loadTemplates() {
		for (String filename : TEMPLATE_LIST) {
			String path = DUNGEON_TEMPLATE_DIR + filename + ".txt";
			try (InputStream is = DungeonTemplate.class.getResourceAsStream(path)) {
				if (is == null) {
					LOGGER.error("Techguns: missing dungeon template {}", path);
					continue;
				}
				DungeonTemplate template = load(is, filename);
				template.applySpecialMBlocks();
				dungeonTemplates.put(filename, template);
			} catch (Exception e) {
				LOGGER.error("Techguns: could not load dungeon template {}", path, e);
			}
		}
	}

	public static void loadTemplates_debug() {
		File folder = new File(SCAN_DIR);
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getName().endsWith(".txt")) {
					try (FileInputStream streamIn = new FileInputStream(file)) {
						String name = file.getName().substring(0, file.getName().length() - 4);
						DungeonTemplate template = load(streamIn, name);
						template.applySpecialMBlocks();
						dungeonTemplates.put(name, template);
					} catch (Exception e) {
						LOGGER.error("Techguns: could not load dungeon template {}", file, e);
					}
				}
			}
		}
	}

	public String getName() {
		return name;
	}

}
