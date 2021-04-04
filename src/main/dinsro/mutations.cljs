(ns dinsro.mutations
  (:require
   [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]]
   [dinsro.model.categories :as m.categories]
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

(defmutation create-account [_props]
  (action [_env] true)
  (remote [_env] true))

(defmutation create-category [_props]
  (action [_env] true)
  (remote [_env] true)
  (ok-action
   [{{:keys [body]} :result
     :keys          [state]
     :as            env}]
   (let [cc         (get body 'dinsro.mutations/create-category)
         categories (:created-category cc)
         ids        (map ::m.categories/id categories)
         path       [:component/id
                     :dinsro.ui.user-categories/UserCategories
                     :dinsro.ui.user-categories/categories
                     :dinsro.ui.user-categories/categories]
         idents     (map #(vector ::m.categories/id %) ids)]
     (swap! state update-in path concat idents))))

(defmutation create-currency [_props]
  (action [_env] true)
  (remote [_env] true))

(defmutation create-rate [_props]
  (action [_env] true)
  (remote [_env] true))

(defmutation create-rate-source [_props]
  (action [_env] true)
  (remote [_env] true)
  (ok-action
   [{{:keys [body]} :result
     :keys          [state]
     :as            env}]
   (let [cc         (get body 'dinsro.mutations/create-category)
         categories (:created-category cc)
         ids        (map ::m.categories/id categories)
         path       [:component/id
                     :dinsro.ui.user-categories/UserCategories
                     :dinsro.ui.user-categories/categories
                     :dinsro.ui.user-categories/categories]
         idents     (map #(vector ::m.categories/id %) ids)]
     (swap! state update-in path concat idents))))

(defmutation create-transaction [_props]
  (action [_env] true)
  (remote [_env] true))

(defmutation delete [_props]
  (action [_env] true)
  (remote [_env] true))

(defmutation delete-account [_props]
  (action [_env] true)
  (remote [_env] true))

(defmutation delete-category [_props]
  (action [_env] true)
  (remote [_env] true))

(defmutation delete-currency [_props]
  (action [_env] true)
  (remote [_env] true))

(defmutation delete-rate [_props]
  (action [_env] true)
  (remote [_env] true))

(defmutation delete-rate-source [_props]
  (action [_env] true)
  (remote [_env] true))

(defmutation delete-transaction [_props]
  (action [_env] true)
  (remote [_env] true))

(defmutation delete-user [_props]
  (action [_env] true)
  (remote [_env] true))

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
