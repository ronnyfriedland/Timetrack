java -cp lib -Xms32m -Xmx128m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=heapdump.hprof -Dtimetable.popup.show=true -jar timetable-${project.version}.jar
