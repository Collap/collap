./tomcat/bin/shutdown.sh
gradle build tomcatCopy tomcatClean
cd tomcat
./bin/startup.sh
cd ..