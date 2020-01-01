(ns dinsro.components.admin-index-rate-sources
  (:require [devcards.core :refer-macros [defcard-rg]]
            [dinsro.components.admin-index-rate-sources :as c.admin-index-rate-sources]
            [dinsro.translations :refer [tr]]))

(defcard-rg c.admin-index-rate-sources/form
  "**Admin Index Rate Sources**"
  (fn []
    [:div.box
     [c.admin-index-rate-sources/section]])
  {})
