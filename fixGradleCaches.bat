taskkill /F /IM java.exe & .\gradlew clean --no-daemon & rmdir /S /Q %USERPROFILE%\.gradle\build-cache-1
