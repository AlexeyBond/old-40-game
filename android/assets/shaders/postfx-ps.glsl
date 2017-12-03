#ifdef GL_ES
    precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform vec2 u_targetSizeInv;

void main() {
    vec4 color = vec4(0);

    vec2 d = u_targetSizeInv * 1.0;

    color += texture2D(u_texture, v_texCoords + d * vec2(+1, +1));
    color += texture2D(u_texture, v_texCoords + d * vec2(+1, -1));
    color += texture2D(u_texture, v_texCoords + d * vec2(-1, -1));
    color += texture2D(u_texture, v_texCoords + d * vec2(-1, +1));
    color += texture2D(u_texture, v_texCoords + d * vec2(+0, +2)) * 0.5;
    color += texture2D(u_texture, v_texCoords + d * vec2(+0, -2)) * 0.5;
    color += texture2D(u_texture, v_texCoords + d * vec2(+2, +0)) * 0.5;
    color += texture2D(u_texture, v_texCoords + d * vec2(-2, +0)) * 0.5;

    color *= (1.0 / 6.0) * 0.9;

    gl_FragColor = color;
}
