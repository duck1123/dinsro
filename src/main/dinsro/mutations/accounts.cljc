(ns dinsro.mutations.accounts
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.accounts :as p.accounts])
   [dinsro.responses.accounts :as r.accounts]))

;; [[../processors/accounts.clj]]
;; [[../responses/accounts.cljc]]
;; [[../ui/accounts.cljc]]

(def model-key ::m.accounts/id)

#?(:cljs (comment ::mu/_ ::pc/_))

;; Create

#?(:clj
   (pc/defmutation create!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::mu/errors]}
     (p.accounts/create! env props))

   :cljs
   (defmutation create! [_props]
     (action [_env] true)
     (remote [env]
       (fm/returning env r.accounts/CreateResponse))))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [_env props]
     {::pc/params #{::m.accounts/id}
      ::pc/output [::mu/status ::mu/errors ::r.accounts/deleted-records]}
     (p.accounts/delete! props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.accounts/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.accounts/DeleteResponse))))

#?(:clj (def resolvers [create! delete!]))
