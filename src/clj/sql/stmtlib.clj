(ns sql.stmtlib
	(import com.mchange.v2.c3p0.ComboPooledDataSource)
	(require [clojure.java.jdbc :as jdbc])
	(:gen-class))

(def ^{:private true} jdbc-url
	"jdbc:oracle:thin:SYSADM/SYSADM@//10.0.0.88:1521/XENCSDEV")

(def ^{:private true} pooled-conn-ds
	"Returns the pooled connection's underlying DataSource
   in the form of a map."
	(delay
		(hash-map
			:datasource
			(doto (ComboPooledDataSource.) (.setJdbcUrl jdbc-url)))))

(defn query-pnlgrpdefn
	[[pnlgrpname market] func]
	(jdbc/db-query-with-resultset
		@pooled-conn-ds
		[(str "SELECT DESCR, ACTIONS, VERSION, SEARCHRECNAME, ADDSRCHRECNAME,  "
					"SEARCHPNLNAME, LOADLOC, SAVELOC, DISABLESAVE, PRIMARYACTION, "
					"DFLTACTION, DFLTSRCHTYPE,  DEFERPROC, EXPENTRYPROC, WSRPCOMPLIANT, "
					"REQSECURESSL, INCLNAVIGATION, FORCESEARCH, ALLOWACTMODESEL,  "
					"PNLNAVFLAGS, TBARBTNS, SHOWTBAR, ADDLINKMSGSET, ADDLINKMSGNUM, "
					"SRCHLINKMSGSET, SRCHLINKMSGNUM,  SRCHTEXTMSGSET, SRCHTEXTMSGNUM, "
					"OBJECTOWNERID, TO_CHAR(CAST((LASTUPDDTTM) AS TIMESTAMP),"
					"'YYYY-MM-DD-HH24.MI.SS.FF'), LASTUPDOPRID,  DESCRLONG  FROM "
					"PSPNLGRPDEFN WHERE PNLGRPNAME = ? AND MARKET = ?")
			pnlgrpname market]
		func))

(defn query-pnlgroup
	[[pnlgrpname market] func]
	(jdbc/db-query-with-resultset
		@pooled-conn-ds	
		[(str	"SELECT PNLNAME, ITEMNAME, HIDDEN, ITEMLABEL, FOLDERTABLABEL, "
					"SUBITEMNUM FROM PSPNLGROUP WHERE PNLGRPNAME = ? AND MARKET = ? "
					"ORDER BY SUBITEMNUM")
			pnlgrpname market]
		func))

(defn query-menudefn
	[[menuname] func]
	(jdbc/db-query-with-resultset
		@pooled-conn-ds
		[(str "SELECT MENULABEL, MENUGROUP, GROUPORDER, MENUORDER, "
					"VERSION, INSTALLED, GROUPSEP, MENUSEP, MENUTYPE, OBJECTOWNERID, "
					"LASTUPDOPRID, TO_CHAR(CAST((LASTUPDDTTM) AS TIMESTAMP),"
					"'YYYY-MM-DD-HH24.MI.SS.FF'), DESCR, DESCRLONG FROM PSMENUDEFN "
					"WHERE MENUNAME = ?")
			menuname]
		func))

(defn query-menuitem
	[[menuname] func]
	(jdbc/db-query-with-resultset
		@pooled-conn-ds
		[(str "SELECT BARNAME, ITEMNAME, BARLABEL, ITEMLABEL, "
					"MARKET, ITEMTYPE, PNLGRPNAME, SEARCHRECNAME, ITEMNUM, "
					"XFERCOUNT FROM PSMENUITEM WHERE MENUNAME = ? ORDER BY ITEMNUM")
			menuname]
		func))
