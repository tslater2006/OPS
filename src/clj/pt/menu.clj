(ns pt.menu
	(require [clojure.java.jdbc :as jdbc])
	(:use [sql.stmtlib :as stmtlib])
	(:use [runtime.log :only [get-log INFO DEBUG]])
	(:gen-class))

(def ^{:private true} log (get-log *ns*))

(defn init
	[menuname]
	(INFO log "Loading Menu:{}" (object-array [menuname]))

	(def initial-rec (stmtlib/query-menudefn
		[menuname]
		#(let [rs %1] {})))		;; Do nothing with record for now.

	(def items (stmtlib/query-menuitem
		[menuname]
		#(let [rs %1] []))) 	;; Do nothing with records for now.

	(into initial-rec {:menuname menuname :_items items})
)
