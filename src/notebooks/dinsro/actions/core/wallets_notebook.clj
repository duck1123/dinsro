^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.core.wallets-notebook
  (:require
   [dinsro.actions.core.wallets :as a.c.wallets]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.client.converters.ext-private-key :as cc.ext-privat-key]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk])
  (:import
   org.bitcoins.core.hd.HDPurpose
   org.bitcoins.core.crypto.BIP39Seed))

;; # Core Wallet Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def descriptor "wpkh([7c6cf2c1/84h/1h/0h]tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc/0/*)#8phlkw5l")

(def node-name "bitcoin-alice")

^{::clerk/viewer clerk/code}
(def wallet (first (q.c.wallets/index-records)))
(def wallet-id (::m.c.wallets/id wallet))

;; ## get-xpriv

(def xpriv (try (a.c.wallets/get-xpriv wallet-id) (catch Exception _ex nil)))

(def account-path (c.bitcoin-s/->bip32-path "m/84'/0'/0'"))
(def first-address-path (c.bitcoin-s/->segwit-path "m/84'/0'/0'/0/0"))
(def account-xpub (some-> xpriv (.deriveChildPrivKey account-path) .extPublicKey))
(def diffsome (.diff account-path first-address-path))
(def purpose (HDPurpose. 84))

;; ##  get-word-list

(a.c.wallets/get-word-list wallet-id)

;; ## ->bip39-seed

(try (a.c.wallets/->bip39-seed wallet) (catch Exception ex ex))

;; ## calculate-derivation

(try (a.c.wallets/calculate-derivation wallet) (catch Exception ex ex))

;; ## get-mnemonic

(a.c.wallets/get-mnemonic wallet-id)

;; ## get-bip39-seed

#_(a.c.wallets/get-bip39-seed wallet)

^{::clerk/viewer clerk/code}
(cc.ext-privat-key/ExtPrivateKey->record xpriv)

(.toStringSensitive xpriv)

;; ## get-wif

(a.c.wallets/get-wif wallet-id)

;; ## get-address

(a.c.wallets/get-address wallet 0)

;; ## update-words!

(comment

  (a.c.wallets/update-words! wallet-id (c.bitcoin-s/create-mnemonic-words))

  nil)

;; ## ->priv-key

(comment

  (a.c.wallets/->priv-key wallet)

  nil)

;; ## parse-descriptor

(a.c.wallets/parse-descriptor descriptor)

(comment
  {:type        "wpkh"
   :fingerprint "7c6cf2c1"
   :path        "84h/1h/0h"
   :key         "tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc"
   :keypath     "0/*"
   :checksum    "8phlkw5l"}

  (c.bitcoin-s/create-mnemonic-words)

  (.key (a.c.wallets/get-ext-pub-key wallet 0))

  (a.c.wallets/roll! {::m.c.wallets/id wallet-id})

  (c.bitcoin-s/->wif (.key (a.c.wallets/get-xpriv wallet-id)))
  (a.c.wallets/get-wif wallet-id)

  (c.bitcoin-s/words->mnemonic (a.c.wallets/get-word-list wallet-id))

  (.words (a.c.wallets/calculate-derivation (first (q.c.wallets/index-records))))

  (.get (.deriveChildPubKey account-xpub (.get diffsome)))

  (BIP39Seed/EMPTY_PASSWORD)

  nil)
