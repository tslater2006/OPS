(ns main
  (import org.apache.logging.log4j.Logger)
  (import org.apache.logging.log4j.LogManager)
	(:use [pt.component :only [load-defn ora-test]])
	(:use [runtime.log :only [get-log INFO]])
	(:gen-class))

(def ^{:private true} log (get-log *ns*))

(defn -main [& args]
	(INFO log "main starting...")
	(pt.component/load-defn "SSS_STUDENT_CENTER" "GBL")
	(pt.component/ora-test)
	(INFO log "-main exiting.")
)
