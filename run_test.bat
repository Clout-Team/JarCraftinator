@echo off

echo Detecting jar version...
call mvn_version > version
set /P version=<version
del version

echo Building Verdigris %version%...
del /Q .\test\*.jar
call mvn clean
call mvn package
copy .\target\*.jar .\test\

echo Changing to test directory...
cd .\test\

java -jar JARCraftinator-%version%.jar

cd ..
@echo on