COMPONENT=SSR_SSENRL_LIST
#TRACE_FILE=trace/003_KADAMS_SSS_STUDENT_CENTER.tracesql
#TRACE_FILE=trace/004_KADAMS_CLASS_SEARCH.tracesql
TRACE_FILE=trace/005_KADAMS_SSR_SSENRL_LIST.tracesql
#TRACE_FILE=trace/006_KADAMS_SSR_SSENRL_ADD.tracesql

JAVA_D=-Duser.timezone=GMT -Dlog4j.configurationFile=conf/log4j.xml -DComponentToLoad=$(COMPONENT) \
-Dtracefile=$(TRACE_FILE) -Dignore_stmts_file=conf/ignore_stmts.conf

JAVA_CP=bin:lib/*:$(OCI_DIR)/ojdbc7.jar

# For execution on local Xen server.
JAVA_D+= -DDbSID=XENCSDEV
JAVA_D+= -DDbIP=10.0.0.88
JAVA_D+= -DDbDriver=jdbc:oracle:thin

# For execution on AWS VPC.
#OCI_DIR=/usr/lib/oracle/12.1/client64/lib
#JAVA_D+= -Djava.library.path=$(OCI_DIR)
#JAVA_D+= -DDbSID=ENTCSDEV
#JAVA_D+= -DDbIP=10.0.1.88
#JAVA_D+= -DDbDriver=jdbc:oracle:oci

all: build_and_run

build_and_run:
		ant build_all
		java $(JAVA_D) -cp $(JAVA_CP) com.enterrupt.Main
 
