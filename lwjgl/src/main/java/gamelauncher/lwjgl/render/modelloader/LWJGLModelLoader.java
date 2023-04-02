package gamelauncher.lwjgl.render.modelloader;

import gamelauncher.engine.io.Files;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.model.ModelLoader;
import gamelauncher.engine.resource.Resource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.mesh.Mesh;
import gamelauncher.lwjgl.render.model.MeshModel;
import gamelauncher.lwjgl.render.modelloader.MaterialList.Material;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DasBabyPixel
 */
public class LWJGLModelLoader implements ModelLoader {

	private static final Logger logger = Logger.logger(LWJGLModelLoader.class);
	private final Map<ModelType, ModelSubLoader> loaders = new HashMap<>();
	private final LWJGLGameLauncher launcher;
	private final Path modelDirectory;
	private final String version = "0.0.11";

	/**
	 * @param launcher
	 *
	 * @throws GameException
	 */
	public LWJGLModelLoader(LWJGLGameLauncher launcher) throws GameException {
		this.launcher = launcher;
		this.modelDirectory = this.launcher.dataDirectory().resolve("models");
		Files.createDirectories(this.modelDirectory);

		this.loaders.put(ModelType.WAVEFRONT, new WaveFrontModelLoader(launcher));
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

	@Override
	public Model loadModel(Resource resource) throws GameException {
		ResourceStream stream = resource.newResourceStream();
		String hash = hash(stream.readAllBytes());
		stream.cleanup();
		Path file = modelDirectory.resolve(
						stream.getPath().getParent().toAbsolutePath().toString().substring(1))
				.resolve(stream.getPath().getFileName() + ".bin");
		boolean hasSavedFile = false;
		if (Files.exists(file)) {
			stream = new ResourceStream(null, false, Files.newInputStream(file), null);
			String version = stream.readUTF8(stream.readInt());
			String savedHash = stream.readUTF8(stream.readInt());

			if (savedHash.equals(hash)) {
				hasSavedFile = true;
			} else {
				logger.infof("Model changed");
			}
			if (!version.equals(this.version)) {
				logger.infof("New Version for Model: %s (Old: %s)", this.version, version);
				hasSavedFile = false;
			}
		}
		if (hasSavedFile) {
			return loadConvertedModel(stream);
		}
		ModelType type = ModelType.WAVEFRONT;
		ModelSubLoader loader = loaders.get(type);
		stream = resource.newResourceStream();
		byte[] bytes = loader.convertModel(stream);
		if (!stream.cleanedUp())
			stream.cleanup();
		if (!Files.exists(file)) {
			Files.createFile(file);
		}
		stream = new ResourceStream(file, false, new ByteArrayInputStream(bytes),
				Files.newOutputStream(file));
		saveConvertedModel(hash, bytes, stream);
		return loadConvertedModel(stream);
	}

	private void saveConvertedModel(String hash, byte[] bytes, ResourceStream stream)
			throws GameException {
		stream.writeInt(this.version.length());
		stream.writeUTF8(this.version);
		stream.writeInt(hash.length());
		stream.writeUTF8(hash);
		stream.writeBytes(bytes);
	}

	private Model loadConvertedModel(ResourceStream stream) throws GameException {
		float[] vertices = stream.sreadFloats();
		float[] texCoords = stream.sreadFloats();
		float[] normals = stream.sreadFloats();
		int[] indices = stream.sreadInts();
		MaterialList materialList = new MaterialList();
		materialList.read(stream);

		stream.cleanup();
		Mesh mesh = new Mesh(vertices, texCoords, normals, indices);
		Mesh.Material lm = mesh.material();

		if (lm.texture == null) {
			for (Material mat : materialList.materials) {
				byte[] tex = mat.diffuseColor.texture;
				if (tex != null) {
					ResourceStream st =
							new ResourceStream(null, false, new ByteArrayInputStream(tex), null);
					LWJGLTexture lt = launcher.textureManager().createTexture();
					lt.uploadAsync(st).thenRun(launcher.guiManager()::redraw);
					lm.texture = lt;
					break;
				}
			}
		}
		if (lm.texture == null) {
			for (Material mat : materialList.materials) {
				lm.diffuseColour = mat.diffuseColor.color;
				lm.ambientColour = mat.ambientColor.color;
				lm.specularColour = mat.specularColor.color;
				break;
			}
		}
		return new MeshModel(mesh);
	}

}
