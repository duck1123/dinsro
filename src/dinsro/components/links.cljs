(ns dinsro.components.links
  (:require
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.users :as e.users]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.spec.users :as s.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn account-link
  [store id]
  (if-let [item @(st/subscribe store [::e.accounts/item id])]
    (let [name (::s.accounts/name item)]
      [:a {:href (st/path-for store [:show-account-page {:id id}])} name])
    [:span "Accounts " (tr [:not-loaded])]))

(defn currency-link
  [store id]
  (if-let [currency @(st/subscribe store [::e.currencies/item id])]
    (let [name (::s.currencies/name currency)]
      [:a {:href (st/path-for store [:show-currency-page {:id id}])} name])
    [:span (tr [:sats])]))

(defn user-link
  [store id]
  (if-let [user @(st/subscribe store [::e.users/item id])]
    (let [name (::s.users/name user)]
      [:a {:href (st/path-for store [:show-user-page {:id id}])} name])
    [:span "User " (tr [:not-loaded])]))
