(ns dinsro.views.register-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [dinsro.views.register :as register]
            [dinsro.utils :refer [with-mounted-component found-in]]
            [reagent.core :as r]
            [reagent.ratom :as rv :refer-macros [reaction]]
            [taoensso.timbre :as timbre]))

(def tests-done (atom {}))

(use-fixtures :once
  {:before (fn []
             (set! rv/debug true))
   :after  (fn []
             (set! rv/debug false))})

;; (deftest registration-page-
;;   (is (vector? (register/registration-page- (r/atom {})))))

(deftest page-component
  (when r/is-client
    (let [name "Berry"
          email "foo@bar.com"
          password "hunter2"
          confirm-password "hunter3"
          app-step (r/atom {:name name
                            :email email
                            :password password
                            :confirm-password confirm-password})]
      (is (vector? (register/page)))

      #_(with-mounted-component [register/page app-step ""]
        (fn [c div]
          (are [value] (found-in (re-pattern value) div)
            name
            email
            password
            confirm-password))))))
