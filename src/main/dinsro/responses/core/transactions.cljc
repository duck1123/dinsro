(ns dinsro.responses.core.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.guardrails.core :refer [>def => ?]]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.mutations :as mu]))

;; [[../../actions/core/transactions.clj]]
;; [[../../mutations/core/transactions.cljc]]

(def model-key ::m.c.transactions/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

(>def ::fetch-result (s/keys))

(defsc FetchResponse
  [_ _]
  {:initial-state {::m.c.transactions/item {}}
   :query         [::mu/status
                   ::m.c.transactions/item]})

(defsc SearchResponse
  [_ _]
  {:initial-state {::mu/status             :initial
                   :tx-id                  nil
                   :node                   nil
                   :tx                     nil
                   ::m.c.transactions/item {}}
   :query         [::mu/status
                   :tx-id
                   :node
                   :tx
                   ::m.c.transactions/item]})
