(ns dinsro.ui.scanner
  (:require
   ["react-qr-scanner" :as QrReader]
   [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.react.error-boundaries :as eb]))

(def ui-qr-reader (interop/react-factory QrReader))

(defsc Scanner
  [_this _props {:keys [onScan]}]
  {}
  (dom/div {}
    (eb/error-boundary
     (ui-qr-reader
      {:onScan  (fn [data] (when data (onScan (js->clj data))))
       :onError (fn [data] (when data (println (str "on error: " data))))
       :delay   100}))))

(def ui-scanner (comp/computed-factory Scanner))
