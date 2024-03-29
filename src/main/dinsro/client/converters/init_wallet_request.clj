(ns dinsro.client.converters.init-wallet-request
  (:require
   [dinsro.client.scala :as cs]
   [erp12.fijit.collection :as efc]
   [lambdaisland.glogc :as log])
  (:import
   lnrpc.InitWalletRequest
   com.google.protobuf.ByteString
   scala.Option))

(defn ->request
  "see: https://bitcoin-s.org/api/lnrpc/InitWalletRequest.html"
  ([]
   (->request (ByteString/copyFromUtf8 "passphrase12345678")))
  ([^ByteString wallet-password]
   (->request wallet-password (efc/scala-list)))
  ([^ByteString wallet-password cipher-seed-mnemonic]
   (->request wallet-password cipher-seed-mnemonic ByteString/EMPTY))
  ([^ByteString wallet-password cipher-seed-mnemonic ^ByteString aezeed-passphrase]
   (->request wallet-password cipher-seed-mnemonic aezeed-passphrase (int 0)))
  ([wallet-password cipher-seed-mnemonic aezeed-passphrase recovery-window]
   (->request wallet-password cipher-seed-mnemonic aezeed-passphrase recovery-window (cs/none)))
  ([wallet-password cipher-seed-mnemonic aezeed-passphrase recovery-window channel-backups]
   (->request wallet-password cipher-seed-mnemonic aezeed-passphrase recovery-window channel-backups false))
  ([wallet-password
    cipher-seed-mnemonic
    aezeed-passphrase
    recovery-window
    channel-backups
    stateless-init]
   (->request
    wallet-password
    cipher-seed-mnemonic
    aezeed-passphrase
    recovery-window
    channel-backups
    stateless-init
    ""))
  ([wallet-password
    cipher-seed-mnemonic
    aezeed-passphrase
    recovery-window
    channel-backups
    stateless-init
    extended-master-key]
   (->request
    wallet-password
    cipher-seed-mnemonic
    aezeed-passphrase
    recovery-window
    channel-backups
    stateless-init
    extended-master-key
    (cs/uint64 0)))
  ([wallet-password
    cipher-seed-mnemonic
    aezeed-passphrase
    recovery-window
    channel-backups
    stateless-init
    extended-master-key
    extended-master-key-birthday-timestamp]
   (->request
    wallet-password
    cipher-seed-mnemonic
    aezeed-passphrase
    recovery-window
    channel-backups
    stateless-init
    extended-master-key
    extended-master-key-birthday-timestamp
    (Option/empty)))
  ([wallet-password
    cipher-seed-mnemonic
    aezeed-passphrase
    recovery-window
    channel-backups
    stateless-init
    extended-master-key
    extended-master-key-birthday-timestamp
    watch-only]
   (->request
    wallet-password
    cipher-seed-mnemonic
    aezeed-passphrase
    recovery-window
    channel-backups
    stateless-init
    extended-master-key
    extended-master-key-birthday-timestamp
    watch-only
    (cs/empty-unknown-field-set)))
  ([;; ^ByteString
    wallet-password
    cipher-seed-mnemonic
    ;; ^ByteString
    aezeed-passphrase
    ;; ^Integer
    recovery-window
    ;; ^Option
    channel-backups
    ;; ^Boolean
    stateless-init
    ;; ^String
    extended-master-key
    ;; ^UInt64
    extended-master-key-birthday-timestamp
    ;; ^Option
    watch-only
    ;; ^UnknownFieldSet
    unknown-fields]
   (log/info :->request/starting
     {:wallet-password                        wallet-password
      :cipher-seed-mnemonic                   cipher-seed-mnemonic
      :aezeed-passphrase                      aezeed-passphrase
      :recovery-window                        recovery-window
      :channel-backups                        channel-backups
      :stateless-init                         stateless-init
      :extended-master-key                    extended-master-key
      :extended-master-key-birthday-timestamp extended-master-key-birthday-timestamp
      :watch-only                             watch-only
      :unknown-fields                         unknown-fields})

   (InitWalletRequest.
    wallet-password
    cipher-seed-mnemonic
    aezeed-passphrase
    recovery-window
    channel-backups
    stateless-init
    extended-master-key
    extended-master-key-birthday-timestamp
    watch-only
    unknown-fields))
  #_{:response [wallet-password cipher-seed-mnemonic aezeed-passphrase recovery-window channel-backups stateless-init extended-master-key extended-master-key-birthday-timestamp watch-only unknown-fields]})
