(ns dinsro.mutations.core.tx
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.tx :as a.c.tx])
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   #?(:clj [dinsro.queries.core.nodes :as q.c.nodes])
   #?(:clj [dinsro.queries.core.tx :as q.c.tx])
   [lambdaisland.glogc :as log]))

(comment
  ::m.c.blocks/_
  ::m.c.nodes/_
  ::m.c.tx/_
  ::pc/_)

(defsc FetchResponse
  [_ _]
  {:initial-state {::m.c.tx/item {}}
   :query [:status
           ::m.c.tx/item]})

(defsc SearchResponse
  [_ _]
  {:initial-state {:status :initial
                   :tx-id nil
                   :node nil
                   :tx nil
                   ::m.c.tx/item {}}
   :query         [:status
                   :tx-id
                   :node
                   :tx
                   ::m.c.tx/item]})

#?(:clj
   (pc/defmutation fetch!
     [_env props]
     {::pc/params #{::m.c.tx/id}
      ::pc/output [:status]}
     (a.c.tx/fetch! props))

   :cljs
   (defmutation fetch! [_props]
     (action [_env] true)
     (remote [env]
       (fm/returning env FetchResponse))))

#?(:clj
   (defn do-search!
     [props]
     (log/info :tx/searching {:props props})
     (let [{tx-id   ::m.c.tx/tx-id
            node-id ::m.c.tx/node} props]
       (log/info :search/started {:tx-id tx-id :node-id node-id})
       (let [result (a.c.tx/search! props)]
         (log/info :search/result {:result result})
         (if result
           {:status  :passed
            :tx  result
            :tx-id   tx-id
            :node-id node-id}
           {:status  :failed
            :tx  result
            :tx-id   tx-id
            :node-id node-id})))))

#?(:clj
   (pc/defmutation search!
     [_env props]
     {::pc/params #{::m.c.tx/id}
      ::pc/output [:status]}
     (do-search! props))

   :cljs
   (defmutation search! [_props]
     (action [_env] true)
     (error-action [env]
       (log/info :search/error {:env env}))
     (ok-action [{:keys [result]
                  :as env}]
       (let [{:keys [body]} result
             data (get body `search!)]
         (log/info :search/completed (merge
                                      {:body body
                                       :data data
                                       :result result}
                                      (when false {:env env})))))

     (remote [env]
       (fm/returning env SearchResponse))))

#?(:clj (def resolvers [fetch! search!]))

#?(:clj
   (comment
     (def node-alice (q.c.nodes/read-record (q.c.nodes/find-id-by-name "bitcoin-alice")))
     (def node-bob (q.c.nodes/read-record (q.c.nodes/find-id-by-name "bitcoin-bob")))
     node-alice
     (def tx-id2 (::m.c.tx/tx-id (first (q.c.tx/index-records))))

     (q.c.nodes/index-ids)

     (do-search! {::m.c.tx/tx-id "foo" ::m.c.tx/node (::m.c.nodes/id node-alice)})
     (tap> (do-search! {::m.c.tx/tx-id tx-id2 ::m.c.tx/node (::m.c.nodes/id node-alice)}))

     nil))
