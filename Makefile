all: 
	javac -d class -classpath .:class/* src/*.java
clean:
	rm -f class/*.class
