(ns dinsro.ui.index-transactions
  (:require
   [dinsro.events.transactions :as e.transactions]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]))

(defn format-date
  [date]
  (str date))

(defn row-line
  [store transaction]
  (let [date (::m.transactions/date transaction)
        account-id (:db/id (::m.transactions/account transaction))]
    [:div.card
     {:style {:margin-bottom "5px"}}
     [:div.card-content
      [:div.level.is-mobile
       [:div.level-left
        [:div.level-item
         [:p (::m.transactions/description transaction)]]]]

      [:div.level.is-mobile
       [:div.level-left
        [:div.level-item
         [:p (::m.transactions/value transaction)]]]]

      [:div.level.is-mobile
       [:div.level-left
        [:div.level-item (format-date date)]]
       [:div.level-right
        [:div.level-item
         [u.links/account-link store account-id]]]]]
     [:footer.card-footer
      [:a.button.card-footer-item
       {:on-click #(st/dispatch store [::e.transactions/do-delete-record transaction])}
       (tr [:delete])]]]))

(defn index-transactions
  [store items]
  (if-not (seq items)
    [:p "no items"]
    [:div.column
     (for [transaction items]
       ^{:key (:db/id transaction)}
       [row-line store transaction])]))
