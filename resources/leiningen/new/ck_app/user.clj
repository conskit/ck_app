 (ns user
   (:require [clojure.pprint :as pprint]
             [clojure.tools.namespace.repl :refer [refresh]]
             [puppetlabs.trapperkeeper.app :as tka]
             [puppetlabs.trapperkeeper.bootstrap :as bootstrap]
             [puppetlabs.trapperkeeper.config :as config]
             [puppetlabs.trapperkeeper.core :as tk]
             [figwheel-sidecar.repl-api :as f]))

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
