(ns dinsro.views.register
  (:require [dinsro.components.forms.account :as forms.account]
            [taoensso.timbre :as timbre]))

(defn page
  []
  [:section.section>div.container>div.content
   [:h1 "Index Accounts"]
   [forms.account/new-account-form]])
