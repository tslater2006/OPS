(ns main
  (import org.apache.logging.log4j.Logger)
  (import org.apache.logging.log4j.LogManager)
	(:use [pt.component :only [load-defn]]))

(def log (. LogManager getLogger "main"))

(defn -main [& args]
	(. log info "-main starting...")
	(pt.component/load-defn "SSS_STUDENT_CENTER" "GBL")
	(. log info "-main exiting.")
)
