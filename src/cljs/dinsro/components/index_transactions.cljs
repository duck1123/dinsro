(ns dinsro.components.index-transactions
  (:require [clojure.spec.alpha :as s]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.links :as c.links]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

(defn format-date
  [date]
  (str date))

(defn-spec row-line vector?
  [transaction ::s.transactions/item]
  (let [date (::s.transactions/date transaction)
        account-id (:db/id (::s.transactions/account transaction))]
    [:div.card
     {:style {:margin-bottom "5px"}}
     [:div.card-content
      [:div.level.is-mobile
       [:div.level-left
        [:div.level-item
         [:p "Placeholder for Transaction description"]]]]
      [:div.level.is-mobile
       [:div.level-left
        [:div.level-item (format-date date)]]
       [:div.level-right
        [:div.level-item
         [c.links/account-link account-id]]]]]
     [:footer.card-footer
      [:a.button.card-footer-item
       {:on-click #(rf/dispatch [::e.transactions/do-delete-record transaction])}
       (tr [:delete])]]]))

(defn-spec index-transactions vector?
  [items (s/coll-of ::s.transactions/item)]
  (if-not (seq items)
    [:p "no items"]
    [:<>
     [c.debug/debug-box items]
     [:div.column
      (for [transaction items]
        ^{:key (:db/id transaction)}
        [row-line transaction])]]))
