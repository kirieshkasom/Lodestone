#version 150

#moj_import <lodestone:common_math.glsl>

uniform sampler2D Sampler0;
uniform sampler2D SceneDepthBuffer;
uniform sampler2D SceneDiffuseBuffer;

uniform float LumiTransparency;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec2 ScreenSize;
uniform mat4 InvProjMat;
uniform float DepthFade;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 viewSpacePos;

out vec4 fragColor;

vec3 blend(vec3 color1, vec3 color2, float alpha) {
    return color1 * alpha + color2 * (1.0 - alpha);
}

void main() {
    vec4 color = transformColor(texture(Sampler0, texCoord0), LumiTransparency, vertexColor, ColorModulator);
    vec4 fog = applyFog(color, FogStart, FogEnd, FogColor, vertexDistance);
    if (fog.a == 0.0) {
        discard;
    }

    vec2 screenUV = gl_FragCoord.xy/ScreenSize;
    vec4 sceneColor = texture(SceneDiffuseBuffer, screenUV);

    float sceneDepthClip = texture(SceneDepthBuffer, screenUV).r;
    vec3 sceneViewSpace = viewSpaceFromDepth(sceneDepthClip, screenUV, InvProjMat);

    float depthFade = applyDepthFade(sceneViewSpace.z, viewSpacePos.z, DepthFade);

    fragColor = vec4(blend(fog.rgb, sceneColor.rgb, depthFade), color.a);
}