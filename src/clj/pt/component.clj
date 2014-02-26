(ns pt.component
	(require [clojure.java.jdbc :as jdbc])
	(:use [sql.stmtlib :as stmtlib])
	(:use [runtime.log :only [get-log INFO DEBUG]])
	(:gen-class))

(def ^{:private true} log (get-log *ns*))

(defn init-component [pnlgrpname market]
	(INFO log "Loading Component:{}.{}" (object-array [pnlgrpname market]))

	(def initial-rec (stmtlib/query-pnlgrpdefn
		[pnlgrpname market]
		(fn [rs]
			(def rs-seq (jdbc/result-set-seq rs))
			(assert (= (count rs-seq) 1))
			(select-keys (first rs-seq) 
					[:actions
					 :addsrchrecname
					 :dfltaction
					 :searchrecname
					 :primaryaction]))))

	(def pages (stmtlib/query-pspnlgroup
		[pnlgrpname market]
		(fn [rs]
			(def rs-seq (jdbc/result-set-seq rs))
			(assert (> (count rs-seq) 0))
			(vec (map :pnlname rs-seq))
	)))

	(into initial-rec {:pnlgrpname pnlgrpname :_pages pages})
)
