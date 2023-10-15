uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform vec2 u_campos;
uniform vec4 u_gravitySource[256];
uniform int u_gravitySource_count;

varying vec2 v_texCoords;

void main() {
    vec2 worldCoords = v_texCoords * u_resolution + u_campos;
    vec2 displacement = vec2(0.0, 0.0);
    for (int i = 0; i < u_gravitySource_count; i++) {
        vec4 source = u_gravitySource[i];
        float radius = source.z;
        float dst = (10 * (distance(worldCoords, source.xy))) / radius;
        float strength = (1 / (dst + 1)) * radius * source.w;
        vec2 relative = normalize(worldCoords - source.xy);
        displacement += (relative * strength) / u_resolution;
    }
    vec4 c = texture2D(u_texture, v_texCoords + displacement);
    gl_FragColor = c;
}