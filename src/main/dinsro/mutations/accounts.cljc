(ns dinsro.mutations.accounts
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   #?(:clj [dinsro.queries.accounts :as q.accounts])
   [taoensso.timbre :as log]))

#?(:cljs (comment ::pc/_ ::m.accounts/_ ::m.currencies/_))

#?(:clj
   (defn do-create
     [name currency-id user-id initial-value]
     (let [params {::m.accounts/name          name
                   ::m.accounts/currency      currency-id
                   ::m.accounts/initial-value initial-value
                   ::m.accounts/user          user-id}]
       (if-let [eid (q.accounts/create-record params)]
         (when-let [record (q.accounts/read-record eid)]
           {:status       :success
            :created-item (m.accounts/ident-item record)})
         {:status :failure}))))

#?(:clj
   (defn do-delete
     [id]
     (q.accounts/delete-record id)
     {:status :success}))

#?(:clj
   (pc/defmutation create!
     [{{{:keys [identity]} :session} :request} params]
     {::pc/params #{::m.accounts/name}
      ::pc/output [:status
                   :created-item [::m.accounts/id]]}
     (let [{::m.accounts/keys               [name initial-value]
            {currency-id ::m.currencies/id} ::m.accounts/currency} params]
       (do-create name currency-id identity initial-value)))
   :cljs
   (fm/defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation delete!
     [_env {::m.accounts/keys [id]}]
     {::pc/params #{::m.accounts/id}
      ::pc/output [:status]}
     (do-delete id))
   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers []))
