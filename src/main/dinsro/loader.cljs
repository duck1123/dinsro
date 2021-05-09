(ns dinsro.loader
  (:require
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]]
   [dinsro.app :as da]
   [dinsro.mutations.session :as mu.session]
   [taoensso.timbre :as log]))

(defmutation init
  [_]
  (action
   [{:keys [state]}]
   (df/load! da/app :session/current-user mu.session/CurrentUser
             {:post-mutation `mu.session/finish-login})
   {::loaded true}))
