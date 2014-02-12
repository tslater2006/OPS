(defproject ops "0.1.0-SNAPSHOT"
  :description "An open source alternative runtime engine for Oracle's PeopleSoft applications."
  :url "http://openpplsoft.org"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                [org.apache.logging.log4j/log4j-api "2.0-beta9"]
                [org.apache.logging.log4j/log4j-core "2.0-beta9"]]
	:source-paths	["src/clj"]
	:java-source-paths	["src/java"]
	:main main)
