package gamelauncher.lwjgl.render;

import java.util.ArrayList;
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
		int vertexCount = 0;
		List<String> lines = new ArrayList<>();

		for (String line : content.split("\\n")) {
			String[] l = line.split(" ");
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
				vertexCount += 3;
				lines.add(line);
			}
		}

		float[] textures = new float[vertexCount * 2];
		float[] normals = new float[vertexCount * 3];
		float[] vertices = new float[vertexCount * 3];

		int pointer = 0;
		for (String line2 : lines) {
			String[] l = line2.split(" ");
			for (int j = 1; j < 4; j++) {
				String[] vs = l[j].split("/");

				Vector3f vertice = v.get(parseIntM1(vs[0]));
				vertices[pointer * 3] = vertice.x;
				vertices[pointer * 3 + 1] = vertice.y;
				vertices[pointer * 3 + 2] = vertice.z;

				Vector2f texture = vt.get(parseIntM1(vs[1]));
				textures[pointer * 2] = texture.x;
				textures[pointer * 2 + 1] = texture.y;

				Vector3f normal = vn.get(parseIntM1(vs[2]));
				normals[pointer * 3] = normal.x;
				normals[pointer * 3 + 1] = normal.y;
				normals[pointer * 3 + 2] = normal.z;

				i.add(j);

				pointer++;
			}
		}
		int[] indices = new int[i.size()];
		for (int j = 0; j < i.size(); j++) {
			indices[j] = i.get(j);
		}
		return null;
	}

	private float parseFloat(String s) throws GameException {
		try {
			return Float.parseFloat(s);
		} catch (Exception ex) {
			throw new GameException(ex);
		}
	}

	private int parseIntM1(String s) throws GameException {
		return parseInt(s) - 1;
	}

	private int parseInt(String s) throws GameException {
		try {
			return Integer.parseInt(s);
		} catch (Exception ex) {
			throw new GameException(ex);
		}
	}
}
