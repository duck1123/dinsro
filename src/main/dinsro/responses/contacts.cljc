(ns dinsro.responses.contacts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.mutations :as mu]))

;; [[../model/contacts.cljc]]
;; [[../mutations/contacts.cljc]]
;; [[../processors/contacts.clj]]
;; [[../ui/contacts.cljs]]

(defsc CreateResponse
  [_this _props]
  {:initial-state {::created-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::created-records [::m.contacts/id]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [::m.contacts/id]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})
