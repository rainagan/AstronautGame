NAME = "AstronautBasic"

# you may need to pass OS=win to run on windows
OS = 

# HACK: vecmath is included regardless if needed
all:
	@echo "Compiling..."
	javac -cp vecmath.jar *.java

run: all
# windows needs a semicolon
ifeq ($(OS),win)
		@echo "Running on windows ..."
		java -cp "vecmath.jar;." $(NAME)
# everyone else likes a colon
else
		@echo "Running ..."
		java -cp "vecmath.jar:." $(NAME)
endif

clean:
	rm -rf *.class

