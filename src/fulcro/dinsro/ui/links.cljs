(ns dinsro.ui.links
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc AccountLink
  [_this {:account/keys [id name]}]
  {:ident :account/id
   :query [:account/id :account/name]
   :initial-state {:account/id 1
                   :account/name "Unloaded"}}
  (let [path (str "/accounts/" id)]
    (dom/a {:href path} name)))

(defsc CurrencyLink
  [_this {:currency/keys [id name]}]
  {:ident :currency/id
   :query [:currency/id :currency/name]
   :initial-state {:currency/id 1
                   :currency/name "Unloaded"}}
  (let [path (str "/currencies/" id)]
    (dom/a {:href path} name)))

(defsc UserLink
  [_this {:user/keys [id name]}]
  {:ident :user/id
   :query [:user/id :user/name]
   :initial-state {:user/id 1
                   :user/name "Unloaded"}}
  (let [path (str "/users/" id)]
    (dom/a {:href path} name)))
