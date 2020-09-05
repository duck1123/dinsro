(ns dinsro.components.forms.create-transaction-test
  (:require
   [devcards.core :refer-macros [defcard]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.create-transaction :as c.f.create-transaction]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.spec :as ds]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(cards/header "Create Transaction Form Components" [])

(defcard a
  (ds/gen-key ::e.f.create-transaction/form-data))

(defcard create-transaction-card
  "**Create Transaction**"
  (fn []
    [error-boundary
     [c.f.create-transaction/form]]))
