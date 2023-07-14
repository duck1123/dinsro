(ns dinsro.mutations.navbars
  (:require
   [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   #?(:cljs [com.fulcrologic.fulcro.components :as comp])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   #?(:cljs [com.fulcrologic.rad.routing :as rroute])
   #?(:cljs [com.fulcrologic.rad.authorization :as auth])
   [dinsro.model.navlinks :as m.navlinks]
   #?(:cljs [lambdaisland.glogc :as log])))

;; [[../joins/navbars.cljc]]
;; [[../model/navbars.cljc]]
;; [[../ui/navbars.cljs]]

#?(:clj (comment ::m.navlinks/_ ::dr/_ ::fs/_ ::uism/_))

#?(:cljs
   (defmutation navigate! [props]
     (action [{:keys [app state] :as env}]
       (log/info :navigate!/starting {:props props :env env})
       (let [{::m.navlinks/keys [auth-link? id navigate]} props
             {::m.navlinks/keys [control]} navigate]

         ;; This tells any forms that we can navigate away
         (when-let [form-ident (get-in props [:ui/router ::dr/current-route ::fs/config ::fs/id])]
           (swap! state #(assoc-in % [::uism/asm-id form-ident ::uism/local-storage :abandoned?] true)))

         (uism/trigger! app ::navbarsm :event/hide {})
         (uism/trigger! app auth/machine-id :event/cancel {})

         (if auth-link?
           (auth/authenticate! app :local nil)
           (if-let [component (comp/registry-key->class control)]
             (do
               (log/info :navigate!/component-found {:id id :component component :props props})
               (rroute/route-to! app component {}))
             (log/info :navigate!/component-not-found {:id id :control control :props props})))))))
