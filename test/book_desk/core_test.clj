(ns book-desk.core-test
  (:require [book-desk.core :as core]
            [book-desk.fixture :as fixture]
            [book-desk.db :as db]
            [clojure.test :refer [deftest is testing]]
            [datomic.client.api :as d]
            [matcher-combinators.test :refer [match?]]))

(def client (d/client {:server-type :dev-local
                       :system "dev"}))

(d/create-database client {:db-name "book-desk"})
(def conn (d/connect client {:db-name "book-desk"}))
(d/transact conn {:tx-data db/schema})
#_(d/delete-database client {:db-name "book-desk"})

(deftest new-bookings-in-db
  (testing "if user is created successfully"
    (let [_ (d/transact conn {:tx-data (fixture/new-users)})
          _ (d/transact conn {:tx-data fixture/floors})
          users (db/users (d/db conn))
          floors (db/floors (d/db conn))
          _ (->> (db/booking-tx (ffirst users) (ffirst floors))
                 (assoc {} :tx-data)
                 (d/transact conn))
          bookings (db/bookings (d/db conn))]
     (is (< 0 (count bookings))))))

