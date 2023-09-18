// combine filter


uniform sampler2D u_texture;
uniform sampler2D u_highlightTexture;

varying vec2 v_texCoord0;


void main()
{
	vec4 color = texture2D(u_texture, v_texCoord0);
    vec4 color2 = texture2D(u_highlightTexture, v_texCoord0);

    gl_FragColor = color + color2 *2.0;     // change the constant for effect
}
