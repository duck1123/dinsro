(ns dinsro.components.forms.registration-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.registration :as c.f.registration]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.spec :as ds]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Registration Form Components"]
   [:ul.box
    [:a {:href "devcards.html#!/dinsro.views.registration_test"}
     "Registration View"]]
   [:ul.box
    [:a {:href "devcards.html#!/dinsro.events.forms.registration_test"}
     "Registration Form Events"]]])

(let [form-data (ds/gen-key ::e.f.registration/form-data)]
  (defcard form-data-card form-data)

  (defcard-rg form
    (fn []
      [error-boundary
       [c.f.registration/form]])))
