(ns dinsro.actions.wallets
  (:require
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.wallets :as m.wallets]
   [dinsro.queries.core-block :as q.core-block]
   [dinsro.queries.core-nodes :as q.core-nodes]
   [dinsro.queries.wallets :as q.wallets]
   [taoensso.timbre :as log])
  (:import
   org.bitcoins.core.hd.BIP32Path
   org.bitcoins.core.hd.SegWitHDPath
   org.bitcoins.core.hd.HDPurpose
   org.bitcoins.core.hd.SegWitHDPath
   org.bitcoins.crypto.ECPrivateKey
   org.bitcoins.core.protocol.script.P2WPKHWitnessSPKV0
   org.bitcoins.core.protocol.Bech32Address
   org.bitcoins.core.crypto.MnemonicCode
   org.bitcoins.core.crypto.BIP39Seed
   org.bitcoins.core.crypto.ExtPrivateKey
   org.bitcoins.core.crypto.ExtKeyVersion$SegWitMainNetPriv$
   org.bitcoins.core.crypto.ExtKeyPrivVersion
   org.bitcoins.core.util.HDUtil
   org.bitcoins.core.config.BitcoinNetworks
   scodec.bits.ByteVector))

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
  [{::m.wallets/keys      [name user]
    {node ::m.core-nodes/id} ::m.wallets/node}]
  (log/info "creating wallet")
  (let [props {::m.wallets/name name
               ::m.wallets/user user
               ::m.wallets/node node}]
    (q.wallets/create-record props)))

(defn vector->vec
  [v]
  (vec (.vectorSlice v 0)))

(defn create-mnemonic
  []
  (let [entopy        (MnemonicCode/getEntropy256Bits)
        mnemonic-code (MnemonicCode/fromEntropy entopy)]
    mnemonic-code))

(defn get-words
  [mc]
  (vector->vec (.words mc)))

(defn create-seed
  [passphrase]
  (let [mc (create-mnemonic)]
    (BIP39Seed/fromMnemonic mc passphrase)))

(defn regtest-network
  []
  (BitcoinNetworks/fromString "regtest"))

(defn get-xpub-version
  [purpose network]
  (HDUtil/getXpubVersion
   (HDPurpose. purpose)
   (BitcoinNetworks/fromString network)))

(defn get-xpriv-version
  [purpose network]
  (HDUtil/getXprivVersion
   (HDPurpose. purpose)
   (BitcoinNetworks/fromString network)))

(defn get-address
  [script-pub-key network]
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
  [bip39-seed purpose network]
  (let [priv-version (get-xpriv-version purpose network)]
    (.toExtPrivateKey bip39-seed priv-version)))

(comment
  (def descriptor "wpkh([7c6cf2c1/84h/1h/0h]tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc/0/*)#8phlkw5l")

  {:type        "wpkh"
   :fingerprint "7c6cf2c1"
   :path        "84h/1h/0h"
   :key         "tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc"
   :keypath     "0/*"
   :checksum    "8phlkw5l"}

  (q.core-nodes/index-records)
  (q.core-block/index-records)
  (def node-name "bitcoin-alice")
  (def node (q.core-nodes/read-record (q.core-nodes/find-id-by-name node-name)))
  node

  (def client (m.core-nodes/get-client node ""))
  client

  (c.bitcoin/add-node client "bitcoin.bitcoin-bob")
  (c.bitcoin/get-peer-info client)
  (c.bitcoin/generate-to-address client "bcrt1q69zq0gn5cuflasuu8redktssdqxyxg8h6mh53j")

  (q.wallets/index-ids)
  (tap> (q.wallets/index-records))
  (q.wallets/index-records)

  (parse-descriptor descriptor)

  (def mn (create-mnemonic))
  mn
  (get-words mn)

  (def account-path (BIP32Path/fromString "m/84'/0'/0'"))
  account-path

  (regtest-network)

  (def purpose (HDPurpose. 84))
  purpose

  (vec (.getDeclaredMethods (class purpose)))

  (get-xpub-version 84 "regtest")
  (def priv-version (get-xpriv-version 84 "regtest"))

  (ExtKeyPrivVersion)

  (def segwit-path (SegWitHDPath/fromString "m/84'/0'/0'/0/0"))

  (def passphrase "secret-passphrase")
  (def wallet-path "m/84'/0'/0'")
  (def address-path (str wallet-path "/0/0"))
  (def bip39-seed (create-seed passphrase))

  (-> bip39-seed
      (get-xpriv 84 "regtest")
      (get-child-key wallet-path address-path)
      (get-script-pub-key)
      (get-address "regtest"))

  (def priv-key-s "xprv9s21ZrQH143K4LCRq4tUZUt3fiTNZr6QTiep3HGzMxtSwfxKAhBmNJJnsmoyWuYZCPC4DNsiVwToHJbxZtq4iEkozBhMzWNTiCH4tzJNjPi")
  (parse-ext-priv-key priv-key-s)

  (ExtPrivateKey/freshRootKey ExtKeyVersion$SegWitMainNetPriv$)

  (.fromValidHex ByteVector "70ea14ac30939a972b5a67cab952d6d7d474727b05fe7f9283abc1e505919e83")

  (def private-key (ECPrivateKey/freshPrivateKey))
  private-key

  (def public-key (.publicKey private-key))
  public-key

  nil)
