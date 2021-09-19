(ns dinsro.ui.media
  (:require
   ["@artsy/fresnel" :rename {createMedia create-media}]
   [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [taoensso.timbre :as log]))

(def breakpoints
  {:mobile      320,
   :tablet      768,
   :computer    992,
   :largeScreen 1200,
   :widescreen  1920})

(def AppMedia
  (create-media
   (clj->js {:breakpoints breakpoints})))

(def media-styles (.createMediaStyle AppMedia))

(def Media AppMedia.Media)
(def ui-media (interop/react-factory Media))

(def MediaContextProvider AppMedia.MediaContextProvider)
(def ui-media-context-provider (interop/react-factory MediaContextProvider))

(defsc MediaStyles
  [_this _props]
  {}
  (dom/style media-styles))

(def ui-media-styles (comp/factory MediaStyles))
