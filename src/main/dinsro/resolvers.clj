(ns dinsro.resolvers
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.session :as mu.session]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as log]))

(defresolver current-user-resolver
  [_env props]
  {::pc/output
   [{:session/current-user
     [:user/username
      {:user/ref [::m.users/id]}
      :user/valid?]}]}
  (log/spy :info props)
  (let [username m.users/default-username
        user-id  (q.users/find-eid-by-name m.users/default-username)]
    {:session/current-user
     {:user/username username
      :user/ref      {::m.users/id user-id}
      :user/valid?   true}}))

(defresolver current-user-ref-resolver
  [{{{:keys [identity]} :session} :ring/request} _props]
  {::pc/output
   [{:session/current-user-ref [::m.users/id]}]}
  (if-let [user-id (and identity (q.users/find-eid-by-name identity))]
    {:session/current-user-ref {::m.users/id user-id}}
    {:session/current-user-ref nil}))

(def resolvers
  [current-user-ref-resolver
   mu.session/resolvers])
