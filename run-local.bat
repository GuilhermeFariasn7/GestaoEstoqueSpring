@echo off
echo =========================================
echo   INICIANDO APLICACAO SPRING BOOT LOCAL
echo =========================================

REM Verifica se o arquivo .env.local existe
if exist .env.local (
    echo Carregando variaveis de .env.local...

    REM Lê cada linha do .env.local
    for /F "usebackq tokens=*" %%i in (".env.local") do (
        REM Remove linhas de comentário e vazias
        echo %%i | findstr /v "^#" | findstr /v "^$" >nul && (
            set "%%i"
        )
    )
) else (
    echo AVISO: Arquivo .env.local nao encontrado!
    echo Usando variaveis padrao...
)

REM Variáveis padrão se não existirem
if not defined PORT set PORT=8080
if not defined DATABASE_URL set DATABASE_URL=jdbc:mysql://localhost:3306/applogin
if not defined DB_USERNAME set DB_USERNAME=root
if not defined DB_PASSWORD set DB_PASSWORD=
if not defined UPLOAD_DIR set UPLOAD_DIR=./uploads

echo.
echo Configuracao carregada:
echo PORT: %PORT%
echo DATABASE_URL: %DATABASE_URL%
echo DB_USERNAME: %DB_USERNAME%
echo UPLOAD_DIR: %UPLOAD_DIR%
echo =========================================

REM Inicia a aplicação
mvn spring-boot:run