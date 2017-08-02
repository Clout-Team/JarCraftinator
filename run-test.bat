@echo off
cls
echo Cleaning test directory...
rd /s /q test
mkdir test
echo Installing JARCraftinator
copy target\JARCraftinator-0.0.1a-SNAPSHOT-shaded.jar test\
echo Executing...
cd test/
java -jar JARCraftinator-0.0.1a-SNAPSHOT-shaded.jar
cd ..