(ns dinsro.views.show-category
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.model.categories :as m.categories]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.category-transactions :as u.category-transactions]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.show-category :as u.show-category]
   [taoensso.timbre :as log]))

(defsc ShowCategoryPage
  [_this {::keys [category transactions]}]
  {:ident         (fn [] [:page/id ::page])
   :initial-state {::category     {}
                   ::transactions {}}
   :query         [{::category (comp/get-query u.show-category/ShowCategory)}
                   {::transactions (comp/get-query u.category-transactions/CategoryTransactions)}]
   :route-segment ["categories" ::m.categories/id]
   :will-enter
   (fn [app {::m.categories/keys [id]}]
     (log/info "will enter")
     (df/load app [::m.categories/id id] u.show-category/ShowCategory
              {:target [:page/id ::page ::category]})
     (dr/route-immediate (comp/get-ident ShowCategoryPage {})))}
  (bulma/page
   (bulma/box
    (u.show-category/ui-show-category category))
   (u.category-transactions/ui-category-transactions transactions)))
