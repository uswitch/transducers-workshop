(ns transducers-workshop.solutions.lab03
  (:require
    [clojure.edn :as edn]
    [transducers-workshop.solutions.lab01 :refer [prepare-data]]
    [clojure.core.async :refer [buffer go go-loop chan >! <! <!! close!]]))

; Welcome to lab3: attaching a transducer chain to a core.async process. In this lab we simulate a stream of incoming financial products. The products are nested maps of values, lists or other data. We developed a transducer to prepare this data for searching. Our idea is that the preparation step can be attached directly to the stream of incoming (raw) products so when we need to search them they are ready. We are going to save already prepared products in a local cache in memory. The follwing are functions and variables to use in the next step:

(defn load-data []
  (edn/read-string (slurp "feed.edn")))

(def cache (atom []))

(defn to-stream
  "Takes a collection and simulate a stream by
  adding items one at a time into a channel.
  The go-loop builds the blocking queue
  waiting for a consumer downstream to pick the items up."
  [items in]
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

; Step1: create an input and output channel. Invoke consume with the output channel. "consume" will be blocking waiting for input! Wrap the call in a "future" to start consuming from a separate thread.

(def products (load-data))

(def in
  ;... complete here
  )
(def out
  ;... complete here
  )

; start the consumer here.

; STEP 2: stream some products into the input channel. The consumer will start consuming from inside its thread and you should see products added to the cache.

(to-stream
  ; complete here
  )

; STEP 3: check that something ended up in the cache. How does it look like? Is that ready for filtering or it requires "preparation"?

; check inside the cache here. Grab the first product and print it on screen. To access an atom, you will need to defer it with "@" before.
; stops the channels using the (stop! function)
; reset the cache so you can see new results later. You can just redefine the var.

; STEP 4: repeat the expreience by attaching the "prepare-data" transducer chain to the input or output channel. What is the difference?

; create the input and output channel. This time be sure to attach the transducer chain in one of them.

(def in
  ;... complete here
  )
(def out
  ;... complete here
  )

; start the consumer in a future:
; ............

; stream a few products back in:
(to-stream
  ; complete here
  )

; check the content of the cache now. Are the product different?
; remember to close the channels.
