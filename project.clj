(defproject ops "0.1.0-SNAPSHOT"
  :description "An open source alternative runtime engine for Oracle's PeopleSoft applications."
  :url "http://openpplsoft.org"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                [org.apache.logging.log4j/log4j-core "2.0-beta9"]
                [org.apache.logging.log4j/log4j-slf4j-impl "2.0-rc1"]
                [self/ojdbc7 "7.0.0"]
                [com.mchange/c3p0 "0.9.5-pre6"]
                [org.clojure/java.jdbc "0.3.3"]]
  :repositories {"local" "file:mvn_repo"}
  :source-paths ["src/clj"]
  :java-source-paths  ["src/java"]
  :main main)

