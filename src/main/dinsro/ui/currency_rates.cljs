(ns dinsro.ui.currency-rates
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.specs :as ds]
   [dinsro.specs.rates :as s.rates]
   [dinsro.ui.rate-chart :as u.rate-chart]
   [taoensso.timbre :as timbre]))

(defn section
  [rates]
  [:div.box
   [u.rate-chart/rate-chart rates]])

(s/fdef section
  :args (s/cat :currency-id ::ds/id
               :rate-feed ::s.rates/rate-feed)
  :ret vector?)
