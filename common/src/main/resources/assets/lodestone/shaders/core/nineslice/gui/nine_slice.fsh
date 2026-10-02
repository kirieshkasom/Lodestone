#version 150

in vec2 texCoord;
in vec4 vertexColor;

uniform sampler2D Sampler0;
uniform vec2 Size;

out vec4 fragColor;

vec2 sliceUV(vec2 uv, vec2 size) {
    vec2 p = uv * size;

    const float unitSize = 1.0/2.0;

    vec2 idx = vec2(1.0);

    // Left / top
    idx.x = mix(idx.x, 0.0, step(p.x, unitSize));
    idx.y = mix(idx.y, 0.0, step(p.y, unitSize));

    // Right / bottom
    idx.x = mix(idx.x, 2.0, step(size.x - unitSize, p.x));
    idx.y = mix(idx.y, 2.0, step(size.y - unitSize, p.y));

    vec2 local  = fract(p * 2.0) / 3.0;
    return local + idx / 3.0;
}

void main() {
    vec2 uv = sliceUV(texCoord, Size);
    vec4 texture = texture(Sampler0, uv);
    fragColor = texture * vertexColor;
}