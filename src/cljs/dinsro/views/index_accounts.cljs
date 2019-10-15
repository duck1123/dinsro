(ns dinsro.views.index-accounts
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components.forms.account :refer [new-account-form] :as forms.account]
            [dinsro.components.index-accounts :refer [index-accounts]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn page
  []
  [:section.section>div.container>div.content
   [:h1 "Index Accounts"]
   [index-accounts]
   [new-account-form]])
