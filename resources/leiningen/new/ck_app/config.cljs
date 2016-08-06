(ns {{name}}.config
  (:require [re-frame.core :as re-frame]))

(goog-define DEBUG! true)

(def debug?
  ^boolean DEBUG!)

(when debug?
  (enable-console-print!))

(def standard-middlewares
  [(when debug? re-frame/debug) re-frame/trim-v])
