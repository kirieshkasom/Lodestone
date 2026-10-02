#version 150

uniform sampler3D VolumeTexture;
uniform vec3 CameraPos;
uniform vec2 ScreenSize;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

in vec3 worldPos;

out vec4 fragColor;

float sdBoxFrame( vec3 p, vec3 b, float e ){
    p = abs(p)-b;
    vec3 q = abs(p+e)-e;
    return min(min(
    length(max(vec3(p.x,q.y,q.z),0.0))+min(max(p.x,max(q.y,q.z)),0.0),
    length(max(vec3(q.x,p.y,q.z),0.0))+min(max(q.x,max(p.y,q.z)),0.0)),
    length(max(vec3(q.x,q.y,p.z),0.0))+min(max(q.x,max(q.y,p.z)),0.0));
}

vec3 rayMarchObject(vec3 rayOrigin, vec3 rayDir, float maxDistance, int maxSteps, float epsilon, out bool hit) {
    float distTraveled = 0.0;
    for (int i = 0; i < maxSteps; i++) {
        vec3 rayPos = rayOrigin + rayDir * distTraveled;
        float sdFrame = sdBoxFrame(rayPos, vec3(1.0), 0.01);
        float sdTexture = texture(VolumeTexture, (rayPos + 1.0) * 0.5).r;
        float distFromSDF = min(sdFrame, sdTexture);

        if (distFromSDF < epsilon) {
            hit = true;
            return rayPos;
        }

        distTraveled += distFromSDF;
        if (distTraveled > maxDistance) {
            break;
        }
    }
    hit = false;
    return vec3(0.0);
}

vec3 viewPosFromDepth(in float depth, in mat4 iProjMat, in vec2 uv) {
    float z = depth * 2.0 - 1.0;

    vec4 positionCS = vec4(uv * 2.0 - 1.0, z, 1.0);
    vec4 positionVS = iProjMat * positionCS;
    positionVS /= positionVS.w;

    return positionVS.xyz;
}

vec3 viewDirection(in vec2 uv, in mat4 iViewMat, in mat4 iProjMat) {
    return (iViewMat * vec4(normalize(viewPosFromDepth(1.0, iProjMat, uv)), 0.0)).xyz;
}

void main() {
    fragColor = vec4(0.0);
    vec3 rayDirWorld = viewDirection(gl_FragCoord.xy / ScreenSize, inverse(ModelViewMat), inverse(ProjMat));
    vec3 rayOriginWorld = worldPos;

    float distTraveled;
    int maxStepCount = 200;
    float epsilon = 0.01;


    bool hit;
    vec3 hitPosWorld = rayMarchObject(rayOriginWorld, rayDirWorld, 10.0, maxStepCount, epsilon, hit);
    fragColor = vec4(hitPosWorld, 1.0);


    vec4 clipSpacePos = ProjMat * ModelViewMat * vec4(hitPosWorld-CameraPos, 1.0);
    vec3 ndc = clipSpacePos.xyz / clipSpacePos.w;
    gl_FragDepth = ndc.z * 0.5 + 0.5;

    if (!hit) {
        discard;
    }
}