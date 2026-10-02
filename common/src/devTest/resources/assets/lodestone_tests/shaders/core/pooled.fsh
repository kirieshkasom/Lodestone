#version 330 core
#moj_import <lodestone:common_math.glsl>
uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
in vec2 texCoord;
in vec4 vertexColor;
out vec4 fragColor;
void main() {
    fragColor = transformColor(texture(Sampler0, texCoord), 1.0, vertexColor, ColorModulator);
    if (fragColor.a < 0.01) discard;
}
