(ns dinsro.views.index-transactions
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.create-transaction :as u.f.create-transaction]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [kee-frame.core :as kf]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:document/title "Index Transactions"
   :dispatch-n [[::e.transactions/do-fetch-index]
                [::e.accounts/do-fetch-index]
                [::e.currencies/do-fetch-index]]})

(defn load-buttons
  [store]
  [:div.box
   [u.buttons/fetch-transactions store]
   [u.buttons/fetch-accounts store]
   [u.buttons/fetch-currencies store]])

(s/fdef load-buttons
  :args (s/cat)
  :ret vector?)

(defn section-inner
  [store transactions]
  [:div.box
   [:h1
    (tr [:index-transactions-title "Index Transactions"])
    [u/show-form-button store ::e.f.create-transaction/shown?]]
   [u.f.create-transaction/form store]
   [:hr]
   [u.index-transactions/index-transactions store transactions]])

(s/fdef section-inner
  :args (s/cat :transactions (s/coll-of ::m.transactions/item))
  :ret vector?)

(defn page
  [store _match]
  [:section.section>div.container>div.content
   (u.debug/hide store [load-buttons store])
   (let [transactions (or @(st/subscribe store [::e.transactions/items]) [])]
     [section-inner store transactions])])

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match #(instance? rc/Match %))
  :ret vector?)

(defn init-handlers!
  [store]
  (doto store
    (st/reg-event-fx ::init-page init-page))

  (kf/reg-controller
   ::page-controller
   {:params (u/filter-page :index-transactions-page)
    :start [::init-page]})

  store)
