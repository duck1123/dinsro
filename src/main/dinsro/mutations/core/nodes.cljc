(ns dinsro.mutations.core.nodes
  (:require
   [clojure.spec.alpha :as s]
   #?(:cljs [com.fulcrologic.fulcro.algorithms.merge :as merge])
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.guardrails.core :refer #?(:clj [>def >defn =>] :cljs [>def])]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.nodes :as a.c.nodes])
   #?(:clj [dinsro.actions.core.peers :as a.c.peers])
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.queries.core.nodes :as q.c.nodes])
   [lambdaisland.glogc :as log]))

(comment ::m.c.nodes/_ ::pc/_)

(>def ::item ::m.c.nodes/item)
(>def ::creation-response (s/keys :req [::mu/status ::mu/errors ::m.c.nodes/item]))

;; Connect

(defsc ConnectResponse
  [_ _]
  {:initial-state {::m.c.nodes/item nil
                   ::mu/status         :initial
                   ::mu/errors         {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::m.c.nodes/item]})

#?(:clj
   (pc/defmutation connect!
     [_env {::m.c.nodes/keys [id]}]
     {::pc/params #{::m.c.nodes/id}
      ::pc/output [::mu/status ::m.c.nodes/item]}
     (let [node     (q.c.nodes/read-record id)
           response (a.c.nodes/update-blockchain-info! node)]
       {::mu/status         :ok
        :response           response
        ::m.c.nodes/item nil}))

   :cljs
   (defmutation connect! [_props]
     (action [_env] true)
     (remote [env]  (fm/returning env ConnectResponse))))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [_env props]
     {::pc/params #{::m.c.nodes/id}
      ::pc/output [::mu/status ::mu/errors ::m.c.nodes/item]}
     (a.c.nodes/do-delete! props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

;; Fetch

(>def ::fetch!-request
  (s/keys :req [::m.c.nodes/id]))

(>def ::fetch!-response-success
  (s/keys :req [::mu/status ::m.c.nodes/item]))

(>def ::fetch!-response-error
  (s/keys :req [::mu/status ::mu/errors]))

(>def ::fetch!-response
  (s/or :success ::fetch!-response-success
        :error ::fetch!-response-error))

(defsc FetchResponse
  [_ _]
  {:initial-state {::m.c.nodes/item nil
                   ::mu/status         :initial
                   ::mu/errors         {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::m.c.nodes/item]})

#?(:clj
   (>defn do-fetch!
     "Handler for fetch! mutation"
     [{::m.c.nodes/keys [id]}]
     [::fetch!-request => ::fetch!-response]
     (log/info :do-fetch!/started {:id id})
     (let [node (q.c.nodes/read-record id)]
       (try
         (log/info :do-fetch!/starting {:id id})
         (let [updated-node (a.c.nodes/fetch! node)]
           (log/info :do-fetch!/finished {:id id :updated-node updated-node})
           {::mu/status         :ok
            ::m.c.nodes/item updated-node})
         (catch Exception ex
           (log/error :do-fetch!/failed {:exception ex})
           (mu/exception-response ex))))))

#?(:cljs
   (defn handle-fetch
     [{:keys [state] :as env}]
     (let [body                                        (get-in env [:result :body])
           response                                    (get body `fetch!)
           {:com.fulcrologic.rad.pathom/keys [errors]} response]
       (if errors
         (do
           (log/error :handle-fetch/errored {:errors errors})
           {})
         (let [status (:dinsro.mutations/status response)]
           (if (= status :error)
             (let [errors (:dinsro.mutations/errors response)]
               (log/info :handle-fetch/errored {:response response :errors errors})
               {})
             (do
               (log/info :handle-fetch/completed {:response response})
               (let [{::m.c.nodes/keys [item]} response
                     {::m.c.nodes/keys [id]}   item]
                 (swap! state #(merge/merge-ident % [::m.c.nodes/id id] item))
                 {}))))))))

#?(:clj
   (pc/defmutation fetch!
     [_env props]
     {::pc/params #{::m.c.nodes/id}
      ::pc/output [::mu/status
                   ::mu/errors
                   ::m.c.nodes/item]}
     (do-fetch! props))

   :cljs
   (defmutation fetch! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env FetchResponse))
     (ok-action [env]  (handle-fetch env))))

;; Generate

(>def ::generate!-request (s/keys :req [::m.c.nodes/id]))
(>def ::generate!-response (s/keys :req [::mu/status] :opt [::mu/errors]))

#?(:clj
   (>defn do-generate!
     [{::m.c.nodes/keys [id]}]
     [::generate!-request => ::generate!-response]
     (try
       (let [response (a.c.nodes/generate! id)]
         (log/debug :do-generate!/response {:node-id id :response response})
         {::mu/status         :ok
          ::m.c.nodes/item (q.c.nodes/read-record id)})
       (catch Exception ex
         (log/error :do-generate!/failed {:exception ex})
         (mu/exception-response ex)))))

#?(:clj
   (pc/defmutation generate!
     [_env props]
     {::pc/params #{::m.c.nodes/id}
      ::pc/output [::mu/status ::mu/errors ::m.c.nodes/item]}
     (do-generate! props))

   :cljs
   (defmutation generate! [_props]
     (action [_env] true)
     (remote [_env] true)))

;; Fetch Peers

#?(:clj
   (defn do-fetch-peers!
     [{::m.c.nodes/keys [id]}]
     (let [node (q.c.nodes/read-record id)]
       (a.c.peers/fetch-peers! node))))

#?(:clj
   (pc/defmutation fetch-peers!
     [_env params]
     {::pc/params #{::m.c.nodes/id}
      ::pc/output [::mu/status]}
     (do-fetch-peers! params))

   :cljs
   (defmutation fetch-peers! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers [connect! delete! fetch! generate! fetch-peers!]))
