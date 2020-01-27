(ns dinsro.components.currency-rates
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.forms.add-currency-rate :as c.f.add-currency-rate]
   [dinsro.components.rate-chart :as c.rate-chart]
   [dinsro.events.forms.add-currency-rate :as e.f.add-currency-rate]
   [dinsro.spec :as ds]
   [dinsro.spec.rates :as s.rates]
   [taoensso.timbre :as timbre]))

(defn section
  [currency-id rate-feed]
  [:div.box
   [:h2
    "Rates"
    [c/show-form-button ::e.f.add-currency-rate/shown?]]
   [c.f.add-currency-rate/form currency-id]
   [:hr]
   [c.rate-chart/rate-chart rate-feed]])

(s/fdef section
  :args (s/cat :currency-id ::ds/id
               :rate-feed ::s.rates/rate-feed)
  :ret vector?)
