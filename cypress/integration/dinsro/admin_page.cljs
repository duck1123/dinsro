(ns dinsro.admin-page
  (:require
   [dinsro.support :refer [cy handle-pathom]])
  (:require-macros [latte.core :refer [describe beforeEach it]]))

(describe "Admin Page"
  (beforeEach
   []
   (.intercept cy #js {:method "POST" :url "/api"} handle-pathom)
   (comment (.visit cy "/login")))

  (it "should have an admin page" []
      (.. cy (visit "/admin"))
      (comment
        (.. cy (get ":nth-child(1) > .control > div > .input") clear (type "admin"))
        (.. cy (get ":nth-child(2) > .control > div > .input") clear (type "hunter3"))
        (.. cy (get ".control > .ui") click)
        (.. cy (get ".notification") (should "exist"))

        (.. cy (get ".title"))
        (.. cy (get ".login-link") (as "login-link"))

        (.. cy (get "@login-link") (should "contain" "login"))
        (.. cy (get "@login-link") click)
        (.. cy (get ".title") (should "contain" "Login")))


      ))
