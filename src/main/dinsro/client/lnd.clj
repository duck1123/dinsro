(ns dinsro.client.lnd
  (:require
   [buddy.core.codecs :as bcc]
   [clojure.core.async :as async :refer [>!!]]
   [clojure.data.json :as json]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   io.grpc.stub.StreamObserver
   org.lightningj.lnd.wrapper.AsynchronousLndAPI
   org.lightningj.lnd.wrapper.invoices.AsynchronousInvoicesAPI
   org.lightningj.lnd.wrapper.invoices.message.LookupInvoiceMsg
   org.lightningj.lnd.wrapper.Message
   org.lightningj.lnd.wrapper.message.ConnectPeerRequest
   org.lightningj.lnd.wrapper.message.GetInfoRequest
   org.lightningj.lnd.wrapper.message.GetTransactionsRequest
   org.lightningj.lnd.wrapper.message.Invoice
   org.lightningj.lnd.wrapper.message.LightningAddress
   org.lightningj.lnd.wrapper.message.ListChannelsRequest
   org.lightningj.lnd.wrapper.message.ListInvoiceRequest
   org.lightningj.lnd.wrapper.message.ListPaymentsRequest
   org.lightningj.lnd.wrapper.message.NewAddressRequest
   org.lightningj.lnd.wrapper.message.NodeInfoRequest
   org.lightningj.lnd.wrapper.message.PaymentHash
   org.lightningj.lnd.wrapper.message.PayReqString
   org.lightningj.lnd.wrapper.message.SendRequest
   org.lightningj.lnd.wrapper.walletkit.message.AddrRequest
   org.lightningj.lnd.wrapper.walletunlocker.AsynchronousWalletUnlockerAPI
   org.lightningj.lnd.wrapper.walletunlocker.message.InitWalletRequest
   org.lightningj.lnd.wrapper.walletunlocker.message.UnlockWalletRequest))

(defn client?
  [o]
  (instance? AsynchronousLndAPI o))

(>defn get-client
  [host port cert macaroon]
  [string? number? any? any? => client?]
  (AsynchronousLndAPI. host port cert macaroon))

(>defn get-invoices-client
  [host port cert macaroon]
  [string? number? any? any? => (ds/instance? AsynchronousInvoicesAPI)]
  (AsynchronousInvoicesAPI. host port cert macaroon))

(>defn get-unlocker-client
  [host port cert]
  [string? number? any? => (ds/instance? AsynchronousWalletUnlockerAPI)]
  (AsynchronousWalletUnlockerAPI. host port cert nil))

(defn parse
  [^Message m]
  (json/read-str (.toJsonAsString m false) :key-fn keyword))

(defn ch-observer
  [ch]
  (reify StreamObserver
    (onNext [_this message]
      (let [data (parse message)]
        (log/info :ch-observer/onNext {:data data})
        (>!! ch data)))
    (onError [_this err]
      (log/info :ch-observer/onError {:err err})
      (>!! ch {:error err})
      (async/close! ch))
    (onCompleted [_this]
      (log/info :ch-observer/onCompleted {})
      (async/close! ch))))

(defmacro fetch-async
  [client op request]
  `(let [ch# (async/chan)]
     (~op ~client ~request (ch-observer ch#))
     ch#))

(>defn ->lightning-address
  [host pubkey]
  [string? string? => (partial instance? LightningAddress)]
  (let [address (LightningAddress.)]
    (doto address
      (.setHost host)
      (.setPubkey pubkey))))

(>defn ->invoice
  [value memo]
  [number? string? => (ds/instance? Invoice)]
  (let [invoice (Invoice.)]
    (.setValue invoice value)
    (.setMemo invoice memo)
    invoice))

(defn ->send-request
  [payment-request]
  (let [request (SendRequest.)]
    (.setPaymentRequest request payment-request)
    request))

;; Requests

(>defn ->addr-request
  [account]
  [string? => (ds/instance? AddrRequest)]
  (let [request (AddrRequest.)]
    (.setAccount request account)
    request))

(>defn ->connect-peer-request
  ([address]
   [(partial instance? LightningAddress) => (ds/instance? ConnectPeerRequest)]
   (let [request (ConnectPeerRequest.)]
     (doto request
       (.setAddr address))
     request))
  ([host pubkey]
   [string? string? => (ds/instance? ConnectPeerRequest)]
   (let [address (->lightning-address host pubkey)]
     (->connect-peer-request address))))

(defn ->get-info-request
  []
  (let [request (GetInfoRequest.)]
    request))

(>defn ->get-transactions-request
  []
  [=> (ds/instance? GetTransactionsRequest)]
  (let [request (GetTransactionsRequest.)]
    request))

(defn ->init-wallet-request
  [mnemonic password]
  (let [request (InitWalletRequest.)]
    (.setCipherSeedMnemonic request mnemonic)
    (.setWalletPassword request (bcc/str->bytes password))
    request))

(>defn ->list-channels-request
  []
  [=> (ds/instance? ListChannelsRequest)]
  (let [request (ListChannelsRequest.)]
    (.setActiveOnly request true)
    request))

(>defn ->list-invoice-request
  []
  [=> (ds/instance? ListInvoiceRequest)]
  (let [request (ListInvoiceRequest.)]
    request))

(>defn ->list-payment-request
  []
  [=> (ds/instance? ListPaymentsRequest)]
  (let [request (ListPaymentsRequest.)]
    request))

(>defn ->lookup-invoice-request
  [hash]
  [string? => (ds/instance? LookupInvoiceMsg)]
  (let [request (LookupInvoiceMsg.)]
    (.setPaymentHash request (byte-array (map byte hash)))
    request))

(>defn ->new-address-request
  []
  [=> (ds/instance? NewAddressRequest)]
  (let [request (NewAddressRequest.)]
    request))

(defn ->node-info-request
  [pubkey]
  (let [request (NodeInfoRequest.)]
    (.setPubKey request pubkey)
    (.setIncludeChannels request true)
    request))

(>defn ->pay-req-string
  [hash]
  [string? => (ds/instance? PayReqString)]
  (let [req (PayReqString.)]
    (.setPayReq req hash)
    req))

(>defn ->payment-hash
  [r-hash]
  [string? => (ds/instance? PaymentHash)]
  (let [hash (PaymentHash.)]
    (.setRHashStr hash r-hash)
    hash))

(>defn ->unlock-wallet-request
  [password]
  [string? => (ds/instance? UnlockWalletRequest)]
  (let [request (UnlockWalletRequest.)]
    (.setWalletPassword request (bcc/str->bytes password))
    request))

;; Operations

(>defn add-invoice
  [client value memo]
  [client? number? string? => ds/channel?]
  (fetch-async client .addInvoice (->invoice value memo)))

(>defn connect-peer
  [client host pubkey]
  [client? string? string? => ds/channel?]
  (fetch-async client .connectPeer (->connect-peer-request host pubkey)))

(>defn decode-pay-req
  [client hash]
  [client? string? => ds/channel?]
  (fetch-async client .decodePayReq (->pay-req-string hash)))

(>defn get-info
  [client]
  [client? => ds/channel?]
  (fetch-async client .getInfo (->get-info-request)))

(>defn get-node-info
  [client pubkey]
  [client? string? => ds/channel?]
  (fetch-async client .getNodeInfo (->node-info-request pubkey)))

(>defn get-transactions
  [client]
  [client? => ds/channel?]
  (fetch-async client .getTransactions (->get-transactions-request)))

(>defn list-invoices
  [client]
  [client? => ds/channel?]
  (fetch-async client .listInvoices (->list-invoice-request)))

(>defn list-payments
  [client]
  [client? => ds/channel?]
  (fetch-async client .listPayments (->list-payment-request)))

(>defn lookup-invoice
  [client hash]
  [client? string? => ds/channel?]
  (fetch-async client .lookupInvoice (->payment-hash hash)))

(>defn lookup-invoice-v2
  [client hash]
  [client? string? => ds/channel?]
  (fetch-async client .lookupInvoiceV2 (->lookup-invoice-request hash)))

(defn send-payment-sync
  [client payment-request]
  (fetch-async client .sendPaymentSync (->send-request payment-request)))

(>defn unlock-wallet
  [client password]
  [client? string? => ds/channel?]
  (fetch-async client .unlockWallet (->unlock-wallet-request password)))
