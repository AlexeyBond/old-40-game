#ifdef GL_ES
    precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform vec2 u_targetSizeInv;

void main() {
    vec4 color = vec4(0);

    vec2 d = u_targetSizeInv * 2.0;

    color += texture2D(u_texture, v_texCoords + d * vec2(+1, +1));
    color += texture2D(u_texture, v_texCoords + d * vec2(+1, -1));
    color += texture2D(u_texture, v_texCoords + d * vec2(-1, -1));
    color += texture2D(u_texture, v_texCoords + d * vec2(-1, +1));

    color *= 0.25 * 0.9;

    gl_FragColor = color;
}
