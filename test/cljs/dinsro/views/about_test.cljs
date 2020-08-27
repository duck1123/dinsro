(ns dinsro.views.about-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.views.about :as about]
   [pjstadig.humane-test-output]))

(defcard-rg title
  [:div
   [:h1.title "About View"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.views_test"}
      "Views"]]]

   [:ul.box]])

(deftest about-page-test
  (let [store nil
        match nil]
    (is (vector? (about/page store match)))))
