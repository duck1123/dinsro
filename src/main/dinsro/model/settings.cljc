(ns dinsro.model.settings
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.queries.users :as q.users])))

;; [../ui/settings.cljs]

(s/def ::allow-registration boolean?)
(s/def ::first-run boolean?)

(s/def ::settings
  (s/keys :req-un
          [::allow-registration
           ::first-run]))

#?(:clj
   (defn get-site-config
     []
     ;; TODO: has user with admin role
     (let [has-admin? (boolean (q.users/find-by-name "admin"))]
       {::id           :main
        ::initialized? has-admin?
        ::loaded?      true})))

(defattr site-config ::site-config :ref
  {ao/pc-output [{::site-config
                  [::id ::initialized? ::loaded?
                   {::menu [:menu/id]}]}]
   ao/target ::id
   ao/pc-resolve
   (fn [_env _props]
     {::site-config #?(:cljs {}
                       :clj (assoc (get-site-config) ::menu {:menu/id :main}))})})

(def attributes [site-config])
