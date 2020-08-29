(ns dinsro.components.forms.create-transaction-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.create-transaction :as c.f.create-transaction]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.spec :as ds]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Create Transaction Form Components"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components_test"}
      "Components"]]]])

(defcard a
  (ds/gen-key ::e.f.create-transaction/form-data))

(defcard create-transaction-card
  "**Create Transaction**"
  (fn []
    [error-boundary
     [c.f.create-transaction/form]]))
