@echo off

IF not exist data/database/timetrack-database (
 xcopy data\database\timetable-database\* data\database\timetrack-database /i/s/q
 rmdir data\database\timetable-database /s/q
)
 
java -cp lib -jar lib/liquibase-core-1.9.5.jar --classpath="lib/derby-10.8.1.2.jar" --changeLogFile="db/master.xml" --defaultsFile="db/liquibase.properties" --logLevel=info update
