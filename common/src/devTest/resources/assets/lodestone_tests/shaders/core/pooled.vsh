#version 330 core
layout(location=0) in vec3 Position;
layout(location=1) in vec2 UV0;
layout(location=2) in vec3 InstancePosition;
layout(location=3) in vec3 InstanceScale;
layout(location=4) in vec4 InstanceColor;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
out vec2 texCoord;
out vec4 vertexColor;
void main() {
    vec4 center = ModelViewMat * vec4(InstancePosition, 1.0);
    center.xy += Position.xy * InstanceScale.xy;
    gl_Position = ProjMat * center;
    texCoord = UV0;
    vertexColor = InstanceColor;
}
