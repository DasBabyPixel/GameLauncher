package game.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import game.render.shader.ShaderProgram;
import game.render.shader.ShaderProgram.Shader;

public class ResourceStream implements AutoCloseable {

	private final ResourcePath path;
	private final boolean directory;
	private final InputStream in;
	private final OutputStream out;

	public ResourceStream(ResourcePath path, boolean directory, InputStream in, OutputStream out) {
		this.path = path;
		this.directory = directory;
		this.in = in;
		this.out = out;
	}

	public boolean hasInputStream() {
		return in != null;
	}

	public InputStream getInputStream() {
		return in;
	}

	public boolean hasOutputStream() {
		return out != null;
	}

	public OutputStream getOutputStream() {
		return out;
	}

	@Override
	public void close() throws IOException {
		if (in != null) {
			in.close();
		}
		if (out != null) {
			out.close();
		}
	}

	public ShaderProgram loadProgram() {
		if (!isDirectory()) {
			throw new UnsupportedOperationException("Cant load a ShaderProgram from a file!");
		}
		Shader vertex = null;
		Shader fragment = null;
		try {
			ResourceStream stream = new ResourcePath(getPath().getPath().concat("vertex.glsl")).newResourceStream();
			vertex = stream.loadShader(Shader.Type.VERTEX);
			stream.close();
			stream = new ResourcePath(getPath().getPath().concat("fragment.glsl")).newResourceStream();
			fragment = stream.loadShader(Shader.Type.FRAGMENT);
			stream.close();
			ShaderProgram program = new ShaderProgram(vertex, fragment);
			vertex.delete();
			fragment.delete();
			return program;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public Shader loadShader(Shader.Type type) throws IOException {
		return new Shader(type, readUTF8Fully());
	}

	public String readUTF8(int length) throws IOException {
		return new String(readBytes(length), StandardCharsets.UTF_8);
	}

	public String readUTF8Fully() throws IOException {
		return new String(readAllBytes(), StandardCharsets.UTF_8);
	}

	public int readBytes(byte[] bytes) throws IOException {
		return in.read(bytes);
	}

	public byte[] readAllBytes() throws IOException {
		byte[] buffer = new byte[16384];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read;
		while ((read = readBytes(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		return out.toByteArray();
	}

	public byte[] readBytes(int length) throws IOException {
		byte[] bytes = new byte[length];
		length = readBytes(bytes);
		return Arrays.copyOf(bytes, length);
	}

	public boolean isDirectory() {
		return directory;
	}

	public ResourcePath getPath() {
		return path;
	}
}
