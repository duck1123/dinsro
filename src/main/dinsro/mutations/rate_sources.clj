(ns dinsro.mutations.rate-sources
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [taoensso.timbre :as timbre]))

(defn do-create
  [params]
  (if-let [record (q.rate-sources/create-record params)]
    {:status :success
     :item   [(m.rate-sources/ident (:db/id record))]}
    {:status :failure}))

(defn do-delete
  [id]
  (q.rate-sources/delete-record id)
  {:status :success})

(defmutation create!
  [_env params]
  {::pc/params #{:name :url :currency-id}
   ::pc/output [:status {:item [::m.rate-sources/id]}]}
  (do-create params))

(defmutation delete!
  [_request {::m.rate-sources/keys [id]}]
  {::pc/params #{::m.rate-sources/id}
   ::pc/output [:status]}
  (do-delete id))

(def resolvers [create! delete!])
