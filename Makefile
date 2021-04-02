JFLAGS = -g
JC = javac
JVM=java
.SUFFIXES: .java .class
run:
	$(JC) $(JFLAGS) *.java
	 $(JVM) Main
clean:
	$(RM) *.class
