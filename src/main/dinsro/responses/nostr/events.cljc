(ns dinsro.responses.nostr.events
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.guardrails.core :refer #?(:clj [>def =>] :cljs [>def])]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.mutations :as mu]))

;; [[../../mutations/nostr/events.cljc]]

(def model-key ::m.n.events/id)

;; Delete

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
  (s/keys :req [::m.n.events/id]))

(>def ::fetch!-response-success
  (s/keys :req [::mu/status ::m.n.events/item]))

(>def ::fetch!-response-error
  (s/keys :req [::mu/status ::mu/errors]))

(>def ::fetch!-response
  (s/or :success ::fetch!-response-success
        :error ::fetch!-response-error))

(defsc FetchResponse
  [_ _]
  {:initial-state {::mu/status      :initial
                   ::mu/errors      {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
