(ns dinsro.mutations.navbar
  (:require
   [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   #?(:cljs [com.fulcrologic.fulcro.components :as comp])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   #?(:cljs [com.fulcrologic.rad.routing :as rroute])
   #?(:cljs [com.fulcrologic.rad.authorization :as auth])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.navlink :as m.navlink]
   #?(:cljs [taoensso.timbre :as log])))

(comment ::m.navlink/_ ::pc/_ ::dr/_ ::fs/_ ::uism/_)

#?(:cljs
   (defmutation navigate! [props]
     (action [{:keys [app state]}]
       (let [{::m.navlink/keys [auth-link? target]} props]
         (when-let [ident (get-in props [:root/router ::dr/current-route ::fs/config ::fs/id])]
           (swap! state #(assoc-in % [::uism/asm-id ident ::uism/local-storage :abandoned?] true)))
         (if-let [component (comp/registry-key->class target)]
           (do
             (uism/trigger! app ::navbarsm :event/hide {})
             (uism/trigger! app auth/machine-id :event/cancel {})
             (if auth-link?
               (auth/authenticate! app :local nil)
               (rroute/route-to! app component {})))
           (log/infof "Could not find target: %s" target))))))
