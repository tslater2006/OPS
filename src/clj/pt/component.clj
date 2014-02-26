(ns pt.component
	(require [clojure.java.jdbc :as jdbc])
	(:use [sql.stmtlib :as stmtlib])
	(:use [runtime.log :only [get-log INFO DEBUG]])
	(:gen-class))

(def ^{:private true} log (get-log *ns*))

(defn init
	[pnlgrpname market]
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

	(def pages (stmtlib/query-pnlgroup
		[pnlgrpname market]
		(fn [rs]
			(def rs-seq (jdbc/result-set-seq rs))
			(assert (> (count rs-seq) 0))
			(vec (map :pnlname rs-seq))
	)))

	(into initial-rec {:pnlgrpname pnlgrpname :_pages pages})
)

(defn load-programs
	[{:keys [pnlgrpname]}]
	(INFO log "Loading programs for Component:{}", (object-array [pnlgrpname]))
	;; TODO: Create skeleton type hierarchy for PeopleCodeProg,
	;; which requires init() to be filled out on the parent
	;; and ComponentPeopleCodeProg to derive from it.
	;; TODO: Think about ways to make caching cleaner and to replace the
	;; use of guards that are preventing various functions from executing
	;; more than once.
)
