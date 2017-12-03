(ns transducers-workshop.lab03
  (:require
    [clojure.edn :as edn]
    [transducers-workshop.solutions.lab01 :refer [prepare-data]]
    [clojure.core.async :refer [go go-loop chan >! <! <!! close!]]))

; Welcome to lab3: attaching a transducer chain to a core.async process.
; In this lab we simulate a stream of incoming financial products.
; The products are nested maps of values, lists or other data.
; In lab 1 we developed a transducer to prepare this data for searching.
; The preparation step can be attached directly to the stream of
; incoming (raw) products so when we need to search for them they are ready.
; We are going to save already prepared products in a local cache in memory.
; The follwing are functions and variables helpers to use in the next steps:

(defn load-data [] (edn/read-string (slurp "feed.edn")))
(def cache (atom []))
(def products (load-data))

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

(defn consume
  "Consume products out of a channel and put them
  in the internal 'atom' cache"
  [out]
  (loop []
    (when-some [item (<!! out)]
      (println "adding to cache")
      (swap! cache conj item)
      (recur))))

; Step1: create an input and output channel with buffer size 1.
; See the labs slides for the correct syntax.

(def in
  ;... complete here
  )
(def out
  ;... complete here
  )

; This is how to start the consumer. Note that "consume" is a blocking
; call, waiting for products to flow through. We detach for the blocking
; thread putting it in a separate thread with "future".
(future (consume out))

; STEP 2: stream some products into the input channel.
; The consumer will start consuming from the "future" thread.

(to-stream
  ; complete here
  )

; STEP 3: check that something ended up in the cache.
; How does it look like? Is that ready for filtering or it requires "preparation"?
; Grab the first product and print it on screen. To access an atom, you will need
; to defer it with "@" before. To stop the channels use (stop!) on the channel.
; Reset the cache so you can see new results later. To do this, you can just redefine the var.

(println (first "Check the cache here"))

; STEP 4: repeat the experience by attaching the "prepare-data"
; transducer chain to the input or output channel.
; Create the input and output channel. This time be sure to attach the transducer chain in one of them.
; What is the difference?

(def in
  ;... complete here
  )
(def out
  ;... complete here
  )

; start the consumer in a future as we did before.

; stream a few products in:
(to-stream
  ; complete here
  )

; check the content of the cache again Are the product different?
; remember to close the channels.
