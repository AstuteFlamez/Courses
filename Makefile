Run : Main.class
	@java Main

Venus :
	@echo "Hello Venus"

ListADT.class : ListADT.java
	javac ListADT.java

MyList.class : MyList.java ListADT.class
	javac -cp . MyList.java

Main.class : Main.java MyList.class ListADT.class
	javac -cp . Main.java

MyFirstJUnit.class: MyFirstJUnit.java MyList.class ListADT.class
	javac -cp .:../junit5.jar MyFirstJUnit.java

Test: MyFirstJUnit.class
	java -jar ../junit5.jar -cp . -c MyFirstJUnit