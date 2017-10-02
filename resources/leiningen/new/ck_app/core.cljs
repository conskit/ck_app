(ns {{name}}.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [devtools.core :as devtools]
            [{{name}}.handlers]
            [{{name}}.subs]
            [{{name}}.views :as views]
            [{{name}}.config :as config]
            [{{name}}.routing :as routing]
            [cljs.reader :as edn]
            [goog.dom]))


(defn dev-setup []
  (when config/debug?
    (devtools/install!)))

(defn ^:export render-to-string
  "Takes an app state as EDN and returns the HTML for that state.
  It can be invoked from JS as `{{name}}.core.render_to_string(edn)`."
  [state-edn]
  (let [state (edn/read-string state-edn)]
    (re-frame/dispatch-sync [:initialize-db state])
    (reagent/render-to-string [views/main-panel])))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (let [state (->> "app-state"
                   goog.dom/getElement
                   .-textContent
                   edn/read-string)]
    (routing/init!)
    (dev-setup)
    (re-frame/dispatch-sync [:initialize-db state])
    (mount-root)))
