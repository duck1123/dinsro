(ns dinsro.model.ln.htlcs)

(def example-hop
  {:fee              0,
   :expiry           544,
   :mppRecord
   {:paymentAddr  "71DLFIzPbGZ7qYnz2SwVriiTm/MSB9n1nT4BRvbTtOc=",
    :totalAmtMsat 5000000},
   :feeMsat          0,
   :ampRecord        {:rootShare "", :setId "", :childIndex 0},
   :amtToForward     5000,
   :customRecords    [],
   :pubKey
   "020e78000d4d907877ab352cd53c0dd382071c224b500c1fa05fb6f7902f5fa544",
   :amtToForwardMsat 5000000,
   :chanId           332052511653888,
   :tlvPayload       true,
   :chanCapacity     16777215})

(def example-htlc
  {:attemptId     1,
   :status        "SUCCEEDED",
   :route
   {:totalTimeLock 544,
    :totalFees     0,
    :totalAmt      5000,
    :hops
    [example-hop],
    :totalFeesMsat 0,
    :totalAmtMsat  5000000},
   :attemptTimeNs 1640654951318021027,
   :resolveTimeNs 1640654951419751237,
   :failure
   {:code               "RESERVED",
    :channelUpdate
    {:htlcMinimumMsat 0,
     :htlcMaximumMsat 0,
     :signature       "",
     :chainHash       "",
     :chanId          0,
     :baseFee         0,
     :timestamp       0,
     :channelFlags    0,
     :messageFlags    0,
     :extraOpaqueData "",
     :feeRate         0,
     :timeLockDelta   0},
    :htlcMsat           0,
    :onionSha256        "",
    :cltvExpiry         0,
    :flags              0,
    :failureSourceIndex 0,
    :height             0},
   :preimage      "PEoEBH295oPkLimyOGtwjMzb1aE6Tt1ehSICzB5L2yE="})
