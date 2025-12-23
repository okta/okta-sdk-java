///*
// * Copyright 2025-Present Okta, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.okta.sdk.tests.it
//
//import com.okta.sdk.resource.api.PolicyApi
//import com.okta.sdk.tests.it.util.ITSupport
//import org.testng.annotations.Test
//
///**
// * ONE-TIME CLEANUP TEST
// *
// * This test deletes all test policies matching the pattern "policy+{uuid}".
// *
// * IMPORTANT:
// * - Run this test ONCE to clean up test policies
// * - After running, DELETE THIS FILE from the codebase
// * - This should NOT be committed to the repository
// *
// * Usage:
// * mvn test -pl integration-tests -Dtest=PolicyCleanupIT
// */
//class PolicyCleanupIT extends ITSupport {
//
//    @Test
//    void testOneTimeCleanupTestPolicies() {
//        PolicyApi policyApi = new PolicyApi(getClient())
//
//        println "Starting policy cleanup..."
//
//        int deletedCount = 0
//        int failedCount = 0
//        int skippedCount = 0
//
//        // List of policy IDs to delete (2nd batch: policies 100-199 from the Okta org)
//        // EXCLUDING the Default Policy
//        def policyIds = [
//            "00prm0hgrlxk7xG8T1d7", "00prm0i7f9aT3c6hC1d7", "00prm08ej2ROPS2yM1d7", "00prm0b4v6Mcb2Spo1d7",
//            "00prlzl6w4VwUFJKL1d7", "00prlzl6w5J8mliIN1d7", "00prlznrqxsPJcN4F1d7", "00prlzpkf1RqSPJGE1d7",
//            "00prlznrl3QKrnXAC1d7", "00prlzj8mho04iVfB1d7", "00prlxtptxvHsRLDR1d7", "00prlxokpp1k1hJKX1d7",
//            "00prlxtghrIT0iG1T1d7", "00prlxrf1dkp4oDyh1d7", "00prlxe4yoFUYXLT91d7", "00prlxihxylMaArgb1d7",
//            "00prlxrgi11eina8a1d7", "00prlxjl75SJfURl81d7", "00prlxlbno8htKAEq1d7", "00prlwjpq3gbWh8V31d7",
//            "00prlwm2krKgToD9j1d7", "00prlwotwzJKxEREO1d7", "00prlwi5pdsH3nv3M1d7", "00prlwoamuO2Nltp21d7",
//            "00prlwmuwkGFwAQXr1d7", "00prlwlldmKvNy6Ye1d7", "00prlwkp6qCGgnOKa1d7", "00prlto4dfy98nAZJ1d7",
//            "00prltqgurH8anNNo1d7", "00prltnxmxeBrHxM81d7", "00prltq1miRup1R8X1d7", "00prltoechxN73pCu1d7",
//            "00prltt7qidSHRBCh1d7", "00prltoszbXHV6OQe1d7", "00prltosyi61Ricul1d7", "00prltlzgld6iYRn81d7",
//            "00prltqgjw2ipu7ye1d7", "00prltnl0bIDRP93o1d7", "00prjildzyzBI1gYZ1d7", "00prltm5kvv8IqwW91d7",
//            "00prjfm946Lt6Tiy91d7", "00prjfl38jf0kcNwk1d7", "00prjfaj66qXZm8kq1d7", "00prjfgk12sKjNHTP1d7",
//            "00prjfdwkhmTjC3zM1d7", "00prjf7gukNbv6IT21d7", "00prjfg5ikxwaN1CO1d7", "00prjfakhdQfeSPRS1d7",
//            "00prjfbvvfrp7271o1d7", "00prjfboyrSctTYhp1d7", "00prjfcd8dIOygaxZ1d7", "00prjf160pwj6lsSo1d7",
//            "00prh35oy8BSCCmzy1d7", "00prjf97ktoYOBdGk1d7", "00prflsauv3j1XjGS1d7", "00prjf1ixpTiNQWGt1d7",
//            "00prflser8ThIUDo11d7", "00prflbgftHfi4iSO1d7", "00prfl9i0qWwWZLd41d7", "00prfllh6r8146AJ11d7",
//            "00prflao4lP1mG3Bx1d7", "00prfl9h4xl55Pm2a1d7", "00prfla653K8bWCik1d7", "00prfl3dbeg9soS2d1d7",
//            "00prfl6fyu5oQCzkG1d7", "00prfl9bymy2xNVyI1d7", "00prfl6h8sRTtXjij1d7", "00prfl0vc5NejK0hb1d7",
//            "00prfl20c5t1Pb65N1d7", "00prfjn8og4Soyrjd1d7", "00prfjykzydODvldb1d7", "00prfj9a9g1ShFnCh1d7",
//            "00prf509fd84Ew7Jt1d7", "00prf4p916f3gs75P1d7", "00prf4lqoxggo2Sqq1d7", "00prf4bvijdOGnMAu1d7",
//            "00prf346j9arxuFXH1d7", "00prf0zyqmashRsT11d7", "00prf254tx5xRSX1R1d7", "00prf1vampxQtFJYI1d7",
//            "00prexrod0KLehNUf1d7", "00prezujosVTEEEdE1d7", "00prexy3vsR8TtVn41d7", "00prexw9ebPU5mWQ41d7",
//            "00prexpfgqQ194RIC1d7", "00prexpqswOJl1SIE1d7", "00prexh8dyVXWIGKF1d7", "00prexnrzht9ln1qp1d7",
//            "00prex0z058BTvIXU1d7", "00prexjsqklIC4gWQ1d7", "00prewnrclDcD6i0h1d7", "00prex4qp4LNTg6u21d7",
//            "00prewumgwgrTp0Od1d7", "00prex0yvrsuqWTO01d7", "00prevpoqeSlBZtQi1d7", "00prewd879frl9gZJ1d7",
//            "00prevs5maoGHoJRj1d7", "00preveh9xmAn4AC91d7", "00prevqngtYfNm9071d7", "00prevpu3dFsm4Z2p1d7"
//        ]
//
//        policyIds.each { policyId ->
//            try {
//                println "Deleting policy: ${policyId}"
//
//                // Delete the policy directly
//                policyApi.deletePolicy(policyId)
//                deletedCount++
//
//                // Add a small delay to avoid rate limiting
//                Thread.sleep(100)
//
//            } catch (com.okta.sdk.resource.client.ApiException e) {
//                if (e.code == 404) {
//                    println "Policy not found (already deleted?): ${policyId}"
//                    skippedCount++
//                } else {
//                    println "Failed to delete policy ${policyId}: ${e.message}"
//                    failedCount++
//                }
//            } catch (Exception e) {
//                println "Failed to delete policy ${policyId}: ${e.message}"
//                failedCount++
//            }
//        }
//
//        println ""
//        println "======================================"
//        println "Policy Cleanup Summary:"
//        println "======================================"
//        println "Total policies in list: ${policyIds.size()}"
//        println "Deleted: ${deletedCount}"
//        println "Failed:  ${failedCount}"
//        println "Skipped: ${skippedCount}"
//        println "======================================"
//        println ""
//        println "⚠️  REMEMBER TO DELETE THIS TEST FILE AFTER RUNNING! ⚠️"
//    }
//}
