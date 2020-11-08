(ns dinsro.ui.show-currency
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]))

(defn show-currency
  [store currency]
  (let [name (::m.currencies/name currency)]
    [:<>
     [:p name]
     (u.debug/hide store [u.buttons/delete-currency store currency])]))

(s/fdef show-currency
  :args (s/cat :currency ::m.currencies/item)
  :ret vector?)
