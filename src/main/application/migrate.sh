if [ ! -e ${timetrack.path}/database/timetrack-database ]; then
  mv ${timetrack.path}/database/timetable-database ${timetrack.path}/database/timetrack-database
fi

java -cp lib -jar lib/liquibase-core-1.9.5.jar --classpath="lib/derby-10.8.1.2.jar" --changeLogFile="db/master.xml" --defaultsFile="db/liquibase.properties" --logLevel=info update
