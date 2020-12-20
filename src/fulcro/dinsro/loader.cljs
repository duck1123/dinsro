(ns dinsro.loader
  (:require
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]]
   [dinsro.app :as da]
   [dinsro.session :as session]
   [dinsro.ui.debug-menu :as u.debug-menu]
   [dinsro.ui.index-rate-sources :as u.index-rate-sources]
   [dinsro.ui.index-users :as u.index-users]
   [dinsro.ui.navbar :as u.navbar]
   [taoensso.timbre :as timbre]))

(defmutation init
  [_]
  (action
   [{:keys [state]}]
   (timbre/debug "Init")

   (comment (df/load! da/app :root/navbar u.navbar/Navbar))

   (df/load! da/app :menu-links u.navbar/NavLink
             {:target [:component/id ::u.navbar/Navbar ::u.navbar/menu-links]})

   (df/load! da/app :dropdown-links u.navbar/NavLink
             {:target [:component/id ::u.navbar/Navbar ::u.navbar/dropdown-links]})

   (df/load! da/app [:navlink/id :transactions] u.navbar/NavLink
             {:target [:component/id ::u.navbar/Navbar ::u.navbar/auth-links]})

   (df/load! da/app :all-rate-sources u.index-rate-sources/IndexRateSourceLine
             {:target [:page/id
                       :dinsro.views.index-rate-sources/page
                       :dinsro.views.index-rate-sources/rate-sources
                       :dinsro.ui.index-rate-sources/items]})

   (df/load! da/app :all-users u.index-users/IndexUserLine
             {:target [:page/id
                       :dinsro.views.index-users/page
                       :dinsro.views.index-users/users
                       :dinsro.ui.index-users/items]})

   (df/load! da/app :debug-menu/list u.debug-menu/DebugLinkButton
             {:target [:component/id ::u.debug-menu/component :items]})

   (df/load! da/app :session/current-user session/CurrentUser
             {:post-mutation `session/finish-login})
   {::loaded true}))
