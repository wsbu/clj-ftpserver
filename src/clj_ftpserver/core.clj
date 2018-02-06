(ns clj-ftpserver.core
  (:import [org.apache.ftpserver FtpServer FtpServerFactory]
           [org.apache.ftpserver.listener ListenerFactory]
           [org.apache.ftpserver.ftplet UserManager Authority]
           [org.apache.ftpserver.usermanager.impl BaseUser WritePermission]
           [org.apache.ftpserver.usermanager PropertiesUserManagerFactory]
           [org.apache.ftpserver.ssl SslConfigurationFactory SslConfiguration])
  (:require [komcrad-utils.io :refer [resource-as-file]]))

(defn users
  [coll]
  "returns a vector of BaseUser objects given a collection of maps with
   :username, :password, :home-dir, and :write (optional) key-value pairs."
  (loop [users coll result []]
    (if (> (count users) 0)
      (let [user (first users) base-user (BaseUser.)]
        (.setName base-user (:username user))
        (.setPassword base-user (:password user))
        (.setHomeDirectory base-user (:home-dir user))
        (when (:write user)
          (.setAuthorities base-user (. java.util.Arrays asList (into-array [(WritePermission.)]))))
        (recur (rest users) (conj result base-user)))
      result)))

(defn ftp-server
  "returns an FtpServer object given a map
   format: (ftp-server {:port port :ssl true/false :implicit-ssl true/false
                        :users [{:username username :password password :home-dir path-to-home-dir :write true/false}]})
   OR
   (ftp-server [{:username username :password password :home-dir path-to-home-dir :write true/false}]
               {:ssl true/false :port port :implicit-ssl true/false})"
  ([config]
    (let [user-manager-factory (PropertiesUserManagerFactory.)
          user-manager (.createUserManager user-manager-factory)
          server-factory (FtpServerFactory.)
          listener-factory (ListenerFactory.)
          ssl (SslConfigurationFactory.)]
      (cond
        (not (:users config)) (throw (Exception. "Could not find :users"))
        (not (:port config)) (throw (Exception. "Could not find :port")))
      (loop [base-users (users (:users config))]
        (when (> (count base-users) 0)
          (.save user-manager (first base-users))
          (recur (rest base-users))))
      (.setPort listener-factory (:port config))
      (when (:ssl config)
        (if (:keystore config)
          (.setKeystoreFile ssl (clojure.java.io/file (:keystore config)))
          (let [key-store (resource-as-file "ftpserver.jks")]
            (.setKeystoreFile ssl key-store)
            (.deleteOnExit key-store)))
        (.setKeystorePassword ssl "password")
        (.setSslConfiguration listener-factory (.createSslConfiguration ssl)))
      (when (:implicit-ssl config)
        (.setImplicitSsl listener-factory true))
      (.addListener server-factory "default" (.createListener listener-factory))
      (.setUserManager server-factory user-manager)
      (.createServer server-factory)))
  ([users server-config]
    (ftp-server (assoc server-config :users users))))

(defn stop
  "stops server"
  [server]
  (.stop server))

(defn start
  [server]
  "start server"
  (.start server))

(defmacro with-ftp-server
  "Creates a context in which the server is running and listening.
   Ensures the server is stopped on exit"
  [server & body]
  `(let [server# ~server]
     (try (when (.isStopped server#) (.start server#))
          ~@body
          (finally (.stop server#)))))
