(ns dinsro.ui.links
  (:require
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.users :as e.users]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn account-link
  [store id]
  (if-let [item @(st/subscribe store [::e.accounts/item id])]
    (let [name (::m.accounts/name item)]
      [:a {:href (st/path-for store [:show-account-page {:id id}])} name])
    [:span "Accounts " (tr [:not-loaded])]))

(defn currency-link
  [store id]
  (if-let [currency @(st/subscribe store [::e.currencies/item id])]
    (let [name (::m.currencies/name currency)]
      [:a {:href (st/path-for store [:show-currency-page {:id id}])} name])
    [:span (tr [:sats])]))

(defn user-link
  [store id]
  (if-let [user @(st/subscribe store [::e.users/item id])]
    (let [name (::m.users/name user)]
      [:a {:href (st/path-for store [:show-user-page {:id id}])} name])
    [:span "User " (tr [:not-loaded])]))
