@ECHO OFF 

cd AgentInstaller
java -cp .;httpcore-4.3.3.jar;httpclient-4.3.6.jar;commons-logging-1.1.3.jar silentinstaller/SilentAgentInstaller
