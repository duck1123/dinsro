(ns dinsro.views.show-account
  (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.account-transactions :as c.account-transactions]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.show-account :refer [show-account]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.events.users :as e.users]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))


(s/def ::init-page-cofx (s/keys))
(s/def ::init-page-event (s/keys))
(s/def ::init-page-response (s/keys))

(defn init-page
  [_  _]
  {:document/title "Show Account"
   :dispatch-n [[::e.currencies/do-fetch-index]
                [::e.accounts/do-fetch-index]
                [::e.users/do-fetch-index]]})

(s/fdef init-page
  :args (s/cat :cofx ::init-page-cofx
               :event ::init-page-event)
  :ret ::init-page-response)

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-param-page :show-account-page)
  :start  [::init-page]})

(defn load-buttons
  []
  [:div.box
   [c.buttons/fetch-accounts]
   [c.buttons/fetch-currencies]
   [c.buttons/fetch-transactions]])

(defn debug-items
  [items]
  (into [:ul] (for [item items] ^{:key (:db/id item)} [:li [c.debug/debug-box item]])))

(s/def :show-account-view/id          ::ds/id-string)
(s/def :show-account-view/path-params (s/keys :req-un [:show-account-view/id]))
(s/def ::view-map (s/keys :req-un [:show-account-view/path-params]))

(defn page
  [match]
  (let [{{:keys [id]} :path-params} match
        id (int id)
        account @(rf/subscribe [::e.accounts/item id])]
    [:section.section>div.container>div.content
     (c.debug/hide [load-buttons])
     [:div.box
      [:h1 (tr [:show-account])]
      (when account
        [show-account account])]
     (when account
       (let [items @(rf/subscribe [::e.transactions/items-by-account id])
             transactions (sort-by ::s.transactions/date items)]
         [c.account-transactions/section id transactions]))]))

(s/fdef page
  :args (s/cat :match ::view-map)
  :ret vector?)
