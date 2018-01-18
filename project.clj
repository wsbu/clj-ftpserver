(defproject clj-ftpserver "0.1.1-SNAPSHOT"
  :description "A wrapper around the Apache FtpServer library"
  :url "https://github.com/komcrad/clj-ftpserver"
  :license {:name "GNU Lesser Public License"
            :url "https://www.gnu.org/licenses/lgpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.apache.ftpserver/ftpserver-core "1.1.1"]
                 [komcrad-utils "0.1.5-SNAPSHOT"]]
  :profiles {:dev {:dependencies [[com.velisco/clj-ftp "0.3.9"]]}})
