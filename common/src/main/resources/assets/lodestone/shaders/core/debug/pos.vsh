#version 150

in vec3 Position;
in vec2 UV0;

in vec3 InstancedPosition;
in vec4 InstancedColor;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

uniform vec3 CamLeft;
uniform vec3 CamUp;

out vec2 texCoord;
out vec4 Color;

void main() {
    vec3 localPos = (CamLeft * Position.x) + (CamUp * Position.y);
    vec3 worldPos = InstancedPosition + localPos;
    gl_Position = ProjMat * ModelViewMat * vec4(worldPos, 1.0);

    texCoord = UV0;
    Color = InstancedColor;
}
