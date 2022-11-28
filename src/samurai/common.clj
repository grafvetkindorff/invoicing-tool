(ns samurai.common
  (:require [clojure.java.io :as io]))


(defn load-properties
  []
  (let [prop (Properties.)
        propFileName (io/resource "config.properties")
        inputStream (io/input-stream propFileName)
        _ (doto prop
            (.load inputStream))]
    prop))