(ns dinsro.mutations
  (:require
   [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.routing :as routing]
   [taoensso.timbre :as log]))

(defmutation activate-nav-link [{::m.navlink/keys [id]}]
  (action [{:keys [state]}]
    (swap! state assoc-in [:component/id
                           :dinsro.ui.navbar/Navbar
                           :dinsro.ui.navbar/expanded?]
           false)
    (let [link (get-in @state [::m.navlink/id id])
          href (::m.navlink/href link)]
      (routing/route-to! href))))

(defmutation submit [props]
  (action [{:keys [state]}]
    (log/infof "submitting: %s" props)))

(defmutation toggle [_]
  (action [{:keys [state]}]
    (swap! state update-in [:component/id
                            :dinsro.ui.navbar/Navbar
                            :dinsro.ui.navbar/expanded?]
           not)))

(defmutation initialize-form
  [{:form-button/keys [id]}]
  (action [{:keys [state]}]
    (swap! state update-in [:form-button/id id] #(merge {:form-button/state false} %))))

(defmutation show-form [{:form-button/keys [id]}]
  (action [{:keys [state]}]
    (let [button-state (get-in @state [:form-button/id id :form-button/state])]
      (swap! state assoc-in [:form-button/id id :form-button/state] (not button-state)))))
