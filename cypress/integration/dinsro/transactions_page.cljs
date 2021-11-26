(ns dinsro.transactions-page
  (:require
   [dinsro.support :refer [cy handle-pathom]])
  (:require-macros [latte.core :refer [beforeEach describe it]]))

(describe "Transactions Page"
  (beforeEach
   []
   (.intercept cy #js {:method "POST" :url "/api"} handle-pathom))

  (it "should have an transactions page"
    []
    (.. cy (visit "/transactions"))
    (comment
      (.. cy (get ":nth-child(1) > :nth-child(5) > .button") (should "exist"))

      (.. cy (get ":nth-child(1) > :nth-child(3) > a")
          (then
           (fn [link]
             (let [text (.text link)]
               (.. cy (get link) click)
               (.. cy (get ".show-currency > :nth-child(1)") (should "have.text" text))))))

      (.. cy (get "tbody > :nth-child(1) > :nth-child(2) > a") (should "have.text" "b")))

    ))
