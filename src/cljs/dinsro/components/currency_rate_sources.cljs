(ns dinsro.components.currency-rate-sources
  (:require [dinsro.components.debug :as c.debug])
  )

(defn section
  [currency-id rate-sources]
  [:div.box
   [:p "Rate sources" currency-id]
   [c.debug/debug-box rate-sources]
   ]
  )
