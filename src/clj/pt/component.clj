(ns pt.component
  (import org.apache.logging.log4j.Logger)
  (import org.apache.logging.log4j.LogManager)
	(import java.sql.DriverManager)
	(:gen-class))

(def log (. LogManager getLogger "pt.component"))

(defn load-defn [pnlgrpname market]
	(. log info "Loading Component:{}.{}" (object-array [pnlgrpname market]))
)

/**
 * TODO:
 *   - Wrap db functions in another namespace (look at reify for
 *     making resultsets into iterable sequences?)
 *   - Wrap log functions in another namespace.
 */
(defn ora-test []
;;	(jdbc/query oracle ["select * from PS_INSTALLATION"] )
	(def conn	(. DriverManager getConnection
				"jdbc:oracle:thin:@//10.0.0.88:1521/XENCSDEV" "SYSADM" "SYSADM"))
	(def pstmt (. conn prepareStatement
				"SELECT * FROM PSPNLGRPDEFN"))
	(def rs (. pstmt executeQuery))
	(. rs next)
	(def temp (. rs getString "SEARCHRECNAME"))
	(. log info "Search record name: {}" (object-array [temp]))
)
