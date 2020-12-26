(ns dinsro.ui.links
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc AccountLink
  [_this {:account/keys [id name]}]
  {:ident :account/id
   :initial-state {:account/id   0
                   :account/name ""}
   :query [:account/id :account/name]}
  (let [path (str "/accounts/" id)]
    (dom/a {:href path} name)))

(defsc CurrencyLink
  [_this {:currency/keys [id name]}]
  {:ident :currency/id
   :initial-state {:currency/id   0
                   :currency/name ""}
   :query [:currency/id :currency/name]}
  (let [path (str "/currencies/" id)]
    (dom/a {:href path} name)))

(defsc UserLink
  [_this {::m.users/keys [id name]}]
  {:ident ::m.users/id
   :initial-state {::m.users/id   0
                   ::m.users/name ""}
   :query [::m.users/id ::m.users/name]}
  (timbre/info "link")
  (let [path (str "/users/" id)]
    (dom/a {:href path} name)))

(def ui-user-link (comp/factory UserLink {:keyfn ::m.users/id}))
