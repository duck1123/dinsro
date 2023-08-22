(ns dinsro.mutations.currencies
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.currencies :as p.currencies])
   [dinsro.responses.currencies :as r.currencies]))

;; [[../processors/currencies.clj]]
;; [[../responses/currencies.cljc]]

(def id-key ::m.currencies/id)

#?(:cljs (comment ::pc/_ ::mu/_))

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.currencies/id}
      ::pc/output [::mu/status ::mu/errors ::r.currencies/deleted-records]}
     (p.currencies/delete! env props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.currencies/deleted-records])]
         (swap! state fns/remove-entity [id-key (id-key record)])))
     (remote [env]
       (fm/returning env r.currencies/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
