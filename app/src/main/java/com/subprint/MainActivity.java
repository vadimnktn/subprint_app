package com.subprint;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.io.InputStream;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    
    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View splashScreen;
    private String serverUrl;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Устанавливаем фон ДО setContentView
        try {
            InputStream inputStream = getAssets().open("config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            String color = properties.getProperty("splash_color");
            if (color != null) {
                getWindow().getDecorView().setBackgroundColor(Color.parseColor(color));
            }
        } catch (Exception e) {
            // Без оповещений
        }
        
        setContentView(R.layout.activity_main);
        
        webView = findViewById(R.id.webView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        splashScreen = findViewById(R.id.splashScreen);
        
        // Полный экран
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        
        try {
            String appName = getAppNameFromConfig();
            setTitle(appName);
            
            setSplashColorFromConfig();
            serverUrl = getServerUrlFromConfig();
            int startDelay = getStartDelayFromConfig();
            
            // Отключаем кэш
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.clearCache(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    splashScreen.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
                
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    // Перехватываем ошибку но ничего не показываем
                }
                
                @Override
                public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                    // Перехватываем HTTP ошибки
                }
            });
            
            // ЗАДЕРЖКА из конфига перед первой загрузкой
            splashScreen.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            
            new android.os.Handler().postDelayed(() -> {
                webView.loadUrl(serverUrl);
            }, startDelay);
            
            swipeRefreshLayout.setOnRefreshListener(() -> {
                webView.reload();
                swipeRefreshLayout.setRefreshing(false);
            });
            
        } catch (Exception e) {
            // Без оповещений об ошибках
        }
    }
    
    private String getAppNameFromConfig() throws Exception {
        InputStream inputStream = getAssets().open("config.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        
        String appName = properties.getProperty("app_name");
        if (appName == null) {
            throw new Exception();
        }
        
        return appName;
    }
    
    private String getServerUrlFromConfig() throws Exception {
        InputStream inputStream = getAssets().open("config.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        
        String ip = properties.getProperty("server_ip");
        String port = properties.getProperty("server_port");
        
        if (ip == null || port == null) {
            throw new Exception();
        }
        
        return "http://" + ip + ":" + port;
    }
    
    private int getStartDelayFromConfig() throws Exception {
        InputStream inputStream = getAssets().open("config.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        
        String delay = properties.getProperty("start_delay");
        if (delay == null) {
            return 2000; // значение по умолчанию
        }
        
        return Integer.parseInt(delay);
    }
    
    private void setSplashColorFromConfig() throws Exception {
        InputStream inputStream = getAssets().open("config.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        
        String color = properties.getProperty("splash_color");
        if (color == null) {
            throw new Exception();
        }
        
        splashScreen.setBackgroundColor(Color.parseColor(color));
    }
    
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}