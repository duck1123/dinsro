(ns dinsro.views.index-rates
  (:require [dinsro.components.index-currencies :refer [index-currencies]]
            [dinsro.events.currencies :as e.currencies]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn page
  []
  [:section.section>div.container>div.content
   [:hi "Rates"]])
