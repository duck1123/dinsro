(ns dinsro.ui.admin-index-users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.index-users :as u.index-users]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc AdminIndexUsers
  [_this {::keys [toggle-button users]}]
  {:ident         (fn [_] [:component/id ::AdminIndexUsers])
   :initial-state {::toggle-button {:form-button/id form-toggle-sm}
                   ::users         {}}
   :query         [{::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                   {::users (comp/get-query u.index-users/IndexUsers)}]}
  (bulma/box
   (dom/h2 :.title.is-2
     (tr [:users])
     (u.buttons/ui-show-form-button toggle-button))
   (dom/hr)
   (u.index-users/ui-index-users users)))

(def ui-section (comp/factory AdminIndexUsers))
