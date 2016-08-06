(ns {{name}}.main
  (:gen-class)
  (:require [clojure.tools.logging :as log]))


(defn -main
  "The entry point of the application"
  [& args]
  (log/info "Starting Application")
  (require 'puppetlabs.trapperkeeper.core)
  (apply (resolve 'puppetlabs.trapperkeeper.core/main) args))
