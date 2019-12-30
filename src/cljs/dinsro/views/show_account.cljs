(ns dinsro.views.show-account
  (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.forms.add-account-transaction :as c.f.add-account-transaction]
            [dinsro.components.index-transactions :refer [index-transactions]]
            [dinsro.components.show-account :refer [show-account]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.events.users :as e.users]
            [dinsro.specs :as ds]
            [dinsro.spec.events.forms.add-account-transaction :as s.e.f.add-account-transaction]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))


(s/def ::init-page-cofx (s/keys))
(s/def ::init-page-event (s/keys))
(s/def ::init-page-response (s/keys))

(defn-spec init-page ::init-page-response
  [cofx ::init-page-cofx event ::init-page-event]
  (let [[{:keys [id]}] event]
    {:document/title "Show Account"
     :dispatch-n [
                  [::e.currencies/do-fetch-index]
                  ;; [::e.categories/do-fetch-index]
                  [::e.accounts/do-fetch-index]
                  [::e.users/do-fetch-index]
                  ]}))

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-param-page :show-account-page)
  :start  [::init-page]})

(defn-spec load-buttons vector?
  []
  (when @(rf/subscribe [::e.debug/shown?])
    [:div.box
     [c.buttons/fetch-accounts]
     [c.buttons/fetch-currencies]
     [c.buttons/fetch-transactions]]))

(defn-spec transactions-section vector?
  [account-id pos-int?]
  [:div.box
   [:h2
    (tr [:transactions])
    [c/show-form-button
     ::s.e.f.add-account-transaction/shown?
     ::s.e.f.add-account-transaction/set-shown?]]
   [c.f.add-account-transaction/form]
   [:hr]
   (let [items @(rf/subscribe [::e.transactions/items])]
     [c.debug/debug-box items]
     [index-transactions items])])

(s/def :show-account-view/id          ::ds/id-string)
(s/def :show-account-view/path-params (s/keys :req-un [:show-account-view/id]))
(s/def ::view-map (s/keys :req-un [:show-account-view/path-params]))

(defn-spec page vector?
  [match ::view-map]
  (let [{{:keys [id]} :path-params} match
        id (int id)
        account @(rf/subscribe [::e.accounts/item id])]
    [:section.section>div.container>div.content
     [load-buttons]
     [:div.box
      [:h1 (tr [:show-account])]
      (when account
        [show-account account])]
     [transactions-section id]]))
