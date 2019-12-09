(ns dinsro.routes
  (:require #_[config.core :refer [env]]
            [dinsro.actions.accounts :as a.accounts]
            [dinsro.actions.authentication :as a.authentication]
            [dinsro.actions.currencies :as a.currencies]
            [dinsro.actions.home :as a.home]
            [dinsro.actions.rates :as a.rates]
            [dinsro.actions.status :as a.status]
            [dinsro.actions.users :as a.users]
            [dinsro.middleware :as middleware]
            [hiccup.page :refer [include-js include-css html5]]))

(def mount-target
  [:div#app
   [:h2 "Welcome to hello-devcard2"]
   [:p "please wait while Figwheel is waking up ..."]
   [:p "(Check the js console for hints if nothing exciting happens.)"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   #_(include-css (if true #_(env :dev) "/css/site.css" "/css/site.min.css"))])

(defn cards-page []
  (html5
   (head)
   [:body
    mount-target
    (include-js "/js/app_devcards.js")]))

(defn cards-handler
  [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (cards-page)})

(def view-mappings
  ["/"
   "/about"
   "/accounts"
   "/accounts/:id"
   "/currencies"
   "/currencies/:id"
   "/login"
   "/rates"
   "/rates/:id"
   "/register"
   "/settings"
   "/transactions"
   "/users"
   "/users/:id"])

(def routes
  [(into [""] (map (fn [path] [path {:get a.home/home-handler}]) view-mappings))
   ["/cards" {:get {:handler cards-handler}}]
   ["/api/v1" {:middleware [middleware/wrap-formats]}
    ["/accounts"
     [""                {:post   a.accounts/create-handler
                         :get    a.accounts/index-handler}]
     ["/:id"            {:get    a.accounts/read-handler
                         :delete a.accounts/delete-handler}]]
    ["/authenticate"    {:post   a.authentication/authenticate-handler}]
    ["/currencies"
     [""                {:get    a.currencies/index-handler
                         :post   a.currencies/create-handler}]
     ["/:id"            {:delete a.currencies/delete-handler
                         :get    a.currencies/read-handler}]]
    ["/logout"          {:post   a.authentication/logout-handler}]
    ["/rates"
     [""                {:get    a.rates/index-handler
                         :post   a.rates/create-handler}]
     ["/:id"            {:get    a.rates/read-handler
                         :delete a.rates/delete-handler}]]
    ["/register"        {:post   a.authentication/register-handler}]
    ["/status"          {:get    a.status/status-handler}]
    ["/users" {:middleware [middleware/wrap-restricted]}
     [""                {:get    a.users/index-handler
                         :post   a.users/create-handler}]
     ["/:id"            {:get    a.users/read-handler
                         :delete a.users/delete-handler}]]]])
