(ns dinsro.components.user-categories
  (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.forms.add-user-category :as c.f.add-user-category]
            [dinsro.components.index-categories :as c.index-categories]
            [dinsro.spec.categories :as s.categories]
            [taoensso.timbre :as timbre]))

(defn section
  [user-id categories]
  [:div.box
   [:h2
    "Categories"
    [c/show-form-button ::c.f.add-user-category/shown? ::c.f.add-user-category/set-shown?]]
   [c.f.add-user-category/form user-id]
   [:hr]
   [c.index-categories/index-categories categories]])

(s/fdef section
  :args (s/cat :user-id pos-int?
               :categories (s/coll-of ::s.categories/item))
  :ret vector?)
