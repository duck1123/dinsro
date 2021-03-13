(ns dinsro.ui.currency-rate-sources
  (:require
   [dinsro.ui.forms.add-currency-rate-source :as u.f.add-currency-rate-source]
   [dinsro.ui.index-rate-sources :as u.index-rate-sources]))

(defn section
  [store currency-id rate-sources]
  [:div.box
   [u.f.add-currency-rate-source/form store currency-id]
   [u.index-rate-sources/section store rate-sources]])
