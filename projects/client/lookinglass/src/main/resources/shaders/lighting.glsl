struct Light {
	int type;
	vec4 color;
	vec3 position;
	vec4 direction;
};

struct Section {
    int lightCount;
    float ambient;
    Light lights[40];
};

layout(std140) uniform SectionUniform {
    Section section;
};

const float constant_attenuation = 0.5;
const float linear_attenuation = 0.2;
const float quadratic_attenuation = 0.05;
const float shininess = 0.9;
const float strength = 0.3;

struct Relationship {
    vec3 direction;
    float distance;
};

Relationship get_relationship(Light light, vec3 position) {
    Relationship info;
	info.direction = normalize(light.position - position);
//	info.direction = normalize(vec3(-1, 0, 0));
	info.distance = length(info.direction);
	return info;
}

vec3 processLight(Light light, vec4 input_color, vec3 normal, vec3 cameraDirection, vec3 position) {
	Relationship info = get_relationship(light, position);
    float maxDistance = light.direction.w;
    float distanceValue = 1 - min(maxDistance, distance(position, light.position)) / maxDistance;
    float distanceFade = distanceValue;// * distanceValue;
//    return vec3(distanceFade);
	vec3 lightColor = light.color.xyz * light.color.w;

	float attenuation = 1.5;
	vec3 half_vector = normalize(info.direction + cameraDirection);

	float diffuse = max(0.0, dot(normal, info.direction * 1.0));
	float specular = max(0.0, dot(normal, half_vector));
//	diffuse = diffuse > 0.01 ? 2.0 : 0.5;

	if (diffuse == 0.0)
		specular = 0.0;
	 else
		specular = pow(specular, shininess) * strength;

	//specular = 0;
	vec3 scattered_light = lightColor * diffuse * attenuation;
	vec3 reflected_light = lightColor * specular * attenuation;
	reflected_light = vec3(0);

	vec3 rgb = min(input_color.rgb * scattered_light + reflected_light, vec3(1.0));
	return rgb * distanceFade;
}

vec3 processLights(vec4 input_color, vec3 normal, vec3 cameraDirection, vec3 position, float glow) {
	vec3 result = input_color.xyz * (glow + section.ambient);
	for(int i = 0; i < section.lightCount; ++i) {
		result += processLight(section.lights[i], input_color, normal, cameraDirection, position);
	}
	return min(result, vec3(1.0));
}
