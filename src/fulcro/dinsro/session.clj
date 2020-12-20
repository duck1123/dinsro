(ns dinsro.session
  (:require
   [com.fulcrologic.fulcro.server.api-middleware :refer [augment-response]]
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [taoensso.timbre :as timbre]))

(defmutation login
  [{{:keys [session]} :request} {:user/keys [email password]}]
  {::pc/params #{:user/email :user/password}
   ::pc/output [:user/id :user/valid?]}
  (if (= password "hunter2")
    (augment-response
     {:user/id email
      :user/valid? true}
     (fn [ring-response]
       (assoc ring-response :session (assoc session :identity email))))
    {:user/id nil
     :user/valid? false}))

(def resolvers [login])
