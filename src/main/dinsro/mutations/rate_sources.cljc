(ns dinsro.mutations.rate-sources
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.rate-sources :as a.rate-sources])
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.rate-sources :as p.rate-sources])
   [dinsro.responses.rate-sources :as r.rate-sources]))

;; [../actions/rate_sources.clj]
;; [../processors/rate_sources.clj]
;; [../responses/rate_sources.cljc]

(def id-key ::m.rate-sources/_)

#?(:cljs (comment ::mu/_ ::pc/_))

#?(:clj
   (defn do-run!
     [id]
     (a.rate-sources/run-query! id)
     {::mu/status :success}))

;; Create

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

;; Delete

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
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.rate-sources/deleted-records])]
         (swap! state fns/remove-entity [id-key (id-key record)])))
     (remote [env]  (fm/returning env r.rate-sources/DeleteResponse))))

;; Run Query

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
