(ns dinsro.admin-page
  (:require-macros [latte.core :refer [describe beforeEach it]]))

(def cy js/cy)

(describe "Admin Page"
  (beforeEach
   []
   (.visit cy "/login"))

  (it "should have an admin page" []
      (.. cy (visit "/admin"))

      ;; (.. cy (get ":nth-child(1) > .control > div > .input") clear (type "admin"))
      ;; (.. cy (get ":nth-child(2) > .control > div > .input") clear (type "hunter3"))
      ;; (.. cy (get ".control > .ui") click)

      ;; (.. cy (get ".notification") (should "exist"))

      ;; (.. cy (get ".title"))
      ;; (.. cy (get ".login-link") (as "login-link"))

      ;; (.. cy (get "@login-link") (should "contain" "login"))
      ;; (.. cy (get "@login-link") click)
      ;; (.. cy (get ".title") (should "contain" "Login"))
      ))
