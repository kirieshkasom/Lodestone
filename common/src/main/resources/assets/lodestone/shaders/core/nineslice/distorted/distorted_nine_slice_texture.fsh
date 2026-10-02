#version 150

#moj_import <lodestone:common_math.glsl>

uniform sampler2D Sampler0;
uniform float LumiTransparency;
uniform float Width;
uniform float Height;
uniform float GameTime;
uniform float TimeOffset;
uniform float Speed;
uniform float Intensity;
uniform float XFrequency;
uniform float YFrequency;
uniform vec4 UVCoordinates;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord;
flat in vec2 size;

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
    float time = GameTime * Speed + TimeOffset;
    vec2 uv = texCoord;
    vec2 uCap = vec2(UVCoordinates.x, UVCoordinates.y);
    vec2 vCap = vec2(UVCoordinates.z, UVCoordinates.w);

    if (Width > 0.0){
        uv.x = floor(uv.x* Width)/ Width;
    }
    if (Height > 0.0){
        uv.y = floor(uv.y* Height)/ Height;
    }

    uv.x += cos(uv.y*XFrequency+time)/Intensity;
    uv.y += sin(uv.x*YFrequency+time)/Intensity;

    uv.x = clamp(uv.x, uCap.x, uCap.y);
    uv.y = clamp(uv.y, vCap.x, vCap.y);
    uv = sliceUV(uv, size);
    vec4 textureColor = texture(Sampler0, uv);
    if (textureColor.a == 0) {
        discard;
    }
    vec4 color = transformColor(textureColor, LumiTransparency, vertexColor, ColorModulator);
    vec4 fog = applyFog(color, FogStart, FogEnd, FogColor, vertexDistance);
    if (fog.a == 0.0) {
        discard;
    }
    fragColor = fog;
}