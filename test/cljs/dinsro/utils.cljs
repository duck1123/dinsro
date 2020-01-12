(ns dinsro.utils
  (:require
   [reagent.core :as r]))

(defn found-in [re div]
  (let [res (.-innerHTML div)]
    (if (re-find re res)
      true
      (do (println "Not found: " res)
          false))))

(defn with-mounted-component [comp f]
  (when r/is-client
    (let [div (.createElement js/document "div")]
      (try
        (let [c (r/render comp div)]
          (f c div))
        (finally
          (r/unmount-component-at-node div)
          (r/flush))))))
