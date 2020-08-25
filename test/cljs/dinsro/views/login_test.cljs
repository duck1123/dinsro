(ns dinsro.views.login-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.views.login :as v.login]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Login View"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.views_test"}
      "Views"]]]

   [:ul.box]])

(defcard-rg form
  ;; "**Documentation**"
  (fn [name] [:p name])
  {:name "foo"})

(deftest page
  (is (vector? (v.login/page ""))))
