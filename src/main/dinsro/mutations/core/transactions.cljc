(ns dinsro.mutations.core.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.transactions :as a.c.transactions])
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.queries.core.nodes :as q.c.nodes])
   #?(:clj [dinsro.queries.core.transactions :as q.c.transactions])
   [lambdaisland.glogc :as log]))

#?(:cljs (comment ::m.c.nodes/_ ::mu/_ ::pc/_))

(defsc FetchResponse
  [_ _]
  {:initial-state {::m.c.transactions/item {}}
   :query [::mu/status
           ::m.c.transactions/item]})

(defsc SearchResponse
  [_ _]
  {:initial-state {::mu/status                 :initial
                   :tx-id                  nil
                   :node                   nil
                   :tx                     nil
                   ::m.c.transactions/item {}}
   :query         [::mu/status
                   :tx-id
                   :node
                   :tx
                   ::m.c.transactions/item]})

#?(:clj
   (pc/defmutation fetch!
     [_env props]
     {::pc/params #{::m.c.transactions/id}
      ::pc/output [::mu/status]}
     (a.c.transactions/do-fetch! props))

   :cljs
   (defmutation fetch! [_props]
     (action [_env] true)
     (remote [env]
       (fm/returning env FetchResponse))))

#?(:clj
   (defn do-search!
     [props]
     (log/info :do-search!/starting {:props props})
     (let [{tx-id   ::m.c.transactions/tx-id
            node-id ::m.c.transactions/node} props]
       (log/info :search/started {:tx-id tx-id :node-id node-id})
       (let [result (a.c.transactions/search! props)]
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
     {::pc/params #{::m.c.transactions/id}
      ::pc/output [::mu/status]}
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

#?(:clj
   (pc/defmutation delete!
     [_env props]
     {::pc/params #{::m.c.transactions/id}
      ::pc/output [::mu/status]}
     (a.c.transactions/do-delete! props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [delete! fetch! search!]))

#?(:clj
   (comment
     (def node-alice (q.c.nodes/read-record (q.c.nodes/find-by-name "bitcoin-alice")))
     node-alice
     (def tx-id2 (::m.c.transactions/tx-id (first (q.c.transactions/index-records))))

     (q.c.nodes/index-ids)

     (do-search! {::m.c.transactions/tx-id "foo" ::m.c.transactions/node (::m.c.nodes/id node-alice)})
     (tap> (do-search! {::m.c.transactions/tx-id tx-id2 ::m.c.transactions/node (::m.c.nodes/id node-alice)}))

     nil))
