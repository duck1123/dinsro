(ns dinsro.mocks.ui.accounts
  (:require
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.specs :as ds]
   [dinsro.ui.accounts :as u.accounts]))

;; [[../../ui/accounts.cljc]]
;; [[../../../../test/dinsro/ui/accounts_test.cljs]]

(defn Show-data
  [_]
  {o.accounts/id       (ds/gen-key o.accounts/id)
   o.accounts/name     (ds/gen-key o.accounts/name)
   o.accounts/currency nil
   o.accounts/source   nil
   o.accounts/wallet   nil
   :ui/transactions    {}})

(defn IndexPage-data
  [_]
  {o.navlinks/id u.accounts/index-page-id
   :ui/form      {}
   :ui/form2     {}
   :ui/report    {}})

(defn ShowPage-data
  [_]
  {o.navlinks/id        u.accounts/show-page-id
   o.navlinks/target    (Show-data {})
   u.accounts/model-key (ds/gen-key u.accounts/model-key)})
