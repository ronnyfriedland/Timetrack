@echo off

IF not exist ${timetrack.path}/database/timetrack-database (
 xcopy ${timetrack.path}\database\timetable-database\* ${timetrack.path}\database\timetrack-database /i/s/q
 rmdir ${timetrack.path}\database\timetable-database /s/q
)
 
java -cp lib -jar lib/liquibase-core-1.9.5.jar --classpath="lib/derby-10.14.2.0.jar" --changeLogFile="db/master.xml" --defaultsFile="db/liquibase.properties" --logLevel=info update
