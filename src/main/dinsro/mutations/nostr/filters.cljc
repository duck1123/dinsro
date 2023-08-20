(ns dinsro.mutations.nostr.filters
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.filters :as p.n.filters])
   [dinsro.responses.nostr.filters :as r.n.filters]))

;; [[../../processors/nostr/filters.clj]]
;; [[../../responses/nostr/filters.cljc]]
;; [[../../ui/nostr/filters.cljc]]

(def model-key ::m.n.filters/id)

#?(:cljs (comment ::mu/_ ::pc/_))

#?(:clj
   (pc/defmutation add-filter! [_env props]
     {::pc/params #{::m.n.filters/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.filters/item]}
     (p.n.filters/add-filters! props))

   :cljs
   (fm/defmutation add-filter! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation delete! [_env props]
     {::pc/params #{::m.n.filters/id}
      ::pc/output [::mu/status ::mu/errors ::r.n.filters/deleted-records]}
     (p.n.filters/delete! props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.n.filters/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.n.filters/DeleteResponse))))

#?(:clj (def resolvers [add-filter! delete!]))
