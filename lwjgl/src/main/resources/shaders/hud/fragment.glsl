#version 320 es
precision highp float;

in vec2 outTexCoord;
in vec3 mvPos;
out vec4 fragColor;

uniform int hasTexture;
uniform sampler2D texture_sampler;
uniform vec4 color;
uniform vec4 textureAddColor;

void main() {
    vec4 fc;
    if (hasTexture==1) {
        fc = texture(texture_sampler, outTexCoord);
    } else {
        fc = vec4(1, 1, 1, 1);
    }

    fragColor = color * (textureAddColor + fc);
    //fragColor = textureAddColor + fc;
}
