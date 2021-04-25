(ns dinsro.ui.category-transactions
  (:require
   [clojure.spec.alpha :as s]
   #?(:cljs [com.fulcrologic.fulcro.components :as comp :refer [defsc]])
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:cljs [com.fulcrologic.fulcro.ui-state-machines :as uism])
   #?(:cljs [dinsro.machines :as machines])
   [dinsro.model.transactions :as m.transactions]
   [dinsro.translations :refer [tr]]
   #?(:cljs [dinsro.ui.bulma :as bulma])
   #?(:cljs [dinsro.ui.buttons :as u.buttons])
   #?(:cljs [dinsro.ui.forms.add-category-transaction :as u.f.add-category-transaction])
   #?(:cljs [dinsro.ui.links :as u.links])
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(s/def ::IndexCategoryTransactionLine-state
  (s/keys :req [::m.transactions/id
                ::m.transactions/date
                ::m.transactions/description
                ::m.transactions/account    ]))

#?(:cljs
   (defsc IndexCategoryTransactionLine
     [_this {::m.transactions/keys [account date description id]}]
     {:css           [[:.card {:margin-bottom "5px"}]]
      :ident         ::m.transactions/id
      :initial-state {::m.transactions/id          ""
                      ::m.transactions/date        ""
                      ::m.transactions/description ""
                      ::m.transactions/account     {}}
      :query         [::m.transactions/id
                      ::m.transactions/date
                      ::m.transactions/description
                      {::m.transactions/account (comp/get-query u.links/AccountLink)}]}
     (dom/div :.card
       (dom/div :.card-content
         (dom/div :.level.is-mobile
           (dom/div :.level-left
             (dom/div :.level-item (dom/p description))))
         (dom/div :.level.is-mobile
           (dom/div :.level-left
             (dom/div :.level-item date))
           (dom/div :.level-right
             (dom/div :.level-item (u.links/ui-account-link account)))))
       (dom/footer :.card-footer
         (u.buttons/ui-delete-transaction-button {::m.transactions/id id})))))

#?(:cljs
   (def ui-index-category-transaction-line
     (comp/factory IndexCategoryTransactionLine {:keyfn ::m.transactions/id})))

(s/def ::transactions (s/coll-of ::IndexCategoryTransactionLine-state))
;; (s/def ::transactions (s/keys :req [::IndexCategoryTransactionLine-state]))
(s/def ::form (s/keys))
(s/def ::toggle-button (s/keys))

(s/def ::CategoryTransactions-state
  (s/keys :req [::form ::toggle-button ::transactions]))

#?(:cljs
   (defsc CategoryTransactions
     [this {::keys [form transactions toggle-button]}]
     {:componentDidMount
      #(uism/begin! % machines/hideable form-toggle-sm {:actor/navbar CategoryTransactions})
      :ident         (fn [] [:component/id ::CategoryTransactions])
      :initial-state {::form          {}
                      ::toggle-button {:form-button/id form-toggle-sm}
                      ::transactions  []}
      :query         [{::form (comp/get-query u.f.add-category-transaction/AddCategoryTransactionForm)}
                      {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                      {::transactions (comp/get-query IndexCategoryTransactionLine)}
                      [::uism/asm-id form-toggle-sm]]}
     (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
       (bulma/box
        (dom/h2 {}
          "Transactions"
          (u.buttons/ui-show-form-button toggle-button))
        (when shown?
          (u.f.add-category-transaction/ui-form form))
        (if (seq transactions)
          (dom/div {}
            (map ui-index-category-transaction-line (timbre/spy :info transactions)))
          (dom/p "no items"))))))

#?(:cljs
   (def ui-category-transactions
     (comp/factory CategoryTransactions)))
