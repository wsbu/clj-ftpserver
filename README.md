# clj-ftpserver

Wrapper over Apache FtpServer


## Usage

[![Clojars Project](https://img.shields.io/clojars/v/clj-ftpserver.svg)](https://clojars.org/clj-ftpserver)
<br>
[![CircleCI](https://circleci.com/gh/komcrad/clj-ftpserver.svg?style=shield&circle-token=272e0d0d795c221f3938c380f8d915bef7eafa1d)](https://circleci.com/gh/komcrad/clj-ftpserver)

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
