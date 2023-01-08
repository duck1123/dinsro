(ns dinsro.mutations.nostr.relays
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.fulcrologic.guardrails.core :refer #?(:clj [>def >defn =>] :cljs [>def])]
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

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

#?(:clj (def resolvers []))
