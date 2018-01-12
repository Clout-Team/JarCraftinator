@echo off
echo Building Verdigris...
del /Q .\test\*.jar
call mvn clean
call mvn package
copy .\target\*.jar .\test\
echo Changing to test directory...
cd .\test\
@echo on