(ns dinsro.responses.ln.nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.mutations :as mu]))

;; [[../../mutations/ln/nodes.cljc]]

(def model-key ::m.ln.nodes/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
(defsc NodeCert
  [_this _props]
  {:query [::m.ln.nodes/hasCert?
           ::m.ln.nodes/id
           :com.fulcrologic.fulcro.algorithms.form-state/config]
   :ident ::m.ln.nodes/id})

(defsc NodeMacaroonResponse
  [_this _props]
  {:query [::m.ln.nodes/hasMacaroon?
           ::m.ln.nodes/id
           ::mu/status]
   :ident ::m.ln.nodes/id})

(defsc PeerResponse
  [_this _props]
  {:query         [::mu/status]
   :initial-state {::mu/status :initial}})
