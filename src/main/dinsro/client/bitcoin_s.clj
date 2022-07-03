(ns dinsro.client.bitcoin-s
  (:require
   [clojure.core.async :as async]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.converters :as c.converters]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   akka.actor.ActorSystem
   org.bitcoins.core.hd.BIP32Path
   org.bitcoins.core.hd.SegWitHDPath
   org.bitcoins.core.hd.HDPurpose
   org.bitcoins.core.hd.SegWitHDPath
   org.bitcoins.crypto.ECPrivateKeyBytes
   org.bitcoins.core.crypto.ECPrivateKeyUtil
   org.bitcoins.core.crypto.MnemonicCode
   org.bitcoins.core.crypto.BIP39Seed
   org.bitcoins.core.crypto.ExtPrivateKey
   org.bitcoins.core.crypto.ExtKeyPrivVersion
   org.bitcoins.core.config.NetworkParameters
   org.bitcoins.core.protocol.Bech32Address
   org.bitcoins.core.protocol.script.P2WPKHWitnessSPKV0
   org.bitcoins.core.protocol.script.WitnessScriptPubKey
   org.bitcoins.core.config.BitcoinNetworks
   org.bitcoins.core.util.HDUtil
   org.bitcoins.rpc.client.v22.BitcoindV22RpcClient
   org.bitcoins.rpc.config.BitcoindAuthCredentials$PasswordBased
   org.bitcoins.rpc.config.BitcoindInstanceRemote
   org.bitcoins.rpc.config.ZmqConfig
   scala.Option
   scodec.bits.BitVector))

(>def ::client (ds/instance? BitcoindV22RpcClient))

(defn get-auth-credentials
  ^BitcoindAuthCredentials$PasswordBased [^String rpcuser ^String rpcpass]
  (BitcoindAuthCredentials$PasswordBased. rpcuser rpcpass))

(defn get-entropy
  "Generate 256 bits of entropy"
  ^BitVector []
  (MnemonicCode/getEntropy256Bits))

(defn create-mnemonic
  "Create a Mnemonic code"
  (^MnemonicCode []
   (let [entropy (get-entropy)]
     (create-mnemonic entropy)))
  (^MnemonicCode [entropy]
   (log/info :mnemonic/create {:entropy entropy})
   (MnemonicCode/fromEntropy entropy)))

(defn create-mnemonic-words
  ([]
   (create-mnemonic-words (create-mnemonic)))
  ([mnemonic]
   (cs/vector->vec (.words mnemonic))))

(defn words->mnemonic
  [words]
  (let [word-vector (cs/create-vector words)]
    (MnemonicCode/fromWords word-vector)))

(defn get-words
  [^MnemonicCode mc]
  (cs/vector->vec (.words mc)))

(defn create-seed
  [passphrase]
  (let [mc (create-mnemonic)]
    (BIP39Seed/fromMnemonic mc passphrase)))

(defn regtest-network
  "A reference to the regtest network"
  []
  (BitcoinNetworks/fromString "regtest"))

(defn get-xpub-version
  [purpose network]
  (HDUtil/getXpubVersion
   (HDPurpose. purpose)
   (BitcoinNetworks/fromString network)))

(defn get-xpriv-version
  "See: https://bitcoin-s.org/api/org/bitcoins/core/util/HDUtil$.html#getXprivVersion(hdPurpose:org.bitcoins.core.hd.HDPurpose,network:org.bitcoins.core.config.NetworkParameters):org.bitcoins.core.crypto.ExtKeyPrivVersion"
  ^ExtKeyPrivVersion
  [purpose network-name]
  (let [hd-purpose (HDPurpose. purpose)
        network    (BitcoinNetworks/fromString network-name)]
    (HDUtil/getXprivVersion hd-purpose network)))

(defn get-remote-instance
  [network remote-uri rpc-uri auth-credentials zmq-config]
  (BitcoindInstanceRemote/apply
   network
   remote-uri
   rpc-uri
   auth-credentials
   zmq-config
   (Option/empty)
   (ActorSystem/apply)))

(defn get-client
  ^BitcoindV22RpcClient [remote-instance]
  (BitcoindV22RpcClient/apply remote-instance))

(defn get-address
  [^WitnessScriptPubKey script-pub-key
   ^NetworkParameters network]
  (.value (Bech32Address/apply script-pub-key (BitcoinNetworks/fromString network))))

(defn get-ext-pubkey
  [xpriv account-path]
  (.extPublicKey (.deriveChildPrivKey xpriv account-path)))

(defn parse-ext-priv-key
  [key]
  (ExtPrivateKey/fromString key))

(defn get-child-key
  [xpriv wallet-path child-path]
  (let [account-path (BIP32Path/fromString wallet-path)
        account-xpub (get-ext-pubkey xpriv account-path)
        segwit-path  (SegWitHDPath/fromString child-path)
        path-diff    (.get (.diff account-path segwit-path))
        ext-pub-key  (.get (.deriveChildPubKey account-xpub path-diff))]
    ext-pub-key))

(defn get-script-pub-key
  [ext-pub-key]
  (let [pub-key (.key ext-pub-key)]
    (P2WPKHWitnessSPKV0/apply pub-key)))

(defn get-xpriv
  ^ExtPrivateKey [^BIP39Seed bip39-seed purpose network]
  (let [priv-version (get-xpriv-version purpose network)]
    (.toExtPrivateKey bip39-seed priv-version)))

(defn ->wif
  [key]
  (let [pk-bv (.bytes key)
        pk-bytes (ECPrivateKeyBytes. pk-bv false)
        network (regtest-network)]
    (ECPrivateKeyUtil/toWIF pk-bytes network)))

(defn wif->pk
  ^ECPrivateKeyBytes [wif]
  (ECPrivateKeyUtil/fromWIFToPrivateKey wif))

(defn get-zmq-config
  []
  (ZmqConfig/empty))

(defn generate-to-address!
  [client address]
  (log/info :generate-to-address!/starting {:address address})
  (let [response (.generateToAddress client address)]
    (log/info :generate-to-address!/finished {:resonse response})
    response))

(defn get-peer-info
  [client]
  (let [fut      (.getPeerInfo client)
        response (async/<!! (cs/await-future fut))]
    (log/info :get-peer-info/response {:response response})
    (let [{:keys [passed result]} response]
      (if passed
        (cs/vector->vec result)
        (throw "Did not pass")))))

(defn ->segwit-path
  [path]
  (SegWitHDPath/fromString path))

(defn ->bip32-path
  [path]
  (BIP32Path/fromString path))

;; https://bitcoin-s.org/api/org/bitcoins/rpc/client/v22/BitcoindV22RpcClient.html#getBlockChainInfo:scala.concurrent.Future[org.bitcoins.commons.jsonmodels.bitcoind.GetBlockChainInfoResult]

(defn get-blockchain-info-raw
  [client]
  (log/info :get-blockchain-info-raw/starting {:client client})
  (let [fut (.getBlockChainInfo client)
        response (async/<!! (cs/await-future fut))]
    (log/info :get-blockchain-info-raw/response {:response response})
    (let [{:keys [passed result]} response]
      (if passed
        result
        (throw "Did not pass")))))

(defn get-blockchain-info
  [client]
  (cs/->record (get-blockchain-info-raw client)))

(>defn list-transactions-raw
  [client]
  [::client => any?]
  (log/info :list-transactions/starting {:client client})
  (let [account             "*"
        count               (int 10)
        skip                (int 0)
        include-watch-only? false]
    (.listTransactions client account count skip include-watch-only?)))

(defn list-transactions
  [client]
  (cs/->record (list-transactions-raw client)))

(defn get-block-hash
  [client height]
  (cs/await-future (.getBlockHash client height)))

(>defn fetch-block-by-height
  [client height]
  [::client number? => ::c.converters/fetch-block-by-height-result]
  (log/finer :fetch-block-by-height/starting {:height height :client client})
  (let [hash (:result (async/<!! (get-block-hash client height)))]
    (log/fine :fetch-block-by-height/located {:hash hash})
    (let [response (:result (async/<!! (cs/await-future (.getBlock client hash))))]
      (log/finer :fetch-block-by-height/read {:response response})
      (let [converted-response (cs/->record response)]
        (log/info :fetch-block-by-height/converted {:converted-response converted-response})
        converted-response))))

(defn add-node
  [client address]
  (log/info :add-node/starting {:client client :address address}))

(defn disconnect-node
  [client addr]
  (log/info :disconnect-node/starting {:client client :addr addr}))

(defn get-raw-transaction
  [client tx-id]
  (log/info :get-raw-transaction/starting {:client client :tx-id tx-id})
  (.getRawTransaction client tx-id))
