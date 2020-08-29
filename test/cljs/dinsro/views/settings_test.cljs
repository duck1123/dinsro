(ns dinsro.views.settings-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.settings :as v.settings]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Settings View"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.views_test"}
      "Views"]]]

   [:ul.box]])

(let [store (mock-store)
      match nil]
  (deftest page
    (is (vector? (v.settings/page store match)))))

(let [store (mock-store)
      match nil]
  (defcard-rg page-card
    (fn []
      [error-boundary
       [v.settings/page store match]])))
