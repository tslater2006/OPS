(ns main
	(require [pt.component :as component])
	(require [pt.menu :as menu])
	(:use [runtime.log :only [get-log INFO DEBUG]])
	(:gen-class))

(def ^{:private true} log (get-log *ns*))

(defn -main [& args]
	(INFO log "main starting...")
	(def inited-comp (component/init "SSS_STUDENT_CENTER" "GBL"))
	(DEBUG log (str inited-comp))
	(DEBUG log (str (menu/init "SA_LEARNER_SERVICES")))
	(DEBUG log (str (component/load-programs inited-comp)))
	(INFO log "-main exiting.")
)
