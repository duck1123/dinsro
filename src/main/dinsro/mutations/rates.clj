(ns dinsro.mutations.rates
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.model.rates :as m.rates]
   [dinsro.queries.rates :as q.rates]
   [taoensso.timbre :as timbre]))

(defmutation create!
  [_env _params]
  {::pc/params #{::m.rates/value}
   ::pc/output [:status
                :items [::m.rates/id]]}
  {})

(defmutation delete!
  [_env {::m.rates/keys [id]}]
  {::pc/params #{::m.rates/id}
   ::pc/output [:status]}
  (q.rates/delete-record id)
  {:status :success})

(def resolvers
  [create! delete!])
