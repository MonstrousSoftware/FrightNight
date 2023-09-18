// lightning
#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

//uniform vec2 u_resolution;
//uniform sampler2D u_texture;

//varying vec2 v_texCoord0;
varying vec4 v_color;

void main()
{

   vec4 color = vec4(1.0, 0.0, 0.0, 1.0);

    gl_FragColor = color;
}

