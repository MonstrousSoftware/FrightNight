// attributes of this vertex
attribute vec3 a_position;
attribute vec2 a_texCoord0;

uniform vec2 u_resolution;
uniform mat4 u_projTrans;


varying vec2 v_blurTexCoords[11];

void main() {

	gl_Position = u_projTrans * vec4(a_position, 1.0);
	float pixelSize = 1.0 / u_resolution.y;
	for(int i = -5; i <= 5; i++) {
		v_blurTexCoords[i+5] = 	a_texCoord0 + vec2(0.0, pixelSize*i);
	}

}
