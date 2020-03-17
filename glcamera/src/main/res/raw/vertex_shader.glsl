//顶点坐标
attribute vec4 vertexCoord;
//纹理坐标
attribute vec2 textureCoord;
//传入片元着色器的纹理坐标
varying vec2 vTextureCoord;

void main(){
    vTextureCoord = textureCoord;
    //gl_Position是OpenGL ES自带参数
    gl_Position = vertexCoord ;
}