(ns dinsro.helm.specter
  (:require
   #?(:clj [cheshire.core :as json2])
   #?(:clj [clj-yaml.core :as yaml])
   #?(:cljs [dinsro.yaml :as yaml])))

(defn device-config
  []
  {:name "bar"
   :alias "bar"
   :type "bitcoincore"
   :keys
   [{:original    "upub5E7bCguufNrMb1Umm9HZgcxSRAQngjfYK19AwXuUi6DDR79BVRE3nNgG8aptADyMW7QW7bF3Qj2FEGnvevXxFNRY5BCsahTKaCjLhxR61BW",
     :fingerprint "8c694d63",
     :derivation  "m/49h/1h/0h",
     :type        "sh-wpkh",
     :purpose     "#0 Single Sig (Nested)",
     :xpub        "tpubDCz1dvkLphSFQhEyiQdXWoaKGBwnVz96byJ8eGeHC26kKhFaKcZpo1KWSEnRgStMWwHpybYbKouwaK8ECsdEXWvdjo3T1xa1yLWgdk6m9gw"},
    {:original     "vpub5YY8XGMvhB4Y8ss6g4dEd5aWiqEenqbkWv3geZFHbjzGNZwMxknBKNNmFUp43wJ71vRufHbXCo4QkZwkHdhNbYsntCSGVsvCDdRUfqiiKMB",
     :fingerprint  "8c694d63",
     :derivation   "m/84h/1h/0h",
     :type         "wpkh",
     :purpose      "#0 Single Sig (Segwit)",
     :xpub         "tpubDCaHeqXShp6x7GSBnyBaFB6tPtdCfU5otmgRZu6ChfVvE4EXYHxPhwMsXvp1aFZBd7CRmpJWfDbZDKfV7tNe5ThHgUaRMEDQM39BCyxWwNU"},
    {:original     "tpubDD5kYZF56y2uHkG2jonAXzUMpMETfCV7iNTVv6jq2gkiFeEuqdwqr8YuPAkcy2SbfGNa3dSsFZGPidBfyKd5KPvmauJF3xuKoSJgTonr9Tu",
     :fingerprint  "8c694d63",
     :derivation   "m/86h/1h/0h",
     :type         "tr",
     :purpose      "#0 Single Sig (Taproot)",
     :xpub         "tpubDD5kYZF56y2uHkG2jonAXzUMpMETfCV7iNTVv6jq2gkiFeEuqdwqr8YuPAkcy2SbfGNa3dSsFZGPidBfyKd5KPvmauJF3xuKoSJgTonr9Tu"},
    {:original     "Upub5TBPRhvE5Ra2WboEWtd7Azr4YaJYavt5nBMNSq6r8DwLTmwsq53YGJVs1xG6mRBbcJ2vgNRBEYXV4mak7PcfD2CVpCG4EsnmtWwcv6ETQja",
     :fingerprint  "8c694d63",
     :derivation   "m/48h/1h/0h/1h",
     :type         "sh-wsh",
     :purpose      "#0 Multisig Sig (Nested)",
     :xpub         "tpubDF9iji2DfnbYuiQ4XVW6B788fooHBpg3ksrfEJa7ENzTkBVMts13BpHBWuGAjCshPerGfnicGR2gHeJHN7YzLgaveLzEGjSUPvT6FsDrNMa"},
    {:original     "Vpub5n1ejNb9E77WR4vEkReDTWYyAwZsyqwH3gj6ycgdomkeY3m54zUBTi56qUgYQat9C8SMZdyTym5XGjSB69nKqHZP56ZcNRkeirQf3zdV7tM",
     :fingerprint  "8c694d63",
     :derivation   "m/48h/1h/0h/2h",
     :type         "wsh",
     :purpose      "#0 Multisig Sig (Segwit)",
     :xpub         "tpubDF9iji2DfnbYxtKwvfjaFXjY8CvAe7jk7GiAyhG1XvRtmMVKt8G7mACHKDj2NTvKZr8toagLYyEAcKY9dBJeAiGD2ubMpNarxXrV1J2w3zh"}],
   :blinding_key "",
   :fullpath "/data/.specter/devices/bar.json"})

(defn wallet-config
  []
  {:name              "bar",
   :alias             "bar",
   :description       "Single (Segwit)",
   :address_type      "bech32",
   :address           "bcrt1qpe5thhr9p582w0ymg99ea5ql9e5y5lqsxm5527",
   :address_index     0,
   :change_address    "bcrt1q9lqkyt84nctp7eh2sfp6fxuuf0wcjug42r3see",
   :change_index      0,
   :keypool           60,
   :change_keypool    20,
   :recv_descriptor   "wpkh([8c694d63/84h/1h/0h]tpubDCaHeqXShp6x7GSBnyBaFB6tPtdCfU5otmgRZu6ChfVvE4EXYHxPhwMsXvp1aFZBd7CRmpJWfDbZDKfV7tNe5ThHgUaRMEDQM39BCyxWwNU/0/*)#ytuthqy3",
   :change_descriptor "wpkh([8c694d63/84h/1h/0h]tpubDCaHeqXShp6x7GSBnyBaFB6tPtdCfU5otmgRZu6ChfVvE4EXYHxPhwMsXvp1aFZBd7CRmpJWfDbZDKfV7tNe5ThHgUaRMEDQM39BCyxWwNU/1/*)#4le2245f",
   :keys
   [{:original    "vpub5YY8XGMvhB4Y8ss6g4dEd5aWiqEenqbkWv3geZFHbjzGNZwMxknBKNNmFUp43wJ71vRufHbXCo4QkZwkHdhNbYsntCSGVsvCDdRUfqiiKMB",
     :fingerprint "8c694d63",
     :derivation  "m/84h/1h/0h",
     :type        "wpkh",
     :purpose     "#0 Single Sig (Segwit)",
     :xpub        "tpubDCaHeqXShp6x7GSBnyBaFB6tPtdCfU5otmgRZu6ChfVvE4EXYHxPhwMsXvp1aFZBd7CRmpJWfDbZDKfV7tNe5ThHgUaRMEDQM39BCyxWwNU"}],
   :devices          ["bar"],
   :sigs_required    1,
   :blockheight      0,
   :pending_psbts    {},
   :frozen_utxo      [],
   :last_block       "25bacd254290721c9ce8185a6295776c562c838c9a440276ba7b1d469b768faf"})

(defn ->node-config
  [options]
  (let [{:keys [name alias rpcuser rpcpassword port host]
         :or   {rpcuser     "rpcuser"
                rpcpassword "rpcpassword"}} options]
    {:name          name
     :alias         alias
     :autodetect    false
     :datadir       ""
     :user          rpcuser
     :password      rpcpassword
     :port          port
     :host          host
     :protocol      "http"
     :external_node true
     :fullpath      (str "/data/.specter/nodes/" name ".json")}))

(defn merge-defaults
  [options]
  (let [{:keys [name alias rpcuser rpcpassword port host]
         :or
         {name        "foo"
          alias       "bar"
          rpcuser     "rpcuser"
          rpcpassword "rpcpassword"
          port        18443
          host        (str "bitcoin." name)}} options]

    {:name        name
     :alias       alias
     :rpcuser     rpcuser
     :rpcpassword rpcpassword
     :port        port
     :host        host}))

(defn ->values
  [{:keys [name] :as options}]
  (let [options (merge-defaults options)
        host    (str "specter." name ".localhost")]
    {:image        {:tag "v1.10.3"}
     :ingress      {:hosts [{:host  host
                             :paths [{:path "/"}]}]}
     :persistence  {:storageClassName "local-path"}
     :walletConfig #?(:clj (json2/encode (->node-config options))
                      :cljs (do (comment options) "{}"))}))

(defn ->values-yaml
  [options]
  (yaml/generate-string (->values (merge-defaults options))))
