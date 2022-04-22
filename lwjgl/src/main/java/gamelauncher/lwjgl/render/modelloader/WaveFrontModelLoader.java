package gamelauncher.lwjgl.render.modelloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.joml.Vector2f;
import org.joml.Vector3f;

import gamelauncher.engine.GameException;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.lwjgl.render.Mesh;

public class WaveFrontModelLoader implements ModelSubLoader {

	@Override
	public byte[] convertModel(ResourceStream in) throws GameException {
		String content = in.readUTF8FullyClose();
		List<Vector3f> v = new ArrayList<>();
		List<Vector2f> vt = new ArrayList<>();
		List<Vector3f> vn = new ArrayList<>();
		List<Face> faces = new ArrayList<>();

		for (String line : content.split("(\\r\\n|\\n)")) {
			String[] l = line.split("\\s+");
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
			texCoord[i * 2 + 1] = v2f.y;
			v3f = vn.get(group.idxNormal);
			normals[i * 3 + 0] = v3f.x;
			normals[i * 3 + 1] = v3f.y;
			normals[i * 3 + 2] = v3f.z;
			i++;
		}
		List<Integer> indexList = new ArrayList<>();
		for (Face face : faces) {
			for (int groupId = 0; groupId < face.idxGroups.length; groupId++) {
				if (groupId >= 3) {
					indexList.add(face.idxGroups[0].index);
					indexList.add(face.idxGroups[groupId - 1].index);
					indexList.add(face.idxGroups[groupId].index);
				}
			}
		}
		int[] indices = indexList.stream().mapToInt(a -> a).toArray();
		Mesh mesh = new Mesh(vertices, texCoord, normals, indices);

		return null;
	}

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
