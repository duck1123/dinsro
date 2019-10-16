(ns dinsro.views.index-accounts
  (:require [dinsro.components.forms.account :refer [new-account-form]]
            [dinsro.components.index-accounts :refer [index-accounts]]
            [taoensso.timbre :as timbre]))

(defn page
  []
  [:section.section>div.container>div.content
   [:h1 "Index Accounts"]
   [index-accounts]
   [new-account-form]])
