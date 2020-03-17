#pragma once

#include <memory>
#include <vector>

int test();

struct NoiseContext {
    std::vector<int16_t> perm;
//    int16_t *permGradIndex3D;
};

using Unit = float;

NoiseContext *newOpenSimplexNoise(int64_t seed);
Unit openSimplexNoise2d(NoiseContext *context, Unit x, Unit y);
void open_simplex_noise_delete(NoiseContext *context);
void fillNoiseBuffer2d(NoiseContext *context, float *buffer, int dimensionsX, int dimensionsY, int octaves);
