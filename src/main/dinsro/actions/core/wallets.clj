(ns dinsro.actions.core.wallets
  (:require
   [com.fulcrologic.guardrails.core :refer [=> >defn]]
   [dinsro.actions.authentication :as a.authentication]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.queries.core.words :as q.c.words]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   [org.bitcoins.core.config BitcoinNetworks]
   org.bitcoins.core.crypto.BIP39Seed
   org.bitcoins.core.crypto.ExtPrivateKey
   org.bitcoins.core.crypto.ExtPublicKey
   org.bitcoins.core.crypto.MnemonicCode
   [org.bitcoins.core.hd BIP32Path]
   org.bitcoins.core.protocol.Bech32Address
   org.bitcoins.core.protocol.script.P2WPKHWitnessSPKV0
   [org.bitcoins.crypto ECPublicKey]))

;; FIXME: Use a proper parser
(defn parse-descriptor
  "parse a descriptor using regex"
  [descriptor]
  (let [pattern #"(?<type>.*)\(\[(?<fingerprint>.*?)/(?<derivation>.*)\](?<xpub>.*?)/(?<keypath>.*)\)#(?<checksum>.*)"
        matcher (re-matcher pattern  descriptor)]
    (when (.matches matcher)
      {:type        (.group matcher "type")
       :fingerprint (.group matcher "fingerprint")
       :derivation  (.group matcher "derivation")
       :xpub        (.group matcher "xpub")
       :keypath     (.group matcher "keypath")
       :checksum    (.group matcher "checksum")})))

(defn create!
  [{::m.c.wallets/keys    [name user]
    {node ::m.c.nodes/id} ::m.c.wallets/node}]
  (let [props {::m.c.wallets/name name
               ::m.c.wallets/user user
               ::m.c.wallets/node node}]
    (log/finer :wallet/create {:props props})
    (q.c.wallets/create-record props)))

(defn get-word-list
  [wallet-id]
  (let [ids (q.c.words/find-by-wallet wallet-id)]
    (log/finer :get-word-list/ids-read {:ids ids})
    (->> ids
         (map q.c.words/read-record)
         (sort-by ::m.c.words/position)
         (mapv ::m.c.words/word))))

(>defn calculate-derivation
  "get a mnemonic code from a wallet"
  [wallet]
  [::m.c.wallets/item => (ds/instance? MnemonicCode)]
  (log/info :calculate-derivation/starting {:wallet wallet})
  (let [{::m.c.wallets/keys [id]} wallet
        words                     (get-word-list id)]
    (c.bitcoin-s/words->mnemonic words)))

(defn ->bip39-seed
  ^BIP39Seed [wallet]
  (let [^MnemonicCode code (calculate-derivation wallet)
        password           (BIP39Seed/EMPTY_PASSWORD)]
    (BIP39Seed/fromMnemonic code password)))

(defn ->priv-key
  ^ExtPrivateKey [wallet]
  (let [seed (->bip39-seed wallet)]
    (c.bitcoin-s/get-xpriv seed 84 "regtest")))

(defn update-words!
  [wallet-id words]
  (log/info :update-words!/starting {:wallet-id wallet-id :words words})
  (let [old-ids (q.c.words/find-by-wallet wallet-id)]
    (doseq [id old-ids]
      (q.c.words/delete! id))
    (let [response (doall
                    (map-indexed
                     (fn [i word]
                       (let [props   {::m.c.words/word     word
                                      ::m.c.words/position (inc i)
                                      ::m.c.words/wallet   wallet-id}
                             word-id (q.c.words/create-record props)]
                         (q.c.words/read-record word-id)))
                     words))]
      (log/info :update-words!/finished {:response response})
      response)))

(defn get-mnemonic
  ^MnemonicCode [wallet-id]
  (log/finer :get-mnemonic/starting {:wallet-id wallet-id})
  (let [word-list (get-word-list wallet-id)]
    (c.bitcoin-s/words->mnemonic word-list)))

;; https://bitcoin-s.org/api/org/bitcoins/core/crypto/ExtPrivateKey.html

(defn mnemonic->seed
  (^BIP39Seed [^MnemonicCode mnemonic]
   (mnemonic->seed mnemonic (BIP39Seed/EMPTY_PASSWORD)))
  (^BIP39Seed [^MnemonicCode mnemonic ^String password]
   (BIP39Seed/fromMnemonic mnemonic password)))

(defn get-xpriv
  "Calculate the xpriv for a wallet from its mnemonic words"
  ^ExtPrivateKey [wallet-id]
  (log/finer :get-xpriv/starting {:wallet-id wallet-id})
  (let [;; ^MnemonicCode
        mnemonic     ^MnemonicCode (get-mnemonic wallet-id)
        network-name "testnet"
        bip39-seed   ^BIP39Seed (mnemonic->seed mnemonic)
        purpose      84]
    (c.bitcoin-s/get-xpriv bip39-seed purpose network-name)))

(defn get-wif
  "Get the wallet's private key wif formatted"
  ^String [wallet-id]
  (let [xpriv (get-xpriv wallet-id)
        key   (.key xpriv)]
    (c.bitcoin-s/->wif key)))

(defn roll!
  "Update wallet with a new set of words"
  [props]
  (log/info :roll/started {:props props})
  (let [wallet-id (::m.c.wallets/id props)
        words     (c.bitcoin-s/create-mnemonic-words)
        response  (update-words! wallet-id words)
        wif       (get-wif wallet-id)
        wallet    (q.c.wallets/read-record wallet-id)
        props     {::m.c.wallets/key wif}]
    (q.c.wallets/update! wallet-id props)
    (log/info :roll/finished {:response response})
    (merge wallet {::m.c.wallets/words response})))

(>defn get-ext-pub-key
  "Calculate the pubkey for an address for the wallet at the given index"
  ^ExtPublicKey [wallet index]
  [::m.c.wallets/item number? => (ds/instance? ExtPublicKey)]
  (log/finer :get-ext-pub-key/starting {:wallet wallet :index index})
  (let [wallet-id          (::m.c.wallets/id wallet)
        xpriv              ^ExtPrivateKey (get-xpriv wallet-id)
        prefix             "m/84'/1'/0'"
        account-path       (c.bitcoin-s/->bip32-path prefix)
        first-address-path ^BIP32Path (c.bitcoin-s/->segwit-path (str prefix "/0/" index))
        ;; Option[BIP32Path]
        diffsome           (.diff account-path first-address-path)
        child-priv-key     ^ExtPrivateKey (.deriveChildPrivKey xpriv account-path)
        account-xpub       ^ExtPublicKey (.extPublicKey child-priv-key)
        diff-path          ^BIP32Path  (.get diffsome)
        ;; Try[ExtPublicKey]
        child-public-key   (.deriveChildPubKey account-xpub diff-path)
        ext-pub-key        ^ExtPublicKey (.get child-public-key)]
    ext-pub-key))

(>defn get-address
  "Get the address at the given index for the wallet"
  [wallet index]
  [::m.c.wallets/item number? => string?]
  (let [wallet-id (::m.c.wallets/id wallet)]
    (log/finer :get-address/starting {:wallet-id wallet-id :index index})
    (let [ext-pub-key    ^ExtPublicKey (get-ext-pub-key wallet index)
          pubkey         ^ECPublicKey (.key ext-pub-key)
          script-pub-key ^P2WPKHWitnessSPKV0 (P2WPKHWitnessSPKV0/apply pubkey)
          network        ^BitcoinNetworks (c.bitcoin-s/regtest-network)
          address        ^Bech32Address (Bech32Address/apply script-pub-key network)]
      (.value address))))

(defn do-delete!
  [props]
  (log/info :do-delete!/starting {:props props}))

(defn do-derive!
  [env props]
  (log/info :do-derive!/starting {:props props})
  (let [user-id   (a.authentication/get-user-id env)
        wallet-id (::m.c.wallets/id props)]
    (log/info :do-derrive/parsed {:user-id user-id})
    (if-let [confirmed-wallet-id (q.c.wallets/find-by-user-and-wallet-id user-id wallet-id)]
      (if-let [wallet (q.c.wallets/read-record confirmed-wallet-id)]
        (do
          (log/info :do-derive!/read {:wallet wallet})
          (let [xpriv (get-xpriv wallet-id)]
            (log/info :do-derive!/derived {:xpriv xpriv}))
          {:status :ok})
        (throw (ex-info "no wallet" {})))
      (throw (ex-info "no wallet" {})))))
