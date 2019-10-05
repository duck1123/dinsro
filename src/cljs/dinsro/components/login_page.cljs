(ns dinsro.components.login-page
  (:require [ajax.core :as ajax]
            [reagent.core :as r]))

(def container-styles
  {:display "flex"
   :flexWrap "wrap"
   :padding 15})

(defn form-item
  [body]
  [:div body]
  #_[ui/grid {:item true :xs 12} body])

(defn handle-submit
  [params]
  (fn [event]
    (.preventDefault event)
    (ajax/POST "/api/v1/authenticate"
               {:params params})))

(defn login-page []
  (let [state (r/atom {:email nil :password nil})]
    (fn []
      [:div
       [:h1 "Login Page"]
       #_[ui/grid {:container true :alignItems "center" :justify "center"}
        [ui/grid {:item true :xs 6}
         [ui/paper
          [:form
           {:onSubmit (handle-submit @state)
            :style container-styles}
           [ui/grid {:container true :justify "center"
                     :alignItems "center" :alignContent "center"}
            [ui/grid {:item true :xs 12 :styles {:textAlign "center"}}
             [:h1 "Log In"]]
            (form-item
             [ui/grid {:container true :direction "column" :spacing 24}
              (form-item [:strong "Error: "])
              (form-item
               [ui/text-field
                {:name "email"
                 :onChange (fn [event] (swap! state assoc :email event.target.value))
                 :label "Email"}])])
            (form-item
             [ui/text-field
              {:name "password"
               :label "Password"
               :type "password"
               :onChange (fn [event] (swap! state assoc :password event.target.value))}])
            [ui/button {:type "submit"} "Login"]]]]]]])))
