package silentorb.mythic.lookinglass.shading

const val instancedParticleHeader = """
struct Instance {
    mat4 position;
    vec4 color;
    vec2 textureOffset;
};

struct InstanceSection {
    Instance instances[$maxInstanceCount];
};

layout(std140) uniform InstanceUniform {
    InstanceSection instanceSection;
};

out vec4 fragmentColor;
"""
