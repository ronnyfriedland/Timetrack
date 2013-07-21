java -Xms32m -Xmx128m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=heapdump.hprof -Dtimetrack.popup.show=true -jar timetrack-${project.version}.jar
