#version 150

in vec3 Position;
in vec2 UV;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec2 outUV;
out vec4 outColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    outUV = UV;
    outColor = Color;
}