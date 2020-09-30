package mttdat.utils;

import android.webkit.WebView;

public class WebViewUtils {
    public static void callJavaScript(WebView webView, String methodName, Object...params){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("javascript:try{");
        stringBuilder.append(methodName);
        stringBuilder.append("(");
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];

            if(param instanceof String){
                stringBuilder.append("'");
                stringBuilder.append(param.toString().replace("'", "\\'"));
                stringBuilder.append("'");
            }else {
                stringBuilder.append(param);
            }

            // If the last param, don't add ','.
            if(i < params.length - 1){
                stringBuilder.append(",");
            }
        }
        stringBuilder.append(")}catch(error){Android.onError(error.message);}");
        webView.loadUrl(stringBuilder.toString());
    }
}
