^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.client.converters.init-wallet-request-notebook
  (:require
   [dinsro.client.converters.init-wallet-request :as c.c.init-wallet-request]
   [dinsro.client.scala :as cs]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk])
  (:import
   com.google.protobuf.ByteString))

;; # Init Wallet Request [link](https://bitcoin-s.org/api/lnrpc/InitWalletRequest.html)

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

;; scala.collection.immutable.Seq/empty

(def wallet-password                        (ByteString/copyFromUtf8 "passphrase12345678"))
(def cipher-seed-mnemonic                   nil)
(def aezeed-passphrase                      ByteString/EMPTY)
(def recovery-window                        (int 0))
(def channel-backups                        (cs/none))
(def stateless-init                         false)
(def extended-master-key                    "")
(def extended-master-key-birthday-timestamp (int 0))

(def watch-only                             (cs/none))
(def unknown-fields                         (cs/empty-unknown-field-set))

^{::clerk/viewer clerk/code}
(def request
  (try
    (c.c.init-wallet-request/->request
     wallet-password
     cipher-seed-mnemonic
     aezeed-passphrase
     recovery-window
     channel-backups
     stateless-init
     extended-master-key
     extended-master-key-birthday-timestamp
     watch-only
     unknown-fields)
    (catch Exception ex ex)))

(c.c.init-wallet-request/->request wallet-password)
