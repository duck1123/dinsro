(ns dinsro.components.index-transactions-test
  (:require [clojure.spec.alpha :as s]
            [devcards.core :refer-macros [defcard defcard-rg]]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.links :as c.links]
            [dinsro.components.index-transactions :as c.index-transactions]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(let [item (ds/gen-key ::s.transactions/item)]
  (defcard item item)
  (defcard-rg row-line
    [c.index-transactions/row-line item]))

(let [items (ds/gen-key (s/coll-of ::s.transactions/item))]
  (defcard items items)
  (defcard-rg section
    [c.index-transactions/index-transactions items]))
