float fetch(samplerBuffer DataBuffer, int index) {
    return texelFetch(DataBuffer, index).r;
}

vec2 fetch2(samplerBuffer DataBuffer, int startIndex) {
    return vec2(fetch(DataBuffer, startIndex), fetch(DataBuffer, startIndex + 1));
}

vec3 fetch3(samplerBuffer DataBuffer, int startIndex) {
    return vec3(fetch(DataBuffer, startIndex), fetch(DataBuffer, startIndex + 1), fetch(DataBuffer, startIndex + 2));
}

vec4 fetch4(samplerBuffer DataBuffer, int startIndex) {
    return vec4(fetch(DataBuffer, startIndex), fetch(DataBuffer, startIndex + 1), fetch(DataBuffer, startIndex + 2), fetch(DataBuffer, startIndex + 3));
}

bool fetchBool(samplerBuffer DataBuffer, int index) {
    return fetch(DataBuffer, index) > 0.5;
}