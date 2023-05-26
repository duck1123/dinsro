(ns dinsro.processors.core.nodes
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.core.nodes :as a.c.nodes]
   [dinsro.actions.core.peers :as a.c.peers]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.mutations :as mu]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.responses.core.nodes :as r.c.nodes]
   [lambdaisland.glogc :as log]))

;; [../../mutations/core/nodes.cljc]

(>defn fetch!
  "Handler for fetch! mutation"
  [{::m.c.nodes/keys [id]}]
  [::r.c.nodes/fetch!-request => ::r.c.nodes/fetch!-response]
  (log/info :do-fetch!/started {:id id})
  (let [node (q.c.nodes/read-record id)]
    (try
      (log/info :do-fetch!/starting {:id id})
      (let [updated-node (a.c.nodes/fetch! node)]
        (log/info :do-fetch!/finished {:id id :updated-node updated-node})
        {::mu/status      :ok
         ::m.c.nodes/item updated-node})
      (catch Exception ex
        (log/error :do-fetch!/failed {:exception ex})
        (mu/exception-response ex)))))

(>defn generate!
  [{::m.c.nodes/keys [id]}]
  [::r.c.nodes/generate!-request => ::r.c.nodes/generate!-response]
  (try
    (let [response (a.c.nodes/generate! id)]
      (log/debug :do-generate!/response {:node-id id :response response})
      {::mu/status      :ok
       ::m.c.nodes/item (q.c.nodes/read-record id)})
    (catch Exception ex
      (log/error :do-generate!/failed {:exception ex})
      (mu/exception-response ex))))

(defn fetch-peers!
  [{::m.c.nodes/keys [id]}]
  (let [node (q.c.nodes/read-record id)]
    (a.c.peers/fetch-peers! node)))
