@echo off
echo y | C:\Windows\System32\jdk-25.0.1\bin\keytool.exe -genkey -v -keystore subprint.keystore -alias subprint -keyalg RSA -keysize 2048 -validity 10000 -storepass subprint -keypass subprint -dname "CN=subprint, OU=subprint, O=subprint, L=St. Petersburg, ST=St. Petersburg, C=RU"
echo Key created successfully!
pause