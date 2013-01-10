java -cp lib -jar lib/liquibase-core-2.0.5.jar --classpath="lib/derby-10.8.1.2.jar" --changeLogFile="db/master.xml" --defaultsFile="db/liquibase.properties" --logLevel=info update
