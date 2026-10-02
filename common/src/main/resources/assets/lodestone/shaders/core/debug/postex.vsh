#version 150

in vec3 Position;
in vec2 UV0;

in mat4 InstancedModelMat;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec2 texCoord;
out vec4 color;

void main() {
    gl_Position = ProjMat * ModelViewMat * InstancedModelMat * vec4(Position, 1.0);

    texCoord = UV0;
    color = vec4(1);
}
