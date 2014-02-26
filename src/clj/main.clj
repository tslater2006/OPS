(ns main
	(:use [runtime.log :only [get-log INFO DEBUG]])
	(:use [pt.component :only [init-component]])
	(:gen-class))

(def ^{:private true} log (get-log *ns*))

(defn -main [& args]
	(INFO log "main starting...")
	(DEBUG log (str (pt.component/init-component "SSS_STUDENT_CENTER" "GBL")))
	(INFO log "-main exiting.")
)
