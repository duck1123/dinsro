(ns dinsro.views.index-transactions
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.forms.create-transaction :as c.f.create-transaction]
   [dinsro.components.index-transactions :as c.index-transactions]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.spec.transactions :as s.transactions]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:document/title "Index Transactions"
   :dispatch-n [[::e.transactions/do-fetch-index]
                [::e.accounts/do-fetch-index]
                [::e.currencies/do-fetch-index]]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-transactions-page)
  :start [::init-page]})

(defn load-buttons
  [store]
  [:div.box
   [c.buttons/fetch-transactions store]
   [c.buttons/fetch-accounts store]
   [c.buttons/fetch-currencies store]])

(s/fdef load-buttons
  :args (s/cat)
  :ret vector?)

(defn section-inner
  [store transactions]
  [:div.box
   [:h1
    (tr [:index-transactions-title "Index Transactions"])
    [c/show-form-button store ::e.f.create-transaction/shown?]]
   [c.f.create-transaction/form store]
   [:hr]
   [c.index-transactions/index-transactions store transactions]])

(s/fdef section-inner
  :args (s/cat :transactions (s/coll-of ::s.transactions/item))
  :ret vector?)

(defn page
  [store _match]
  [:section.section>div.container>div.content
   (c.debug/hide store [load-buttons store])
   (let [transactions (or @(st/subscribe store [::e.transactions/items]) [])]
     [section-inner store transactions])])

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match #(instance? rc/Match %))
  :ret vector?)
