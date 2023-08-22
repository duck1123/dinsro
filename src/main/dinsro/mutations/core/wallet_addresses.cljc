(ns dinsro.mutations.core.wallet-addresses
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.wallet-addresses :as a.c.wallet-addresses])
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses])
   #?(:clj [dinsro.processors.core.wallet-addresses :as p.c.wallet-addresses])
   [dinsro.responses.core.wallet-addresses :as r.c.wallet-addresses]
   [lambdaisland.glogc :as log]))

;; [[../../actions/core/wallet_addresses.clj]]

(def model-key ::m.c.wallet-addresses/id)

#?(:cljs (comment ::pc/_ ::log/_ ::mu/_))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::r.c.wallet-addresses/deleted-records]}
     (p.c.wallet-addresses/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.c.wallet-addresses/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.c.wallet-addresses/DeleteResponse))))

;; Generate

#?(:clj
   (pc/defmutation generate!
     [_env {::m.c.wallet-addresses/keys [id]}]
     {::pc/params #{::m.c.wallet-addresses/id}
      ::pc/output [::mu/status]}
     (log/info :generate!/starting {:id id})
     (let [node (q.c.wallet-addresses/read-record id)]
       (a.c.wallet-addresses/generate! node)
       {::mu/status :ok}))

   :cljs
   (fm/defmutation generate! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers [delete! generate!]))
