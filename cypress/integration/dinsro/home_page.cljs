(ns dinsro.home-page
  (:require-macros [latte.core :refer [describe beforeEach it]]))

(def cy js/cy)

(describe "Home Page"
  (beforeEach
   []
   (.visit cy "/"))

  (it "should display the homepage" []
    (.. cy (get ".title") (should "contain" "Home Page")))

  (it "should have a login link" []
    (.. cy (get ".title"))
    (.. cy (get ".login-link") (as "login-link"))

    (.. cy (get "@login-link") (should "contain" "login"))
    (.. cy (get "@login-link") click)
    (.. cy (get ".title") (should "contain" "Login"))))
