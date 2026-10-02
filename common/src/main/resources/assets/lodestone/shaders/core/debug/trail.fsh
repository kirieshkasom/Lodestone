#version 150

in vec2 outUV;
in vec4 outColor;

uniform sampler2D Sampler0;

out vec4 fragColor;

void main() {
    fragColor = outColor;
    fragColor.a *= outUV.x;
}