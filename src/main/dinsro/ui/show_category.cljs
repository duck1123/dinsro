(ns dinsro.ui.show-category
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.categories :as m.categories]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.category-transactions :as u.category-transactions]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc ShowCategory
  [_this {::m.categories/keys [id name user category-transactions]}]
  {:query         [::m.categories/id
                   ::m.categories/name
                   {::m.categories/category-transactions (comp/get-query u.category-transactions/CategoryTransactions)}
                   {::m.categories/user (comp/get-query u.links/UserLink)}]
   :ident         ::m.categories/id
   :initial-state {::m.categories/id                    nil
                   ::m.categories/user                  []
                   ::m.categories/name                  ""
                   ::m.categories/category-transactions {}}}
  (bulma/page
   {}
   (bulma/box
    {}
    (dom/div {}
      (dom/p name)
      (dom/p (u.links/ui-user-link user))
      (u.buttons/ui-delete-category-button {::m.categories/id id})))
   (when category-transactions
     (u.category-transactions/ui-category-transactions category-transactions))))

(def ui-show-category (comp/factory ShowCategory))
