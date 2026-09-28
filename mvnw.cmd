@echo off
setlocal
set MAVEN_DIR=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6-bin
if not exist "%MAVEN_DIR%" (
    echo Baixando Maven 3.9.6 para o ambiente local...
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; New-Item -ItemType Directory -Path '%MAVEN_DIR%' -Force | Out-Null; Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip' -OutFile '%MAVEN_DIR%\mvn.zip'; Expand-Archive -Path '%MAVEN_DIR%\mvn.zip' -DestinationPath '%MAVEN_DIR%' -Force; Remove-Item '%MAVEN_DIR%\mvn.zip'"
)
for /f "delims=" %%I in ('dir /b /s "%MAVEN_DIR%\mvn.cmd" 2^>nul') do (
    set "MVN_CMD=%%I"
)
if defined MVN_CMD (
    "%MVN_CMD%" %*
) else (
    echo Erro ao localizar o comando Maven em %MAVEN_DIR%
    exit /b 1
)
endlocal
