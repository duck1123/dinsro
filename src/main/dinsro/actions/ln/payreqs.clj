(ns dinsro.actions.ln.payreqs
  (:require
   [dinsro.queries.ln.payreqs :as q.ln.payreqs]
   [lambdaisland.glogc :as log]))

(defn decode
  [_props]
  (throw (ex-info "not implemented" {})))

(defn submit!
  [_props]
  (throw (ex-info "not implemented" {})))

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.ln.payreqs/delete! id))
