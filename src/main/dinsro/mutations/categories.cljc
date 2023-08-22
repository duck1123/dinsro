(ns dinsro.mutations.categories
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.categories :as m.categories]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.categories :as p.categories])
   [dinsro.responses.categories :as r.categories]))

;; [[../joins/categories.cljc]]
;; [[../model/categories.cljc]]
;; [[../processors/categories.clj]]
;; [[../responses/categories.cljc]]
;; [[../ui/categories.cljs]]

(def id-key ::m.categories/id)

#?(:cljs (comment ::mu/_ ::pc/_))

#?(:clj
   (pc/defmutation create!
     [env props]
     {::pc/params #{::m.categories/id}
      ::pc/output [::mu/status ::mu/errors ::r.categories/created-record]}
     (p.categories/delete! env props))

   :cljs
   (fm/defmutation create! [_props]
     (action [_env] true)
     (ok-action [env]
       (let [body     (get-in env [:result :body])
             response (get body `create!)]
         response))
     (remote [env]
       (fm/returning env r.categories/CreateResponse))))

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.categories/id}
      ::pc/output [::mu/status ::mu/errors ::r.categories/deleted-records]}
     (p.categories/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.categories/deleted-records])]
         (swap! state fns/remove-entity [id-key (id-key record)])))
     (remote [env]
       (fm/returning env r.categories/DeleteResponse))))

#?(:clj (def resolvers [create! delete!]))
