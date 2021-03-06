# clj-ftpserver

Wrapper over Apache FtpServer


## Example

From functional code:

```
(require '[clj-ftpserver.core :as ftp])
(let [server (ftp/ftp-server [{:username "bob" :password "bobisgreat" :home-dir "/bob/ftp"}]
                              {:port 5555 :ssl true :implicit-ssl true})]
  (with-ftp-server
    server
    (do stuff)))
```

From REPL:

```
(require '[clj-ftpserver.core :as ftp])
(def myserver (ftp/ftp-server [{:username "bob" :password "bobisgreat" :home-dir "/bob/ftp" :write true}]
                              {:port 5555 :ssl true}))
(ftp/start myserver)
;play around with the server with filezilla or something
(ftp/stop myserver)
```

## Definitions

The ftp-server function accepts two arguments:

users: a vector of user maps

user: a map containing :username, :password, :home-dir, and :write (optional) key-value pairs

server-config: a map containing :port, :ssl (optional), and :implicit-ssl (optional) key-value pairs
