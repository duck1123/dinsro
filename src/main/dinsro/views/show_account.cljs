(ns dinsro.views.show-account
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.events.users :as e.users]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.specs.views.show-account :as s.v.show-account]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [dinsro.ui.account-transactions :as u.account-transactions]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.show-account :refer [show-account]]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(defn init-page
  [_  _]
  {:document/title (tr [:show-account])
   :dispatch-n [[::e.currencies/do-fetch-index]
                [::e.accounts/do-fetch-index]
                [::e.users/do-fetch-index]]})

(s/fdef init-page
  :args (s/cat :cofx ::s.v.show-account/init-page-cofx
               :event ::s.v.show-account/init-page-event)
  :ret ::s.v.show-account/init-page-response)

(defn load-buttons
  [store]
  [:div.box
   [u.buttons/fetch-accounts store]
   [u.buttons/fetch-currencies store]
   [u.buttons/fetch-transactions store]])

(defn page
  [store match]
  (let [{{:keys [id]} :path-params} match
        id (int id)
        account @(st/subscribe store [::e.accounts/item id])]
    [:section.section>div.container>div.content
     (u.debug/hide store [load-buttons store])
     [:div.box
      [:h1 (tr [:show-account])]
      (when account
        [show-account store account])]
     (when account
       (let [items @(st/subscribe store [::e.transactions/items-by-account id])
             transactions (sort-by ::m.transactions/date items)]
         [u.account-transactions/section store id transactions]))]))

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match ::s.v.show-account/view-map ;; #(instance? rc/Match %)
               )
  :ret vector?)

(defn init-handlers!
  [store]
  (doto store
    (st/reg-event-fx ::init-page init-page))

  (kf/reg-controller
   ::page-controller
   {:params (u/filter-param-page :show-account-page)
    :start  [::init-page]})

  store)
