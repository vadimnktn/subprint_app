package com.subprint;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.io.InputStream;
import java.util.Properties;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    
    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout errorLayout;
    private View splashScreen;
    private TextView errorText;
    private String serverUrl;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        webView = findViewById(R.id.webView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        errorLayout = findViewById(R.id.errorLayout);
        splashScreen = findViewById(R.id.splashScreen);
        errorText = findViewById(R.id.errorText);
        
        // Полный экран
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        
        try {
            // Установить название приложения из конфига
            String appName = getAppNameFromConfig();
            setTitle(appName);
            
            setSplashColorFromConfig();
            serverUrl = getServerUrlFromConfig();
            
            // Проверяем доступность сервера перед загрузкой
            checkServerAndLoad();
            
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    splashScreen.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            
            swipeRefreshLayout.setOnRefreshListener(() -> {
                // При ручном обновлении тоже проверяем доступность
                checkServerAndLoad();
            });
            
        } catch (Exception e) {
            showError();
        }
    }
    
    private void checkServerAndLoad() {
        new Thread(() -> {
            boolean serverAvailable = isServerAvailable();
            
            runOnUiThread(() -> {
                if (serverAvailable) {
                    // Сервер доступен - загружаем страницу
                    splashScreen.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                    webView.loadUrl(serverUrl);
                } else {
                    // Сервер недоступен - показываем нашу ошибку
                    showError();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }).start();
    }
    
    private boolean isServerAvailable() {
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            return (responseCode == 200);
        } catch (Exception e) {
            return false;
        }
    }
    
    private String getAppNameFromConfig() throws Exception {
        InputStream inputStream = getAssets().open("config.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        
        String appName = properties.getProperty("app_name");
        if (appName == null) {
            throw new Exception("app_name not found in config");
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
            throw new Exception("server_ip or server_port not found in config");
        }
        
        return "http://" + ip + ":" + port;
    }
    
    private void setSplashColorFromConfig() throws Exception {
        InputStream inputStream = getAssets().open("config.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        
        String color = properties.getProperty("splash_color");
        if (color == null) {
            throw new Exception("splash_color not found in config");
        }
        
        splashScreen.setBackgroundColor(Color.parseColor(color));
        // Устанавливаем тот же фон для errorLayout
        errorLayout.setBackgroundColor(Color.parseColor(color));
    }
    
    private void showError() {
        splashScreen.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
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