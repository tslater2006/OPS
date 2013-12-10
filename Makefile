OCI_DIR=/usr/lib/oracle/12.1/client64/lib
JAVA_D=-Duser.timezone=GMT -Djava.library.path=$(OCI_DIR) -Dlog4j.configurationFile=conf/log4j.xml -DComponentToLoad=CLASS_SEARCH

#TRACE_FILE=-Dtracefile=trace/003_KADAMS_SSS_STUDENT_CENTER.tracesql
TRACE_FILE=-Dtracefile=trace/004_KADAMS_CLASS_SEARCH.tracesql
#TRACE_FILE=-Dtracefile=trace/005_KADAMS_SSR_SSENRL_LIST.tracesql
#TRACE_FILE=-Dtracefile=trace/006_KADAMS_SSR_SSENRL_ADD.tracesql

IGNORE_STMTS_FILE=-Dignore_stmts_file=conf/ignore_stmts.conf
JAVA_CP=bin:lib/*:$(OCI_DIR)/ojdbc7.jar
JAVA_MAIN=com.enterrupt.Main

all: build_and_run

build_and_run:
		ant build_all
		java $(JAVA_D) $(TRACE_FILE) $(IGNORE_STMTS_FILE) -cp $(JAVA_CP) $(JAVA_MAIN)
 
