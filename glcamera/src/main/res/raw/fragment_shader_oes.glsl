//导入oes拓展纹理
#extension GL_OES_EGL_image_external:require
//精度
precision mediump float;
//传入的纹理坐标
varying vec2 vTextureCoord;
//使用OES 2D纹理
uniform samplerExternalOES oesTexture;

void main(){
    gl_FragColor = texture2D(oesTexture,vTextureCoord);
}