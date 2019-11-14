(ns dinsro.views.show-account
  (:require [dinsro.components.show-account :refer [show-account]]
            [dinsro.events.accounts :as e.accounts]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn page
  [{{:keys [id]} :path-params :as match}]
  (let [account @(rf/subscribe [::e.accounts/item {:id (int id)}])]
    [:section.section>div.container>div.content
     [:h1 "Show Account"]
     [show-account account]]))
