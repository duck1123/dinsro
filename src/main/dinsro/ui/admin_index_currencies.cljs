(ns dinsro.ui.admin-index-currencies
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.forms.create-currency :as e.f.create-currency]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.create-currency :as u.f.create-currency]
   [dinsro.ui.index-currencies :as u.index-currencies]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(defn index-currency-line
  [store currency]
  (let [{:keys [db/id]} currency]
    [:tr
     [:td [u.links/currency-link store id]]
     (u.debug/hide store [:td [u.buttons/delete-currency store currency]])]))

(s/fdef index-currency-line
  :args (s/cat :currency ::m.currencies/item)
  :ret vector?)

(defn index-currencies
  [store currencies]
  (if-not (seq currencies)
    [:div (tr [:no-currencies])]
    [:table
     [:thead>tr
      [:th (tr [:name-label])]
      (u.debug/hide store [:th "Buttons"])]
     (into
      [:tbody]
      (for [{:keys [db/id] :as currency} currencies]
        ^{:key id} [index-currency-line store currency]))]))

(s/fdef index-currencies
  :args (s/cat :currencies (s/coll-of ::m.currencies/item))
  :ret vector?)

(defn section-inner
  [store currencies]
  [:div.box
   [:h1
    (tr [:index-currencies "Index Currencies"])
    [u/show-form-button store ::e.f.create-currency/shown?]]
   [u.f.create-currency/form store]
   [:hr]
   (when currencies
     [u.index-currencies/index-currencies store currencies])])

(defn section
  [store]
  (let [currencies @(st/subscribe store [::e.currencies/items])]
    [section-inner store currencies]))
