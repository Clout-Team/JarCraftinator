@echo off
IF "%1"=="" (
	echo No action specified.
	goto :end
)

IF "%1"=="build" (
	echo Building...
	call mvn clean
	call mvn package
	goto :end
)

IF "%1"=="test" (
	echo Executing test...
	call run_test.bat
	goto :eof
)

:end
@echo on