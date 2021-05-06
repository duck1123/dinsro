(ns dinsro.mutations.users
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defn do-delete
  [username]
  (q.users/delete-record username)
  {:status :success})

(defmutation delete!
  [_request {::m.users/keys [username]}]
  {::pc/params #{::m.users/username}
   ::pc/output [:status]}
  (do-delete username))

(def resolvers
  [delete!])
