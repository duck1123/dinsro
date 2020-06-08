(ns dinsro.components.boundary
  (:require
   [cljs.repl :as repl]
   [reagent.core :as r]
   [taoensso.timbre :as timbre]))

(defn error-boundary
  [& _children]
  (let [err-state (r/atom nil)]
    (r/create-class
     {:display-name "ErrBoundary"
      :component-did-catch
      (fn [component info]
        (.error js/console info)
        (reset! err-state [component info]))
      :reagent-render
      (fn [& children]
        (if (nil? @err-state)
          (into [:<>] children)
          (let [[_ info] @err-state]
            [:div.error [:pre [:code (repl/error->str info)]]])))})))
