runApplication:
	javac App.java
	java App

runAllTests:
	javac -cp .:../junit5.jar FrontendTests.java
	java -jar ../junit5.jar --class-path . --select-class=FrontendTests
	
clean:
	rm -f *.class

.PHONY: runApplication runAllTests clean