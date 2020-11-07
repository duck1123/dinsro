(ns dinsro.components.account-picker
  (:require
   [dinsro.components.user-accounts :as c.user-accounts]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(defn section
  [store]
  ;; FIXME: hard-coded user
  (let [user-id 12
        accounts @(st/subscribe store [::e.accounts/items-by-user user-id])]
    [c.user-accounts/section store user-id accounts]))
