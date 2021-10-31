(ns dinsro.ui.admin
  (:require
   [com.fulcrologic.rad.container :as container]
   [com.fulcrologic.rad.container-options :as co]
   [dinsro.ui.categories :as u.categories]
   [dinsro.ui.currencies :as u.currencies]
   [dinsro.ui.rate-sources :as u.rate-sources]
   [dinsro.ui.users :as u.users]
   [taoensso.timbre :as log]))

(container/defsc-container AdminPage
  [_this _props]
  {co/children {:categories   u.categories/AdminIndexCategoriesReport
                :currencies   u.currencies/AdminIndexCurrenciesReport
                :rate-sources u.rate-sources/AdminIndexRateSourcesReport
                :users        u.users/AdminIndexUsersReport}
   co/layout   [[{:id :users :width 16}]
                [{:id :categories :width 16}]
                [{:id :currencies :width 16}]
                [{:id :rate-sources :width 16}]]
   co/route    "admin"
   co/title    "Admin"})
