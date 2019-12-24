(ns dinsro.views.index-categories
  (:require [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.create-category :as c.f.create-category]
            [dinsro.components.index-categories :refer [index-categories]]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.categories :as e.categories]
            [dinsro.events.users :as e.users]
            [dinsro.spec.events.forms.create-category :as s.e.f.create-category]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [{:keys [db]} _]
  {:db (assoc db ::e.categories/items [])
   :document/title "Index Categories"
   :dispatch-n [[::e.categories/do-fetch-index]
                [::e.users/do-fetch-index]]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-categories-page)
  :start [::init-page]})

(defn load-buttons
  []
  (when @(rf/subscribe [::e.debug/shown?])
    [:div.box
     [c.buttons/fetch-categories]
     [c.buttons/fetch-currencies]]))

(defn page
  []
  (let [items @(rf/subscribe [::e.categories/items])]
    [:section.section>div.container>div.content
     [load-buttons]
     [:div.box
      [:h1
       (tr [:categories "Categories"])
       [c/show-form-button ::e.f.create-category/shown? ::e.f.create-category/set-shown?]]
      [c.f.create-category/form]
      [:hr]
      (when items
        [index-categories items])]]))
