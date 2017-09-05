(ns transducers-workshop.solutions.lab01
  (:require [clojure.edn :as edn]
            [criterium.core :refer [quick-bench]]
            [transducers-workshop.xf :as xf])
  (:import java.util.Date))

; data shape
; [{:fee-attributes []
;   :product {}
;   :created-at 111111}
;  {:fee-attributes []
;   :product {}
;   :created-at 111111}]

(defn load-data
  "Load example feed from disk."
  []
  (edn/read-string (slurp "feed.edn")))

(def prepare-data
  (comp
    (xf/merge-into :product [:fee-attribute :created-at])
    (xf/update-at :created-at #(Date. %))))

(defn filter-data [params]
  (comp
    (xf/allow-if :visible)
    (xf/allow-if :online)
    (xf/allow-if-equal :company-id (params :company-id))
    (xf/allow-if (params :repayment-method))
    (xf/allow-in-range :min-loan-amount :max-loan-amount (params :loan-amount))
    ))

(defn xform [params]
  (comp
    prepare-data
    (filter-data params)
    ))

(defn products [params feed]
  (eduction (xform params) feed))

; (require '[transducers-workshop.solutions.lab01 :as lab01] :reload)

; (def xs
;   (lab01/products {; :company-id 46
;                    :repayment-method :payment-method-part-repayment
;                    :loan-amount 1500000}
;                   (lab01/load-data)))
; (count xs)
; 69

(defn create-search
  [params]
  (sequence (xform params) (load-data)))

(def company1 (create-search {:company-id 46}))
(def company2 (create-search {:company-id 50}))

; (let [data (lab01/load-data)] (quick-bench (sequence (lab01/xform {:company-id 46}) data)))
; (let [data (lab01/load-data)] (quick-bench (eduction (lab01/xform {:company-id 46}) data)))
