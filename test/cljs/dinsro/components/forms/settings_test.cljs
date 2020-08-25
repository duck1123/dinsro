(ns dinsro.components.forms.settings-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.settings :as c.f.settings]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Settings Form Components"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components.forms_test"}
      "Form Components"]]]
   [:ul.box]])

(defcard-rg form
  [error-boundary
   [c.f.settings/form]])
