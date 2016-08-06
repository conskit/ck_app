(ns {{name}}.dev
  (:require [{{name}}.core :as core]
            [figwheel.client :as figwheel]))

(enable-console-print!)

(figwheel/start {:websocket-url   "ws://localhost:3449/figwheel-ws"
                 :build-id        "dev"
                 :debug           true
                 :jsload-callback  core/mount-root})
