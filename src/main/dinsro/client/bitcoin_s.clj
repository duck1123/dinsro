(ns dinsro.client.bitcoin-s
  (:require
   [clojure.core.async :as async]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   dinsro.client.converters
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   akka.actor.ActorSystem
   java.net.URI
   org.bitcoins.commons.jsonmodels.bitcoind.GetRawTransactionResult
   org.bitcoins.commons.jsonmodels.bitcoind.RpcOpts$AddNodeArgument$Add$
   org.bitcoins.core.hd.BIP32Path
   org.bitcoins.core.hd.SegWitHDPath
   org.bitcoins.core.hd.HDPurpose
   org.bitcoins.core.hd.SegWitHDPath
   org.bitcoins.crypto.ECPrivateKeyBytes
   org.bitcoins.core.crypto.ECPrivateKeyUtil
   org.bitcoins.core.crypto.MnemonicCode
   org.bitcoins.core.crypto.BIP39Seed
   org.bitcoins.core.crypto.ExtPrivateKey
   org.bitcoins.core.crypto.ExtPublicKey
   org.bitcoins.core.crypto.ExtKeyPrivVersion
   org.bitcoins.core.crypto.ExtKeyPubVersion
   org.bitcoins.core.protocol.Bech32Address
   org.bitcoins.core.protocol.BitcoinAddress
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
  ^ExtKeyPubVersion
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
   network]
  (.value (Bech32Address/apply script-pub-key (BitcoinNetworks/fromString network))))

(defn get-ext-pubkey
  [xpriv account-path]
  (.extPublicKey (.deriveChildPrivKey xpriv account-path)))

(defn parse-ext-priv-key
  [key]
  (ExtPrivateKey/fromString key))

(defn get-child-key
  [xpriv wallet-path child-path]
  (log/info :get-child-key/starting {:xpriv xpriv :wallet-path wallet-path :child-path child-path})
  (let [account-path (BIP32Path/fromString wallet-path)
        account-xpub (get-ext-pubkey xpriv account-path)
        segwit-path  (SegWitHDPath/fromString child-path)
        path-diff    (.get (.diff account-path segwit-path))
        ext-pub-key  (.get (.deriveChildPubKey account-xpub path-diff))]
    (log/info :get-child-key/finished {:ext-public-key ext-pub-key})
    ext-pub-key))

(defn get-child-key-pub
  [account-xpub wallet-path child-path]
  (log/info :get-child-key/starting {:account-xpub account-xpub
                                     :wallet-path  wallet-path
                                     :child-path   child-path})
  (let [account-path (BIP32Path/fromString wallet-path)
        segwit-path  (BIP32Path/fromString child-path)
        path-diff    (.get (.diff account-path segwit-path))
        ext-pub-key  (.get (.deriveChildPubKey account-xpub path-diff))]
    (log/info :get-child-key/finished {:ext-public-key ext-pub-key})
    ext-pub-key))

(defn get-script-pub-key
  "https://bitcoin-s.org/api/org/bitcoins/core/protocol/script/P2WPKHWitnessSPKV0$.html#apply(pubKey:org.bitcoins.crypto.ECPublicKey):org.bitcoins.core.protocol.script.P2WPKHWitnessSPKV0"
  [^ExtPublicKey ext-pub-key]
  (let [pub-key (.key ext-pub-key)]
    (P2WPKHWitnessSPKV0/apply pub-key)))

(defn get-xpriv
  "https://bitcoin-s.org/api/org/bitcoins/core/crypto/BIP39Seed.html#toExtPrivateKey(keyVersion:org.bitcoins.core.crypto.ExtKeyPrivVersion):org.bitcoins.core.crypto.ExtPrivateKey"
  ^ExtPrivateKey [^BIP39Seed bip39-seed purpose network]
  (let [priv-version (get-xpriv-version purpose network)]
    (.toExtPrivateKey bip39-seed priv-version)))

(defn ->wif
  "https://bitcoin-s.org/api/org/bitcoins/core/crypto/ECPrivateKeyUtil$.html#toWIF(privKey:org.bitcoins.crypto.ECPrivateKeyBytes,network:org.bitcoins.core.config.NetworkParameters):String"
  [key]
  (let [pk-bv (.bytes key)
        pk-bytes (ECPrivateKeyBytes. pk-bv false)
        network (regtest-network)]
    (ECPrivateKeyUtil/toWIF pk-bytes network)))

(defn wif->pk
  "https://bitcoin-s.org/api/org/bitcoins/core/crypto/ECPrivateKeyUtil$.html#fromWIFToPrivateKey(WIF:String):org.bitcoins.crypto.ECPrivateKeyBytes"
  ^ECPrivateKeyBytes [wif]
  (ECPrivateKeyUtil/fromWIFToPrivateKey wif))

(defn get-zmq-config
  "https://bitcoin-s.org/api/org/bitcoins/rpc/config/ZmqConfig$.html#empty:org.bitcoins.rpc.config.ZmqConfig"
  []
  (ZmqConfig/empty))

(defn ->bitcoin-address
  [address-s]
  (BitcoinAddress/apply address-s))

(defn generate-to-address!
  "https://bitcoin-s.org/api/org/bitcoins/rpc/client/v22/BitcoindV22RpcClient.html#generateToAddress(blocks:Int,address:org.bitcoins.core.protocol.BitcoinAddress,maxTries:Int):scala.concurrent.Future[Vector[org.bitcoins.crypto.DoubleSha256DigestBE]]"
  [client address-s]
  (log/info :generate-to-address!/starting {:address-s address-s})
  (let [blocks    (int 1)
        address   (->bitcoin-address address-s)
        max-tries (int 1000000)
        fut       (.generateToAddress client blocks address max-tries)
        response  (async/<!! (cs/await-future fut))]
    (log/info :generate-to-address!/response {:response response})
    (let [{:keys [passed result]} response]
      (if passed
        (do
          (log/info :generate-to-address!/passed {:result result})
          (let [parsed-response (map cs/->record (cs/vector->vec result))]
            (log/info :generate-to-address!/parsed {:parsed-response parsed-response})
            parsed-response))
        (do
          (log/info :generate-to-address!/not-passed {:result result})
          (let [result-obj (.get result)
                ex         (or result-obj (RuntimeException. "did not pass"))]
            (throw ex)))))))

(defn get-peer-info
  [client]
  (let [fut      (.getPeerInfo client)
        response (async/<!! (cs/await-future fut))]
    (log/finer :get-peer-info/response {:response response})
    (let [{:keys [passed result]} response]
      (if passed
        (let [parsed-response (map cs/->record (cs/vector->vec result))]
          (log/finer :get-peer-info/parsed-response {:parsed-response parsed-response})
          parsed-response)
        (let [result-obj (.get result)
              ex         (or result-obj (RuntimeException. "Did not pass"))]
          (throw ex))))))

(defn ->segwit-path
  [path]
  (SegWitHDPath/fromString path))

(defn ->bip32-path
  [path]
  (BIP32Path/fromString path))

;; https://bitcoin-s.org/api/org/bitcoins/rpc/client/v22/BitcoindV22RpcClient.html#getBlockChainInfo:scala.concurrent.Future[org.bitcoins.commons.jsonmodels.bitcoind.GetBlockChainInfoResult]

(defn get-blockchain-info-raw
  [client]
  (log/finer :get-blockchain-info-raw/starting {:client client})
  (let [fut (.getBlockChainInfo client)
        response (async/<!! (cs/await-future fut))]
    (log/info :get-blockchain-info-raw/response {:response response})
    (let [{:keys [passed result]} response]
      (if passed
        result
        (let [result-obj (.get result)
              ex (or result-obj (RuntimeException. "Did not pass"))]
          (throw ex))))))

(defn get-blockchain-info
  [client]
  (cs/->record (get-blockchain-info-raw client)))

(>defn list-transactions-raw
  [client]
  [::client => any?]
  (log/info :list-transactions-raw/starting {})
  (let [account             "*"
        count               (int 10)
        skip                (int 0)
        include-watch-only? false
        response (async/<!! (cs/await-future (.listTransactions client account count skip include-watch-only?)))]
    (log/finer :list-transactions-raw/finished {:response response})
    response))

(>defn list-transactions
  [client]
  [::client => any?]
  (let [obj (list-transactions-raw client)]
    (cs/->record obj)))

(defn get-block-hash
  [client height]
  (cs/await-future (.getBlockHash client height)))

(>defn fetch-block-by-height
  [client height]
  [::client number? => any?]
  (log/finer :fetch-block-by-height/starting {:height height})
  (let [hash (:result (async/<!! (get-block-hash client height)))]
    (log/fine :fetch-block-by-height/located {:hash hash})
    (let [response (:result (async/<!! (cs/await-future (.getBlock client hash))))]
      (log/finer :fetch-block-by-height/read {:response response})
      (let [converted-response (cs/->record response)]
        (log/finer :fetch-block-by-height/converted {:converted-response converted-response})
        converted-response))))

(>defn add-node
  "https://bitcoin-s.org/api/org/bitcoins/rpc/client/v22/BitcoindV22RpcClient.html#addNode(address:java.net.URI,command:org.bitcoins.commons.jsonmodels.bitcoind.RpcOpts.AddNodeArgument):scala.concurrent.Future[Unit]"
  [client address-s]
  [::client string? => any?]
  (log/info :add-node/starting {:address-s address-s})
  (let [address  (URI. address-s)
        command  (RpcOpts$AddNodeArgument$Add$.)
        p        (.addNode client address command)]
    (log/finer :add-node/sent {:p p :address address :command command})
    (let [ch (cs/await-future p)]
      (log/finer :add-node/awaited {:ch ch})
      (let [response (async/<!! ch)]
        (log/finer :add-node/finished {:response response})
        response))))

(>defn disconnect-node
  [client addr]
  [::client string? => any?]
  (log/info :disconnect-node/starting {:addr addr})
  (comment client))

(>defn get-raw-transaction-raw
  "https://bitcoin-s.org/api/org/bitcoins/rpc/client/v22/BitcoindV22RpcClient.html#addNode(address:java.net.URI,command:org.bitcoins.commons.jsonmodels.bitcoind.RpcOpts.AddNodeArgument):scala.concurrent.Future[Unit]"
  [client tx-id-s]
  [::client string? => (ds/instance? GetRawTransactionResult)]
  (log/info :get-raw-transaction/starting {:tx-id-s tx-id-s})
  (let [tx-id                   (cs/double-sha256-digest-be tx-id-s)
        block-hash              (cs/none)
        p                       (.getRawTransaction client tx-id block-hash)
        ch                      (cs/await-future p)
        response                (async/<!! ch)
        {:keys [passed result]} response]
    (if passed result (throw result))))

(>defn get-raw-transaction
  "https://bitcoin-s.org/api/org/bitcoins/rpc/client/v22/BitcoindV22RpcClient.html#addNode(address:java.net.URI,command:org.bitcoins.commons.jsonmodels.bitcoind.RpcOpts.AddNodeArgument):scala.concurrent.Future[Unit]"
  [client tx-id-s]
  [::client string? => any?]
  (let [record (cs/->record (get-raw-transaction-raw client tx-id-s))]
    (log/finer :get-raw-transaction/finished {:record record})
    record))
