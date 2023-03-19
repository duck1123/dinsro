^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.actions.core.wallets-notebook
  (:require
   [dinsro.actions.core.wallets :as a.c.wallets]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.client.converters.ext-private-key :as cc.ext-privat-key]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk])
  (:import
   org.bitcoins.core.crypto.BIP39Seed
   org.bitcoins.core.crypto.ECPrivateKeyUtil
   org.bitcoins.core.crypto.MnemonicCode
   org.bitcoins.core.hd.HDPurpose
   [scodec.bits BitVector]))

;; # Core Wallet Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(def descriptor "wpkh([8c694d63/84h/1h/0h]tpubDCaHeqXShp6x7GSBnyBaFB6tPtdCfU5otmgRZu6ChfVvE4EXYHxPhwMsXvp1aFZBd7CRmpJWfDbZDKfV7tNe5ThHgUaRMEDQM39BCyxWwNU/0/*)#ytuthqy3")

(def node-name "bitcoin-alice")

^{::clerk/viewer clerk/code}
(def wallet (first (q.c.wallets/index-records)))
(def wallet-id (::m.c.wallets/id wallet))

;; ## get-xpriv

(def xpriv (try (a.c.wallets/get-xpriv wallet-id) (catch Exception _ex nil)))

(and xpriv (.toStringSensitive xpriv))

(and xpriv (.extPublicKey xpriv))

(def base-path "m/84'/1'/0'")
(def account-path (try (c.bitcoin-s/->bip32-path base-path) (catch Exception _ex nil)))
(def first-address-path (c.bitcoin-s/->segwit-path (str base-path "/0/0")))
(def account-xpub (and account-path
                       (some-> xpriv (.deriveChildPrivKey account-path) .extPublicKey)))
(def diffsome (and account-path (.diff account-path first-address-path)))
(def purpose (HDPurpose. 84))

(and xpriv (.fingerprint xpriv))

;; ##  get-word-list

(try (a.c.wallets/get-word-list wallet-id) (catch Exception ex ex))

;; ## ->bip39-seed

(try (a.c.wallets/->bip39-seed wallet) (catch Exception ex ex))

;; ## calculate-derivation

(try (a.c.wallets/calculate-derivation wallet) (catch Exception ex ex))

;; ## get-mnemonic

(try (a.c.wallets/get-mnemonic wallet-id) (catch Exception ex ex))

;; ## get-bip39-seed

#_(a.c.wallets/get-bip39-seed wallet)

^{::clerk/viewer clerk/code}
(try (cc.ext-privat-key/ExtPrivateKey->record xpriv) (catch Exception ex ex))

;; ## get-wif

(try (a.c.wallets/get-wif wallet-id) (catch Exception ex ex))

;; ## get-address

(try (a.c.wallets/get-address wallet 0) (catch Exception ex ex))

;; ## update-words!

(comment

  (a.c.wallets/update-words! wallet-id (c.bitcoin-s/create-mnemonic-words))

  nil)

;; ## ->priv-key

(try (a.c.wallets/->priv-key wallet) (catch Exception ex ex))

;; ## parse-descriptor

^{::clerk/viewer clerk/code}
(a.c.wallets/parse-descriptor descriptor)

(try (a.c.wallets/get-ext-pub-key wallet 0) (catch Exception ex ex))

(def wallet-a-words
  ["universe" "loud"   "stable" "patrol"  "artwork"  "chimney"
   "acoustic" "chief"  "one"    "use"     "object"   "gossip"
   "enter"    "green"  "scout"  "brother" "worry"    "fancy"
   "olive"    "salmon" "chef"   "repair"  "hospital" "milk"])

(def wallet-b-words ["violin" "bleak"  "raw"  "mistake" "toddler" "wire"
                     "kind"   "state"  "aim"  "game"    "glass"   "peace"
                     "bone"   "luxury" "list" "flash"   "music"   "impulse"
                     "naive"  "type"   "wet"  "reform"  "panic"   "expand"])

(def wallet-a-entropy-hex "edd0874f5090d25000813e9abdf6603274b6ccb050e7fdca5e68df32756d1b7c")
(def wallet-a-seed-hex "91a06a5c738f077a9b0ea21f2a07250336af6e1e5f82dd822c676b39fbc3da6fb7d7101438a35dbd491f6ea55880a19751ff8a892bdc9b549265ac83f82a3de5")
(def wallet-a-tpriv "tprv8ZgxMBicQKsPerr7TjPkdyirqJXtYBqiDYHhYusszUa4DQksbdAHnpViXG2VEyEHqgQjhZ7F8cFwi9rRtdQrAxbZ5g2BaR1pWmojCzHdftQ")
(def wallet-a-vpriv "vprv9MzYTyxDsS6wiGT8r3gLJN3mathoRdA2SPnTXaymAUFknU5oDnsn22ntkFm4455LQM726qF33xWDJrywAmxk2XGYp2X3mmZzH32Y3SHGqib")

(comment
  {:type        "wpkh"
   :fingerprint "7c6cf2c1"
   :path        "84h/1h/0h"
   :key         "tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc"
   :keypath     "0/*"
   :checksum    "8phlkw5l"}

  (c.bitcoin-s/create-mnemonic-words)

  (a.c.wallets/roll! {::m.c.wallets/id wallet-id})

  (c.bitcoin-s/->wif (.key (a.c.wallets/get-xpriv wallet-id)))

  (c.bitcoin-s/words->mnemonic (a.c.wallets/get-word-list wallet-id))

  (def xpriv3
    (c.bitcoin-s/get-xpriv
     (a.c.wallets/mnemonic->seed
      (c.bitcoin-s/words->mnemonic wallet-b-words))
     84 "testnet"))

  (def wallet-a-mc (c.bitcoin-s/words->mnemonic wallet-a-words))

  (.toBase16 (.toEntropy wallet-a-mc))

  (c.bitcoin-s/get-words
   (MnemonicCode/fromEntropy
    (BitVector/fromValidHex
     wallet-a-entropy-hex
     (BitVector/fromHex$default$2))))

  (def wallet-a-seed (a.c.wallets/mnemonic->seed wallet-a-mc))
  (def xpriv2 (c.bitcoin-s/get-xpriv wallet-a-seed  84 "testnet"))
  xpriv2

  (.bytes wallet-a-seed)
  (.key xpriv2)
  (.hexLE wallet-a-seed)

  (def wallet-a-seed-hex)

  (.toStringSensitive wallet-a-seed)

  (.hex xpriv2)

  (def xpriv2-hex "045f18bc000000000000000000c798c6deaed4cd6a88858afa94bb9b3dab21a7d323d0dc83c4cde964f07d65800086380c05b1676053ba1cbc5670358e4f6e6717213854f61cddbf3e5b675d703d")

  (.bytes (BIP39Seed/fromHex wallet-a-seed-hex))
  (MnemonicCode/fromEntropy (.bytesLE (BIP39Seed/fromHex wallet-a-seed-hex)))
  (c.bitcoin-s/get-words (MnemonicCode/fromEntropy (.bytes (BIP39Seed/fromHex wallet-a-seed-hex))))

  (.hex (BIP39Seed/fromHex wallet-a-seed-hex))

  (let [wif      (c.bitcoin-s/->wif (.key xpriv2))
        pk-bytes (ECPrivateKeyUtil/fromWIFToPrivateKey wif)
        pk-bv    (.bytes pk-bytes)]
    {:pk-bv pk-bv
     :pk-hex (.hex pk-bytes)
     :xpriv (.hex xpriv2)
     :words (c.bitcoin-s/get-words (MnemonicCode/fromEntropy pk-bv))})

  (def wif2 "92uc3cEZDSiWzcmK9dfXniiV2Yoo51HLwbkAdqQbvKF2zt3o4tb")
  wif2

  (.hex (ECPrivateKeyUtil/fromWIFToPrivateKey wif2))

  (.hex (.toPrivateKey (ECPrivateKeyUtil/fromWIFToPrivateKey wif2)))

  (= (.bytes (ECPrivateKeyUtil/fromWIFToPrivateKey wif2))
     (.bytes (.toPrivateKey (ECPrivateKeyUtil/fromWIFToPrivateKey wif2))))

  (ECPrivateKeyUtil/isCompressed wif2)
  (ECPrivateKeyUtil/parseNetworkFromWIF wif2)

  (.words (a.c.wallets/calculate-derivation (first (q.c.wallets/index-records))))

  (.get (.deriveChildPubKey account-xpub (.get diffsome)))

  (BIP39Seed/EMPTY_PASSWORD)

  nil)
