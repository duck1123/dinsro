(ns dinsro.mutations.nostr.pubkey-contacts
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.merge :as merge])
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.guardrails.core :refer [>defn =>]])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.nostr.pubkey-contacts :as a.n.pubkey-contacts])
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

(comment ::pc/_)

(defsc FetchResponse
  [_ _]
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::m.c.nodes/item]})

#?(:cljs
   (>defn handle-fetch
     [{:keys [state] :as env}]
     [any? => any?]
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
                 (swap! state #(merge/merge-ident % [::m.n.pubkeys/id id] item))
                 {}))))))))

#?(:clj
   (pc/defmutation fetch-contacts! [_env props]
     {::pc/params #{::m.n.pubkeys/id}
      ::pc/output [::status ::errors ::m.n.pubkeys/item]}
     (log/info :subscribe/starting {:props props})
     (a.n.pubkey-contacts/do-fetch-contacts! props))

   :cljs
   (fm/defmutation fetch-contacts! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env FetchResponse))
     (ok-action [env]  (handle-fetch env))))

#?(:clj (def resolvers [fetch-contacts!]))
