(ns dinsro.mutations.rate-sources
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.actions.rate-sources :as a.rate-sources]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [taoensso.timbre :as timbre]))

(defmutation create!
  [_env params]
  {::pc/params #{:name :url :currency-id}
   ::pc/output [:status
                {:item [::m.rate-sources/id]}]}
  (if-let [record (a.rate-sources/create! params)]
    {:status :success
     :item   [{::m.rate-sources/id (:db/id record)}]}
    {:status :failure}))

(defmutation delete!
  [_request {::m.rate-sources/keys [id]}]
  {::pc/params #{::m.rate-sources/id}
   ::pc/output [:status]}
  (q.rate-sources/delete-record id)
  {:status :success})

(def resolvers [create! delete!])
