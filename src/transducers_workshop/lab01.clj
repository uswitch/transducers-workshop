(ns transducers-workshop.lab01
  (:require [clojure.edn :as edn]
            [transducers-workshop.xf :as xf])
  (:import java.util.Date))

(defn load-data
  "Load example feed from disk."
  []
  (edn/read-string (slurp "feed.edn")))

; Data in the feed are stored as group of keys:

; INPUT shape
; [{:fee-attributes []
;   :product {:key1 "val1" :key2 "val2"}
;   :created-at 111111}
;  {:fee-attributes []
;   :product {}
;   :created-at 111111}]

; Take a look at the actual feed in ./feed.edn.
; We want to merge everything into the :product key

; OUTPUT shape:
; [{:key1 "val1"
;   :key2 "val2"
;   :fee-attributes [1 2 3]
;   :created-at java.util.Date}
;  {:key1 "val1"
;   :key2 "val2"
;   :fee-attributes [1 2 3]
;   :created-at java.util.Date}]

; Task 1: prepare the data.
; Write a combination of transducers to perform the following:
; * Merge :fee-attributes and :create-at into the :product map.
; * Transform :created-at date from long into java.util.Date
; Place the transducers into the "xform" function.

(defn xform [params]
  ;; ... compose transducers here
  )

; Task 2: now add filtering to perform the following:
; * Only show a product if it is :visible and :online (they should be true)
; * If the search params contain a :company-id, then filter for that company ID
; * If the search params contain a :repayment-method type, then filter pruducts with that repayment type set to 'true'
; * If the search params contain a :loan-amount, only show products :min-loan-amount <= loan-amout <= :max-loan-amount
; Can you guess why using eduction here? Why not sequence or transduce?

(defn products [params feed]
  (eduction (xform params) feed))

;; How to use at the REPL to veryfy if everything is working:
; (require '[transducers-workshop.lab01 :as lab01] :reload)

; (def xs
;   (lab01/products {; :company-id 46
;                    :repayment-method :payment-method-part-repayment
;                    :loan-amount 1500000}
;                   (lab01/load-data)))
; (count xs)
; 69

; Task 3: store searches for company-id 46 and company-id 50.
; sequence attaches the transducers chain to a specific collection (our feed)
; and produces results on demand. The search can be stored (as far as the feed
; doesn't change) and re-used in multiple places. The transducer chain won't execute
; again and evaluated results cached.

(defn create-search [params]
  ;; ... create sequence here.
  )

(def company1 (create-search {:company-id 46}))
(def company2 (create-search {:company-id 50}))

; What is sequence allowing in this scenario? Here's an example of what you should see:
; (map :name lab01/company1)
; ("Green Professional Credit Intermediary 1.5% Buyer AA123 3 0.75% Legals"
;  "AAA132 IO A130 BBB124 AAA125 Older Self / only) Year"
;  "(DMS) Clear128 Fixed Intrinsic Part/Part Switcher AA130 BiR Tracker Reward")
; (map :name lab01/company2)
; ("Loan Monthly AAA124 C/A Product A126 1% Tier 10 Starter")

; What happens if company-id 50 launches a new product in the feed and I run
; (map :name lab01/company2)
; again?
