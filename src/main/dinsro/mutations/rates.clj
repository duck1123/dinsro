(ns dinsro.mutations.rates
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.model.rates :as m.rates]
   [dinsro.queries.rates :as q.rates]
   [taoensso.timbre :as log]))

(defn do-create
  [_params]
  {})

(defn do-delete
  [id]
  (q.rates/delete-record id)
  {:status :success})

(defmutation create!
  [_env params]
  {::pc/params #{::m.rates/value}
   ::pc/output [:status
                :items [::m.rates/id]]}
  (do-create params))

(defmutation delete!
  [_env {::m.rates/keys [id]}]
  {::pc/params #{::m.rates/id}
   ::pc/output [:status]}
  (do-delete id))

(def resolvers
  [create! delete!])
