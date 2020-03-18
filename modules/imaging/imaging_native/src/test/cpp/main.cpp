#include "noise.h"

int main (int argc, char *argv[]) {
    std::unique_ptr<NoiseContext> context { newOpenSimplexNoise() };
    std::vector<float> buffer;
    buffer.resize(512 * 512);
    fillNoiseBuffer2d(context.get(), buffer.data(), 512, 512, 7);
}
