(ns dinsro.ui.controls
  (:require
   #?(:cljs ["react-moment" :default Moment])
   #?(:cljs [com.fulcrologic.fulcro.algorithms.react-interop :as interop])
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.rendering.semantic-ui.semantic-ui-controls :as sui]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-field :refer [ui-form-field]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [lambdaisland.glogc :as log]))

(def ui-moment
  #?(:cljs (interop/react-factory Moment)
     :clj  (fn [_this _props])))

(defn ref-control
  [{:keys [value]} _attribute]
  (dom/div :.ui
    (dom/div {} "default ref control: ")
    (dom/pre
     {}
     (dom/code {} (pr-str value)))))

(def render-ref (render-field-factory ref-control))

(defn date-control
  [{:keys [value] :as env} attribute]
  (log/info :date-control/starting {:env env :attribute attribute})
  (let [this nil
        iso-string (.toISOString value)
        date-string (.substring iso-string 0 10)
        time-string (.substring iso-string 11 19)]
    (dom/div {}
      (ui-form-field {}
        (dom/div {} (str value))
        (dom/div {} (str iso-string))
        (dom/div {} (str date-string))
        (dom/div {} (str time-string))
        (ui-form-input
         {:value    date-string
          :type     "date"
          :onChange (fn [evt _] (fm/set-string! this :user/username :event evt))})
        (ui-form-input
         {:value    time-string
          :type     "time"
          :onChange (fn [evt _] (fm/set-string! this :user/username :event evt))})))))

(def render-date (render-field-factory date-control))

(defn uuid-control
  [{:keys [value]} _attribute]
  (dom/div {} (str "uuid control" value)))

(def render-uuid (render-field-factory uuid-control))

(defn control-type
  [controls type style control]
  (assoc-in controls [::form/type->style->control type style] control))

(defsc UUIDControl
  [_this {:keys [control-key instance]}]
  (let [props (comp/props instance)
        id    (get-in props [:ui/parameters control-key])]
    (log/trace :uuid/render {:id id :control-key control-key})
    (dom/div {})))

(def uuid-control-render (comp/factory UUIDControl {:keyfn :control-key}))

(defn all-controls
  []
  (-> sui/all-controls
      (control-type :ref  :default          render-ref)
      (control-type :uuid :default          render-uuid)
      (control-type :date :default          render-date)
      (assoc-in [::control/type->style->control :uuid :default] uuid-control-render)))

(defn relative-date
  "Convert date to a relative interval"
  [v]
  (log/info :relative-date/starting {:v v})
  (when v
    (ui-moment {:date      (str v)
                :fromNow   :true
                :withTitle true}
      nil)))

(defn date-formatter
  [_ v _]
  (relative-date v))

(defn sub-page-report-loader
  [props ui-report parent-model-key report-key]
  (let [index-page-id (::m.navlinks/id props)]
    (if (parent-model-key props)
      (if-let [report (report-key props)]
        (ui-report report)
        (u.debug/load-error props (str index-page-id " report")))
      (u.debug/load-error props (str index-page-id " page")))))
