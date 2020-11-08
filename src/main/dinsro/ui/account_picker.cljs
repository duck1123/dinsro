(ns dinsro.ui.account-picker
  (:require
   [dinsro.events.accounts :as e.accounts]
   [dinsro.store :as st]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [taoensso.timbre :as timbre]))

(defn section
  [store]
  ;; FIXME: hard-coded user
  (let [user-id 12
        accounts @(st/subscribe store [::e.accounts/items-by-user user-id])]
    [u.user-accounts/section store user-id accounts]))
