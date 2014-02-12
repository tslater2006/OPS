(ns main
  (import org.apache.logging.log4j.Logger)
  (import org.apache.logging.log4j.LogManager))

(defn -main [& args]
  (def log (. LogManager getLogger "main"))
	(. log info "START In main...")
	(. log warn "END In main...")
)
