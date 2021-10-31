(ns dinsro.ui.admin-index-users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.index-users :as u.index-users]
   [taoensso.timbre :as log]))

(defsc AdminIndexUsers
  [_this {::keys [users]}]
  {:ident         (fn [_] [:component/id ::AdminIndexUsers])
   :initial-state {::users         {}}
   :query         [{::users (comp/get-query u.index-users/IndexUsers)}]}
  (bulma/box
   (dom/h2 :.title.is-2
     (tr [:users]))
   (dom/hr)
   (u.index-users/ui-index-users users)))

(def ui-section (comp/factory AdminIndexUsers))
