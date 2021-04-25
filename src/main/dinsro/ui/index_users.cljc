(ns dinsro.ui.index-users
  (:require
   [clojure.spec.alpha :as s]
   #?(:cljs [com.fulcrologic.fulcro.components :as comp :refer [defsc]])
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   #?(:cljs [dinsro.ui.buttons :as u.buttons])
   #?(:cljs [dinsro.ui.links :as u.links])
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(s/def ::IndexUserLine-state
  (s/keys :req [::m.users/email
                ::m.users/username
                ::m.users/name]))

#?(:cljs
   (defsc IndexUserLine
     [_this {::m.users/keys [email username] :as user}]
     {:ident         ::m.users/username
      :initial-state {::m.users/email    ""
                      ::m.users/username ""
                      ::m.users/name     ""}
      :query         [::m.users/email
                      ::m.users/username
                      ::m.users/name]}
     (dom/tr {}
       (dom/td username)
       (dom/th (u.links/ui-user-link user))
       (dom/th email)
       (dom/th (u.buttons/ui-delete-user-button {::m.users/username username})))))

#?(:cljs
   (def ui-index-user-line (comp/factory IndexUserLine {:keyfn ::m.users/username})))

(def users-path "/admin/users")

(s/def ::items (s/coll-of ::IndexUserLine-state))

(s/def ::IndexUsers-state
  (s/keys :req [::items]))

#?(:cljs
   (defsc IndexUsers
     [_this {::keys [items]}]
     {:initial-state {::items []}
      :query         [{::items (comp/get-query IndexUserLine)}]}
     (if-not (seq items)
       (dom/div {} (dom/p (tr [:no-users])))
       (dom/div {}
         (dom/p
          (dom/a {:href users-path} "Users"))
         (dom/table :.table.ui
           (dom/thead {}
             (dom/tr {}
               (dom/th (tr [:id-label]))
               (dom/th (tr [:name-label]))
               (dom/th (tr [:email-label]))
               (dom/th "Buttons")))
           (dom/tbody {}
             (map ui-index-user-line items)))))))

#?(:cljs
   (def ui-index-users (comp/factory IndexUsers)))
