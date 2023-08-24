(ns dinsro.responses.nostr.relays
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.guardrails.core :refer [>def]]
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

;; Fetch

(>def ::fetch!-request
  (s/keys :req [::m.n.relays/id]))

(>def ::fetch!-response-success
  (s/keys :req [::mu/status ::m.n.relays/item]))

(>def ::fetch!-response-error
  (s/keys :req [::mu/status ::mu/errors]))

(>def ::fetch!-response
  (s/or :success ::fetch!-response-success
        :error ::fetch!-response-error))

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

;; Connect

(>def ::connect!-request
  (s/keys :req [::m.n.relays/id]))

(>def ::connect!-response-success
  (s/keys :req [::mu/status ::m.n.relays/item]))

(>def ::connect!-response-error
  (s/keys :req [::mu/status ::mu/errors]))

(>def ::connect!-response
  (s/or :success ::connect!-response-success
        :error ::connect!-response-error))

(defsc ConnectResponse
  [_ _]
  {:initial-state {::mu/status       :initial
                   ::m.n.relays/item {}
                   ::mu/errors       {}}
   :ident         (fn [] [:responses/id ::ConnectReponse])
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   {::m.n.relays/item (comp/get-query RelayData)}
                   ::mu/status]})
