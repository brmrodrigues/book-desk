(ns book-desk.core-test
  (:require [book-desk.core :as core]
            [clojure.test :refer [deftest is testing]]
            [datomic.client.api :as d]
            [matcher-combinators.test :refer [match?]])

  (:import java.util.UUID))

(def client (d/client {:server-type :dev-local
                       :system "dev"}))

(d/create-database client {:db-name "book-desk"})

(def conn (d/connect client {:db-name "book-desk"}))

(def schema [;User
             {:db/ident :user/id
              :db/valueType :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique :db.unique/identity
              :db/doc "User ID for this app"}
             {:db/ident :user/first-name
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "User first name"}
             {:db/ident :user/last-name
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "User last name"}
             {:db/ident :user/admin?
              :db/valueType :db.type/boolean
              :db/cardinality :db.cardinality/one
              :db/doc "Is user an admin of the app?"}

             ;Floor
             {:db/ident :floor/number
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one
              :db/unique :db.unique/identity
              :db/doc "Floor number. E.g. 3rd floor"}
             {:db/ident :floor/size
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc "Size used to display desks for UI"}

             ;Booking
             {:db/ident :booking/user
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one}
             {:db/ident :booking/floor
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one}])

(d/transact conn {:tx-data schema})

(deftest a-test
  (testing "if user is created successfully"
    (let [new-users [{:user/id (UUID/randomUUID)
                      :user/first-name "Bruno"
                      :user/last-name "Rodrigues"
                      :user/admin? true}
                     {:user/id (UUID/randomUUID)
                      :user/first-name "Renata"
                      :user/last-name "Reis"
                      :user/admin? false}]]
      (def tx-resp (d/transact conn {:tx-data new-users})))
    (is (< 0 (count (:tx-data tx-resp))))))

; CREATED BY AN ADMIN USER
[10 :floor/number 3]
[10 :floor/size 100]

; CREATED BY SINGLE SIGN ON
[20 :user/id (UUID/randomUUID)]
[20 :user/first-name "Bruno"]
[20 :user/last-name "Rodrigues"]

; CREATED BY USER
[30 :booking/floor 10] ;; reference many-to-one
[30 :booking/user 20]
