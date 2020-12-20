(ns dinsro.session
  (:require
   [com.fulcrologic.fulcro.server.api-middleware :refer [augment-response]]
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [taoensso.timbre :as timbre]))

(defmutation login
  [{{:keys [session]} :request} {:user/keys [email password]}]
  {::pc/params #{:user/email :user/password}
   ::pc/output [:user/id :user/valid?]}
  (let [subject {:user/email email :user/password password}]
    (when subject
      (augment-response
       {:user/id email
        :user/valid? false}
       (fn [ring-response]
         (assoc ring-response :session (merge session {::foo subject})))))))
