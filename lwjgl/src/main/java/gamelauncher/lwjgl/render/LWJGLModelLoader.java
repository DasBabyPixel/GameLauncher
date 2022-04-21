package gamelauncher.lwjgl.render;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import gamelauncher.engine.GameException;
import gamelauncher.engine.render.Model;
import gamelauncher.engine.render.ModelLoader;
import gamelauncher.engine.resource.Resource;

public class LWJGLModelLoader implements ModelLoader {

	@Override
	public Model loadModel(Resource resource) throws GameException {
		String content = resource.newResourceStream().readUTF8FullyClose();

		List<Vector3f> v = new ArrayList<>();
		List<Vector2f> vt = new ArrayList<>();
		List<Vector3f> vn = new ArrayList<>();
		List<Integer> i = new ArrayList<>();
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

		float[] textures = new float[v.size() * 2];
		float[] normals = new float[v.size() * 3];
		float[] vertices = new float[v.size() * 3];

		int[] indices = new int[i.size()];
		for (int j = 0; j < i.size(); j++) {
			indices[j] = i.get(j);
		}
		return null;
	}
	
	private byte[] convertModel(String content) {
		
	}

	private void loadConvertedModel(byte[] bytes) throws GameException {

	}

	private static class Face {
		private static final int indexPos = 0;
		private static final int indexTexCoord = 1;
		private static final int indexNormal = 2;
		private static final int indexGroupSize = 3;
		private static final int NO_VALUE = -1;

		private final int indexGroupCount;
		private final int[] indices;

		public Face(String[] indexGroupTokens) throws GameException {
//			String[] indexGroupTokens = face.split("\\s+");
			indexGroupCount = indexGroupTokens.length;
			indices = new int[indexGroupCount * indexGroupSize];
			Arrays.fill(indices, NO_VALUE);
			int indexGroupIndex = 0;
			for (String indexGroupToken : indexGroupTokens) {
				String[] tokens = indexGroupToken.split("/");
				setIndex(indexGroupIndex, indexPos, parseIntM1(tokens[0]));
				if (tokens.length > 1) {
					if (!tokens[1].isEmpty()) {
						setIndex(indexGroupIndex, indexTexCoord, parseIntM1(tokens[1]));
					}
					if (tokens.length > 2) {
						setIndex(indexGroupIndex, indexNormal, parseIntM1(tokens[2]));
					}
				}
				indexGroupIndex++;
			}
		}

		public void setIndex(int indexGroupIndex, int indexGroup, int index) {
			indices[indexGroupIndex * indexGroupSize + indexGroup] = index;
		}

		public int getIndex(int indexGroupIndex, int indexGroup) {
			return indices[indexGroupIndex * indexGroupSize + indexGroup];
		}
	}

	private static String hash(byte[] bytes) throws GameException {
		try (Formatter formatter = new Formatter()) {
			for (byte b : MessageDigest.getInstance("SHA-1").digest(bytes)) {
				formatter.format("%02x", b);
			}
			return formatter.toString();
		} catch (Exception ex) {
			throw new GameException(ex);
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
