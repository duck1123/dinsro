(ns dinsro.home-page
  (:require
   [dinsro.support :refer [cy handle-pathom]])
  (:require-macros
   [latte.core :refer [describe beforeEach it]]))

(describe
 "Home Page"
  (beforeEach
   []
   (.intercept cy #js {:method "POST" :url "/api"} handle-pathom))

 (it "should display the homepage" []
     (.visit cy "/")
     (.. cy (get ".title") (should "contain" "Home Page")))

 (comment
   (it "should have a login link" []
      (.visit cy "/")
      (.. cy (get ".title"))
      (.. cy (get ".login-link") (as "login-link"))

      (.. cy (get "@login-link") (should "contain" "login"))
      (.. cy (get "@login-link") click)
      (.. cy (get ".title") (should "contain" "Login")))))
