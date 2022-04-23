package gamelauncher.lwjgl.render.modelloader;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import gamelauncher.engine.GameException;
import gamelauncher.engine.resource.ResourceStream;

public class MaterialList {

	public final List<Material> materials = new ArrayList<>();

	public void write(ResourceStream stream) throws GameException {
		stream.writeInt(materials.size());
		for (Material mat : materials) {
			mat.write(stream);
		}
	}

	public void read(ResourceStream stream) throws GameException {
		materials.clear();
		int size = stream.readInt();
		for (int i = 0; i < size; i++) {
			Material mat = new Material();
			materials.add(mat);
			mat.read(stream);
		}
	}

	public static class Material {
		public String name; // newmtl <name>
		public final ColorProvider ambientColor = new ColorProvider(); // Ka <color>
		public final ColorProvider diffuseColor = new ColorProvider(); // Kd <color>
		public final ColorProvider specularColor = new ColorProvider(); // Ks <color>
		public float specularComponent; // Ns <float>
		public float transparency; // Tr <transparency> | d <opacity> => Tr = 1-d
		public float indexOfRefraction; // Ni <float> Optical density
		public int illum;

		public void write(ResourceStream stream) throws GameException {
			stream.swriteUTF8(name);
			ambientColor.write(stream);
			diffuseColor.write(stream);
			specularColor.write(stream);
			stream.writeFloat(specularComponent);
			stream.writeFloat(transparency);
			stream.writeFloat(indexOfRefraction);
			stream.writeInt(illum);
		}

		public void read(ResourceStream stream) throws GameException {
			name = stream.sreadUTF8();
			ambientColor.read(stream);
			diffuseColor.read(stream);
			specularColor.read(stream);
			specularComponent = stream.readFloat();
			transparency = stream.readFloat();
			indexOfRefraction = stream.readFloat();
			illum = stream.readInt();
		}
	}

	public static class ColorProvider {
		public Vector4f color;
		public byte[] texture;

		public ColorProvider() {
			this(null, null);
		}

		public ColorProvider(Vector4f color, byte[] texture) {
			this.color = color;
			this.texture = texture;
		}

		public void write(ResourceStream stream) throws GameException {
			stream.writeFloat(color.x);
			stream.writeFloat(color.y);
			stream.writeFloat(color.z);
			stream.writeFloat(color.w);
			stream.swriteBytes(texture);
		}

		public void read(ResourceStream stream) throws GameException {
			color = new Vector4f();
			color.x = stream.readFloat();
			color.y = stream.readFloat();
			color.z = stream.readFloat();
			color.w = stream.readFloat();
			texture = stream.sreadBytes();
		}
	}
}
