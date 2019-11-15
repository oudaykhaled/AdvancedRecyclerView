//package com.apps2u.stickyheadercursorrecycleradapter;
//
//import android.app.Activity;
//import android.support.annotation.NonNull;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.apps2u.happiestrecyclerview.AdsHolder;
//import com.apps2u.happiestrecyclerview.R;
//import com.apps2u.happiestrecyclerview.RecyclerView;
//import com.apps2u.happiestrecyclerview.Injection;
//import com.google.android.gms.ads.AdSize;
//import com.google.android.gms.ads.doubleclick.AppEventListener;
//import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
//import com.google.android.gms.ads.doubleclick.PublisherAdView;
//
//import java.util.ArrayList;
//
///**
// * Created by AppsOuday on 9/21/2018.
// */
//
//public class ViewInjections extends Injection {
//
//    private static final AdSize[] bannerSize = new AdSize[]{AdSize.BANNER, AdSize.LARGE_BANNER, AdSize.MEDIUM_RECTANGLE};
//    private static final AdSize[] nativeSize = new AdSize[]{AdSize.FLUID};
//
//
//
//    Activity activity;
//
//    static final int Image = 1;
//    static final int Video = 2;
//    static final int Audio = 3;
//    static final int Google = 4;
//
//    public ViewInjections(Activity activity) {
//        super(new ArrayList<Integer>() {{
//            add(Image);
//            add(Video);
//            add(Audio);
//            add(Google);
//        }}, 2, 3,"test");
//
//        this.activity = activity;
//    }
//
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup injection, int viewType) {
//        View view = activity.getLayoutInflater().inflate(R.layout.ads_row, null);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
//        ViewHolder adsHolder = (ViewHolder) viewHolder;
//        switch (position){
//            case Image:
//                adsHolder.textView.setText("Image");
//                break;
//            case Video:
//                adsHolder.textView.setText("Video");
//                break;
//            case Audio:
//                adsHolder.textView.setText("Audio");
//
//                break;
//            case Google:
//
//                if(adsHolder.parentLayout.getChildCount() == 0) {
//                    PublisherAdView adView = new PublisherAdView(activity);
//                    adView.setAdSizes(nativeSize);
//                    adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
//
//                    adView.setAppEventListener(new AppEventListener() {
//                        @Override
//                        public void onAppEvent(String s, String s1) {
////                    handleNativeAdClick(context, s, s1);
//                        }
//                    });
//
//                    PublisherAdRequest.Builder adRequest = new PublisherAdRequest.Builder();
//
//                    adView.loadAd(adRequest.build());
//                    adsHolder.parentLayout.addView(adView);
//                }
//                break;
//        }
//
//
//
//
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder{
//        public RelativeLayout parentLayout;
//        public TextView textView ;
//        public ViewHolder(View itemView) {
//            super(itemView);
//            parentLayout = itemView.findViewById(R.id.parent_layout);
//            textView = itemView.findViewById(R.id.text);
//        }
//
//        public void bind(int position) {
//
//        }
//    }
//
//}
