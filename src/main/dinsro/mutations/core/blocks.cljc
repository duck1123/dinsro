(ns dinsro.mutations.core.blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.blocks :as a.core-block])
   [dinsro.model.core.blocks :as m.core-block]
   #?(:clj [dinsro.queries.core.blocks :as q.core-block])
   #?(:clj [dinsro.queries.core.nodes :as q.core-nodes])
   [lambdaisland.glogc :as log]))

(comment ::pc/_ ::m.core-block/_)

(defsc FetchResponseResponse
  [_this _props]
  {:query [::m.core-block/id]
   :ident ::m.core-block/id})

(defsc FetchResponse
  [_this _props]
  {:query [:status
           {:item (comp/get-query FetchResponseResponse)}
           :message]})

#?(:clj
   (defn do-fetch!
     [id]
     (if-let [block (q.core-block/read-record id)]
       (let [{node-id ::m.core-block/node height ::m.core-block/height} block]
         (if-let [node (q.core-nodes/read-record node-id)]
           (let [block-id (a.core-block/update-block-by-height node height)
                 updated-item (q.core-block/read-record block-id)]
             {:status   :passed
              :item updated-item})
           (do
             (log/error :node/not-found {})
             {:status  :failed
              :message "no node"})))
       (do
         (log/error :block/not-found {})
         {:status  :failed
          :message "No block"}))))

#?(:clj
   (pc/defmutation fetch!
     [_env {::m.core-block/keys [id]}]
     {::pc/params #{::m.core-block/id}
      ::pc/output [:status]}
     (do-fetch! id))

   :cljs
   (defmutation fetch! [_props]
     (action [_env] true)
     (remote [env] (fm/returning env FetchResponse))))

#?(:clj
   (pc/defmutation fetch-transactions!
     [_env {::m.core-block/keys [id]}]
     {::pc/params #{::m.core-block/id}
      ::pc/output [:status]}

     (if-let [block (q.core-block/read-record id)]
       (a.core-block/fetch-transactions! block)
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
                   ::m.core-block/item {}}
   :query         [:status
                   :tx-id
                   :node
                   :tx
                   ::m.core-block/item]})

#?(:clj
   (defn do-search!
     [props]
     (log/info :tx/searching {:props props})
     (let [{hash    ::m.core-block/block
            node-id ::m.core-block/node} props]
       (log/info :search/started {:hash hash :node-id node-id})
       (let [result (a.core-block/search! props)]
         (log/info :search/result {:result result})
         (if result
           {:status  :passed
            :tx      result
            :hash    hash
            :node-id node-id}
           {:status  :failed
            :tx      result
            :hash    hash
            :node-id node-id})))))

#?(:clj
   (pc/defmutation search!
     [_env props]
     {::pc/params #{::m.core-block/id}
      ::pc/output [:status]}
     (do-search! props))

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
