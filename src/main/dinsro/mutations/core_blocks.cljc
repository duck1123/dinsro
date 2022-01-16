(ns dinsro.mutations.core-blocks
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core-block :as a.core-block])
   [dinsro.model.core-block :as m.core-block]
   #?(:clj [dinsro.queries.core-block :as q.core-block])
   #?(:clj [dinsro.queries.core-nodes :as q.core-nodes])
   [taoensso.timbre :as log]))

(comment ::pc/_ ::m.core-block/_)

#?(:clj
   (pc/defmutation fetch!
     [_env {::m.core-block/keys [id]}]
     {::pc/params #{::m.core-block/id}
      ::pc/output [:status]}
     (if-let [block (q.core-block/read-record id)]
       (let [{node-id ::m.core-block/node height ::m.core-block/height} block]
         (if-let [node (q.core-nodes/read-record node-id)]
           (let [response (a.core-block/update-block-by-height node height)]
             {:status   :passed
              :response response})
           (do
             (log/error "no node")
             {:status  :failed
              :message "no node"})))
       (do
         (log/error "no block")
         {:status  :failed
          :message "No block"})))

   :cljs
   (defmutation fetch! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation fetch-transactions!
     [_env {::m.core-block/keys [id]}]
     {::pc/params #{::m.core-block/id}
      ::pc/output [:status]}

     (if-let [block (q.core-block/read-record id)]
       (a.core-block/fetch-transactions! block)
       (do
         (log/error "no block")
         {:status  :failed
          :message "No block"})))

   :cljs
   (defmutation fetch-transactions! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [fetch! fetch-transactions!]))
