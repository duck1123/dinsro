(ns dinsro.views.show-category
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.model.categories :as m.categories]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.show-category :as u.show-category]
   [taoensso.timbre :as log]))

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
                   {::m.categories/link (comp/get-query u.show-category/ShowCategory)}]
   :route-segment ["categories" ::m.categories/id]
   :will-enter
   (fn [app {::m.categories/keys [id]}]
     (log/info "will enter")
     (when id
       (df/load app [::m.categories/id (new-uuid id)]
                u.show-category/ShowCategory
                {:target [:page/id ::page ::m.categories/link]}))
     (dr/route-immediate (comp/get-ident ShowCategoryPage {})))}
  (when (::m.categories/id link) (u.show-category/ui-show-category link)))
