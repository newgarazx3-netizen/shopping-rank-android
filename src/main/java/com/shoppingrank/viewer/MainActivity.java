package com.shoppingrank.viewer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
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

    // Viewing-only mode. Management/seller verification UI is hidden and clicks are blocked.
    private static final String VIEWER_SCRIPT = "(function(){" +
        "try{" +
        "document.documentElement.classList.add('native-viewer-app');" +
        "var id='native-viewer-style';" +
        "if(!document.getElementById(id)){var s=document.createElement('style');s.id=id;s.textContent='" +
        "#sellerOnlinePanel,#onlineSellerStatus,#extensionConnectionBadge,#onlineSellerCheckButton,#retentionAdminPanel,.retention-admin-inline,.seller-check-col,.seller-single-check-button,#installAppButton{display:none!important;}" +
        "#dashboardView[data-role=\"admin\"] .seller-check-col{display:none!important;}" +
        "@media(max-width:760px){" +
        ".overview-toolbar .toolbar-seller{display:none!important;}.mobile-rank-actions .seller-single-check-button{display:none!important;}" +
        ".shell{padding:5px 7px 12px!important;}" +
        ".compact-topbar{padding-top:3px!important;margin-bottom:5px!important;}" +
        ".compact-topbar h1{font-size:18px!important;line-height:1.12!important;}" +
        ".compact-topbar .eyebrow{font-size:8px!important;margin-bottom:1px!important;}" +
        ".compact-top-actions{gap:4px!important;}" +
        ".compact-top-actions button{min-height:25px!important;padding:0 7px!important;font-size:9px!important;}" +
        ".mobile-filter-toolbar{min-height:28px!important;padding:3px 7px!important;margin-bottom:4px!important;}" +
        ".compact-filter-panel{grid-template-columns:minmax(0,1fr) minmax(0,1fr)!important;gap:6px 7px!important;padding:7px!important;margin-bottom:5px!important;}" +
        ".compact-filter-panel .filter-date,.compact-filter-panel .filter-slot,.compact-filter-panel .filter-platform,.compact-filter-panel .filter-keyword,.compact-filter-panel .filter-layout,.compact-filter-panel .filter-own{grid-column:auto!important;}" +
        ".compact-filter-panel label{gap:3px!important;font-size:10px!important;}" +
        ".compact-filter-panel select{height:34px!important;padding:0 8px!important;font-size:11px!important;border-radius:8px!important;}" +
        ".compact-filter-panel .filter-own{height:34px!important;min-height:34px!important;align-self:end!important;display:flex!important;align-items:center!important;justify-content:flex-start!important;gap:7px!important;padding:0 9px!important;border:1px solid #cfd9e7!important;border-radius:8px!important;background:#fff!important;white-space:nowrap!important;}" +
        ".compact-filter-panel .filter-own input{width:16px!important;height:16px!important;min-width:16px!important;min-height:16px!important;flex:0 0 16px!important;margin:0!important;padding:0!important;box-shadow:none!important;}" +
        ".compact-filter-panel .filter-models{grid-column:1/-1!important;gap:3px!important;}" +
        ".model-filter-heading{font-size:9px!important;}" +
        ".filter-models .model-filter-status{font-size:8px!important;}" +
        ".model-filter-controls{gap:4px!important;}" +
        ".filter-models textarea{height:31px!important;min-height:31px!important;max-height:56px!important;padding:5px 7px!important;font-size:10px!important;line-height:1.3!important;}" +
        ".filter-models .model-filter-actions{gap:3px!important;}" +
        ".filter-models .model-filter-actions button{min-width:42px!important;min-height:31px!important;height:31px!important;padding:0 6px!important;font-size:9px!important;}" +
        ".overview-toolbar{margin-bottom:5px!important;}" +
        "}" +
        "';document.head.appendChild(s);}" +
        "if(!window.__rankViewerGuard){window.__rankViewerGuard=true;document.addEventListener('click',function(e){var t=e.target&&e.target.closest?e.target.closest('.seller-single-check-button,#onlineSellerCheckButton,#retentionButton,#retentionCleanupButton,#retentionSaveButton'):null;if(t){e.preventDefault();e.stopImmediatePropagation();}},true);}" +
        "}catch(e){}" +
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
            root.setOnApplyWindowInsetsListener((v, insets) -> {
                // Android 15 edge-to-edge 강제 적용 시 상태바/내비게이션바와 WebView가 겹치지 않게 함.
                v.setPadding(
                        0,
                        insets.getSystemWindowInsetTop(),
                        0,
                        insets.getSystemWindowInsetBottom()
                );
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
        settings.setUserAgentString(settings.getUserAgentString() + " ShoppingRankViewer/0.2");

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
