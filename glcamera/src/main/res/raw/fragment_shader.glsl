precision mediump float;
//传入的纹理坐标
varying vec2 vTextureCoord;
//使用2D纹理
uniform sampler2D texture;
void main(){
    gl_FragColor = texture2D(texture,vTextureCoord);
}