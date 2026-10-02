#version 150

in vec2 texCoord;
in vec4 Color;

uniform sampler2D Sampler0;

out vec4 fragColor;

void main() {
    fragColor = texture(Sampler0, texCoord);
}