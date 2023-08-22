(ns dinsro.responses.ln.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.mutations :as mu]))

;; [[../../mutations/ln/peers.cljc]]

(def model-key ::m.ln.peers/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
