(ns dinsro.mutations.nostr.events
  (:require
   [clojure.spec.alpha :as s]
   #?(:cljs [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting])
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.fulcrologic.guardrails.core :refer #?(:clj [>def =>] :cljs [>def])]
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.events :as p.n.events])
   #?(:clj [lambdaisland.glogc :as log])))

;; [[../../actions/nostr/events.clj][Event Actions]]

#?(:cljs (comment ::m.n.pubkeys/_ ::m.n.relays/_  ::pc/_))

;; Fetch

(>def ::fetch!-request
  (s/keys :req [::m.n.events/id]))

(>def ::fetch!-response-success
  (s/keys :req [::mu/status ::m.n.events/item]))

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
     {::pc/params #{::m.n.events/id}
      ::pc/output [::status
                   ::errors
                   ::m.n.events/item]}
     (try
       (let [updated-item (p.n.events/fetch! props)]
         {::mu/status       :ok
          ::m.n.events/item updated-item})
       (catch Exception ex
         (log/error :fetch!/errored {:ex ex}))))

   :cljs
   (fm/defmutation fetch! [_props]
     (action [_env] true)
     (remote [env]
       (-> env
           (fm/returning FetchResponse)
           (fm/with-target (targeting/append-to [:responses/id ::FetchReponse]))))))

#?(:clj
   (pc/defmutation fetch-events!
     [_env props]
     {::pc/params #{::m.n.relays/id ::m.n.pubkeys/id}
      ::pc/output [::mu/status ::mu/errors]}
     (p.n.events/fetch-events! props))

   :cljs
   (fm/defmutation fetch-events! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [fetch! fetch-events!]))
