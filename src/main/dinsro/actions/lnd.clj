(ns dinsro.actions.lnd
  (:require
   [buddy.core.codecs :as bcc]
   [buddy.core.codecs.base64 :as b64]
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [puget.printer :as puget]
   [taoensso.timbre :as log])
  (:import
   io.grpc.stub.StreamObserver
   java.io.File
   java.net.URL
   org.bitcoinj.params.MainNetParams
   org.bitcoinj.params.RegTestParams
   org.bitcoinj.wallet.KeyChainGroup
   org.lightningj.lnd.wrapper.Message
   org.lightningj.lnd.wrapper.message.AddressType
   org.lightningj.lnd.wrapper.message.LightningAddress
   org.lightningj.lnd.wrapper.message.ListChannelsRequest
   org.lightningj.lnd.wrapper.message.ListInvoiceRequest
   org.lightningj.lnd.wrapper.message.OpenChannelRequest
   org.lightningj.lnd.wrapper.AsynchronousLndAPI
   org.lightningj.lnd.wrapper.SynchronousLndAPI
   org.lightningj.lnd.wrapper.walletunlocker.AsynchronousWalletUnlockerAPI
   org.lightningj.lnd.wrapper.walletunlocker.message.InitWalletRequest
   org.lightningj.lnd.wrapper.walletunlocker.message.UnlockWalletRequest
   org.lightningj.lnd.wrapper.walletunlocker.SynchronousWalletUnlockerAPI
   wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient))

(def wallet-base "/usr/src/app/")
(def payment-request "")

(def config1
  {:name          "lnd"
   :host          "lnd-internal.lnd.svc.cluster.local"
   :port          10009
   :cert-path     (str wallet-base "tls-lnd.cert")
   :macaroon-path (str wallet-base "admin-lnd.macaroon")
   :mnemonic      ["abandon" "horror"  "because" "buddy"
                   "jump"    "satisfy" "escape"  "flee"
                   "tape"    "pull"    "bacon"   "arm"
                   "twenty"  "filter"  "burst"   "mirror"
                   "ghost"   "short"   "work"    "home"
                   "punch"   "little"  "like"    "gym"]})

(def config2
  {:name          "lnd2"
   :host          "lnd2-internal.lnd2.svc.cluster.local"
   :port          10009
   :cert-path     (str wallet-base "tls-lnd2.cert")
   :macaroon-path (str wallet-base "admin-lnd2.macaroon")
   :mnemonic      ["absorb" "impulse"  "slide"   "trumpet"
                   "garage" "happy"    "round"   "rely"
                   "rebel"  "flower"   "vessel"  "regular"
                   "trick"  "mechanic" "bird"    "hope"
                   "appear" "oblige"   "someone" "spell"
                   "robot"  "riot"     "swamp"   "pulp"]})

(def configs [config1 config2])

(defn get-url
  []
  (let [user     "rpcuser"
        password "rpcpassword"
        host     "bitcoind.bitcoin"
        port     "18443"]
    (URL. (str "http://" user ":" password "@" host ":" port))))

(defn download-file
  [uri file]
  (with-open [in  (io/input-stream uri)
              out (io/output-stream file)]
    (io/copy in out)))

(defn download-certs
  []
  (doseq [config configs]
    (let [{:keys [host name]} config]
      (download-file
       (format "http://%s/tls.cert" host)
       (io/file (format "tls-%s.cert" name))))))

(defn download-macaroon
  [config]
  (let [{:keys [host name]} config]
    (download-file
     (format "http://%s/admin.macaroon" host)
     (io/file (format "admin-%s.macaroon" name)))))

(defn download-macaroons
  []
  (doseq [config configs]
    (download-macaroon config)))

(defn download-files
  []
  (download-certs)
  (download-macaroons))

(defn get-api
  [config]
  (SynchronousLndAPI. (:host config)
                      (:port config)
                      (File. (:cert-path config))
                      (File. (:macaroon-path config))))

(defn get-wallet-unlocker
  [config]
  (SynchronousWalletUnlockerAPI.
   (:host config)
   (:port config)
   (File. (:cert-path config))
   nil))

(defn get-async-unlocker-api
  [config]
  (AsynchronousWalletUnlockerAPI.
   (:host config)
   (:port config)
   (File. (:cert-path config))
   nil))

(defn get-params
  []
  (RegTestParams/get))

(defn get-mainnet-params
  []
  (MainNetParams/get))

(defn get-keychain-group
  []
  (KeyChainGroup/createBasic (get-mainnet-params)))

(defn get-async-api
  [config]
  (AsynchronousLndAPI.
   (:host config)
   (:port config)
   (File. (:cert-path config))
   (File. (:macaroon-path config))))

(defn parse
  [^Message m]
  (json/read-str (.toJsonAsString m false) :key-fn keyword))

(defn list-channels
  [api]
  (let [request (ListChannelsRequest.)]
    (.setActiveOnly request true)
    (parse (.listChannels api request))))

(defn balance-observer
  []
  (reify StreamObserver
    (onNext [this note]
      ;; there will be multiple .onNext here
      (println (parse note)))
    (onError [this err]
      (println err))
    (onCompleted [this]
      (println "onCompleted server")
      ;; there will be no .onNext here
      #_(.onCompleted res))))

(defn list-peers
  [s-lnd-api]
  (parse (.listPeers s-lnd-api false)))

(defn channel-balance
  [s-lnd-api]
  (parse (.channelBalance s-lnd-api)))

(defn channel-balance-a
  [a-lnd-api]
  (.channelBalance a-lnd-api
                   (balance-observer)))

(defn index-invoices
  [api]
  (let [request (ListInvoiceRequest.)]
    (parse (.listInvoices api request))))

(defn get-client
  [url]
  (BitcoinJSONRPCClient. url))

(defn get-pub-key
  [client]
  (:identityPubkey (parse (.getInfo client))))

(defn get-ln-address
  [pubkey host]
  (let [addr (LightningAddress.)]
    (.setPubkey addr pubkey)
    (.setHost addr host)
    addr))

(defn connect-peer
  [client client2]
  (let [pubkey  (get-pub-key client2)
        host    (:host config2)
        addr    (get-ln-address pubkey host)
        perm    false
        timeout 500000000]
    (.connectPeer client addr perm timeout)))

(defn get-lnd-address
  [s-lnd-api]
  (.getAddress (.newAddress s-lnd-api AddressType/WITNESS_PUBKEY_HASH "foo")))

(defn open-channel-request
  [pubkey]
  (let [request (OpenChannelRequest.)]
    (.setNodePubkey request (bcc/hex->bytes pubkey))
    (.setNodePubkeyString request pubkey)
    (.setLocalFundingAmount request 1000000)
    (.setPushSat request 0)
    (.setSatPerByte request 1)
    (.setPrivate request false)
    request))

(defn open-channel
  [client target-client]
  (let [pubkey  (get-pub-key target-client)
        request (open-channel-request pubkey)]
    (.openChannel client request (balance-observer))))

(defn setup-connection
  []
  (let [client2 (get-api config2)]
    (parse (.getInfo client2))))

(defn get-init-wallet
  [mnemonic]
  (let [request (InitWalletRequest.)]
    (.setCipherSeedMnemonic request mnemonic)
    (.setWalletPassword request (bcc/str->bytes "password12345678"))
    request))

(defn write-macaroon
  [f response]
  (with-open [w (io/output-stream f)]
    (.write w (buddy.core.codecs.base64/decode (:adminMacaroon (parse response))))))

(defn init-wallet
  [unlocker1 mnemonic1]
  (let [response1 (.initWallet unlocker1 (get-init-wallet mnemonic1))]
    response1))

(defn get-genesis-block
  []
  (let [block        (.getGenesisBlock (get-mainnet-params))
        transactions (.getTransactions block)
        inputs       (.getInputs (first transactions))]
    (String. (.getScriptBytes (first inputs)))))

(defn get-unlock-request
  []
  (let [request (UnlockWalletRequest.)]
    (.setWalletPassword request (bcc/str->bytes "password12345678"))
    request))

(defn unlock-wallet!
  [config]
  (let [client   (get-async-unlocker-api config)
        request  (get-unlock-request)
        observer (balance-observer)]
    (.unlockWallet client request observer)))

(defn init-wallet!
  [config]
  (let [unlocker                (get-wallet-unlocker config)
        {:keys [name mnemonic]} config
        macaroon-path           (format "admin-%s.macaroon" name)]
    (write-macaroon macaroon-path (init-wallet unlocker mnemonic))))

(comment
  (download-certs)
  (download-macaroons)
  (def client (get-client (get-url)))

  (init-wallet! config1)
  (download-macaroon config1)
  (unlock-wallet! config1)

  (init-wallet! config2)
  (unlock-wallet! config2)
  ;; (def unlocker1 (get-wallet-unlocker config1))
  ;; (def unlocker2 (get-wallet-unlocker config2))
  ;; (write-macaroon (format "admin-%s.macaroon" (:name config1)) (init-wallet unlocker1 (:mnemonic config1)))
  ;; (write-macaroon (format "admin-%s.macaroon" (:name config2)) (init-wallet unlocker2 (:mnemonic config2)))

  (def s-lnd-api (get-api config1))
  (def a-lnd-api (get-async-api config1))
  (def s-lnd-api2 (get-api config2))
  (def a-lnd-api2 (get-async-api config2))
  (def address (get-lnd-address s-lnd-api))
  (dotimes [_ 100] (.generateToAddress client 1 address))
  (parse (.getInfo s-lnd-api))
  (parse (.getInfo s-lnd-api2))
  (connect-peer s-lnd-api s-lnd-api2)
  (list-peers s-lnd-api)
  (list-peers s-lnd-api2)
  (open-channel a-lnd-api s-lnd-api2)
  (dotimes [_ 100] (.generateToAddress client 1 address))
  (channel-balance s-lnd-api)
  (channel-balance s-lnd-api2)
  (parse (.walletBalance s-lnd-api))
  (parse (.walletBalance s-lnd-api2))

  (unlock-wallet! config2)

  (get-wallet-unlocker config1)

  ;; btc node options
  (.getWalletInfo client)
  (.listTransactions client)
  (.generateToAddress client 1 address)

  (parse (.getNetworkInfo s-lnd-api))

  (get-ln-address (get-pub-key s-lnd-api2) (str (:host config2) ":" (:port config2)))

  (list-peers s-lnd-api)
  (index-invoices s-lnd-api)
  (download-macaroons)
  (download-files)

  (first (:channels (list-channels s-lnd-api)))

  (.getTransactions s-lnd-api (int 0) (int 100) "")

  (.getBytes (get-pub-key s-lnd-api2) "UTF-8")
  (bcc/str->bytes (get-pub-key s-lnd-api2))
  (bytes (byte-array (get-pub-key s-lnd-api2)))
  (.length (get-pub-key s-lnd-api2))
  (.isValid (.validate (open-channel-request (get-pub-key s-lnd-api2))))
  (parse (open-channel-request (get-pub-key s-lnd-api2)))
  ;; (def address (.getNewAddress client "foo"))

  (list-peers s-lnd-api)
  (list-peers s-lnd-api2)

  (spit "backups.txt"  (.exportAllChannelBackups s-lnd-api))
  (puget/cprint (parse (.listPayments s-lnd-api false)))

  (.getNumSatoshis (.decodePayReq s-lnd-api payment-request))
  (puget/cprint (parse (.listPayments s-lnd-api false)))

  ;; (def a-lnd-api (get-async-api config))
  (.listPayments a-lnd-api true (long 0) (long 100) false (balance-observer))

  (channel-balance-a a-lnd-api)
  (.listPeers a-lnd-api (balance-observer)))
