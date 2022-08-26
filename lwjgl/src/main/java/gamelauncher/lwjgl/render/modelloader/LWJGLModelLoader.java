package gamelauncher.lwjgl.render.modelloader;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import gamelauncher.engine.io.Files;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.model.ModelLoader;
import gamelauncher.engine.resource.Resource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.mesh.Mesh;
import gamelauncher.lwjgl.render.model.MeshModel;
import gamelauncher.lwjgl.render.modelloader.MaterialList.Material;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

/**
 * @author DasBabyPixel
 */
public class LWJGLModelLoader implements ModelLoader {

	private static final Logger logger = Logger.getLogger(LWJGLModelLoader.class);
	private final Map<ModelType, ModelSubLoader> loaders = new HashMap<>();
	private final LWJGLGameLauncher launcher;
	private final Path modelDirectory;
	private final String version = "0.0.11";

	/**
	 * @param launcher
	 * @throws GameException
	 */
	public LWJGLModelLoader(LWJGLGameLauncher launcher) throws GameException {
		this.launcher = launcher;
		this.modelDirectory = this.launcher.getDataDirectory().resolve("models");
		Files.createDirectories(this.modelDirectory);

		this.loaders.put(ModelType.WAVEFRONT, new WaveFrontModelLoader(launcher));
	}

	@Override
	public Model loadModel(Resource resource) throws GameException {
		ResourceStream stream = resource.newResourceStream();
		String hash = hash(stream.readAllBytes());
		stream.cleanup();
		Path file = modelDirectory.resolve(stream.getPath().getParent().toAbsolutePath().toString().substring(1))
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
			Model model = loadConvertedModel(stream);
			stream.cleanup();
			return model;
		}
		ModelType type = ModelType.WAVEFRONT;
		ModelSubLoader loader = loaders.get(type);
		stream = resource.newResourceStream();
		byte[] bytes = loader.convertModel(stream);
		stream.cleanup();
		if (!Files.exists(file)) {
			Files.createFile(file);
		}
		stream = new ResourceStream(file, false, new ByteArrayInputStream(bytes), Files.newOutputStream(file));
		saveConvertedModel(hash, bytes, stream);
		Model model = loadConvertedModel(stream);
		stream.cleanup();
		return model;
	}

	private void saveConvertedModel(String hash, byte[] bytes, ResourceStream stream) throws GameException {
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
		Mesh.Material lm = mesh.getMaterial();

		if (lm.texture == null) {
			for (Material mat : materialList.materials) {
				byte[] tex = mat.diffuseColor.texture;
				if (tex != null) {
					ResourceStream st = new ResourceStream(null, false, new ByteArrayInputStream(tex), null);
					LWJGLTexture lt = Threads.waitFor(launcher.getTextureManager().createTexture());
					Threads.waitFor(lt.uploadAsync(st));
					lm.texture = lt;
					st.cleanup();
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

}
