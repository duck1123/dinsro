(ns dinsro.views.index-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-category :as u.f.create-category]
   [dinsro.ui.index-categories :as u.index-categories]
   [taoensso.timbre :as timbre]))

(defsc IndexCategoriesPage
  [_this {::keys [categories form toggle-button]}]
  {:ident (fn [] [:page/id ::page])
   :initial-state {::categories    {}
                   ::form          {}
                   ::toggle-button {}}
   :route-segment ["categories"]
   :will-enter
   (fn [app _props]
     (df/load! app :all-categories u.index-categories/IndexCategoryLine
                 {:target [:page/id ::page ::categories ::u.index-categories/categories]})
     (dr/route-immediate (comp/get-ident IndexCategoriesPage {})))}
  (let [shown? false]
    (bulma/section
     (bulma/container
      (bulma/content
       (bulma/box
        (dom/h1
         (tr [:index-categories "Index Categories"])
         (u.buttons/ui-show-form-button toggle-button))
        (when shown? (u.f.create-category/ui-create-category-form form))
        (dom/hr)
        (u.index-categories/ui-index-categories categories)))))))

(def ui-page (comp/factory IndexCategoriesPage))
