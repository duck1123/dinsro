(ns dinsro.mutations
  (:require
   [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]]
   [dinsro.routing :as routing]
   [taoensso.timbre :as timbre]))

(defmutation activate-nav-link [{:navlink/keys [id]}]
  (action
   [{:keys [state]}]
   (swap! state assoc-in [:component/id
                          :dinsro.ui.navbar/Navbar
                          :dinsro.ui.navbar/expanded?]
          false)
   (let [href (get-in @state [:navlink/id id :navlink/href])]
     (routing/route-to! href))))

(defmutation submit [props]
  (action
   [{:keys [state]}]
   (timbre/infof "submitting: %s" props)))

(defmutation delete [props]
  (action
   [{:keys [state]}]
   (timbre/infof "deleting: %s" props)))

(defmutation toggle [_]
  (action
   [{:keys [state]}]
   (swap! state update-in [:component/id
                           :dinsro.ui.navbar/Navbar
                           :dinsro.ui.navbar/expanded?]
          not)))

(defmutation initialize-form
  [{:form-button/keys [id]}]
  (action
   [{:keys [state]}]
   (swap! state update-in [:form-button/id id] #(merge {:form-button/state false} %))))

(defmutation show-form [{:form-button/keys [id]}]
  (action
   [{:keys [state]}]
   (let [button-state (get-in @state [:form-button/id id :form-button/state])]
     (swap! state assoc-in [:form-button/id id :form-button/state] (not button-state)))))
