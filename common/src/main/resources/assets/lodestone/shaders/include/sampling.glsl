/*
    * Samples a 2D texture atlas which represents a sliced 3D volume.
*/
vec4 sampleVolumeAtlas(in sampler2D volume, in ivec2 spriteDims, in vec3 texCoord3D) {
    float numSlices   = float(spriteDims.x * spriteDims.y);
    float sliceFloat  = clamp(texCoord3D.z, 0.0, 1.0 - 1e-6) * numSlices;
    int   sliceIndex  = int(floor(sliceFloat));

    int row = sliceIndex / spriteDims.x;
    int col = sliceIndex % spriteDims.x;

    float tileSizeX = 1.0 / float(spriteDims.x);
    float tileSizeY = 1.0 / float(spriteDims.y);

    float u = (float(col) + texCoord3D.x) * tileSizeX;
    float v = (float(row) + texCoord3D.y) * tileSizeY;

    return texture(volume, vec2(u, v));
}

/*
    * Samples a 2D texture atlas which represents a sliced 3D volume.
    * The sample is interpolated between the two nearest slices.
    * Uses twice as many texture lookups as sampleVolumeAtlas.
*/
vec4 sampleVolumeAtlasInterpolated(in sampler2D volume, in ivec2 spriteDims, in vec3 texCoord3D) {
    float numSlices  = float(spriteDims.x * spriteDims.y);
    float sliceFloat = texCoord3D.z * (numSlices - 1.0);
    int   sliceIndex = int(floor(sliceFloat));
    int   sliceIndex2 = min(sliceIndex + 1, spriteDims.x * spriteDims.y - 1);

    int row  =  sliceIndex / spriteDims.x;
    int col  =  sliceIndex % spriteDims.x;
    int row2 = sliceIndex2 / spriteDims.x;
    int col2 = sliceIndex2 % spriteDims.x;

    float tileSizeX = 1.0 / float(spriteDims.x);
    float tileSizeY = 1.0 / float(spriteDims.y);

    float u  = (float(col)  + texCoord3D.x) * tileSizeX;
    float v  = (float(row)  + texCoord3D.y) * tileSizeY;
    float u2 = (float(col2) + texCoord3D.x) * tileSizeX;
    float v2 = (float(row2) + texCoord3D.y) * tileSizeY;

    vec4 sample1 = texture(volume, vec2(u,  v));
    vec4 sample2 = texture(volume, vec2(u2, v2));
    float t = fract(sliceFloat);

    return mix(sample1, sample2, t);
}