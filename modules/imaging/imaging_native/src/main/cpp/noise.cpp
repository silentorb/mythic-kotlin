#include "noise.h"

#define STRETCH_CONSTANT_2D (-0.211324865405187)    /* (1 / sqrt(2 + 1) - 1 ) / 2; */
#define SQUISH_CONSTANT_2D  (0.366025403784439)     /* (sqrt(2 + 1) -1) / 2; */
#define STRETCH_CONSTANT_3D (-1.0 / 6.0)            /* (1 / sqrt(3 + 1) - 1) / 3; */
#define SQUISH_CONSTANT_3D  (1.0 / 3.0)             /* (sqrt(3+1)-1)/3; */
#define STRETCH_CONSTANT_4D (-0.138196601125011)    /* (1 / sqrt(4 + 1) - 1) / 4; */
#define SQUISH_CONSTANT_4D  (0.309016994374947)     /* (sqrt(4 + 1) - 1) / 4; */

#define NORM_CONSTANT_2D (47.0)
#define NORM_CONSTANT_3D (103.0)
#define NORM_CONSTANT_4D (30.0)

#define ARRAYSIZE(x) (sizeof((x)) / sizeof((x)[0]))

/*
 * Gradients for 2D. They approximate the directions to the
 * vertices of an octagon from the center.
 */
static const int8_t gradients2D[] = {
  5, 2, 2, 5,
  -5, 2, -2, 5,
  5, -2, 2, -5,
  -5, -2, -2, -5,
};

/*
 * Gradients for 3D. They approximate the directions to the
 * vertices of a rhombicuboctahedron from the center, skewed so
 * that the triangular and square facets can be inscribed inside
 * circles of the same radius.
 */
static const signed char gradients3D[] = {
  -11, 4, 4, -4, 11, 4, -4, 4, 11,
  11, 4, 4, 4, 11, 4, 4, 4, 11,
  -11, -4, 4, -4, -11, 4, -4, -4, 11,
  11, -4, 4, 4, -11, 4, 4, -4, 11,
  -11, 4, -4, -4, 11, -4, -4, 4, -11,
  11, 4, -4, 4, 11, -4, 4, 4, -11,
  -11, -4, -4, -4, -11, -4, -4, -4, -11,
  11, -4, -4, 4, -11, -4, 4, -4, -11,
};

Unit extrapolate2(struct NoiseContext *context, int xsb, int ysb, Unit dx, Unit dy) {
  auto &perm = context->perm;
  int index = perm[(perm[xsb & 0xFF] + ysb) & 0xFF] & 0x0E;
  return gradients2D[index] * dx
         + gradients2D[index + 1] * dy;
}

int fastFloor(Unit x) {
  int xi = (int) x;
  return x < xi ? xi - 1 : xi;
}

//static int allocatePerm(NoiseContext *ctx, int nperm, int ngrad)
//{
////  if (ctx->perm)
////    free(ctx->perm);
////  if (ctx->permGradIndex3D)
////    free(ctx->permGradIndex3D);
//  ctx->perm = (int16_t *) malloc(sizeof(*ctx->perm) * nperm);
////  if (!ctx->perm)
////    return -ENOMEM;
////  ctx->permGradIndex3D = (int16_t *) malloc(sizeof(*ctx->permGradIndex3D) * ngrad);
////  if (!ctx->permGradIndex3D) {
////    free(ctx->perm);
////    return -ENOMEM;
////  }
//  return 0;
//}

int open_simplex_noise_init_perm(struct NoiseContext *context, int16_t p[], int nelements) {
//  context->perm.resize(256);
//  memcpy(context->perm, p, sizeof(context->perm.data()) * nelements);

//  for (i = 0; i < 256; i++) {
//    /* Since 3D has 24 gradients, simple bitmask won't work, so precompute modulo array. */
//    ctx->permGradIndex3D[i] = (int16_t)((ctx->perm[i] % (ARRAYSIZE(gradients3D) / 3)) * 3);
//  }
  return 0;
}

/*
 * Initializes using a permutation array generated from a 64-bit seed.
 * Generates a proper permutation (i.e. doesn't merely perform N successive pair
 * swaps on a base array).  Uses a simple 64-bit LCG.
 */
NoiseContext *newOpenSimplexNoise(int64_t seed) {
  int rc;
  int16_t source[256];
  int i;
//  int16_t *permGradIndex3D;
  int r;

  try {
    auto context = new NoiseContext();
//  (*ctx)->permGradIndex3D = nullptr;

    context->perm.resize(256);
    auto &perm = context->perm;
//  permGradIndex3D = (*ctx)->permGradIndex3D;

    uint64_t seedU = seed;
    for (i = 0; i < 256; i++)
      source[i] = (int16_t) i;
    seedU = seedU * 6364136223846793005ULL + 1442695040888963407ULL;
    seedU = seedU * 6364136223846793005ULL + 1442695040888963407ULL;
    seedU = seedU * 6364136223846793005ULL + 1442695040888963407ULL;
    for (i = 255; i >= 0; i--) {
      seedU = seedU * 6364136223846793005ULL + 1442695040888963407ULL;
      r = (int) ((seedU + 31) % (i + 1));
      if (r < 0)
        r += (i + 1);
      perm[i] = source[r];
//    permGradIndex3D[i] = (short)((perm[i] % (ARRAYSIZE(gradients3D) / 3)) * 3);
      source[r] = source[i];
    }
    return context;
  }
  catch (...) {
    return nullptr;
  }
}

void open_simplex_noise_delete(NoiseContext *context) {
  delete context;
//  if (!ctx)
//    return;
//  if (ctx->perm) {
//    free(ctx->perm);
//    ctx->perm = nullptr;
//  }
//  if (ctx->permGradIndex3D) {
//    free(ctx->permGradIndex3D);
//    ctx->permGradIndex3D = nullptr;
//  }
//  free(ctx);
}

Unit openSimplexNoise2d(NoiseContext *context, Unit x, Unit y) {

  /* Place input coordinates onto grid. */
  Unit stretchOffset = (x + y) * STRETCH_CONSTANT_2D;
  Unit xs = x + stretchOffset;
  Unit ys = y + stretchOffset;

  /* Floor to get grid coordinates of rhombus (stretched square) super-cell origin. */
  int xsb = fastFloor(xs);
  int ysb = fastFloor(ys);

  /* Skew out to get actual coordinates of rhombus origin. We'll need these later. */
  Unit squishOffset = (xsb + ysb) * SQUISH_CONSTANT_2D;
  Unit xb = xsb + squishOffset;
  Unit yb = ysb + squishOffset;

  /* Compute grid coordinates relative to rhombus origin. */
  Unit xins = xs - xsb;
  Unit yins = ys - ysb;

  /* Sum those together to get a value that determines which region we're in. */
  Unit inSum = xins + yins;

  /* Positions relative to origin point. */
  Unit dx0 = x - xb;
  Unit dy0 = y - yb;

  /* We'll be defining these inside the next block and using them afterwards. */
  Unit dx_ext, dy_ext;
  int xsv_ext, ysv_ext;

  Unit dx1;
  Unit dy1;
  Unit attn1;
  Unit dx2;
  Unit dy2;
  Unit attn2;
  Unit zins;
  Unit attn0;
  Unit attn_ext;

  Unit value = 0;

  /* Contribution (1,0) */
  dx1 = dx0 - 1 - SQUISH_CONSTANT_2D;
  dy1 = dy0 - 0 - SQUISH_CONSTANT_2D;
  attn1 = 2 - dx1 * dx1 - dy1 * dy1;
  if (attn1 > 0) {
    attn1 *= attn1;
    value += attn1 * attn1 * extrapolate2(context, xsb + 1, ysb + 0, dx1, dy1);
  }

  /* Contribution (0,1) */
  dx2 = dx0 - 0 - SQUISH_CONSTANT_2D;
  dy2 = dy0 - 1 - SQUISH_CONSTANT_2D;
  attn2 = 2 - dx2 * dx2 - dy2 * dy2;
  if (attn2 > 0) {
    attn2 *= attn2;
    value += attn2 * attn2 * extrapolate2(context, xsb + 0, ysb + 1, dx2, dy2);
  }

  if (inSum <= 1) { /* We're inside the triangle (2-Simplex) at (0,0) */
    zins = 1 - inSum;
    if (zins > xins || zins > yins) { /* (0,0) is one of the closest two triangular vertices */
      if (xins > yins) {
        xsv_ext = xsb + 1;
        ysv_ext = ysb - 1;
        dx_ext = dx0 - 1;
        dy_ext = dy0 + 1;
      }
      else {
        xsv_ext = xsb - 1;
        ysv_ext = ysb + 1;
        dx_ext = dx0 + 1;
        dy_ext = dy0 - 1;
      }
    }
    else { /* (1,0) and (0,1) are the closest two vertices. */
      xsv_ext = xsb + 1;
      ysv_ext = ysb + 1;
      dx_ext = dx0 - 1 - 2 * SQUISH_CONSTANT_2D;
      dy_ext = dy0 - 1 - 2 * SQUISH_CONSTANT_2D;
    }
  }
  else { /* We're inside the triangle (2-Simplex) at (1,1) */
    zins = 2 - inSum;
    if (zins < xins || zins < yins) { /* (0,0) is one of the closest two triangular vertices */
      if (xins > yins) {
        xsv_ext = xsb + 2;
        ysv_ext = ysb + 0;
        dx_ext = dx0 - 2 - 2 * SQUISH_CONSTANT_2D;
        dy_ext = dy0 + 0 - 2 * SQUISH_CONSTANT_2D;
      }
      else {
        xsv_ext = xsb + 0;
        ysv_ext = ysb + 2;
        dx_ext = dx0 + 0 - 2 * SQUISH_CONSTANT_2D;
        dy_ext = dy0 - 2 - 2 * SQUISH_CONSTANT_2D;
      }
    }
    else { /* (1,0) and (0,1) are the closest two vertices. */
      dx_ext = dx0;
      dy_ext = dy0;
      xsv_ext = xsb;
      ysv_ext = ysb;
    }
    xsb += 1;
    ysb += 1;
    dx0 = dx0 - 1 - 2 * SQUISH_CONSTANT_2D;
    dy0 = dy0 - 1 - 2 * SQUISH_CONSTANT_2D;
  }

  /* Contribution (0,0) or (1,1) */
  attn0 = 2 - dx0 * dx0 - dy0 * dy0;
  if (attn0 > 0) {
    attn0 *= attn0;
    value += attn0 * attn0 * extrapolate2(context, xsb, ysb, dx0, dy0);
  }

  /* Extra Vertex */
  attn_ext = 2 - dx_ext * dx_ext - dy_ext * dy_ext;
  if (attn_ext > 0) {
    attn_ext *= attn_ext;
    value += attn_ext * attn_ext * extrapolate2(context, xsv_ext, ysv_ext, dx_ext, dy_ext);
  }

  return value / NORM_CONSTANT_2D;
}

void fillNoiseBuffer2d(NoiseContext *context, float *buffer, int dimensionsX, int dimensionsY, int octaves) {
//  std::vector<float> buffer2;
//  buffer2.resize(dimensionsX * dimensionsY);
  for (int octave = 0; octave < octaves; ++octave) {
//    auto bufferOffset = buffer2.data();
    auto bufferOffset = buffer;
    for (int y = 0; y < dimensionsY; ++y) {
      for (int x = 0; x < dimensionsX; ++x) {
        auto sampleX = static_cast<double>(x) / dimensionsX;
        auto sampleY = 1 - static_cast<double>(y) / dimensionsY;
        *bufferOffset++ = static_cast<float>(openSimplexNoise2d(context, sampleX, sampleY));
        return;
      }
    }
  }
}
