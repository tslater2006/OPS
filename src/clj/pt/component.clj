(ns pt.component
  (import org.apache.logging.log4j.Logger)
  (import org.apache.logging.log4j.LogManager)
	(import java.sql.DriverManager)
	(:use [runtime.log :only [get-log INFO DEBUG]])
	(:gen-class))

(def ^{:private true} log (get-log *ns*))

(defn load-defn [pnlgrpname market]
	(INFO log "Loading Component:{}.{}" (object-array [pnlgrpname market]))
	pnlgrpname
)

(defn ora-test []
	(DEBUG log "Start of ora-test")  
	(def conn	(. DriverManager getConnection
				"jdbc:oracle:thin:@//10.0.0.88:1521/XENCSDEV" "SYSADM" "SYSADM"))
	(def pstmt (. conn prepareStatement
				"SELECT * FROM PSPNLGRPDEFN"))
	(def rs (. pstmt executeQuery))
	(. rs next)
	(def temp (. rs getString "SEARCHRECNAME"))
	(DEBUG log "Search record name: {}" (object-array [temp]))
	temp
)
