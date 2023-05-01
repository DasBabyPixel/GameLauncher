#version 100
precision mediump float;

varying vec2 outTexCoord;

uniform int hasTexture;
uniform sampler2D texture_sampler;
uniform vec4 color;
uniform vec4 textureAddColor;
uniform int idIndex;
uniform lowp vec4 id;
uniform lowp int idIndex;

void main() {
    vec4 fc;
    if (hasTexture==1) {
        fc = texture2D(texture_sampler, outTexCoord.xy);
        if (fc.w == float(0)) {
            discard;
        }
    } else {
        fc = vec4(1, 1, 1, 1);
    }

    gl_FragData[0] = color * (textureAddColor + fc);
    if (idIndex!=-1) gl_FragData[gl_MaxDrawBuffers-1] = id;
}
