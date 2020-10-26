//#weightHeader

uniform mat4 modelTransform;
uniform mat4 normalTransform;
uniform vec4 uniformColor;

out vec4 fragmentColor;
out vec4 fragmentPosition;
out vec3 fragmentNormal;
out vec2 textureCoordinates;

void main() {
  fragmentColor = uniformColor;
  vec4 position4 = vec4(position, 1.0);
//#weightApplication
  vec4 modelPosition = modelTransform * position4;
  fragmentPosition = modelPosition;
  fragmentNormal = normalize((normalTransform * vec4(normal, 1.0)).xyz);
  gl_Position = scene.cameraTransform * modelPosition;
  textureCoordinates = uv;
}
