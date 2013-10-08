SRC_PACKAGE=src/com/enterrupt
JAVA_D=-Duser.timezone=GMT -Djava.library.path=/usr/lib/oracle/12.1/client64/lib
JAVA_CP=bin:/usr/lib/oracle/12.1/client64/lib/ojdbc7.jar
JAVA_MAIN=com.enterrupt.DbBroker
BUILD_DIR=bin

all: main

main: com.enterrupt
		java $(JAVA_D) -cp $(JAVA_CP) com.enterrupt.DbBroker

com.enterrupt:
		javac -d $(BUILD_DIR) $(SRC_PACKAGE)/*.java

run:
		java $(JAVA_D) -cp $(JAVA_CP) com.enterrupt.DbBroker
 
