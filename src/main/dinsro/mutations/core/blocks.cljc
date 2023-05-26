(ns dinsro.mutations.core.blocks
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.blocks :as a.c.blocks])
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.users :as m.users]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.queries.core.blocks :as q.c.blocks])
   #?(:clj [dinsro.queries.core.nodes :as q.c.nodes])
   [dinsro.responses.core.blocks :as r.c.blocks]
   [lambdaisland.glogc :as log]))

#?(:clj (comment ::r.c.blocks/_))
#?(:cljs (comment ::pc/_ ::m.c.blocks/_ ::m.users/_ ::mu/_))

#?(:clj
   (pc/defmutation delete!
     [_env props]
     {::pc/params #{::m.c.blocks/id}
      ::pc/output [::mu/status]}
     (a.c.blocks/do-delete! props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation fetch!
     [{user-id ::m.users/id} {block-id ::m.c.blocks/id}]
     {::pc/params #{::m.c.blocks/id}
      ::pc/output [::mu/status]}
     (if-let [node-id (first (q.c.nodes/find-by-user user-id))]
       (a.c.blocks/do-fetch! node-id block-id)
       (throw (ex-info "no node id" {}))))

   :cljs
   (defmutation fetch! [_props]
     (action [_env] true)
     (remote [env] (fm/returning env r.c.blocks/FetchResponse))))

#?(:clj
   (pc/defmutation fetch-transactions!
     [_env {::m.c.blocks/keys [id]}]
     {::pc/params #{::m.c.blocks/id}
      ::pc/output [::mu/status]}

     (if-let [block (q.c.blocks/read-record id)]
       (a.c.blocks/fetch-transactions! block)
       (do
         (log/error :block/not-found {})
         {:status  :failed
          :message "No block"})))

   :cljs
   (defmutation fetch-transactions! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation search!
     [_env props]
     {::pc/params #{::m.c.blocks/id}
      ::pc/output [::mu/status]}
     (a.c.blocks/do-search! props))

   :cljs
   (defmutation search! [_props]
     (action [_env] true)
     (error-action [env]
       (log/info :search/error {:env env}))
     (ok-action [{:keys [result]
                  :as   env}]
       (let [{:keys [body]} result
             data           (get body `search!)]
         (log/info :search/completed (merge
                                      {:body   body
                                       :data   data
                                       :result result}
                                      (when false {:env env})))))

     (remote [env]
       (fm/returning env r.c.blocks/SearchResponse))))

#?(:clj (def resolvers [delete! fetch! fetch-transactions! search!]))
