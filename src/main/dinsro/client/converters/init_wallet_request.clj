(ns dinsro.client.converters.init-wallet-request
  (:import
   lnrpc.InitWalletRequest
   ;; com.google.protobuf.ByteString
   ;; scala.Option
   ;; org.bitcoins.core.number.UInt64
   ;; scalapb.UnknownFieldSet
   ))

(defn ->request
  "see: https://bitcoin-s.org/api/lnrpc/InitWalletRequest.html"
  [;; ^ByteString
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
   unknown-fields)
  #_{:response [wallet-password cipher-seed-mnemonic aezeed-passphrase recovery-window channel-backups stateless-init extended-master-key extended-master-key-birthday-timestamp watch-only unknown-fields]})
