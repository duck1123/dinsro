(ns dinsro.mutations.nostr.pubkey-contacts
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.merge :as merge])
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   #?(:cljs [com.fulcrologic.guardrails.core :refer [>defn ? =>]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.nostr.pubkey-contacts :as a.n.pubkey-contacts])
   [dinsro.model.nostr.pubkey-contacts :as m.n.pubkey-contacts]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/pubkey_contacts.clj][Pubkey Contact Actions]]


(comment ::pc/_ ::m.n.pubkey-contacts/_)

(defsc Item
  [_ _]
  {:initial-state {}
   :query         []})

(defsc FetchResponse
  [_ _]
  {:initial-state {::mu/status        :initial
                   ::mu/errors        {}
                   ::m.n.pubkeys/item {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   {::m.n.pubkeys/item (comp/get-query Item)}]})

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
               (let [{::m.n.pubkeys/keys [item]} response
                     {::m.n.pubkeys/keys [id]}   item]
                 (swap! state #(merge/merge-ident % [::m.n.pubkeys/id id] item))
                 {}))))))))

#?(:clj
   (pc/defmutation fetch-contacts! [_env props]
     {::pc/params #{::m.n.pubkeys/id}
      ::pc/output [::status ::errors ::m.n.pubkeys/item]}
     (log/info :fetch-contacts/starting {:props props})
     (let [response (a.n.pubkey-contacts/do-fetch-contacts! props)]
       (log/info :fetch-contacts/finished {:response response})
       response))

   :cljs
   (fm/defmutation fetch-contacts! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env FetchResponse))
     (ok-action [env]  (handle-fetch env))))

#?(:clj (def resolvers [fetch-contacts!]))
