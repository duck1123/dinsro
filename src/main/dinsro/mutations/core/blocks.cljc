(ns dinsro.mutations.core.blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.blocks :as a.c.blocks])
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.core.blocks :as q.c.blocks])
   #?(:clj [dinsro.queries.core.nodes :as q.c.nodes])
   [lambdaisland.glogc :as log]))

(comment ::pc/_ ::m.c.blocks/_ ::m.c.nodes/_ ::m.users/_)

(defsc FetchResponseResponse
  [_this _props]
  {:query [::m.c.blocks/id
           ::m.c.blocks/bits
           ::m.c.blocks/chainwork
           ::m.c.blocks/difficulty
           ::m.c.blocks/hash
           ::m.c.blocks/height
           ::m.c.blocks/merkle-root
           ::m.c.blocks/nonce
           ::m.c.blocks/size
           ::m.c.blocks/time
           ::m.c.blocks/version
           ::m.c.blocks/transaction-count
           ::m.c.blocks/median-time
           ::m.c.blocks/next-block
           ::m.c.blocks/previous-block
           ::m.c.blocks/weight
           ::m.c.blocks/version-hex
           ::m.c.blocks/stripped-size
           ::m.c.blocks/fetched?
           ::m.c.blocks/network]
   :ident ::m.c.blocks/id})

(defsc FetchResponse
  [_this _props]
  {:query [:status
           {:item (comp/get-query FetchResponseResponse)}
           :message]})

#?(:clj
   (pc/defmutation fetch!
     [{user-id ::m.users/id} {block-id ::m.c.blocks/id}]
     {::pc/params #{::m.c.blocks/id}
      ::pc/output [:status]}
     (if-let [node-id (first (q.c.nodes/find-by-user user-id))]
       (a.c.blocks/do-fetch! node-id block-id)
       (throw (RuntimeException. "no node id"))))

   :cljs
   (defmutation fetch! [_props]
     (action [_env] true)
     (remote [env] (fm/returning env FetchResponse))))

#?(:clj
   (pc/defmutation fetch-transactions!
     [_env {::m.c.blocks/keys [id]}]
     {::pc/params #{::m.c.blocks/id}
      ::pc/output [:status]}

     (if-let [block (q.c.blocks/read-record id)]
       (a.c.blocks/fetch-transactions! block)
       (do
         (log/error :block/not-found {})
         {:status  :failed
          :message "No block"})))

   :cljs
   (defmutation fetch-transactions! [_props]
     (action [_env] true)
     (remote [_env] true)))

(defsc SearchResponse
  [_ _]
  {:initial-state {:status          :initial
                   :tx-id           nil
                   :node            nil
                   :tx              nil
                   ::m.c.blocks/item {}}
   :query         [:status
                   :tx-id
                   :node
                   :tx
                   ::m.c.blocks/item]})

#?(:clj
   (pc/defmutation search!
     [_env props]
     {::pc/params #{::m.c.blocks/id}
      ::pc/output [:status]}
     (a.c.blocks/do-search! props))

   :cljs
   (defmutation search! [_props]
     (action [_env] true)
     (error-action [env]
       (log/info :search/error {:env env}))
     (ok-action [{:keys [result]
                  :as   env}]
       (let [{:keys [body]} result
             data           (get body `search!)]
         (log/info :search/completed (merge
                                      {:body   body
                                       :data   data
                                       :result result}
                                      (when false {:env env})))))

     (remote [env]
       (fm/returning env SearchResponse))))

#?(:clj (def resolvers [fetch! fetch-transactions! search!]))
