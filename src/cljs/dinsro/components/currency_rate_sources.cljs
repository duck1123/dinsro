(ns dinsro.components.currency-rate-sources
  (:require
   [dinsro.components.forms.add-currency-rate-source :as c.f.add-currency-rate-source]
   [dinsro.components.index-rate-sources :as c.index-rate-sources]))

(defn section
  [store currency-id rate-sources]
  [:div.box
   [:p "Rate sources" currency-id]
   [c.f.add-currency-rate-source/form store currency-id]
   [c.index-rate-sources/section store rate-sources]])
