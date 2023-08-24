(ns dinsro.responses.nostr.pubkeys
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.guardrails.core :refer [>def =>]]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations :as mu]))

;; [[../../mutations/nostr/pubkeys.cljc]]

(def model-key ::m.n.pubkeys/id)

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

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

;; fetch!

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
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::m.n.pubkeys/item]})

(defsc FetchAwardsResponse
  [_ _]
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::m.n.badge-awards/items]})

;; fetch-contacts!

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

(defsc FetchDefinitionsResponse
  [_ _]
  {:initial-state {::mu/status :initial
                   ::mu/errors {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status ::m.n.badge-definitions/items]})

;; fetch-events!

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
