(ns dinsro.mutations.nostr.relays
  (:require
   [clojure.spec.alpha :as s]
   #?(:cljs [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting])
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   ;; [com.fulcrologic.fulcro.data-fetch :as df]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.fulcrologic.guardrails.core :refer #?(:clj [>def >defn =>] :cljs [>def])]
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.nostr.relays :as a.n.relays])
   #?(:clj [dinsro.actions.nostr.subscription-pubkeys :as a.n.subscription-pubkeys])
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
   (pc/defmutation fetch!
     [_env props]
     {::pc/params #{::m.n.relays/id}
      ::pc/output [::status
                   ::errors
                   ::m.n.relays/item]}
     (try
       (let [updated-node (a.n.subscription-pubkeys/do-fetch! props)]
         {::mu/status       :ok
          ::m.n.relays/item updated-node})
       (catch Exception ex
         (log/error :fetch!/errored {:ex ex}))))

   :cljs
   (fm/defmutation fetch! [_props]
     (action    [_env] true)
     (remote    [env]
       (-> env
           (fm/returning FetchResponse)
           (fm/with-target (targeting/append-to [:responses/id ::ConnectReponse]))))))

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

(defsc RelayData
  [_ _]
  {:query [::m.n.relays/id ::m.n.relays/address ::m.n.relays/connected]
   :ident ::m.n.relays/id})

(defsc ConnectResponse
  [_ _]
  {:initial-state {::mu/status       :initial
                   ::m.n.relays/item {}
                   ::mu/errors       {}}
   :ident         (fn [] [:responses/id ::ConnectReponse])
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   {::m.n.relays/item (comp/get-query RelayData)}
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
     [{:keys [state result] :as env}]
     (log/info :handle-connect/starting {:env env})
     (let [{:keys [body]} result
           data           (get body `toggle!)
           relay          (get data ::m.n.relays/item)]
       (comment state env)
       (log/info :handle-connect/started {:result result :body body :data data :relay relay})
       {})))

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

#?(:clj
   (pc/defmutation delete!
     [_env props]
     {::pc/params #{::m.n.relays/id}
      ::pc/output [::status
                   ::errors
                   ::m.n.relays/item]}
     (a.n.relays/do-delete! props))

   :cljs
   (fm/defmutation delete! [_props]
     (action    [_env] true)
     (remote    [_env]  true)))

;; Toggle

#?(:clj
   (pc/defmutation toggle!
     [_env props]
     {::pc/params #{::m.n.relays/id}
      ::pc/output [::status ::errors ::m.n.relays/item]}
     (a.n.relays/do-toggle! props))

   :cljs
   (fm/defmutation toggle! [_props]
     (action    [_env] true)
     (remote    [env]
       (-> env
           (fm/returning ConnectResponse)
           (fm/with-target (targeting/append-to [:responses/id ::ConnectReponse]))))))

#?(:clj (def resolvers [connect! delete! fetch! toggle!]))
