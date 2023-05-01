/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.shader;

import java.util.List;
import java.util.Map;

public class ShaderConfiguration {
    public static final String DEFAULT_VERSION = "default";
    private final String version;
    private final String vertexShader;
    private final String fragmentShader;
    private final UniformMap uniforms;
    private final Structs structs;

    public ShaderConfiguration(String version, String vertexShader, String fragmentShader, UniformMap uniforms, Structs structs) {
        this.version = version;
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.uniforms = uniforms;
        this.structs = structs;
    }

    public String version() {
        return version;
    }

    public String vertexShader() {
        return vertexShader;
    }

    public String fragmentShader() {
        return fragmentShader;
    }

    public UniformMap uniforms() {
        return uniforms;
    }

    public Structs structs() {
        return structs;
    }

    public static class UniformMap {
        private final Map<String, String> typeByUniform;

        public UniformMap(Map<String, String> typeByUniform) {
            this.typeByUniform = typeByUniform;
        }

        public Map<String, String> typeByUniform() {
            return typeByUniform;
        }
    }

    public static class Structs {
        private final List<StructConfiguration> structs;

        public Structs(List<StructConfiguration> structs) {
            this.structs = structs;
        }

        public List<StructConfiguration> structs() {
            return structs;
        }
    }

    public static class StructConfiguration {
        private final String name;
        private final StructVariables variables;

        public StructConfiguration(String name, StructVariables variables) {
            this.name = name;
            this.variables = variables;
        }

        public String name() {
            return name;
        }

        public StructVariables variables() {
            return variables;
        }
    }

    public static class StructVariables {
        private final List<StructVariable> variables;

        public StructVariables(List<StructVariable> variables) {
            this.variables = variables;
        }

        public List<StructVariable> variables() {
            return variables;
        }
    }

    public static class StructVariable {
        private final String name;
        private final String type;

        public StructVariable(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String name() {
            return name;
        }

        public String type() {
            return type;
        }
    }
}
