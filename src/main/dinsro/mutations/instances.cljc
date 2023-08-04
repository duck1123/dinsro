(ns dinsro.mutations.instances
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.instances :as m.instances]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.instances :as p.instances])
   [dinsro.responses.instances :as r.instances]))

;; [[../model/instances.cljc]]
;; [[../processors/instances.cljc]]
;; [[../responses/instances.cljc]]
;; [[../../../notebooks/dinsro/notebooks/instances_notebook.clj]]

(def model-key ::m.instances/id)

#?(:cljs (comment ::mu/_ ::pc/_))

#?(:clj
   (pc/defmutation beat!
     [env props]
     {::pc/params #{::m.instances/id}
      ::pc/output [::mu/status ::mu/errors ::r.instances/item]}
     (p.instances/beat! env props))

   :cljs
   (fm/defmutation beat! [_props]
     (action [_env] true)
     ;; (ok-action [{:keys [state] :as env}] true)
     (remote [env]
       (fm/returning env r.instances/BeatResponse))))

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.instances/id}
      ::pc/output [::mu/status ::mu/errors ::r.instances/deleted-records]}
     (p.instances/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.instances/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.instances/DeleteResponse))))

#?(:clj (def resolvers [beat! delete!]))
