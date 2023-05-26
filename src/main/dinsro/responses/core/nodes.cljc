(ns dinsro.responses.core.nodes
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.guardrails.core :refer #?(:clj [>def =>] :cljs [>def])]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.mutations :as mu]))

(>def ::item ::m.c.nodes/item)
(>def ::creation-response (s/keys :req [::mu/status ::mu/errors ::m.c.nodes/item]))

(defsc ConnectResponse
  [_ _]
  {:initial-state {::m.c.nodes/item nil
                   ::mu/status      :initial
                   ::mu/errors      {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::m.c.nodes/item]})

(>def ::fetch!-request
  (s/keys :req [::m.c.nodes/id]))

(>def ::fetch!-response-success
  (s/keys :req [::mu/status ::m.c.nodes/item]))

(>def ::fetch!-response-error
  (s/keys :req [::mu/status ::mu/errors]))

(>def ::fetch!-response
  (s/or :success ::fetch!-response-success
        :error ::fetch!-response-error))

(defsc FetchResponse
  [_ _]
  {:initial-state {::m.c.nodes/item nil
                   ::mu/status      :initial
                   ::mu/errors      {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::m.c.nodes/item]})

(>def ::generate!-request (s/keys :req [::m.c.nodes/id]))
(>def ::generate!-response (s/keys :req [::mu/status] :opt [::mu/errors]))
