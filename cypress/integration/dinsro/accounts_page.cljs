(ns dinsro.accounts-page
  (:require
   [dinsro.support :refer [cy handle-pathom]])
  (:require-macros [latte.core :refer [beforeEach describe it]]))

(describe "Admin Page"
  (beforeEach
   []
   (.intercept cy #js {:method "POST" :url "/api"} handle-pathom))

  (it "is"
    []
    true)

  (it "should have an admin page"
    []
    (.. cy (visit "/accounts"))
    (comment
      (.. cy (get ":nth-child(1) > :nth-child(5) > .button") (should "exist"))

      (.. cy (get ":nth-child(1) > :nth-child(3) > a")
          (then
           (fn [link]
             (let [text (.text link)]
               (.. cy (get link) click)
               (.. cy (get ".show-currency > :nth-child(1)") (should "have.text" text)))))))))
