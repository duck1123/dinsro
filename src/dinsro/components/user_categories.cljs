(ns dinsro.components.user-categories
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.forms.add-user-category :as c.f.add-user-category]
   [dinsro.components.index-categories :as c.index-categories]
   [dinsro.events.forms.add-user-category :as e.f.add-user-category]
   [dinsro.spec.categories :as s.categories]
   [taoensso.timbre :as timbre]))

(defn section
  [store user-id categories]
  [:div.box
   [:h2
    "Categories"
    [c/show-form-button store ::e.f.add-user-category/shown?]]
   [c.f.add-user-category/form store user-id]
   [:hr]
   [c.index-categories/index-categories store categories]])

(s/fdef section
  :args (s/cat :user-id pos-int?
               :categories (s/coll-of ::s.categories/item))
  :ret vector?)
