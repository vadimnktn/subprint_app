package com.subprint;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.io.InputStream;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    
    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout errorLayout;
    private ImageView splashScreen;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        webView = findViewById(R.id.webView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        errorLayout = findViewById(R.id.errorLayout);
        splashScreen = findViewById(R.id.splashScreen);
        
        // Полный экран
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Скрываем splash когда страница загрузилась
                splashScreen.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });
        
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        
        // Читаем URL из конфига
        String serverUrl = getServerUrlFromConfig();
        webView.loadUrl(serverUrl);
        
        swipeRefreshLayout.setOnRefreshListener(() -> {
            webView.reload();
            swipeRefreshLayout.setRefreshing(false);
        });
    }
    
    private String getServerUrlFromConfig() {
        try {
            InputStream inputStream = getAssets().open("config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            
            String ip = properties.getProperty("server_ip", "192.168.0.10");
            String port = properties.getProperty("server_port", "8001");
            
            return "http://" + ip + ":" + port;
        } catch (Exception e) {
            return "http://192.168.0.10:8001";
        }
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