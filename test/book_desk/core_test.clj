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
          ; TODO: floor tx here
          ; TODO: booking tx here 
          users (db/users (d/db conn))]
     (is (< 0 (count users))))))

; CREATED BY AN ADMIN USER
[10 :floor/number 3]
[10 :floor/size 100]

; CREATED BY USER
[30 :booking/floor 10] ;; reference many-to-one
[30 :booking/user 20]
