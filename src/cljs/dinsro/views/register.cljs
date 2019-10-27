(ns dinsro.views.register
  (:require [dinsro.components.forms.registration-form :refer [registration-form]]
            [taoensso.timbre :as timbre]))

(defn page
  []
  [:section.section>div.container>div.content
   [:h1 "Registration Page"]
   [registration-form]])
