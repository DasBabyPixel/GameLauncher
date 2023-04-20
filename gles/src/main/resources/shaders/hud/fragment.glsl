precision mediump float;

varying vec2 outTexCoord;

uniform int hasTexture;
uniform sampler2D texture_sampler;
uniform vec4 color;
uniform vec4 textureAddColor;

void main() {
    vec4 fc;
    if (hasTexture==1) {
        fc = texture2D(texture_sampler, outTexCoord.xy);
    } else {
        fc = vec4(1, 1, 1, 1);
    }

    gl_FragColor = color * (textureAddColor + fc);
}
