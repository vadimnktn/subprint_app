package com.subprint;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    
    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout errorLayout;
    private View splashScreen;
    private TextView errorText;
    
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
            setSplashColorFromConfig();
            String serverUrl = getServerUrlFromConfig();
            webView.loadUrl(serverUrl);
            
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    splashScreen.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }
                
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    showError("Ошибка загрузки: " + description);
                }
            });
            
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            
            swipeRefreshLayout.setOnRefreshListener(() -> {
                webView.reload();
                swipeRefreshLayout.setRefreshing(false);
            });
            
        } catch (Exception e) {
            showError("Ошибка конфига: " + e.getMessage());
        }
    }
    
    private String getServerUrlFromConfig() throws Exception {
        File configFile = getConfigFile();
        if (!configFile.exists()) {
            copyDefaultConfig();
        }
        
        FileInputStream inputStream = new FileInputStream(configFile);
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();
        
        String ip = properties.getProperty("server_ip");
        String port = properties.getProperty("server_port");
        
        if (ip == null || port == null) {
            throw new Exception("server_ip or server_port not found in config");
        }
        
        return "http://" + ip + ":" + port;
    }
    
    private void setSplashColorFromConfig() throws Exception {
        File configFile = getConfigFile();
        if (!configFile.exists()) {
            copyDefaultConfig();
        }
        
        FileInputStream inputStream = new FileInputStream(configFile);
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();
        
        String color = properties.getProperty("splash_color");
        if (color == null) {
            throw new Exception("splash_color not found in config");
        }
        
        splashScreen.setBackgroundColor(Color.parseColor(color));
    }
    
    private File getConfigFile() {
        File appDir = new File(Environment.getExternalStorageDirectory(), "Android/data/com.subprint/files");
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        return new File(appDir, "config.properties");
    }
    
    private void copyDefaultConfig() throws Exception {
        // Создаем дефолтный конфиг если файла нет
        File configFile = getConfigFile();
        java.io.FileOutputStream outputStream = new java.io.FileOutputStream(configFile);
        String defaultConfig = "# subprint_app/app/src/main/assets/config.properties\n" +
                "# Конфигурационный файл с настройками приложения\n" +
                "\n" +
                "# Название приложения\n" +
                "app_name=SubPrint\n" +
                "\n" +
                "# Адрес сервера\n" +
                "server_ip=192.168.0.10\n" +
                "server_port=8001\n" +
                "\n" +
                "# Таймауты в секундах\n" +
                "connection_timeout=15\n" +
                "read_timeout=30\n" +
                "\n" +
                "# Цвет загрузки (splash screen) в HEX формате\n" +
                "splash_color=#FFFFFF";
        outputStream.write(defaultConfig.getBytes());
        outputStream.close();
    }
    
    private void showError(String message) {
        try {
            setSplashColorFromConfig();
        } catch (Exception e) {
            // Если не получилось - оставляем текущий цвет
        }
        
        splashScreen.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        errorText.setText(message);
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