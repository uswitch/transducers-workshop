(ns transducers-workshop.solutions.lab03
  (:require
    [clojure.edn :as edn]
    [transducers-workshop.solutions.lab01 :refer [prepare-data]]
    [clojure.core.async :refer [buffer go go-loop chan >! <! <!! close!]]))

(defn load-data []
  (edn/read-string (slurp "feed.edn")))

(def cache (atom []))

(defn to-stream
  "Takes a collection and simulate a stream by
  adding items one at a time into a channel.
  The go-loop builds the blocking queue
  waiting for a consumer downstream to pick the items up."
  [items in out]
  (go-loop []
    (when-some [item (<! in)]
      (>! out item)
      (recur)))
  (go
    (doseq [item items]
      (>! in item))))

(defn consume [out]
  (loop []
    (when-some [item (<!! out)]
      (println "adding to cache")
      (swap! cache conj item)
      (recur))))

(def products (load-data))
(def in (chan 1))
(def out (chan 1 prepare-data))
(future (consume out))

(to-stream (take 10 products) in out)

(close! in)
(close! out)
