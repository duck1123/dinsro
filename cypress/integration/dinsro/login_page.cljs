(ns dinsro.login-page
  (:require-macros [latte.core :refer [describe beforeEach it xit]]))

(def cy js/cy)

(def DEFAULT-USERNAME "admin")
(def DEFAULT-PASSWORD "hunter2")

(defn login
  ([]
   (login DEFAULT-USERNAME DEFAULT-PASSWORD))
  ([username password]
   (.. cy (log "logging in"))
   (.. cy (get ":nth-child(1) > .control > div > .input") clear (type username))
   (.. cy (get ":nth-child(2) > .control > div > .input") clear (type password))
   (.. cy (get ".control > .ui") click)))

(describe "Login Page"
  (beforeEach
   []
   (.visit cy "/login"))

  (xit "should have a login link" []
      ;; (.. cy (get ":nth-child(1) > .control > div > .input") clear (type "admin"))
      ;; (.. cy (get ":nth-child(2) > .control > div > .input") clear (type "hunter3"))
      ;; (.. cy (get ".control > .ui") click)

      (login)
      (.. cy (get ".notification") (should "exist"))

      ;; (.. cy (get ".title"))
      ;; (.. cy (get ".login-link") (as "login-link"))

      ;; (.. cy (get "@login-link") (should "contain" "login"))
      ;; (.. cy (get "@login-link") click)
      ;; (.. cy (get ".title") (should "contain" "Login"))
      ))
