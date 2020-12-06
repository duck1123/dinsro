(ns dinsro.views.login
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc LoginPage
  [_this {:keys [foo]}]
  {:query [:foo]
   :route-segment ["login"]
   :ident (fn [_] [:page/id :login])
   :initial-state {:foo "bar"}}
  (timbre/info "login page")
  (dom/section
   :.section
   (dom/div
    :.container
    (dom/div
     :.content
     (dom/h1 "Login")
     (dom/p (str "foo: " foo))))))
