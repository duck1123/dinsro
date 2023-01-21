^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.client.bitcoin-s-notebook
  (:require
   ;; [clojure.core.async :as async]
   [dinsro.actions.core.node-base :as a.c.node-base]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.client.scala :as cs]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk])
  (:import
   org.bitcoins.core.crypto.ExtKeyVersion$SegWitMainNetPriv$
   org.bitcoins.core.crypto.ExtPrivateKey
   org.bitcoins.crypto.ECPrivateKey
   org.bitcoins.rpc.config.BitcoindInstanceRemote
   scodec.bits.ByteVector))

;; # Scala Bitcoin Client

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(def client
  (-> "bitcoin-alice"
      q.c.nodes/find-by-name
      q.c.nodes/read-record
      a.c.node-base/get-client))

;; ## get entropy

(c.bitcoin-s/get-entropy)

;; ## create mnemonic words

(c.bitcoin-s/create-mnemonic-words)

;; ## Networks

;; ### regtest

(c.bitcoin-s/regtest-network)

;; ## get blockchain info result [link](https://bitcoin-s.org/api/org/bitcoins/rpc/client/v22/BitcoindV22RpcClient.html#getBlockChainInfo:scala.concurrent.Future[org.bitcoins.commons.jsonmodels.bitcoind.GetBlockChainInfoResult])


;; (:result (async/<!! (c.bitcoin-s/get-block-hash client 0)))

(comment
  (cs/vector->vec (.words (c.bitcoin-s/create-mnemonic)))
  (prn (c.bitcoin-s/create-mnemonic-words))

  (c.bitcoin-s/create-seed "")

  (def mn (c.bitcoin-s/create-mnemonic))
  mn
  (c.bitcoin-s/get-words mn)

  (c.bitcoin-s/regtest-network)

  (c.bitcoin-s/get-xpub-version 84 "regtest")
  (def priv-version (c.bitcoin-s/get-xpriv-version 84 "regtest"))

  (def segwit-path (c.bitcoin-s/->segwit-path "m/84'/0'/0'/0/0"))
  segwit-path

  (def passphrase "secret-passphrase")
  (def wallet-path "m/84'/0'/0'")
  (def address-path (str wallet-path "/0/0"))
  (def bip39-seed (c.bitcoin-s/create-seed passphrase))

  ;; (def extpub (get-child-key xpriv wallet-path address-path))
  ;; extpub

  (-> bip39-seed
      (c.bitcoin-s/get-xpriv 84 "regtest")
      (c.bitcoin-s/get-child-key wallet-path address-path)
      (c.bitcoin-s/get-script-pub-key)
      (c.bitcoin-s/get-address "regtest"))

  (def priv-key-s "xprv9s21ZrQH143K4LCRq4tUZUt3fiTNZr6QTiep3HGzMxtSwfxKAhBmNJJnsmoyWuYZCPC4DNsiVwToHJbxZtq4iEkozBhMzWNTiCH4tzJNjPi")
  (c.bitcoin-s/parse-ext-priv-key priv-key-s)

  (ExtPrivateKey/freshRootKey ExtKeyVersion$SegWitMainNetPriv$)

  (.fromValidHex ByteVector "70ea14ac30939a972b5a67cab952d6d7d474727b05fe7f9283abc1e505919e83")

  (def private-key (ECPrivateKey/freshPrivateKey))
  private-key

  (def public-key (.publicKey private-key))
  public-key

  (BitcoindInstanceRemote/apply
   (c.bitcoin-s/regtest-network)
   (URI. ""))

  nil)
