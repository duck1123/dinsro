(ns dinsro.views.show-account
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.account-transactions :as c.account-transactions]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.show-account :refer [show-account]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.events.users :as e.users]
   [dinsro.specs.transactions :as s.transactions]
   [dinsro.specs.views.show-account :as s.v.show-account]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
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
   [c.buttons/fetch-accounts store]
   [c.buttons/fetch-currencies store]
   [c.buttons/fetch-transactions store]])

(defn page
  [store match]
  (let [{{:keys [id]} :path-params} match
        id (int id)
        account @(st/subscribe store [::e.accounts/item id])]
    [:section.section>div.container>div.content
     (c.debug/hide store [load-buttons store])
     [:div.box
      [:h1 (tr [:show-account])]
      (when account
        [show-account store account])]
     (when account
       (let [items @(st/subscribe store [::e.transactions/items-by-account id])
             transactions (sort-by ::s.transactions/date items)]
         [c.account-transactions/section store id transactions]))]))

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
   {:params (c/filter-param-page :show-account-page)
    :start  [::init-page]})

  store)
