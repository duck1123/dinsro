(ns dinsro.components.currency-rates
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components.rate-chart :as c.rate-chart]
   [dinsro.spec :as ds]
   [dinsro.spec.rates :as s.rates]
   [taoensso.timbre :as timbre]))

(defn section
  [rates]
  [:div.box
   [c.rate-chart/rate-chart rates]])

(s/fdef section
  :args (s/cat :currency-id ::ds/id
               :rate-feed ::s.rates/rate-feed)
  :ret vector?)
