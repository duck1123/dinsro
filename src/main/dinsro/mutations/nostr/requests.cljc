(ns dinsro.mutations.nostr.requests
  (:refer-clojure :exclude [run!])
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.requests :as p.n.requests])
   [dinsro.responses.nostr.requests :as r.n.requests]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/requests.clj]]
;; [[../../processors/nostr/requests.clj]]

(def model-key ::m.n.requests/id)

#?(:clj ::log/_)
#?(:cljs (comment ::pc/_ ::mu/_))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.n.requests/id}
      ::pc/output [::mu/status ::mu/errors ::r.n.requests/deleted-records]}
     (p.n.requests/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.n.requests/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.n.requests/DeleteResponse))))

;; Edit

#?(:cljs
   (fm/defmutation edit [props]
     (action [env]
       (log/info :edit/starting {:props props :env env})
       true)))

;; Run

#?(:clj
   (pc/defmutation run! [_env props]
     {::pc/params #{::m.n.requests/id}
      ::pc/output [::status ::errors]}
     (p.n.requests/run! props))

   :cljs
   (fm/defmutation run! [_props]
     (action [_env] true)
     (remote [_env]  true)))

#?(:clj
   (pc/defmutation start! [_env props]
     {::pc/params #{::m.n.requests/id}
      ::pc/output [::status ::errors]}
     (p.n.requests/start! props))

   :cljs
   (fm/defmutation start! [_props]
     (action [_env] true)
     (remote [_env]  true)))

#?(:clj
   (def resolvers
     [delete! run! start!]))
