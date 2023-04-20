precision mediump float;

attribute vec3 position;
attribute vec2 texCoord;
attribute vec3 vertexNormal;

varying vec2 outTexCoord;

uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * modelMatrix * vec4(position.xyz, 1.0);
    outTexCoord = texCoord;
}
