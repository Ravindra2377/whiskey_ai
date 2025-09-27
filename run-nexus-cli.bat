@echo off
setlocal enabledelayedexpansion
set JAR_PATH=%~dp0nexus\target\nexus-1.0.0.jar
if not exist "%JAR_PATH%" (
  echo Building Nexus AI CLI...
  pushd "%~dp0nexus"
  call mvnw.cmd -q -Pcli package
  popd
)
if not exist "%JAR_PATH%" (
  echo ERROR: Jar not found: %JAR_PATH%
  exit /b 1
)
java -jar "%JAR_PATH%" %*
