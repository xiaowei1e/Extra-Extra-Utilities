uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform vec2 u_campos;
uniform vec4 u_gravitySource[256];
uniform int u_gravitySource_count;

varying vec2 v_texCoords;

float getStrength(float dst, float radius, float power) {
    return (1.0 / pow(dst - radius / 2.0, 2.0)) * power;
}

void main() {
    vec2 worldCoords = v_texCoords * u_resolution + u_campos;
    vec2 displacement = vec2(0.0, 0.0);
    //vec4 colormul = vec4(1.0, 1.0, 1.0, 1.0);
    for (int i = 0; i < u_gravitySource_count; i++) {
        vec4 source = u_gravitySource[i];
        float radius = source.z;
        float dst = distance(worldCoords, source.xy);
        float power = source.w;
        float strength = getStrength(dst, radius, power);
        //float sub = (dst * getStrength(radius, radius, power)) / radius;
        vec2 relative = -normalize(worldCoords - source.xy);
        //colormul *= min(max(0.0, dst - radius / 2.0), 1.0);
        displacement += (relative * strength) / u_resolution;
    }
    vec4 c = texture2D(u_texture, v_texCoords + displacement);
    //c.rgb *= colormul.rgb;
    gl_FragColor = c;
}
