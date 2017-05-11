(ns user
  (:require [clojure.pprint :as pprint]
            [clojure.tools.namespace.repl :refer [refresh]]
            [puppetlabs.trapperkeeper.app :as tka]
            [puppetlabs.trapperkeeper.bootstrap :as bootstrap]
            [puppetlabs.trapperkeeper.config :as config]
            [puppetlabs.trapperkeeper.core :as tk]
            [conskit.protocols :as ckp]
            [figwheel-sidecar.repl-api :as f])
  (:import (java.util.regex Pattern)))

(def system nil)

(defn init []
  (alter-var-root #'system
                  (fn [_] (tk/build-app
                            (bootstrap/parse-bootstrap-config! "./dev-resources/bootstrap.cfg")
                            (config/load-config "./dev-resources/config.conf"))))
  (alter-var-root #'system tka/init)
  (tka/check-for-errors! system))

(defn start []
  (f/start-figwheel!)
  (alter-var-root #'system
                  (fn [s] (if s (tka/start s))))
  (tka/check-for-errors! system))

(defn stop []
  (f/stop-figwheel!)
  (alter-var-root #'system
                  (fn [s] (when s (tka/stop s)))))

(defn go []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))

 (defn cljs-repl []
   (f/cljs-repl))

(defn route-info
  [route-str]
  (if system
    (let [routes (get-in @(tka/app-context system) [:service-contexts :CKRouter :routes])
          registry (tka/get-service system :ActionRegistry)
          pred #(let [route (:route %)]
                  (if (= (type route) Pattern)
                    (re-matches route route-str)
                    (= route route-str)))
          action-id (:id (first (filter pred routes)))]
      (ckp/select-meta-keys registry action-id [:route :id :file :line :ns]))
    (throw (Exception. "Instance could not be found. Please run (reset) first"))))
