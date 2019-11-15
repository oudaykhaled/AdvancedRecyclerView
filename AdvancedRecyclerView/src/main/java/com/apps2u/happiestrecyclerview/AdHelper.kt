package com.apps2u.happiestrecyclerview

import android.content.Context


object AdHelper {
    //    private static final AdSize[] bannerSize = new AdSize[]{AdSize.BANNER, AdSize.LARGE_BANNER, AdSize.MEDIUM_RECTANGLE};
    //    private static final AdSize[] nativeSize = new AdSize[]{AdSize.FLUID};


    fun handleNativeAdClick(context: Context, key: String, data: String) {
        //        if (key.equalsIgnoreCase("mv-ad-click")) {
        //            Gson gson = new Gson();
        //            JsonObject json = gson.fromJson(data, JsonObject.class);
        //            String url = json.get("url").getAsString();
        //
        //            if (json.get("url").toString().contains("mvi")) {
        //                String mviKey = url.substring(url.lastIndexOf("="));
        //                url = "https://www.lbcgroup.tv/sponsored?mvi" + mviKey;
        //            }
        //
        //            if (!TextUtils.isEmpty(url)) {
        //                Intent intent = WebViewActivity.newIntent(context, url, context.getString(R.string.app_name));
        //                context.startActivity(intent);
        //            }
        //        }
    }


}
