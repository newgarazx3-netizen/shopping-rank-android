package com.shoppingrank.viewer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
        "#sellerOnlinePanel,#onlineSellerStatus,#extensionConnectionBadge,#onlineSellerCheckButton,#retentionAdminPanel,.retention-admin-inline,.seller-check-col,.seller-single-check-button{display:none!important;}" +
        "#dashboardView[data-role=\\\"admin\\\"] .seller-check-col{display:none!important;}" +
        "@media(max-width:760px){.overview-toolbar .toolbar-seller{display:none!important;}.mobile-rank-actions .seller-single-check-button{display:none!important;}}" +
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
        settings.setUserAgentString(settings.getUserAgentString() + " ShoppingRankViewer/0.1");

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
