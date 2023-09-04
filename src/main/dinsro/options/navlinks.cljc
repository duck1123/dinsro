(ns dinsro.options.navlinks
  (:require
   [dinsro.model.navlinks :as m.navlinks]))

(def auth-link?
  "If true, this link triggers the auth flow"
  ::m.navlinks/auth-link?)

(def control
  "A key naming the control to be displayed"
  ::m.navlinks/control)

(def description
  "A long-form description of the target"
  ::m.navlinks/description)

(def id
  ::m.navlinks/id)

(def input-key
  "The model key of the required input"
  ::m.navlinks/input-key)

(def label
  "The label to display in the menu"
  ::m.navlinks/label)

(def model-key
  "The model key of the record used by this target"
  ::m.navlinks/model-key)

(def navigate
  "The target from `navigate-key` as an object reference"
  ::m.navlinks/navigate)

(def navigate-key
  "The target to navigate to instead of this target"
  ::m.navlinks/navigate-key)

(def parent-key
  "The navlink id of the page that is the parent of this one.

  The target is the used for breadcrumbs and does not need to be a direct child"
  ::m.navlinks/parent-key)

(def required-role
  "The minimum role the user must have to access this target"
  ::m.navlinks/required-role)

(def router
  "The navlink id of the page holding the router this control belongs to"
  ::m.navlinks/router)

(def target
  "If the route targets a record, this is that record"
  ::m.navlinks/target)
