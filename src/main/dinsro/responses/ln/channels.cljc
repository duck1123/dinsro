(ns dinsro.responses.ln.channels
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.mutations :as mu]))

;; [[../../mutations/ln/channels.cljc]]

(def model-key ::m.ln.channels/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
