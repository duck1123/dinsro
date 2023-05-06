(ns dinsro.mutations.rate-sources
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.rate-sources :as a.rate-sources])
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.rate-sources :as p.rate-sources])
   [dinsro.responses.rate-sources :as r.rate-sources]
   #?(:cljs [lambdaisland.glogc :as log])))

;; [../actions/rate_sources.clj]
;; [../processors/rate_sources.clj]
;; [../responses/rate_sources.cljc]

#?(:cljs (comment ::m.rate-sources/_ ::mu/_ ::pc/_))

#?(:clj
   (defn do-run!
     [id]
     (a.rate-sources/run-query! id)
     {::mu/status :success}))

#?(:clj
   (pc/defmutation create!
     [_request {::m.rate-sources/keys [id]}]
     {::pc/params #{::m.rate-sources/id}
      ::pc/output [::mu/status ::mu/errors ::r.rate-sources/deleted-records]}
     (p.rate-sources/create! id))
   :cljs
   (fm/defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.rate-sources/id}
      ::pc/output [::mu/status
                   ::mu/errors
                   {::r.rate-sources/deleted-records [::m.rate-sources/id]}]}
     (p.rate-sources/delete! env props))
   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [env]
       (let [{:keys [state]}                           env
             body                                      (get-in env [:result :body])
             response                                  (get body `delete!)
             {::r.rate-sources/keys [deleted-records]} response]
         (doseq [rate-source deleted-records]
           (log/info :delete!/deleted {:rate-source rate-source})
           (let [rate-source-id (::m.rate-sources/id rate-source)
                 target-ident   [::m.rate-sources/id rate-source-id]]
             (swap! state fns/remove-entity target-ident)))
         response))
     (remote [env]  (fm/returning env r.rate-sources/DeleteResponse))))

#?(:clj
   (pc/defmutation run-query!
     [_request {::m.rate-sources/keys [id]}]
     {::pc/params #{::m.rate-sources/id}
      ::pc/output [::mu/status]}
     (do-run! id))
   :cljs
   (fm/defmutation run-query! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers [create! delete! run-query!]))
