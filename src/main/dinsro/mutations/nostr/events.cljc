(ns dinsro.mutations.nostr.events
  (:require
   [clojure.spec.alpha :as s]
   #?(:cljs [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting])
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.fulcrologic.guardrails.core :refer #?(:clj [>def =>] :cljs [>def])]
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.nostr.events :as a.n.events])

   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.mutations :as mu]

   #?(:clj [lambdaisland.glogc :as log])))

;; [[../../actions/nostr/events.clj][Event Actions]]


(comment ::pc/_)

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
       (let [updated-item (a.n.events/do-fetch! props)]
         {::mu/status       :ok
          ::m.n.events/item updated-item})
       (catch Exception ex
         (log/error :fetch!/errored {:ex ex}))))

   :cljs
   (fm/defmutation fetch! [_props]
     (action    [_env] true)
     (remote    [env]
       (-> env
           (fm/returning FetchResponse)
           (fm/with-target (targeting/append-to [:responses/id ::FetchReponse]))))))

#?(:clj (def resolvers [fetch!]))
