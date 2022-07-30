^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.core.wallets-notebook
  (:require
   [dinsro.actions.core.wallets :as a.c.wallets]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.queries.core.blocks :as q.c.blocks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk])
  (:import
   org.bitcoins.core.hd.HDPurpose
   org.bitcoins.core.crypto.BIP39Seed
   org.bitcoins.core.crypto.MnemonicCode
   scala.collection.immutable.Vector))

;; # Core Wallet Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def descriptor "wpkh([7c6cf2c1/84h/1h/0h]tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc/0/*)#8phlkw5l")

(def node-name "bitcoin-alice")

(def wallet (first (q.c.wallets/index-records)))
(def wallet-id (::m.c.wallets/id wallet))

(a.c.wallets/->bip39-seed wallet)

;; ##  get-word-list

(a.c.wallets/get-word-list wallet-id)

(def xpriv (a.c.wallets/get-xpriv wallet-id))
(def account-path (c.bitcoin-s/->bip32-path "m/84'/0'/0'"))
(def first-address-path (c.bitcoin-s/->segwit-path "m/84'/0'/0'/0/0"))
(def account-xpub (.extPublicKey (.deriveChildPrivKey xpriv account-path)))
(def diffsome (.diff account-path first-address-path))
(def purpose (HDPurpose. 84))

(comment
  {:type        "wpkh"
   :fingerprint "7c6cf2c1"
   :path        "84h/1h/0h"
   :key         "tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc"
   :keypath     "0/*"
   :checksum    "8phlkw5l"}

  (q.c.nodes/index-records)
  (q.c.blocks/index-records)
  (def node (q.c.nodes/read-record (q.c.nodes/find-id-by-name node-name)))
  node

  (def client nil)
  client

  (c.bitcoin/add-node client "bitcoin.bitcoin-bob")
  (c.bitcoin/get-peer-info client)
  (c.bitcoin/generate-to-address client "bcrt1q69zq0gn5cuflasuu8redktssdqxyxg8h6mh53j")

  (c.bitcoin-s/create-mnemonic-words)

  (q.c.wallets/index-ids)
  (tap> (q.c.wallets/index-records))
  (q.c.wallets/index-records)

  (a.c.wallets/get-bip39-seed wallet)
  (a.c.wallets/get-address wallet 2)

  (map
   #(a.c.wallets/get-address wallet %)
   (range 20))

  (.key (a.c.wallets/get-ext-pub-key wallet 0))

  (a.c.wallets/roll! {::m.c.wallets/id wallet-id})

  (c.bitcoin-s/->wif (.key (a.c.wallets/get-xpriv wallet-id)))
  (a.c.wallets/get-wif wallet-id)

  (a.c.wallets/get-word-list wallet-id)

  (c.bitcoin-s/words->mnemonic (a.c.wallets/get-word-list wallet-id))

  (a.c.wallets/get-mnemonic wallet-id)

  (a.c.wallets/update-words! wallet-id (c.bitcoin-s/create-mnemonic-words))
  (a.c.wallets/->priv-key wallet)

  (a.c.wallets/->bip39-seed wallet)

  (a.c.wallets/calculate-derivation wallet)
  (.words (a.c.wallets/calculate-derivation (first (q.c.wallets/index-records))))

  (tap> (seq (.getDeclaredMethods (.getClass (a.c.wallets/calculate-derivation (first (q.c.wallets/index-records)))))))

  (tap> (seq (.getDeclaredMethods BIP39Seed)))

  xpriv

  account-path
  account-xpub

  diffsome
  (.get diffsome)

  (.get (.deriveChildPubKey account-xpub (.get diffsome)))

  (.key xpriv)
  (.hex (.chainCode xpriv))

  (.fingerprint xpriv)

  (BIP39Seed/EMPTY_PASSWORD)

  (a.c.wallets/parse-descriptor descriptor)

  (MnemonicCode.)

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
