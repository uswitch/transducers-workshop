(ns transducers-workshop.lab04
  (:require
    [clojure.core.reducers :as r]
    [transducers-workshop.solutions.lab01 :as lab01]
    [transducers-workshop.xf :as xf]
    [clojure.core.async :as async]))

;; LAB 04: Paralellizing Transducers. There are essentially two approaches: one is using Reducers (fork-join framework) and apply transducers when the smallest chunk is reached. The other is using core.async pipeline, another form of multi-threading where each item is sent down one of many parallel channels available and gets transformed.

(defn load-data
  "load the usual test data but repeat them to create a much
  larger dataset"
  [n]
  (into [] (apply concat (repeat n (lab01/load-data)))))

;; STEP 1: parallelize with reducers. The entry pointis the r/fold function. It requires a combine function and reducing function. A transducer chain is just a function of one argument waiting for a reducing function (such as + or conj). If we call the transducer chain with that function, we obtain our reducing function that we can use with reducers. Write here the missing part of the call to r/fold so that our feed of products can be transduced in parallel:

(defn parallel-reducers [params feed]
  (r/fold
    (r/monoid
      ;; combine two vector chunks into one.
      ;; give the initial empty vector to start with
      )
    ((lab01/xform params)
      ; what should we call our transducing chain with to make into into a reducer?
      ; our reducing function should take a vector as input and return a vector as output.
     )
    feed))

(defn call-parallel-reducers []
  (lab04/parallel-reducers
    {:repayment-method :payment-method-part-repayment
     :loan-amount 1500000}
    (lab04/load-data 1000)))

; (time (def cs (lab04/call-parallel-reducers)))

;; STEP 2: complete the following core.async pipeline to process the feed in parallel.

(defn parallel-async [params feed]
  (let [out (async/chan (async/buffer 20))]
    (pipeline
      ; put here the number of available processors on your machine.
      out
      ; put here our transducing chain
      (async/to-chan feed))
    (->> out
         (async/reduce conj [])
         async/<!!)))

(defn call-parallel-async []
  (lab04/parallel-async
    {:repayment-method :payment-method-part-repayment
     :loan-amount 1500000}
    (lab04/load-data 1000)))

; (time (def cs (lab04/call-parallel-async)))

;; STEP 3: uncomment now the time benchmarks to test how fast they are. Which one is the fastest? Bonus: would you be able to rewrite the transducer chain as standard sequential processing. How long does it take to process the same list of products?
