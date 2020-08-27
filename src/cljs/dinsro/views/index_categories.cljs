(ns dinsro.views.index-categories
  (:require
   [dinsro.components :as c]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.forms.create-category :as c.f.create-category]
   [dinsro.components.index-categories :refer [index-categories]]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.events.users :as e.users]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]))

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
  [:div.box
   [c.buttons/fetch-categories]
   [c.buttons/fetch-currencies]])

(defn page
  [_store _match]
  (let [items @(rf/subscribe [::e.categories/items])]
    [:section.section>div.container>div.content
     (c.debug/hide [load-buttons])
     [:div.box
      [:h1
       (tr [:categories "Categories"])
       [c/show-form-button ::e.f.create-category/shown?]]
      [c.f.create-category/form]
      [:hr]
      (when items
        [index-categories items])]]))
