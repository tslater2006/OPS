(ns pt.component
	(require [clojure.java.jdbc :as jdbc])
	(:use [sql.stmtlib])
	(:use [runtime.log :only [get-log INFO DEBUG]])
	(:gen-class))

(def ^{:private true} log (get-log *ns*))

(defn init-component [pnlgrpname market]
	(INFO log "Loading Component:{}.{}" (object-array [pnlgrpname market]))

	(jdbc/db-query-with-resultset
		@pooled-conn-ds
		["SELECT * FROM PSPNLGRPDEFN WHERE PNLGRPNAME = ?" pnlgrpname]
		(fn [rs]
			(def rs-seq (jdbc/result-set-seq rs))
			(assert (= (count rs-seq) 1))
			(select-keys (first rs-seq) 
					[:actions
					 :addsrchrecname
					 :dfltaction
					 :searchrecname
					 :primaryaction]))))
