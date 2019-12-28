(ns dinsro.components.account-picker
  (:require [dinsro.components.user-accounts :as c.user-accounts]
            [dinsro.events.accounts :as e.accounts]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn section
  []
  (let [user-id 12
        accounts @(rf/subscribe [::e.accounts/items-by-user user-id])]
    [c.user-accounts/section user-id accounts]))
