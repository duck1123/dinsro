(ns dinsro.views.index-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [dinsro.model.categories :as m.categories]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.user-categories :as u.user-categories]
   [taoensso.timbre :as log]))

(defsc IndexCategoriesPage
  [_this {::keys [categories]}]
  {:componentDidMount
   (fn [this]
     (df/load! this ::m.categories/all-categories u.user-categories/IndexCategoryLine
               {:target [:component/id
                         ::u.user-categories/UserCategories
                         ::u.user-categories/categories
                         ::u.user-categories/categories]}))
   :ident         (fn [] [:page/id ::page])
   :initial-state {::categories {}}
   :query         [{::categories (comp/get-query u.user-categories/UserCategories)}]
   :route-segment ["categories"]}
  (bulma/page
   {:className "index-categories-page"}
   (u.user-categories/ui-user-categories categories)))

(def ui-page (comp/factory IndexCategoriesPage))
