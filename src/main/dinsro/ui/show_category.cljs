(ns dinsro.ui.show-category
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
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

(defsc ShowCategoryPage
  [_this {::m.categories/keys [link]}]
  {:ident         (fn [] [:page/id ::page])
   :initial-state {:page/id            ::page
                   ::m.categories/id   nil
                   ::m.categories/link {}
                   ::category          {}}
   :query         [:page/id
                   ::m.categories/id
                   ::category
                   {::m.categories/link (comp/get-query ShowCategory)}]
   :route-segment ["categories" ::m.categories/id]
   :will-enter
   (fn [app {::m.categories/keys [id]}]
     (log/info "will enter")
     (when id
       (df/load app [::m.categories/id (new-uuid id)]
                ShowCategory
                {:target [:page/id ::page ::m.categories/link]}))
     (dr/route-immediate (comp/get-ident ShowCategoryPage {})))}
  (when (::m.categories/id link) (ui-show-category link)))
