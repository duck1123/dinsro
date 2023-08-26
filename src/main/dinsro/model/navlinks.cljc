(ns dinsro.model.navlinks
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [lambdaisland.glogc :as log]))

;; [[../joins/navlinks.cljc]]
;; [[../mutations/navlinks.cljc]]
;; [[../ui/navlinks.cljs]]

(defonce routes-atom (atom {}))

(defn defroute
  "Add model to routes"
  [key options]
  (swap! routes-atom assoc key options))

(s/def ::id keyword?)
(s/def ::label string?)

(defn ident [id] {::id id})
(defn idents [ids] (mapv ident ids))

(defn serve-route-key
  ([key]
   (serve-route-key key false nil))
  ([key use-replacement? replacement]
   (fn [_env props]
     (if-let [id (get props ::id)]
       (if-let [value (get-in @routes-atom [id key])]
         {key value}
         (if use-replacement?
           {key replacement}
           (throw (ex-info "No value found" {:key key :props props}))))
       (throw (ex-info "No id found" {:key key :props props}))))))

(defn find-nav-target
  [id]
  (if-let [navigate-key (get-in @routes-atom [id ::navigate-key])]
    (find-nav-target navigate-key)
    id))

(defattr id ::id :keyword
  {ao/identity? true
   ao/pc-resolve (fn [_env {::keys [id] :as props}]
                   (if-let [_record (get @routes-atom id)]
                     {::id id}
                     (do
                       (log/error :id/not-found {:id id})
                       (throw (ex-info "No id found" {:props props})))))})

;; if true, this link starts the login process

(s/def ::auth-link? boolean?)
(defattr auth-link? ::auth-link? :boolean
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::auth-link? true false)})

;; A keyword naming the control

(s/def ::control keyword?)
(defattr control ::control :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::control)})

;; A string describing the route

(s/def ::description string?)
(defattr description ::description :string
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::description true "")})

;; The model key identifying this route's target

(s/def ::input-key (s/or :nil nil? :keyword keyword?))
(defattr input-key ::input-key :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::input-key true nil)})

;; The string to show on the menu

(s/def ::label string?)
(defattr label ::label :string?
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::label)})

;; The model key that this route returns

(s/def ::model-key (s/or :nil nil? :keyword keyword?))
(defattr model-key ::model-key :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::model-key true nil)})

;; A reference to a navlink that navigating to this link should navigate to the other instead

;; (s/def ::navigate (s/or :nil nil? :keyword keyword?))
(defattr navigate ::navigate :ref
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/target     ::id
   ao/pc-resolve
   (fn [_env props]
     (let [{::keys [id]} props
           nav-id       (find-nav-target id)]
       {::navigate (ident nav-id)}))})

;; A key naming a navlink that navigating to this link should navigate to the other instead

(s/def ::navigate-key (s/or :nil nil? :keyword keyword?))
(defattr navigate-key ::navigate-key :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::navigate-key true nil)})

;; A reference to this route's parent

(defattr parent ::parent :ref
  {ao/identities #{::id}
   ao/pc-input   #{::id ::parent-key}
   ao/target     ::id
   ao/pc-resolve (fn [_env {:keys [parent-key]}]
                   {::parent (ident parent-key)})})

;; A keyword naming this route's parent

(defattr parent-key ::parent-key :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::parent-key true nil)})

;; The minimum required role to access this route

(defattr required-role ::required-role :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::required-role)})

;; "The id of this link's parent navbar. While multiple bars can point at a link, this is the bar that this link considers its parent"
(defattr router ::router :ref
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [{::router [:dinsro.model.navbars/id]}]
   ao/target     :dinsro.model.navbars/id
   ao/pc-resolve (fn [_env props]
                   (log/info :router/starting {:props props})
                   (let [{::keys [id]} props
                         router        (get-in @routes-atom [id ::router])]
                     {::router (when router {:dinsro.model.navbars/id router})}))})

;; the record identified by the supplied id

(defattr target ::target :ref
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [{::target [:dinsro.model.users/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} props]
     (log/info :target/starting {:props props :query-params query-params})
     (let [{::keys [id]} props]
       (if-let [navlink (get @routes-atom id)]
         (if-let [input-key (get-in @routes-atom [id ::model-key])]
           (if-let [model-key (get-in @routes-atom [id ::model-key])]
             (if-let [record-id (get query-params model-key)]
               (do
                 (log/info :target/targeted
                   {:navlink   navlink
                    :record-id record-id
                    :input-key input-key})
                 {::target {model-key record-id}})
               {::target nil})
             (throw (ex-info "Failed to find model key" {:id id})))
           {::target nil})
         (throw (ex-info "Failed to find navlink" {:id id})))))})

(def attributes
  [id
   auth-link?
   control
   description
   input-key
   label
   model-key
   navigate
   navigate-key
   parent
   parent-key
   required-role
   router
   target])
