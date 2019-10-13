(ns dinsro.views.login
  (:require [ajax.core :as ajax]
            [clojure.string :as string]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn page []
  [:div.section
   [:h1 "Login"]
   [:p (str @(rf/subscribe [::login-data]))]
   [login-form]])
