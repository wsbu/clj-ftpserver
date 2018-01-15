(ns clj-ftpserver.core-test
  (:require [clojure.test :refer :all]
            [clj-ftpserver.core :refer :all]
            [miner.ftp :as ftp]
            [komcrad-utils.io :as io]))

(deftest users-test
  (testing "users"
    (let [test-users (users [{:username "bob" :password "password1" :home-dir "/tmp/bob/"}
                             {:username "billy" :password "password2" :home-dir "/tmp/billy" :write true}])]
      (let [user (first test-users)]
        (is "bob" (.getName user))
        (is "password1" (.getPassword user))
        (is "/tmp/bob/" (.getHomeDirectory user)))
      (let [user (second test-users)]
        (is "billy" (.getName user))
        (is "password2" (.getPassword user))
        (is "/tmp/billy" (.getHomeDirectory user))
        (is (= org.apache.ftpserver.usermanager.impl.WritePermission
               (type (first (.getAuthorities user)))))))))

(deftest ftp-server-test
  (testing "ftp-server")
  (let [home-dir (io/tmp-folder)
        server (ftp-server [{:username "bob" :password "password" :home-dir (.getPath home-dir)}] {:port 5555})]
    (io/with-tmp-file home-dir
      (spit (str (.getPath home-dir) "/foo.txt") "I'm foo.txt")
      (with-ftp-server server
        (ftp/with-ftp [client "ftp://bob:password@127.0.0.1:5555/"]
          (ftp/client-get client "foo.txt" (str (.getPath home-dir) "/foo2.txt"))
          (is (= "I'm foo.txt" (slurp (str (.getPath home-dir) "/foo2.txt"))))))))
  (let [home-dir (io/tmp-folder)
        server (ftp-server [{:username "bob" :password "password" :home-dir (.getPath home-dir)}]
                           {:port 5555 :ssl true})]
    (io/with-tmp-file home-dir
      (spit (str (.getPath home-dir) "/foo.txt") "I'm foo.txt")
      (with-ftp-server server
        (ftp/with-ftp [client "ftps://bob:password@127.0.0.1:5555/"]
          (ftp/client-get client "foo.txt" (str (.getPath home-dir) "/foo2.txt"))
          (is (= "I'm foo.txt" (slurp (str (.getPath home-dir) "/foo2.txt"))))))))

  (let [home-dir (io/tmp-folder)
        server (ftp-server [{:username "bob" :password "password" :home-dir (.getPath home-dir)}]
                           {:port 5555})]
    (io/with-tmp-file home-dir
      (spit (str (.getPath home-dir) "/foo.txt") "I'm foo.txt")
      (with-ftp-server server
        (is (thrown? javax.net.ssl.SSLException
                     (ftp/with-ftp [client "ftps://bob:password@127.0.0.1:5555/"]
                       (ftp/client-get client "foo.txt" (str (.getPath home-dir) "/foo2.txt")))))))))
