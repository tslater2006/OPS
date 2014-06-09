#*===---------------------------------------------------------------------===*#
#*                       The OpenPplSoft Runtime Project                     *#
#*                                                                           *#
#*              This file is distributed under the MIT License.              *#
#*                         See LICENSE.md for details.                       *#
#*===---------------------------------------------------------------------===*#

# Note: TRACE_FILE_DATE Overrides the current date used by the EVM in order
# to emit the correct date for trace file verification purposes.

COMPONENT=SSS_STUDENT_CENTER

# Trace files generated on AWS VPC Cluster:
TRACE_FILE=trace/003_KADAMS_SSS_STUDENT_CENTER.tracesql
#TRACE_FILE=trace/004_KADAMS_CLASS_SEARCH.tracesql
#TRACE_FILE=trace/005_KADAMS_SSR_SSENRL_LIST.tracesql
#TRACE_FILE=trace/006_KADAMS_SSR_SSENRL_ADD.tracesql
TRACE_FILE_DATE=2013-11-16

# Trace files generated on local Xen cluster:
#TRACE_FILE=trace/007_KADAMS_SSR_SSENRL_LIST.tracesql
#TRACE_FILE_DATE=2014-01-15

JAVA_CP=bin:lib/*
JAVA_D=-Duser.timezone=GMT -Dlog4j.configurationFile=resources/log4j2.xml -DComponentToLoad=$(COMPONENT) \
-Dtracefile=$(TRACE_FILE) -Dignore_stmts_file=conf/ignore_stmts.conf -DcacheProgText=true \
-DtraceFileDate=$(TRACE_FILE_DATE) -Ddefn_stmts_file=conf/defn_stmts.conf

# For execution on local Xen server.
#JAVA_D+= -DDbSID=XENCSDEV
#JAVA_D+= -DDbIP=10.0.0.88
#JAVA_D+= -DDbDriver=jdbc:oracle:thin

# For execution on AWS VPC.
JAVA_D+= -DDbSID=ENTCSDEV
JAVA_D+= -DDbIP=10.0.1.88
JAVA_D+= -DDbDriver=jdbc:oracle:thin

# Enables OCI driver on AWS VPC; must remember to uncomment "thin" line above.
#OCI_DIR=/usr/lib/oracle/12.1/client64/lib
#JAVA_D+= -Djava.library.path=$(OCI_DIR)
#JAVA_D+= -DDbDriver=jdbc:oracle:oci

all: build run

build:
	ant build_all

run:
	java $(JAVA_D) -cp $(JAVA_CP) com.enterrupt.Main
 
