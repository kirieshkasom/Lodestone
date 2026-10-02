#version 400 core

layout (triangles) in;
layout(triangle_strip, max_vertices = 3) out;

in DATA {
    vec3 localVertexPos;
    vec4 vertexColor;
    float vertexDistance;
    vec2 texCoord0;
    vec2 texCoord2;
} data_in[];

out vec4 vertexColor;
out float vertexDistance;
out vec2 texCoord;
flat out vec2 size;

void main() {
    size = vec2(
        length(data_in[0].localVertexPos - data_in[1].localVertexPos),
        length(data_in[1].localVertexPos - data_in[2].localVertexPos)
    );

    size = round(size);

    for (int i = 0; i < gl_in.length(); i++) {
        gl_Position = gl_in[i].gl_Position;
        vertexColor = data_in[i].vertexColor;
        vertexDistance = data_in[i].vertexDistance;
        texCoord = data_in[i].texCoord0;
        EmitVertex();
    }
    EndPrimitive();
}