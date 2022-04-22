package gamelauncher.lwjgl.render.modelloader;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.file.Path;
import gamelauncher.engine.render.Model;
import gamelauncher.engine.render.ModelLoader;
import gamelauncher.engine.resource.Resource;
import gamelauncher.engine.resource.ResourceStream;

public class LWJGLModelLoader implements ModelLoader {

	private final Map<ModelType, ModelSubLoader> loaders = new HashMap<>();
	private final GameLauncher launcher;
	private final Path modelDirectory;

	public LWJGLModelLoader(GameLauncher launcher) throws GameException {
		this.launcher = launcher;
		this.modelDirectory = this.launcher.getDataDirectory().resolve("models");
		this.launcher.getFileSystem().createDirectories(this.modelDirectory);

		this.loaders.put(ModelType.WAVEFRONT, new WaveFrontModelLoader());
	}

	@Override
	public Model loadModel(Resource resource) throws GameException {
		ResourceStream stream = resource.newResourceStream();
		String hash = hash(stream.readAllBytes());
		stream.cleanup();
		Path file = modelDirectory.resolve(stream.getPath().getPath() + ".bin");
		boolean hasSavedFile = false;
		if (launcher.getFileSystem().exists(file)) {
			stream = new ResourceStream(null, false, launcher.getFileSystem().createInputStream(file), null);
			String savedHash = stream.readUTF8(stream.readInt());

			if (savedHash.equals(hash)) {
				hasSavedFile = true;
			}
		}
		if (hasSavedFile) {
			Model model = loadConvertedModel(stream);
			stream.cleanup();
			return model;
		}
		ModelType type = ModelType.WAVEFRONT;
		ModelSubLoader loader = loaders.get(type);
		byte[] bytes = loader.convertModel(stream);
		stream.cleanup();
		launcher.getFileSystem().createFile(file);
		stream = new ResourceStream(null, false, null, launcher.getFileSystem().createOutputStream(file));
		saveConvertedModel(hash, bytes, stream);
		stream.cleanup();
		stream = new ResourceStream(null, false, new ByteArrayInputStream(bytes), null);
		Model model = loadConvertedModel(stream);
		stream.cleanup();
		return model;
	}

	private void saveConvertedModel(String hash, byte[] bytes, ResourceStream stream) throws GameException {
		stream.writeInt(hash.length());
		stream.writeUTF8(hash);
		stream.writeBytes(bytes);
	}

	private Model loadConvertedModel(ResourceStream stream) throws GameException {
		byte[] bytes = stream.readAllBytes();
		stream.cleanup();
		return null;
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
