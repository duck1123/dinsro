(ns dinsro.actions.core.wallets
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.queries.core.blocks :as q.c.blocks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.queries.core.words :as q.c.words]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.hd.BIP32Path
   org.bitcoins.core.hd.HDPurpose
   org.bitcoins.core.crypto.MnemonicCode
   org.bitcoins.core.crypto.BIP39Seed
   scala.collection.immutable.Vector))

(defn parse-descriptor
  [descriptor]
  (let [pattern #"(?<type>.*)\(\[(?<fingerprint>.*?)/(?<path>.*)\](?<key>.*?)/(?<keypath>.*)\)#(?<checksum>.*)"
        matcher (re-matcher pattern  descriptor)]
    (when (.matches matcher)
      {:type        (.group matcher "type")
       :fingerprint (.group matcher "fingerprint")
       :path        (.group matcher "path")
       :key         (.group matcher "key")
       :keypath     (.group matcher "keypath")
       :checksum    (.group matcher "checksum")})))

(defn create!
  [{::m.c.wallets/keys      [name user]
    {node ::m.c.nodes/id} ::m.c.wallets/node}]
  (let [props {::m.c.wallets/name name
               ::m.c.wallets/user user
               ::m.c.wallets/node node}]
    (log/info :wallet/create {:props props})
    (q.c.wallets/create-record props)))

(>defn calculate-derivation
  [wallet]
  [::m.c.wallets/item => any?]
  (let [{::m.c.wallets/keys [seed]} wallet]
    (MnemonicCode/fromWords (c.bitcoin-s/create-vector seed))))

(defn ->bip39-seed
  [wallet]
  (let [^MnemonicCode code (calculate-derivation wallet)
        password (BIP39Seed/EMPTY_PASSWORD)]
    (BIP39Seed/fromMnemonic code password)))

(defn ->priv-key
  [wallet]
  (let [seed (->bip39-seed wallet)]
    (c.bitcoin-s/get-xpriv seed 84 "regtest")))

(defn get-word-list
  [wallet-id]
  (let [ids (q.c.words/find-by-wallet wallet-id)]
    (->> ids
         (map q.c.words/read-record)
         (sort-by ::m.c.words/position)
         (mapv
          (fn [word]
            (log/info :word-list/item {:word word})
            (::m.c.words/word word))))))

(defn update-words!
  [wallet-id words]
  (log/info :words/updating {:wallet-id wallet-id :words words})
  (let [old-ids (q.c.words/find-by-wallet wallet-id)]
    (doseq [id old-ids]
      (q.c.words/delete! id))
    (let [response (doall
                    (map-indexed
                     (fn [i word]
                       (let [props {::m.c.words/word     word
                                    ::m.c.words/position (inc i)
                                    ::m.c.words/wallet   wallet-id}
                             word-id (q.c.words/create-record props)]
                         (q.c.words/read-record word-id)))
                     words))]
      (log/info :words/update-finished {:response response})
      response)))

(defn ^MnemonicCode get-mnemonic
  [wallet-id]
  (c.bitcoin-s/words->mnemonic (get-word-list wallet-id)))

(defn get-xpriv
  [wallet-id]
  (let [;; wallet (q.c.wallets/read-record wallet-id)
        mnemonic (get-mnemonic wallet-id)]
    (c.bitcoin-s/get-xpriv (BIP39Seed/fromMnemonic mnemonic (BIP39Seed/EMPTY_PASSWORD)) 84 "regtest")))

(defn get-wif
  [wallet-id]
  (let [xpriv (get-xpriv wallet-id)
        key   (.key xpriv)]
    (c.bitcoin-s/->wif key)))

(defn roll!
  [props]
  (log/info :roll/started {:props props})
  (let [wallet-id (::m.c.wallets/id props)
        words     (c.bitcoin-s/create-mnemonic-words)
        response  (update-words! wallet-id words)
        wif (get-wif wallet-id)
        wallet (q.c.wallets/read-record wallet-id)
        ;; props (assoc wallet ::m.c.wallets/key wif)
        props {::m.c.wallets/key wif}]
    (q.c.wallets/update! wallet-id props)
    (log/info :roll/finished {:response response})
    (merge wallet {::m.c.wallets/words response})))

(comment
  (def descriptor "wpkh([7c6cf2c1/84h/1h/0h]tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc/0/*)#8phlkw5l")

  {:type        "wpkh"
   :fingerprint "7c6cf2c1"
   :path        "84h/1h/0h"
   :key         "tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc"
   :keypath     "0/*"
   :checksum    "8phlkw5l"}

  (q.c.nodes/index-records)
  (q.c.blocks/index-records)
  (def node-name "bitcoin-alice")
  (def node (q.c.nodes/read-record (q.c.nodes/find-id-by-name node-name)))
  node

  (def client (m.c.nodes/get-client node ""))
  client

  (c.bitcoin/add-node client "bitcoin.bitcoin-bob")
  (c.bitcoin/get-peer-info client)
  (c.bitcoin/generate-to-address client "bcrt1q69zq0gn5cuflasuu8redktssdqxyxg8h6mh53j")

  (c.bitcoin-s/create-mnemonic-words)

  (roll! {})

  (q.c.wallets/index-ids)
  (tap> (q.c.wallets/index-records))
  (q.c.wallets/index-records)
  (def wallet (first (q.c.wallets/index-records)))
  (def wallet-id (::m.c.wallets/id wallet))

  (roll! {::m.c.wallets/id wallet-id})

  (c.bitcoin-s/->wif (.key (get-xpriv wallet-id)))
  (get-wif wallet-id)

  (get-word-list wallet-id)

  (c.bitcoin-s/words->mnemonic (get-word-list wallet-id))

  (get-mnemonic wallet-id)

  (update-words! wallet-id (c.bitcoin-s/create-mnemonic-words))
  (->priv-key wallet)

  (->bip39-seed wallet)

  (.words (calculate-derivation (first (q.c.wallets/index-records))))

  (tap> (seq (.getDeclaredMethods (.getClass (calculate-derivation (first (q.c.wallets/index-records)))))))

  (tap> (seq (.getDeclaredMethods BIP39Seed)))

  (def xpriv (c.bitcoin-s/get-xpriv
              (BIP39Seed/fromMnemonic
               (calculate-derivation (first (q.c.wallets/index-records)))
               (BIP39Seed/EMPTY_PASSWORD))
              84 "regtest"))

  (tap> (map
         (fn [m]
           {:str (str m)
            :name (.getName m)
            ;; :return (str (.getReturnTypes m))
            })
         (vec (.getDeclaredMethods (class xpriv)))))
  xpriv

  (.key xpriv)
  (.hex (.chainCode xpriv))

  (.fingerprint xpriv)

  (BIP39Seed/EMPTY_PASSWORD)

  (parse-descriptor descriptor)

  (MnemonicCode.)

  (def account-path (BIP32Path/fromString "m/84'/0'/0'"))
  account-path

  (def purpose (HDPurpose. 84))
  purpose

  (vec (.getDeclaredMethods (class purpose)))

  (let [builder (Vector/newBuilder)]
    (.addOne builder 1)
    (.addOne builder 2)
    (.addOne builder 3)
    (.result builder))

  (tap> (seq (.getDeclaredMethods (.getClass (Vector/newBuilder)))))

  (Vector/fill 1 2 3 4 5)

  nil)
