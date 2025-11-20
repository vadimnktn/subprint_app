package com.subprint;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.io.InputStream;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    
    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout errorLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        webView = findViewById(R.id.webView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        errorLayout = findViewById(R.id.errorLayout);
        
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        
        // Читаем URL из конфига
        String serverUrl = getServerUrlFromConfig();
        webView.loadUrl(serverUrl);
        
        // Настройка pull-to-refresh
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
            return "http://192.168.0.10:8001"; // fallback
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