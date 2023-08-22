(ns dinsro.mutations.core.nodes
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.merge :as merge])
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.nodes :as a.c.nodes])
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.core.nodes :as p.c.nodes])
   #?(:clj [dinsro.queries.core.nodes :as q.c.nodes])
   [dinsro.responses.core.nodes :as r.c.nodes]
   [lambdaisland.glogc :as log]))

;; [[../../processors/core/nodes.clj]]
;; [[../../../../notebooks/dinsro/mutations/core/nodes_notebook.clj]]

(def model-key ::m.c.nodes/id)

#?(:clj (comment ::r.c.nodes/_ ::log/_))
#?(:cljs (comment ::mu/_ ::pc/_))

;; Connect

#?(:clj
   (pc/defmutation connect!
     [_env {::m.c.nodes/keys [id]}]
     {::pc/params #{::m.c.nodes/id}
      ::pc/output [::mu/status ::m.c.nodes/item]}
     (let [node     (q.c.nodes/read-record id)
           response (a.c.nodes/update-blockchain-info! node)]
       {::mu/status         :ok
        :response           response
        ::m.c.nodes/item nil}))

   :cljs
   (fm/defmutation connect! [_props]
     (action [_env] true)
     (remote [env]
       (fm/returning env r.c.nodes/ConnectResponse))))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::mu/errors ::r.c.nodes/deleted-records]}
     (p.c.nodes/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.c.nodes/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.c.nodes/DeleteResponse))))

;; Fetch

#?(:cljs
   (defn handle-fetch
     [{:keys [state] :as env}]
     (let [body                                        (get-in env [:result :body])
           response                                    (get body `fetch!)
           {:com.fulcrologic.rad.pathom/keys [errors]} response]
       (if errors
         (do
           (log/error :handle-fetch/errored {:errors errors})
           {})
         (let [status (:dinsro.mutations/status response)]
           (if (= status :error)
             (let [errors (:dinsro.mutations/errors response)]
               (log/info :handle-fetch/errored {:response response :errors errors})
               {})
             (do
               (log/info :handle-fetch/completed {:response response})
               (let [{::m.c.nodes/keys [item]} response
                     {::m.c.nodes/keys [id]}   item]
                 (swap! state #(merge/merge-ident % [::m.c.nodes/id id] item))
                 {}))))))))

#?(:clj
   (pc/defmutation fetch!
     [_env props]
     {::pc/params #{::m.c.nodes/id}
      ::pc/output [::mu/status
                   ::mu/errors
                   ::m.c.nodes/item]}
     (p.c.nodes/fetch! props))

   :cljs
   (fm/defmutation fetch! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env r.c.nodes/FetchResponse))
     (ok-action [env]  (handle-fetch env))))

;; Generate

#?(:clj
   (pc/defmutation generate!
     [_env props]
     {::pc/params #{::m.c.nodes/id}
      ::pc/output [::mu/status ::mu/errors ::m.c.nodes/item]}
     (p.c.nodes/generate! props))

   :cljs
   (fm/defmutation generate! [_props]
     (action [_env] true)
     (remote [_env] true)))

;; Fetch Peers

#?(:clj
   (pc/defmutation fetch-peers!
     [_env params]
     {::pc/params #{::m.c.nodes/id}
      ::pc/output [::mu/status]}
     (p.c.nodes/fetch-peers! params))

   :cljs
   (fm/defmutation fetch-peers! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers [connect! delete! fetch! generate! fetch-peers!]))
