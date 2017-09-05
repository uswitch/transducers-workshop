(ns transducers-workshop.core)

;; This namespace prepares raw data for the labs. Please ignore.

(def buzz ["BTL" "Prime" "TSP:21" "Variable" "Type" "A" "Purchase" "2" "Year" "Discount" "Fee" "5" "Fixed" "Semi" "Exclusive" "Older" "Borrower" "3" "Tracker" "Direct" "No" "BiR" "Cashback" "Existing" "Borrowers" "10" "Further" "Advance" "Switcher" "FTB" "C/A" "Green" "Offer" "Intermediary" "Incentives" "HTB" "MG" "Scheme" "Intrinsic" "Tier" "1" "1.5%" "Ltd" "Co" "Flexx" "7" "IO" "(Regional" "Lending" "Area" "only)" "RTB" "Near" "Credit" "Assist" "Shared" "Equity" "EMC" "Reward" "Offset" "Privileged" "Ptns" "/" "Valuation" "Ownership" "Home" "Starter" "PTFS" "Self" "Employed" "Free" "Legals" "Professional" "Term" "Remotgage" "Wales" "0.75%" "CBTL" "Simply" "New" "Build" "(DMS)" "1.00%" "Scotland" "1%" "Transfer" "Chorley" "Local" "First" "Time" "Buyer" "self-build" "CBS" "customers" "A121" "A120" "A119" "AA123" "AA122" "AA121" "AA120" "AA119" "AAA123" "AAA122" "AAA121" "AAA120" "AAA119" "Clear123" "Clear122" "Clear121" "Clear120" "Clear119" "BBB120" "BBB119" "A126" "A125" "A124" "AA128" "AA127" "AA126" "AA125" "AA124" "AAA128" "AAA127" "AAA126" "AAA125" "AAA124" "Clear128" "Clear127" "Clear126" "Clear125" "Clear124" "BBB125" "BBB124" "A131" "A130" "A129" "AA134" "AA132" "AA131" "AA130" "AA129" "AAA134" "AAA132" "AAA131" "AAA130" "AAA129" "Clear134" "Clear132" "Clear131" "Clear130" "Clear129" "BBB130" "BBB129" "Portfolio" "4" "L" "&" "G" "0.5%" "Save" "To" "Buy" "Over" "Monthly" "Interest" "Family" "BS" "PMS" "Part/Part" "Product" "Premier" "Large" "Loan" "Connells" "For"])

(defn- buzz-gen [x]
  (let [n (:name x)]
    (assoc x :name (apply str (interpose " " (take 10 (shuffle buzz)))))))

(defn- fee-gen []
  (into [] (take 10 (repeatedly #(int (* 100 (rand)))))))

(def kk [:legal-fee-added :apply-url :max-ltv-variance-by-loan-amount :payment-method-part-repayment :max-ltv :name :apr :max-age :company-name :initial-period :tracker-rate :initial-rate :initial-term-label :company-id :min-age :online :product-type :fixed-rate :revert-rate :self-employed-accepted :id :offset-rate :low-deposit :offset-facility :initial-rate-variance-by-ltv :payment-method-interest-only :max-loan-amount :payment-method-repayment :visible :min-loan-amount :existing-borrower :variable-rate :bankruptcy])

#_(let [raw (read-string (slurp "/Users/reborg/prj/uswitch/transducers-workshop/raw.edn"))]
    (spit
      "feed.edn"
      (with-out-str
        (clojure.pprint/write
          (into [] (comp
                     (map :mortgage)
                     (map #(select-keys % kk))
                     (map buzz-gen)
                     (map #(hash-map
                             :product %
                             :fee-attributes (fee-gen)
                             :created-at (System/currentTimeMillis))))
                raw)))))

(first (sequence (comp (mapcat range) (mapcat range)) [3000 6000 9000]))
(first (mapcat range (mapcat range [3000 6000 9000])))
(->> [[0 1 2] [3 4 5] [6 7 8]] (apply map vector))
