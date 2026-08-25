package com.shoppingrank.viewer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.graphics.Insets;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String DASHBOARD_URL = "https://shopping-rank-dashboard.vercel.app/?nativeViewer=1";
    private static final String DASHBOARD_HOST = "shopping-rank-dashboard.vercel.app";
    private WebView webView;
    private LinearLayout errorView;

    private static final String VIEWER_SCRIPT = "(function(){" +
        "try{" +
        "function q(s){return document.querySelector(s);}function qa(s){return Array.from(document.querySelectorAll(s));}" +
        "function sp(el,p,v){if(el)el.style.setProperty(p,v,'important');}" +
        "function hide(s){qa(s).forEach(function(el){sp(el,'display','none');});}" +
        "function apply(){" +
          "document.documentElement.classList.add('native-viewer-app');" +
          "hide('#sellerOnlinePanel,#onlineSellerStatus,#extensionConnectionBadge,#onlineSellerCheckButton,#retentionAdminPanel,.retention-admin-inline,.seller-check-col,.seller-single-check-button,#installAppButton');" +
          "var shell=q('.shell');sp(shell,'padding','6px 7px 12px');" +
          "var top=q('.compact-topbar');sp(top,'margin-bottom','5px');sp(top,'gap','3px');" +
          "var title=q('.compact-topbar h1');sp(title,'font-size','18px');sp(title,'line-height','1.1');" +
          "var eye=q('.compact-topbar .eyebrow');sp(eye,'font-size','8px');sp(eye,'margin-bottom','1px');" +
          "var actions=q('.compact-top-actions');sp(actions,'gap','4px');" +
          "qa('.compact-top-actions button').forEach(function(b){sp(b,'min-height','25px');sp(b,'height','25px');sp(b,'padding','0 7px');sp(b,'font-size','9px');});" +
          "var bar=q('.mobile-filter-toolbar');sp(bar,'min-height','28px');sp(bar,'padding','3px 7px');sp(bar,'margin-bottom','4px');" +
          "var panel=q('.compact-filter-panel');if(panel){sp(panel,'display','grid');sp(panel,'grid-template-columns','minmax(0,1fr) minmax(0,1fr)');sp(panel,'gap','6px 7px');sp(panel,'padding','7px');sp(panel,'margin-bottom','5px');}" +
          "['.filter-date','.filter-slot','.filter-platform','.filter-keyword','.filter-layout','.filter-own'].forEach(function(s){qa('.compact-filter-panel '+s).forEach(function(el){sp(el,'grid-column','auto');});});" +
          "qa('.compact-filter-panel label').forEach(function(el){sp(el,'gap','3px');sp(el,'font-size','10px');});" +
          "qa('.compact-filter-panel select').forEach(function(el){sp(el,'height','34px');sp(el,'min-height','34px');sp(el,'padding','0 8px');sp(el,'font-size','11px');sp(el,'border-radius','8px');});" +
          "var own=q('.compact-filter-panel .filter-own');if(own){sp(own,'height','34px');sp(own,'min-height','34px');sp(own,'align-self','end');sp(own,'display','flex');sp(own,'align-items','center');sp(own,'justify-content','flex-start');sp(own,'gap','6px');sp(own,'padding','0 8px');sp(own,'border','1px solid #cfd9e7');sp(own,'border-radius','8px');sp(own,'background','#fff');sp(own,'white-space','nowrap');}" +
          "var cb=q('#ownOnlyFilter');if(cb){sp(cb,'width','15px');sp(cb,'height','15px');sp(cb,'min-width','15px');sp(cb,'min-height','15px');sp(cb,'flex','0 0 15px');sp(cb,'margin','0');sp(cb,'padding','0');}" +
          "var models=q('.compact-filter-panel .filter-models');if(models){sp(models,'grid-column','1 / -1');sp(models,'gap','3px');}" +
          "var mh=q('.model-filter-heading');sp(mh,'font-size','9px');" +
          "var ms=q('.filter-models .model-filter-status');sp(ms,'font-size','8px');" +
          "var mc=q('.model-filter-controls');if(mc){sp(mc,'display','grid');sp(mc,'grid-template-columns','minmax(0,1fr) auto');sp(mc,'gap','4px');sp(mc,'align-items','stretch');}" +
          "var ta=q('.filter-models textarea');if(ta){sp(ta,'height','30px');sp(ta,'min-height','30px');sp(ta,'max-height','52px');sp(ta,'padding','4px 7px');sp(ta,'font-size','10px');sp(ta,'line-height','1.25');sp(ta,'resize','none');}" +
          "var ma=q('.filter-models .model-filter-actions');if(ma){sp(ma,'display','flex');sp(ma,'gap','3px');}" +
          "qa('.filter-models .model-filter-actions button').forEach(function(b){sp(b,'min-width','42px');sp(b,'height','30px');sp(b,'min-height','30px');sp(b,'padding','0 6px');sp(b,'font-size','9px');});" +
          "var ov=q('.overview-toolbar');sp(ov,'margin-bottom','5px');" +
        "}" +
        "apply();" +
        "if(!window.__nativeViewerObserver){window.__nativeViewerObserver=new MutationObserver(function(){clearTimeout(window.__nativeViewerTimer);window.__nativeViewerTimer=setTimeout(apply,60);});window.__nativeViewerObserver.observe(document.documentElement,{childList:true,subtree:true,attributes:true,attributeFilter:['class','data-role']});}" +
        "setTimeout(apply,250);setTimeout(apply,1000);setTimeout(apply,2500);" +
        "if(!window.__rankViewerGuard){window.__rankViewerGuard=true;document.addEventListener('click',function(e){var t=e.target&&e.target.closest?e.target.closest('.seller-single-check-button,#onlineSellerCheckButton,#retentionButton,#retentionCleanupButton,#retentionSaveButton'):null;if(t){e.preventDefault();e.stopImmediatePropagation();}},true);}" +
        "}catch(e){console.error('native viewer ui',e);}" +
        "})();";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(243, 246, 251));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(243, 246, 251));

        if (Build.VERSION.SDK_INT >= 35) {
            int fallbackTop = getSystemBarHeight("status_bar_height");
            int fallbackBottom = getSystemBarHeight("navigation_bar_height");
            root.setPadding(0, Math.max(fallbackTop, dp(24)) + dp(4), 0, Math.max(fallbackBottom, 0));
        }

        if (Build.VERSION.SDK_INT >= 30) {
            root.setOnApplyWindowInsetsListener((v, insets) -> {
                Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
                int top = bars.top;
                int bottom = bars.bottom;
                if (Build.VERSION.SDK_INT >= 35) top = Math.max(top, getSystemBarHeight("status_bar_height"));
                v.setPadding(bars.left, top + dp(4), bars.right, bottom);
                return insets;
            });
            root.requestApplyInsets();
        } else if (Build.VERSION.SDK_INT >= 23) {
            root.setOnApplyWindowInsetsListener((v, insets) -> {
                v.setPadding(0, insets.getSystemWindowInsetTop() + dp(4), 0, insets.getSystemWindowInsetBottom());
                return insets;
            });
            root.requestApplyInsets();
        }

        webView = new WebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setUserAgentString(settings.getUserAgentString() + " ShoppingRankViewer/0.3");

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        WebView.setWebContentsDebuggingEnabled(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new RankWebViewClient());

        errorView = createErrorView();
        errorView.setVisibility(View.GONE);

        root.addView(webView);
        root.addView(errorView);
        setContentView(root);

        if (savedInstanceState == null) {
            webView.loadUrl(DASHBOARD_URL);
        } else {
            webView.restoreState(savedInstanceState);
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private int getSystemBarHeight(String name) {
        int id = getResources().getIdentifier(name, "dimen", "android");
        return id > 0 ? getResources().getDimensionPixelSize(id) : 0;
    }

    private LinearLayout createErrorView() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(android.view.Gravity.CENTER);
        box.setPadding(40, 40, 40, 40);
        box.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        TextView title = new TextView(this);
        title.setText("대시보드에 연결할 수 없습니다");
        title.setTextSize(20);
        title.setTextColor(Color.rgb(23, 32, 51));
        title.setGravity(android.view.Gravity.CENTER);

        TextView desc = new TextView(this);
        desc.setText("인터넷 연결을 확인한 뒤 다시 시도해 주세요.");
        desc.setTextSize(14);
        desc.setTextColor(Color.rgb(113, 128, 150));
        desc.setGravity(android.view.Gravity.CENTER);
        desc.setPadding(0, 18, 0, 22);

        Button retry = new Button(this);
        retry.setText("다시 연결");
        retry.setOnClickListener(v -> {
            errorView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl(DASHBOARD_URL);
        });

        box.addView(title);
        box.addView(desc);
        box.addView(retry);
        return box;
    }

    private class RankWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            applyViewerUi(view);
        }

        private void applyViewerUi(WebView view) {
            view.evaluateJavascript(VIEWER_SCRIPT, null);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri uri = request.getUrl();
            String host = uri.getHost();
            if (host != null && host.equalsIgnoreCase(DASHBOARD_HOST)) {
                return false;
            }
            openExternal(uri);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (request.isForMainFrame()) {
                webView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void openExternal(Uri uri) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException ignored) {
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (webView != null) webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
        }
        super.onDestroy();
    }
}
