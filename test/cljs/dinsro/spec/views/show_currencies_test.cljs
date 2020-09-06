(ns dinsro.spec.views.show-currency-test
  (:require
   [devcards.core :refer-macros [defcard]]
   [dinsro.cards :as cards]
   [dinsro.spec :as ds]
   [dinsro.spec.views.show-currency :as s.v.show-currency]
   [taoensso.timbre :as timbre]))

(cards/header "Show Currency View Spec" [])

(defcard init-page-cofx (ds/gen-key ::s.v.show-currency/init-page-cofx))
(defcard init-page-event (ds/gen-key ::s.v.show-currency/init-page-event))
(defcard init-page-response (ds/gen-key ::s.v.show-currency/init-page-response))
(defcard view-map (ds/gen-key ::s.v.show-currency/view-map))
