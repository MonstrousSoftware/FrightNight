// worley


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

uniform vec2 u_resolution;
uniform float u_time;


varying vec2 v_texCoord0;


//Calculate the squared length of a vector
float length2(vec2 p){
    return dot(p,p);
}

//Generate some noise to scatter points.
float noise(vec2 p){
    return fract(sin(fract(sin(p.x) * (43.13311)) + p.y) * 31.0011);
}

float worley(vec2 p) {
    //Set our distance to infinity
    float d = 1e30;
    //For the 9 surrounding grid points
    for (int xo = -1; xo <= 1; ++xo) {
        for (int yo = -1; yo <= 1; ++yo) {
            //Floor our vec2 and add an offset to create our point
            vec2 tp = floor(p) + vec2(xo, yo);
            //Calculate the minimum distance for this grid point
            //Mix in the noise value too!
            d = min(d, length2(p - tp - noise(tp)));
        }
    }
    return d; //3.0*exp(-4.0*abs(2.5*d - 1.0));
}


void main()
{
    vec2 uv = v_texCoord0;

    vec2 p = vec2(uv * u_resolution.xy/100.0);
    //p = p * 1.0 + 0.5*iTime;
    p.y += 0.1*u_time;
    float t = worley(p );
    //Add some gradient
    t*=exp(-length2(abs(0.8*(uv-vec2(0.5, 0.5)))));
    //colour it
    //fragColor = vec4(t, t, t, 1.0);
    float bright = 0.7+0.3*abs(sin(2.4*u_time));
    t *= bright;
    gl_FragColor = vec4(t * vec3(pow(t, 0.5-t), 0.3, 0.4*t ), 1.0);

}
