(ns pt.component
  (import org.apache.logging.log4j.Logger)
  (import org.apache.logging.log4j.LogManager))

(def log (. LogManager getLogger "pt.component"))

(defn load-defn [pnlgrpname market]
	(. log info "Loading Component:{}.{}" (object-array [pnlgrpname market]))
)
