(ns dinsro.machines
  (:require
   [com.fulcrologic.fulcro.ui-state-machines :as uism :refer [defstatemachine]]))

(defstatemachine hideable
  {::uism/actor-names #{:actor/navbar}
   ::uism/aliases     {:expanded [:actor/navbar :navbar/expanded?]}
   ::uism/plugins     {:toggle (fn [data] data)}
   ::uism/states
   {:initial
    {::uism/events
     {::uism/started
      {::uism/target-state :state/hidden}}}
    :state/shown
    {::uism/events
     {:event/hide
      {::uism/target-state :state/hidden}
      :event/show
      {::uism/target-state :state/shown}
      :event/toggle
      {::uism/target-state :state/hidden}}}
    :state/hidden
    {::uism/events
     {:event/hide
      {::uism/target-state :state/hidden}
      :event/show
      {::uism/target-state :state/shown}
      :event/toggle
      {::uism/target-state :state/shown}}}}})
