(ns dinsro.ui.buttons
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.utils :as utils]
   [taoensso.timbre :as timbre]))

(defsc ShowFormButton
  [_this {:form-button/keys [id state]}]
  {:query [:form-button/id
           :form-button/state]
   :initial-state {:form-button/id 1
                   :form-button/state true}
   :ident (fn [_id] [:form-button/id (utils/uuid)])}
  (dom/a
   :.is-pulled-right
   {:onClick (fn [] (timbre/infof "clicked %s" id))}
   (tr [:show-form (str "Show" state)])))

(def ui-show-form-button (comp/factory ShowFormButton))

(defsc DeleteButton
  [_this _props]
  {:query []}
  (dom/button :.button.is-danger "Delete"))

(def ui-delete-button (comp/factory DeleteButton))

(defsc CloseButton
  [_this _props]
  (dom/a
   :.delete.is-pulled-right
   {:onClick (fn [] (timbre/info "close button"))}
   (tr [:show-form "Show"])))

(def ui-close-button (comp/factory CloseButton))
