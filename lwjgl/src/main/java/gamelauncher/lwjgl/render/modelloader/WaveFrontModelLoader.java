package gamelauncher.lwjgl.render.modelloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.io.Files;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;

@SuppressWarnings("javadoc")
public class WaveFrontModelLoader implements ModelSubLoader {

	private final GameLauncher launcher;
//	private final Random random = new Random();

	public WaveFrontModelLoader(GameLauncher launcher) {
		this.launcher = launcher;
	}

	@Override
	public byte[] convertModel(ResourceStream in) throws GameException {
		String content = in.readUTF8FullyClose();
		List<Vector3f> v = new ArrayList<>();
		List<Vector2f> vt = new ArrayList<>();
		List<Vector3f> vn = new ArrayList<>();
		List<Face> faces = new ArrayList<>();
		MaterialList materialList = new MaterialList();

		for (String line : content.split("\\R")) {
			String[] l = line.split("\\s");
			if (l[0].equals("v")) {
				Vector3f vec = new Vector3f(parseFloat(l[1]), parseFloat(l[2]), parseFloat(l[3]));
				v.add(vec);
			} else if (l[0].equals("vt")) {
				Vector2f vec = new Vector2f(parseFloat(l[1]), parseFloat(l[2]));
				vt.add(vec);
			} else if (l[0].equals("vn")) {
				Vector3f vec = new Vector3f(parseFloat(l[1]), parseFloat(l[2]), parseFloat(l[3]));
				vn.add(vec);
			} else if (l[0].equals("f")) {
				faces.add(new Face(Arrays.copyOfRange(l, 1, l.length)));
			} else if (l[0].equals("mtllib")) {
				String fileName = line.split("\\s", 2)[1];
				Path file = in.getPath().getParent().resolve(fileName);
				String mtlcontent = Files.readUTF8(file);
				MaterialList.Material material = null;
				launcher.getLogger().infof("Material: %n%s", mtlcontent);
				for (String mtlline : mtlcontent.split("\\R")) {
					if (mtlline.startsWith("#")) {
						continue;
					}
					String[] mtll = mtlline.split("\\s");
					if (mtll[0].equals("newmtl")) {
						material = new MaterialList.Material();
						materialList.materials.add(material);
						material.name = mtlline.split("\\s", 2)[1];
					}
					if (material == null) {
						continue;
					}
					if (mtll[0].equals("Ns")) {
						material.specularComponent = parseFloat(mtll[1]);
					} else if (mtll[0].equals("Ka")) {
						material.ambientColor.color = new Vector4f(parseFloat(mtll[1]), parseFloat(mtll[2]),
								parseFloat(mtll[3]), 1F);
					} else if (mtll[0].equals("Kd")) {
						material.diffuseColor.color = new Vector4f(parseFloat(mtll[1]), parseFloat(mtll[2]),
								parseFloat(mtll[3]), 1F);
					} else if (mtll[0].equals("Ks")) {
						material.specularColor.color = new Vector4f(parseFloat(mtll[1]), parseFloat(mtll[2]),
								parseFloat(mtll[3]), 1F);
					} else if (mtll[0].equals("Ni")) {
						material.indexOfRefraction = parseFloat(mtll[1]);
					} else if (mtll[0].equals("d")) {
						material.transparency = 1 - parseFloat(mtll[1]);
					} else if (mtll[0].equals("Tr")) {
						material.transparency = parseFloat(mtll[1]);
					} else if (mtll[0].equals("illum")) {
						material.illum = parseInt(mtll[1]);
					} else if (mtll[0].startsWith("map_")) {
						String[] mtla = mtlline.split("\\s", 2);
						String map = mtla[1];
						Path mapFile = file.resolveSibling(map);
						byte[] texture = Files.readAllBytes(mapFile);
						if (mtll[0].equals("map_Kd")) {
							material.diffuseColor.texture = texture;
						} else if (mtll[0].equals("map_Ka")) {
							material.ambientColor.texture = texture;
						} else if (mtll[0].equals("map_Ks")) {
							material.specularColor.texture = texture;
						}
					}
				}
			}
		}
		List<IndexGroup> groups = new ArrayList<>();
		int index = 0;
		for (Face face : faces) {
			for (int i = 0; i < face.idxGroups.length; i++) {
				IndexGroup g = face.idxGroups[i];
				if (!groups.contains(g)) {
					g.index = index;
					groups.add(g);
					index++;
				} else {
					face.idxGroups[i] = groups.get(groups.indexOf(g));
				}
			}
		}
		float[] vertices = new float[groups.size() * 3];
		float[] texCoord = new float[groups.size() * 2];
		float[] normals = new float[groups.size() * 3];
		int i = 0;
		for (IndexGroup group : groups) {
			Vector3f v3f = v.get(group.idxVertex);
			vertices[i * 3 + 0] = v3f.x;
			vertices[i * 3 + 1] = v3f.y;
			vertices[i * 3 + 2] = v3f.z;
			Vector2f v2f = vt.get(group.idxTexCoord);
			texCoord[i * 2 + 0] = v2f.x;
			texCoord[i * 2 + 1] = 1 - v2f.y;
			v3f = vn.get(group.idxNormal);
			normals[i * 3 + 0] = v3f.x;
			normals[i * 3 + 1] = v3f.y;
			normals[i * 3 + 2] = v3f.z;
			i++;
		}
//		addRandomMinimals(vertices);
		List<Integer> indexList = new ArrayList<>();
		for (Face face : faces) {
			for (int groupId = 0; groupId < face.idxGroups.length; groupId++) {
				if (groupId >= 2) {
					indexList.add(face.idxGroups[0].index);
					indexList.add(face.idxGroups[groupId - 1].index);
					indexList.add(face.idxGroups[groupId].index);
				}
			}
		}
		int[] indices = indexList.stream().mapToInt(a -> a).toArray();
//		Mesh mesh = new Mesh(vertices, texCoord, normals, indices);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (ResourceStream r = new ResourceStream(null, false, null, out)) {
			r.swriteFloats(vertices);
			r.swriteFloats(texCoord);
			r.swriteFloats(normals);
			r.swriteInts(indices);

			materialList.write(r);

			return out.toByteArray();
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

//	private void addRandomMinimals(float[] a) {
//		for (int i = 0; i < a.length; i++) {
//			float r = random.nextFloat() - 0.5F;
//			r = r / 10000F;
//			a[i] += r;
//		}
//	}

	private static class Face {

		private final IndexGroup[] idxGroups;

		public Face(String[] indexGroupTokens) throws GameException {
			idxGroups = new IndexGroup[indexGroupTokens.length];
			int i = 0;
			for (String indexGroupToken : indexGroupTokens) {
				String[] tokens = indexGroupToken.split("/");
				int idxVertex = parseIntM1(tokens[0]);
				int idxTexCoord = IndexGroup.NO_VALUE;
				int idxNormal = IndexGroup.NO_VALUE;
				if (tokens.length > 1) {
					if (!tokens[1].isEmpty()) {
						idxTexCoord = parseIntM1(tokens[1]);
					}
					if (tokens.length > 2) {
						idxNormal = parseIntM1(tokens[2]);
					}
				}
				idxGroups[i] = new IndexGroup(idxVertex, idxTexCoord, idxNormal);
				i++;
			}
		}

		@Override
		public String toString() {
			return "Face [idxGroups=" + Arrays.toString(idxGroups) + "]";
		}
	}

	private static class IndexGroup {
		private static final int NO_VALUE = -1;

		private final int idxVertex;
		private final int idxTexCoord;
		private final int idxNormal;
		private int index;

		public IndexGroup(int idxVertex, int idxTexCoord, int idxNormal) {
			this.idxVertex = idxVertex;
			this.idxTexCoord = idxTexCoord;
			this.idxNormal = idxNormal;
		}

		@Override
		public int hashCode() {
			return Objects.hash(idxNormal, idxTexCoord, idxVertex);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IndexGroup other = (IndexGroup) obj;
			return idxNormal == other.idxNormal && idxTexCoord == other.idxTexCoord && idxVertex == other.idxVertex;
		}

		@Override
		public String toString() {
			return "IndexGroup [idxVertex=" + idxVertex + ", idxTexCoord=" + idxTexCoord + ", idxNormal=" + idxNormal
					+ ", index=" + index + "]";
		}
	}

	private static float parseFloat(String s) throws GameException {
		try {
			return Float.parseFloat(s);
		} catch (Exception ex) {
			throw new GameException(ex);
		}
	}

	private static int parseIntM1(String s) throws GameException {
		return parseInt(s) - 1;
	}

	private static int parseInt(String s) throws GameException {
		try {
			return Integer.parseInt(s);
		} catch (Exception ex) {
			throw new GameException(ex);
		}
	}
}
