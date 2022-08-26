package gamelauncher.lwjgl.render.mesh;

import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.joml.Vector4f;

import gamelauncher.engine.render.shader.ProgramObject;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.states.GlStates;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

@SuppressWarnings("javadoc")
public class Mesh extends AbstractGameResource {

//	private static final Vector4f emptyColor = new Vector4f(1, 1, 1, 1);

	private final List<Integer> vbos = new ArrayList<>();
	private final int vaoId;
	private final int vertexCount;
	private final Material material = new Material();
	private final int vaoSize;
	private final int renderType;

	public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices, int renderType) {
		vertexCount = indices.length;
		this.renderType = renderType;
		
		GlStates cur = GlStates.current();

		vaoId = cur.genVertexArrays();
		cur.bindVertexArray(vaoId);

		createBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW, indices);

		createBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, positions);
		cur.vertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

		createBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, textCoords);
		cur.vertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

		createBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, normals);
		cur.vertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

		vaoSize = 3;

		cur.bindVertexArray(0);
	}

	public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
		this(positions, textCoords, normals, indices, GL_TRIANGLES);
	}

	public Material getMaterial() {
		return material;
	}

	private int createBuffer(int target, int usage, int[] array) {
		int id = createBuffer(target);
		upload(target, usage, array);
		return id;
	}

	private int createBuffer(int target, int usage, float[] array) {
		int id = createBuffer(target);
		upload(target, usage, array);
		return id;
	}

	private int createBuffer(int target) {
		int id = GlStates.current().genBuffers();
		GlStates.current().bindBuffer(target, id);
		vbos.add(id);
		return id;
	}

	private void upload(int target, int usage, int[] array) {
		IntBuffer buffer = memAllocInt(array.length);
		buffer.put(array).flip();
		GlStates.current().bufferData(target, buffer, usage);
		memFree(buffer);
	}

	private void upload(int target, int usage, float[] array) {
		FloatBuffer buffer = memAllocFloat(array.length);
		buffer.put(array).flip();
		GlStates.current().bufferData(target, buffer, usage);
		memFree(buffer);
	}

	public void render() {
		if (material.texture != null) {
			GlStates.current().activeTexture(GL_TEXTURE0);
			GlStates.current().bindTexture(GL_TEXTURE_2D, material.texture.getTextureId());
//			glBindTexture(GL_TEXTURE_2D, material.texture.getTextureId());
		}

		GlStates cur = GlStates.current();
		cur.bindVertexArray(getVaoId());
		for (int i = 0; i < vaoSize; i++) {
			cur.enableVertexAttribArray(i);
		}
		cur.drawElements(this.renderType, getVertexCount(), GL_UNSIGNED_INT, 0);

		for (int i = 0; i < vaoSize; i++) {
			cur.disableVertexAttribArray(i);
		}
	}

	public int getVaoId() {
		return vaoId;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	@Override
	public void cleanup0() throws GameException {
		// Delete the VBOs
		for (int vbo : vbos) {
			GlStates.current().deleteBuffers(vbo);
		}
		vbos.clear();
		// Delete the VAO
		GlStates.current().deleteVertexArrays(vaoId);
	}

	public static class Material implements ProgramObject {
		private static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
		public Vector4f ambientColour;
		public Vector4f diffuseColour;
		public Vector4f specularColour;
		public float reflectance;
		public LWJGLTexture texture;

		public Material() {
			this.ambientColour = DEFAULT_COLOUR;
			this.diffuseColour = DEFAULT_COLOUR;
			this.specularColour = DEFAULT_COLOUR;
			this.texture = null;
			this.reflectance = 0;
		}

		public Material(Vector4f colour, float reflectance) {
			this(colour, colour, colour, null, reflectance);
		}

		public Material(LWJGLTexture texture) {
			this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, 0);
		}

		public Material(LWJGLTexture texture, float reflectance) {
			this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, reflectance);
		}

		public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour, LWJGLTexture texture,
				float reflectance) {
			this.ambientColour = ambientColour;
			this.diffuseColour = diffuseColour;
			this.specularColour = specularColour;
			this.texture = texture;
			this.reflectance = reflectance;
		}

		@Override
		public void upload(ShaderProgram program, String name) {
			program.uniformMap.get(name + ".ambient").set(ambientColour).upload();
			program.uniformMap.get(name + ".diffuse").set(diffuseColour).upload();
			program.uniformMap.get(name + ".specular").set(specularColour).upload();
			program.uniformMap.get(name + ".reflectance").set(reflectance).upload();
			program.uniformMap.get(name + ".hasTexture").set(texture == null ? 0 : 1).upload();
		}

		public static final Comparator<Material> COMPARATOR = new Comparator<Mesh.Material>() {
			@Override
			public int compare(Material o1, Material o2) {
				if (o1.texture != null) {
					if (o2.texture == null) {
						return 1;
					}
					return Integer.compare(o1.texture.hashCode(), o2.texture.hashCode());
				}
				return 0;
			}
		};
	}

	public boolean applyLighting() {
		return true;
	}

//	public boolean hasTexture() {
//		return texture != null;
//	}
}
