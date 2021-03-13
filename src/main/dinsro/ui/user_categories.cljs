(ns dinsro.ui.user-categories
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.forms.add-user-category :as e.f.add-user-category]
   [dinsro.model.categories :as m.categories]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-user-category :as u.f.add-user-category]
   [dinsro.ui.index-categories :as u.index-categories]
   [taoensso.timbre :as timbre]))

(defn section
  [store user-id categories]
  [:div.box
   [:h2
    "Categories"
    [u.buttons/show-form-button store ::e.f.add-user-category/shown?]]
   [u.f.add-user-category/form store user-id]
   [:hr]
   [u.index-categories/index-categories store categories]])

(s/fdef section
  :args (s/cat :user-id pos-int?
               :categories (s/coll-of ::m.categories/item))
  :ret vector?)
