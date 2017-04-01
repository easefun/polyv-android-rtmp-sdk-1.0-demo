#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
//varying highp vec2 pos[9];
//varying highp vec2 pos1[10];
uniform samplerExternalOES sTexture;


highp vec4 params = vec4(0.6, 0.95, 0.03, 0.02);
//highp vec4 params = vec4(0.7, 0.7, 0.03, 0.02);
//highp vec4 params = vec4(0.5, 0.9, 0.01, 0.01);
//highp vec4 params = vec4(0.9, 0.9, 0.05, 0.03);
const highp vec3 W = vec3(0.299,0.587,0.114);
const mat3 saturateMatrix = mat3(1.1102,-0.0598,-0.061,
                                 -0.0774,1.0826,-0.1186,
                                 -0.0228,-0.0228,1.1772);

float hardlight(float color) {
  if(color <= 0.5) {
     color = color * color * 2.0;
  } else {
     color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);
   }
  return color;
}


void main() {

    vec4 tc = texture2D(sTexture, textureCoordinate);
    float color = tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11;
    //gl_FragColor = vec4(color, color, color, 1.0);

//    vec4 tc = texture2D(sTexture, textureCoordinate);
//    vec4 tc1 = texture2D(sTexture, textureCoordinate);
//    vec4 sum = vec4(0,0,0,0);
//    mediump float r;
//    mediump float g;
//    mediump float b;
//    if((tc.r > 0.372549 && tc.g > 0.156863 && tc.b > 0.078431 &&tc.r - tc.g > 0.058823 && tc.r - tc.b > 0.058823) ||
//       (tc.r > 0.784314 && tc.g > 0.823530 && tc.b > 0.666667 &&abs(tc.r - tc.b) <= 0.058823 && tc.r > tc.b && tc.g > tc.b)) {
//               for(int i = 0; i < 9; ++i) {
//                 tc = texture2D(sTexture, pos[i]) * 0.112;
//                 sum += tc;
//               }
//
//             r = min(sum.r, 1.0);
//             g = min(sum.g, 1.0);
//             b = min(sum.b, 1.0);
//             if(abs(r - tc1.r) > 0.05) {
//                         r = tc1.r;
//                         g = tc1.g;
//                         b = tc1.b;
////                         r = 1.0;
////                         g = 1.0;
////                         b = 1.0;
//             }
//     }  else{
//            r = tc1.r;
//            g = tc1.g;
//            b = tc1.b;
//      }
//
//
//
////     for(int i = 0; i < 10; ++i) {
////       tc = texture2D(sTexture, pos1[i]) * 0.04;
////       sum += tc;
////     }
//
//
//    float rr = min(r+r-r*r, 1.0);
//    float gg = min(g+g-g*g, 1.0);
//    float bb = min(b+b-b*b, 1.0);
//    r = min(0.5*r+0.5*rr, 1.0);
//    g = min(0.5*g+0.5*gg, 1.0);
//    b = min(0.5*b+0.5*bb, 1.0);
////    r = min(r+tc1.r - r*tc1.r, 1.0);
////    g = min(g+tc1.g - g*tc1.g, 1.0);
////    b = min(b+tc1.b - b*tc1.b, 1.0);
//
//
//    gl_FragColor = vec4(r, g, b, 1.0);

//gl_FragColor = vec4(1, 1, 1, 1.0);



//gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);

                   vec2 blurCoordinates[24];
                   vec2 singleStepOffset = vec2(0.00078125, 0.00138889);


                   blurCoordinates[0] = textureCoordinate.xy + singleStepOffset * vec2(0.0, -10.0);
                   blurCoordinates[1] = textureCoordinate.xy + singleStepOffset * vec2(0.0, 10.0);
                   blurCoordinates[2] = textureCoordinate.xy + singleStepOffset * vec2(-10.0, 0.0);
                   blurCoordinates[3] = textureCoordinate.xy + singleStepOffset * vec2(10.0, 0.0);

                   blurCoordinates[4] = textureCoordinate.xy + singleStepOffset * vec2(5.0, -8.0);
                   blurCoordinates[5] = textureCoordinate.xy + singleStepOffset * vec2(5.0, 8.0);
                   blurCoordinates[6] = textureCoordinate.xy + singleStepOffset * vec2(-5.0, 8.0);
                   blurCoordinates[7] = textureCoordinate.xy + singleStepOffset * vec2(-5.0, -8.0);

                   blurCoordinates[8] = textureCoordinate.xy + singleStepOffset * vec2(8.0, -5.0);
                   blurCoordinates[9] = textureCoordinate.xy + singleStepOffset * vec2(8.0, 5.0);
                   blurCoordinates[10] = textureCoordinate.xy + singleStepOffset * vec2(-8.0, 5.0);
                   blurCoordinates[11] = textureCoordinate.xy + singleStepOffset * vec2(-8.0, -5.0);

                   blurCoordinates[12] = textureCoordinate.xy + singleStepOffset * vec2(0.0, -6.0);
                   blurCoordinates[13] = textureCoordinate.xy + singleStepOffset * vec2(0.0, 6.0);
                   blurCoordinates[14] = textureCoordinate.xy + singleStepOffset * vec2(6.0, 0.0);
                   blurCoordinates[15] = textureCoordinate.xy + singleStepOffset * vec2(-6.0, 0.0);

                   blurCoordinates[16] = textureCoordinate.xy + singleStepOffset * vec2(-4.0, -4.0);
                   blurCoordinates[17] = textureCoordinate.xy + singleStepOffset * vec2(-4.0, 4.0);
                   blurCoordinates[18] = textureCoordinate.xy + singleStepOffset * vec2(4.0, -4.0);
                   blurCoordinates[19] = textureCoordinate.xy + singleStepOffset * vec2(4.0, 4.0);

                   blurCoordinates[20] = textureCoordinate.xy + singleStepOffset * vec2(-2.0, -2.0);
                   blurCoordinates[21] = textureCoordinate.xy + singleStepOffset * vec2(-2.0, 2.0);
                   blurCoordinates[22] = textureCoordinate.xy + singleStepOffset * vec2(2.0, -2.0);
                   blurCoordinates[23] = textureCoordinate.xy + singleStepOffset * vec2(2.0, 2.0);


                   float sampleColor = texture2D(sTexture, textureCoordinate).g * 22.0;
                   sampleColor += texture2D(sTexture, blurCoordinates[0]).g;
                   sampleColor += texture2D(sTexture, blurCoordinates[1]).g;
                   sampleColor += texture2D(sTexture, blurCoordinates[2]).g;
                   sampleColor += texture2D(sTexture, blurCoordinates[3]).g;
                   sampleColor += texture2D(sTexture, blurCoordinates[4]).g;
                   sampleColor += texture2D(sTexture, blurCoordinates[5]).g;
                   sampleColor += texture2D(sTexture, blurCoordinates[6]).g;
                   sampleColor += texture2D(sTexture, blurCoordinates[7]).g;
                   sampleColor += texture2D(sTexture, blurCoordinates[8]).g;
                   sampleColor += texture2D(sTexture, blurCoordinates[9]).g;
                   sampleColor += texture2D(sTexture, blurCoordinates[10]).g;
                   sampleColor += texture2D(sTexture, blurCoordinates[11]).g;

                   sampleColor += texture2D(sTexture, blurCoordinates[12]).g * 2.0;
                   sampleColor += texture2D(sTexture, blurCoordinates[13]).g * 2.0;
                   sampleColor += texture2D(sTexture, blurCoordinates[14]).g * 2.0;
                   sampleColor += texture2D(sTexture, blurCoordinates[15]).g * 2.0;
                   sampleColor += texture2D(sTexture, blurCoordinates[16]).g * 2.0;
                   sampleColor += texture2D(sTexture, blurCoordinates[17]).g * 2.0;
                   sampleColor += texture2D(sTexture, blurCoordinates[18]).g * 2.0;
                   sampleColor += texture2D(sTexture, blurCoordinates[19]).g * 2.0;

                   sampleColor += texture2D(sTexture, blurCoordinates[20]).g * 3.0;
                   sampleColor += texture2D(sTexture, blurCoordinates[21]).g * 3.0;
                   sampleColor += texture2D(sTexture, blurCoordinates[22]).g * 3.0;
                   sampleColor += texture2D(sTexture, blurCoordinates[23]).g * 3.0;

                   sampleColor = sampleColor / 62.0;




                   vec3 centralColor = texture2D(sTexture, textureCoordinate).rgb;

                   float highpass = centralColor.g - sampleColor + 0.5;

                   for(int i = 0; i < 5;i++)
                   {
                       highpass = hardlight(highpass);
                   }
                   float lumance = dot(centralColor, W);

                   float alpha = pow(lumance, params.r);

                   vec3 smoothColor = centralColor + (centralColor-vec3(highpass))*alpha*0.1;

                   smoothColor.r = clamp(pow(smoothColor.r, params.g),0.0,1.0);
                   smoothColor.g = clamp(pow(smoothColor.g, params.g),0.0,1.0);
                   smoothColor.b = clamp(pow(smoothColor.b, params.g),0.0,1.0);

                   vec3 lvse = vec3(1.0)-(vec3(1.0)-smoothColor)*(vec3(1.0)-centralColor);
                   vec3 bianliang = max(smoothColor, centralColor);
                   vec3 rouguang = 2.0*centralColor*smoothColor + centralColor*centralColor - 2.0*centralColor*centralColor*smoothColor;

                   //gl_FragColor = vec4(mix(centralColor, centralColor, alpha), 1.0);
                   gl_FragColor = vec4(mix(centralColor, lvse, alpha), 1.0);
                   gl_FragColor.rgb = mix(gl_FragColor.rgb, bianliang, alpha);
                   gl_FragColor.rgb = mix(gl_FragColor.rgb, rouguang, params.b);

                   //vec3 satcolor = gl_FragColor.rgb * saturateMatrix;
                   //gl_FragColor.rgb = mix(gl_FragColor.rgb, satcolor, params.a);














}