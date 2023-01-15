(ns dinsro.mutations.nostr.pubkeys
  (:require
   [clojure.spec.alpha :as s]
   #?(:cljs [com.fulcrologic.fulcro.algorithms.merge :as merge])
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.guardrails.core :refer #?(:clj [>def >defn =>] :cljs [>def])]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.nostr.pubkeys :as a.n.pubkeys])
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

(comment ::pc/_)

(>def ::item ::m.n.pubkeys/item)
(>def ::creation-response (s/keys :req [::mu/status ::mu/errors ::m.n.pubkeys/item]))


;; Fetch


(>def ::fetch!-request
  (s/keys :req [::m.n.pubkeys/id]))

(>def ::fetch!-response-success
  (s/keys :req [::mu/status ::m.n.pubkeys/item]))

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
                   ::mu/status
                   ::m.c.nodes/item]})

#?(:clj
   (>defn do-fetch!
     "Handler for fetch! mutation"
     [{::m.n.pubkeys/keys [id]}]
     [::fetch!-request => ::fetch!-response]
     (log/info :do-fetch!/started {:id id})
     (try
       (log/info :do-fetch!/starting {:id id})
       (a.n.pubkeys/fetch-contact! id)
       {::mu/status :ok}
       (catch Exception ex
         (log/error :do-fetch!/failed {:exception ex})
         (mu/exception-response ex)))))

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
      ::pc/output [::status
                   ::errors
                   ::m.c.nodes/item]}
     (do-fetch! props))

   :cljs
   (defmutation fetch! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env FetchResponse))
     (ok-action [env]  (handle-fetch env))))

#?(:clj (def resolvers []))
