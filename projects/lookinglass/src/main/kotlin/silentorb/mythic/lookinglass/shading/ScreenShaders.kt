package silentorb.mythic.lookinglass.shading

import silentorb.mythic.glowing.ShaderProgram
import silentorb.mythic.glowing.Vector4Property
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector4
import org.lwjgl.opengl.GL20
import silentorb.mythic.glowing.Vector2Property

fun routeTexture(program: ShaderProgram, name: String, unit: Int) {
  val location = GL20.glGetUniformLocation(program.id, name)
  program.activate()
  GL20.glUniform1i(location, unit)
}

val screenVertex = """
in vec4 vertex;
out vec2 texCoords;

uniform vec2 scale;

void main()
{
  gl_Position = vec4(vertex.x * scale.x * 2.0 - 1.0, vertex.y * scale.y * 2.0 - 1.0, 0.0, 1.0);
  texCoords = vertex.zw;
}
"""

fun blurPrecalculations(range: Int): String {
  var result = ""
  var divisor = 0f
  for (smallY in -range..range) {
    for (smallX in -range..range) {
      if (smallX == 0 && smallY == 0)
        continue

      val x = smallX// * 2 + dice.getInt(0, 1) * if (smallX > 0) 1 else -1
      val y = smallY// * 2 + dice.getInt(0, 1) * if (smallY > 0) 1 else -1

      val distance = Vector2(x.toFloat(), y.toFloat()).length()
      val strength = 1f / (1f + distance / 2)
      divisor += strength
      result += """
      {
      vec3 localColorSample = textureOffset(colorTexture, texCoords, ivec2(${x}, ${y})).xyz;
//      float brightness = 0.2126 * localColorSample.x + 0.7152 * localColorSample.y + 0.0722 * localColorSample.z;
//      float strength = min(depthStrength * ${strength} + brightness / 2.0, 1.0);
      accumulator += localColorSample * ${strength};
//      divisor += ${strength};
      }
      """.trimIndent()
    }
  }
  return result + "float divisor = ${divisor};"
}

private const val blurRange = 3

//private const val minBlurDepth = 0.999f
private const val minBlurDepth = 0.9985f
//private const val maxBlurDepth = 1f
private const val blurDepthStretch = 1f / (1f - minBlurDepth)

val depthOfFieldFragment = """
in vec2 texCoords;
out vec4 output_color;

uniform sampler2D colorTexture;
uniform sampler2D depthTexture;

void main()
{
  vec3 primaryColorSample = texture(colorTexture, texCoords).xyz;
  float primaryDepthSample = texture(depthTexture, texCoords).x;

  // Filter out any depth values below minBlurDepth
  float filteredDepth = max(primaryDepthSample - $minBlurDepth, 0.0);
  float depthStrength = filteredDepth * $blurDepthStretch;

  vec3 accumulator = primaryColorSample;

${blurPrecalculations(blurRange)}

  vec3 average = accumulator / divisor;
 vec3 result = average * depthStrength + primaryColorSample * (1 - depthStrength);

  output_color = vec4(result, 1.0);
}
"""

val screenColorFragment = """
in vec2 texCoords;
out vec4 output_color;
uniform sampler2D colorTexture;
uniform vec4 inputColor;

void main()
{
  vec3 primaryColorSample = texture(colorTexture, texCoords).xyz;
  vec3 rgb = primaryColorSample * (1.0 - inputColor.w) + inputColor.xyz * inputColor.w;
  output_color = vec4(rgb, 1.0);
}
"""

val screenTextureFragment = """
in vec2 texCoords;
out vec4 output_color;

uniform sampler2D colorTexture;
uniform sampler2D depthTexture;

void main()
{
  vec3 primaryColorSample = texture(colorTexture, texCoords).xyz;

//  float near = 0.01;
//  float far = 1000.0;
//  float inverseNear = 1 / near;
//  float bottom = (1 / far - inverseNear);

  float depth = texture(depthTexture, texCoords).x;
  float alpha = depth > 0.9 ? 0.0 : 1.0;
  output_color = vec4(primaryColorSample, 1.0);
  gl_FragDepth = depth;
}
"""

val screenDesaturation = """
in vec2 texCoords;
out vec4 output_color;
uniform sampler2D colorTexture;

void main()
{
  vec3 s = texture(colorTexture, texCoords).xyz;
  float level = (s.x + s.y + s.z) / 3.0;
  vec3 rgb = vec3(level);
  output_color = vec4(rgb, 1.0);
}
"""

val deferredShadingFragment = """
in vec2 texCoords;
out vec4 output_color;
uniform sampler2D colorTexture;
uniform vec4 inputColor;

void main()
{
  vec3 primaryColorSample = texture(colorTexture, texCoords).xyz;
  vec3 rgb = primaryColorSample * (1.0 - inputColor.w) + inputColor.xyz * inputColor.w;
  output_color = vec4(rgb, 1.0);
}
"""

class SimpleScreenShader(val program: ShaderProgram) {

  init {
    routeTexture(program, "colorTexture", 0)
  }

  fun activate() {
    program.activate()
  }
}

class DepthScreenShader(val program: ShaderProgram) {
  private val scaleProperty = Vector2Property(program, "scale")

  init {
    routeTexture(program, "colorTexture", 0)
    routeTexture(program, "depthTexture", 1)
  }

  fun activate(scale: Vector2) {
    scaleProperty.setValue(scale)
    program.activate()
  }
}

class ScreenColorShader(val program: ShaderProgram) {
  private val colorProperty = Vector4Property(program, "inputColor")
  private val scaleProperty = Vector2Property(program, "scale")

  init {
    routeTexture(program, "colorTexture", 0)
    routeTexture(program, "depthTexture", 1)
  }

  fun activate(scale: Vector2, color: Vector4) {
    scaleProperty.setValue(scale)
    colorProperty.setValue(color)
    program.activate()
  }
}
