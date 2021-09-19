(ns dinsro.views.index-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.user-categories :as u.user-categories]
   [taoensso.timbre :as log]))

(defsc IndexCategoriesPage
  [_this {::keys [categories]}]
  {:componentDidMount
   (fn [this]
     (df/load! this :session/current-user-ref
               u.user-categories/UserCategories
               {:target [:page/id
                         ::page
                         ::categories]}))
   :ident         (fn [] [:page/id ::page])
   :initial-state {::categories {}}
   :query         [:page/id
                   {::categories (comp/get-query u.user-categories/UserCategories)}]
   :route-segment ["categories"]}
  (bulma/page
   {:className "index-categories-page"}
   (when (::m.users/id categories)
     (u.user-categories/ui-user-categories categories))))

(def ui-page (comp/factory IndexCategoriesPage))
