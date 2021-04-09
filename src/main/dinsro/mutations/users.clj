(ns dinsro.mutations.users
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defmutation delete!
  [_request {::m.users/keys [id]}]
  {::pc/params #{::m.users/id}
   ::pc/output [:status]}
  (q.users/delete-record id)
  {:status :success})

(def resolvers
  [delete!])
