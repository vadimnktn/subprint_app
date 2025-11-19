// subprint_app/app/src/main/java/com/subprint/MainActivity.java
// Основная активность приложения - управляет WebView и обработкой ошибок
package com.subprint;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import java.util.Properties;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout errorLayout;
    private String serverUrl;
    private int connectionTimeout;
    private int readTimeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        loadConfig();
        initViews();
        setupWebView();
        loadWebPage();
    }

    private void loadConfig() {
        try {
            InputStream inputStream = getAssets().open("config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            
            String appName = properties.getProperty("app_name", "SubPrint");
            String serverIp = properties.getProperty("server_ip", "192.168.1.100");
            String serverPort = properties.getProperty("server_port", "8880");
            connectionTimeout = Integer.parseInt(properties.getProperty("connection_timeout", "15"));
            readTimeout = Integer.parseInt(properties.getProperty("read_timeout", "30"));
            
            serverUrl = "http://" + serverIp + ":" + serverPort;
            setTitle(appName);
            
        } catch (Exception e) {
            serverUrl = "http://192.168.1.100:8880";
            connectionTimeout = 15;
            readTimeout = 30;
        }
    }

    private void initViews() {
        webView = findViewById(R.id.webView);
        errorLayout = findViewById(R.id.errorLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadWebPage();
            swipeRefreshLayout.setRefreshing(false);
        });
        
        swipeRefreshLayout.setEnabled(true);
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                showErrorLayout();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!url.equals("about:blank")) {
                    hideErrorLayout();
                }
            }
        });
        
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    private void loadWebPage() {
        webView.stopLoading();
        webView.loadUrl(serverUrl);
        
        webView.postDelayed(() -> {
            if (webView.getProgress() < 100) {
                showErrorLayout();
            }
        }, connectionTimeout * 1000);
    }

    private void showErrorLayout() {
        webView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorLayout() {
        errorLayout.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}