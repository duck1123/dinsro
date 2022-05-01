(ns dinsro.client.bitcoin-s
  (:require
   [dinsro.client.scala :as cs]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.hd.BIP32Path
   org.bitcoins.core.hd.SegWitHDPath
   org.bitcoins.core.hd.HDPurpose
   org.bitcoins.core.hd.SegWitHDPath
   org.bitcoins.crypto.ECPrivateKey
   org.bitcoins.crypto.ECPrivateKeyBytes
   org.bitcoins.core.crypto.ECPrivateKeyUtil
   org.bitcoins.core.crypto.MnemonicCode
   org.bitcoins.core.crypto.BIP39Seed
   org.bitcoins.core.crypto.ExtPrivateKey
   org.bitcoins.core.crypto.ExtKeyVersion$SegWitMainNetPriv$
   org.bitcoins.core.crypto.ExtKeyPrivVersion
   org.bitcoins.core.config.NetworkParameters
   org.bitcoins.core.protocol.Bech32Address
   org.bitcoins.core.protocol.script.P2WPKHWitnessSPKV0
   org.bitcoins.core.protocol.script.WitnessScriptPubKey
   org.bitcoins.core.config.BitcoinNetworks
   org.bitcoins.core.util.HDUtil
   org.bitcoins.rpc.config.BitcoindAuthCredentials$PasswordBased
   org.bitcoins.rpc.config.BitcoindInstanceRemote
   org.bitcoins.rpc.config.ZmqConfig
   scodec.bits.BitVector
   scodec.bits.ByteVector))

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
  [bip39-seed purpose network]
  (let [priv-version (get-xpriv-version purpose network)]
    (.toExtPrivateKey bip39-seed priv-version)))

(defn ->wif
  [key]
  (let [pk-bv (.bytes key)
        pk-bytes (ECPrivateKeyBytes. pk-bv false)
        network (regtest-network)]
    (ECPrivateKeyUtil/toWIF pk-bytes network)))

(defn get-zmq-config
  []
  (ZmqConfig/empty))

(comment
  (cs/vector->vec (.words (create-mnemonic)))
  (prn (create-mnemonic-words))

  (create-seed "")

  (def mn (create-mnemonic))
  mn
  (get-words mn)

  (regtest-network)

  (get-xpub-version 84 "regtest")
  (def priv-version (get-xpriv-version 84 "regtest"))

  (ExtKeyPrivVersion)

  (def segwit-path (SegWitHDPath/fromString "m/84'/0'/0'/0/0"))

  (def passphrase "secret-passphrase")
  (def wallet-path "m/84'/0'/0'")
  (def address-path (str wallet-path "/0/0"))
  (def bip39-seed (create-seed passphrase))

  ;; (def extpub (get-child-key xpriv wallet-path address-path))
  ;; extpub

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

  (BitcoindInstanceRemote/apply
   (regtest-network)
   (URI. ""))

  nil)
