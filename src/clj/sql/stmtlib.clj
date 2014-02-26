(ns sql.stmtlib
	(import com.mchange.v2.c3p0.ComboPooledDataSource)
	(:gen-class))

(def ^{:private true} jdbc-url
	"jdbc:oracle:thin:SYSADM/SYSADM@//10.0.0.88:1521/XENCSDEV")

(def pooled-conn-ds
	"Returns the pooled connection's underlying DataSource
   in the form of a map."
	(delay
		(hash-map
			:datasource
			(doto (ComboPooledDataSource.) (.setJdbcUrl jdbc-url)))))





