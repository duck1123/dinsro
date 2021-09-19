(ns dinsro.mutations.currencies
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.mutations :as fm]
   #?(:clj [com.fulcrologic.guardrails.core :refer [>defn =>]])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.currencies :as m.currencies]
   #?(:clj [dinsro.queries.currencies :as q.currencies])
   #?(:clj [dinsro.queries.users :as q.users])
   [taoensso.timbre :as log]))

(comment ::m.currencies/_ ::pc/_ ::fm/_)

(s/def ::created-currency (s/coll-of ::m.currencies/ident))
(s/def ::status #{:success :failure :no-user})
(s/def ::creation-response (s/keys :req-un [::status ::created-currency]))

#?(:clj
   (>defn do-create
     [code name identity]
     [::m.currencies/code ::m.currencies/name string? => ::creation-response]
     (if-let [_user-eid (q.users/find-eid-by-name identity)]
       (let [_can-create? true ;; should be admin
             params       #::m.currencies{:code code
                                          :name name}]
         (if-let [record (q.currencies/create-record params)]
           {:status           :success
            :created-currency [(m.currencies/ident record)]}
           (do
             (log/warn "failed to create currency")
             {:status           :failure
              :created-currency []})))
       {:status :no-user})))

#?(:clj
   (defn do-delete
     [id]
     (let [eid (q.currencies/find-eid-by-id id)]
       (q.currencies/delete-record eid))
     {:status :success}))

#?(:clj
   (pc/defmutation create!
     [{{{:keys [identity]} :session} :request} {::m.currencies/keys [id name]}]
     {::pc/params #{::m.currencies/id ::m.currencies/name}
      ::pc/output [:status
                   :created-currency [::m.currencies/id]]}
     (do-create id name identity))
   :cljs
   (fm/defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation delete!
     [_request {::m.currencies/keys [id]}]
     {::pc/params #{::m.currencies/id}
      ::pc/output [:status :message]}
     (do-delete id))
   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers
     [create!
      delete!]))
