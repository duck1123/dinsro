(ns dinsro.mutations.nostr.pubkeys
  (:require
   [clojure.spec.alpha :as s]
   #?(:cljs [com.fulcrologic.fulcro.algorithms.merge :as merge])
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:clj  [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
      :cljs [com.fulcrologic.guardrails.core :refer [>def =>]])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.nostr.events :as a.n.events])
   #?(:clj [dinsro.actions.nostr.pubkey-contacts :as a.n.pubkey-contacts])
   #?(:clj [dinsro.actions.nostr.pubkey-events :as a.n.pubkey-events])
   #?(:clj [dinsro.actions.nostr.pubkeys :as a.n.pubkeys])
   #?(:clj [dinsro.actions.nostr.subscription-pubkeys :as a.n.subscription-pubkeys])
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/pubkeys.clj][Pubkey Actions]]
;; [[../../actions/nostr/subscription_pubkeys.clj][Subscription Pubkey Actions]]
;; [[../../model/nostr/relays.cljc][Relay Model]]
;; [[../../model/nostr/relay_pubkeys.cljc][Relay Pubkeys Model]]
;; [[../../ui/nostr/pubkeys.cljs][Pubkeys UI]]

(comment ::pc/_)

(>def ::item ::m.n.pubkeys/item)
(>def ::creation-response (s/keys :req [::mu/status ::mu/errors ::m.n.pubkeys/item]))

;; Add Contact

(>def ::add-contact!-request
  (s/keys :req [::m.n.pubkeys/id]))

(>def ::add-contact!-response-success
  (s/keys :req [::mu/status]))

(>def ::add-contact!-response-error
  (s/keys :req [::mu/status]))

(>def ::add-contact!-response
  (s/or :success ::add-contact!-response-success
        :error ::add-contact!-response-error))

(defsc AddContactResponse
  [_ _]
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::m.contacts/item]})

#?(:clj
   (pc/defmutation add-contact!
     [_env props]
     {::pc/params #{::m.n.pubkeys/id}
      ::pc/output [::status ::errors ::m.contacts/item]}
     (a.n.pubkeys/add-contact! props))

   :cljs
   (fm/defmutation add-contact! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env AddContactResponse))))



;; Fetch


(>def ::fetch!-request
  (s/keys :req [::m.n.pubkeys/id]))

(>def ::fetch!-response-success
  (s/keys :req [::mu/status]))

(>def ::fetch!-response-error
  (s/keys :req [::mu/status]))

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
     (log/finer :do-fetch!/started {:id id})
     (try
       (log/finer :do-fetch!/starting {:id id})
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
     {::pc/params #{::m.n.pubkeys/id}
      ::pc/output [::status ::errors ::m.n.pubkeys/item]}
     (do-fetch! props))

   :cljs
   (fm/defmutation fetch! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env FetchResponse))
     (ok-action [env]  (handle-fetch env))))


;; Fetch Contacts


(>def ::fetch-contacts!-request
  (s/keys :req [::m.n.pubkeys/id]))

(>def ::fetch-contacts!-response-success
  (s/keys :req [::mu/status]))

(>def ::fetch-contacts!-response-error
  (s/keys :req [::mu/status]))

(>def ::fetch-contacts!-response
  (s/or :success ::fetch-contacts!-response-success
        :error ::fetch-contacts!-response-error))

(defsc FetchContactsResponse
  [_ _]
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::m.c.nodes/item]})

#?(:cljs
   (defn handle-fetch-contacts
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
   (>defn do-fetch-contacts!
     [{::m.n.pubkeys/keys [id]}]
     [::fetch-contacts!-request => ::fetch-contacts!-response]
     (log/finer :do-fetch-contacts!/starting {:id id})
     (try
       (a.n.events/fetch-events! id)
       {::mu/status :ok}
       (catch Exception ex
         (log/error :do-fetch-contacts!/failed {:exception ex})
         (mu/exception-response ex)))))

#?(:clj
   (pc/defmutation fetch-contacts!
     [_env props]
     {::pc/params #{::m.n.pubkeys/id}
      ::pc/output [::status ::errors ::m.n.pubkeys/item]}
     (a.n.pubkey-contacts/do-fetch-contacts! props))

   :cljs
   (fm/defmutation fetch-contacts! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env FetchContactsResponse))
     (ok-action [env]  (handle-fetch-contacts env))))

;; Fetch Events

(>def ::fetch-events!-request
  (s/keys :req [::m.n.pubkeys/id]))

(>def ::fetch-events!-response-success
  (s/keys :req [::mu/status]))

(>def ::fetch-events!-response-error
  (s/keys :req [::mu/status]))

(>def ::fetch-events!-response
  (s/or :success ::fetch-events!-response-success
        :error ::fetch-events!-response-error))

(defsc FetchEventsResponse
  [_ _]
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::m.c.nodes/item]})

#?(:cljs
   (defn handle-fetch-events
     [{:keys [state] :as env}]
     (let [body                                        (get-in env [:result :body])
           response                                    (get body `fetch!)
           {:com.fulcrologic.rad.pathom/keys [errors]} response]
       (if errors
         (do
           (log/error :handle-fetch-events/errored {:errors errors})
           {})
         (let [status (:dinsro.mutations/status response)]
           (if (= status :error)
             (let [errors (:dinsro.mutations/errors response)]
               (log/info :handle-fetch-events/errored {:response response :errors errors})
               {})
             (do
               (log/info :handle-fetch-events/completed {:response response})
               (let [{::m.c.nodes/keys [item]} response
                     {::m.c.nodes/keys [id]}   item]
                 (swap! state #(merge/merge-ident % [::m.c.nodes/id id] item))
                 {}))))))))

#?(:clj
   (>defn do-fetch-events!
     [{::m.n.pubkeys/keys [id]}]
     [::fetch-events!-request => ::fetch-events!-response]
     (log/finer :do-fetch-events!/started {:id id})
     (try
       (log/finer :do-fetch-events!/starting {:id id})
       (a.n.events/fetch-events! id)
       {::mu/status :ok}
       (catch Exception ex
         (log/error :do-fetch-events!/failed {:exception ex})
         (mu/exception-response ex)))))

#?(:clj
   (pc/defmutation fetch-events!
     [_env props]
     {::pc/params #{::m.n.pubkeys/id}
      ::pc/output [::status ::errors ::m.n.pubkeys/item]}
     (a.n.pubkey-events/do-fetch-events! props))

   :cljs
   (fm/defmutation fetch-events! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env FetchEventsResponse))
     (ok-action [env]  (handle-fetch-events env))))

#?(:clj
   (pc/defmutation subscribe! [_env props]
     {::pc/params #{::m.n.pubkeys/id}
      ::pc/output [::status ::errors ::m.n.pubkeys/item]}
     (log/info :subscribe/starting {:props props})
     (a.n.subscription-pubkeys/do-subscribe! props))

   :cljs
   (fm/defmutation subscribe! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env FetchResponse))
     (ok-action [env]  (handle-fetch env))))

#?(:clj (def resolvers [fetch! fetch-contacts! fetch-events! subscribe!]))
