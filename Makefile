OCI_DIR=/usr/lib/oracle/12.1/client64/lib
JAVA_D=-Duser.timezone=GMT -Djava.library.path=$(OCI_DIR)
TRACE_FILE=-Dtracefile=trace/003_KADAMS_SSS_STUDENT_CENTER.tracesql
IGNORE_STMTS_FILE=-Dignore_stmts_file=conf/ignore_stmts.conf
JAVA_CP=bin:lib/commons-codec-1.8.jar:$(OCI_DIR)/ojdbc7.jar
JAVA_MAIN=com.enterrupt.Main

all: build_and_run

build_and_run:
		ant build
		java $(JAVA_D) $(TRACE_FILE) $(IGNORE_STMTS_FILE) -cp $(JAVA_CP) $(JAVA_MAIN)
 
