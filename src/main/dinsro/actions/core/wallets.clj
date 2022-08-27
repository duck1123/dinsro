(ns dinsro.actions.core.wallets
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.queries.core.words :as q.c.words]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.crypto.BIP39Seed
   org.bitcoins.core.crypto.MnemonicCode
   org.bitcoins.core.crypto.ExtPrivateKey
   org.bitcoins.core.crypto.ExtPublicKey
   org.bitcoins.core.protocol.Bech32Address
   org.bitcoins.core.protocol.script.P2WPKHWitnessSPKV0))

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
  (c.bitcoin-s/words->mnemonic (get-word-list wallet-id)))

;; https://bitcoin-s.org/api/org/bitcoins/core/crypto/ExtPrivateKey.html

(defn get-xpriv
  ^ExtPrivateKey [wallet-id]
  (log/finer :get-xpriv/starting {:wallet-id wallet-id})
  (let [mnemonic     (get-mnemonic wallet-id)
        network-name "testnet"
        bip39-seed   (BIP39Seed/fromMnemonic mnemonic (BIP39Seed/EMPTY_PASSWORD))
        purpose      84]
    (c.bitcoin-s/get-xpriv bip39-seed purpose network-name)))

(defn get-bip39-seed
  [wallet]
  (log/info :get-bip39-seed/starting {:wallet wallet})
  (let [{::m.c.wallets/keys [key]} wallet
        private-key-bytes          (c.bitcoin-s/wif->pk key)]
    private-key-bytes))

(defn get-wif
  "Get the wallet's private key wif formatted"
  [wallet-id]
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
  [wallet index]
  [::m.c.wallets/item number? => (ds/instance? ExtPublicKey)]
  (log/finer :get-ext-pub-key/starting {:wallet wallet :index index})
  (let [wallet-id          (::m.c.wallets/id wallet)
        xpriv              (get-xpriv wallet-id)
        prefix             "m/84'/1'/0'"
        account-path       (c.bitcoin-s/->bip32-path prefix)
        first-address-path (c.bitcoin-s/->segwit-path (str prefix "/0/" index))
        diffsome           (.diff account-path first-address-path)
        account-xpub       (.extPublicKey (.deriveChildPrivKey xpriv account-path))
        ext-pub-key        (.get (.deriveChildPubKey account-xpub (.get diffsome)))]
    ext-pub-key))

(>defn get-address
  "Get the address at the given index for the wallet"
  [wallet index]
  [::m.c.wallets/item number? => string?]
  (let [wallet-id (::m.c.wallets/id wallet)]
    (log/finer :get-address/starting {:wallet-id wallet-id :index index})
    (let [ext-pub-key    (get-ext-pub-key wallet index)
          pubkey         (.key ext-pub-key)
          script-pub-key (P2WPKHWitnessSPKV0/apply pubkey)
          network        (c.bitcoin-s/regtest-network)
          address        (Bech32Address/apply script-pub-key network)]
      (.value address))))
