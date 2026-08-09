JUNIT_JAR = ../junit5.jar
JAVA_SOURCES = $(wildcard *.java)

.PHONY: startServer runAllTests clean

startServer: compile
	java WebApp 8080

runAllTests: compile
	java -jar $(JUNIT_JAR) --class-path . --select-class BackendTests

compile:
	javac -cp $(JUNIT_JAR):. $(JAVA_SOURCES)

clean:
	rm -f *.class