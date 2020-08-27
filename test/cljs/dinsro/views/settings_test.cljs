(ns dinsro.views.settings-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
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

(deftest page
  (let [store (mock-store)
        match nil]
    (is (vector? (v.settings/page store match)))))

(defcard-rg page-card
  (let [store nil
        match nil]
    [v.settings/page store match]))
