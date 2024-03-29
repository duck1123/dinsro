(ns dinsro.mutations.core.transactions
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.core.transactions :as p.c.transactions])
   #?(:clj [dinsro.queries.core.nodes :as q.c.nodes])
   #?(:clj [dinsro.queries.core.transactions :as q.c.transactions])
   [dinsro.responses.core.transactions :as r.c.transactions]
   [lambdaisland.glogc :as log]))

;; [[../../actions/core/transactions.clj]]
;; [[../../processors/core/transactions.clj]]
;; [[../../responses/core/transactions.cljc]]

(def model-key ::m.c.transactions/id)

#?(:clj
   (comment
     ::log/_
     ::r.c.transactions/_))
#?(:cljs
   (comment
     ::m.c.nodes/_
     ::mu/_
     ::pc/_))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::r.c.transactions/deleted-records]}
     (p.c.transactions/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.c.transactions/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.c.transactions/DeleteResponse))))

;; Fetch

#?(:clj
   (pc/defmutation fetch!
     [_env props]
     {::pc/params #{::m.c.transactions/id}
      ::pc/output [::mu/status]}
     (p.c.transactions/fetch! props))

   :cljs
   (fm/defmutation fetch! [_props]
     (action [_env] true)
     (remote [env]
       (fm/returning env r.c.transactions/FetchResponse))))

;; Search

#?(:clj
   (pc/defmutation search!
     [_env props]
     {::pc/params #{::m.c.transactions/id}
      ::pc/output [::mu/status]}
     (p.c.transactions/search! props))

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
       (fm/returning env r.c.transactions/SearchResponse))))

#?(:clj (def resolvers [delete! fetch! search!]))

#?(:clj
   (comment
     (def node-alice (q.c.nodes/read-record (q.c.nodes/find-by-name "bitcoin-alice")))
     node-alice
     (def tx-id2 (::m.c.transactions/tx-id (first (q.c.transactions/index-records))))

     (q.c.nodes/index-ids)

     (p.c.transactions/search! {::m.c.transactions/tx-id "foo" ::m.c.transactions/node (::m.c.nodes/id node-alice)})
     (tap> (p.c.transactions/search! {::m.c.transactions/tx-id tx-id2 ::m.c.transactions/node (::m.c.nodes/id node-alice)}))

     nil))
