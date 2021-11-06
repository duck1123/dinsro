(ns dinsro.mutations.core-nodes
  (:require
   [clojure.spec.alpha :as s]
   #?(:clj [com.fulcrologic.guardrails.core :refer [>defn =>]])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.bitcoind :as a.bitcoind])
   [dinsro.model.core-nodes :as m.core-nodes]
   #?(:clj [dinsro.queries.core-nodes :as q.core-nodes])
   [taoensso.timbre :as log]))

(comment ::m.core-nodes/_ ::pc/_)

(s/def ::creation-response (s/keys))

#?(:clj
   (pc/defmutation connect!
     [_env {::m.core-nodes/keys [id]}]
     {::pc/params #{::m.core-nodes/id}
      ::pc/output [:status]}
     (let [node (q.core-nodes/read-record id)]
       (a.bitcoind/update-info! node)
       (a.bitcoind/update-blockchain-info! node)))

   :cljs
   (defmutation connect! [_props]
     (action [_env] true)
     (remote [_env] true)
     (ok-action [{:keys [state] :as env}]
       (let [body (get-in env [:result :body])]
         (get body `connect!)
         {}))))

#?(:clj
   (>defn do-create
     [params]
     [::m.core-nodes/params => ::m.core-nodes/id]
     (q.core-nodes/create-record params)))

#?(:cljs
   (defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers [connect!]))
