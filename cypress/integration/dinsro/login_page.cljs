(ns dinsro.login-page
  (:require
   [dinsro.support :refer [cy handle-pathom login]])
  (:require-macros [latte.core :refer [describe beforeEach it]]))

(describe "Login Page"
  (beforeEach
   []
   (.intercept cy #js {:method "POST" :url "/api"} handle-pathom)
   )

  (it "should have a login link" []
      (.visit cy "/login")
      (comment
        (.. cy (get ":nth-child(1) > .control > div > .input") clear (type "admin"))
        (.. cy (get ":nth-child(2) > .control > div > .input") clear (type "hunter3"))
        (.. cy (get ".control > .ui") click)

        (login)
        (.. cy (get ".notification") (should "exist"))

        (.. cy (get ".title"))
        (.. cy (get ".login-link") (as "login-link"))

        (.. cy (get "@login-link") (should "contain" "login"))
        (.. cy (get "@login-link") click)
        (.. cy (get ".title") (should "contain" "Login")))
      ))
