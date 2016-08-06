(ns {{name}}.routing
  (:require [re-frame.core :as re-frame]
            [cljs.reader :as reader]
            [taoensso.sente :as sente]
            [ajax.core :refer [POST]]
            [bidi.bidi :as bidi]
            [cljs.reader :as edn]
            [{{name}}.config :as config]))

(def sente-client (atom {}))

(def routes (atom nil))

(defn start-sente! []
  (reset! sente-client (sente/make-channel-socket! "/chsk" {:packer :edn :type :auto})))

(defn init! []
  (do (.addEventListener js/window "popstate" #(re-frame/dispatch [:popstate]))
      (start-sente!)
      (re-frame/dispatch [:get-routes])))

(defn- modify-state
  [state modifiers]
  (reduce
    (fn [new-state [ks val]]
      (assoc-in new-state ks val))
    state
    (partition 2 modifiers)))

(defn navigate
  "Sends a request to the server for the inital state of a page"
  [page-id data pop-state? state-modifiers]
  (let [{:keys [state]} @sente-client]
    (POST (bidi/path-for @routes page-id)
          {:params  {:data (pr-str data)}
           :headers {:X-CSRF-Token (:csrf-token @state)
                     :X-State-Only true}
           :format  :raw
           :handler
                    (fn [resp]
                      (let [state (edn/read-string resp)
                            updated-state (update state 1 #(modify-state % state-modifiers))]
                        (re-frame/dispatch [:update-page updated-state pop-state?])))})))

(defn update-browser!
  "Update Browser after navigation"
  [state pop-state?]
  (set! (.-title js/document) (or (get-in state [1 :meta :title])
                                  "Default Title"))
  (when (not pop-state?)
    (.pushState js/history nil "" (bidi/path-for @routes (first state)))))

(re-frame/register-handler
  :get-routes
  (fn [db _]
    (let [{:keys [send-fn]} @sente-client]
      (send-fn [:{{name}}.core/routes {}]
               5000
               (fn [reply]
                 (reset! routes (reader/read-string reply)))))
    db))


(re-frame/register-handler
  :update-page
  [config/standard-middlewares]
  (fn [_ [state pop-state?]]
    (update-browser! state pop-state?)
    state))

(re-frame/register-handler
  :change-page
  [config/standard-middlewares]
  (fn [db [page-key data & state-modifiers]]
    (navigate page-key data false state-modifiers)
    db))

(re-frame/register-handler
  :popstate
  [config/standard-middlewares]
  (fn [db _]
    (navigate (:handler (bidi/match-route @routes (.-pathname js/location))) {} true nil)
    db))
