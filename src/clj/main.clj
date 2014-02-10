(ns main
	(import org.openpplsoft.JavaClass)
	(:gen-class))

(defn -main [& args]
	(println "START In main...")
	(do
			(def jc (JavaClass.))
			(. jc saySomething)
			(println (. jc sayMore))
	)
	(println "END In main...")
	)
