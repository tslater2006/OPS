(ns runtime.log
	(import org.apache.logging.log4j.Logger)
	(import org.apache.logging.log4j.LogManager)
	(:gen-class))

(defn get-log
	[ns]
	(. LogManager getLogger (str *ns*))
)

(defn INFO
	([log msg] (INFO log msg (into-array [])))
	([log msg placeholder-array]
		(. log info msg placeholder-array))
)

(defn DEBUG
	([log msg] (DEBUG log msg (into-array [])))
	([log msg placeholder-array]
		(. log debug msg placeholder-array))
)
