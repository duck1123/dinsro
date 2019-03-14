(ns dinsro.state
  (:require [reagent.core :as r]))

(defonce session (r/atom {:page :home}))
