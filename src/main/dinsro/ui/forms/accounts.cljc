(ns dinsro.ui.forms.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-field :refer [ui-form-field]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.mutations.accounts :as mu.accounts]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.currencies :as o.currencies]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

;; [[../../../../test/dinsro/ui/forms/accounts_test.cljs]]

(def model-key o.accounts/id)

(def override-form? false)
(def debug-form-props? false)
(def show-form? true)

(def create-action
  (u.buttons/form-action-button
   "Create" mu.accounts/create!
   #{o.accounts/currency o.accounts/name o.accounts/initial-value}))

(defsc CurrencyListItem
  [_this props]
  {:ident         ::m.currencies/id
   :initial-state (fn [_props]
                    {o.currencies/id   nil
                     o.currencies/name ""})
   :query         (fn []
                    [o.currencies/id
                     o.currencies/name])}
  (log/info :CurrencyListItem/starting {:props props})
  (dom/div {}
    (u.debug/ui-props-logger props)))

(def ui-currency-list-item (comp/factory CurrencyListItem {:keyfn o.currencies/id}))
(def currency-load-marker ::currency-load-marker)

;; Create form for accounts as a user
(form/defsc-form NewForm
  [this {currency      o.accounts/currency
         name          o.accounts/name
         initial-value o.accounts/initial-value
         :as           props}]
  {fo/action-buttons [::create]
   fo/attributes     [m.accounts/name
                      m.accounts/currency
                      m.accounts/initial-value]
   fo/cancel-route   ["accounts"]
   fo/controls       (merge form/standard-controls {::create create-action})
   fo/default-values {o.accounts/initial-value 0}
   fo/field-options  {o.accounts/currency u.pickers/currency-picker
                      o.accounts/user     u.pickers/user-picker}
   fo/field-styles   {o.accounts/currency :pick-one
                      o.accounts/user     :pick-one}
   fo/id             m.accounts/id
   fo/route-prefix   "new-account"
   fo/title          "Create Account"}
  (dom/div {}
    (if override-form?
      (form/render-layout this props)
      (ui-segment {}
        (dom/div {}
          (str "Account: " name))
        (dom/div {}
          (str "Initial Value: " initial-value))
        (dom/div {}
          "Currency: " (u.links/ui-currency-link currency))))
    (when debug-form-props?
      (u.debug/ui-props-logger props))))

;; Create form for accounts as a user
(form/defsc-form InlineForm-form
  [this {currency      o.accounts/currency
         name          o.accounts/name
         initial-value o.accounts/initial-value
         :as           props}]
  {fo/action-buttons [::create]
   fo/attributes     [m.accounts/name
                      m.accounts/currency
                      m.accounts/initial-value]
   fo/controls       (merge form/standard-controls {::create create-action})
   fo/default-values {o.accounts/initial-value 0}
   fo/field-options  {o.accounts/currency u.pickers/currency-picker
                      o.accounts/user     u.pickers/user-picker}
   fo/field-styles   {o.accounts/currency :pick-one
                      o.accounts/user     :pick-one}
   fo/id             m.accounts/id
   fo/title          "Create Account (form)"}
  (dom/div {}
    (if override-form?
      (form/render-layout this props)
      (ui-segment {}
        (dom/div {}
          (str "Account: " name))
        (ui-form-field {}
          (ui-form-input
           {:value    (str name)
            :onChange (fn [evt _] (fm/set-string! this o.accounts/name :event evt))
            :label    "Name"}))
        (let [currencies (get-in props [::picker-options/options-cache ::j.currencies/flat-index :options])]
          (ui-form-field {}
            (comment (map ui-currency-list-item currencies))
            (ui-dropdown
             {:label       "Currency"
              :onChange    (fn [evt a]
                             (log/info :InlineForm-component/currency-changed {:evt evt :a a})
                             (fm/set-string! this :ui/currency-id :event evt))
              :placeholder "Currency"
              :selection   true
              :clearable   true
              :options     (map
                            (fn [currency]
                              {:text  (o.currencies/name currency)
                               :value (str (o.currencies/id currency))})
                            currencies)})))
        (ui-form-field {}
          (ui-form-input
           {:value    (str initial-value)
            :onChange (fn [evt _] (fm/set-string! this o.accounts/initial-value :event evt))
            :label    "Initial Value"}))
        (dom/div {}
          (str "Initial Value: " initial-value))
        (dom/div {}
          "Currency: " (u.links/ui-currency-link currency))))
    (when debug-form-props?
      (u.debug/ui-props-logger props))))

(defsc InlineForm-component
  [this {initial-value o.accounts/initial-value
         name          o.accounts/name
         currency-id   :ui/currency-id
         :as           props}]
  {:componentDidMount
   (fn [this]
     (let [props (comp/props this)]
       (log/info :InlineForm-component/mounted {:props props :this this})
       (let [currencies-loaded? (:ui/currencies-loaded? props)]
         (log/info :InlineForm-component/mounted
           {:props              props
            :this               this
            :currencies-loaded? currencies-loaded?})
         (if currencies-loaded?
           (do
             (log/info :InlineForm-component/already-loaded {})
             nil)
           (do
             (log/info :InlineForm-component/loading {})
             (df/load! this ::j.currencies/flat-index CurrencyListItem
                       {:marker currency-load-marker
                        :target [:component/id ::NewForm ::j.currencies/flat-index]})
             nil)))))
   :ident         (fn [] [:component/id ::NewForm])
   :initial-state (fn [_props]
                    {:component/id             ::NewForm
                     ::j.currencies/flat-index []
                     o.accounts/initial-value  0
                     o.accounts/name           ""
                     :ui/currency-id           nil
                     :ui/currencies-loaded?    false})
   :query         (fn []
                    [:component/id
                     [df/marker-table currency-load-marker]
                     {::j.currencies/flat-index (comp/get-query CurrencyListItem)}
                     o.accounts/initial-value
                     o.accounts/name
                     :ui/currency-id
                     :ui/currencies-loaded?])}
  (log/info :InlineForm-component/starting {:props props})
  (let [currencies (::j.currencies/flat-index props)]
    (ui-segment {}
      (when show-form?
        (ui-form {}
          (ui-form-field {}
            (ui-form-input
             {:value    (str name)
              :onChange (fn [evt _data] (fm/set-string! this o.accounts/name :event evt))
              :label    "Name"}))
          (ui-form-field {}
            (ui-dropdown
             {:label       "Currency"
              :onChange    (fn [_evt data] (fm/set-string! this :ui/currency-id :value (.-value data)))
              :placeholder "Currency"
              :selection   true
              :clearable   true
              :options     (map (fn [currency]
                                  {:text  (o.currencies/name currency)
                                   :value (str (o.currencies/id currency))})
                                currencies)}))
          (ui-form-field {}
            (ui-form-input
             {:value    (str initial-value)
              :onChange (fn [evt _] (fm/set-double! this o.accounts/initial-value :event evt))
              :label    "Initial Value"}))

          (ui-form-field {}
            (ui-button
             {:content "Submit"
              :primary true
              :fluid   true
              :size    "large"
              :onClick
              (fn [_ev]
                (when currency-id
                  (let [currency-id-obj (new-uuid currency-id)
                        params          {o.accounts/initial-value initial-value
                                         o.accounts/name          name
                                         o.accounts/currency
                                         {o.currencies/id currency-id-obj}}]
                    (comp/transact! this
                      [(list `mu.accounts/create! params)]))))}))

          (when debug-form-props?
            (ui-form-field {}
              (u.debug/ui-props-logger props))))))))

(def ui-inline-form-component (comp/factory InlineForm-component {:keyfn model-key}))
(def ui-inline-form-form (comp/factory InlineForm-form {:keyfn model-key}))

(def InlineForm InlineForm-component)
(def ui-inline-form ui-inline-form-component)
