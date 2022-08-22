(ns dinsro.mutations.accounts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:clj [com.fulcrologic.guardrails.core :refer [>defn =>]])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.queries.accounts :as q.accounts])
   #?(:clj [lambdaisland.glogc :as log])))

(comment ::pc/_)

(defsc DeleteResponse
  [_ _]
  {:initial-state {::m.accounts/item nil
                   ::status            :initial
                   ::errors            {}}
   :query         [{::errors (comp/get-query mu/ErrorData)}
                   ::status
                   ::deleted-records
                   ::m.accounts/item]})

(s/def ::deleted-records (s/coll-of ::m.accounts/id))
(s/def ::delete!-request (s/keys :req [::m.accounts/id]))
(s/def ::delete!-response (s/keys :opt [::mu/errors ::mu/status ::deleted-records]))

#?(:clj
   (>defn do-delete!
     [props]
     [::delete!-request => ::delete!-response]
     (let [{::m.accounts/keys [id]} props]
       (log/info :do-delete!/starting {})
       (let [response (q.accounts/delete! id)]
         (log/info :do-delete!/finished {:response response})
         {::mu/status       :ok
          ::deleted-records [id]}))))

#?(:clj
   (pc/defmutation delete!
     [_env props]
     {::pc/params #{::m.accounts/id}
      ::pc/output [::mu/status ::mu/errors ::deleted-records]}
     (do-delete! props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (ok-action [env]
       (let [body (get-in env [:result :body])
             response (get body `delete!)]
         response))

     (remote [env]
       (fm/returning env DeleteResponse))))

#?(:clj (def resolvers [delete!]))
