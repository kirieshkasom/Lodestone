#version 330 core

#moj_import <lodestone:common_math.glsl>

in vec3 Position;
//in mat4 InstancedMatrix;
in vec4 InstancedPos;

uniform mat4 ModelViewMat;
uniform mat3 IViewRotMat;
uniform mat4 ProjMat;
uniform int FogShape;

out vec4 vertexColor;
out float vertexDistance;

vec3 offsetPositionByInstance(int instanceID) {
    // 1000 Instances = 10x10x10 grid
    int x = instanceID % 100;
    int y = (instanceID / 100) % 100;
    int z = instanceID / 10000;
    return vec3(x, y, z) * 3.0;
}

void main() {
    vec4 colors[3] = vec4[3](
    vec4(1.0, 0.0, 0.0, 1.0),  // Red for gl_VertexID == 0
    vec4(0.0, 1.0, 0.0, 1.0),  // Green for gl_VertexID == 1
    vec4(0.0, 0.0, 1.0, 1.0)   // Blue for gl_VertexID == 2
    );

    vec4 pos = vec4(Position, 1.0);
    //pos.xyz += offsetPositionByInstance(gl_InstanceID);
    pos.xyz += InstancedPos.xyz;
    gl_Position = ProjMat * ModelViewMat * pos;

    vertexDistance = fogDistance(pos.xyz, FogShape);
    vertexColor = colors[gl_VertexID % 3] * colors[gl_InstanceID % 3];
}