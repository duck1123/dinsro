(ns dinsro.responses.nostr.runs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.mutations :as mu]))

;; [[../../mutations/nostr/runs.cljc]]

(def model-key ::m.n.runs/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
