(ns dinsro.events.forms.registration-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.components.forms.registration :as c.f.registration]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.spec :as ds]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1 "Registration Form Events"]
   [:ul
    [:a {:href "devcards.html#!/dinsro.components.forms.registration_test"}
     "Form Component"]]])

(let [form-data (ds/gen-key ::e.f.registration/form-data)]

  (defcard form-data-card form-data)

  (defcard-rg form
    [c.f.registration/form]))
