if [ ! -e target/database/timetrack-database ]; then
  mv target/database/timetable-database target/database/timetrack-database
fi

java -classpath "lib/derby-10.14.2.0.jar:lib/logback-core-1.2.3.jar:lib/logback-classic-1.2.3.jar:lib/slf4j-api-2.0.0-alpha1.jar:lib/slf4j-jdk14-2.0.0-alpha1.jar:lib/liquibase-core-3.8.4.jar" liquibase.integration.commandline.Main --changeLogFile="db/master.xml" --defaultsFile="db/liquibase.properties" --logLevel=info update
