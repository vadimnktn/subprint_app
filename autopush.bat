@echo off
echo Automatic Git Push Script
echo.

:: Получаем текущую дату и время
for /f "tokens=1-3 delims=/" %%a in ('date /t') do set currentdate=%%c-%%b-%%a
for /f "tokens=1-2 delims=:" %%a in ('time /t') do set currenttime=%%a:%%b

:: Коммит с датой и временем
git add .
git commit -m "Auto commit %currentdate% %currenttime%"

:: Пуш
git push origin main

echo.
echo Pushed successfully at %currentdate% %currenttime%
pause