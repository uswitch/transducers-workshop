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
  (lab04/parallel-reducers
    {:repayment-method :payment-method-part-repayment
     :loan-amount 1500000}
    (lab04/load-data 1000)))

; (time (def cs (lab04/call-parallel-reducers)))

(defn parallel-async [params feed]
  (let [out (async/chan (async/buffer 20))]
    (pipeline
      (inc (.availableProcessors (Runtime/getRuntime)))
      out
      (lab01/xform params)
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
