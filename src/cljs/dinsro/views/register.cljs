(ns dinsro.views.register
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.registration-form :refer [registration-form]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn page
  []
  [:section.section>div.container>div.content
   [:h1 "Registration Page"]
   [registration-form]])
