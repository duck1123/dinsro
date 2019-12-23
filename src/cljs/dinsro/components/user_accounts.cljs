(ns dinsro.components.user-accounts
  (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.forms.add-user-account :as c.f.add-user-account]
            [dinsro.components.index-accounts :as c.index-accounts]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.events.forms.add-user-account :as s.e.f.add-user-account]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [taoensso.timbre :as timbre]))

(defn section
  [user-id accounts]
  [:div.box
   [:h2
    (tr [:accounts])
    [c/show-form-button ::s.e.f.add-user-account/shown? ::s.e.f.add-user-account/set-shown?]]
   [c.f.add-user-account/form user-id]
   [:hr]
   [c.index-accounts/index-accounts accounts]])

(s/fdef section
  :args (s/cat :user-id ::ds/id
               :accounts (s/coll-of ::s.accounts/item))
  :ret vector?)
