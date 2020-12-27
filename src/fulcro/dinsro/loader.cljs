(ns dinsro.loader
  (:require
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]]
   [dinsro.app :as da]
   [dinsro.session :as session]
   [taoensso.timbre :as timbre]))

(defmutation init
  [_]
  (action
   [{:keys [state]}]
   (df/load! da/app :session/current-user session/CurrentUser
             {:post-mutation `session/finish-login})
   {::loaded true}))
