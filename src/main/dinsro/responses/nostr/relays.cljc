(ns dinsro.responses.nostr.relays
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]))

(def model-key ::m.n.relays/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

(defsc FetchResponse
  [_ _]
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

(defsc RelayData
  [_ _]
  {:query [::m.n.relays/id ::m.n.relays/address ::m.n.relays/connected]
   :ident ::m.n.relays/id})

(defsc ConnectResponse
  [_ _]
  {:initial-state {::mu/status       :initial
                   ::m.n.relays/item {}
                   ::mu/errors       {}}
   :ident         (fn [] [:responses/id ::ConnectReponse])
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   {::m.n.relays/item (comp/get-query RelayData)}
                   ::mu/status]})
