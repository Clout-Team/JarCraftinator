@echo off
IF NOT "%1"=="" (
	echo %1
	GOTO :eof
)

mvn -q -Dexec.executable="mvn_version" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec