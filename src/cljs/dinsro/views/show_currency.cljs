(ns dinsro.views.show-currency
  (:require [dinsro.components.show-currency :refer [show-currency]]
            [dinsro.events.currencies :as e.currencies]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn page
  [{{:keys [id]} :path-params}]
  (let [currency @(rf/subscribe [::e.currencies/item (int (timbre/spy :info id))])]
    [:section.section>div.container>div.content
     [show-currency currency]]))
