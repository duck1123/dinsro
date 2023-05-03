(ns dinsro.ui.navlinks
  (:require
   [dinsro.model.navlink :as m.navlink]))

(def admin
  {::m.navlink/id         :admin
   ::m.navlink/auth-link? false
   ::m.navlink/name       "Admin"
   ::m.navlink/target     :dinsro.ui.admin.users/Report
   ::m.navlink/children   []})

(def contacts
  {::m.navlink/id         :contacts
   ::m.navlink/auth-link? false
   ::m.navlink/name       "Contacts"
   ::m.navlink/target     :dinsro.ui.contacts/Report
   ::m.navlink/children   []})

(def home
  {::m.navlink/id         :home
   ::m.navlink/auth-link? false
   ::m.navlink/name       "Home"
   ::m.navlink/target     :dinsro.ui.home/Page
   ::m.navlink/children   []})

(def login
  {::m.navlink/id         :login
   ::m.navlink/auth-link? true
   ::m.navlink/name       "Login"
   ::m.navlink/target     :dinsro.ui.login/LoginPage
   ::m.navlink/children   []})

(def nodes
  {::m.navlink/id         :nodes
   ::m.navlink/auth-link? false
   ::m.navlink/name       "Nodes"
   ::m.navlink/target     :dinsro.ui.nodes/Dashboard
   ::m.navlink/children   []})

(def nostr-events
  {::m.navlink/id         :nostr-events
   ::m.navlink/auth-link? false
   ::m.navlink/name       "Events"
   ::m.navlink/target     :dinsro.ui.nostr.events/Report
   ::m.navlink/children   []})

(def registration
  {::m.navlink/id         :registration
   ::m.navlink/auth-link? false
   ::m.navlink/name       "Registration"
   ::m.navlink/target     :dinsro.ui.registration/RegistrationPage
   ::m.navlink/children   []})

(def settings
  {::m.navlink/id         :settings
   ::m.navlink/auth-link? false
   ::m.navlink/name       "Settings"
   ::m.navlink/target     :dinsro.ui.settings/SettingsPage
   ::m.navlink/children   []})

(def transactions
  {::m.navlink/id         :transactions
   ::m.navlink/auth-link? false
   ::m.navlink/name       "Transactions"
   ::m.navlink/target     :dinsro.ui.transactions/Report
   ::m.navlink/children   []})

(def menu-links
  [transactions
   contacts
   nostr-events
   nodes
   settings
   admin])

(def dropdown-links
  [home
   transactions
   contacts
   nostr-events
   nodes
   settings
   admin])

(def unauth-links
  [login registration])
