(ns dinsro.components.links
  (:require [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.users :as s.users]
            [dinsro.translations :refer [tr]]
            [dinsro.views.show-account :as v.show-account]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn account-link
  [id]
  (if-let [item @(rf/subscribe [::e.accounts/item id])]
    (let [name (::s.accounts/name item)]
      [:a {:href (kf/path-for [:show-account-page {:id id}])} name])
    [:span (tr [:not-loaded])]))

(defn currency-link
  [id]
  (if-let [currency @(rf/subscribe [::e.currencies/item id])]
    (let [name (::s.currencies/name currency)]
      [:a {:href (kf/path-for [:show-currency-page {:id id}])} name])
    [:span (tr [:not-loaded])]))

(defn-spec user-link vector?
  [id pos-int?]
  (if-let [user @(rf/subscribe [::e.users/item id])]
    (let [name (::s.users/name user)]
      [:a {:href (kf/path-for [:show-user-page {:id id}])} name])
    [:span (tr [:not-loaded])]))
