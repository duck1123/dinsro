(ns dinsro.mocks.ui.forms.admin.core.wallets
  (:require
   [dinsro.options.core.wallets :as o.c.wallets]
   [dinsro.options.users :as o.users]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

;; [[../../../../../ui/forms/admin/core/wallets.cljc]]
;; [[../../../../../../../test/dinsro/ui/forms/admin/core/wallets_test.cljs]]

(defn NewForm-state
  []
  (log/info :get-state/starting {})
  {o.c.wallets/id   (ds/gen-key o.c.wallets/id)
   o.c.wallets/name (ds/gen-key o.c.wallets/name)
   o.c.wallets/user
   {o.users/id   (ds/gen-key o.users/id)
    o.users/name (ds/gen-key o.users/name)}})
