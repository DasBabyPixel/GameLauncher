package gamelauncher.engine.render.light;

import gamelauncher.engine.render.shader.ProgramObject;
import gamelauncher.engine.render.shader.ShaderProgram;
import org.joml.Vector3f;

public class PointLight implements ProgramObject {

    public final Vector3f color;
    public final Vector3f position;
    public final float intensity;
    public final Attenuation att;

    public PointLight(Vector3f color, Vector3f position, float intensity) {
        this(color, position, intensity, new Attenuation(1, 0, 0));
    }

    public PointLight(Vector3f color, Vector3f position, float intensity, Attenuation attenuation) {
        this.color = color;
        this.position = position;
        this.intensity = intensity;
        this.att = attenuation;
    }

    public PointLight(PointLight pointLight) {
        this(new Vector3f(pointLight.color), new Vector3f(pointLight.position), pointLight.intensity, pointLight.att);
    }

    @Override public void upload(ShaderProgram program, String name) {
        program.uniformMap.get(name + ".color").set(color).upload();
        program.uniformMap.get(name + ".position").set(position).upload();
        program.uniformMap.get(name + ".intensity").set(intensity).upload();
        program.uniformMap.get(name + ".att").set(att).upload();
    }

    public static class Attenuation implements ProgramObject {
        public float constant;
        public float linear;
        public float exponent;

        public Attenuation() {
        }

        public Attenuation(float constant, float linear, float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        @Override public void upload(ShaderProgram program, String name) {
            program.uniformMap.get(name + ".constant").set(constant).upload();
            program.uniformMap.get(name + ".linear").set(linear).upload();
            program.uniformMap.get(name + ".exponent").set(exponent).upload();
        }
    }
}
