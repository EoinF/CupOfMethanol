#version 130

#ifdef GL_ES
precision mediump float;
#endif
in vec4 gl_FragCoord;
varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float height;

void main()
{

  vec4 colour;
  float xFromCenter;
  ivec2 textureSize2d = textureSize(u_texture,0);

  float textureWidth = textureSize2d.x;

  if (gl_FragCoord.x > (textureWidth / 3)) {
    xFromCenter = 512 - textureWidth;
  } else {
    xFromCenter = 0; //(textureWidth - gl_FragCoord.x) / 10.0;
  }
  if (gl_FragCoord.y > (300 - xFromCenter)) {
    colour = vec4(0,0,0,0);
  } else {
    colour = vec4(height,1,1,1);
  }

  gl_FragColor = colour * v_color * texture2D(u_texture, v_texCoords);
};