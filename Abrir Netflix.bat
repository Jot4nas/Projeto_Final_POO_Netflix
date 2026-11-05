@echo off
rem Abre o sistema. Precisa do Java 11+ e do MySQL Server ligado.
rem (Tambem da para abrir com dois cliques direto no Netflix.jar.)
cd /d "%~dp0"

where java >nul 2>nul
if errorlevel 1 (
    echo ERRO: Java nao encontrado. Instale o Java 17 ^(ex.: https://adoptium.net^) e tente de novo.
    pause
    exit /b 1
)

java -jar Netflix.jar
if errorlevel 1 pause
