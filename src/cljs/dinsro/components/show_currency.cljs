(ns dinsro.components.show-currency
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.translations :refer [tr]]))

(defn show-currency
  [store currency]
  (let [name (::s.currencies/name currency)]
    [:<>
     [c.debug/debug-box store currency]
     [:h1 name]
     (c.debug/hide store [c.buttons/delete-currency store currency])]))

(s/fdef show-currency
  :args (s/cat :currency ::s.currencies/item)
  :ret vector?)
