#version 150

in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

uniform vec3 CameraPos;

out vec3 worldPos;

void main() {
    worldPos = Position + CameraPos;
    vec4 pos = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * pos;
}
