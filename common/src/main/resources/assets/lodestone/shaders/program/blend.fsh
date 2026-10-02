#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D BlendSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

vec3 aces(vec3 x) {
    const float a = 2.51;
    const float b = 0.03;
    const float c = 2.43;
    const float d = 0.59;
    const float e = 0.14;
    return clamp((x * (a * x + b)) / (x * (c * x + d) + e), 0.0, 1.0);
}

void main() {
    vec4 diffuse = texture(DiffuseSampler, texCoord);
    vec4 blend = texture(BlendSampler, texCoord);
    fragColor = vec4(diffuse.rgb + blend.rgb, diffuse.a);
}
