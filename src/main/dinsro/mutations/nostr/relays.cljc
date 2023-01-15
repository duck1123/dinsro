(ns dinsro.mutations.nostr.relays
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.fulcrologic.guardrails.core :refer #?(:clj [>def >defn =>] :cljs [>def])]
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.nostr.relays :as a.n.relays])
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.queries.nostr.relays :as q.n.relays])
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/relays.clj][Actions]]
;; [[../../model/nostr/relays.cljc][Model]]
;; [[../../queries/nostr/relays.clj][Queries]]
;; [[../../ui/nostr/relays.cljs][UI]]

(comment ::pc/_ ::m.n.relays/_ ::log/_)

(>def ::fetch!-request
  (s/keys :req [::m.n.relays/id]))

(>def ::fetch!-response-success
  (s/keys :req [::mu/status ::m.n.relays/item]))

(>def ::fetch!-response-error
  (s/keys :req [::mu/status ::mu/errors]))

(>def ::fetch!-response
  (s/or :success ::fetch!-response-success
        :error ::fetch!-response-error))

(defsc FetchResponse
  [_ _]
  {:initial-state {::mu/status      :initial
                   ::mu/errors      {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

#?(:clj
   (>defn do-fetch!
     "Handler for fetch! mutation"
     [{::m.n.relays/keys [id]}]
     [::fetch!-request => ::fetch!-response]
     (log/info :do-fetch!/started {:id id})
     (let [updated-node nil]
       {::mu/status      :ok
        ::m.n.relays/item updated-node})))

#?(:cljs
   (defn handle-fetch
     [{:keys [state] :as env}]
     (comment state env)
     {}))

#?(:clj
   (pc/defmutation fetch!
     [_env props]
     {::pc/params #{::m.n.relays/id}
      ::pc/output [::status
                   ::errors
                   ::m.n.relays/item]}
     (do-fetch! props))

   :cljs
   (fm/defmutation fetch! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env FetchResponse))
     (ok-action [env]  (handle-fetch env))))

;; Connect

(>def ::connect!-request
  (s/keys :req [::m.n.relays/id]))

(>def ::connect!-response-success
  (s/keys :req [::mu/status ::m.n.relays/item]))

(>def ::connect!-response-error
  (s/keys :req [::mu/status ::mu/errors]))

(>def ::connect!-response
  (s/or :success ::connect!-response-success
        :error ::connect!-response-error))

(defsc ConnectResponse
  [_ _]
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

#?(:clj
   (>defn do-connect!
     [{::m.n.relays/keys [id]}]
     [::connect!-request => ::connect!-response]
     (log/info :do-connect!/started {:id id})
     (a.n.relays/connect! id)
     (let [updated-node (q.n.relays/read-record id)]
       {::mu/status       :ok
        ::m.n.relays/item updated-node})))

#?(:cljs
   (defn handle-connect
     [{:keys [state] :as env}]
     (comment state env)
     {}))

#?(:clj
   (pc/defmutation connect!
     [_env props]
     {::pc/params #{::m.n.relays/id}
      ::pc/output [::status
                   ::errors
                   ::m.n.relays/item]}
     (do-connect! props))

   :cljs
   (fm/defmutation connect! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env ConnectResponse))
     (ok-action [env]  (handle-connect env))))

;; Toggle

#?(:clj
   (pc/defmutation toggle!
     [_env props]
     {::pc/params #{::m.n.relays/id}
      ::pc/output [::status
                   ::errors
                   ::m.n.relays/item]}
     (a.n.relays/do-toggle! props))

   :cljs
   (fm/defmutation toggle! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env ConnectResponse))
     (ok-action [env]  (handle-connect env))))

#?(:clj (def resolvers [connect! fetch! toggle!]))
