#version 300 es
precision mediump float;

in vec2 outTexCoord;

layout (location = 0) out vec4 out_Color;
//layout (location = 1) out vec4 out_Id;

uniform sampler2D Texture;
uniform int HasTexture;
uniform vec4 ColorMultiplier;
uniform vec4 TextureAddColor;
uniform lowp vec4 Id;

void main() {
    vec4 fc;
    if (HasTexture == 1) {
        fc = texture(Texture, outTexCoord.xy) + TextureAddColor;
        if (fc.w == float(0)) {
            discard;
        }
    } else {
        fc = vec4(1, 1, 1, 1);
    }

    out_Color = ColorMultiplier * (TextureAddColor + fc);
    //    out_Id = Id;
}
