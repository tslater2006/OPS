(ns main
	(require [pt.component :as component])
	(require [pt.menu :as menu])
	(:use [runtime.log :only [get-log INFO DEBUG]])
	(:gen-class))

(def ^{:private true} log (get-log *ns*))

(defn -main [& args]
	(INFO log "main starting...")
	(DEBUG log (str (component/init "SSS_STUDENT_CENTER" "GBL")))
	(DEBUG log (str (menu/init "SA_LEARNER_SERVICES")))
	(INFO log "-main exiting.")
)
