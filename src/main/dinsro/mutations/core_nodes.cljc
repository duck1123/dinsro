(ns dinsro.mutations.core-nodes
  (:require
   [clojure.spec.alpha :as s]
   #?(:cljs [com.fulcrologic.fulcro.algorithms.merge :as merge])
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:clj [com.fulcrologic.guardrails.core :refer [>defn =>]])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core-nodes :as a.core-nodes])
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.queries.core-nodes :as q.core-nodes])
   [taoensso.timbre :as log]))

(comment ::m.core-nodes/_ ::pc/_)

(s/def ::item ::m.core-nodes/item)
(s/def ::creation-response
  (s/keys :req [::mu/status ::mu/errors ::m.core-nodes/item]))

(s/def ::fetch!-request (s/keys :req [::m.core-nodes/id]))
(s/def ::fetch!-response (s/keys :req [::mu/status]
                                 :opt [::mu/errors]))

(defsc ConnectResponse
  [_ _]
  {:initial-state {::m.core-nodes/item nil
                   ::mu/status            :initial
                   ::mu/errors            {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::m.core-nodes/item]})

#?(:clj
   (pc/defmutation connect!
     [_env {::m.core-nodes/keys [id]}]
     {::pc/params #{::m.core-nodes/id}
      ::pc/output [::mu/status ::m.core-nodes/item]}
     (let [node     (q.core-nodes/read-record id)
           response (a.core-nodes/update-blockchain-info! node)]
       {::mu/status         :ok
        :response           response
        ::m.core-nodes/item nil}))

   :cljs
   (defmutation connect! [_props]
     (action [_env] true)
     (remote [env]
       (fm/returning env ConnectResponse))))

#?(:clj
   (>defn do-fetch!
     [{::m.core-nodes/keys [id]}]
     [::fetch!-request => ::fetch!-response]
     (let [node (q.core-nodes/read-record id)]
       (try
         (let [response (a.core-nodes/fetch! node)]
           {::mu/status            :ok
            ::m.core-nodes/item response})
         (catch Exception ex
           (log/error "Failed to fetch" ex)
           {::mu/status :error
            ::mu/errors {:message (str ex)
                         :data    {}}}
           (throw ex))))))

#?(:clj
   (pc/defmutation fetch!
     [_env props]
     {::pc/params #{::m.core-nodes/id}
      ::pc/output [::status
                   ::errors
                   ::m.core-nodes/item]}
     (do-fetch! props))

   :cljs
   (defmutation fetch! [_props]
     (action [_env] true)
     (remote [env]
       (fm/returning env ConnectResponse))
     (ok-action [{:keys [state] :as env}]
       (let [body                                        (get-in env [:result :body])
             response                                    (get body `fetch!)
             {:com.fulcrologic.rad.pathom/keys [errors]} response]
         (if errors
           {}
           (let [{::m.core-nodes/keys [item]} response
                 {::m.core-nodes/keys [id]}   item]
             (swap! state #(merge/merge-ident % [::m.core-nodes/id id] item))
             {}))))))

#?(:clj
   (pc/defmutation fetch-peers!
     [_env {::m.core-nodes/keys [id]}]
     {::pc/params #{::m.core-nodes/id}
      ::pc/output [:status]}
     (let [node (q.core-nodes/read-record id)]
       (a.core-nodes/fetch-peers! node)))

   :cljs
   (defmutation fetch-peers! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers [connect! fetch! fetch-peers!]))
