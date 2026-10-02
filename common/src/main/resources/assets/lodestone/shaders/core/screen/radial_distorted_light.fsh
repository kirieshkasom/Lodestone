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

uniform int Angle;
uniform float LightAngleRange;
uniform float LightIntensity;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

float getLightDirection(vec2 uv, float angle) {
    vec2 p = uv * 2.0 - 1.0;
    p = normalize(p);
    float angleRad = radians(angle - 90.0);
    float directionalLight = dot(p, vec2(cos(angleRad), sin(angleRad)));
    return acos(directionalLight);
}

float remap(float value, float fromMin, float fromMax, float toMin, float toMax) {
    return toMin + (value - fromMin) * (toMax - toMin) / (fromMax - fromMin);
}

void main() {
    float time = GameTime * Speed + TimeOffset;
    vec2 uv = texCoord0;
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

    float angle = degrees(getLightDirection(uv, float(Angle)));
    float range = LightAngleRange * 0.5;

    float v = remap(angle, 0.0, range, LightIntensity, 0.0);

    vec4 textureColor = texture(Sampler0, uv);
    if (textureColor.a == 0) {
        discard;
    }
    vec4 color = transformColor(textureColor, LumiTransparency, vertexColor, ColorModulator);
    color *= vec4(vec3(v), 1.0);
    color.a *= step(angle, range);
    fragColor = color;
}
