(ns dinsro.helm.lnd-test
  (:require
   [dinsro.helm.lnd :as h.lnd]
   [nextjournal.devcards :as dc]
   [nextjournal.viewer :as viewer :refer [inspect]]))

(def provided-options
  {:name  "alice"
   :alias "Node Alice"
   :rpc   {:host "foo" :password "bar" :user "alice"}})

(dc/defcard provided-options-card [] [inspect provided-options])

(dc/defcard site-config [] [inspect 42])

(def bitcoin-options
  (h.lnd/merge-defaults provided-options))

(dc/defcard bitcoin-options-card [] [inspect bitcoin-options])

(dc/defcard bitcoin-section []
  [:pre [:code (str (h.lnd/bitcoin-section bitcoin-options))]])

(dc/defcard ao-section []
  [:pre [:code (str (h.lnd/ao-section bitcoin-options))]])

(dc/defcard bitcoind-section []
  [:pre [:code (str (h.lnd/bitcoind-section bitcoin-options))]])

(dc/defcard ->lnd-config []
  [:pre [:code (str (h.lnd/->lnd-config bitcoin-options))]])

(dc/defcard ->values []
  [inspect (h.lnd/->values bitcoin-options)])

(dc/defcard ->values-yaml []
  [:pre (h.lnd/->values-yaml {:name "alice"})])

;; # Headline
