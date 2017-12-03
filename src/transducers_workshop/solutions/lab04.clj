(ns transducers-workshop.solutions.lab04
  (:require
    [clojure.core.reducers :as r]
    [transducers-workshop.solutions.lab01 :as lab01]
    [transducers-workshop.xf :as xf]
    [clojure.core.async :as async]))

(defn load-data
  "load the usual test data but repeat them to create a much
  larger dataset"
  [n]
  (into [] (apply concat (repeat n (lab01/load-data)))))

; (require '[transducers-workshop.solutions.lab04 :as lab04] :reload)
; (require '[transducers-workshop.solutions.lab01 :as lab01] :reload)

(defn parallel-reducers [params feed]
  (r/fold
    (r/monoid into (constantly []))
    ((lab01/xform params) conj)
    feed))

(defn call-parallel-reducers []
  (parallel-reducers
    {:repayment-method :payment-method-repayment
     :loan-amount 1500000}
    (load-data 1000)))

; (time (def cs (call-parallel-reducers)))

(def max-parallel
  (inc (.availableProcessors (Runtime/getRuntime))))

(defn parallel-async [params feed]
  (let [out (async/chan (async/buffer 100))]
    (async/pipeline
      max-parallel
      out
      (lab01/xform params)
      (async/to-chan feed))
    (->> out (async/reduce conj []) async/<!!)))

(defn call-parallel-async []
  (parallel-async
    {:repayment-method :payment-method-repayment
     :loan-amount 1500000}
    (load-data 1000)))

; (time (def cs (call-parallel-async)))

(defn parallel-async-multiple [params feed]
  (let [io (async/chan (async/buffer 100))
        out (async/chan (async/buffer 50))
        prepare-pipeline (async/pipeline
                           max-parallel
                           io
                           lab01/prepare-data
                           (async/to-chan feed))
        filter-pipeline (async/pipeline
                          (quot max-parallel 2)
                          out
                          (lab01/filter-data params)
                          io)]
    (->> out (async/reduce conj []) async/<!!)))

(defn call-parallel-async-multiple []
  (parallel-async-multiple
    {:repayment-method :payment-method-repayment
     :loan-amount 1500000}
    (load-data 1000)))

; (time (def cs (call-parallel-async-multiple)))
