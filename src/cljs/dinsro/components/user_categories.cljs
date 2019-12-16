(ns dinsro.components.user-categories
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.add-user-category :as c.f.add-user-category]
            [dinsro.components.index-categories :as c.index-categories]
            [dinsro.spec.categories :as s.categories]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn-spec section vector?
  [user-id pos-int? categories (s/coll-of ::s.categories/item)]
  [:div.box
   [:h2
    "Categories"
    [c/show-form-button ::c.f.add-user-category/shown? ::c.f.add-user-category/set-shown?]]
   [c.f.add-user-category/form user-id]
   [:hr]
   [c.index-categories/index-categories categories]])
