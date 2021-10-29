(ns dinsro.mutations.categories
  (:require
   [clojure.spec.alpha :as s]
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [com.fulcrologic.guardrails.core :refer [>defn =>]])
   [com.fulcrologic.fulcro.mutations :as fm]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.categories :as q.categories])
   #?(:clj [dinsro.queries.users :as q.users])
   [taoensso.timbre :as log]))

(comment ::m.categories/_ ::m.users/_ ::pc/_ ::fm/_)

(s/def ::created-category (s/coll-of ::m.categories/ident))
(s/def ::status #{:success :failure :no-user})
(s/def ::creation-response
  (s/keys :req-un [::status]
          :opt-un [::created-category]))

#?(:clj
   (>defn do-create
     [identity name]
     [::m.users/id ::m.categories/name => ::creation-response]
     (if-let [_user-eid (q.users/find-eid-by-id identity)]
       (let [params {::m.categories/name name
                     ::m.categories/user identity}]
         (if-let [record (q.categories/create-record params)]
           {:status           :success
            :created-category [(m.categories/ident record)]}
           {:status :failure}))
       {:status :no-user})))

#?(:clj
   (pc/defmutation create!
     [{{{:keys [identity]} :session} :request} {::m.categories/keys [name]}]
     {::pc/params #{::m.categories/name}
      ::pc/output [:status
                   :created-category [::m.categories/id]]}
     (do-create identity name))
   :cljs
   (fm/defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (defn do-delete
     [id]
     (or (when-not (or (nil? id) (empty? id))
           (when-let [eid (q.categories/find-eid-by-id id)]
             (q.categories/delete-record eid)
             {:status :success}))
         {:status :failure})))

#?(:clj
   (pc/defmutation delete!
     [_request {::m.categories/keys [id]}]
     {::pc/params #{::m.categories/id}
      ::pc/output [:status :message]}
     (do-delete id))
   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers []))
