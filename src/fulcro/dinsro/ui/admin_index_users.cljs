(ns dinsro.ui.admin-index-users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.index-users :as u.index-users]
   [taoensso.timbre :as timbre]))

(defsc AdminIndexUsers
  [_this {::keys [toggle-button users]}]
  {:initial-state {::toggle-button {}
                   ::users         {}}
   :query [{::toggle-button (comp/get-query u.buttons/ShowFormButton)}
           {::users         (comp/get-query u.index-users/IndexUsers)}]}
  (bulma/box
   (dom/h2
    :.title.is-2
    (tr [:users])
    (u.buttons/ui-show-form-button toggle-button))
   (dom/hr)
   (u.index-users/ui-index-users users)))

(def ui-section (comp/factory AdminIndexUsers))
