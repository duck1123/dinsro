(ns dinsro.core
  (:require
   [clojure.spec.alpha :as s]
   [com.smxemail.re-frame-cookie-fx]
   [day8.re-frame.http-fx]
   [dinsro.ajax :as ajax]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.settings :as c.settings]
   [dinsro.components.status :as c.status]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.admin-accounts :as e.admin-accounts]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-account-transaction :as e.f.add-account-transaction]
   [dinsro.events.forms.add-currency-rate :as e.f.add-currency-rate]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.events.forms.add-user-category :as e.f.add-user-category]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.events.forms.create-currency :as e.f.create-currency]
   [dinsro.events.forms.create-rate :as e.f.create-rate]
   [dinsro.events.forms.create-rate-source :as e.f.create-rate-source]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.events.forms.login :as e.f.login]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.events.forms.settings :as e.f.settings]
   [dinsro.events.navbar :as e.navbar]
   [dinsro.events.rates :as e.rates]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.events.settings :as e.settings]
   [dinsro.events.users :as e.users]
   [dinsro.events.websocket :as e.websocket]
   [dinsro.mappings :as mappings]
   [dinsro.routing :as routing]
   [dinsro.store.reframe :refer [reframe-store]]
   [dinsro.view :as view]
   ;; [dinsro.views.home :as v.home]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(def ^:dynamic *debug* false)

(defn initial-db
  [debug?]
  {::e.debug/shown?                                      debug?
   :token                                                nil
   ::e.debug/enabled?                                    debug?
   :dinsro.spec.events.forms.settings/allow-registration true})

(s/def ::app-db (s/keys))

(defn app-store
  []
  (doto (reframe-store)
    c.status/init-handlers!
    e.accounts/init-handlers!
    e.categories/init-handlers!
    e.admin-accounts/init-handlers!
    e.currencies/init-handlers!
    e.rates/init-handlers!
    e.debug/init-handlers!
    e.settings/init-handlers!
    e.authentication/init-handlers!
    e.transactions/init-handlers!
    e.rate-sources/init-handlers!
    e.users/init-handlers!
    mappings/init-handlers!
    e.f.add-account-transaction/init-handlers!
    e.f.add-currency-rate/init-handlers!
    e.f.add-user-account/init-handlers!
    e.f.add-user-category/init-handlers!
    e.f.create-account/init-handlers!
    e.f.create-category/init-handlers!
    e.f.create-currency/init-handlers!
    e.f.create-rate/init-handlers!
    e.f.create-rate-source/init-handlers!
    e.f.create-transaction/init-handlers!
    e.f.registration/init-handlers!
    e.f.login/init-handlers!
    e.f.settings/init-handlers!
    e.navbar/init-handlers!
    e.websocket/init-handlers!
    ;; v.home/init-handlers!
    ))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components
  []
  (rf/clear-subscription-cache!)
  (s/check-asserts (boolean *debug*))

  (kf/start!
   {:debug?         *debug*
    :routes         routing/routes
    :app-db-spec    ::app-db
    :initial-db     (initial-db *debug*)
    :root-component [(fn []
                       [error-boundary
                        (let [store (app-store)]
                          [c.status/require-status
                           store
                           (c.settings/require-settings
                            store
                            [view/root-component store])])])]}))

(defn init! [debug?]
  (ajax/load-interceptors!)
  (binding [*debug* debug?]
    (mount-components)))
